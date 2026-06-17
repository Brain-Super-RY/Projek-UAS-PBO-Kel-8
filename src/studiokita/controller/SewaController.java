package studiokita.controller;

import studiokita.model.*;
import studiokita.model.dao.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;

public class SewaController {

    /**
     * Helper Method: Mengambil data tarif dan kategori langsung dari database master_alat.
     * Menggunakan nama kamera sebagai keyword pencarian.
     */
    private static Object[] ambilDataAlatDariDB(String namaKamera) {
        Object[] data = new Object[2];
        data[0] = "Kamera";    // Default jenis_alat jika tidak ditemukan
        data[1] = 250000.0;    // Default tarif_per_hari jika tidak ditemukan

        String query = "SELECT jenis_alat, tarif_per_hari FROM master_alat WHERE nama_kamera = ?";
        
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://thomas.proxy.rlwy.net:49499/railway", "root", "qKuilazWpVhjLbYPsCsFZFZubZCSUBPI"); 
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setString(1, namaKamera.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    data[0] = rs.getString("jenis_alat");
                    data[1] = rs.getDouble("tarif_per_hari");
                }
            }
        } catch (SQLException e) {
            System.err.println("[SewaController] Gagal mengambil tarif dari DB: " + e.getMessage());
        }
        return data;
    }

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
     * Memeriksa apakah suatu spesifik unit gear (berdasarkan nama kamera) 
     * sudah disewa pada rentang tanggal yang dipilih.
     */
    public static boolean isJadwalBentrok(String namaKamera, LocalDate mulaiBaru, LocalDate kembaliBaru) {
        List<Transaksi> semuaSewa = getAllSewa();

        for (Transaksi t : semuaSewa) {
            if (t == null || !(t.getLayanan() instanceof SewaAlat s)) {
                continue;
            }

            if (s.getNamaKamera().equalsIgnoreCase(namaKamera)
                    && (t.getStatus().equalsIgnoreCase("APPROVED") || t.getStatus().equalsIgnoreCase("PENDING"))) {

                if (s.getTglDikembalikan() == null) {
                    LocalDate mulaiLama = s.getTglMulai();
                    LocalDate kembaliLama = s.getTglKembali();

                    // Rumus Tabrakan Jadwal: (MulaiBaru <= KembaliLama) && (KembaliBaru >= MulaiLama)
                    if (!mulaiBaru.isAfter(kembaliLama) && !kembaliBaru.isBefore(mulaiLama)) {
                        return true; 
                    }
                }
            }
        }
        return false; 
    }

    /**
     * Validasi dan simpan data sewa baru.
     * Parameter 'alatIdx' tetap dipertahankan agar tidak merusak parameter UI lama, 
     * namun harganya sekarang otomatis di-override langsung dari database online.
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

        // 3. Validasi bentrok jadwal
        if (isJadwalBentrok(namaKamera.trim(), tglMulai, tglKembali)) {
            return "BENTROK|Maaf, unit gear [" + namaKamera + "] sudah habis dibooking pada rentang tanggal tersebut!";
        }

        // 4. AMBIL DATA REAL-TIME DARI DATABASE (Menggantikan Array Statis)
        Object[] dataAlat = ambilDataAlatDariDB(namaKamera);
        String kategoriDipilih = (String) dataAlat[0];
        double tarifDinamis = (Double) dataAlat[1];

        try {
            // Cek apakah username sudah ada
            Customer cust = UserDAO.getCustomerByUsername(username.trim());

            if (cust == null) {
                cust = new Customer(username.trim(), "pass123", namaCustomer, "", telepon, "");
                UserDAO.insertCustomer(cust);
            } else {
                cust.setNamaLengkap(namaCustomer);
                cust.setNoTelepon(telepon);
                UserDAO.updateCustomer(cust);
            }

            // Buat objek layanan dengan tarif dinamis hasil query database
            String idSewa = SewaAlatDAO.generateId();
            SewaAlat sw = new SewaAlat(idSewa, kategoriDipilih, namaKamera.trim(),
                    tglMulai, tglKembali, tarifDinamis);

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
     * UBAHAN: Menggunakan parameter namaKamera untuk mencari tarif asli di database.
     */
    public static double hitungEstimasi(String namaKamera, String tglMulaiStr, String tglKembaliStr) {
        try {
            LocalDate m = LocalDate.parse(tglMulaiStr.trim());
            LocalDate k = LocalDate.parse(tglKembaliStr.trim());
            long dur = java.time.temporal.ChronoUnit.DAYS.between(m, k);
            
            // Ambil harga asli dari DB online
            Object[] dataAlat = ambilDataAlatDariDB(namaKamera);
            double tarifDinamis = (Double) dataAlat[1];
            
            return Math.max(0, dur) * tarifDinamis;
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

    public static List<SewaAlat> dapatkanKatalogCari(String keyword) {
        try {
            return KatalogAlatDAO.cariKatalogAvailable(keyword);
        } catch (Exception e) {
            System.err.println("Gagal memuat katalog: " + e.getMessage());
            return new java.util.ArrayList<>();
        }
    }

    private static void logError(SQLException e) {
        System.err.println("[SewaController] " + e.getMessage());
    }
}