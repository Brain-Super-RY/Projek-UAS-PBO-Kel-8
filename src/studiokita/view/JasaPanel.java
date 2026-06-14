package studiokita.view;

import studiokita.UIKit;
import studiokita.controller.JasaController;
import studiokita.controller.AuthController;
import studiokita.model.JasaFoto;
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
 * JasaPanel — Premium Custom Edition (Glassmorphism & Live Pricing).
 */
public class JasaPanel extends JPanel implements MainFrame.Refreshable {

    private ArrayList<Transaksi> daftarJasa = new ArrayList<>();
    private JTextField txtNamaCust, txtUserCust, txtTelp, txtFotografer, txtSesi, txtDurasi, txtFotoEdit;
    private JComboBox<String> cmbPaket;
    private JLabel lblTotal;
    private JTable tabel;
    private DefaultTableModel model;
    private JPanel center, pnlForm;

    public JasaPanel() {
        initComponents();
        refresh();
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 0));
        setOpaque(false);

        add(UIKit.topBar("📸", "MANAJEMEN JASA FOTO", "Booking sesi pemotretan profesional", UIKit.GREEN), BorderLayout.NORTH);

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

        JLabel title = new JLabel("BOOKING SESI FOTO");
        title.setFont(UIKit.FONT_H3);
        title.setForeground(UIKit.GREEN);
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
        
        addLabel(pnlForm, "Pilihan Paket");
        cmbPaket = new JComboBox<>(JasaController.PAKET_FOTO);
        cmbPaket.setBackground(new Color(255, 255, 255, 30));
        cmbPaket.setOpaque(false);
        cmbPaket.addActionListener(e -> updateEstimasi());
        pnlForm.add(cmbPaket); pnlForm.add(UIKit.gap(15));

        txtFotografer = addLabeledField(pnlForm, "Fotografer", "Nama Talent");
        txtSesi       = addLabeledField(pnlForm, "Tanggal (yyyy-MM-dd)", LocalDate.now().toString());
        txtDurasi     = addLabeledField(pnlForm, "Durasi (Jam)", "1");
        txtFotoEdit   = addLabeledField(pnlForm, "Jml Foto Edit", "5");
        
        txtDurasi.addActionListener(e -> updateEstimasi());
        txtFotoEdit.addActionListener(e -> updateEstimasi());
        txtDurasi.addFocusListener(new java.awt.event.FocusAdapter() { @Override public void focusLost(java.awt.event.FocusEvent e) { updateEstimasi(); } });
        txtFotoEdit.addFocusListener(new java.awt.event.FocusAdapter() { @Override public void focusLost(java.awt.event.FocusEvent e) { updateEstimasi(); } });

        pnlForm.add(UIKit.gap(10));
        JSeparator s = new JSeparator();
        pnlForm.add(s); pnlForm.add(UIKit.gap(15));

        lblTotal = new JLabel("TOTAL: RP 0");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTotal.setForeground(UIKit.ACCENT);
        pnlForm.add(lblTotal); pnlForm.add(UIKit.gap(20));

        JButton btnSimpan = UIKit.btn("KONFIRMASI BOOKING", UIKit.GREEN);
        btnSimpan.addActionListener(e -> simpan());
        pnlForm.add(btnSimpan);

        pnlForm.add(Box.createVerticalGlue());
        return pnlForm;
    }

    private JPanel buildTableArea() {
        JPanel p = UIKit.glassCard();
        p.setLayout(new BorderLayout(0, 15));

        String[] kol = {"ID", "PELANGGAN", "PAKET", "TGL SESI", "DURASI", "BIAYA"};
        model = new DefaultTableModel(kol, 0) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        tabel = new JTable(model);
        UIKit.styleTable(tabel);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnRow.setOpaque(false);
        JButton btnHapus = UIKit.btn("BATALKAN", UIKit.RED);
        btnHapus.addActionListener(e -> hapus());
        
        if (AuthController.isAdmin()) {
            btnRow.add(btnHapus);
        }

        JScrollPane sp = new JScrollPane(tabel);
        UIKit.styleScroll(sp);

        p.add(sp, BorderLayout.CENTER);
        p.add(btnRow, BorderLayout.SOUTH);
        return p;
    }

    private void simpan() {
        try {
            String res = JasaController.simpanJasa(
                    txtNamaCust.getText(), txtUserCust.getText(), txtTelp.getText(),
                    cmbPaket.getSelectedIndex(), txtFotografer.getText(), txtSesi.getText(),
                    Integer.parseInt(txtDurasi.getText()), Integer.parseInt(txtFotoEdit.getText()));
            
            if (res.startsWith("OK")) {
                JOptionPane.showMessageDialog(this, "Booking berhasil dicatat!");
                refresh(); resetForm();
            } else JOptionPane.showMessageDialog(this, res, "Gagal", 0);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Durasi dan Foto Edit harus angka!");
        }
    }

    private void hapus() {
        int r = tabel.getSelectedRow();
        if (r < 0) return;
        String id = (String) model.getValueAt(r, 0);
        if (JOptionPane.showConfirmDialog(this, "Batalkan booking " + id + "?", "Konfirmasi", 0) == 0) {
            if (JasaController.hapus(id)) refresh();
        }
    }

    private void resetForm() {
        if (AuthController.isAdmin()) {
            txtNamaCust.setText(""); txtUserCust.setText(""); txtTelp.setText("");
        }
        txtFotografer.setText(""); txtDurasi.setText("1"); txtFotoEdit.setText("5");
        updateEstimasi();
    }

    private void updateEstimasi() {
        try {
            double est = JasaController.hitungEstimasi(cmbPaket.getSelectedIndex(), 
                    Integer.parseInt(txtDurasi.getText()), Integer.parseInt(txtFotoEdit.getText()));
            lblTotal.setText("TOTAL: RP " + String.format("%,.0f", est));
        } catch (Exception e) { lblTotal.setText("TOTAL: RP 0"); }
    }

    @Override public void refresh() {
        if (AuthController.isAdmin()) {
            daftarJasa = new ArrayList<>(JasaController.getAllJasa());
        } else {
            daftarJasa = new ArrayList<>(JasaController.getJasaByCustomer(AuthController.getCurrentUser().getUsername()));
        }
        model.setRowCount(0);
        for (Transaksi t : daftarJasa) {
            JasaFoto j = (JasaFoto) t.getLayanan();
            model.addRow(new Object[]{j.getIdLayanan(), t.getCustomer().getNamaLengkap(), j.getPaket(), 
                j.getTglSesi(), j.getDurasiJam() + " Jam", "Rp "+String.format("%,.0f", j.hitungBiaya())});
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
