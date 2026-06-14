package studiokita.view;

import studiokita.ThemeManager;
import studiokita.UIKit;
import studiokita.controller.TransaksiController;
import studiokita.model.Transaksi;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * PenghasilanPanel — Premium Custom Edition (Glassmorphism & Responsive Report).
 * Panel khusus Admin untuk melihat ringkasan omzet studio dengan tema Luxury Gold.
 */
public class PenghasilanPanel extends JPanel implements MainFrame.Refreshable {

    private JLabel lblTotalOmzet, lblCountSewa, lblCountJasa, lblCountTotal, title, tTitle;
    private JTable tabel;
    private DefaultTableModel model;
    private JPanel center, pnlStat, pnlTable;

    public PenghasilanPanel() {
        initComponents();
        refresh();
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 0));
        setOpaque(false);

        // Header menggunakan token emas premium dinamis
        add(UIKit.topBar("LAPORAN PENGHASILAN", "Ringkasan finansial dan omzet studio secara keseluruhan", UIKit.currentGold()), BorderLayout.NORTH);

        center = new JPanel(new BorderLayout(30, 20));
        center.setOpaque(false);
        center.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        pnlStat = buildStatArea();
        pnlTable = buildTableArea();

        // Mode Default (Fullscreen)
        center.add(pnlStat, BorderLayout.WEST);
        center.add(pnlTable, BorderLayout.CENTER);

        add(center, BorderLayout.CENTER);

        // FITUR AUTO-RESIZE RESPONSIVE LAYOUT
        this.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                center.removeAll();
                if (getWidth() < 950) { 
                    // Mode Windowed: Kartu Statistik Pindah ke Atas
                    center.add(pnlStat, BorderLayout.NORTH);
                    center.add(pnlTable, BorderLayout.CENTER);
                } else { 
                    // Mode Fullscreen: Kartu Statistik Berada di Kiri
                    center.add(pnlStat, BorderLayout.WEST);
                    center.add(pnlTable, BorderLayout.CENTER);
                }
                center.revalidate();
                center.repaint();
            }
        });
    }

    private JPanel buildStatArea() {
        // Menggunakan panel transparan custom dengan background gradient emas mewah dinamis
        JPanel p = new UIKit.GlassPanel(25) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Ambil warna emas dinamis terkini untuk diolah menjadi gradient transparan
                Color goldColor = UIKit.currentGold();
                g2.setPaint(new GradientPaint(0, 0, new Color(goldColor.getRed(), goldColor.getGreen(), goldColor.getBlue(), 35), 
                                              0, getHeight(), new Color(255, 255, 255, 5)));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
                g2.dispose();
            }
        };
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setPreferredSize(new Dimension(380, 0));
        p.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        title = new JLabel("TOTAL PENDAPATAN");
        title.setFont(new Font("Segoe UI", Font.BOLD, 15));
        title.setForeground(UIKit.fgPrimary());
        title.setAlignmentX(CENTER_ALIGNMENT);
        p.add(title);
        p.add(UIKit.gap(10));

        // Label Total Omzet (FIX BUG: Menggunakan token emas terkini)
        lblTotalOmzet = new JLabel("Rp 0", SwingConstants.CENTER);
        lblTotalOmzet.setFont(new Font("Segoe UI", Font.BOLD, 42));
        lblTotalOmzet.setForeground(UIKit.currentGold());
        lblTotalOmzet.setAlignmentX(CENTER_ALIGNMENT);
        p.add(lblTotalOmzet);

        p.add(UIKit.gap(35));
        JSeparator sep = new JSeparator();
        sep.setBackground(ThemeManager.isDark() ? new Color(212, 160, 23, 40) : new Color(160, 115, 15, 40));
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        p.add(sep);
        p.add(UIKit.gap(25));

        // Rincian Statistik Finansial
        p.add(infoRow("Total Transaksi Sewa", lblCountSewa = new JLabel("0")));
        p.add(UIKit.gap(15));
        p.add(infoRow("Total Booking Jasa", lblCountJasa = new JLabel("0")));
        p.add(UIKit.gap(15));
        p.add(infoRow("Total Semua Transaksi", lblCountTotal = new JLabel("0")));

        p.add(Box.createVerticalGlue());
        return p;
    }

    private JPanel infoRow(String labelText, JLabel lblValue) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        JLabel l = new JLabel(labelText);
        l.setFont(UIKit.FONT_REGULAR);
        l.setForeground(UIKit.fgMuted()); // Menyesuaikan visibilitas kontras teks otomatis

        lblValue.setFont(UIKit.FONT_BOLD);
        lblValue.setForeground(UIKit.fgPrimary());

        p.add(l, BorderLayout.WEST);
        p.add(lblValue, BorderLayout.EAST);
        return p;
    }

    private JPanel buildTableArea() {
        if (pnlTable != null) return pnlTable;
        pnlTable = UIKit.glassCard();
        pnlTable.setLayout(new BorderLayout());

        tTitle = new JLabel("RIWAYAT PEMASUKAN");
        tTitle.setFont(UIKit.FONT_BOLD);
        tTitle.setForeground(UIKit.currentGold()); // FIX BUG: Menggunakan token emas terkini
        tTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        pnlTable.add(tTitle, BorderLayout.NORTH);

        String[] kol = {"ID TRX", "PELANGGAN", "LAYANAN", "STATUS", "TANGGAL", "NOMINAL"};
        model = new DefaultTableModel(kol, 0) { 
            @Override public boolean isCellEditable(int r, int c) { return false; } 
        };
        tabel = new JTable(model);
        UIKit.styleTable(tabel);
        
        JScrollPane sp = new JScrollPane(tabel);
        sp.setOpaque(false); 
        sp.getViewport().setOpaque(false);
        sp.setBorder(BorderFactory.createEmptyBorder());
        
        pnlTable.add(sp, BorderLayout.CENTER);
        return pnlTable;
    }

    @Override 
    public void refresh() {
        // Pembaruan properti komponen teks saat runtime ganti tema/refresh
        if (title != null) title.setForeground(UIKit.fgPrimary());
        if (tTitle != null) tTitle.setForeground(UIKit.currentGold());
        if (lblTotalOmzet != null) lblTotalOmzet.setForeground(UIKit.currentGold());
        
        if (tabel != null) {
            UIKit.styleTable(tabel);
            tabel.repaint();
        }

        List<Transaksi> list = TransaksiController.getAll();
        model.setRowCount(0);
        
        double totalOmzet = 0;
        int countSewa = 0;
        int countJasa = 0;

        for (Transaksi t : list) {
            if (t == null) continue;
            
            // Hanya hitung pemasukan jika statusnya BUKAN DECLINED
            if (!"DECLINED".equals(t.getStatus())) {
                totalOmzet += t.getTotalBiaya();
                
                if ("SEWA".equals(t.getJenisLayanan())) countSewa++;
                else if ("JASA".equals(t.getJenisLayanan())) countJasa++;

                String namaPelanggan = (t.getCustomer() != null) ? t.getCustomer().getNamaLengkap() : "Guest";

                model.addRow(new Object[]{
                    t.getIdTransaksi(), 
                    namaPelanggan, 
                    t.getJenisLayanan(), 
                    t.getStatus(), 
                    t.getTglInput(), 
                    "Rp " + String.format("%,.0f", t.getTotalBiaya())
                });
            }
        }

        // Update UI Angka Finansial & Counter Data
        if (lblTotalOmzet != null) lblTotalOmzet.setText("Rp " + String.format("%,.0f", totalOmzet));
        if (lblCountSewa != null) lblCountSewa.setText(String.valueOf(countSewa));
        if (lblCountJasa != null) lblCountJasa.setText(String.valueOf(countJasa));
        if (lblCountTotal != null) lblCountTotal.setText(String.valueOf(countSewa + countJasa));
    }
}