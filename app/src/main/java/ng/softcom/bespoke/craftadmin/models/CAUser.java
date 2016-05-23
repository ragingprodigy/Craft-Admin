package ng.softcom.bespoke.craftadmin.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by oladapo on 27/04/2016.
 * as part of ng.softcom.bespoke.craftadmin.models in Craft Admin
 */
public class CAUser extends CABaseModel {
    @SerializedName("_id")
    String id;
    String email;
    String username;
    String name;
    String createdAt;
    String role;

    public String getName() {
        return name;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getEmail() {
        return email;
    }

    public String getId() {
        return id;
    }

    public String getRole() {
        return role;
    }

    public String getUsername() {
        return username;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
