package studiokita.controller;

import studiokita.model.User;
import studiokita.model.dao.DatabaseConnection;
import studiokita.model.dao.UserDAO;

import java.sql.SQLException;

/**
 * ============================================================
 *  AuthController — Logika Login, Logout, dan Session
 *
 *  MVC Role: Controller
 *  - Menerima input dari LoginView
 *  - Memanggil UserDAO untuk query ke database
 *  - Mengembalikan hasil ke View
 *
 *  Session disimpan di static field currentUser.
 * ============================================================
 */
public class AuthController {

    // ── Session ───────────────────────────────────────────────
    private static User currentUser = null;

    private AuthController() {}

    // ── Login ─────────────────────────────────────────────────

    /**
     * Memproses login: validasi input, query DB, set session.
     *
     * @return LoginResult berisi status dan pesan error (jika ada)
     */
    public static LoginResult login(String username, String password) {
        // Validasi input kosong
        if (username == null || username.isBlank())
            return LoginResult.fail("Username tidak boleh kosong.");
        if (password == null || password.isBlank())
            return LoginResult.fail("Password tidak boleh kosong.");

        try {
            // Cek koneksi database
            if (!DatabaseConnection.isConnected()) {
                DatabaseConnection.getConnection(); // coba konek
            }

            // Query ke database via UserDAO
            User user = UserDAO.login(username.trim(), password);

            if (user == null) {
                return LoginResult.fail("Username atau password salah.");
            }

            // Set session
            currentUser = user;
            return LoginResult.success(user);

        } catch (SQLException e) {
            return LoginResult.fail(
                "Gagal terhubung ke database.\n"
                + "Pastikan XAMPP (MySQL) sudah berjalan.\n"
                + "Error: " + e.getMessage()
            );
        }
    }

    // ── Logout ────────────────────────────────────────────────

    /** Menghapus session user aktif. */
    public static void logout() {
        currentUser = null;
    }

    // ── Session Getter ────────────────────────────────────────

    /** Mengembalikan user yang sedang login. */
    public static User getCurrentUser() { return currentUser; }

    /** Cek apakah user aktif adalah Admin. */
    public static boolean isAdmin() {
        return currentUser != null && "ADMIN".equals(currentUser.getRole());
    }

    /** Cek apakah ada user yang sedang login. */
    public static boolean isLoggedIn() { return currentUser != null; }

    // ── Inner Class: LoginResult ──────────────────────────────

    /**
     * Hasil proses login.
     * View hanya perlu periksa isSuccess() dan getMessage().
     */
    public static class LoginResult {
        private final boolean success;
        private final String  message;
        private final User    user;

        private LoginResult(boolean s, String m, User u) {
            success = s; message = m; user = u;
        }

        public static LoginResult success(User u) {
            return new LoginResult(true, "Login berhasil.", u);
        }
        public static LoginResult fail(String msg) {
            return new LoginResult(false, msg, null);
        }

        public boolean isSuccess() { return success; }
        public String  getMessage(){ return message; }
        public User    getUser()   { return user; }
    }
}
