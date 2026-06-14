package studiokita;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

/**
 * ═══════════════════════════════════════════════════════════════
 *  UIKit — Premium Glassmorphism Design System for Studio Kita
 *  Pure Java Swing + Graphics2D  •  No FlatLaf Dependency
 * ═══════════════════════════════════════════════════════════════
 */
public class UIKit {

    // ─────────────────────────────────────────────────────────
    //  🎨 COLOR PALETTE
    // ─────────────────────────────────────────────────────────
    public static final Color ACCENT = new Color(0xFF, 0xB4, 0x32);
    public static final Color BLUE   = new Color(0x5A, 0xA9, 0xFF);
    public static final Color GREEN  = new Color(0x4E, 0xD9, 0x8A);
    public static final Color RED    = new Color(0xFF, 0x5A, 0x5A);
    public static final Color PURPLE = new Color(0xB4, 0x8E, 0xFF);
    public static final Color ORANGE = new Color(0xFF, 0x8C, 0x38);
    public static final Color CYAN   = new Color(0x56, 0xE8, 0xD0);

    // ─────────────────────────────────────────────────────────
    //  🔤 FONT SYSTEM
    // ─────────────────────────────────────────────────────────
    public static final Font FONT_H1     = new Font("Segoe UI", Font.BOLD, 32);
    public static final Font FONT_H2     = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_H3     = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font FONT_NORMAL = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_BODY   = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_SMALL  = new Font("Segoe UI", Font.BOLD, 11);

    // ─────────────────────────────────────────────────────────
    //  🌗 THEME COLOR HELPERS
    // ─────────────────────────────────────────────────────────
    public static Color fgPrimary()   { return ThemeManager.isDark() ? Color.WHITE : new Color(20, 20, 40); }
    public static Color fgSecondary() { return ThemeManager.isDark() ? new Color(190, 190, 220) : new Color(70, 70, 100); }
    public static Color fgMuted()     { return ThemeManager.isDark() ? new Color(130, 130, 160) : new Color(110, 110, 140); }

    public static Color fieldBg() {
        return ThemeManager.isDark() ? new Color(255, 255, 255, 18) : new Color(255, 255, 255, 160);
    }
    public static Color fieldBorder() {
        return ThemeManager.isDark() ? new Color(255, 255, 255, 30) : new Color(0, 0, 0, 15);
    }

    // Premium card background
    public static Color cardBg() {
        return ThemeManager.isDark() ? new Color(35, 35, 65, 180) : new Color(255, 255, 255, 190);
    }

    // ═════════════════════════════════════════════════════════
    //  GLASSPANEL — Multi-Layer Frosted Glass Effect
    // ═════════════════════════════════════════════════════════
    public static class GlassPanel extends JPanel {
        private int arc;
        private Color bgColor, borderColor;
        private boolean useTheme;

        public GlassPanel(int arc) {
            this.arc = arc;
            this.useTheme = true;
            setOpaque(false);
            updateColors();
            ThemeManager.addListener(this::updateColors);
        }

        public GlassPanel(int arc, Color bg) {
            this.arc = arc;
            this.bgColor = bg;
            this.borderColor = new Color(255, 255, 255, 40);
            this.useTheme = false;
            setOpaque(false);
        }

        public void updateColors() {
            if (!useTheme) return;
            bgColor = cardBg();
            borderColor = ThemeManager.isDark() 
                    ? new Color(255, 255, 255, 35) 
                    : new Color(200, 200, 225, 120);
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight();
            RoundRectangle2D.Float shape = new RoundRectangle2D.Float(0, 0, w, h, arc, arc);

            // Layer 1: Base glass fill
            g2.setColor(bgColor);
            g2.fill(shape);

            // Layer 2: Top-down highlight (simulated light refraction)
            g2.setPaint(new GradientPaint(0, 0,
                    new Color(255, 255, 255, ThemeManager.isDark() ? 18 : 45),
                    0, h * 0.5f,
                    new Color(255, 255, 255, 0)));
            g2.fill(shape);

            // Layer 3: Inner glow edge
            g2.setColor(new Color(255, 255, 255, 10));
            g2.setStroke(new BasicStroke(2.5f));
            g2.draw(new RoundRectangle2D.Float(1.5f, 1.5f, w - 3, h - 3, arc - 1, arc - 1));

            // Layer 4: Outer border
            g2.setStroke(new BasicStroke(1.0f));
            g2.setColor(borderColor);
            g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, w - 1, h - 1, arc, arc));

