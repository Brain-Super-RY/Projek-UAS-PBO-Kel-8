package studiokita.view;

import studiokita.ThemeManager;
import studiokita.UIKit;
import studiokita.controller.AuthController;
import studiokita.controller.CustomerController;

import javax.swing.*;
import java.awt.*;

/**
 * LoginForm — Ultra Luxury Gold & Pitch Black Edition. Mengganti background
 * animasi dengan gradien statis hitam-emas yang megah dan solid.
 */
public class LoginForm extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JLabel lblStatus;

    public LoginForm() {
        initComponents();
    }

    private void initComponents() {
        setTitle("Studio Kita — Premium Creative Studio");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        // FIX: Menghapus Animasi Timer, menggantinya dengan panel Hitam Pekat statis bergradien mewah
        JPanel bgPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Dasar latar belakang: HITAM SEKALI (Pure Pitch Black)
                Color deepBlack = new Color(10, 10, 12);
                // Aksen sudut: Kilau Emas Redup Muted
                Color goldGlow = new Color(45, 35, 15);

                // Gradien radial/linear statis untuk menciptakan efek kedalaman (vignette luxury)
                GradientPaint gp = new GradientPaint(0, 0, goldGlow, getWidth(), getHeight(), deepBlack);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());

                g2.dispose();
            }
        };

        JPanel center = new JPanel(new GridBagLayout());
        center.setOpaque(false);
        bgPanel.add(center, BorderLayout.CENTER);

        // Card Glassmorphic bernuansa gelap pekat dengan border emas tipis
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Latar belakang internal card: Hitam transparan solid
                g2.setColor(new Color(20, 20, 25, 230));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(420, 620));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(212, 160, 23, 80), 1, true), // Border Emas Tegas
                BorderFactory.createEmptyBorder(40, 40, 40, 40)
        ));

        // Logo Aplikasi
        JLabel lblIcon = new JLabel("📸", SwingConstants.CENTER);
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 64));
        lblIcon.setAlignmentX(CENTER_ALIGNMENT);
        card.add(lblIcon);
        card.add(UIKit.gap(15));

        // Judul Utama (Warna Emas)
        JLabel lblTitle = new JLabel("STUDIO KITA", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitle.setForeground(UIKit.ACCENT_GOLD);
        lblTitle.setAlignmentX(CENTER_ALIGNMENT);
        card.add(lblTitle);
        card.add(UIKit.gap(30));

        // Fields Input Username
        txtUsername = UIKit.field();
        txtUsername.setMaximumSize(UIKit.maxField());
        txtUsername.setAlignmentX(CENTER_ALIGNMENT);

        // Fields Input Password (Diselaraskan ke Emas-Hitam)
        txtPassword = new JPasswordField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        txtPassword.setOpaque(false); // WAJIB FALSE agar tidak memicu bug grafik bergaris
        txtPassword.setFont(UIKit.FONT_REGULAR);
        txtPassword.setForeground(UIKit.fgPrimary());
        txtPassword.setCaretColor(UIKit.currentGold());
        txtPassword.setBackground(ThemeManager.isDark() ? new Color(255, 255, 255, 15) : new Color(0, 0, 0, 10));
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeManager.isDark() ? new Color(255, 255, 255, 25) : new Color(0, 0, 0, 30), 1, true),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));
        txtPassword.setMaximumSize(UIKit.maxField());

        // Menyusun Form ke dalam Card
        card.add(fieldLabel("USERNAME"));
        card.add(UIKit.gap(5));
        card.add(txtUsername);
        card.add(UIKit.gap(15));

        card.add(fieldLabel("PASSWORD"));
        card.add(UIKit.gap(5));
        card.add(txtPassword);
        card.add(UIKit.gap(10));

        // Label Status Notifikasi
        lblStatus = new JLabel(" ", SwingConstants.CENTER);
        lblStatus.setFont(UIKit.FONT_SMALL);
        lblStatus.setForeground(UIKit.ACCENT_GOLD);
        lblStatus.setAlignmentX(CENTER_ALIGNMENT);
        card.add(lblStatus);
        card.add(UIKit.gap(15));

        // Tombol Utama (Otomatis Emas berkat UIKit update)
        btnLogin = UIKit.btn("SIGN IN", UIKit.ACCENT_GOLD);
        btnLogin.setMaximumSize(new Dimension(250, 45));
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogin.addActionListener(e -> performLogin());

        JButton btnExit = UIKit.btn("EXIT", new Color(255, 255, 255, 20));
        btnExit.setMaximumSize(new Dimension(250, 45));
        btnExit.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnExit.addActionListener(e -> System.exit(0));

        JButton btnRegister = new JButton("Belum punya akun? Daftar di sini");
        btnRegister.setFont(UIKit.FONT_SMALL);
        btnRegister.setForeground(UIKit.ACCENT_GOLD); // Diubah ke Emas
        btnRegister.setContentAreaFilled(false);
        btnRegister.setBorderPainted(false);
        btnRegister.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRegister.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnRegister.addActionListener(e -> openRegisterDialog());

        // Memasukkan Tombol ke dalam Card
        card.add(btnLogin);
        card.add(UIKit.gap(10));
        card.add(btnExit);
        card.add(UIKit.gap(20));
        card.add(btnRegister);

        center.add(card);

        add(bgPanel);
        setSize(1100, 800);
        setLocationRelativeTo(null);
    }

    private JLabel fieldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(UIKit.FONT_SMALL);
        l.setForeground(new Color(180, 170, 150)); // Berwarna krem/emas redup
        l.setAlignmentX(CENTER_ALIGNMENT);
        return l;
    }

    private void performLogin() {
        String u = txtUsername.getText();
        String p = new String(txtPassword.getPassword());

        if (u.isEmpty() || p.isEmpty()) {
            lblStatus.setText("Credentials cannot be empty!");
            return;
        }

        lblStatus.setText("Verifying identity...");
        btnLogin.setEnabled(false);

        new Thread(() -> {
            var res = AuthController.login(u, p);
            SwingUtilities.invokeLater(() -> {
                if (res.isSuccess()) {
                    new MainFrame().setVisible(true);
                    this.dispose();
                } else {
                    lblStatus.setText(res.getMessage());
                    btnLogin.setEnabled(true);
                }
            });
        }).start();
    }

    private void openRegisterDialog() {
        JTextField regNama = new JTextField();
        JTextField regUser = new JTextField();
        JPasswordField regPass = new JPasswordField();
        JTextField regEmail = new JTextField();
        JTextField regTelp = new JTextField();
        JTextField regAlamat = new JTextField();

        Object[] message = {
            "Nama Lengkap:", regNama,
            "Username:", regUser,
            "Password:", regPass,
            "Email:", regEmail,
            "No WhatsApp/Telp:", regTelp,
            "Alamat Lengkap:", regAlamat
        };

        // Mengubah warna Dialog Register agar ikutan Hitam-Emas pekat
        UIManager.put("OptionPane.background", new Color(15, 15, 18));
        UIManager.put("Panel.background", new Color(15, 15, 18));
        UIManager.put("OptionPane.messageForeground", Color.WHITE);

        int option = JOptionPane.showConfirmDialog(this, message, "Pendaftaran Akun Customer", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);

        if (option == JOptionPane.OK_OPTION) {
            String nama = regNama.getText();
            String user = regUser.getText();
            String pass = new String(regPass.getPassword());
            String email = regEmail.getText();
            String telp = regTelp.getText();
            String alamat = regAlamat.getText();

            String hasil = CustomerController.tambahCustomer(nama, user, pass, email, telp, alamat);

            if ("OK".equals(hasil)) {
                JOptionPane.showMessageDialog(this, "Pendaftaran Berhasil! Silakan Login.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                txtUsername.setText(user);
                txtPassword.setText(pass);
            } else {
                JOptionPane.showMessageDialog(this, hasil, "Pendaftaran Gagal", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
