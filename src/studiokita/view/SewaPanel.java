package studiokita.view;

import studiokita.ThemeManager;
import studiokita.UIKit;
import studiokita.controller.SewaController;
import studiokita.controller.AuthController;
import studiokita.controller.TransaksiController;
import studiokita.model.SewaAlat;
import studiokita.model.Transaksi;
import studiokita.model.Customer;
import studiokita.util.OnlineImageLoader;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 * SewaPanel — Premium Live Search Edition. UPGRADE: Fitur Katalog Real-time
 * Auto-Suggest, Filter Unit Available, Pencarian Alat Efisien, dan Integrasi
 * Sinkronisasi Global Real-time.
 */
public class SewaPanel extends JPanel implements MainFrame.Refreshable {

    private ArrayList<Transaksi> daftarSewa = new ArrayList<>();

    // Komponen Form
    private JTextField txtNamaCust, txtUserCust, txtTelp;

    // REVOLUSI KATALOG: Mengganti JComboBox kaku dengan Sistem Auto-Suggest Search Box
    private JTextField txtSearchAlat;
    private JList<String> listHasilSearch;
    private DefaultListModel<String> listModelSearch;
    private JPopupMenu popupSearch;
    private java.util.List<SewaAlat> katalogSaatIni = new ArrayList<>();
    private SewaAlat gearTerpilihSaran = null;

    private JLabel lblFoto, lblEstimasi;

    // Dropdown Tanggal G-Form Style
    private JComboBox<String> cmbTglM, cmbBlnM, cmbThnM;
    private JComboBox<String> cmbTglK, cmbBlnK, cmbThnK;

    // Komponen Layouting
    private JTable tabel;
    private DefaultTableModel model;
    private JPanel center, pnlForm, pnlTable;

