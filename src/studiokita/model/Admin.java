package studiokita.model;
public class Admin extends User {
    private String adminLevel;
    public Admin(){}
    public Admin(String u,String p,String n,String e,String al){
        super(u,p,n,"ADMIN",e);adminLevel=al;}
    public String getAdminLevel(){return adminLevel;}
    public void setAdminLevel(String v){adminLevel=v;}
}
