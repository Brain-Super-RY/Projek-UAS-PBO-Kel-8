package studiokita.controller;

import studiokita.model.Transaksi;
import studiokita.model.dao.TransaksiDAO;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

/**
 * TransaksiController — Statistik & Laporan Transaksi
 * MVC Role: Controller (dipakai DashboardView dan PenghasilanView)
 */
public class TransaksiController {

    public static List<Transaksi> getAll() {
        try { return TransaksiDAO.getAll(); }
        catch (SQLException e) { logError(e); return Collections.emptyList(); }
    }

    public static List<Transaksi> getByCustomer(String username) {
        try { return TransaksiDAO.getByCustomer(username); }
        catch (SQLException e) { logError(e); return Collections.emptyList(); }
    }

    public static double getTotalPenghasilan() {
        try { return TransaksiDAO.getTotalPenghasilan(); }
        catch (SQLException e) { logError(e); return 0; }
    }

    public static double getTotalByCustomer(String username) {
        try { return TransaksiDAO.getTotalByCustomer(username); }
        catch (SQLException e) { logError(e); return 0; }
    }

    public static int countSewa() {
        try { return TransaksiDAO.countByJenis("SEWA"); }
        catch (SQLException e) { logError(e); return 0; }
    }

    public static int countJasa() {
        try { return TransaksiDAO.countByJenis("JASA"); }
        catch (SQLException e) { logError(e); return 0; }
    }

    public static boolean hapus(String idTransaksi) {
        try { return TransaksiDAO.delete(idTransaksi); }
        catch (SQLException e) { logError(e); return false; }
    }

    private static void logError(SQLException e) {
        System.err.println("[TransaksiController] " + e.getMessage()); }
}
