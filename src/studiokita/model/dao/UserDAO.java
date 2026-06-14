package studiokita.model.dao;

import studiokita.model.Admin;
import studiokita.model.Customer;
import studiokita.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * ============================================================
 *  UserDAO — Data Access Object untuk tabel `users`
 *
 *  Berisi operasi: login, getAll, getById, insert, update, delete
 *  MVC Role: bagian Model (layer akses database)
 * ============================================================
 */
public class UserDAO {

    // ── LOGIN ─────────────────────────────────────────────────

    /**
     * Mencari user berdasarkan username + password.
     * Mengembalikan objek Admin atau Customer sesuai role-nya.
     *
     * @return User jika cocok, null jika tidak ada
     */
    public static User login(String username, String password) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapUser(rs);
                }
            }
        }
        return null;
    }

    // ── READ ──────────────────────────────────────────────────

    /** Ambil semua customer (role = 'CUSTOMER'). */
    public static List<Customer> getAllCustomers() throws SQLException {
        List<Customer> list = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE role = 'CUSTOMER' ORDER BY nama_lengkap";

        Connection conn = DatabaseConnection.getConnection();
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                list.add((Customer) mapUser(rs));
            }
        }
        return list;
    }

    /** Cari customer berdasarkan username. */
    public static Customer getCustomerByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ? AND role = 'CUSTOMER'";
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return (Customer) mapUser(rs);
            }
        }
        return null;
    }

    /** Cek apakah username sudah dipakai. */
    public static boolean isUsernameExist(String username) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    // ── CREATE ────────────────────────────────────────────────

    /**
     * Menyimpan customer baru ke database.
     *
     * @return true jika berhasil
     */
    public static boolean insertCustomer(Customer c) throws SQLException {
        String sql = "INSERT INTO users (username,password,nama_lengkap,role,email,no_telepon,alamat) "
                   + "VALUES (?,?,?,'CUSTOMER',?,?,?)";

        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, c.getUsername());
            ps.setString(2, c.getPassword());
            ps.setString(3, c.getNamaLengkap());
            ps.setString(4, c.getEmail());
            ps.setString(5, c.getNoTelepon());
            ps.setString(6, c.getAlamat());

            return ps.executeUpdate() > 0;
        }
    }

    // ── UPDATE ────────────────────────────────────────────────

    /**
     * Memperbarui data customer (kecuali username yang jadi PK FK).
     */
    public static boolean updateCustomer(Customer c) throws SQLException {
        String sql = "UPDATE users SET nama_lengkap=?, email=?, no_telepon=?, alamat=? "
                   + "WHERE username=? AND role='CUSTOMER'";

        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, c.getNamaLengkap());
            ps.setString(2, c.getEmail());
            ps.setString(3, c.getNoTelepon());
            ps.setString(4, c.getAlamat());
            ps.setString(5, c.getUsername());

            return ps.executeUpdate() > 0;
        }
    }

    /** Ganti password customer. */
    public static boolean updatePassword(String username, String newPassword) throws SQLException {
        String sql = "UPDATE users SET password=? WHERE username=?";
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newPassword);
            ps.setString(2, username);
            return ps.executeUpdate() > 0;
        }
    }

    // ── DELETE ────────────────────────────────────────────────

    /**
     * Menghapus customer berdasarkan username.
     * Karena ada FK ke transaksi (ON DELETE CASCADE), transaksi
     * milik customer ini juga otomatis terhapus.
     */
    public static boolean deleteCustomer(String username) throws SQLException {
        String sql = "DELETE FROM users WHERE username=? AND role='CUSTOMER'";
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            return ps.executeUpdate() > 0;
        }
    }

    // ── METODE TAMBAHAN UNTUK ADMIN ──────────────────────────

    /** Ambil semua user dengan role 'ADMIN'. */
    public static List<Admin> getAllAdmins() throws SQLException {
        List<Admin> list = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE role = 'ADMIN' ORDER BY nama_lengkap";

        Connection conn = DatabaseConnection.getConnection();
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add((Admin) mapUser(rs));
            }
        }
        return list;
    }

    /** Menyimpan admin baru ke database. */
    public static boolean insertAdmin(Admin a) throws SQLException {
        String sql = "INSERT INTO users (username, password, nama_lengkap, role, email, admin_level) "
                   + "VALUES (?, ?, ?, 'ADMIN', ?, ?)";

        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, a.getUsername());
            ps.setString(2, a.getPassword());
            ps.setString(3, a.getNamaLengkap());
            ps.setString(4, a.getEmail());
            ps.setString(5, a.getAdminLevel());

            return ps.executeUpdate() > 0;
        }
    }

    /** Menghapus admin berdasarkan username. */
    public static boolean deleteAdmin(String username) throws SQLException {
        String sql = "DELETE FROM users WHERE username=? AND role='ADMIN'";
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            return ps.executeUpdate() > 0;
        }
    }
    
    // ── MAPPER ───────────────────────────────────────────────

    /**
     * Mengubah satu baris ResultSet menjadi objek User (Admin/Customer).
     * Private helper — hanya dipakai di dalam DAO ini.
     */
    private static User mapUser(ResultSet rs) throws SQLException {
        String role = rs.getString("role");

        if ("ADMIN".equals(role)) {
            Admin a = new Admin();
            a.setId(rs.getInt("id"));
            a.setUsername(rs.getString("username"));
            a.setPassword(rs.getString("password"));
            a.setNamaLengkap(rs.getString("nama_lengkap"));
            a.setRole(role);
            a.setEmail(rs.getString("email"));
            a.setAdminLevel(rs.getString("admin_level"));
            return a;
        } else {
            Customer c = new Customer();
            c.setId(rs.getInt("id"));
            c.setUsername(rs.getString("username"));
            c.setPassword(rs.getString("password"));
            c.setNamaLengkap(rs.getString("nama_lengkap"));
            c.setRole(role);
            c.setEmail(rs.getString("email"));
            c.setNoTelepon(rs.getString("no_telepon"));
            c.setAlamat(rs.getString("alamat"));
            return c;
        }
    }
}
