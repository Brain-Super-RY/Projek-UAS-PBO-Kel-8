package studiokita.view;

import studiokita.UIKit;
import studiokita.controller.TransaksiController;
import studiokita.model.Transaksi;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * RekapPanel — Premium Custom Edition (Glassmorphism).
 */
public class RekapPanel extends JPanel implements MainFrame.Refreshable {

    private JTable tabel;
    private DefaultTableModel model;

    public RekapPanel() {
        initComponents();
        refresh();
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 0));
        setOpaque(false);

        add(UIKit.topBar("📊", "REKAPITULASI TRANSAKSI", "Log riwayat seluruh aktivitas studio", UIKit.BLUE), BorderLayout.NORTH);

        JPanel centerArea = UIKit.glassCard();
        centerArea.setLayout(new BorderLayout(0, 15));

        String[] kol = {"ID", "CUSTOMER", "LAYANAN", "UNIT", "TANGGAL", "BIAYA"};
        model = new DefaultTableModel(kol, 0) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        tabel = new JTable(model);
        UIKit.styleTable(tabel);

        JScrollPane sp = new JScrollPane(tabel);
        sp.setOpaque(false); sp.getViewport().setOpaque(false);
        sp.setBorder(BorderFactory.createEmptyBorder());

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnRow.setOpaque(false);
        JButton btnRefresh = UIKit.btn("REFRESH DATA", UIKit.BLUE);
        btnRefresh.addActionListener(e -> refresh());
        btnRow.add(btnRefresh);

        centerArea.add(sp, BorderLayout.CENTER);
        centerArea.add(btnRow, BorderLayout.SOUTH);
        
        add(centerArea, BorderLayout.CENTER);
    }

    @Override public void refresh() {
        List<Transaksi> list = TransaksiController.getAll();
        model.setRowCount(0);
        for (Transaksi t : list) {
            model.addRow(new Object[]{
                t.getIdTransaksi(), t.getCustomer().getNamaLengkap(), t.getJenisLayanan(),
                t.getLayanan().getIdLayanan(), t.getTglInput(), "Rp " + String.format("%,.0f", t.getTotalBiaya())
            });
        }
    }
}
