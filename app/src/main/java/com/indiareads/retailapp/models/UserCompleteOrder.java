package com.indiareads.retailapp.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vijay on 11/5/2015.
 */
public class UserCompleteOrder implements Serializable {

    List<CartModel> userBooks;
    Address userAddress;
    String paymentMethod;

    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }

    String couponCode;
    int shippingCharge;
//    int codCharge;
    int couponDiscount;
    int totalPayable;
    int storeCredits;
    int parentOrderId;

    public int getStoreCreditDiscount() {
        return storeCreditDiscount;
    }

    public void setStoreCreditDiscount(int storeCreditDiscount) {
        this.storeCreditDiscount = storeCreditDiscount;
    }

    int storeCreditDiscount;

    public int getParentOrderId() {
        return parentOrderId;
    }

    public void setParentOrderId(int parentOrderId) {
        this.parentOrderId = parentOrderId;
    }




//    public int getCodCharge() {
//        return codCharge;
//    }
//
//    public void setCodCharge(int codCharge) {
//        this.codCharge = codCharge;
//    }

    public int getCouponDiscount() {
        return couponDiscount;
    }

    public void setCouponDiscount(int couponDiscount) {
        this.couponDiscount = couponDiscount;
    }

    public int getShippingCharge() {
        return shippingCharge;
    }

    public void setShippingCharge(int shippingCharge) {
        this.shippingCharge = shippingCharge;
    }

    public int getTotalPayable() {
        return totalPayable;
    }

    public void setTotalPayable(int totalPayable) {
        this.totalPayable = totalPayable;
    }



    public Address getUserAddress() {return userAddress;}
    public void setUserAddress(Address userAddress) {this.userAddress = userAddress;}

    public List<CartModel> getUserBook() {
        return userBooks;
    }

    public void setUserBook(List<CartModel> bookList) {
        if(userBooks==null){
            userBooks=new ArrayList<>();
        }
        this.userBooks = bookList;
    }

    public String getPaymentMethod() {return paymentMethod;}
    public void setPaymentMethod(String paymentMethod) {this.paymentMethod = paymentMethod;}

    public int getStoreCredits() {return storeCredits;}
    public void setStoreCredits(int storeCredits) {this.storeCredits = storeCredits;}

}
