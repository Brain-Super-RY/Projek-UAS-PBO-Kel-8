package studiokita.view;

import studiokita.ThemeManager;
import studiokita.UIKit;
import studiokita.controller.AuthController;
import studiokita.controller.CustomerController;
import studiokita.controller.TransaksiController;
import studiokita.model.Transaksi;
import studiokita.model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DashboardPanel — Dual Role Analytics Center (Admin vs Customer).
 * Memisahkan hak akses visualisasi data secara absolut demi keamanan dan privasi data.
 */
public class DashboardPanel extends JPanel implements MainFrame.Refreshable {

    private JLabel lblCard1, lblCard2, lblCard3, lblCard4;
    private JLabel lblTitleTabel;
    private JTable tabelTerbaru;
    private DefaultTableModel modelTerbaru;
    private JPanel pnlCards;

    public DashboardPanel() {
        initComponents();
        refresh();
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 0));
        setOpaque(false);

        // 1. HEADER DINAMIS BERDASARKAN ROLE
        String roleTitle = AuthController.isAdmin() ? "ADMIN EXECUTIVE CENTER" : "CUSTOMER HUB";
        String roleSubtitle = AuthController.isAdmin() ? 
                "Ringkasan performa bisnis, omzet, dan log aktivitas studio" : 
                "Pantau status sewa alat, jadwal sesi foto, dan riwayat transaksi Anda";
        
        add(UIKit.topBar(roleTitle, roleSubtitle, UIKit.currentGold()), BorderLayout.NORTH);

        // Panel Utama Tengah
        JPanel pnlCenter = new JPanel();
        pnlCenter.setLayout(new BoxLayout(pnlCenter, BoxLayout.Y_AXIS));
        pnlCenter.setOpaque(false);
        pnlCenter.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 2. BARIS KARTU STATISTIK (4 Cards Layout)
        pnlCards = new JPanel(new GridLayout(1, 4, 15, 0));
        pnlCards.setOpaque(false);
        pnlCards.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        // Inisialisasi label penampung angka statistik
        if (AuthController.isAdmin()) {
            lblCard1 = createStatCard(pnlCards, "TOTAL OMZET", "Rp 0", UIKit.currentGold());
            lblCard2 = createStatCard(pnlCards, "ORDERAN PENDING", "0 Trx", UIKit.RED);
            lblCard3 = createStatCard(pnlCards, "ALAT TERSEWA", "0 Unit", new Color(30, 144, 255));
            lblCard4 = createStatCard(pnlCards, "TOTAL PELANGGAN", "0 User", new Color(155, 89, 182));
        } else {
            lblCard1 = createStatCard(pnlCards, "TOTAL PENGELUARAN", "Rp 0", UIKit.currentGold());
            lblCard2 = createStatCard(pnlCards, "SEWA AKTIF SAYA", "0 Alat", new Color(30, 144, 255));
            lblCard3 = createStatCard(pnlCards, "BOOKING JASA SAYA", "0 Paket", new Color(155, 89, 182));
            lblCard4 = createStatCard(pnlCards, "STATUS AKUN", "REGULAR", new Color(46, 204, 113));
        }

        pnlCenter.add(pnlCards);
        pnlCenter.add(UIKit.gap(25));

        // 3. BAGIAN BAWAH: TABEL DATA DINAMIS
        JPanel pnlTableCard = UIKit.glassCard();
        pnlTableCard.setLayout(new BorderLayout(0, 12));
        pnlTableCard.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        String titleTabel = AuthController.isAdmin() ? "TRANSAKSI TERBARU" : "RIWAYAT AKTIVITAS TRANSAKSI SAYA";
        lblTitleTabel = new JLabel(titleTabel);
        lblTitleTabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitleTabel.setForeground(UIKit.fgPrimary());
        pnlTableCard.add(lblTitleTabel, BorderLayout.NORTH);

        // Konfigurasi Kolom Tabel
        String[] cols = AuthController.isAdmin() ? 
                new String[]{"ID TRX", "PELANGGAN", "JENIS", "LAYANAN / GEAR", "STATUS TOTAL"} :
                new String[]{"ID TRX", "JENIS LAYANAN", "DESKRIPSI ITEM", "TOTAL BIAYA", "STATUS"};
                
        modelTerbaru = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabelTerbaru = new JTable(modelTerbaru);
        UIKit.styleTable(tabelTerbaru);

        JScrollPane scroll = new JScrollPane(tabelTerbaru);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        pnlTableCard.add(scroll, BorderLayout.CENTER);

        pnlCenter.add(pnlTableCard);
        add(pnlCenter, BorderLayout.CENTER);

        // Validasi Responsif Layanan
        this.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                if (getWidth() < 850) {
                    pnlCards.setLayout(new GridLayout(2, 2, 15, 15));
                    pnlCards.setMaximumSize(new Dimension(Integer.MAX_VALUE, 250));
                } else {
                    pnlCards.setLayout(new GridLayout(1, 4, 15, 0));
                    pnlCards.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
                }
                pnlCards.revalidate();
            }
        });
    }

    private JLabel createStatCard(JPanel parent, String title, String value, Color accentColor) {
        JPanel card = UIKit.glassCard();
        card.setLayout(new BorderLayout(0, 5));
        card.setBorder(BorderFactory.createEmptyBorder(15, 18, 15, 18));

        JLabel lblTitle = new JLabel(title.toUpperCase());
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblTitle.setForeground(UIKit.fgMuted());

        JLabel lblVal = new JLabel(value);
        lblVal.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblVal.setForeground(accentColor);

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(lblVal, BorderLayout.CENTER);
        parent.add(card);

        return lblVal;
    }

    @Override
    public void refresh() {
        User u = AuthController.getCurrentUser();
        String username = (u != null) ? u.getUsername() : "";

        // Bersihkan tabel sebelum dimuat ulang
        modelTerbaru.setRowCount(0);

        if (AuthController.isAdmin()) {
            // === LOGIKA LIVE DATA ADMIN ===
            double pendapatan = TransaksiController.getTotalPenghasilan();
            int jmlSewa = TransaksiController.countSewa();
            int jmlJasa = TransaksiController.countJasa();
            int totalPelanggan = CustomerController.getAllCustomers().size();
            
            int jmlPending = (int) TransaksiController.getAll().stream()
                    .filter(t -> "PENDING".equalsIgnoreCase(t.getStatus()))
                    .count();

            lblCard1.setText("Rp " + String.format("%,.0f", pendapatan));
            lblCard2.setText(jmlPending + " Order");
            lblCard3.setText(jmlSewa + " Unit");
            lblCard4.setText(totalPelanggan + " User");

            // --- PERBAIKAN DI SINI: MENAMPILKAN KESELURUHAN TRANSAKSI ADMIN ---
            List<Transaksi> semuaTrx = TransaksiController.getAll();
            // Looping dibalik dari index terakhir agar transaksi paling baru tetap berada di atas tabel
            for (int i = semuaTrx.size() - 1; i >= 0; i--) {
                Transaksi t = semuaTrx.get(i);
                if (t == null) continue;
                
                String pelanggan = (t.getCustomer() != null) ? t.getCustomer().getNamaLengkap() : "Guest";
                String layanan = (t.getLayanan() != null) ? t.getLayanan().getNamaLayanan() : "-";
                
                modelTerbaru.addRow(new Object[]{
                    t.getIdTransaksi(), pelanggan, t.getJenisLayanan(), layanan, t.getStatus()
                });
            }
        } else {
            // === LOGIKA LIVE DATA CUSTOMER ===
            List<Transaksi> dataSaya = TransaksiController.getByCustomer(username);
            double totalPengeluaran = TransaksiController.getTotalByCustomer(username);
            
            long cntSewa = dataSaya.stream().filter(t -> "SEWA".equalsIgnoreCase(t.getJenisLayanan())).count();
            long cntJasa = dataSaya.stream().filter(t -> "JASA".equalsIgnoreCase(t.getJenisLayanan())).count();

            lblCard1.setText("Rp " + String.format("%,.0f", totalPengeluaran));
            lblCard2.setText(cntSewa + " Gear");
            lblCard3.setText(cntJasa + " Sesi");
            lblCard4.setText((totalPengeluaran > 1_500_000) ? "GOLD MEMBER" : "REGULAR");

            // Isi Tabel Customer: Semua transaksi milik user yang sedang login
            for (int i = dataSaya.size() - 1; i >= 0; i--) {
                Transaksi t = dataSaya.get(i);
                String deskripsi = (t.getLayanan() != null) ? t.getLayanan().getDeskripsi() : "-";
                double biaya = (t.getLayanan() != null) ? t.getLayanan().hitungBiaya() : 0;
                
                modelTerbaru.addRow(new Object[]{
                    t.getIdTransaksi(),
                    t.getJenisLayanan(),
                    deskripsi,
                    "Rp " + String.format("%,.0f", biaya),
                    t.getStatus()
                });
            }
        }

        if (tabelTerbaru != null) {
            UIKit.styleTable(tabelTerbaru);
            tabelTerbaru.repaint();
        }
        this.revalidate();
        this.repaint();
    }
}