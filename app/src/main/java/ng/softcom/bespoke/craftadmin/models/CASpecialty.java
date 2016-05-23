package ng.softcom.bespoke.craftadmin.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by oladapo on 30/04/2016.
 * as part of ng.softcom.bespoke.craftadmin.models in Craft Admin
 */
public class CASpecialty extends CABaseModel {
    @SerializedName("_id")
    String id;
    String name;
    String description;

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
