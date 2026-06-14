package studiokita.view;

import studiokita.ThemeManager;
import studiokita.UIKit;
import studiokita.controller.AuthController;
import studiokita.controller.TransaksiController;
import studiokita.model.Customer;
import studiokita.model.JasaFoto;
import studiokita.model.SewaAlat;
import studiokita.model.Transaksi;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * ProfilPanel — Premium Custom Edition (Glassmorphism & Responsive Layout).
 * Menggunakan label teks murni untuk backwards compatibility kompilator Java.
 */
public class ProfilPanel extends JPanel implements MainFrame.Refreshable {

    private JLabel lblNama, lblUser, lblEmail, lblTelp, lblAlamat, lblTotTrx, lblTotBiaya;
    private JLabel tSewa, tJasa; 
    private JTable tabelSewa, tabelJasa;
    private DefaultTableModel modelSewa, modelJasa;
    private JPanel center, pnlKiri, pnlKanan;

    public ProfilPanel() {
        initComponents();
        refresh();
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 0));
        setOpaque(false);

        add(UIKit.topBar("PROFIL SAYA", "Informasi akun dan riwayat pesanan studio kamu", UIKit.currentGold()), BorderLayout.NORTH);

        center = new JPanel(new BorderLayout(30, 20));
        center.setOpaque(false);
        center.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        pnlKiri = buildProfileCard();
        pnlKanan = buildHistoryArea();

        center.add(pnlKiri, BorderLayout.WEST);
        center.add(pnlKanan, BorderLayout.CENTER);

        add(center, BorderLayout.CENTER);

        // FITUR AUTO-RESIZE RESPONSIVE LAYOUT
        this.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                if (center == null || pnlKiri == null || pnlKanan == null) return;
                center.removeAll();
                if (getWidth() < 950) { 
                    center.add(pnlKiri, BorderLayout.NORTH);
                    center.add(pnlKanan, BorderLayout.CENTER);
                } else { 
                    center.add(pnlKiri, BorderLayout.WEST);
                    center.add(pnlKanan, BorderLayout.CENTER);
                }
                center.revalidate();
                center.repaint();
            }
        });
    }

    private JPanel buildProfileCard() {
        JPanel p = UIKit.glassCard();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setPreferredSize(new Dimension(350, 0));
        p.setBorder(BorderFactory.createEmptyBorder(25, 20, 25, 20));

        // Mengganti emoji avatar orang menjadi teks inisial placeholder formal [MEMBER]
        JLabel avatar = new JLabel("[ MEMBER ]", SwingConstants.CENTER);
        avatar.setFont(new Font("Segoe UI", Font.BOLD, 24));
        avatar.setForeground(UIKit.currentGold());
        avatar.setAlignmentX(CENTER_ALIGNMENT);
        p.add(avatar); p.add(UIKit.gap(20));

        lblNama = new JLabel("Nama Lengkap", SwingConstants.CENTER);
        lblNama.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblNama.setForeground(UIKit.fgPrimary()); 
        lblNama.setAlignmentX(CENTER_ALIGNMENT);
        p.add(lblNama);

        lblUser = new JLabel("@username", SwingConstants.CENTER);
        lblUser.setFont(UIKit.FONT_REGULAR);
        lblUser.setForeground(UIKit.currentGold()); 
        lblUser.setAlignmentX(CENTER_ALIGNMENT);
        p.add(lblUser); p.add(UIKit.gap(25));

        lblEmail = new JLabel("-");
        lblTelp = new JLabel("-");
        lblAlamat = new JLabel("-");
        
        // Mengganti emoji ikon menjadi Teks Label Deskriptif yang aman dicompile
        p.add(infoRow("Email :", lblEmail)); p.add(UIKit.gap(5));
        p.add(infoRow("No. Telp :", lblTelp)); p.add(UIKit.gap(5));
        p.add(infoRow("Alamat :", lblAlamat));

        p.add(UIKit.gap(20));
        JSeparator s = new JSeparator();
        s.setBackground(ThemeManager.isDark() ? new Color(212, 160, 23, 40) : new Color(160, 115, 15, 40));
        s.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        p.add(s);
        p.add(UIKit.gap(20));

        lblTotTrx = new JLabel("Total Transaksi: 0");
        lblTotTrx.setFont(UIKit.FONT_BOLD);
        lblTotTrx.setForeground(UIKit.fgPrimary()); 
        
        lblTotBiaya = new JLabel("Total Pengeluaran: Rp 0");
        lblTotBiaya.setFont(UIKit.FONT_BOLD);
        lblTotBiaya.setForeground(UIKit.currentGold()); 

        lblTotTrx.setAlignmentX(LEFT_ALIGNMENT);
        lblTotBiaya.setAlignmentX(LEFT_ALIGNMENT);
        
        p.add(lblTotTrx); p.add(UIKit.gap(10));
        p.add(lblTotBiaya);

        p.add(Box.createVerticalGlue());
        return p;
    }

    private JPanel infoRow(String titleText, JLabel label) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        p.setOpaque(false);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JLabel title = new JLabel(titleText);
        title.setFont(UIKit.FONT_BOLD);
        title.setForeground(UIKit.currentGold());
        title.setPreferredSize(new Dimension(75, 20)); // Lebar tetap agar kolom text rapi lurus sejajar
        
        label.setFont(UIKit.FONT_REGULAR);
        label.setForeground(UIKit.fgPrimary()); 

        p.add(title); p.add(label);
        return p;
    }

    private JPanel buildHistoryArea() {
        if (pnlKanan != null) return pnlKanan;
        pnlKanan = new JPanel(new GridLayout(2, 1, 0, 20));
        pnlKanan.setOpaque(false);

        // Tabel Riwayat Sewa Alat
        JPanel pSewa = UIKit.glassCard();
        pSewa.setLayout(new BorderLayout());
        pSewa.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        tSewa = new JLabel("RIWAYAT SEWA ALAT GEAR");
        tSewa.setFont(UIKit.FONT_BOLD); 
        tSewa.setForeground(UIKit.currentGold()); 
        tSewa.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        pSewa.add(tSewa, BorderLayout.NORTH);

        String[] kSewa = {"ID TRX", "KAMERA", "TGL MULAI", "TGL KEMBALI", "STATUS", "BIAYA"};
        modelSewa = new DefaultTableModel(kSewa, 0) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        tabelSewa = new JTable(modelSewa);
        UIKit.styleTable(tabelSewa);
        
        JScrollPane scrollSewa = new JScrollPane(tabelSewa);
        scrollSewa.setOpaque(false);
        scrollSewa.getViewport().setOpaque(false);
        scrollSewa.setBorder(BorderFactory.createEmptyBorder());
        pSewa.add(scrollSewa, BorderLayout.CENTER);

        // Tabel Riwayat Booking Jasa Foto
        JPanel pJasa = UIKit.glassCard();
        pJasa.setLayout(new BorderLayout());
        pJasa.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        tJasa = new JLabel("RIWAYAT BOOKING JASA FOTO");
        tJasa.setFont(UIKit.FONT_BOLD); 
        tJasa.setForeground(UIKit.currentGold()); 
        tJasa.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        pJasa.add(tJasa, BorderLayout.NORTH);

        String[] kJasa = {"ID TRX", "PAKET", "FOTOGRAFER", "TGL SESI", "STATUS", "BIAYA"};
        modelJasa = new DefaultTableModel(kJasa, 0) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        tabelJasa = new JTable(modelJasa);
        UIKit.styleTable(tabelJasa);
        
        JScrollPane scrollJasa = new JScrollPane(tabelJasa);
        scrollJasa.setOpaque(false);
        scrollJasa.getViewport().setOpaque(false);
        scrollJasa.setBorder(BorderFactory.createEmptyBorder());
        pJasa.add(scrollJasa, BorderLayout.CENTER);

        pnlKanan.add(pSewa);
        pnlKanan.add(pJasa);
        return pnlKanan;
    }

    @Override 
    public void refresh() {
        if (lblNama != null) lblNama.setForeground(UIKit.fgPrimary());
        if (lblEmail != null) lblEmail.setForeground(UIKit.fgPrimary());
        if (lblTelp != null) lblTelp.setForeground(UIKit.fgPrimary());
        if (lblAlamat != null) lblAlamat.setForeground(UIKit.fgPrimary());
        if (lblTotTrx != null) lblTotTrx.setForeground(UIKit.fgPrimary());
        if (lblTotBiaya != null) lblTotBiaya.setForeground(UIKit.currentGold());
        if (tSewa != null) tSewa.setForeground(UIKit.currentGold());
        if (tJasa != null) tJasa.setForeground(UIKit.currentGold());
        
        if (tabelSewa != null) {
            UIKit.styleTable(tabelSewa);
            tabelSewa.repaint();
        }
        if (tabelJasa != null) {
            UIKit.styleTable(tabelJasa);
            tabelJasa.repaint();
        }

        if (!(AuthController.getCurrentUser() instanceof Customer cust)) return;

        lblNama.setText(cust.getNamaLengkap());
        lblUser.setText("@" + cust.getUsername());
        lblEmail.setText(cust.getEmail() != null && !cust.getEmail().isEmpty() ? cust.getEmail() : "Email belum diatur");
        lblTelp.setText(cust.getNoTelepon());
        lblAlamat.setText(cust.getAlamat());

        List<Transaksi> riwayat = TransaksiController.getByCustomer(cust.getUsername());
        modelSewa.setRowCount(0);
        modelJasa.setRowCount(0);
        
        double totalBiaya = 0;

        for (Transaksi t : riwayat) {
            if (t == null) continue;
            totalBiaya += t.getTotalBiaya();
            
            if (t.getLayanan() instanceof SewaAlat s) {
                String statusText = s.getTglDikembalikan() != null ? "SELESAI" : t.getStatus();
                modelSewa.addRow(new Object[]{
                    t.getIdTransaksi(), s.getNamaKamera(), s.getTglMulai(), s.getTglKembali(), 
                    statusText, "Rp " + String.format("%,.0f", s.hitungBiaya())
                });
            } 
            else if (t.getLayanan() instanceof JasaFoto j) {
                modelJasa.addRow(new Object[]{
                    t.getIdTransaksi(), j.getPaket(), j.getFotografer(), j.getTglSesi(), 
                    t.getStatus(), "Rp " + String.format("%,.0f", j.hitungBiaya())
                });
            }
        }

        lblTotTrx.setText("Total Transaksi: " + riwayat.size() + " Pesanan");
        lblTotBiaya.setText("Total Pengeluaran: Rp " + String.format("%,.0f", totalBiaya));
    }
}