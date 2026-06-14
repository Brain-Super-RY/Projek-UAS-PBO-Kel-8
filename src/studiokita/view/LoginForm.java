package studiokita.view;

import studiokita.UIKit;
import studiokita.controller.AuthController;
import studiokita.ThemeManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;

/**
 * LoginForm — Premium Web-Style Login (Glassmorphism & Dynamic Background).
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
        setUndecorated(true);

        // Main Container with Animated-Style Background
        JPanel bgPanel = new JPanel(new BorderLayout()) {
            private float phase = 0;
            {
                Timer t = new Timer(50, e -> {
                    phase += 0.02f;
                    repaint();
                });
                t.start();
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Deep premium gradient
                Color c1 = new Color(15, 15, 35);
                Color c2 = new Color(45, 25, 75);
                g2.setPaint(new GradientPaint(0, 0, c1, getWidth(), getHeight(), c2));
                g2.fillRect(0, 0, getWidth(), getHeight());

                // Floating "Nebula" Orbs
                drawOrb(g2, getWidth() * 0.8, getHeight() * 0.2, 400, UIKit.ACCENT, 0.15f, phase);
                drawOrb(g2, getWidth() * 0.1, getHeight() * 0.8, 350, UIKit.PURPLE, 0.12f, phase * 0.7f);
                drawOrb(g2, getWidth() * 0.5, getHeight() * 0.5, 250, UIKit.BLUE, 0.1f, phase * 1.2f);

                g2.dispose();
            }

            private void drawOrb(Graphics2D g2, double x, double y, int size, Color c, float alpha, float p) {
                double dx = Math.sin(p) * 30;
                double dy = Math.cos(p) * 30;
                g2.setPaint(new RadialGradientPaint(
                        new Point((int)(x + dx), (int)(y + dy)),
                        size,
                        new float[]{0f, 1f},
                        new Color[]{new Color(c.getRed(), c.getGreen(), c.getBlue(), (int)(alpha * 255)), new Color(c.getRed(), c.getGreen(), c.getBlue(), 0)}
                ));
                g2.fill(new Ellipse2D.Double(x + dx - size, y + dy - size, size * 2, size * 2));
            }
        };
        setContentPane(bgPanel);

        // Window Bar
        bgPanel.add(UIKit.windowBar(this, "STUDIO KITA • SECURE LOGIN"), BorderLayout.NORTH);

        // Center Content
        JPanel center = new JPanel(new GridBagLayout());
        center.setOpaque(false);
        bgPanel.add(center, BorderLayout.CENTER);

        // Login Card
        UIKit.GlassPanel card = new UIKit.GlassPanel(45) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Extra inner border for card
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setStroke(new BasicStroke(1.5f));
                g2.setColor(new Color(255, 255, 255, 15));
                g2.drawRoundRect(10, 10, getWidth()-20, getHeight()-20, 35, 35);
                g2.dispose();
            }
        };
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(450, 650));
        card.setBorder(new EmptyBorder(50, 55, 50, 55));

        // Header Section
        JLabel lblIcon = new JLabel("📸");
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 72));
        lblIcon.setAlignmentX(CENTER_ALIGNMENT);

        JLabel lblWelcome = new JLabel("STUDIO KITA");
        lblWelcome.setFont(new Font("Segoe UI", Font.BOLD, 38));
        lblWelcome.setForeground(Color.WHITE);
        lblWelcome.setAlignmentX(CENTER_ALIGNMENT);

        JLabel lblDesc = new JLabel("Professional Camera & Studio Rental");
        lblDesc.setFont(UIKit.FONT_BODY);
        lblDesc.setForeground(new Color(190, 190, 220));
        lblDesc.setAlignmentX(CENTER_ALIGNMENT);

        // Input Styling
        txtUsername = UIKit.field();
        txtUsername.setPreferredSize(new Dimension(0, 48));
        txtUsername.setBackground(new Color(255, 255, 255, 12));
        txtUsername.setForeground(Color.WHITE);
        txtUsername.setCaretColor(UIKit.ACCENT);
        
        txtPassword = UIKit.passField();
        txtPassword.setPreferredSize(new Dimension(0, 48));
        txtPassword.setBackground(new Color(255, 255, 255, 12));
        txtPassword.setForeground(Color.WHITE);
        txtPassword.setCaretColor(UIKit.ACCENT);

        lblStatus = new JLabel(" ");
        lblStatus.setFont(UIKit.FONT_SMALL);
        lblStatus.setForeground(UIKit.RED);
        lblStatus.setAlignmentX(CENTER_ALIGNMENT);

        // Login Button (Premium Gradient)
        btnLogin = UIKit.btn("AUTHENTICATE", UIKit.ACCENT);
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogin.setPreferredSize(new Dimension(0, 52));
        btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));
        btnLogin.setAlignmentX(CENTER_ALIGNMENT);
        btnLogin.addActionListener(e -> performLogin());

        JButton btnExit = UIKit.btn("EXIT APPLICATION", new Color(255, 255, 255, 35));
        btnExit.setPreferredSize(new Dimension(0, 48));
        btnExit.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        btnExit.setAlignmentX(CENTER_ALIGNMENT);
        btnExit.addActionListener(e -> System.exit(0));

        // Assemble Components
        card.add(lblIcon);
        card.add(UIKit.gap(12));
        card.add(lblWelcome);
        card.add(lblDesc);
        card.add(UIKit.gap(45));

        card.add(fieldLabel("USERNAME"));
        card.add(UIKit.gap(6));
        card.add(txtUsername);
        card.add(UIKit.gap(22));

        card.add(fieldLabel("PASSWORD"));
        card.add(UIKit.gap(6));
        card.add(txtPassword);
        
        card.add(UIKit.gap(15));
        card.add(lblStatus);
        card.add(UIKit.gap(15));
        
        card.add(btnLogin);
        card.add(UIKit.gap(12));
        card.add(btnExit);

        center.add(card);
        
        setSize(1100, 800);
        setLocationRelativeTo(null);
    }

    private JLabel fieldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(UIKit.FONT_SMALL);
        l.setForeground(new Color(160, 160, 190));
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
}
