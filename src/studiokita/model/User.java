package studiokita.model;

public class User {

    private int id;
    private String username, password, namaLengkap, role, email;

    public User() {
    }

    public User(String username, String password, String namaLengkap, String role, String email) {
        this.username = username;
        this.password = password;
        this.namaLengkap = namaLengkap;
        this.role = role;
        this.email = email;
    }

    public boolean validatePassword(String input) {
        return password != null && password.equals(input);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String v) {
        username = v;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String v) {
        password = v;
    }

    public String getNamaLengkap() {
        return namaLengkap;
    }

    public void setNamaLengkap(String v) {
        namaLengkap = v;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String v) {
        role = v;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String v) {
        email = v;
    }

    @Override
    public String toString() {
        return namaLengkap + " (" + role + ")";
    }
}
