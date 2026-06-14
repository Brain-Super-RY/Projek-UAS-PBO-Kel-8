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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * MainFrame — Premium Container (Custom Glassmorphism & State Persistence).
 */
public class MainFrame extends JFrame {

    private JPanel pnlSidebar, pnlContent, pnlTopStatus;
    private CardLayout contentCards;
    private JButton btnActiveMenu = null;

    public MainFrame() {
        initComponents();
        navigasiKe("dashboard");
        syncActiveButton("dashboard");
    }

    /** Constructor untuk mempertahankan state saat ganti tema */
    public MainFrame(int extendedState, Rectangle bounds) {
        initComponents();
        setExtendedState(extendedState);
        if (extendedState != MAXIMIZED_BOTH) setBounds(bounds);
        navigasiKe("dashboard");
        syncActiveButton("dashboard");
    }

    private void syncActiveButton(String dest) {
        if (pnlSidebar == null) return;
        for (Component c : pnlSidebar.getComponents()) {
            if (c instanceof JButton b && dest.equals(b.getClientProperty("dest"))) {
                if (btnActiveMenu != null) btnActiveMenu.setSelected(false);
                b.setSelected(true);
                btnActiveMenu = b;
                break;
            }
        }
    }

    private JButton navBtn(String label, String icon, String dest) {
        JButton b = UIKit.sidebarBtn(label, icon);
        b.putClientProperty("dest", dest);
        b.addActionListener(e -> {
            if (btnActiveMenu != null) btnActiveMenu.setSelected(false);
            b.setSelected(true);
            btnActiveMenu = b;
            navigasiKe(dest);
        });
        return b;
    }

