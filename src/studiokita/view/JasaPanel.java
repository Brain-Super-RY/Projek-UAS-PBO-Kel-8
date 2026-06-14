package studiokita.view;

import studiokita.ThemeManager;
import studiokita.UIKit;
import studiokita.controller.JasaController;
import studiokita.controller.AuthController;
import studiokita.controller.TransaksiController;
import studiokita.model.JasaFoto;
import studiokita.model.Transaksi;
import studiokita.model.Customer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 * JasaPanel — Premium Booking Edition. 
 * FIXED: Menambahkan bagan input Fotografer, Durasi Jam, Jumlah Foto Edit, 
 * dan Live Preview Biaya tanpa merusak arsitektur Dropdown Date Picker & Reflection.
 */
public class JasaPanel extends JPanel implements MainFrame.Refreshable {

    private ArrayList<Transaksi> daftarJasa = new ArrayList<>();

    // Komponen Form Utama
    private JTextField txtNamaCust, txtUserCust, txtTelp;
    private JComboBox<String> cmbPaket;
    private JComboBox<String> cmbFotografer; // Bagan Input Nama Fotografer
    private JSpinner spnJam;                // Bagan Input Durasi Jam Kerja
    private JSpinner spnFotoEdit;           // Bagan Input Jumlah Foto Diedit
    private JLabel lblEstimasi;             // Live Preview Estimasi Biaya

    // Dropdown Tanggal G-Form Style
    private JComboBox<String> cmbTgl, cmbBln, cmbThn;

    // Komponen Layouting
    private JTable tabel;
    private DefaultTableModel model;
    private JPanel center, pnlForm, pnlTable;

