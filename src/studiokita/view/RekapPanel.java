package studiokita.view;

import studiokita.UIKit;
import studiokita.controller.TransaksiController;
import studiokita.model.JasaFoto;
import studiokita.model.SewaAlat;
import studiokita.model.Transaksi;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * RekapPanel — Premium Custom Edition (Glassmorphism & Advanced Filter). Log
 * riwayat seluruh aktivitas studio khusus untuk Admin.
 */
public class RekapPanel extends JPanel implements MainFrame.Refreshable {

    private JTable tabel;
    private DefaultTableModel model;
    private JComboBox<String> cmbFilter;

    public RekapPanel() {
        initComponents();
        refresh();
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 0));
        setOpaque(false);

        // Header
        add(UIKit.topBar("REKAPITULASI TRANSAKSI", "Log riwayat seluruh aktivitas dan pesanan masuk di Studio Kita", UIKit.BLUE), BorderLayout.NORTH);

        JPanel centerArea = new JPanel(new BorderLayout(0, 15));
        centerArea.setOpaque(false);
        centerArea.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        // 1. Panel Kontrol (Filter & Refresh)
        centerArea.add(buildControlPanel(), BorderLayout.NORTH);

        // 2. Area Tabel Transaksi
        centerArea.add(buildTableArea(), BorderLayout.CENTER);

        add(centerArea, BorderLayout.CENTER);
    }

    private JPanel buildControlPanel() {
        JPanel p = UIKit.glassCard();
        p.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 10));

        JLabel lblFilter = new JLabel("Filter Status:");
        lblFilter.setFont(UIKit.FONT_BOLD);
        lblFilter.setForeground(Color.WHITE);
        p.add(lblFilter);

        // Dropdown Filter
        String[] opsiFilter = {"SEMUA", "PENDING", "APPROVED", "SELESAI", "DECLINED"};
        cmbFilter = new JComboBox<>(opsiFilter);
        cmbFilter.setFont(UIKit.FONT_REGULAR);
        cmbFilter.setPreferredSize(new Dimension(150, 35));
        cmbFilter.addActionListener(e -> refresh());
        p.add(cmbFilter);

        // Spacer agar tombol Refresh ada di kanan
        p.add(Box.createHorizontalStrut(20));

        JButton btnRefresh = UIKit.btn("REFRESH DATA", UIKit.BLUE);
        btnRefresh.setPreferredSize(new Dimension(160, 35));
        btnRefresh.addActionListener(e -> refresh());
        p.add(btnRefresh);

        return p;
    }

    private JPanel buildTableArea() {
        JPanel p = UIKit.glassCard();
        p.setLayout(new BorderLayout());

        String[] kol = {"ID TRX", "TANGGAL", "PELANGGAN", "KATEGORI", "ITEM / PAKET", "STATUS", "TOTAL BIAYA"};
        model = new DefaultTableModel(kol, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        tabel = new JTable(model);
        UIKit.styleTable(tabel);

        // Menyesuaikan lebar kolom agar lebih proporsional
        tabel.getColumnModel().getColumn(0).setPreferredWidth(80);  // ID TRX
        tabel.getColumnModel().getColumn(1).setPreferredWidth(100); // TANGGAL
        tabel.getColumnModel().getColumn(2).setPreferredWidth(150); // PELANGGAN
        tabel.getColumnModel().getColumn(3).setPreferredWidth(100); // KATEGORI
        tabel.getColumnModel().getColumn(4).setPreferredWidth(200); // ITEM/PAKET
        tabel.getColumnModel().getColumn(5).setPreferredWidth(100); // STATUS
        tabel.getColumnModel().getColumn(6).setPreferredWidth(120); // TOTAL BIAYA

        JScrollPane sp = new JScrollPane(tabel);
        sp.setOpaque(false);
        sp.getViewport().setOpaque(false);
        sp.setBorder(BorderFactory.createEmptyBorder());

        p.add(sp, BorderLayout.CENTER);
        return p;
    }

    @Override
    public void refresh() {
        List<Transaksi> list = TransaksiController.getAll();
        model.setRowCount(0);

        String filterTerpilih = cmbFilter.getSelectedItem().toString();

        for (Transaksi t : list) {
            String idTrx = t.getIdTransaksi();
            String tgl = t.getTglInput().toString();
            String pelanggan = t.getCustomer().getNamaLengkap();
            String kategori = t.getJenisLayanan();
            String item = "-";
            String status = t.getStatus();
            String total = "Rp " + String.format("%,.0f", t.getTotalBiaya());

            // Identifikasi detail layanan dan status Selesai untuk sewa
            if (t.getLayanan() instanceof SewaAlat s) {
                item = s.getNamaKamera() + " (" + s.getDurasi() + " Hari)";
                if (s.getTglDikembalikan() != null) {
                    status = "SELESAI";
                }
            } else if (t.getLayanan() instanceof JasaFoto j) {
                item = j.getPaket() + " (Oleh " + j.getFotografer() + ")";
            }

            // Logika Filter
            if (!filterTerpilih.equals("SEMUA") && !filterTerpilih.equals(status)) {
                continue; // Lewati baris ini jika tidak cocok dengan filter
            }

            model.addRow(new Object[]{
                idTrx, tgl, pelanggan, kategori, item, status, total
            });
        }
    }
}
