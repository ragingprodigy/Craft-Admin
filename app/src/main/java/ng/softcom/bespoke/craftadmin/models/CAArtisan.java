package ng.softcom.bespoke.craftadmin.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by oladapo on 30/04/2016.
 * as part of ng.softcom.bespoke.craftadmin.models in Craft Admin
 */
public class CAArtisan extends CABaseModel {
    @SerializedName("_id")
    String id;

    String firstName;
    String middleName;
    String surname;
    String businessName;
    String phone;
    String address;
    String identification;
    String profileUrl;
    String email;

    CABankDetail bankDetails;
    List<CACertificate> certifications;
    List<CAGuarantor> guarantors;
    List<CAWorkPicture> workPictures;

    CASpecialty specialty;
    List<Double> location;

    public CAArtisan() {
        certifications = new ArrayList<>();
        guarantors = new ArrayList<>();
        workPictures = new ArrayList<>();

        location = new ArrayList<>(2);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public CABankDetail getBankDetails() {
        return bankDetails;
    }

    public List<CACertificate> getCertifications() {
        return certifications;
    }

    public List<CAGuarantor> getGuarantors() {
        return guarantors;
    }

    public List<CAWorkPicture> getWorkPictures() {
        return workPictures;
    }

    public List<Double> getLocation() {
        return location;
    }

    public String getBusinessName() {
        return businessName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getIdentification() {
        return identification;
    }

    public String getMiddleName() {
        return middleName;
    }

    public String getSurname() {
        return surname;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setBankDetails(CABankDetail bankDetails) {
        this.bankDetails = bankDetails;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public void setCertifications(List<CACertificate> certifications) {
        this.certifications = certifications;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setGuarantors(List<CAGuarantor> guarantors) {
        this.guarantors = guarantors;
    }

    public void setIdentification(String identification) {
        this.identification = identification;
    }

    public void setLocation(List<Double> location) {
        this.location = location;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setWorkPictures(List<CAWorkPicture> workPictures) {
        this.workPictures = workPictures;
    }

    public CASpecialty getSpecialty() {
        return specialty;
    }

    public void setSpecialty(CASpecialty specialty) {
        this.specialty = specialty;
    }

    public String getFullName() {
        return firstName + " " + surname;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
