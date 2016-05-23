package ng.softcom.bespoke.craftadmin.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by oladapo on 01/05/2016.
 * as part of ng.softcom.bespoke.craftadmin.models in Craft Admin
 */
public class CABank extends CABaseModel {
    @SerializedName("_id")
    String id;
    String name;

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(String id) {
        this.id = id;
    }

}
