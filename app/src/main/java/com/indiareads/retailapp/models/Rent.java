package com.indiareads.retailapp.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.io.Serializable;

/**
 * Created by vijay on 10/8/2015.
 */
public class Rent implements Serializable {

    String rent[]=new String[4];
    String bookID;
    String mrp;
    String initialPayable;
    String availablityStatus;
    String merchantLibrary;
    String bookLibrary;
    String procurementTime;

    public String getMerchantLibrary() {
        return merchantLibrary;
    }

    public void setMerchantLibrary(String merchantLibrary) {
        this.merchantLibrary = merchantLibrary;
    }

    public String getBookLibrary() {
        return bookLibrary;
    }

    public void setBookLibrary(String bookLibrary) {
        this.bookLibrary = bookLibrary;
    }

    public String getProcurementTime() {
        return procurementTime;
    }

    public void setProcurementTime(String procurementTime) {
        this.procurementTime = procurementTime;
    }



    public String getSavePer() {
        return savePer;
    }

    public void setSavePer(String savePer) {
        this.savePer = savePer;
    }

    String savePer;



    public String getAvailablityStatus() {
        return availablityStatus;
    }

    public void setAvailablityStatus(String availablityStatus) {
        this.availablityStatus = availablityStatus;
    }





    public String[] getRent() {
        return rent;
    }
    public void setRent(String []rent) {
        this.rent = rent;
    }


    public String getBookId() {
        return bookID;
    }
    public void setBookId(String bookId) {
        this.bookID = bookId;
    }

    public String getMrp() {
        return mrp;
    }

    public void setMrp(String mrp) {
        this.mrp = mrp;
    }


    public void setInitialPayable(String initialPayable){
        this.initialPayable=initialPayable;
    }

    public String getInitialPayable(){
        return  initialPayable;
    }



}
