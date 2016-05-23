package ng.softcom.bespoke.craftadmin.api.interfaces;

import ng.softcom.bespoke.craftadmin.api.responses.CALoginResponse;
import ng.softcom.bespoke.craftadmin.models.CALogin;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by oladapo on 27/04/2016.
 * as part of ng.softcom.bespoke.craftadmin.api in Craft Admin
 */
public interface CALoginInterface {

    @POST("/auth/login")
    Call<CALoginResponse> login(@Body CALogin user);

}
