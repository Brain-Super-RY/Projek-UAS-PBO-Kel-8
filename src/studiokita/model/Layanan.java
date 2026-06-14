package studiokita.model;

/**
 * Abstract class Layanan — Abstraction (konsep PBO)
 */
public abstract class Layanan {

    protected String idLayanan;

    public Layanan() {
    }

    public Layanan(String id) {
        idLayanan = id;
    }

    // Mengembalikan nama layanan/barang secara generik untuk tabel/dashboard
    public abstract String getNamaLayanan(); 

    public abstract double hitungBiaya();   // Polymorphism
    public abstract String getDeskripsi();  // Polymorphism

    public String getIdLayanan() {
        return idLayanan;
    }

    public void setIdLayanan(String v) {
        idLayanan = v;
    }
}