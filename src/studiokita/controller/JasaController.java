package studiokita.controller;

import studiokita.model.*;
import studiokita.model.dao.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;

/**
 * JasaController — Logika Bisnis Jasa Foto
 * MVC Role: Controller
 * UPGRADE: Proteksi Status Awal 'PENDING' untuk Validasi Admin & Sinkronisasi Real-time.
 */
public class JasaController {

    public static final String[] PAKET_FOTO = {
        "Paket Reguler", "Paket Prewedding", "Paket Wisuda", "Paket Produk", "Paket Portrait"
    };
    
    public static final double[] HARGA_DASAR = {
        300_000, 800_000, 400_000, 500_000, 350_000
    };

    public static List<Transaksi> getAllJasa() {
        try {
            return TransaksiDAO.getAll().stream()
                .filter(t -> "JASA".equals(t.getJenisLayanan())).toList();
        } catch (SQLException e) { logError(e); return Collections.emptyList(); }
    }

    public static List<Transaksi> getJasaByCustomer(String username) {
        try {
            return TransaksiDAO.getByCustomer(username).stream()
                .filter(t -> "JASA".equals(t.getJenisLayanan())).toList();
        } catch (SQLException e) { logError(e); return Collections.emptyList(); }
    }

    public static String simpanJasa(String namaCustomer, String username, String telepon,
                                    int paketIdx, String fotografer,
                                    String tglSesiStr, int durasi, int fotoEdit) {
        if (namaCustomer.isBlank() || telepon.isBlank() || fotografer.isBlank())
            return "Nama customer, telepon, dan fotografer wajib diisi!";

        LocalDate tglSesi;
        try { tglSesi = LocalDate.parse(tglSesiStr.trim()); }
        catch (DateTimeParseException e) { return "Format tanggal salah! Gunakan: yyyy-MM-dd"; }

        try {
            Customer cust = UserDAO.getCustomerByUsername(username.trim());
            if (cust == null) {
                cust = new Customer(username.trim(), "pass123", namaCustomer, "", telepon, "");
                UserDAO.insertCustomer(cust);
            } else {
                cust.setNamaLengkap(namaCustomer);
                cust.setNoTelepon(telepon);
                UserDAO.updateCustomer(cust);
            }

            String idJasa = JasaFotoDAO.generateId();
            JasaFoto jf = new JasaFoto(idJasa, fotografer, PAKET_FOTO[paketIdx],
                                        tglSesi, durasi, fotoEdit, HARGA_DASAR[paketIdx]);

            String idTrx = TransaksiDAO.generateId();
            Transaksi trx = new Transaksi(idTrx, cust, jf);
            
            // ================================================================
            // FIX BUG REALTIME: Paksa status transaksi baru menjadi PENDING
            // ================================================================
            trx.setStatus("PENDING"); 

            TransaksiDAO.insert(trx);
            return "OK|" + String.format("%.0f", jf.hitungBiaya());

        } catch (SQLException e) { return "Error DB: " + e.getMessage(); }
    }

    public static double hitungEstimasi(int paketIdx, int durasi, int fotoEdit) {
        return HARGA_DASAR[paketIdx] + (durasi * 100_000) + (fotoEdit * 15_000);
    }

    public static boolean hapus(String idLayanan) {
        try { return JasaFotoDAO.delete(idLayanan); }
        catch (SQLException e) { logError(e); return false; }
    }

    private static void logError(SQLException e) {
        System.err.println("[JasaController] " + e.getMessage()); 
    }
}