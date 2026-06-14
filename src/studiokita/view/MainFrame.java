package studiokita.view;

import studiokita.ThemeManager;
import studiokita.UIKit;
import studiokita.controller.AuthController;
import studiokita.controller.CustomerController;
import studiokita.controller.TransaksiController;
import studiokita.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * MainFrame — Premium Dashboard Frame.
 * UPGRADE: Sistem Sinkronisasi Global Real-time Event Trigger untuk
 * pembaruan data lintas panel (Dashboard, Sewa, Jasa, dan Rekap).
 */
public class MainFrame extends JFrame {

    private JPanel pnlSidebar, pnlContent, bgPanel;
    private CardLayout contentCards;

    public interface Refreshable {
        void refresh();
    }

    public MainFrame() {
        initComponents();
        navigasiKe("dashboard");
        syncActiveButton("dashboard");
    }

    public MainFrame(int extendedState, Rectangle bounds) {
        initComponents();
        setExtendedState(extendedState);
        if (extendedState != MAXIMIZED_BOTH) {
            setBounds(bounds);
        }
        navigasiKe("dashboard");
        syncActiveButton("dashboard");
    }

    private void initComponents() {
        setTitle("Studio Kita — Premium Dashboard Rental Kamera");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 800); 
        setMinimumSize(new Dimension(1100, 750));
        setLocationRelativeTo(null);

        // BACKGROUND ANIMASI MESH GRADIENT LUXURY
        bgPanel = new UIKit.AnimatedGradientBackground();
        bgPanel.setLayout(new BorderLayout(20, 20)); 
        bgPanel.setBorder(new EmptyBorder(25, 25, 25, 25)); 
        setContentPane(bgPanel);

        pnlSidebar = new UIKit.GlassPanel(30); 
        pnlSidebar.setLayout(new BorderLayout());
        pnlSidebar.setPreferredSize(new Dimension(280, 0)); 
        
        buildSidebar();
        bgPanel.add(pnlSidebar, BorderLayout.WEST);

        contentCards = new CardLayout();
        pnlContent = new JPanel(contentCards);
        pnlContent.setOpaque(false);
        pnlContent.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Mendaftarkan seluruh sub-panel ke dalam container CardLayout
        pnlContent.add(new DashboardPanel(), "dashboard");
        pnlContent.add(new SewaPanel(), "sewa");
        pnlContent.add(new JasaPanel(), "jasa");
        pnlContent.add(new ProfilPanel(), "profil");
        
        if (AuthController.isAdmin()) {
            pnlContent.add(new CustomerPanel(), "customer");
            pnlContent.add(new AdminPanel(), "admin");
            pnlContent.add(new RekapPanel(), "rekap");
            pnlContent.add(new PenghasilanPanel(), "penghasilan");
        }

