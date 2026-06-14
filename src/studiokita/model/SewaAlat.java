package studiokita.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class SewaAlat extends Layanan {

    private String jenisAlat, namaKamera, fotoUrl; // 1. Tambah variabel fotoUrl di sini
    private LocalDate tglMulai, tglKembali, tglDikembalikan;
    private double tarifPerHari;
    public static final double DENDA_PER_HARI = 50_000;

    public SewaAlat() {
    }

    // 2. Overload Constructor agar tetap mendukung pembuatan objek fleksibel
    public SewaAlat(String id, String j, String n, LocalDate m, LocalDate k, double t) {
        super(id);
        jenisAlat = j;
        namaKamera = n;
        tglMulai = m;
        tglKembali = k;
        tarifPerHari = t;
    }

    public SewaAlat(String id, String j, String n, LocalDate m, LocalDate k, double t, String fotoUrl) {
        super(id);
        jenisAlat = j;
        namaKamera = n;
        tglMulai = m;
        tglKembali = k;
        tarifPerHari = t;
        this.fotoUrl = fotoUrl;
    }

    @Override
    public String getNamaLayanan() {
        return namaKamera; 
    }

    public String getKategoriAlat() {
        return jenisAlat; 
    }

    // 3. TAMBAHKAN GETTER & SETTER FOTO URL ONLINE
    public String getFotoUrl() {
        if (fotoUrl == null || fotoUrl.isEmpty()) {
            // Gambar kamera default dari internet jika database kosong
            return "https://images.unsplash.com/photo-1516035069371-29a1b244cc32?w=500&q=80";
        }
        return fotoUrl;
    }

    public void setFotoUrl(String v) {
        this.fotoUrl = v;
    }

    public long getDurasi() {
        return (tglMulai != null && tglKembali != null) ? ChronoUnit.DAYS.between(tglMulai, tglKembali) : 0;
    }

    public long getHariTerlambat() {
        if (tglDikembalikan == null) {
            return 0;
        }
        return Math.max(0, ChronoUnit.DAYS.between(tglKembali, tglDikembalikan));
    }

    public double hitungDenda() {
        return getHariTerlambat() * DENDA_PER_HARI;
    }

    @Override
    public double hitungBiaya() {
        return getDurasi() * tarifPerHari + hitungDenda();
    }

    @Override
    public String getDeskripsi() {
        return jenisAlat + ": " + namaKamera + " (" + getDurasi() + " hari)";
    }

    public String getJenisAlat() {
        return jenisAlat;
    }

    public void setJenisAlat(String v) {
        jenisAlat = v;
    }

    public String getNamaKamera() {
        return namaKamera;
    }

    public void setNamaKamera(String v) {
        namaKamera = v;
    }

    public LocalDate getTglMulai() {
        return tglMulai;
    }

    public void setTglMulai(LocalDate v) {
        tglMulai = v;
    }

    public LocalDate getTglKembali() {
        return tglKembali;
    }

    public void setTglKembali(LocalDate v) {
        tglKembali = v;
    }

    public LocalDate getTglDikembalikan() {
        return tglDikembalikan;
    }

    public void setTglDikembalikan(LocalDate v) {
        tglDikembalikan = v;
    }

    public double getTarifPerHari() {
        return tarifPerHari;
    }

    public void setTarifPerHari(double v) {
        tarifPerHari = v;
    }
}