            g2.dispose();
        }
    }

    // ═════════════════════════════════════════════════════════
    //  BACKGROUND — Gradient + Decorative Glass Orbs
    // ═════════════════════════════════════════════════════════
    public static void paintGlassBackground(Graphics g, int w, int h) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Main gradient
        Color c1 = ThemeManager.isDark() ? new Color(12, 12, 32) : new Color(225, 228, 248);
        Color c2 = ThemeManager.isDark() ? new Color(42, 28, 72) : new Color(248, 242, 255);
        g2.setPaint(new GradientPaint(0, 0, c1, w, h, c2));
        g2.fillRect(0, 0, w, h);

        // Decorative blurred glass orbs
        int a = ThemeManager.isDark() ? 18 : 35;
        g2.setColor(new Color(ACCENT.getRed(), ACCENT.getGreen(), ACCENT.getBlue(), a));
        g2.fillOval(w - 250, -100, 500, 500);

        g2.setColor(new Color(PURPLE.getRed(), PURPLE.getGreen(), PURPLE.getBlue(), a));
        g2.fillOval(-130, h - 280, 400, 400);

        g2.setColor(new Color(BLUE.getRed(), BLUE.getGreen(), BLUE.getBlue(), a / 2));
        g2.fillOval(w / 3, h / 3, 250, 250);

        g2.dispose();
    }

    // ═════════════════════════════════════════════════════════
    //  GLASS CARD — Preset Glass Container
    // ═════════════════════════════════════════════════════════
    public static JPanel glassCard() {
        GlassPanel p = new GlassPanel(22);
        p.setBorder(new EmptyBorder(25, 28, 25, 28));
        p.setLayout(new BorderLayout());
        return p;
    }

    // ═════════════════════════════════════════════════════════
    //  BUTTONS
    // ═════════════════════════════════════════════════════════

    /** Primary action button with gradient fill and hover effect */
    public static JButton btn(String text, Color bg) {
        JButton b = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color base = getModel().isPressed() ? bg.darker()
                           : getModel().isRollover() ? brighten(bg, 35) : bg;

                // Gradient fill: lighter at top, base at bottom
                g2.setPaint(new GradientPaint(0, 0, brighten(base, 20), 0, getHeight(), base));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);

                // Top shine
                g2.setColor(new Color(255, 255, 255, 25));
                g2.fillRoundRect(0, 0, getWidth(), getHeight() / 2, 16, 16);

                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setForeground(Color.WHITE);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    public static Color sidebarSelection() {
        return ThemeManager.isDark() ? new Color(255, 180, 50, 40) : new Color(255, 180, 50, 30);
    }

    /** Sidebar navigation button with hover highlight */
    public static JButton sidebarBtn(String text, String icon) {
        JButton b = new JButton("<html><span style='font-size:14pt'>" + icon + "</span>&nbsp;&nbsp;" + text + "</html>") {
            private boolean hovered = false;
            {
                addMouseListener(new MouseAdapter() {
                    @Override public void mouseEntered(MouseEvent e) { hovered = true; repaint(); }
                    @Override public void mouseExited(MouseEvent e)  { hovered = false; repaint(); }
                });
            }
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (isSelected()) {
                    g2.setColor(sidebarSelection());
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                    // Selection indicator line
                    g2.setColor(ACCENT);
                    g2.fillRoundRect(0, 8, 4, getHeight() - 16, 2, 2);
                } else if (hovered) {
                    g2.setColor(ThemeManager.isDark()
                            ? new Color(255, 255, 255, 12)
                            : new Color(0, 0, 0, 8));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setFont(FONT_NORMAL);
        b.setForeground(fgPrimary());
        b.setOpaque(false);
        b.setContentAreaFilled(false);
        b.setBorder(new EmptyBorder(12, 20, 12, 20));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        return b;
    }

    /** Small icon-only button */
    public static JButton iconBtn(String icon, Color fg) {
        JButton b = new JButton(icon) {
            @Override
            protected void paintComponent(Graphics g) {
                if (getModel().isRollover()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(255, 255, 255, 15));
                    g2.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 10, 10);
                    g2.dispose();
                }
                super.paintComponent(g);
            }
        };
        b.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        b.setForeground(fg);
        b.setOpaque(false);
        b.setContentAreaFilled(false);
        b.setBorder(new EmptyBorder(5, 8, 5, 8));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        return b;
    }

    // ═════════════════════════════════════════════════════════
    //  INPUT FIELDS — Glass-Style with Focus Glow
    // ═════════════════════════════════════════════════════════

    /** Rounded text field with accent focus glow */
    public static JTextField field() {
        JTextField f = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Background fill
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);

                // Border
                g2.setStroke(new BasicStroke(1.2f));
                g2.setColor(fieldBorder());
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);

                // Focus glow ring
                if (hasFocus()) {
                    g2.setColor(new Color(ACCENT.getRed(), ACCENT.getGreen(), ACCENT.getBlue(), 100));
                    g2.setStroke(new BasicStroke(2.0f));
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
                }

                g2.dispose();
                super.paintComponent(g);
            }
        };
        applyFieldStyle(f);
        f.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) { f.repaint(); }
            @Override public void focusLost(FocusEvent e)   { f.repaint(); }
        });
        return f;
    }

    /** Rounded password field with accent focus glow */
    public static JPasswordField passField() {
        JPasswordField f = new JPasswordField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);

                g2.setStroke(new BasicStroke(1.2f));
                g2.setColor(fieldBorder());
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);

                if (hasFocus()) {
                    g2.setColor(new Color(ACCENT.getRed(), ACCENT.getGreen(), ACCENT.getBlue(), 100));
                    g2.setStroke(new BasicStroke(2.0f));
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
                }

                g2.dispose();
                super.paintComponent(g);
            }
        };
        applyFieldStyle(f);
        f.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) { f.repaint(); }
            @Override public void focusLost(FocusEvent e)   { f.repaint(); }
        });
        return f;
    }

    private static void applyFieldStyle(JTextField f) {
        f.setFont(FONT_NORMAL);
        f.setOpaque(false);
        f.setBackground(fieldBg());
        f.setForeground(fgPrimary());
        f.setCaretColor(fgPrimary());
        f.setBorder(new EmptyBorder(10, 15, 10, 15));
    }

    // ═════════════════════════════════════════════════════════
    //  COMBOBOX — Styled to match glass theme
    // ═════════════════════════════════════════════════════════
    public static void styleComboBox(JComboBox<?> cb) {
        cb.setFont(FONT_NORMAL);
        cb.setForeground(fgPrimary());
        cb.setBackground(ThemeManager.isDark() ? new Color(38, 38, 62) : new Color(240, 240, 252));
        cb.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(fieldBorder(), 1, true),
                new EmptyBorder(6, 10, 6, 10)));
        cb.setFocusable(false);

        cb.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                JLabel lbl = (JLabel) super.getListCellRendererComponent(
                        list, value, index, isSelected, cellHasFocus);
                lbl.setFont(FONT_NORMAL);
                lbl.setBorder(new EmptyBorder(8, 12, 8, 12));
                if (isSelected) {
                    lbl.setBackground(ThemeManager.isDark()
                            ? new Color(60, 50, 90) : new Color(230, 228, 250));
                    lbl.setForeground(fgPrimary());
                } else {
                    lbl.setBackground(ThemeManager.isDark()
                            ? new Color(35, 35, 55) : Color.WHITE);
                    lbl.setForeground(fgPrimary());
                }
                return lbl;
            }
        });
    }

    // ═════════════════════════════════════════════════════════
    //  TABLE & SCROLL STYLING
    // ═════════════════════════════════════════════════════════
    public static void styleTable(JTable t) {
        t.setRowHeight(44);
        t.setShowVerticalLines(false);
        t.setShowHorizontalLines(true);
        t.setFont(FONT_BODY);
        t.setOpaque(false);
        t.setBackground(new Color(0, 0, 0, 0));
        t.setForeground(fgPrimary());
        t.setSelectionBackground(new Color(ACCENT.getRed(), ACCENT.getGreen(), ACCENT.getBlue(), 55));
        t.setSelectionForeground(fgPrimary());
        t.setGridColor(ThemeManager.isDark() ? new Color(255, 255, 255, 18) : new Color(0, 0, 0, 12));
        t.setIntercellSpacing(new Dimension(0, 1));

        // Header
        JTableHeader hdr = t.getTableHeader();
        hdr.setOpaque(false);
        hdr.setBackground(ThemeManager.isDark() ? new Color(40, 40, 65) : new Color(235, 235, 250));
        hdr.setForeground(fgSecondary());
        hdr.setFont(FONT_SMALL);
        hdr.setPreferredSize(new Dimension(0, 42));
        hdr.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0,
                ThemeManager.isDark() ? new Color(255, 255, 255, 25) : new Color(0, 0, 0, 12)));
        hdr.setReorderingAllowed(false);

        // Custom cell renderer with padding
        t.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBorder(new EmptyBorder(0, 14, 0, 14));
                setFont(FONT_BODY);
                if (!isSelected) {
                    setForeground(fgPrimary());
                    setBackground(new Color(0, 0, 0, 0));
                }
                return this;
            }
        });
    }

    public static void styleScroll(JScrollPane sp) {
        sp.setOpaque(false);
        sp.getViewport().setOpaque(false);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.getVerticalScrollBar().setUnitIncrement(16);
        sp.getHorizontalScrollBar().setUnitIncrement(16);
    }

    // ═════════════════════════════════════════════════════════
    //  STAT CARD — Dashboard Metric Card
    // ═════════════════════════════════════════════════════════
    public static JPanel statCard(String icon, String title, String value, Color accent) {
        GlassPanel c = new GlassPanel(24) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Subtle glow of accent color
                g2.setPaint(new GradientPaint(0, 0, new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 30),
                        0, getHeight(), new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 0)));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 24, 24);
                g2.dispose();
            }
        };
        c.setLayout(new BorderLayout(20, 0));
        c.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 6, 0, 0, accent),
                new EmptyBorder(25, 28, 25, 28)));

        JLabel li = new JLabel(icon);
        li.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));

        JPanel text = new JPanel(new GridLayout(2, 1, 0, 2));
        text.setOpaque(false);

        JLabel lt = new JLabel(title.toUpperCase());
        lt.setFont(FONT_SMALL);
        lt.setForeground(fgSecondary());

        JLabel lv = new JLabel(value);
        lv.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lv.setForeground(fgPrimary());

        text.add(lt);
        text.add(lv);

        c.add(li, BorderLayout.WEST);
        c.add(text, BorderLayout.CENTER);
        return c;
    }

    // ═════════════════════════════════════════════════════════
    //  BADGE — Small Status Label
    // ═════════════════════════════════════════════════════════
    public static JLabel badge(String text, Color bg, Color fg) {
        JLabel l = new JLabel(text.toUpperCase(), SwingConstants.CENTER) {
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
        l.setOpaque(false);
        l.setBackground(bg);
        l.setForeground(fg);
        l.setFont(new Font("Segoe UI", Font.BOLD, 10));
        l.setBorder(new EmptyBorder(4, 12, 4, 12));
        return l;
    }

    // ═════════════════════════════════════════════════════════
    //  TOP BAR — Page Section Header
    // ═════════════════════════════════════════════════════════
    public static JPanel topBar(String icon, String title, String subtitle, Color titleColor) {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setOpaque(false);
        bar.setBorder(new EmptyBorder(10, 5, 25, 5));

        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setOpaque(false);

        JLabel lblTitle = new JLabel(icon + "  " + title);
        lblTitle.setFont(FONT_H2);
        lblTitle.setForeground(titleColor);

        JLabel lblSub = new JLabel(subtitle);
        lblSub.setFont(FONT_BODY);
        lblSub.setForeground(fgMuted());

        left.add(lblTitle);
        left.add(gap(4));
        left.add(lblSub);

        bar.add(left, BorderLayout.WEST);
        return bar;
    }

    // ═════════════════════════════════════════════════════════
    //  WINDOW BAR — Custom Title Bar for Undecorated Frames
    // ═════════════════════════════════════════════════════════
    public static JPanel windowBar(JFrame frame, String title) {
        JPanel bar = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(ThemeManager.isDark()
                        ? new Color(18, 18, 35, 220)
                        : new Color(230, 230, 248, 220));
                g2.fillRect(0, 0, getWidth(), getHeight());
                // Bottom separator
                g2.setColor(ThemeManager.isDark()
                        ? new Color(255, 255, 255, 15)
                        : new Color(0, 0, 0, 8));
                g2.fillRect(0, getHeight() - 1, getWidth(), 1);
                g2.dispose();
            }
        };
        bar.setOpaque(false);
        bar.setPreferredSize(new Dimension(0, 42));
        bar.setBorder(new EmptyBorder(0, 18, 0, 5));

        JLabel lblTitle = new JLabel("●  " + title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblTitle.setForeground(ThemeManager.isDark() ? new Color(200, 200, 220) : new Color(80, 80, 100));
        bar.add(lblTitle, BorderLayout.WEST);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 2, 5));
        controls.setOpaque(false);

        JButton btnMin = windowCtrlBtn("─", fgSecondary(), new Color(255, 255, 255, 0));
        btnMin.addActionListener(e -> frame.setState(Frame.ICONIFIED));

        JButton btnMax = windowCtrlBtn("□", fgSecondary(), new Color(255, 255, 255, 0));
        btnMax.addActionListener(e -> {
            if (frame.getExtendedState() == Frame.MAXIMIZED_BOTH)
                frame.setExtendedState(Frame.NORMAL);
            else
                frame.setExtendedState(Frame.MAXIMIZED_BOTH);
        });

        JButton btnClose = windowCtrlBtn("✕", RED, new Color(RED.getRed(), RED.getGreen(), RED.getBlue(), 30));
        btnClose.addActionListener(e ->
                frame.dispatchEvent(new java.awt.event.WindowEvent(
                        frame, java.awt.event.WindowEvent.WINDOW_CLOSING)));

        controls.add(btnMin);
        controls.add(btnMax);
        controls.add(btnClose);
        bar.add(controls, BorderLayout.EAST);

        // Draggable title bar
        MouseAdapter ma = new MouseAdapter() {
            private Point offset;
            @Override
            public void mousePressed(MouseEvent e) { offset = e.getPoint(); }
            @Override
            public void mouseDragged(MouseEvent e) {
                if (frame.getExtendedState() != Frame.MAXIMIZED_BOTH) {
                    Point loc = frame.getLocation();
                    frame.setLocation(loc.x + e.getX() - offset.x,
                                      loc.y + e.getY() - offset.y);
                }
            }
        };
        bar.addMouseListener(ma);
        bar.addMouseMotionListener(ma);

        return bar;
    }

    /** Small window control button (min/max/close) */
    private static JButton windowCtrlBtn(String text, Color fg, Color hoverBg) {
        JButton b = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                if (getModel().isRollover()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(hoverBg.getAlpha() > 0 ? hoverBg : new Color(255, 255, 255, 20));
                    g2.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 8, 8);
                    g2.dispose();
                }
                super.paintComponent(g);
            }
        };
        b.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        b.setForeground(fg);
        b.setPreferredSize(new Dimension(36, 30));
        b.setOpaque(false);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    // ═════════════════════════════════════════════════════════
    //  FORM LABEL — Consistent Field Label
    // ═════════════════════════════════════════════════════════
    public static JLabel formLabel(String text) {
        JLabel l = new JLabel(text.toUpperCase());
        l.setFont(FONT_SMALL);
        l.setForeground(fgMuted());
        return l;
    }

    // ═════════════════════════════════════════════════════════
    //  UTILITY
    // ═════════════════════════════════════════════════════════
    public static Component gap(int h) {
        return Box.createRigidArea(new Dimension(0, h));
    }

    public static Dimension maxField() {
        return new Dimension(Integer.MAX_VALUE, 45);
    }

    /** Brighten a color by adding amount to each channel */
    public static Color brighten(Color c, int amount) {
        return new Color(
                Math.min(255, c.getRed()   + amount),
                Math.min(255, c.getGreen() + amount),
                Math.min(255, c.getBlue()  + amount),
                c.getAlpha());
    }
}
