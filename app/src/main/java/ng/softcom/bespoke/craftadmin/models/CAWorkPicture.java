package ng.softcom.bespoke.craftadmin.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by oladapo on 30/04/2016.
 * as part of ng.softcom.bespoke.craftadmin.models in Craft Admin
 */
public class CAWorkPicture extends CABaseModel {
    @SerializedName("_id")
    String id;
    String title;
    String url;

    public CAWorkPicture() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public String getTitle() {
        return title;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
