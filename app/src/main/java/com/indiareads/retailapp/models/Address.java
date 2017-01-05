package com.indiareads.retailapp.models;

import java.io.Serializable;

/**
 * Created by vijay on 8/11/2015.
 */
public class   Address implements Serializable {
    String address_book_id;
    String user_id;
    String fullname;
    String address_line1;
    String address_line2;
    String city;
    String state;
    String pincode;
    String phone;

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    boolean isSelected=false;

    public boolean isSelected() {
        return isSelected;
    }



    public String getAddress_book_id() {
        return address_book_id;
    }

    public void setAddress_book_id(String address_book_id) {
        this.address_book_id = address_book_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getAddress_line1() {
        return address_line1;
    }

    public void setAddress_line1(String address_line1) {
        this.address_line1 = address_line1;
    }

    public String getAddress_line2() {
        return address_line2;
    }

    public void setAddress_line2(String address_line2) {
        this.address_line2 = address_line2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

}