    private void initComponents() {
        setTitle("Studio Kita Management System — Premium Edition");
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setUndecorated(true); // Modern undecorated look
        setSize(1280, 800);
        setLocationRelativeTo(null);

        // Resize Listener for consistency
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                // Force panels to re-layout if they are visible
                if (pnlContent != null) {
                    for (Component c : pnlContent.getComponents()) {
                        if (c.isVisible() && c instanceof Refreshable r) {
                            r.refresh();
                        }
                    }
                }
            }
        });

        // Main Layout (Glass Background)
        JPanel root = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                Color c1 = ThemeManager.isDark() ? new Color(20, 20, 40) : new Color(220, 225, 245);
                Color c2 = ThemeManager.isDark() ? new Color(40, 30, 70) : new Color(245, 240, 255);
                g2.setPaint(new GradientPaint(0, 0, c1, getWidth(), getHeight(), c2));
                g2.fillRect(0, 0, getWidth(), getHeight());
                
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Floating Decorative Orbs
                int a = ThemeManager.isDark() ? 12 : 25;
                g2.setColor(new Color(UIKit.ACCENT.getRed(), UIKit.ACCENT.getGreen(), UIKit.ACCENT.getBlue(), a));
                g2.fillOval(getWidth() - 300, -100, 600, 600);
                
                g2.setColor(new Color(UIKit.PURPLE.getRed(), UIKit.PURPLE.getGreen(), UIKit.PURPLE.getBlue(), a));
                g2.fillOval(-150, getHeight() - 350, 450, 450);
                
                g2.setColor(new Color(UIKit.BLUE.getRed(), UIKit.BLUE.getGreen(), UIKit.BLUE.getBlue(), a / 2));
                g2.fillOval(getWidth() / 2, getHeight() / 2, 200, 200);
                
                g2.dispose();
            }
        };
        setContentPane(root);

        // Window Bar
        root.add(UIKit.windowBar(this, "STUDIO KITA MANAGEMENT SYSTEM"), BorderLayout.NORTH);

        // Body Wrapper (Sidebar + Main)
        JPanel body = new JPanel(new BorderLayout());
        body.setOpaque(false);
        root.add(body, BorderLayout.CENTER);

        // 1. Sidebar (Glass)
        pnlSidebar = buildSidebar();
        body.add(pnlSidebar, BorderLayout.WEST);

        // 2. Main Area
        JPanel mainArea = new JPanel(new BorderLayout());
        mainArea.setOpaque(false);
        
        pnlTopStatus = buildTopStatus();
        mainArea.add(pnlTopStatus, BorderLayout.NORTH);

        contentCards = new CardLayout();
        pnlContent = new JPanel(contentCards);
        pnlContent.setOpaque(false);
        pnlContent.setBorder(new EmptyBorder(10, 30, 30, 30));
        mainArea.add(pnlContent, BorderLayout.CENTER);

        body.add(mainArea, BorderLayout.CENTER);

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override public void windowClosing(java.awt.event.WindowEvent e) { keluar(); }
        });
    }

    private JPanel buildSidebar() {
        UIKit.GlassPanel s = new UIKit.GlassPanel(0) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Right border separator
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(ThemeManager.isDark() 
                        ? new Color(255, 255, 255, 15) 
                        : new Color(0, 0, 0, 8));
                g2.fillRect(getWidth() - 1, 0, 1, getHeight());
                g2.dispose();
            }
        };
        s.setLayout(new BoxLayout(s, BoxLayout.Y_AXIS));
        s.setPreferredSize(new Dimension(280, 0));
        s.setBorder(new EmptyBorder(40, 25, 30, 25));

        JLabel lblBrand = new JLabel("STUDIO KITA");
        lblBrand.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblBrand.setForeground(UIKit.ACCENT);
        lblBrand.setAlignmentX(LEFT_ALIGNMENT);
        s.add(lblBrand);
        s.add(UIKit.gap(45));

        addSidebarSection(s, "UTAMA");
        s.add(navBtn("Dashboard", "🏠", "dashboard"));
        
        if (AuthController.isAdmin()) {
            addSidebarSection(s, "MANAJEMEN");
            s.add(navBtn("Sewa Alat", "🎒", "sewa"));
            s.add(navBtn("Jasa Foto", "📸", "jasa"));
            s.add(navBtn("Data Pelanggan", "👥", "customer"));
            
            addSidebarSection(s, "LAPORAN");
            s.add(navBtn("Penghasilan", "💰", "penghasilan"));
            s.add(navBtn("Rekap Transaksi", "📊", "rekap"));
        } else {
            addSidebarSection(s, "LAYANAN");
            s.add(navBtn("Sewa Alat", "🎒", "sewa"));
            s.add(navBtn("Jasa Foto", "📸", "jasa"));
            
            addSidebarSection(s, "AKUN SAYA");
            s.add(navBtn("Booking & Profil", "👤", "profil"));
        }

        s.add(Box.createVerticalGlue());
        
        // Add Logout at bottom of sidebar too for convenience
        s.add(UIKit.gap(20));
        JButton btnLogoutSid = UIKit.sidebarBtn("Logout", "🚪");
        btnLogoutSid.setForeground(UIKit.RED);
        btnLogoutSid.addActionListener(e -> logout());
        s.add(btnLogoutSid);
        
        return s;
    }

    private JPanel buildTopStatus() {
        JPanel t = new JPanel(new BorderLayout());
        t.setPreferredSize(new Dimension(0, 75));
        t.setBorder(new EmptyBorder(15, 30, 0, 30));
        t.setOpaque(false);

        // User Info
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        left.setOpaque(false);
        JLabel lblAvatar = new JLabel("👤");
        lblAvatar.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        
        User cur = AuthController.getCurrentUser();
        JPanel userInfo = new JPanel(new GridLayout(2, 1, 0, 0));
        userInfo.setOpaque(false);
        JLabel lblName = new JLabel(cur != null ? cur.getNamaLengkap() : "Guest");
        lblName.setFont(UIKit.FONT_H3);
        lblName.setForeground(UIKit.fgPrimary());
        
        JLabel lblRole = new JLabel(AuthController.isAdmin() ? "ADMINISTRATOR" : "CUSTOMER");
        lblRole.setFont(UIKit.FONT_SMALL);
        lblRole.setForeground(UIKit.ACCENT);
        
        userInfo.add(lblName); userInfo.add(lblRole);
        left.add(lblAvatar); left.add(userInfo);

        // Controls
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        right.setOpaque(false);
        
        JButton btnTheme = UIKit.iconBtn(ThemeManager.isDark() ? "☀️" : "🌙", UIKit.ACCENT);
        btnTheme.addActionListener(e -> toggleTheme());

        JButton btnLogout = UIKit.btn("LOGOUT", UIKit.RED);
        btnLogout.addActionListener(e -> logout());

        right.add(btnTheme); right.add(btnLogout);

        t.add(left, BorderLayout.WEST);
        t.add(right, BorderLayout.EAST);
        return t;
    }

    private void toggleTheme() {
        int state = getExtendedState();
        Rectangle bounds = getBounds();
        ThemeManager.toggle();
        this.dispose();
        new MainFrame(state, bounds).setVisible(true);
    }

    private void addSidebarSection(JPanel p, String text) {
        p.add(UIKit.gap(32));
        JLabel h = new JLabel(text);
        h.setFont(new Font("Segoe UI", Font.BOLD, 11));
        h.setForeground(UIKit.fgMuted());
        h.setAlignmentX(LEFT_ALIGNMENT);
        p.add(h); p.add(UIKit.gap(14));
    }

    private void navigasiKe(String dest) {
        try {
            Component comp = switch (dest) {
                case "dashboard"   -> new DashboardPanel(this);
                case "sewa"        -> new SewaPanel();
                case "jasa"        -> new JasaPanel();
                case "customer"    -> new CustomerPanel();
                case "penghasilan" -> new PenghasilanPanel();
                case "rekap"       -> new RekapPanel();
                case "profil"      -> new ProfilPanel();
                default -> {
                    JPanel p = new JPanel(new GridBagLayout());
                    p.setOpaque(false);
                    JLabel l = new JLabel("Halaman '" + dest + "' sedang dikembangkan 🛠️");
                    l.setFont(UIKit.FONT_H3);
                    l.setForeground(UIKit.fgSecondary());
                    p.add(l);
                    yield p;
                }
            };

            pnlContent.removeAll();
            pnlContent.add(comp, dest);
            if (comp instanceof Refreshable r) r.refresh();
            contentCards.show(pnlContent, dest);
            pnlContent.revalidate(); pnlContent.repaint();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal memuat halaman: " + e.getMessage(), "Error", 0);
        }
    }

    private void logout() {
        if (JOptionPane.showConfirmDialog(this, "Logout dari sistem?", "Logout", 0) == 0) {
            AuthController.logout();
            new LoginForm().setVisible(true);
            this.dispose();
        }
    }

    private void keluar() {
        if (JOptionPane.showConfirmDialog(this, "Keluar aplikasi?", "Exit", 0) == 0) System.exit(0);
    }

    public void goTo(String dest) { navigasiKe(dest); }

    public interface Refreshable { void refresh(); }

    static class DashboardPanel extends JPanel implements Refreshable {
        private final MainFrame parent;
        private JPanel grid;
        public DashboardPanel(MainFrame p) { 
            this.parent = p; 
            setOpaque(false); 
            addComponentListener(new java.awt.event.ComponentAdapter() {
                @Override public void componentResized(java.awt.event.ComponentEvent e) { updateGridLayout(); }
            });
            refresh(); 
        }
        
        private void updateGridLayout() {
            if (grid == null) return;
            int w = getWidth();
            int cols = w > 1000 ? 3 : (w > 650 ? 2 : 1);
            ((GridLayout)grid.getLayout()).setColumns(cols);
            grid.revalidate();
        }

        @Override public void refresh() {
            removeAll(); setLayout(new BorderLayout(0, 30));
            
            JPanel welcome = new JPanel(new BorderLayout()); welcome.setOpaque(false);
            JLabel lblHello = new JLabel("Selamat Datang Kembali,");
            lblHello.setFont(UIKit.FONT_NORMAL); 
            lblHello.setForeground(UIKit.fgSecondary());
            
            JLabel lblName = new JLabel(AuthController.getCurrentUser().getNamaLengkap() + "!");
            lblName.setFont(new Font("Segoe UI", Font.BOLD, 36));
            lblName.setForeground(UIKit.fgPrimary());
            
            welcome.add(lblHello, BorderLayout.NORTH); welcome.add(lblName, BorderLayout.CENTER);

            grid = new JPanel(new GridLayout(0, 3, 25, 25)); grid.setOpaque(false);
            if (AuthController.isAdmin()) {
                grid.add(UIKit.statCard("👥", "Total Pelanggan", String.valueOf(CustomerController.getAllCustomers().size()), UIKit.BLUE));
                grid.add(UIKit.statCard("🎒", "Alat Disewa", String.valueOf(TransaksiController.countSewa()), UIKit.ORANGE));
                grid.add(UIKit.statCard("📸", "Booking Jasa", String.valueOf(TransaksiController.countJasa()), UIKit.GREEN));
                grid.add(UIKit.statCard("💰", "Total Omzet", "Rp "+String.format("%,.0f", TransaksiController.getTotalPenghasilan()), UIKit.ACCENT));
            } else {
                var mine = TransaksiController.getByCustomer(AuthController.getCurrentUser().getUsername());
                grid.add(UIKit.statCard("🎒", "Sewa Saya", String.valueOf(mine.stream().filter(t->"SEWA".equals(t.getJenisLayanan())).count()), UIKit.ORANGE));
                grid.add(UIKit.statCard("📸", "Jasa Saya", String.valueOf(mine.stream().filter(t->"JASA".equals(t.getJenisLayanan())).count()), UIKit.GREEN));
                grid.add(UIKit.statCard("💳", "Total Transaksi", "Rp "+String.format("%,.0f", TransaksiController.getTotalByCustomer(AuthController.getCurrentUser().getUsername())), UIKit.BLUE));
            }
            add(welcome, BorderLayout.NORTH); add(grid, BorderLayout.CENTER);
            updateGridLayout();
            revalidate(); repaint();
        }
    }
}
