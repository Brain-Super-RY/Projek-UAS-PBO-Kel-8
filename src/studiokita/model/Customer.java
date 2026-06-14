package studiokita.model;
public class Customer extends User {
    private String noTelepon, alamat;
    public Customer(){}
    public Customer(String u,String p,String n,String e,String t,String a){
        super(u,p,n,"CUSTOMER",e);noTelepon=t;alamat=a;}
    public String getNoTelepon(){return noTelepon;} public void setNoTelepon(String v){noTelepon=v;}
    public String getAlamat(){return alamat;} public void setAlamat(String v){alamat=v;}
}
