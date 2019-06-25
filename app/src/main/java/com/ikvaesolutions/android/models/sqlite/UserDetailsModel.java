package com.ikvaesolutions.android.models.sqlite;

/**
 * Created by amarilindra on 05/05/17.
 */

public class UserDetailsModel {

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public UserDetailsModel() {
    }

    public UserDetailsModel(String name, String email, String phone, String profilePicture) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.profilePicture = profilePicture;
    }

    private String name;
    private String email;
    private String phone;
    private String profilePicture;
}
