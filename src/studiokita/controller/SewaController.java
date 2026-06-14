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
 * FIXED: Pencegah double-booking berbasis Nama Spesifik Unit Gear agar tidak memblokir kategori global.
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
     * FIXED: Memeriksa apakah suatu spesifik unit gear (berdasarkan nama kamera) 
     * sudah disewa pada rentang tanggal yang dipilih.
     */
    public static boolean isJadwalBentrok(String namaKamera, LocalDate mulaiBaru, LocalDate kembaliBaru) {
        List<Transaksi> semuaSewa = getAllSewa();

        for (Transaksi t : semuaSewa) {
            if (t == null || !(t.getLayanan() instanceof SewaAlat s)) {
                continue;
            }

            // PERBAIKAN UTAMA: Validasi bentrok wajib membandingkan Nama Spesifik Kamera/Alat (bukan kategori makro lagi)
            if (s.getNamaKamera().equalsIgnoreCase(namaKamera)
                    && (t.getStatus().equalsIgnoreCase("APPROVED") || t.getStatus().equalsIgnoreCase("PENDING"))) {

                // Pastikan alat tersebut belum dikembalikan oleh customer lama
                if (s.getTglDikembalikan() == null) {
                    LocalDate mulaiLama = s.getTglMulai();
                    LocalDate kembaliLama = s.getTglKembali();

                    // Rumus Tabrakan Jadwal: (MulaiBaru <= KembaliLama) && (KembaliBaru >= MulaiLama)
                    if (!mulaiBaru.isAfter(kembaliLama) && !kembaliBaru.isBefore(mulaiLama)) {
                        return true; // Terjadi bentrok nyata pada unit yang sama!
                    }
                }
            }
        }
        return false; // Aman digunakan karena unit berbeda atau jadwal luang
    }

    /**
     * Validasi dan simpan data sewa baru. Controller memastikan semua input
     * valid serta memeriksa bentrok jadwal sebelum disimpan ke DAO.
     *
     * @return "OK|TotalBiaya" atau pesan error
     */
    public static String simpanSewa(String namaCustomer, String username, String telepon,
            int alatIdx, String namaKamera,
            String tglMulaiStr, String tglKembaliStr) {

        // 1. Validasi input standar
        if (namaCustomer.isBlank() || telepon.isBlank() || namaKamera.isBlank()) {
            return "Nama customer, telepon, dan nama alat wajib diisi!";
        }

        // 2. Validasi format tanggal
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

        // 3. FIXED: Kirim parameter 'namaKamera' ke validator bentrok jadwal
        if (isJadwalBentrok(namaKamera.trim(), tglMulai, tglKembali)) {
            return "BENTROK|Maaf, unit gear [" + namaKamera + "] sudah habis dibooking pada rentang tanggal tersebut!";
        }

        String kategoriDipilih = (alatIdx >= 0 && alatIdx < JENIS_ALAT.length) ? JENIS_ALAT[alatIdx] : "Kamera DSLR";

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
            SewaAlat sw = new SewaAlat(idSewa, kategoriDipilih, namaKamera.trim(),
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

    public static java.util.List<studiokita.model.SewaAlat> dapatkanKatalogCari(String keyword) {
        try {
            return studiokita.model.dao.KatalogAlatDAO.cariKatalogAvailable(keyword);
        } catch (Exception e) {
            System.err.println("Gagal memuat katalog: " + e.getMessage());
            return new java.util.ArrayList<>();
        }
    }

    private static void logError(SQLException e) {
        System.err.println("[SewaController] " + e.getMessage());
    }
}