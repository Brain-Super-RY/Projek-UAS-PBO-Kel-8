package studiokita.model;

import java.time.LocalDate;

public class Transaksi {

    private int id;
    private String idTransaksi;
    private Customer customer;
    private Layanan layanan;
    private String jenisLayanan;   // "SEWA" atau "JASA"
    private double totalBiaya;
    private LocalDate tglInput;
    private String status = "PENDING"; // TAMBAHAN BARU: Default status pesanan

    public Transaksi() {
    }

    public Transaksi(String idTransaksi, Customer customer, Layanan layanan) {
        this.idTransaksi = idTransaksi;
        this.customer = customer;
        this.layanan = layanan;
        this.totalBiaya = layanan.hitungBiaya();
        this.tglInput = LocalDate.now();
        this.jenisLayanan = (layanan instanceof SewaAlat) ? "SEWA" : "JASA";
        this.status = "PENDING"; // Pastikan pesanan baru selalu PENDING
    }

    public int getId() {
        return id;
    }

    public void setId(int v) {
        id = v;
    }

    public String getIdTransaksi() {
        return idTransaksi;
    }

    public void setIdTransaksi(String v) {
        idTransaksi = v;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer v) {
        customer = v;
    }

    public Layanan getLayanan() {
        return layanan;
    }

    public void setLayanan(Layanan v) {
        layanan = v;
    }

    public String getJenisLayanan() {
        return jenisLayanan;
    }

    public void setJenisLayanan(String v) {
        jenisLayanan = v;
    }

    public double getTotalBiaya() {
        return totalBiaya;
    }

    public void setTotalBiaya(double v) {
        totalBiaya = v;
    }

    public LocalDate getTglInput() {
        return tglInput;
    }

    public void setTglInput(LocalDate v) {
        tglInput = v;
    }

    // TAMBAHAN BARU: Getter dan Setter untuk Status
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