        bgPanel.add(pnlContent, BorderLayout.CENTER);
    }

    private void buildSidebar() {
        JPanel pnlProfil = new JPanel(new BorderLayout(15, 0));
        pnlProfil.setOpaque(false);
        pnlProfil.setBorder(new EmptyBorder(35, 25, 30, 20));

        JLabel lblAvatar = new JLabel("SK"); 
        lblAvatar.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lblAvatar.setForeground(UIKit.currentGold()); 
        pnlProfil.add(lblAvatar, BorderLayout.WEST);

        JPanel pnlNama = new JPanel(new GridLayout(2, 1));
        pnlNama.setOpaque(false);
        
        User user = AuthController.getCurrentUser();
        String namaLengkap = (user != null) ? user.getNamaLengkap() : "Guest";
        String roleStr = (user != null) ? user.getRole() : "";

        JLabel lblUser = new JLabel(namaLengkap);
        lblUser.setFont(UIKit.FONT_BOLD);
        lblUser.setForeground(UIKit.fgPrimary());
        
        JLabel lblRole = new JLabel(roleStr);
        lblRole.setFont(UIKit.FONT_SMALL);
        lblRole.setForeground(UIKit.currentGold()); 

        pnlNama.add(lblUser);
        pnlNama.add(lblRole);
        pnlProfil.add(pnlNama, BorderLayout.CENTER);

        pnlSidebar.add(pnlProfil, BorderLayout.NORTH);

        JPanel pnlMenu = new JPanel();
        pnlMenu.setLayout(new BoxLayout(pnlMenu, BoxLayout.Y_AXIS));
        pnlMenu.setOpaque(false);
        pnlMenu.setBorder(new EmptyBorder(0, 15, 0, 15));

        addMenu(pnlMenu, "Overview", "dashboard");
        addMenu(pnlMenu, "Sewa Alat", "sewa");
        addMenu(pnlMenu, "Booking Jasa", "jasa");

        if (AuthController.isAdmin()) {
            pnlMenu.add(UIKit.gap(25));
            JLabel lblAdmin = new JLabel("    ADMIN WORKSPACE");
            lblAdmin.setFont(new Font("Segoe UI", Font.BOLD, 10));
            lblAdmin.setForeground(UIKit.fgMuted());
            pnlMenu.add(lblAdmin);
            pnlMenu.add(UIKit.gap(10));

            addMenu(pnlMenu, "Daftar Pelanggan", "customer");
            addMenu(pnlMenu, "Staf Admin", "admin");
            addMenu(pnlMenu, "Rekap Transaksi", "rekap");
            addMenu(pnlMenu, "Penghasilan", "penghasilan");
        } else {
            pnlMenu.add(UIKit.gap(25));
            addMenu(pnlMenu, "Profil Saya", "profil");
        }

        pnlSidebar.add(pnlMenu, BorderLayout.CENTER);

        JPanel pnlBottom = new JPanel(new GridLayout(2, 1, 0, 10)); 
        pnlBottom.setOpaque(false);
        pnlBottom.setBorder(new EmptyBorder(20, 20, 30, 20));

        String themeText = ThemeManager.isDark() ? "LIGHT MODE" : "DARK MODE";
        JButton btnTheme = new JButton(themeText);
        btnTheme.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnTheme.setForeground(UIKit.fgPrimary());
        btnTheme.setBackground(ThemeManager.isDark() ? new Color(255, 255, 255, 30) : new Color(0, 0, 0, 15));
        btnTheme.setFocusPainted(false);
        btnTheme.setBorder(new EmptyBorder(10, 10, 10, 10));
        btnTheme.setContentAreaFilled(false);
        btnTheme.setOpaque(true);
        btnTheme.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnTheme.addActionListener(e -> {
            ThemeManager.toggle();
            int state = getExtendedState();
            Rectangle bounds = getBounds();
            dispose();
            new MainFrame(state, bounds).setVisible(true);
        });
        pnlBottom.add(btnTheme);

        JButton btnLogout = new JButton("LOG OUT");
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnLogout.setForeground(UIKit.RED); 
        btnLogout.setBackground(new Color(255, 55, 95, 30)); 
        btnLogout.setFocusPainted(false);
        btnLogout.setBorder(new EmptyBorder(10, 10, 10, 10));
        btnLogout.setContentAreaFilled(false);
        btnLogout.setOpaque(true);
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogout.addActionListener(e -> {
            AuthController.logout();
            dispose();
            new LoginForm().setVisible(true);
        });
        pnlBottom.add(btnLogout);

        pnlSidebar.add(pnlBottom, BorderLayout.SOUTH);
    }

    private void addMenu(JPanel parent, String text, String dest) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                if (isOpaque()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(getBackground());
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15); 
                    g2.dispose();
                }
                super.paintComponent(g);
            }
        };
        btn.putClientProperty("dest", dest);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(UIKit.fgMuted());
        
        Color baseGold = UIKit.currentGold();
        btn.setBackground(new Color(baseGold.getRed(), baseGold.getGreen(), baseGold.getBlue(), ThemeManager.isDark() ? 40 : 25));
        
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(0, 20, 0, 0));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addActionListener(e -> {
            navigasiKe(dest);
            syncActiveButton(dest);
        });

        parent.add(btn);
        parent.add(UIKit.gap(8));
    }

    public void navigasiKe(String dest) {
        contentCards.show(pnlContent, dest);
        for (Component c : pnlContent.getComponents()) {
            if (c.isVisible() && c instanceof Refreshable r) {
                r.refresh();
            }
        }
    }

    /**
     * UPGRADE UTAMA: Fungsi global pemicu sinkronisasi data real-time.
     * Panggil fungsi ini dari SewaPanel atau JasaPanel setelah melakukan APPROVE/TOLAK.
     */
    public void refreshSemuaPanel() {
        for (Component c : pnlContent.getComponents()) {
            if (c instanceof Refreshable r) {
                r.refresh();
            }
        }
        pnlContent.revalidate();
        pnlContent.repaint();
    }

    private void syncActiveButton(String dest) {
        if (pnlSidebar == null) return;
        resetButtonColors(pnlSidebar, dest);
    }

    private void resetButtonColors(Container container, String activeDest) {
        for (Component c : container.getComponents()) {
            if (c instanceof JButton b) {
                String d = (String) b.getClientProperty("dest");
                if (d != null) {
                    if (d.equals(activeDest)) {
                        b.setForeground(UIKit.fgPrimary());
                        Color activeGold = UIKit.currentGold();
                        b.setBackground(new Color(activeGold.getRed(), activeGold.getGreen(), activeGold.getBlue(), ThemeManager.isDark() ? 55 : 40));
                        b.setOpaque(true);
                    } else {
                        b.setForeground(UIKit.fgMuted());
                        b.setOpaque(false);
                    }
                    b.repaint();
                }
            } else if (c instanceof Container) {
                resetButtonColors((Container) c, activeDest);
            }
        }
    }
}