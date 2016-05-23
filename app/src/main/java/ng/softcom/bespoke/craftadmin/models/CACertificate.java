package ng.softcom.bespoke.craftadmin.models;

/**
 * Created by oladapo on 30/04/2016.
 * as part of ng.softcom.bespoke.craftadmin.models in Craft Admin
 */
public class CACertificate extends CABaseModel {
    String url;
    String title;

    public CACertificate() { }

    public CACertificate(String title, String url) {
        this.title = title;
        this.url = url;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }
}
