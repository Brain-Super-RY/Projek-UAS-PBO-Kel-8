package studiokita.controller;

import studiokita.model.*;
import studiokita.model.dao.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;

/**
 * SewaController — Logika Bisnis Penyewaan Alat MVC Role: Controller
 */
public class SewaController {

    public static final String[] JENIS_ALAT = {
        "Kamera DSLR", "Kamera Mirrorless", "Lensa Wide", "Lensa Tele",
        "Lensa Portrait", "Tripod", "Flash Eksternal", "Softbox", "Drone"
    };
    public static final double[] TARIF = {
        250_000, 300_000, 150_000, 175_000,
        125_000, 50_000, 75_000, 80_000, 500_000
    };

    /**
     * Ambil semua data sewa dari DB via DAO.
     */
    public static List<Transaksi> getAllSewa() {
        try {
            return TransaksiDAO.getAll().stream()
                    .filter(t -> "SEWA".equals(t.getJenisLayanan()))
                    .toList();
        } catch (SQLException e) {
            logError(e);
            return Collections.emptyList();
        }
    }

    /**
     * Ambil sewa milik satu customer.
     */
    public static List<Transaksi> getSewaByCustomer(String username) {
        try {
            return TransaksiDAO.getByCustomer(username).stream()
                    .filter(t -> "SEWA".equals(t.getJenisLayanan()))
                    .toList();
        } catch (SQLException e) {
            logError(e);
            return Collections.emptyList();
        }
    }

    /**
     * Validasi dan simpan data sewa baru. Controller memastikan semua input
     * valid sebelum ke DAO.
     *
     * @return "OK" atau pesan error
     */
    public static String simpanSewa(String namaCustomer, String username, String telepon,
            int alatIdx, String namaKamera,
            String tglMulaiStr, String tglKembaliStr) {
        // Validasi input
        if (namaCustomer.isBlank() || telepon.isBlank() || namaKamera.isBlank()) {
            return "Nama customer, telepon, dan nama alat wajib diisi!";
        }

        LocalDate tglMulai, tglKembali;
        try {
            tglMulai = LocalDate.parse(tglMulaiStr.trim());
            tglKembali = LocalDate.parse(tglKembaliStr.trim());
        } catch (DateTimeParseException e) {
            return "Format tanggal salah! Gunakan format: yyyy-MM-dd";
        }
        if (!tglKembali.isAfter(tglMulai)) {
            return "Tanggal kembali harus setelah tanggal mulai!";
        }

        try {
            // Cek apakah username sudah ada (Case Insensitive)
            Customer cust = UserDAO.getCustomerByUsername(username.trim());
            
            if (cust == null) {
                // Jika belum ada, buat baru
                cust = new Customer(username.trim(), "pass123", namaCustomer, "", telepon, "");
                UserDAO.insertCustomer(cust);
            } else {
                // Jika sudah ada, update info terbarunya (nama/telepon)
                cust.setNamaLengkap(namaCustomer);
                cust.setNoTelepon(telepon);
                UserDAO.updateCustomer(cust);
            }

            // Buat objek layanan
            String idSewa = SewaAlatDAO.generateId();
            SewaAlat sw = new SewaAlat(idSewa, JENIS_ALAT[alatIdx], namaKamera,
                    tglMulai, tglKembali, TARIF[alatIdx]);

            // Buat transaksi
            String idTrx = TransaksiDAO.generateId();
            Transaksi trx = new Transaksi(idTrx, cust, sw);

            TransaksiDAO.insert(trx);
            return "OK|" + String.format("%.0f", sw.hitungBiaya());

        } catch (SQLException e) {
            return "Error DB: " + e.getMessage();
        }
    }

    /**
     * Proses pengembalian alat dan hitung denda.
     */
    public static String prosesKembalikan(String idLayanan, String tglAktualStr) {
        try {
            LocalDate tglAktual = LocalDate.parse(tglAktualStr.trim());
            SewaAlatDAO.updatePengembalian(idLayanan, tglAktual);
            SewaAlat sw = SewaAlatDAO.getById(idLayanan);
            if (sw == null) {
                return "Data tidak ditemukan.";
            }
            long terlambat = sw.getHariTerlambat();
            double denda = sw.hitungDenda();
            return "OK|" + terlambat + "|" + String.format("%.0f", denda);
        } catch (DateTimeParseException e) {
            return "Format tanggal salah! Gunakan: yyyy-MM-dd";
        } catch (SQLException e) {
            return "Error DB: " + e.getMessage();
        }
    }

    /**
     * Hitung estimasi biaya sewa (live preview di form).
     */
    public static double hitungEstimasi(int alatIdx, String tglMulaiStr, String tglKembaliStr) {
        try {
            LocalDate m = LocalDate.parse(tglMulaiStr.trim());
            LocalDate k = LocalDate.parse(tglKembaliStr.trim());
            long dur = java.time.temporal.ChronoUnit.DAYS.between(m, k);
            return Math.max(0, dur) * TARIF[alatIdx];
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Hapus data sewa dan transaksi terkait.
     */
    public static boolean hapus(String idLayanan) {
        try {
            return SewaAlatDAO.delete(idLayanan);
        } catch (SQLException e) {
            logError(e);
            return false;
        }
    }

    private static void logError(SQLException e) {
        System.err.println("[SewaController] " + e.getMessage());
    }
}
