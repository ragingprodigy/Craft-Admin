package ng.softcom.bespoke.craftadmin.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by oladapo on 30/04/2016.
 * as part of ng.softcom.bespoke.craftadmin.models in Craft Admin
 */
public class CABankDetail extends CABaseModel {
    @SerializedName("_id")
    String id;
    String nubanNumber;
    String accountName;
    CABank bank;

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public void setNubanNumber(String nubanNumber) {
        this.nubanNumber = nubanNumber;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAccountName() {
        return accountName;
    }

    public String getId() {
        return id;
    }

    public String getNubanNumber() {
        return nubanNumber;
    }

    public void setBank(CABank bank) {
        this.bank = bank;
    }

    public CABank getBank() {
        return bank;
    }
}
