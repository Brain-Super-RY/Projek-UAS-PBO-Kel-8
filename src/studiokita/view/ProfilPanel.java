package studiokita.view;

import studiokita.UIKit;
import studiokita.ThemeManager;
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
 * ProfilPanel — Premium Custom Edition (Glassmorphism).
 */
public class ProfilPanel extends JPanel implements MainFrame.Refreshable {

    private JLabel lblNama, lblUser, lblEmail, lblTelp, lblAlamat, lblTotTrx, lblTotBiaya;
    private JTable tabelSewa, tabelJasa;
    private DefaultTableModel modelSewa, modelJasa;

    public ProfilPanel() {
        initComponents();
        refresh();
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 0));
        setOpaque(false);

        add(UIKit.topBar("👤", "PROFIL SAYA", "Informasi akun dan riwayat aktivitas", UIKit.BLUE), BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(0, 30));
        center.setOpaque(false);

        // Header: Profile & Stats
        JPanel top = new JPanel(new GridLayout(1, 2, 25, 0));
        top.setOpaque(false);
        top.add(buildProfileCard());
        top.add(buildStatsCard());
        center.add(top, BorderLayout.NORTH);

        // Tables
        JPanel historyArea = UIKit.glassCard();
        historyArea.setLayout(new BorderLayout());
        
        JTabbedPane tabs = new JTabbedPane();
        
        String[] colS = {"ID SEWA", "UNIT", "MULAI", "KEMBALI", "STATUS", "BIAYA"};
        modelSewa = new DefaultTableModel(colS, 0) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        tabelSewa = new JTable(modelSewa);
        UIKit.styleTable(tabelSewa);
        tabs.addTab("🎒 SEWA ALAT", new JScrollPane(tabelSewa));

        String[] colJ = {"ID JASA", "PAKET", "FOTOGRAFER", "TANGGAL", "DURASI", "BIAYA"};
        modelJasa = new DefaultTableModel(colJ, 0) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        tabelJasa = new JTable(modelJasa);
        UIKit.styleTable(tabelJasa);
        tabs.addTab("📸 JASA FOTO", new JScrollPane(tabelJasa));

        historyArea.add(tabs, BorderLayout.CENTER);
        center.add(historyArea, BorderLayout.CENTER);
        
        add(center, BorderLayout.CENTER);
    }

    private JPanel buildProfileCard() {
        JPanel c = UIKit.glassCard();
        c.setLayout(new BoxLayout(c, BoxLayout.Y_AXIS));
        
        JLabel title = new JLabel("DETAIL AKUN");
        title.setFont(UIKit.FONT_SMALL);
        title.setForeground(UIKit.BLUE);
        c.add(title); c.add(UIKit.gap(15));

        lblNama  = addInfoRow(c, "Nama Lengkap");
        lblUser  = addInfoRow(c, "Username");
        lblEmail = addInfoRow(c, "Email");
        lblTelp  = addInfoRow(c, "Telepon");
        lblAlamat = addInfoRow(c, "Alamat");

        return c;
    }

    private JPanel buildStatsCard() {
        JPanel c = UIKit.glassCard();
        c.setLayout(new BoxLayout(c, BoxLayout.Y_AXIS));
        
        JLabel title = new JLabel("RINGKASAN");
        title.setFont(UIKit.FONT_SMALL);
        title.setForeground(UIKit.GREEN);
        c.add(title); c.add(UIKit.gap(20));

        lblTotTrx = new JLabel("0 Pesanan");
        lblTotTrx.setFont(UIKit.FONT_H1);
        lblTotTrx.setForeground(UIKit.GREEN);
        
        lblTotBiaya = new JLabel("Rp 0");
        lblTotBiaya.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTotBiaya.setForeground(UIKit.ACCENT);

        c.add(new JLabel("Total Transaksi:"));
        c.add(lblTotTrx);
        c.add(UIKit.gap(20));
        c.add(new JLabel("Total Pengeluaran:"));
        c.add(lblTotBiaya);

        return c;
    }

    private JLabel addInfoRow(JPanel p, String label) {
        JPanel r = new JPanel(new BorderLayout());
        r.setOpaque(false);
        JLabel l = new JLabel(label + ": ");
        l.setFont(UIKit.FONT_BODY);
        l.setForeground(new Color(150, 150, 160));
        l.setPreferredSize(new Dimension(110, 25));
        
        JLabel v = new JLabel("-");
        v.setFont(UIKit.FONT_NORMAL);
        
        r.add(l, BorderLayout.WEST);
        r.add(v, BorderLayout.CENTER);
        p.add(r); p.add(UIKit.gap(8));
        return v;
    }

    @Override public void refresh() {
        if (!(AuthController.getCurrentUser() instanceof Customer cust)) return;

        lblNama.setText(cust.getNamaLengkap());
        lblUser.setText(cust.getUsername());
        lblEmail.setText(cust.getEmail());
        lblTelp.setText(cust.getNoTelepon());
        lblAlamat.setText(cust.getAlamat());

        List<Transaksi> riwayat = TransaksiController.getByCustomer(cust.getUsername());
        modelSewa.setRowCount(0);
        modelJasa.setRowCount(0);
        double total = 0;

        for (Transaksi t : riwayat) {
            total += t.getTotalBiaya();
            if (t.getLayanan() instanceof SewaAlat s) {
                String status = s.getTglDikembalikan() != null ? "SELESAI" : "DISEWA";
                modelSewa.addRow(new Object[]{s.getIdLayanan(), s.getNamaKamera(), s.getTglMulai(), s.getTglKembali(), status, "Rp "+String.format("%,.0f", s.hitungBiaya())});
            } else if (t.getLayanan() instanceof JasaFoto j) {
                modelJasa.addRow(new Object[]{j.getIdLayanan(), j.getPaket(), j.getFotografer(), j.getTglSesi(), j.getDurasiJam()+" Jam", "Rp "+String.format("%,.0f", j.hitungBiaya())});
            }
        }
        
        lblTotTrx.setText(riwayat.size() + " Pesanan");
        lblTotBiaya.setText("Rp " + String.format("%,.0f", total));
    }
}
