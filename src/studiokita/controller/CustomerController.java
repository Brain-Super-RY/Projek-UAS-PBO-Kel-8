package studiokita.controller;

import studiokita.model.Customer;
import studiokita.model.dao.UserDAO;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

/**
 * CustomerController — Logika CRUD Customer
 * MVC Role: Controller (antara CustomerView dan UserDAO)
 */
public class CustomerController {

    public static List<Customer> getAllCustomers() {
        try { return UserDAO.getAllCustomers(); }
        catch (SQLException e) { showError(e); return Collections.emptyList(); }
    }

    /**
     * Simpan atau update customer.
     */
    public static String simpanCustomer(Customer c) {
        if (c.getNamaLengkap().isBlank() || c.getUsername().isBlank())
            return "Nama dan username wajib diisi!";
        
        try {
            if (UserDAO.isUsernameExist(c.getUsername())) {
                return UserDAO.updateCustomer(c) ? "OK" : "Gagal update data.";
            } else {
                return UserDAO.insertCustomer(c) ? "OK" : "Gagal simpan data.";
            }
        } catch (SQLException e) { return "Error DB: " + e.getMessage(); }
    }

    /**
     * Simpan customer baru. Validasi nama, telepon, dan duplikat username.
     * @return pesan sukses atau error
     */
    public static String tambahCustomer(String nama, String username, String password,
                                         String email, String telepon, String alamat) {
        if (nama.isBlank() || username.isBlank() || password.isBlank())
            return "Nama, username, dan password wajib diisi!";
        if (telepon.isBlank())
            return "No. telepon wajib diisi!";
        try {
            if (UserDAO.isUsernameExist(username))
                return "Username '" + username + "' sudah dipakai. Pilih yang lain.";
            Customer c = new Customer(username, password, nama, email, telepon, alamat);
            return UserDAO.insertCustomer(c) ? "OK" : "Gagal menyimpan data customer.";
        } catch (SQLException e) { return "Error DB: " + e.getMessage(); }
    }

    /** Update data customer yang ada. */
    public static String updateCustomer(Customer c) {
        if (c.getNamaLengkap().isBlank()) return "Nama lengkap wajib diisi!";
        try {
            return UserDAO.updateCustomer(c) ? "OK" : "Gagal memperbarui data.";
        } catch (SQLException e) { return "Error DB: " + e.getMessage(); }
    }

    /** Hapus customer berdasarkan username. */
    public static String deleteCustomer(String username) {
        try {
            return UserDAO.deleteCustomer(username) ? "OK" : "Gagal menghapus data.";
        } catch (SQLException e) { return "Error DB: " + e.getMessage(); }
    }

    private static void showError(SQLException e) {
        System.err.println("[CustomerController] DB Error: " + e.getMessage());
    }
}
