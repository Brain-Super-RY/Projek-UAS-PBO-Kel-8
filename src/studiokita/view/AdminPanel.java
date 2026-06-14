package studiokita.view;

import studiokita.UIKit;
import studiokita.model.Admin;
import studiokita.model.dao.DatabaseConnection;
import studiokita.model.dao.UserDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * AdminPanel — Premium Custom Edition (Glassmorphism & Responsive). Manajemen
 * hak akses dan pendaftaran staf internal Studio Kita.
 */
public class AdminPanel extends JPanel implements MainFrame.Refreshable {

    private JTextField txtNama, txtUser;
    private JPasswordField txtPass;
    private JComboBox<String> cmbLevel;

    private JTable tabel;
    private DefaultTableModel model;
    private JPanel center, pnlForm, pnlTable;

    public AdminPanel() {
        initComponents();
        refresh();
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 0));
        setOpaque(false);

        // Header Panel — EMOJI DAN TANDA KUTIP NYASAR SUDAH DIAPUS ✨
        add(UIKit.topBar("PANEL ADMINISTRATOR", "Manajemen hak akses, staf internal, dan kasir studio", UIKit.RED), BorderLayout.NORTH);

        center = new JPanel(new BorderLayout(30, 20));
        center.setOpaque(false);
        center.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        pnlForm = buildForm();
        pnlTable = buildTableArea();

        // Layout Default (Fullscreen)
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
        JPanel p = UIKit.glassCard();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setPreferredSize(new Dimension(320, 0));

        JLabel title = new JLabel("TAMBAH ADMIN BARU");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(UIKit.RED);
        p.add(title);
        p.add(UIKit.gap(20));

        txtNama = addLabeledField(p, "NAMA LENGKAP STAF", "");
        txtUser = addLabeledField(p, "USERNAME LOGIN", "");

        // Input Password Khusus (Dinamis mengikuti Tema Light/Dark)
        addLabel(p, "PASSWORD SEMENTARA");
        txtPass = new JPasswordField();
        txtPass.setFont(UIKit.FONT_REGULAR);
        txtPass.setForeground(UIKit.fgPrimary()); // Warna teks dinamis
        txtPass.setCaretColor(UIKit.fgPrimary()); // Warna kursor dinamis
        txtPass.setOpaque(false);
        txtPass.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 255, 255, 80), 1),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        txtPass.setMaximumSize(UIKit.maxField());
        p.add(txtPass);
        p.add(UIKit.gap(12));

        // Dropdown Level Admin
        addLabel(p, "LEVEL AKSES ADMIN");
        String[] levelOps = {"Super Admin", "Admin Studio", "Kasir / Frontdesk"};
        cmbLevel = new JComboBox<>(levelOps);
        cmbLevel.setMaximumSize(UIKit.maxField());
        p.add(cmbLevel);
        p.add(UIKit.gap(20));

        JButton btnSimpan = UIKit.btn("DAFTARKAN ADMIN", UIKit.RED);
        btnSimpan.setMaximumSize(UIKit.maxField());
        btnSimpan.addActionListener(e -> simpanData());
        p.add(btnSimpan);

        p.add(Box.createVerticalGlue());

        return p;
    }

    private JPanel buildTableArea() {
        JPanel p = UIKit.glassCard();
        p.setLayout(new BorderLayout(0, 15));

        String[] col = {"USERNAME", "NAMA LENGKAP", "LEVEL AKSES"};
        model = new DefaultTableModel(col, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        tabel = new JTable(model);
        UIKit.styleTable(tabel);

        // Sesuaikan lebar kolom
        tabel.getColumnModel().getColumn(0).setPreferredWidth(100);
        tabel.getColumnModel().getColumn(1).setPreferredWidth(200);
        tabel.getColumnModel().getColumn(2).setPreferredWidth(150);

        JScrollPane sp = new JScrollPane(tabel);
        sp.setOpaque(false);
        sp.getViewport().setOpaque(false);
        sp.setBorder(BorderFactory.createEmptyBorder());

        p.add(sp, BorderLayout.CENTER);

        // Tombol Hapus Admin
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnRow.setOpaque(false);

        JButton btnHapus = UIKit.btn("CABUT AKSES (HAPUS)", new Color(200, 50, 50));
        btnHapus.addActionListener(e -> hapusAdmin());

        btnRow.add(btnHapus);
        p.add(btnRow, BorderLayout.SOUTH);

        return p;
    }

    private void simpanData() {
        String nama = txtNama.getText();
        String user = txtUser.getText();
        String pass = new String(txtPass.getPassword());
        String level = cmbLevel.getSelectedItem().toString();

        if (nama.isBlank() || user.isBlank() || pass.isBlank()) {
            JOptionPane.showMessageDialog(this, "Semua kolom (Nama, Username, Password) wajib diisi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Admin a = new Admin();
            a.setUsername(user);
            a.setPassword(pass);
            a.setNamaLengkap(nama);
            a.setAdminLevel(level);
            a.setRole("ADMIN"); // Paksa role menjadi ADMIN

            // Menyimpan ke database via DAO
            if (UserDAO.insertAdmin(a)) {
                JOptionPane.showMessageDialog(this, "Admin baru berhasil didaftarkan!");
                refresh();
                // Kosongkan form
                txtNama.setText("");
                txtUser.setText("");
                txtPass.setText("");
                cmbLevel.setSelectedIndex(0);
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menyimpan data Admin ke database.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            // Menangani duplikasi username
            if (e.getMessage().contains("Duplicate")) {
                JOptionPane.showMessageDialog(this, "Username '" + user + "' sudah terdaftar. Silakan pilih username lain.", "Error Registrasi", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Error Database: " + e.getMessage(), "Fatal Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void hapusAdmin() {
        int row = tabel.getSelectedRow();
        if (row >= 0) {
            String usernameAdmin = tabel.getValueAt(row, 0).toString();

            // Proteksi: Jangan sampai Admin menghapus dirinya sendiri
            if (usernameAdmin.equals(studiokita.controller.AuthController.getCurrentUser().getUsername())) {
                JOptionPane.showMessageDialog(this, "Anda tidak bisa menghapus akun Anda sendiri saat sedang login!", "Akses Ditolak", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Yakin ingin mencabut akses dan menghapus admin @" + usernameAdmin + "?",
                    "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                // Eksekusi Hapus Langsung via JDBC
                String sql = "DELETE FROM users WHERE username = ? AND role = 'ADMIN'";
                try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, usernameAdmin);
                    if (ps.executeUpdate() > 0) {
                        JOptionPane.showMessageDialog(this, "Akses admin berhasil dicabut.");
                        refresh();
                    } else {
                        JOptionPane.showMessageDialog(this, "Gagal menghapus admin.");
                    }
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(this, "Error Database: " + e.getMessage(), "Gagal", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Pilih data admin di tabel terlebih dahulu!");
        }
    }

    private JTextField addLabeledField(JPanel p, String lbl, String ph) {
        addLabel(p, lbl);
        JTextField f = UIKit.field();
        f.setText(ph);
        f.setMaximumSize(UIKit.maxField());
        p.add(f);
        p.add(UIKit.gap(12));
        return f;
    }

    private void addLabel(JPanel p, String text) {
        JLabel l = new JLabel(text.toUpperCase());
        l.setFont(UIKit.FONT_SMALL);
        l.setForeground(UIKit.fgMuted());
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(l);
        p.add(UIKit.gap(4));
    }

    @Override
    public void refresh() {
        try {
            model.setRowCount(0);
            for (Admin a : UserDAO.getAllAdmins()) {
                model.addRow(new Object[]{
                    a.getUsername(),
                    a.getNamaLengkap(),
                    a.getAdminLevel()
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat data admin dari server Railway: " + e.getMessage());
        }
    }
}