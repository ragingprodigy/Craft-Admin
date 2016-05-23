package ng.softcom.bespoke.craftadmin.api.responses;

import ng.softcom.bespoke.craftadmin.models.CAUser;

/**
 * Created by oladapo on 27/04/2016.
 * as part of ng.softcom.bespoke.craftadmin.api in Craft Admin
 */
public class CALoginResponse extends BaseApiResponse {
    protected String message;
    protected String token;
    protected CAUser user;

    public String getMessage() {
        return message;
    }

    public String getToken() {
        return token;
    }

    public CAUser getUser() {
        return user;
    }
}
