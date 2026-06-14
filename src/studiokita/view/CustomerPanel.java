package studiokita.view;

import studiokita.UIKit;
import studiokita.controller.CustomerController;
import studiokita.model.Customer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

/**
 * CustomerPanel — Premium Custom Edition (Glassmorphism).
 */
public class CustomerPanel extends JPanel implements MainFrame.Refreshable {

    private JTextField txtNama, txtUser, txtPass, txtEmail, txtTelp, txtAlamat;
    private JTable tabel;
    private DefaultTableModel model;
    private ArrayList<Customer> daftarCust = new ArrayList<>();

    public CustomerPanel() {
        initComponents();
        refresh();
    }

    private JPanel center;
    private void initComponents() {
        setLayout(new BorderLayout(0, 0));
        setOpaque(false);

        add(UIKit.topBar("👥", "DATABASE PELANGGAN", "Manajemen data member Studio Kita", UIKit.PURPLE), BorderLayout.NORTH);

        center = new JPanel(new BorderLayout(30, 0));
        center.setOpaque(false);

        center.add(buildForm(), BorderLayout.WEST);
        center.add(buildTableArea(), BorderLayout.CENTER);

        add(center, BorderLayout.CENTER);
        
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override public void componentResized(java.awt.event.ComponentEvent e) {
                if (getWidth() < 900) {
                    ((BorderLayout)center.getLayout()).setHgap(0);
                    center.add(buildForm(), BorderLayout.NORTH);
                } else {
                    ((BorderLayout)center.getLayout()).setHgap(30);
                    center.add(buildForm(), BorderLayout.WEST);
                }
                center.revalidate(); center.repaint();
            }
        });
    }

    private JPanel pnlForm;
    private JPanel buildForm() {
        if (pnlForm != null) return pnlForm;
        pnlForm = UIKit.glassCard();
        pnlForm.setLayout(new BoxLayout(pnlForm, BoxLayout.Y_AXIS));
        pnlForm.setPreferredSize(new Dimension(320, 0));

        JLabel title = new JLabel("FORM PELANGGAN");
        title.setFont(UIKit.FONT_H3);
        title.setForeground(UIKit.PURPLE);
        pnlForm.add(title); pnlForm.add(UIKit.gap(20));

        txtNama   = addLabeledField(pnlForm, "Nama Lengkap", "");
        txtUser   = addLabeledField(pnlForm, "Username ID", "");
        txtPass   = addLabeledField(pnlForm, "Password", "");
        txtEmail  = addLabeledField(pnlForm, "Email", "");
        txtTelp   = addLabeledField(pnlForm, "Telepon", "");
        txtAlamat = addLabeledField(pnlForm, "Alamat", "");

        pnlForm.add(UIKit.gap(10));
        JButton btnSimpan = UIKit.btn("SIMPAN DATA", UIKit.PURPLE);
        btnSimpan.addActionListener(e -> simpan());
        pnlForm.add(btnSimpan);

        pnlForm.add(Box.createVerticalGlue());
        return pnlForm;
    }

    private JPanel buildTableArea() {
        JPanel p = UIKit.glassCard();
        p.setLayout(new BorderLayout(0, 15));

        String[] kol = {"USERNAME", "NAMA LENGKAP", "EMAIL", "TELEPON", "ALAMAT"};
        model = new DefaultTableModel(kol, 0) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        tabel = new JTable(model);
        UIKit.styleTable(tabel);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnRow.setOpaque(false);
        JButton btnHapus = UIKit.btn("HAPUS", UIKit.RED);
        btnHapus.addActionListener(e -> hapus());
        btnRow.add(btnHapus);

        JScrollPane sp = new JScrollPane(tabel);
        UIKit.styleScroll(sp);

        p.add(sp, BorderLayout.CENTER);
        p.add(btnRow, BorderLayout.SOUTH);
        return p;
    }

    private void simpan() {
        Customer c = new Customer(txtUser.getText(), txtPass.getText(), txtNama.getText(), 
                txtEmail.getText(), txtTelp.getText(), txtAlamat.getText());
        String res = CustomerController.simpanCustomer(c);
        if (res.startsWith("OK")) {
            JOptionPane.showMessageDialog(this, "Data pelanggan berhasil diperbarui!");
            refresh(); resetForm();
        } else JOptionPane.showMessageDialog(this, res, "Gagal", 0);
    }

    private void hapus() {
        int r = tabel.getSelectedRow();
        if (r < 0) return;
        String user = (String) model.getValueAt(r, 0);
        if (JOptionPane.showConfirmDialog(this, "Hapus pelanggan " + user + "?", "Konfirmasi", 0) == 0) {
            CustomerController.deleteCustomer(user);
            refresh();
        }
    }

    private void resetForm() {
        txtNama.setText(""); txtUser.setText(""); txtPass.setText("");
        txtEmail.setText(""); txtTelp.setText(""); txtAlamat.setText("");
    }

    @Override public void refresh() {
        daftarCust = new ArrayList<>(CustomerController.getAllCustomers());
        model.setRowCount(0);
        for (Customer c : daftarCust) {
            model.addRow(new Object[]{c.getUsername(), c.getNamaLengkap(), c.getEmail(), c.getNoTelepon(), c.getAlamat()});
        }
    }

    private JTextField addLabeledField(JPanel p, String lbl, String ph) {
        JLabel l = new JLabel(lbl);
        l.setFont(UIKit.FONT_SMALL);
        l.setForeground(UIKit.fgSecondary());
        p.add(l); p.add(UIKit.gap(4));
        JTextField f = UIKit.field();
        f.setText(ph);
        f.setMaximumSize(UIKit.maxField());
        p.add(f); p.add(UIKit.gap(12));
        return f;
    }
}
