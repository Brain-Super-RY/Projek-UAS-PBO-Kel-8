package studiokita.view;

import studiokita.UIKit;
import studiokita.controller.SewaController;
import studiokita.controller.AuthController;
import studiokita.model.SewaAlat;
import studiokita.model.Transaksi;
import studiokita.model.User;
import studiokita.model.Customer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 * SewaPanel — Premium Custom Edition (Glassmorphism & Auto-Calc).
 */
public class SewaPanel extends JPanel implements MainFrame.Refreshable {

    private ArrayList<Transaksi> daftarSewa = new ArrayList<>();
    private JTextField txtNamaCust, txtUserCust, txtTelp, txtNamaAlat, txtMulai, txtKembali;
    private JComboBox<String> cmbJenis;
    private JLabel lblEstimasi;
    private JTable tabel;
    private DefaultTableModel model;
    private JPanel center, pnlForm;

    public SewaPanel() {
        initComponents();
        refresh();
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 0));
        setOpaque(false);

        add(UIKit.topBar("🎒", "MANAJEMEN SEWA ALAT", "Penyewaan gear fotografi profesional", UIKit.ORANGE), BorderLayout.NORTH);

        center = new JPanel(new BorderLayout(30, 0));
        center.setOpaque(false);

        center.add(buildForm(), BorderLayout.WEST);
        center.add(buildTableArea(), BorderLayout.CENTER);

        add(center, BorderLayout.CENTER);
        
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override public void componentResized(java.awt.event.ComponentEvent e) {
                if (getWidth() < 950) {
                    ((BorderLayout)center.getLayout()).setHgap(0);
                    center.add(buildForm(), BorderLayout.NORTH);
                } else {
                    ((BorderLayout)center.getLayout()).setHgap(30);
                    center.add(buildForm(), BorderLayout.WEST);
                }
                center.revalidate();
            }
        });
    }

    private JPanel buildForm() {
        if (pnlForm != null) return pnlForm;
        pnlForm = UIKit.glassCard();
        pnlForm.setLayout(new BoxLayout(pnlForm, BoxLayout.Y_AXIS));
        pnlForm.setPreferredSize(new Dimension(350, 0));

        JLabel title = new JLabel("DATA PENYEWAAN");
        title.setFont(UIKit.FONT_H3);
        title.setForeground(UIKit.ORANGE);
        pnlForm.add(title); pnlForm.add(UIKit.gap(20));

        txtNamaCust = addLabeledField(pnlForm, "Pelanggan", "Nama Lengkap");
        txtUserCust = addLabeledField(pnlForm, "Username ID", "ID Login");
        txtTelp     = addLabeledField(pnlForm, "WhatsApp", "628...");
        
        // Auto-fill if customer
        if (!AuthController.isAdmin()) {
            User cur = AuthController.getCurrentUser();
            txtNamaCust.setText(cur.getNamaLengkap());
            txtUserCust.setText(cur.getUsername());
            txtNamaCust.setEditable(false);
            txtUserCust.setEditable(false);
            if (cur instanceof Customer c) {
                txtTelp.setText(c.getNoTelepon());
                txtTelp.setEditable(false);
            }
        }
        
        addLabel(pnlForm, "Pilih Kamera/Lensa");
        cmbJenis = new JComboBox<>(SewaController.JENIS_ALAT);
        cmbJenis.setBackground(new Color(255, 255, 255, 30));
        cmbJenis.setOpaque(false);
        cmbJenis.addActionListener(e -> updateEstimasi());
        pnlForm.add(cmbJenis); pnlForm.add(UIKit.gap(15));

        txtNamaAlat = addLabeledField(pnlForm, "Seri Alat", "Sony/Canon/Nikon...");
        txtMulai    = addLabeledField(pnlForm, "Mulai (yyyy-MM-dd)", LocalDate.now().toString());
        txtKembali  = addLabeledField(pnlForm, "Kembali (yyyy-MM-dd)", LocalDate.now().plusDays(1).toString());
        
        txtMulai.addActionListener(e -> updateEstimasi());
        txtKembali.addActionListener(e -> updateEstimasi());
        txtMulai.addFocusListener(new java.awt.event.FocusAdapter() { @Override public void focusLost(java.awt.event.FocusEvent e) { updateEstimasi(); } });
        txtKembali.addFocusListener(new java.awt.event.FocusAdapter() { @Override public void focusLost(java.awt.event.FocusEvent e) { updateEstimasi(); } });

        pnlForm.add(UIKit.gap(10));
        JSeparator s = new JSeparator();
        pnlForm.add(s); pnlForm.add(UIKit.gap(15));

        lblEstimasi = new JLabel("ESTIMASI: RP 0");
        lblEstimasi.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblEstimasi.setForeground(UIKit.ACCENT);
        pnlForm.add(lblEstimasi); pnlForm.add(UIKit.gap(20));

        JButton btnSimpan = UIKit.btn("KONFIRMASI SEWA", UIKit.ORANGE);
        btnSimpan.addActionListener(e -> simpan());
        pnlForm.add(btnSimpan);

        pnlForm.add(Box.createVerticalGlue());
        return pnlForm;
    }

    private JPanel buildTableArea() {
        JPanel p = UIKit.glassCard();
        p.setLayout(new BorderLayout(0, 15));

        String[] kol = {"ID", "PELANGGAN", "UNIT", "MULAI", "KEMBALI", "STATUS", "BIAYA"};
        model = new DefaultTableModel(kol, 0) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        tabel = new JTable(model);
        UIKit.styleTable(tabel);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnRow.setOpaque(false);
        
        JButton btnKembali = UIKit.btn("TERIMA GEAR", UIKit.GREEN);
        btnKembali.addActionListener(e -> kembalikan());
        
        JButton btnHapus = UIKit.btn("HAPUS", UIKit.RED);
        btnHapus.addActionListener(e -> hapus());
        
        if (AuthController.isAdmin()) {
            btnRow.add(btnKembali);
            btnRow.add(btnHapus);
        }

        JScrollPane sp = new JScrollPane(tabel);
        sp.setOpaque(false); sp.getViewport().setOpaque(false);
        sp.setBorder(BorderFactory.createEmptyBorder());

        p.add(sp, BorderLayout.CENTER);
        p.add(btnRow, BorderLayout.SOUTH);
        return p;
    }

    private void simpan() {
        String res = SewaController.simpanSewa(
                txtNamaCust.getText(), txtUserCust.getText(), txtTelp.getText(),
                cmbJenis.getSelectedIndex(), txtNamaAlat.getText(),
                txtMulai.getText(), txtKembali.getText());
        
        if (res.startsWith("OK")) {
            JOptionPane.showMessageDialog(this, "Data sewa berhasil disimpan!");
            refresh(); resetForm();
        } else JOptionPane.showMessageDialog(this, res, "Gagal", 0);
    }

    private void kembalikan() {
        int r = tabel.getSelectedRow();
        if (r < 0) return;
        String id = (String) model.getValueAt(r, 0);
        String tgl = JOptionPane.showInputDialog(this, "Tgl Dikembalikan (yyyy-MM-dd):", LocalDate.now().toString());
        if (tgl != null && !tgl.isBlank()) {
            String res = SewaController.prosesKembalikan(id, tgl);
            if (res.startsWith("OK")) {
                String[] p = res.split("\\|");
                JOptionPane.showMessageDialog(this, "Gear Diterima!\nTerlambat: " + p[1] + " hari\nDenda: Rp " + String.format("%,.0f", Double.parseDouble(p[2])));
                refresh();
            } else JOptionPane.showMessageDialog(this, res, "Error", 0);
        }
    }

    private void hapus() {
        int r = tabel.getSelectedRow();
        if (r < 0) return;
        String id = (String) model.getValueAt(r, 0);
        if (JOptionPane.showConfirmDialog(this, "Hapus transaksi " + id + "?", "Konfirmasi", 0) == 0) {
            if (SewaController.hapus(id)) refresh();
        }
    }

    private void resetForm() {
        if (AuthController.isAdmin()) {
            txtNamaCust.setText(""); txtUserCust.setText(""); txtTelp.setText("");
        }
        txtNamaAlat.setText(""); updateEstimasi();
    }

    private void updateEstimasi() {
        try {
            double est = SewaController.hitungEstimasi(cmbJenis.getSelectedIndex(), txtMulai.getText(), txtKembali.getText());
            lblEstimasi.setText("ESTIMASI: RP " + String.format("%,.0f", est));
        } catch (Exception e) { lblEstimasi.setText("ESTIMASI: RP 0"); }
    }

    @Override public void refresh() {
        if (AuthController.isAdmin()) {
            daftarSewa = new ArrayList<>(SewaController.getAllSewa());
        } else {
            daftarSewa = new ArrayList<>(SewaController.getSewaByCustomer(AuthController.getCurrentUser().getUsername()));
        }
        model.setRowCount(0);
        for (Transaksi t : daftarSewa) {
            SewaAlat s = (SewaAlat) t.getLayanan();
            String st = s.getTglDikembalikan() != null ? "KEMBALI" : "DISEWA";
            model.addRow(new Object[]{s.getIdLayanan(), t.getCustomer().getNamaLengkap(), s.getNamaKamera(), 
                s.getTglMulai(), s.getTglKembali(), st, "Rp "+String.format("%,.0f", s.hitungBiaya())});
        }
    }

    private JTextField addLabeledField(JPanel p, String lbl, String ph) {
        addLabel(p, lbl);
        JTextField f = UIKit.field();
        f.setText(ph);
        f.setMaximumSize(UIKit.maxField());
        p.add(f); p.add(UIKit.gap(12));
        return f;
    }
    private void addLabel(JPanel p, String text) {
        JLabel l = new JLabel(text);
        l.setFont(UIKit.FONT_SMALL);
        l.setForeground(new Color(150, 150, 160));
        p.add(l); p.add(UIKit.gap(4));
    }
}