    public SewaPanel() {
        initComponents();
        refresh();
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 0));
        setOpaque(false);

        add(UIKit.topBar("MANAJEMEN SEWA ALAT", "Penyewaan gear fotografi profesional bersertifikasi", UIKit.currentGold()), BorderLayout.NORTH);

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

        // Hak Akses Admin / Pengisian otomatis data login jika Customer
        if (AuthController.isAdmin()) {
            addFormLabel(p, "DATA CUSTOMER (ADMIN ONLY)");
            txtNamaCust = addLabeledField(p, "NAMA CUSTOMER", "");
            txtUserCust = addLabeledField(p, "USERNAME CUSTOMER", "");
            txtTelp = addLabeledField(p, "NO TELEPON", "");
            p.add(UIKit.gap(10));
        }

        // PERUBAHAN UTAMA: Search Engine Komponen pada Form Input
        addFormLabel(p, "CARI GEAR FOTOGRAFI (LIVE AVAILABLE)");
        txtSearchAlat = UIKit.field();
        txtSearchAlat.setText("");
        txtSearchAlat.putClientProperty("JTextField.placeholderText", "Ketik nama kamera atau lensa...");
        txtSearchAlat.setMaximumSize(UIKit.maxField());
        txtSearchAlat.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(txtSearchAlat);
        p.add(UIKit.gap(10));

        // Setup Popup Menu & List Rekomendasi di bawah Kolom Search
        listModelSearch = new DefaultListModel<>();
        listHasilSearch = new JList<>(listModelSearch);
        popupSearch = new JPopupMenu();
        JScrollPane scrollSearch = new JScrollPane(listHasilSearch);
        scrollSearch.setPreferredSize(new Dimension(280, 150));
        popupSearch.add(scrollSearch);
        popupSearch.setFocusable(false);

        // Area Penampil Foto Barang
        lblFoto = new JLabel("[ Cari & Pilih Unit Gear ]", SwingConstants.CENTER);
        lblFoto.setOpaque(true);
        lblFoto.setPreferredSize(new Dimension(280, 160));
        lblFoto.setMaximumSize(new Dimension(280, 160));
        lblFoto.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(lblFoto);
        p.add(UIKit.gap(15));

        // Input Tanggal Mulai
        addFormLabel(p, "TANGGAL MULAI SEWA");
        JPanel pMulai = new JPanel(new GridLayout(1, 3, 5, 0));
        pMulai.setOpaque(false);
        cmbTglM = createDayCombo();
        cmbBlnM = createMonthCombo();
        cmbThnM = createYearCombo();
        pMulai.add(cmbTglM);
        pMulai.add(cmbBlnM);
        pMulai.add(cmbThnM);
        pMulai.setMaximumSize(UIKit.maxField());
        pMulai.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(pMulai);
        p.add(UIKit.gap(12));

        // Input Tanggal Kembali
        addFormLabel(p, "TANGGAL KEMBALI");
        JPanel pAkhir = new JPanel(new GridLayout(1, 3, 5, 0));
        pAkhir.setOpaque(false);
        cmbTglK = createDayCombo();
        cmbBlnK = createMonthCombo();
        cmbThnK = createYearCombo();
        pAkhir.add(cmbTglK);
        pAkhir.add(cmbBlnK);
        pAkhir.add(cmbThnK);
        pAkhir.setMaximumSize(UIKit.maxField());
        pAkhir.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(pAkhir);
        p.add(UIKit.gap(20));

        // Label Auto Calculate
        lblEstimasi = new JLabel("ESTIMASI BIAYA: Rp 0");
        lblEstimasi.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblEstimasi.setForeground(UIKit.currentGold());
        lblEstimasi.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(lblEstimasi);
        p.add(UIKit.gap(15));

        // Tombol Booking
        JButton btnSimpan = UIKit.btn("BOOKING SEKARANG", UIKit.currentGold());
        btnSimpan.setMaximumSize(UIKit.maxField());
        btnSimpan.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnSimpan.addActionListener(e -> prosesBooking());
        p.add(btnSimpan);

        // Listener Otomatis Kalkulasi Tanggal
        java.awt.event.ActionListener autoCalc = e -> hitungAuto();
        cmbTglM.addActionListener(autoCalc);
        cmbBlnM.addActionListener(autoCalc);
        cmbThnM.addActionListener(autoCalc);
        cmbTglK.addActionListener(autoCalc);
        cmbBlnK.addActionListener(autoCalc);
        cmbThnK.addActionListener(autoCalc);

        // EVENT LISTENER 1: Real-time Listener saat Customer/Admin mengetik keyword nama barang
        txtSearchAlat.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                String text = txtSearchAlat.getText().trim();
                if (text.isEmpty()) {
                    popupSearch.setVisible(false);
                    return;
                }

                // Ambil data katalog available via jembatan baru Controller
                katalogSaatIni = SewaController.dapatkanKatalogCari(text);
                listModelSearch.clear();

                if (!katalogSaatIni.isEmpty()) {
                    for (SewaAlat alat : katalogSaatIni) {
                        listModelSearch.addElement(alat.getNamaKamera() + " [" + alat.getJenisAlat() + "]");
                    }
                    popupSearch.show(txtSearchAlat, 0, txtSearchAlat.getHeight());
                    txtSearchAlat.requestFocus();
                } else {
                    listModelSearch.addElement("Tidak ada unit available");
                    popupSearch.show(txtSearchAlat, 0, txtSearchAlat.getHeight());
                }
            }
        });

        // EVENT LISTENER 2: Eksekusi saat salah satu hasil pencariannya diklik
        listHasilSearch.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int idx = listHasilSearch.getSelectedIndex();
                if (idx >= 0 && idx < katalogSaatIni.size()) {
                    gearTerpilihSaran = katalogSaatIni.get(idx);

                    // Kunci teks di input field pencarian
                    txtSearchAlat.setText(gearTerpilihSaran.getNamaKamera());
                    popupSearch.setVisible(false);

                    // STREAMING GAMBAR: Ambil link online langsung dari tabel master alat yang ditunjuk
                    OnlineImageLoader.loadInto(lblFoto, gearTerpilihSaran.getFotoUrl(), 280, 160);

                    hitungAuto();
                }
            }
        });

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

        String[] cols = {"ID TRX", "CUSTOMER", "GEAR", "TGL MULAI", "TGL KEMBALI", "STATUS", "TOTAL"};
        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        tabel = new JTable(model);
        UIKit.styleTable(tabel);

        // INTEGRASI LOADER SISI TABEL RIWAYAT: Klik baris riwayat akan langsung memanggil gambar alatnya
        tabel.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = tabel.getSelectedRow();
                // PROTEKSI: Cek batas indeks riwayat array agar tidak OutOfBounds pasca-clear/refresh
                if (row >= 0 && row < daftarSewa.size()) {
                    Transaksi t = daftarSewa.get(row);
                    if (t != null && t.getLayanan() instanceof SewaAlat s) {
                        OnlineImageLoader.loadInto(lblFoto, s.getFotoUrl(), 280, 160);
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(tabel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        pnlTable.add(scrollPane, BorderLayout.CENTER);

        // Panel Baris Tombol Aksi Bawah
        JPanel pnlAksi = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlAksi.setOpaque(false);

        JButton btnCetak = UIKit.btn("CETAK NOTA", UIKit.currentGold());
        btnCetak.addActionListener(e -> aksiCetakNota());
        pnlAksi.add(btnCetak);

        if (AuthController.isAdmin()) {
            JButton btnApprove = UIKit.btn("APPROVE", UIKit.currentGold());
            JButton btnDecline = UIKit.btn("TOLAK", UIKit.RED);
            JButton btnKembali = UIKit.btn("TERIMA PENGEMBALIAN", new Color(34, 139, 34));

            btnApprove.addActionListener(e -> updateStatusTrx("APPROVED"));
            btnDecline.addActionListener(e -> updateStatusTrx("DECLINED"));
            btnKembali.addActionListener(e -> aksiTerimaPengembalian());

            pnlAksi.add(btnApprove);
            pnlAksi.add(btnDecline);
            pnlAksi.add(btnKembali);
        }

        pnlTable.add(pnlAksi, BorderLayout.SOUTH);
        return pnlTable;
    }

    private void hitungAuto() {
        try {
            if (cmbThnM.getSelectedItem() == null || cmbTglK.getSelectedItem() == null || gearTerpilihSaran == null) {
                return;
            }
            String tglM = cmbThnM.getSelectedItem() + "-" + cmbBlnM.getSelectedItem() + "-" + cmbTglM.getSelectedItem();
            String tglK = cmbThnK.getSelectedItem() + "-" + cmbBlnK.getSelectedItem() + "-" + cmbTglK.getSelectedItem();

            // Kalkulasi dinamis berdasarkan tarif harian objek katalog hasil ketikan search
            LocalDate d1 = LocalDate.parse(tglM);
            LocalDate d2 = LocalDate.parse(tglK);
            long durasi = java.time.temporal.ChronoUnit.DAYS.between(d1, d2);
            if (durasi <= 0) {
                durasi = 1; // Minimal hitungan satu hari
            }
            double est = durasi * gearTerpilihSaran.getTarifPerHari();
            lblEstimasi.setText("ESTIMASI BIAYA: Rp " + String.format("%,.0f", est));
        } catch (Exception ex) {
            lblEstimasi.setText("ESTIMASI BIAYA: Rp 0");
        }
    }

    private void prosesBooking() {
        if (cmbThnM.getSelectedItem() == null || gearTerpilihSaran == null) {
            JOptionPane.showMessageDialog(this, "Silakan cari dan pilih unit kamera yang tersedia terlebih dahulu!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String tglM = cmbThnM.getSelectedItem() + "-" + cmbBlnM.getSelectedItem() + "-" + cmbTglM.getSelectedItem();
        String tglK = cmbThnK.getSelectedItem() + "-" + cmbBlnK.getSelectedItem() + "-" + cmbTglK.getSelectedItem();

        String nama = AuthController.isAdmin() ? txtNamaCust.getText() : AuthController.getCurrentUser().getNamaLengkap();
        String user = AuthController.isAdmin() ? txtUserCust.getText() : AuthController.getCurrentUser().getUsername();
        String telp = AuthController.isAdmin() ? txtTelp.getText() : "08123456789";

        if (!AuthController.isAdmin() && AuthController.getCurrentUser() instanceof Customer c) {
            if (c.getNoTelepon() != null && !c.getNoTelepon().isBlank()) {
                telp = c.getNoTelepon();
            }
        }

        // Ambil data nama dan pengenal dari objek hasil pencarian
        String namaAlat = gearTerpilihSaran.getNamaKamera();

        // Catatan: index diarahkan ke index model aslinya di core controller
        String kategoriAlat = gearTerpilihSaran.getJenisAlat();

        int indexAsli = -1;
        for (int i = 0; i < SewaController.JENIS_ALAT.length; i++) {
            // Cocokkan kategori dari database dengan array kategori di controller
            if (SewaController.JENIS_ALAT[i].equalsIgnoreCase(kategoriAlat)) {
                indexAsli = i;
                break;
            }
        }

        // Jika tidak cocok dengan kategori manapun, beri fallback aman ke 0
        if (indexAsli == -1) {
            indexAsli = 0;
        }

        String hasil = SewaController.simpanSewa(nama, user, telp, indexAsli, namaAlat, tglM, tglK);

        if (hasil.startsWith("OK")) {
            String[] part = hasil.split("\\|");
            JOptionPane.showMessageDialog(this,
                    "Pesanan " + namaAlat + " berhasil dicatat!\nEstimasi Biaya Akhir: Rp "
                    + String.format("%,.0f", Double.parseDouble(part[1])) + "\nMenunggu approval Admin.",
                    "Booking Sukses", JOptionPane.INFORMATION_MESSAGE);

            txtSearchAlat.setText("");
            gearTerpilihSaran = null;
            if (AuthController.isAdmin() && txtUserCust != null) {
                txtUserCust.setText("");
                txtNamaCust.setText("");
                txtTelp.setText("");
            }
            triggerGlobalRefresh();
        } else if (hasil.startsWith("BENTROK")) {
            String[] part = hasil.split("\\|");
            JOptionPane.showMessageDialog(this, part[1], "Jadwal Unit Penuh", JOptionPane.ERROR_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, hasil, "Validasi Gagal", JOptionPane.WARNING_MESSAGE);
        }
    }

    // =========================================================================
    // UPGRADE METHOD: Update Status Transaksi dengan Proteksi Validasi Mutlak
    // =========================================================================
    private void updateStatusTrx(String statusBaru) {
        int row = tabel.getSelectedRow();
        if (row >= 0) {
            String idTrx = tabel.getValueAt(row, 0).toString();
            String statusSaatIni = tabel.getValueAt(row, 5).toString();

            // PROTEKSI INTERSEPSI: Mencegah perubahan status jika transaksi sudah berstatus selesai
            if ("SELESAI".equalsIgnoreCase(statusSaatIni)) {
                JOptionPane.showMessageDialog(this,
                        "Transaksi ini sudah SELESAI (alat telah dikembalikan) dan tidak dapat diubah lagi!",
                        "Akses Ditolak", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Pemicu Update ke Database via Jalur Utama Controller
            if (TransaksiController.updateStatusPesanan(idTrx, statusBaru)) {
                JOptionPane.showMessageDialog(this, "Status pesanan " + idTrx + " berhasil diubah menjadi " + statusBaru, "Sukses", JOptionPane.INFORMATION_MESSAGE);
                triggerGlobalRefresh();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal mengubah status pesanan. Cek koneksi Database.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Silakan klik baris transaksi di tabel terlebih dahulu!", "Peringatan", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void aksiTerimaPengembalian() {
        int row = tabel.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Pilih baris sewa aktif yang ingin dikembalikan!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Transaksi t = daftarSewa.get(row);
        if (t == null || !(t.getLayanan() instanceof SewaAlat s)) {
            return;
        }

        if (s.getTglDikembalikan() != null) {
            JOptionPane.showMessageDialog(this, "Gear ini sudah berstatus dikembalikan sebelumnya!", "Informasi", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        if (!t.getStatus().equalsIgnoreCase("APPROVED")) {
            JOptionPane.showMessageDialog(this, "Hanya pesanan berstatus 'APPROVED' yang dapat diproses pengembaliannya!", "Akses Ditolak", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String tglHariIni = LocalDate.now().toString();
        String inputTgl = JOptionPane.showInputDialog(this, "Masukkan Tanggal Pengembalian Aktual (yyyy-MM-dd):", tglHariIni);

        if (inputTgl == null || inputTgl.trim().isBlank()) {
            return;
        }

        String hasil = SewaController.prosesKembalikan(s.getIdLayanan(), inputTgl);
        if (hasil.startsWith("OK")) {
            String[] part = hasil.split("\\|");
            long hari = Long.parseLong(part[1]);
            double denda = Double.parseDouble(part[2]);

            if (hari > 0) {
                JOptionPane.showMessageDialog(this,
                        "Pengembalian Sukses!\nTerlambat: " + hari + " Hari\nDenda Penalti: Rp " + String.format("%,.0f", denda),
                        "Terlambat Mengembalikan Alat", JOptionPane.WARNING_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Pengembalian Sukses! Alat dikembalikan tepat waktu tanpa denda.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            }
            triggerGlobalRefresh();
        } else {
            JOptionPane.showMessageDialog(this, hasil, "Gagal", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Mengirim trigger pembaruan data secara menyeluruh ke semua panel via MainFrame
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
            String gear = model.getValueAt(row, 2).toString();
            String tglM = model.getValueAt(row, 3).toString();
            String tglK = model.getValueAt(row, 4).toString();
            String status = model.getValueAt(row, 5).toString();
            String total = model.getValueAt(row, 6).toString();

            File file = new File("Nota_Sewa_" + idTrx + ".txt");
            PrintWriter pw = new PrintWriter(new FileWriter(file));

            pw.println("=========================================");
            pw.println("                STUDIO KITA               ");
            pw.println("       Premium Photography & Gear Rent     ");
            pw.println("=========================================");
            pw.println(" ID TRANSAKSI : " + idTrx);
            pw.println(" CUSTOMER     : " + customer);
            pw.println(" STATUS TRANG : " + status);
            pw.println(" WAKTU CETAK  : " + java.time.LocalDateTime.now().toString().substring(0, 19));
            pw.println("-----------------------------------------");
            pw.println(" RINCIAN ITEM:");
            pw.println(" - " + gear);
            pw.println("   Mulai Sewa : " + tglM);
            pw.println("   Target Selesai: " + tglK);
            pw.println("-----------------------------------------");
            pw.println(" TOTAL BIAYA  : " + total);
            pw.println("=========================================");
            pw.println("    Terima kasih telah mempercayakan     ");
            pw.println("     kebutuhan produksi Anda kepada      ");
            pw.println("                STUDIO KITA               ");
            pw.println("=========================================");
            pw.close();

            JOptionPane.showMessageDialog(this, "Nota berhasil diekspor ke file lokal!\nLokasi: " + file.getAbsolutePath(), "Ekspor Berhasil", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal mencetak berkas nota: " + ex.getMessage(), "Error File", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void refresh() {
        if (lblFoto != null) {
            lblFoto.setForeground(UIKit.fgPrimary());
            lblFoto.setBackground(ThemeManager.isDark() ? new Color(255, 255, 255, 20) : new Color(0, 0, 0, 15));
            lblFoto.setBorder(BorderFactory.createLineBorder(ThemeManager.isDark() ? new Color(212, 160, 23, 50) : new Color(160, 115, 15, 40)));
        }
        if (lblEstimasi != null) {
            lblEstimasi.setForeground(UIKit.currentGold());
        }
        if (tabel != null) {
            UIKit.styleTable(tabel);
            tabel.repaint();
        }

        // Hubungkan semua dropdown tanggal ke style engine UIKit
        if (cmbTglM != null) {
            UIKit.styleComboBox(cmbTglM);
        }
        if (cmbBlnM != null) {
            UIKit.styleComboBox(cmbBlnM);
        }
        if (cmbThnM != null) {
            UIKit.styleComboBox(cmbThnM);
        }
        if (cmbTglK != null) {
            UIKit.styleComboBox(cmbTglK);
        }
        if (cmbBlnK != null) {
            UIKit.styleComboBox(cmbBlnK);
        }
        if (cmbThnK != null) {
            UIKit.styleComboBox(cmbThnK);
        }

        // Tarik data riwayat transaksi terupdate
        try {
            if (AuthController.isAdmin()) {
                daftarSewa = new ArrayList<>(SewaController.getAllSewa());
            } else {
                daftarSewa = new ArrayList<>(SewaController.getSewaByCustomer(AuthController.getCurrentUser().getUsername()));
            }
        } catch (Exception e) {
            daftarSewa = new ArrayList<>();
        }

        model.setRowCount(0);
        for (Transaksi t : daftarSewa) {
            if (t == null || !(t.getLayanan() instanceof SewaAlat s)) {
                continue;
            }

            String namaPelanggan = "Guest";
            if (t.getCustomer() != null) {
                namaPelanggan = t.getCustomer().getNamaLengkap();
            } else if (AuthController.getCurrentUser() != null) {
                namaPelanggan = AuthController.getCurrentUser().getNamaLengkap();
            }

            String st = (s.getTglDikembalikan() != null) ? "SELESAI" : t.getStatus();

            model.addRow(new Object[]{
                t.getIdTransaksi(),
                namaPelanggan,
                s.getNamaKamera(),
                s.getTglMulai(),
                s.getTglKembali(),
                st,
                "Rp " + String.format("%,.0f", s.hitungBiaya())
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
