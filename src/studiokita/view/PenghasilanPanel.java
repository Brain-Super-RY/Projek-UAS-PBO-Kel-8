package studiokita.view;

import studiokita.UIKit;
import studiokita.controller.TransaksiController;
import studiokita.model.Transaksi;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * PenghasilanPanel — Premium Custom Edition (Glassmorphism).
 */
public class PenghasilanPanel extends JPanel implements MainFrame.Refreshable {

    private JLabel lblTotal;
    private JTable tabel;
    private DefaultTableModel model;

    public PenghasilanPanel() {
        initComponents();
        refresh();
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 0));
        setOpaque(false);

        add(UIKit.topBar("💰", "LAPORAN PENGHASILAN", "Ringkasan finansial dan omzet studio", UIKit.ACCENT), BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(0, 30));
        center.setOpaque(false);

        // Header Summary Card (Premium Glass)
        JPanel card = new UIKit.GlassPanel(28) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, 0, new Color(UIKit.ACCENT.getRed(), UIKit.ACCENT.getGreen(), UIKit.ACCENT.getBlue(), 40),
                        0, getHeight(), new Color(UIKit.ACCENT.getRed(), UIKit.ACCENT.getGreen(), UIKit.ACCENT.getBlue(), 0)));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 28, 28);
                g2.dispose();
            }
        };
        card.setLayout(new BorderLayout());
        card.setPreferredSize(new Dimension(0, 160));
        card.setBorder(new EmptyBorder(30, 40, 30, 40));

        JLabel t = new JLabel("TOTAL PENDAPATAN");
        t.setFont(UIKit.FONT_SMALL);
        t.setForeground(UIKit.fgSecondary());

        lblTotal = new JLabel("Rp 0");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 52));
        lblTotal.setForeground(UIKit.fgPrimary());

        card.add(t, BorderLayout.NORTH);
        card.add(lblTotal, BorderLayout.CENTER);
        center.add(card, BorderLayout.NORTH);

        // Table Area
        JPanel tableGlass = UIKit.glassCard();
        tableGlass.setLayout(new BorderLayout());
        String[] kol = {"ID TRX", "PELANGGAN", "LAYANAN", "TANGGAL", "NOMINAL"};
        model = new DefaultTableModel(kol, 0) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        tabel = new JTable(model);
        UIKit.styleTable(tabel);
        
        JScrollPane sp = new JScrollPane(tabel);
        sp.setOpaque(false); sp.getViewport().setOpaque(false);
        sp.setBorder(BorderFactory.createEmptyBorder());
        
        tableGlass.add(sp, BorderLayout.CENTER);
        center.add(tableGlass, BorderLayout.CENTER);
        
        add(center, BorderLayout.CENTER);
    }

    @Override public void refresh() {
        List<Transaksi> list = TransaksiController.getAll();
        model.setRowCount(0);
        double total = 0;
        for (Transaksi t : list) {
            total += t.getTotalBiaya();
            model.addRow(new Object[]{t.getIdTransaksi(), t.getCustomer().getNamaLengkap(), 
                t.getJenisLayanan(), t.getTglInput(), "Rp " + String.format("%,.0f", t.getTotalBiaya())});
        }
        lblTotal.setText("Rp " + String.format("%,.0f", total));
    }
}