    public JasaPanel() {
        initComponents();
        refresh();
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 0));
        setOpaque(false);

        add(UIKit.topBar("MANAJEMEN LAYANAN JASA", "Booking fotografer dan studio kreatif profesional", UIKit.currentGold()), BorderLayout.NORTH);

        center = new JPanel(new BorderLayout(30, 15));
        center.setOpaque(false);
        center.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        pnlForm = buildForm();
        pnlTable = buildTableArea();

        center.add(pnlForm, BorderLayout.WEST);
        center.add(pnlTable, BorderLayout.CENTER);

        add(center, BorderLayout.CENTER);

        // FITUR AUTO-RESIZE RESPONSIF
        this.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                if (center == null || pnlForm == null || pnlTable == null) {
                    return;
                }
                center.removeAll();
                if (getWidth() < 950) {
                    center.add(pnlForm, BorderLayout.NORTH);
                    center.add(pnlTable, BorderLayout.CENTER);
                } else {
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
        p.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Hak Akses Admin
        if (AuthController.isAdmin()) {
            addFormLabel(p, "DATA CUSTOMER (ADMIN ONLY)");
            txtNamaCust = addLabeledField(p, "NAMA CUSTOMER", "");
            txtUserCust = addLabeledField(p, "USERNAME CUSTOMER", "");
            txtTelp = addLabeledField(p, "NO TELEPON", "");
            p.add(UIKit.gap(10));
        }

        // 1. Pilihan Paket Jasa
        addFormLabel(p, "PILIH PAKET FOTOGRAFI");
        cmbPaket = new JComboBox<>(JasaController.PAKET_FOTO);
        cmbPaket.setMaximumSize(UIKit.maxField());
        cmbPaket.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(cmbPaket);
        p.add(UIKit.gap(12));

        // 2. BAGAN INPUT BARU: Pilihan Talent / Nama Fotografer
        addFormLabel(p, "NAMA FOTOGRAFER / CREW");
        String[] daftarFotografer = {"Studio Kita Team", "Andra Lesmana (Senior)", "Rian Hidayat (Pro)", "Siti Amelia (Creative)"};
        cmbFotografer = new JComboBox<>(daftarFotografer);
        cmbFotografer.setMaximumSize(UIKit.maxField());
        cmbFotografer.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(cmbFotografer);
        p.add(UIKit.gap(12));

        // 3. BAGAN INPUT BARU: Durasi Kerja (Jam)
        addFormLabel(p, "DURASI SESI FOTO (PER JAM)");
        spnJam = new JSpinner(new SpinnerNumberModel(1, 1, 12, 1));
        spnJam.setMaximumSize(UIKit.maxField());
        spnJam.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(spnJam);
        p.add(UIKit.gap(12));

        // 4. BAGAN INPUT BARU: Jumlah Foto di-Edit
        addFormLabel(p, "JUMLAH FOTO YANG DI-EDIT");
        spnFotoEdit = new JSpinner(new SpinnerNumberModel(5, 0, 200, 5));
        spnFotoEdit.setMaximumSize(UIKit.maxField());
        spnFotoEdit.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(spnFotoEdit);
        p.add(UIKit.gap(12));

        // Input Tanggal Menggunakan Dropdown Combo
        addFormLabel(p, "TANGGAL BOOKING LAYANAN");
        JPanel pTanggal = new JPanel(new GridLayout(1, 3, 5, 0));
        pTanggal.setOpaque(false);
        cmbTgl = createDayCombo();
        cmbBln = createMonthCombo();
        cmbThn = createYearCombo();
        pTanggal.add(cmbTgl);
        pTanggal.add(cmbBln);
        pTanggal.add(cmbThn);
        pTanggal.setMaximumSize(UIKit.maxField());
        pTanggal.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(pTanggal);
        p.add(UIKit.gap(15));

        // BAGAN BARU: Real-time Live Price Calculator
        lblEstimasi = new JLabel("ESTIMASI BIAYA: Rp 0");
        lblEstimasi.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblEstimasi.setForeground(UIKit.currentGold());
        lblEstimasi.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(lblEstimasi);
        p.add(UIKit.gap(15));

        // Tombol Booking
        JButton btnSimpan = UIKit.btn("BOOKING LAYANAN", UIKit.currentGold());
        btnSimpan.setMaximumSize(UIKit.maxField());
        btnSimpan.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnSimpan.addActionListener(e -> prosesBooking());
        p.add(btnSimpan);

        // Pasang Event Listener Kalkulasi Otomatis
        cmbPaket.addActionListener(e -> hitungLiveEstimasi());
        spnJam.addChangeListener(e -> hitungLiveEstimasi());
        spnFotoEdit.addChangeListener(e -> hitungLiveEstimasi());

        // Jalankan hitungan default pertama kali dibuka
        SwingUtilities.invokeLater(this::hitungLiveEstimasi);

        return p;
    }

    private void addFormLabel(JPanel panel, String text) {
        JLabel label = new JLabel(text.toUpperCase());
        label.setFont(UIKit.FONT_SMALL);
        label.setForeground(UIKit.fgMuted());
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setMaximumSize(new Dimension(Integer.MAX_VALUE, label.getPreferredSize().height));
        panel.add(label);
        panel.add(UIKit.gap(4));
    }

    private JPanel buildTableArea() {
        if (pnlTable != null) {
            return pnlTable;
        }
        pnlTable = UIKit.glassCard();
        pnlTable.setLayout(new BorderLayout(0, 10));
        pnlTable.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        String[] cols = {"ID TRX", "CUSTOMER", "PAKET JASA", "TANGGAL BOOKING", "STATUS", "TOTAL BIAYA"};
        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        tabel = new JTable(model);
        UIKit.styleTable(tabel);

        JScrollPane scrollPane = new JScrollPane(tabel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        pnlTable.add(scrollPane, BorderLayout.CENTER);

        // Panel Tombol Aksi Sisi Kanan Bawah
        JPanel pnlAksi = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlAksi.setOpaque(false);

        JButton btnCetak = UIKit.btn("CETAK NOTA", UIKit.currentGold());
        btnCetak.addActionListener(e -> aksiCetakNota());
        pnlAksi.add(btnCetak);

        if (AuthController.isAdmin()) {
            JButton btnApprove = UIKit.btn("APPROVE", UIKit.currentGold());
            JButton btnDecline = UIKit.btn("TOLAK", UIKit.RED);

            btnApprove.addActionListener(e -> updateStatusTrx("APPROVED"));
            btnDecline.addActionListener(e -> updateStatusTrx("DECLINED"));

            pnlAksi.add(btnApprove);
            pnlAksi.add(btnDecline);
        }

        pnlTable.add(pnlAksi, BorderLayout.SOUTH);
        return pnlTable;
    }

    private void hitungLiveEstimasi() {
        try {
            int indexPaket = cmbPaket.getSelectedIndex();
            int durasiJam = (int) spnJam.getValue();
            int fotoEdit = (int) spnFotoEdit.getValue();

            double estimasi = JasaController.hitungEstimasi(indexPaket, durasiJam, fotoEdit);
            lblEstimasi.setText("ESTIMASI BIAYA: Rp " + String.format("%,.0f", estimasi));
        } catch (Exception ex) {
            lblEstimasi.setText("ESTIMASI BIAYA: Rp 0");
        }
    }

    private void prosesBooking() {
        if (cmbPaket.getSelectedItem() == null || cmbThn.getSelectedItem() == null) {
            return;
        }

        // Gabung komponen dropdown jadi string format ISO yyyy-MM-dd
        String tanggalBooking = cmbThn.getSelectedItem() + "-" + cmbBln.getSelectedItem() + "-" + cmbTgl.getSelectedItem();

        int indexPaket = cmbPaket.getSelectedIndex();
        String fotograferDipilih = cmbFotografer.getSelectedItem().toString();
        int durasiJam = (int) spnJam.getValue();
        int fotoEdit = (int) spnFotoEdit.getValue();

        String nama = AuthController.isAdmin() ? txtNamaCust.getText() : AuthController.getCurrentUser().getNamaLengkap();
        String user = AuthController.isAdmin() ? txtUserCust.getText() : AuthController.getCurrentUser().getUsername();
        String telp = AuthController.isAdmin() ? txtTelp.getText() : "08123456789";

        if (!AuthController.isAdmin() && AuthController.getCurrentUser() instanceof Customer c) {
            if (c.getNoTelepon() != null && !c.getNoTelepon().isBlank()) {
                telp = c.getNoTelepon();
            }
        }

        // FIXED SINKRONISASI PENUH: Data diambil langsung dari form dinamis Anda!
        String hasil = JasaController.simpanJasa(nama, user, telp, indexPaket, fotograferDipilih, tanggalBooking, durasiJam, fotoEdit);

        if (hasil.startsWith("OK")) {
            String[] part = hasil.split("\\|");
            JOptionPane.showMessageDialog(this,
                    "Booking " + cmbPaket.getSelectedItem().toString() + " berhasil dijadwalkan!\nTotal Biaya: Rp "
                    + String.format("%,.0f", Double.parseDouble(part[1])) + "\nMenunggu approval Admin.",
                    "Booking Sukses", JOptionPane.INFORMATION_MESSAGE);

            if (AuthController.isAdmin() && txtUserCust != null) {
                txtUserCust.setText("");
                txtNamaCust.setText("");
                txtTelp.setText("");
            }
            triggerGlobalRefresh();
        } else {
            JOptionPane.showMessageDialog(this, hasil, "Validasi Gagal", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void updateStatusTrx(String statusBaru) {
        int row = tabel.getSelectedRow();
        if (row >= 0) {
            String idTrx = tabel.getValueAt(row, 0).toString();

            if (TransaksiController.updateStatusPesanan(idTrx, statusBaru)) {
                try {
                    for (Transaksi t : daftarJasa) {
                        if (t != null && t.getIdTransaksi().equals(idTrx)) {
                            t.setStatus(statusBaru);
                            break;
                        }
                    }
                    for (Transaksi t : TransaksiController.getAll()) {
                        if (t != null && t.getIdTransaksi().equals(idTrx)) {
                            t.setStatus(statusBaru);
                            break;
                        }
                    }
                } catch (Exception ignored) {}

                JOptionPane.showMessageDialog(this, 
                        "Status pesanan " + idTrx + " berhasil diubah menjadi " + statusBaru, 
                        "Sukses", JOptionPane.INFORMATION_MESSAGE);
                
                triggerGlobalRefresh();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal mengubah status pesanan.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Silakan klik baris transaksi di tabel terlebih dahulu!", "Peringatan", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void triggerGlobalRefresh() {
        Component topLevel = SwingUtilities.getWindowAncestor(this);
        if (topLevel instanceof MainFrame mf) {
            mf.refreshSemuaPanel();
        } else {
            refresh();
        }
    }

    private void aksiCetakNota() {
        int row = tabel.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Pilih baris riwayat di tabel terlebih dahulu untuk mencetak nota!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            String idTrx = model.getValueAt(row, 0).toString();
            String customer = model.getValueAt(row, 1).toString();
            String paket = model.getValueAt(row, 2).toString();
            String tglBooking = model.getValueAt(row, 3).toString();
            String status = model.getValueAt(row, 4).toString();
            String total = model.getValueAt(row, 5).toString();

            File file = new File("Nota_Jasa_" + idTrx + ".txt");
            PrintWriter pw = new PrintWriter(new FileWriter(file));

            pw.println("=========================================");
            pw.println("                STUDIO KITA               ");
            pw.println("       Premium Photography Studio        ");
            pw.println("=========================================");
            pw.println(" ID TRANSAKSI : " + idTrx);
            pw.println(" CUSTOMER     : " + customer);
            pw.println(" STATUS TRX   : " + status);
            pw.println(" WAKTU CETAK  : " + java.time.LocalDateTime.now().toString().substring(0, 19));
            pw.println("-----------------------------------------");
            pw.println(" RINCIAN LAYANAN:");
            pw.println(" - " + paket);
            pw.println("   Tanggal Pelaksanaan: " + tglBooking);
            pw.println("-----------------------------------------");
            pw.println(" TOTAL BIAYA  : " + total);
            pw.println("=========================================");
            pw.println("    Terima kasih telah mempercayakan     ");
            pw.println("     momen berharga Anda kepada          ");
            pw.println("                STUDIO KITA               ");
            pw.println("=========================================");
            pw.close();

            JOptionPane.showMessageDialog(this, "Nota Jasa berhasil diekspor ke file lokal!\nLokasi: " + file.getAbsolutePath(), "Ekspor Berhasil", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal mencetak berkas nota: " + ex.getMessage(), "Error File", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void refresh() {
        if (cmbPaket != null) UIKit.styleComboBox(cmbPaket);
        if (cmbFotografer != null) UIKit.styleComboBox(cmbFotografer);
        if (cmbTgl != null) UIKit.styleComboBox(cmbTgl);
        if (cmbBln != null) UIKit.styleComboBox(cmbBln);
        if (cmbThn != null) UIKit.styleComboBox(cmbThn);

        if (tabel != null) {
            UIKit.styleTable(tabel);
            tabel.repaint();
        }

        try {
            if (AuthController.isAdmin()) {
                daftarJasa = new ArrayList<>(JasaController.getAllJasa());
            } else {
                daftarJasa = new ArrayList<>(JasaController.getJasaByCustomer(AuthController.getCurrentUser().getUsername()));
            }
        } catch (Exception e) {
            daftarJasa = new ArrayList<>();
        }

        model.setRowCount(0);
        for (Transaksi t : daftarJasa) {
            if (t == null || !(t.getLayanan() instanceof JasaFoto j)) {
                continue;
            }

            String namaPelanggan = "Guest";
            if (t.getCustomer() != null) {
                namaPelanggan = t.getCustomer().getNamaLengkap();
            } else if (AuthController.getCurrentUser() != null) {
                namaPelanggan = AuthController.getCurrentUser().getNamaLengkap();
            }

            // Dynamic Reflection untuk mapping properti objek
            String namaPaketJasa = "Paket Jasa";
            String[] namaMethodPaket = {"getNama", "getPaket", "getNamaPaket", "getJenisPaket", "getLayanan"};
            for (String name : namaMethodPaket) {
                try {
                    java.lang.reflect.Method m = j.getClass().getMethod(name);
                    Object res = m.invoke(j);
                    if (res != null) {
                        namaPaketJasa = res.toString();
                        break;
                    }
                } catch (Exception ignored) {}
            }

            String tglBookingJasa = "-";
            String[] namaMethodTanggal = {"getTglSesi", "getTanggalSesi", "getTanggal", "getTanggalBooking", "getWaktu"};
            for (String name : namaMethodTanggal) {
                try {
                    java.lang.reflect.Method m = j.getClass().getMethod(name);
                    Object res = m.invoke(j);
                    if (res != null) {
                        tglBookingJasa = res.toString();
                        break;
                    }
                } catch (Exception ignored) {}
            }

            model.addRow(new Object[]{
                t.getIdTransaksi(),
                namaPelanggan,
                namaPaketJasa,
                tglBookingJasa,
                t.getStatus(),
                "Rp " + String.format("%,.0f", j.hitungBiaya())
            });
        }

        this.revalidate();
        this.repaint();
    }

    private JTextField addLabeledField(JPanel p, String lbl, String ph) {
        addFormLabel(p, lbl);
        JTextField f = UIKit.field();
        f.setText(ph);
        f.setMaximumSize(UIKit.maxField());
        f.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(f);
        p.add(UIKit.gap(12));
        return f;
    }

    private JComboBox<String> createDayCombo() {
        JComboBox<String> c = new JComboBox<>();
        for (int i = 1; i <= 31; i++) {
            c.addItem(String.format("%02d", i));
        }
        return c;
    }

    private JComboBox<String> createMonthCombo() {
        return new JComboBox<>(new String[]{"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"});
    }

    private JComboBox<String> createYearCombo() {
        JComboBox<String> c = new JComboBox<>();
        int curYear = java.time.LocalDate.now().getYear();
        for (int i = curYear; i <= curYear + 5; i++) {
            c.addItem(String.valueOf(i));
        }
        return c;
    }
}