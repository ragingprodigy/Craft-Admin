package ng.softcom.bespoke.craftadmin.api.interfaces;

import java.util.List;

import ng.softcom.bespoke.craftadmin.models.CAArtisan;
import ng.softcom.bespoke.craftadmin.models.CAWorkPicture;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Created by oladapo on 03/05/2016.
 * as part of ng.softcom.bespoke.craftadmin.api.interfaces in Craft Admin
 */
public interface CAArtisanInterface {

    @POST("/api/artisans")
    Call<CAArtisan> createArtisan(@Body CAArtisan payLoad);

    @GET("/api/artisans")
    Call<List<CAArtisan>> getArtisans();

    @GET("/api/artisans/all")
    Call<List<CAArtisan>> getAllArtisans();

    @DELETE("/api/artisans/{id}")
    Call<String> deleteArtisan(@Path("id") String id);

    @POST("/api/artisans/{id}/addWorkPic")
    Call<CAArtisan> addWorkPic(@Path("id") String id, @Body CAWorkPicture workPicture);

    @PUT("/api/artisans/{id}")
    Call<CAArtisan> updateArtisan(@Path("id") String id, @Body CAArtisan payLoad);

    @DELETE("/api/artisans/{id}/{picId}")
    Call<CAArtisan> deleteWorkPic(@Path("id") String id, @Path("picId") String picId);

}
