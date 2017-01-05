package com.indiareads.retailapp.models;

/**
 * Created by vijay on 10/14/2015.
 */
public class UserProfile {

    String user_id;
    String first_name;
    String last_name;
    String alternate_email;
    String birthdate;
    String landline;
    String mobile;
    String profile_pic;
    String gender;

    public String getGender() {
        return gender;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public String getLandline() {
        return landline;
    }

    public String getMobile() {
        return mobile;
    }

    public String getProfile_pic() {
        return profile_pic;
    }

    public String getAlternate_email() {
        return alternate_email;
    }


}
