package studiokita.view;

import studiokita.UIKit;
import studiokita.controller.CustomerController;
import studiokita.model.Customer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * CustomerPanel — Premium Custom Edition (Glassmorphism & Responsive).
 * Manajemen data pelanggan khusus untuk role ADMIN.
 * Fiksasi Total: Sinkronisasi Warna Kompabilitas & Desain Kaca.
 */
public class CustomerPanel extends JPanel implements MainFrame.Refreshable {

    private JTextField txtNama, txtUser, txtEmail, txtTelp, txtAlamat;
    private JPasswordField txtPass;
    private JTable tabel;
    private DefaultTableModel model;
    private JPanel center, pnlForm, pnlTable;

    public CustomerPanel() {
        initComponents();
        refresh();
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 0));
        setOpaque(false);

        // FIX BUG: Mengganti UIKit.PURPLE menjadi UIKit.BLUE yang valid
        add(UIKit.topBar("DATABASE PELANGGAN", "Manajemen data member dan pendaftaran manual Studio Kita", UIKit.BLUE), BorderLayout.NORTH);

        center = new JPanel(new BorderLayout(30, 20));
        center.setOpaque(false);
        center.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        pnlForm = buildForm();
        pnlTable = buildTableArea();

        // Layout Default (Fullscreen/Lebar)
        center.add(pnlForm, BorderLayout.WEST);
        center.add(pnlTable, BorderLayout.CENTER);

        add(center, BorderLayout.CENTER);

        // -----------------------------------------------------------
        // FITUR AUTO-RESIZE RESPONSIVE
        // -----------------------------------------------------------
        this.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                center.removeAll();
                if (getWidth() < 950) { 
                    // Mode Windowed (Sempit): Form pindah ke Atas, Tabel di Bawah
                    center.add(pnlForm, BorderLayout.NORTH);
                    center.add(pnlTable, BorderLayout.CENTER);
                } else { 
                    // Mode Fullscreen (Lebar): Form di Kiri, Tabel di Kanan
                    center.add(pnlForm, BorderLayout.WEST);
                    center.add(pnlTable, BorderLayout.CENTER);
                }
                center.revalidate();
                center.repaint();
            }
        });
    }

    private JPanel buildForm() {
        if (pnlForm != null) return pnlForm;
        pnlForm = UIKit.glassCard();
        pnlForm.setLayout(new BoxLayout(pnlForm, BoxLayout.Y_AXIS));
        pnlForm.setPreferredSize(new Dimension(340, 0));

        JLabel title = new JLabel("TAMBAH PELANGGAN");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(UIKit.BLUE); // FIX BUG: Dialihkan ke warna BLUE
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlForm.add(title); 
        pnlForm.add(UIKit.gap(20));

        txtNama = addLabeledField(pnlForm, "NAMA LENGKAP", "");
        txtUser = addLabeledField(pnlForm, "USERNAME", "");
        
        // Input Password Khusus dengan Visual Premium Menyesuaikan Gaya Glassmorphism
        addLabel(pnlForm, "PASSWORD");
        txtPass = new JPasswordField();
        txtPass.setFont(UIKit.FONT_REGULAR);
        txtPass.setBackground(new Color(0, 0, 0, 80));
        txtPass.setForeground(UIKit.fgPrimary());
        txtPass.setCaretColor(UIKit.fgPrimary());
        txtPass.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(212, 160, 23, 40), 1, true),
            BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));
        txtPass.setMaximumSize(UIKit.maxField());
        txtPass.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlForm.add(txtPass); 
        pnlForm.add(UIKit.gap(12));

        txtEmail = addLabeledField(pnlForm, "EMAIL", "");
        txtTelp = addLabeledField(pnlForm, "NO WHATSAPP / TELEPON", "");
        txtAlamat = addLabeledField(pnlForm, "ALAMAT LENGKAP", "");

        pnlForm.add(UIKit.gap(10));

        // FIX BUG: Mengganti tombol aksi menggunakan token warna BLUE
        JButton btnSimpan = UIKit.btn("SIMPAN DATA", UIKit.BLUE);
        btnSimpan.setMaximumSize(UIKit.maxField());
        btnSimpan.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnSimpan.addActionListener(e -> simpan());
        pnlForm.add(btnSimpan);
        
        pnlForm.add(Box.createVerticalGlue());

        return pnlForm;
    }

    private JPanel buildTableArea() {
        if (pnlTable != null) return pnlTable;
        pnlTable = UIKit.glassCard();
        pnlTable.setLayout(new BorderLayout(0, 15));
        
        String[] col = {"USERNAME", "NAMA LENGKAP", "EMAIL", "TELEPON"};
        model = new DefaultTableModel(col, 0) { 
            @Override public boolean isCellEditable(int r, int c) { return false; } 
        };
        tabel = new JTable(model);
        UIKit.styleTable(tabel);
        
        // Menyesuaikan proporsi lebar kolom tabel
        tabel.getColumnModel().getColumn(0).setPreferredWidth(100);
        tabel.getColumnModel().getColumn(1).setPreferredWidth(150);
        tabel.getColumnModel().getColumn(2).setPreferredWidth(150);
        tabel.getColumnModel().getColumn(3).setPreferredWidth(100);

        JScrollPane sp = new JScrollPane(tabel);
        sp.setOpaque(false); 
        sp.getViewport().setOpaque(false);
        sp.setBorder(BorderFactory.createEmptyBorder());
        
        pnlTable.add(sp, BorderLayout.CENTER);

        // Tombol Aksi khusus Administrator di bagian bawah tabel
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnRow.setOpaque(false);
        
        JButton btnHapus = UIKit.btn("HAPUS CUSTOMER", UIKit.RED);
        btnHapus.addActionListener(e -> hapusCustomer());
        
        btnRow.add(btnHapus);
        pnlTable.add(btnRow, BorderLayout.SOUTH);

        return pnlTable;
    }

    private void simpan() {
        String nama = txtNama.getText();
        String user = txtUser.getText();
        String pass = new String(txtPass.getPassword());
        String email = txtEmail.getText();
        String telp = txtTelp.getText();
        String alamat = txtAlamat.getText();

        if (nama.isEmpty() || user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama, Username, dan Password wajib diisi!", "Validasi Gagal", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String hasil = CustomerController.tambahCustomer(nama, user, pass, email, telp, alamat);
        
        if ("OK".equals(hasil)) {
            JOptionPane.showMessageDialog(this, "Data Pelanggan berhasil ditambahkan!");
            // Mengosongkan form input
            txtNama.setText(""); txtUser.setText(""); txtPass.setText("");
            txtEmail.setText(""); txtTelp.setText(""); txtAlamat.setText("");
            refresh();
        } else {
            JOptionPane.showMessageDialog(this, hasil, "Gagal", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void hapusCustomer() {
        int row = tabel.getSelectedRow();
        if (row >= 0) {
            String usernameCust = tabel.getValueAt(row, 0).toString();
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Yakin ingin menghapus customer @" + usernameCust + " beserta seluruh riwayatnya?", 
                "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                
            if (confirm == JOptionPane.YES_OPTION) {
                String hasil = CustomerController.deleteCustomer(usernameCust);
                if ("OK".equals(hasil)) {
                    JOptionPane.showMessageDialog(this, "Customer berhasil dihapus.");
                    refresh();
                } else {
                    JOptionPane.showMessageDialog(this, hasil, "Gagal Menghapus", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Pilih data customer di tabel terlebih dahulu!");
        }
    }

    private JTextField addLabeledField(JPanel p, String lbl, String ph) {
        addLabel(p, lbl);
        JTextField f = UIKit.field();
        f.setText(ph);
        f.setMaximumSize(UIKit.maxField());
        f.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(f); 
        p.add(UIKit.gap(12));
        return f;
    }

    private void addLabel(JPanel p, String text) {
        JLabel l = new JLabel(text.toUpperCase());
        l.setFont(UIKit.FONT_SMALL);
        l.setForeground(UIKit.fgMuted());
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        l.setMaximumSize(new Dimension(Integer.MAX_VALUE, l.getPreferredSize().height));
        p.add(l); 
        p.add(UIKit.gap(4));
    }

    @Override
    public void refresh() {
        try {
            List<Customer> list = CustomerController.getAllCustomers();
            model.setRowCount(0);
            if (list != null) {
                for (Customer c : list) {
                    if (c == null) continue;
                    model.addRow(new Object[]{
                        c.getUsername(), 
                        c.getNamaLengkap(), 
                        (c.getEmail() == null || c.getEmail().isEmpty()) ? "-" : c.getEmail(), 
                        c.getNoTelepon()
                    });
                }
            }
        } catch (Exception e) {
            model.setRowCount(0);
        }
    }
}