package studiokita.model.dao;

import studiokita.model.*;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * TransaksiDAO — Data Access Object untuk tabel `transaksi`
 *
 * Query utama menggunakan JOIN ke tabel users + layanan_sewa/layanan_jasa untuk
 * menghasilkan objek Transaksi yang lengkap.
 */
public class TransaksiDAO {

    // ── SQL Base ──────────────────────────────────────────────
    private static final String BASE_SQL
            = "SELECT t.*, "
            + "  u.nama_lengkap, u.email, u.no_telepon, u.alamat, "
            + "  s.jenis_alat, s.nama_kamera, s.tarif_per_hari, "
            + "    s.tgl_mulai, s.tgl_kembali, s.tgl_dikembalikan, "
            + "  j.fotografer, j.paket, j.tgl_sesi, "
            + "    j.durasi_jam, j.jumlah_foto_edit, j.harga_dasar "
            + "FROM transaksi t "
            + "JOIN users u ON t.customer_username = u.username "
            + "LEFT JOIN layanan_sewa s ON t.id_layanan = s.id_layanan AND t.jenis_layanan = 'SEWA' "
            + "LEFT JOIN layanan_jasa j ON t.id_layanan = j.id_layanan AND t.jenis_layanan = 'JASA' ";

    // ── READ ──────────────────────────────────────────────────
    /**
     * Ambil semua transaksi (untuk admin — lihat semua).
     */
    public static List<Transaksi> getAll() throws SQLException {
        List<Transaksi> list = new ArrayList<>();
        String sql = BASE_SQL + "ORDER BY t.tgl_input DESC, t.id DESC";
        Connection conn = DatabaseConnection.getConnection();
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    /**
     * Ambil transaksi milik satu customer (untuk customer — lihat milik
     * sendiri).
     */
    public static List<Transaksi> getByCustomer(String username) throws SQLException {
        List<Transaksi> list = new ArrayList<>();
        String sql = BASE_SQL + "WHERE t.customer_username = ? ORDER BY t.tgl_input DESC";
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    /**
     * Ambil transaksi berdasarkan tanggal tertentu.
     */
    public static List<Transaksi> getByDate(LocalDate tanggal) throws SQLException {
        List<Transaksi> list = new ArrayList<>();
        String sql = BASE_SQL + "WHERE t.tgl_input = ? ORDER BY t.id DESC";
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(tanggal));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    /**
     * Total penghasilan seluruh transaksi.
     */
    public static double getTotalPenghasilan() throws SQLException {
        String sql = "SELECT COALESCE(SUM(total_biaya), 0) FROM transaksi";
        Connection conn = DatabaseConnection.getConnection();
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            return rs.next() ? rs.getDouble(1) : 0;
        }
    }

    /**
     * Total penghasilan milik satu customer.
     */
    public static double getTotalByCustomer(String username) throws SQLException {
        String sql = "SELECT COALESCE(SUM(total_biaya),0) FROM transaksi WHERE customer_username=?";
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getDouble(1) : 0;
            }
        }
    }

    /**
     * Jumlah transaksi per jenis layanan.
     */
    public static int countByJenis(String jenis) throws SQLException {
        String sql = "SELECT COUNT(*) FROM transaksi WHERE jenis_layanan=?";
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, jenis);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    // ── CREATE ────────────────────────────────────────────────
    /**
     * Menyimpan transaksi baru menggunakan SQL Transaction agar konsisten:
     * layanan + transaksi disimpan atomik.
     *
     * @param trx objek Transaksi yang sudah terisi lengkap
     * @return true jika berhasil
     */
    public static boolean insert(Transaksi trx) throws SQLException {
        String sqlTrx = "INSERT INTO transaksi "
                + "(id_transaksi,customer_username,id_layanan,jenis_layanan,total_biaya,tgl_input) "
                + "VALUES (?,?,?,?,?,?)";

        Connection conn = DatabaseConnection.getConnection();
        conn.setAutoCommit(false);  // ── mulai SQL Transaction ──
        try {
            // 1. Simpan layanan terlebih dahulu
            if (trx.getLayanan() instanceof SewaAlat sa) {
                SewaAlatDAO.insert(sa);
            } else if (trx.getLayanan() instanceof JasaFoto jf) {
                JasaFotoDAO.insert(jf);
            }

            // 2. Simpan transaksi
            try (PreparedStatement ps = conn.prepareStatement(sqlTrx)) {
                ps.setString(1, trx.getIdTransaksi());
                ps.setString(2, trx.getCustomer().getUsername());
                ps.setString(3, trx.getLayanan().getIdLayanan());
                ps.setString(4, trx.getJenisLayanan());
                ps.setDouble(5, trx.getTotalBiaya());
                ps.setDate(6, Date.valueOf(trx.getTglInput()));
                ps.executeUpdate();
            }

            conn.commit();  // ── commit jika semua sukses ──
            return true;

        } catch (SQLException e) {
            conn.rollback();  // ── rollback jika ada yang gagal ──
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    // Tambahkan method ini di dalam class TransaksiDAO
    public static boolean updateStatus(String idTransaksi, String statusBaru) throws SQLException {
        String sql = "UPDATE transaksi SET status = ? WHERE id_transaksi = ?";
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, statusBaru);
            ps.setString(2, idTransaksi);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        }
    }

    // ── DELETE ────────────────────────────────────────────────
    public static boolean delete(String idTransaksi) throws SQLException {
        String sql = "DELETE FROM transaksi WHERE id_transaksi=?";
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, idTransaksi);
            return ps.executeUpdate() > 0;
        }
    }

    // ── ID GENERATOR ─────────────────────────────────────────
    public static String generateId() throws SQLException {
        String sql = "SELECT COUNT(*) FROM transaksi";
        Connection conn = DatabaseConnection.getConnection();
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            int count = rs.next() ? rs.getInt(1) : 0;
            return String.format("TRX%04d", count + 1);
        }
    }

    // ── MAPPER ───────────────────────────────────────────────
    private static Transaksi mapRow(ResultSet rs) throws SQLException {
        Transaksi t = new Transaksi();
        t.setId(rs.getInt("id"));
        t.setIdTransaksi(rs.getString("id_transaksi"));
        t.setJenisLayanan(rs.getString("jenis_layanan"));
        t.setTotalBiaya(rs.getDouble("total_biaya"));
        t.setTglInput(rs.getDate("tgl_input").toLocalDate());
        
        // =========================================================================
        // FIX BUG UTAMA: Ambil nilai status dari database agar tidak stuck PENDING!
        // =========================================================================
        t.setStatus(rs.getString("status")); 
        // =========================================================================

        // Map Customer
        Customer cust = new Customer();
        cust.setUsername(rs.getString("customer_username"));
        cust.setNamaLengkap(rs.getString("nama_lengkap"));
        cust.setEmail(rs.getString("email"));
        cust.setNoTelepon(rs.getString("no_telepon"));
        cust.setAlamat(rs.getString("alamat"));
        t.setCustomer(cust);

        // Map Layanan sesuai jenis
        if ("SEWA".equals(t.getJenisLayanan())) {
            SewaAlat sw = new SewaAlat(); // Catatan: Sesuaikan nama kelas SewaAlat-mu
            sw.setIdLayanan(rs.getString("id_layanan"));
            sw.setJenisAlat(rs.getString("jenis_alat"));
            sw.setNamaKamera(rs.getString("nama_kamera"));
            sw.setTarifPerHari(rs.getDouble("tarif_per_hari"));
            sw.setTglMulai(rs.getDate("tgl_mulai").toLocalDate());
            sw.setTglKembali(rs.getDate("tgl_kembali").toLocalDate());
            Date tglBack = rs.getDate("tgl_dikembalikan");
            if (tglBack != null) {
                sw.setTglDikembalikan(tglBack.toLocalDate());
            }
            t.setLayanan(sw);
        } else {
            JasaFoto jf = new JasaFoto();
            jf.setIdLayanan(rs.getString("id_layanan"));
            jf.setFotografer(rs.getString("fotografer"));
            jf.setPaket(rs.getString("paket"));
            jf.setTglSesi(rs.getDate("tgl_sesi").toLocalDate());
            jf.setDurasiJam(rs.getInt("durasi_jam"));
            jf.setJumlahFotoEdit(rs.getInt("jumlah_foto_edit"));
            jf.setHargaDasar(rs.getDouble("harga_dasar"));
            t.setLayanan(jf);
        }
        return t;
    }
}
