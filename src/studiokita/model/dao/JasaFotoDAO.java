package studiokita.model.dao;

import studiokita.model.JasaFoto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * JasaFotoDAO — Data Access Object untuk tabel `layanan_jasa`
 */
public class JasaFotoDAO {

    public static List<JasaFoto> getAll() throws SQLException {
        List<JasaFoto> list = new ArrayList<>();
        String sql = "SELECT * FROM layanan_jasa ORDER BY created_at DESC";
        Connection conn = DatabaseConnection.getConnection();
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public static JasaFoto getById(String idLayanan) throws SQLException {
        String sql = "SELECT * FROM layanan_jasa WHERE id_layanan = ?";
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, idLayanan);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    public static int insert(JasaFoto j) throws SQLException {
        String sql = "INSERT INTO layanan_jasa "
                   + "(id_layanan,fotografer,paket,tgl_sesi,durasi_jam,jumlah_foto_edit,harga_dasar) "
                   + "VALUES (?,?,?,?,?,?,?)";
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, j.getIdLayanan());
            ps.setString(2, j.getFotografer());
            ps.setString(3, j.getPaket());
            ps.setDate(4,   Date.valueOf(j.getTglSesi()));
            ps.setInt(5,    j.getDurasiJam());
            ps.setInt(6,    j.getJumlahFotoEdit());
            ps.setDouble(7, j.getHargaDasar());
            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) return keys.getInt(1);
                }
            }
        }
        return -1;
    }

    public static boolean delete(String idLayanan) throws SQLException {
        String delTrx  = "DELETE FROM transaksi WHERE id_layanan=?";
        String delJasa = "DELETE FROM layanan_jasa WHERE id_layanan=?";
        Connection conn = DatabaseConnection.getConnection();
        conn.setAutoCommit(false);
        try (PreparedStatement ps1 = conn.prepareStatement(delTrx);
             PreparedStatement ps2 = conn.prepareStatement(delJasa)) {
            ps1.setString(1, idLayanan); ps1.executeUpdate();
            ps2.setString(1, idLayanan); int rows = ps2.executeUpdate();
            conn.commit(); return rows > 0;
        } catch (SQLException e) { conn.rollback(); throw e; }
        finally { conn.setAutoCommit(true); }
    }

    public static String generateId() throws SQLException {
        String sql = "SELECT COUNT(*) FROM layanan_jasa";
        Connection conn = DatabaseConnection.getConnection();
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            int count = rs.next() ? rs.getInt(1) : 0;
            return String.format("JF%03d", count + 1);
        }
    }

    private static JasaFoto mapRow(ResultSet rs) throws SQLException {
        JasaFoto j = new JasaFoto();
        j.setIdLayanan(rs.getString("id_layanan"));
        j.setFotografer(rs.getString("fotografer"));
        j.setPaket(rs.getString("paket"));
        j.setTglSesi(rs.getDate("tgl_sesi").toLocalDate());
        j.setDurasiJam(rs.getInt("durasi_jam"));
        j.setJumlahFotoEdit(rs.getInt("jumlah_foto_edit"));
        j.setHargaDasar(rs.getDouble("harga_dasar"));
        return j;
    }
}
