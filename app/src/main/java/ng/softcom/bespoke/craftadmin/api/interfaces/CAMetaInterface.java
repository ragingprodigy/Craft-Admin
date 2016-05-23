package ng.softcom.bespoke.craftadmin.api.interfaces;

import java.util.List;

import ng.softcom.bespoke.craftadmin.models.CABank;
import ng.softcom.bespoke.craftadmin.models.CASpecialty;
import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by oladapo on 30/04/2016.
 * as part of ng.softcom.bespoke.craftadmin.api.interfaces in Craft Admin
 */
public interface CAMetaInterface {

    @GET("/api/meta/banks")
    Call<List<CABank>> banksList();

    @GET("/api/meta/specialties")
    Call<List<CASpecialty>> specialtiesList();
}
