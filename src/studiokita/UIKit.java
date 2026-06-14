package studiokita;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * UIKit — The Core UI Engine for Studio Kita (Ultra-Luxury Black & Gold
 * Edition). Mengatasi bug rendering teks bertumpuk, glitch garis, dan layouting
 * melenceng.
 */
public class UIKit {

    // -------------------------------------------------------------------------
    // TYPOGRAPHY TOKENS
    // -------------------------------------------------------------------------
    public static final Font FONT_REGULAR = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.BOLD, 11);
    public static final Font FONT_LARGE = new Font("Segoe UI", Font.BOLD, 20);

    // -------------------------------------------------------------------------
    // COLOR TOKENS
    // -------------------------------------------------------------------------
    public static final Color RED = new Color(255, 55, 95);
    public static final Color BLUE = new Color(41, 128, 185);
    public static final Color ACCENT_GOLD = new Color(212, 160, 23);

    // -------------------------------------------------------------------------
    // DYNAMIC THEME COLOR TOKENS
    // -------------------------------------------------------------------------
    public static Color currentGold() {
        return ThemeManager.isDark() ? new Color(212, 160, 23) : new Color(160, 115, 15);
    }

    public static Color fgPrimary() {
        return ThemeManager.isDark() ? new Color(245, 245, 250) : new Color(20, 20, 25);
    }

    public static Color fgMuted() {
        return ThemeManager.isDark() ? new Color(160, 160, 180) : new Color(100, 100, 115);
    }

    // -------------------------------------------------------------------------
    // LAYOUT SIZE LIMITERS (Mencegah Komponen Melenceng/Melar)
    // -------------------------------------------------------------------------
    public static Dimension maxField() {
        // Membatasi tinggi maksimum komponen agar seragam di semua layout panel
        return new Dimension(Integer.MAX_VALUE, 42);
    }

    // -------------------------------------------------------------------------
    // FACTORY METHODS (Safe Translucent Components)
    // -------------------------------------------------------------------------
    public static Component gap(int size) {
        return Box.createRigidArea(new Dimension(size, size));
    }

    /**
     * FIX BUG VISUAL: Membuat JPasswordField / JTextField kustom yang aman dari
     * isu text overlapping/garis-garis patah akibat transparansi.
     */
    public static JTextField field() {
        // Menggunakan anon class untuk menggambar background transparan secara aman via paintComponent
        JTextField f = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Bersihkan area background secara manual agar tidak berbayang/bergaris
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };

        f.setFont(FONT_REGULAR);
        f.setForeground(fgPrimary());
        f.setCaretColor(currentGold());

        // KRUSIAL: SetOpaque(false) adalah kunci agar Swing tidak membuat rendering artifak (garis hantu)
        f.setOpaque(false);

        // Definisikan warna dasar transparan komponen
        f.setBackground(ThemeManager.isDark() ? new Color(255, 255, 255, 15) : new Color(0, 0, 0, 10));

        // Border mewah melingkar tipis
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeManager.isDark() ? new Color(255, 255, 255, 25) : new Color(0, 0, 0, 30), 1, true),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));
        return f;
    }

    /**
     * FIX LAYOUT & VISUAL BUG: Tombol glassmorphism anti melar dengan handling
     * state hover presisi
     */
    public static JButton btn(String text, Color baseColor) {
        JButton b = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (getModel().isPressed()) {
                    g2.setColor(new Color(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), 75));
                } else if (getModel().isRollover()) {
                    g2.setColor(new Color(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), 50));
                } else {
                    g2.setColor(new Color(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), 25));
                }

                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setForeground(ThemeManager.isDark() ? Color.WHITE : baseColor.darker());
        b.setContentAreaFilled(false);
        b.setFocusPainted(false);
        b.setOpaque(false); // Pastikan false untuk menghindari bug kotak hitam di belakang tombol melingkar
        b.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    // -------------------------------------------------------------------------
    // STRUCTURAL & DASHBOARD PANELS
    // -------------------------------------------------------------------------
    public static JPanel topBar(String title, String subtitle, Color accentColor) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel lblTitle = new JLabel(title.toUpperCase());
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(accentColor);

        JLabel lblSub = new JLabel(subtitle);
        lblSub.setFont(FONT_REGULAR);
        lblSub.setForeground(fgMuted());

        p.add(lblTitle, BorderLayout.NORTH);
        p.add(lblSub, BorderLayout.SOUTH);
        return p;
    }

    public static JPanel glassCard() {
        return new GlassPanel(20);
    }

    public static JPanel statCard(String title, String value, Color goldColor, Runnable onClick) {
        GlassPanel card = new GlassPanel(20);
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel lblTitle = new JLabel(title.toUpperCase());
        lblTitle.setFont(FONT_SMALL);
        lblTitle.setForeground(fgMuted());

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblValue.setForeground(goldColor);

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(lblValue, BorderLayout.CENTER);

        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (onClick != null) {
                    onClick.run();
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBackground(ThemeManager.isDark() ? new Color(255, 255, 255, 25) : new Color(0, 0, 0, 20));
                card.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                card.setBackground(ThemeManager.isDark() ? new Color(255, 255, 255, 15) : new Color(0, 0, 0, 10));
                card.repaint();
            }
        });

        return card;
    }

    // -------------------------------------------------------------------------
    // PREMIUM INNER CUSTOM CLASSES
    // -------------------------------------------------------------------------
    public static class GlassPanel extends JPanel {

        private final int radius;

        public GlassPanel(int radius) {
            this.radius = radius;
            setOpaque(false);
            setBackground(ThemeManager.isDark() ? new Color(255, 255, 255, 15) : new Color(0, 0, 0, 10));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);

            g2.setColor(ThemeManager.isDark() ? new Color(255, 255, 255, 20) : new Color(0, 0, 0, 15));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);

            g2.dispose();
            super.paintComponent(g);
        }
    }

    public static class AnimatedGradientBackground extends JPanel {

        private float animationFrame = 0;

        public AnimatedGradientBackground() {
            Timer timer = new Timer(40, e -> {
                animationFrame += 0.025f;
                repaint();
            });
            timer.start();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            if (ThemeManager.isDark()) {
                g2.setColor(new Color(10, 10, 12));
                g2.fillRect(0, 0, w, h);

                double dynamicShift = Math.sin(animationFrame) * 0.1;
                float cycleX = (float) (0.3f + dynamicShift);
                float cycleY = (float) (0.2f + Math.cos(animationFrame) * 0.1);

                RadialGradientPaint dynamicGlow = new RadialGradientPaint(
                        new Point((int) (w * cycleX), (int) (h * cycleY)),
                        (int) (w * 0.8f),
                        new float[]{0.0f, 1.0f},
                        new Color[]{new Color(55, 42, 10, 80), new Color(10, 10, 12, 0)}
                );
                g2.setPaint(dynamicGlow);
                g2.fillRect(0, 0, w, h);
            } else {
                g2.setColor(new Color(242, 242, 246));
                g2.fillRect(0, 0, w, h);

                double dynamicShift = Math.sin(animationFrame) * 0.08;
                float cycleX = (float) (0.7f + dynamicShift);

                RadialGradientPaint lightGlow = new RadialGradientPaint(
                        new Point((int) (w * cycleX), (int) (h * 0.3f)),
                        (int) (w * 0.6f),
                        new float[]{0.0f, 1.0f},
                        new Color[]{new Color(212, 160, 23, 30), new Color(242, 242, 246, 0)}
                );
                g2.setPaint(lightGlow);
                g2.fillRect(0, 0, w, h);
            }

            g2.dispose();
        }
    }

    // -------------------------------------------------------------------------
    // TABLE DESIGN STYLER
    // -------------------------------------------------------------------------
    public static void styleTable(JTable table) {
        table.setOpaque(false);
        table.setFont(FONT_REGULAR);
        table.setRowHeight(35);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        Color bgSelected = ThemeManager.isDark() ? new Color(212, 160, 23, 50) : new Color(212, 160, 23, 35);
        table.setSelectionBackground(bgSelected);
        table.setSelectionForeground(fgPrimary());
        table.setForeground(fgPrimary());
        table.setBackground(new Color(0, 0, 0, 0));

        JTableHeader header = table.getTableHeader();
        header.setOpaque(false);
        header.setFont(FONT_BOLD);
        header.setPreferredSize(new Dimension(0, 40));
        header.setForeground(currentGold());
        header.setBackground(ThemeManager.isDark() ? new Color(20, 20, 25) : new Color(230, 230, 235));
    }

    // TAMBAHKAN METODE INI DI DALAM KELAS UIKit.java ANDA
    /**
     * FIX DROPDOWN BUG: Menyetel JComboBox agar teks item daftar pilihan
     * terbaca sempurna baik saat Light Mode maupun Dark Mode.
     */
    public static void styleComboBox(JComboBox<?> comboBox) {
        comboBox.setFont(FONT_REGULAR);
        comboBox.setOpaque(false);

        // Set warna teks utama dan background box
        comboBox.setForeground(fgPrimary());
        comboBox.setBackground(ThemeManager.isDark() ? new Color(30, 30, 35) : new Color(255, 255, 255));

        // Mengubah warna renderer item list di dalam popup dropdown
        comboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                label.setFont(FONT_REGULAR);
                label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

                if (isSelected) {
                    label.setBackground(currentGold());
                    label.setForeground(Color.WHITE);
                } else {
                    label.setBackground(ThemeManager.isDark() ? new Color(30, 30, 35) : Color.WHITE);
                    label.setForeground(fgPrimary());
                }
                return label;
            }
        });
    }

    /**
     * FIX SPINNER / DATE BUG: Menyetel komponen JSpinner (penyeleksi tanggal)
     * agar teks kontras
     */
    public static void styleSpinner(JSpinner spinner) {
        spinner.setFont(FONT_REGULAR);
        spinner.setOpaque(false);

        JComponent editor = spinner.getEditor();
        if (editor instanceof JSpinner.DefaultEditor defaultEditor) {
            JTextField textField = defaultEditor.getTextField();
            textField.setForeground(fgPrimary());
            textField.setCaretColor(currentGold());
            textField.setBackground(ThemeManager.isDark() ? new Color(40, 40, 45) : Color.WHITE);
        }
        spinner.setForeground(fgPrimary());
        spinner.setBackground(ThemeManager.isDark() ? new Color(40, 40, 45) : Color.WHITE);
    }
}
