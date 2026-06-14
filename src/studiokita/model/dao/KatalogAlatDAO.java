package studiokita.model.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import studiokita.model.SewaAlat;

public class KatalogAlatDAO {

    /**
     * Mencari kamera berdasarkan nama/jenis yang berstatus AVAILABLE.
     * Karena database-mu memakai sistem 'layanan_sewa', status available 
     * dihitung jika alat tersebut sedang tidak disewa (tidak dalam status APPROVED).
     */
    public static List<SewaAlat> cariKatalogAvailable(String keyword) throws SQLException {
        List<SewaAlat> list = new ArrayList<>();
        
        // Query taktis: Ambil dari master_alat yang ID-nya TIDAK SEDANG aktif disewa (STATUS 'APPROVED')
        String sql = "SELECT * FROM master_alat WHERE (nama_kamera LIKE ? OR jenis_alat LIKE ?) "
                   + "AND id_alat NOT IN ("
                   + "   SELECT id_alat FROM transaksi_sewa WHERE status_pesanan = 'APPROVED' AND tgl_dikembalikan IS NULL"
                   + ")";

        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, "%" + keyword + "%");
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    SewaAlat s = new SewaAlat();
                    s.setIdLayanan(rs.getString("id_alat")); // Ikat ID Alat ke ID Layanan sementara
                    s.setJenisAlat(rs.getString("jenis_alat"));
                    s.setNamaKamera(rs.getString("nama_kamera"));
                    s.setTarifPerHari(rs.getDouble("tarif_per_hari"));
                    s.setFotoUrl(rs.getString("foto_url"));
                    list.add(s);
                }
            }
        }
        return list;
    }
}