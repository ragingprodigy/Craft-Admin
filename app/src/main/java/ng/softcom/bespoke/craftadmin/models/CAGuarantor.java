package ng.softcom.bespoke.craftadmin.models;

/**
 * Created by oladapo on 30/04/2016.
 * as part of ng.softcom.bespoke.craftadmin.models in Craft Admin
 */
public class CAGuarantor extends CABaseModel {
    String name;
    String phone;
    String address;

    public CAGuarantor() {

    }

    public CAGuarantor(String name, String phone, String address) {
        this.name = name;
        this.phone = phone;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
