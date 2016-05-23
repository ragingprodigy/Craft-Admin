package ng.softcom.bespoke.craftadmin.models;

import com.google.gson.Gson;

/**
 * Created by oladapo on 27/04/2016.
 * as part of ng.softcom.bespoke.craftadmin.models in Craft Admin
 */
public class CABaseModel {

    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

}
