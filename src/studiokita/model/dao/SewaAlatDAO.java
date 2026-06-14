package studiokita.model.dao;

import studiokita.model.SewaAlat;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * SewaAlatDAO — Data Access Object untuk tabel `layanan_sewa`
 */
public class SewaAlatDAO {

    // ── READ ──────────────────────────────────────────────────

    /** Ambil semua data sewa alat. */
    public static List<SewaAlat> getAll() throws SQLException {
        List<SewaAlat> list = new ArrayList<>();
        String sql = "SELECT * FROM layanan_sewa ORDER BY created_at DESC";

        Connection conn = DatabaseConnection.getConnection();
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    /** Ambil data sewa berdasarkan id_layanan. */
    public static SewaAlat getById(String idLayanan) throws SQLException {
        String sql = "SELECT * FROM layanan_sewa WHERE id_layanan = ?";
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, idLayanan);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    // ── CREATE ────────────────────────────────────────────────

    /**
     * Menyimpan data sewa baru ke database.
     * @return ID auto-increment yang baru dibuat, atau -1 jika gagal
     */
    public static int insert(SewaAlat s) throws SQLException {
        String sql = "INSERT INTO layanan_sewa "
                   + "(id_layanan,jenis_alat,nama_kamera,tarif_per_hari,tgl_mulai,tgl_kembali) "
                   + "VALUES (?,?,?,?,?,?)";

        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, s.getIdLayanan());
            ps.setString(2, s.getJenisAlat());
            ps.setString(3, s.getNamaKamera());
            ps.setDouble(4, s.getTarifPerHari());
            ps.setDate(5,   Date.valueOf(s.getTglMulai()));
            ps.setDate(6,   Date.valueOf(s.getTglKembali()));

            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) return keys.getInt(1);
                }
            }
        }
        return -1;
    }

    // ── UPDATE ────────────────────────────────────────────────

    /**
     * Memperbarui tanggal aktual pengembalian alat
     * (dipakai saat proses pengembalian).
     */
    public static boolean updatePengembalian(String idLayanan, LocalDate tglDikembalikan) throws SQLException {
        String sql = "UPDATE layanan_sewa SET tgl_dikembalikan=? WHERE id_layanan=?";
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(tglDikembalikan));
            ps.setString(2, idLayanan);
            return ps.executeUpdate() > 0;
        }
    }

    // ── DELETE ────────────────────────────────────────────────

    public static boolean delete(String idLayanan) throws SQLException {
        // Hapus dulu dari transaksi (atau gunakan FK cascade)
        String delTrx = "DELETE FROM transaksi WHERE id_layanan=?";
        String delSewa = "DELETE FROM layanan_sewa WHERE id_layanan=?";

        Connection conn = DatabaseConnection.getConnection();
        conn.setAutoCommit(false);  // mulai transaction
        try (PreparedStatement ps1 = conn.prepareStatement(delTrx);
             PreparedStatement ps2 = conn.prepareStatement(delSewa)) {
            ps1.setString(1, idLayanan); ps1.executeUpdate();
            ps2.setString(1, idLayanan); int rows = ps2.executeUpdate();
            conn.commit();
            return rows > 0;
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    // ── ID GENERATOR ─────────────────────────────────────────

    /**
     * Menghasilkan ID sewa otomatis: SW001, SW002, ...
     * Berdasarkan jumlah baris di tabel layanan_sewa.
     */
    public static String generateId() throws SQLException {
        String sql = "SELECT COUNT(*) FROM layanan_sewa";
        Connection conn = DatabaseConnection.getConnection();
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            int count = rs.next() ? rs.getInt(1) : 0;
            return String.format("SW%03d", count + 1);
        }
    }

    // ── MAPPER ───────────────────────────────────────────────
    private static SewaAlat mapRow(ResultSet rs) throws SQLException {
        SewaAlat s = new SewaAlat();
        s.setIdLayanan(rs.getString("id_layanan"));
        s.setJenisAlat(rs.getString("jenis_alat"));
        s.setNamaKamera(rs.getString("nama_kamera"));
        s.setTarifPerHari(rs.getDouble("tarif_per_hari"));
        s.setTglMulai(rs.getDate("tgl_mulai").toLocalDate());
        s.setTglKembali(rs.getDate("tgl_kembali").toLocalDate());
        Date tglKembaliAktual = rs.getDate("tgl_dikembalikan");
        if (tglKembaliAktual != null) s.setTglDikembalikan(tglKembaliAktual.toLocalDate());
        return s;
    }
}
