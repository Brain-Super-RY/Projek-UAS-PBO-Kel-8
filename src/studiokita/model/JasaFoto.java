package studiokita.model;
import java.time.LocalDate;
public class JasaFoto extends Layanan {
    private String fotografer, paket;
    private LocalDate tglSesi;
    private int durasiJam, jumlahFotoEdit;
    private double hargaDasar;
    public JasaFoto(){}
    public JasaFoto(String id,String f,String p,LocalDate t,int d,int je,double h){
        super(id);fotografer=f;paket=p;tglSesi=t;durasiJam=d;jumlahFotoEdit=je;hargaDasar=h;}
    @Override public double hitungBiaya(){return hargaDasar+(durasiJam*100_000)+(jumlahFotoEdit*15_000);}
    @Override public String getDeskripsi(){return paket+", "+durasiJam+" jam, "+jumlahFotoEdit+" foto edit";}
    public String getFotografer(){return fotografer;} public void setFotografer(String v){fotografer=v;}
    public String getPaket(){return paket;} public void setPaket(String v){paket=v;}
    public LocalDate getTglSesi(){return tglSesi;} public void setTglSesi(LocalDate v){tglSesi=v;}
    public int getDurasiJam(){return durasiJam;} public void setDurasiJam(int v){durasiJam=v;}
    public int getJumlahFotoEdit(){return jumlahFotoEdit;} public void setJumlahFotoEdit(int v){jumlahFotoEdit=v;}
    public double getHargaDasar(){return hargaDasar;} public void setHargaDasar(double v){hargaDasar=v;}
}
