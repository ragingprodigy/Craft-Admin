package ng.softcom.bespoke.craftadmin.models;

/**
 * Created by oladapo on 27/04/2016.
 * as part of ng.softcom.bespoke.craftadmin.models in Craft Admin
 */
public class CALogin {
    String username;
    String password;

    /**
     * Public Constrictor
     *
     * @param username String
     * @param password String
     */
    public CALogin(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }
}
