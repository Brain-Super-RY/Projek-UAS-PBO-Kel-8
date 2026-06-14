package studiokita.model.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    // ── Konfigurasi Koneksi ──
    private static final String HOST          = "thomas.proxy.rlwy.net";
    private static final String PORT          = "49499";
    private static final String DATABASE_NAME = "railway";
    private static final String USER          = "root";
    private static final String PASSWORD      = "qKuilazWpVhjLbYPsCsFZFZubZCSUBPI";          
    private static final String DRIVER        = "com.mysql.cj.jdbc.Driver";

    private static final String URL =
            "jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE_NAME
            + "?useSSL=false"
            + "&serverTimezone=Asia/Jakarta"
            + "&allowPublicKeyRetrieval=true"
            + "&useUnicode=true"
            + "&characterEncoding=utf8";

    // ── Singleton Instance ────────────────────────────────────
    private static Connection connection = null;

    private DatabaseConnection() {}

    /**
     * Mengembalikan koneksi aktif ke database.
     * Jika koneksi mati/tertutup, otomatis dibuat ulang.
     *
     * @return Connection object yang siap dipakai
     * @throws SQLException jika koneksi gagal
     */
    public static Connection getConnection() throws SQLException {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName(DRIVER);
                // Sudah disesuaikan memanggil USER dan PASSWORD yang benar
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("[DB] Koneksi berhasil ke database: " + DATABASE_NAME);
            }
        } catch (ClassNotFoundException e) {
            throw new SQLException(
                "Driver MySQL tidak ditemukan!\n" +
                "Pastikan mysql-connector-j.jar sudah ditambahkan ke Libraries NetBeans.\n" +
                "Detail: " + e.getMessage()
            );
        }
        return connection;
    }

    /**
     * Menutup koneksi database.
     * Panggil saat aplikasi ditutup (opsional).
     */
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
                System.out.println("[DB] Koneksi ditutup.");
            } catch (SQLException e) {
                System.err.println("[DB] Gagal menutup koneksi: " + e.getMessage());
            }
        }
    }

    /**
     * Cek apakah koneksi ke database aktif.
     * Berguna untuk tampilkan status di UI.
     */
    public static boolean isConnected() {
        try {
            return connection != null && !connection.isClosed() && connection.isValid(2);
        } catch (SQLException e) {
            return false;
        }
    }
}