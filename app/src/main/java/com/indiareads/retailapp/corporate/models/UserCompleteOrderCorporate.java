package com.indiareads.retailapp.corporate.models;

import com.indiareads.retailapp.models.Address;
import com.indiareads.retailapp.models.CartModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vijay on 8/22/2016.
 */
public class UserCompleteOrderCorporate implements Serializable {

    List<CartModelCorporate> userBooks;
    Address userAddress;
    String paymentMethod;
    double totalPayable;
    int parentOrderId;


    public int getParentOrderId() {
        return parentOrderId;
    }

    public void setParentOrderId(int parentOrderId) {
        this.parentOrderId = parentOrderId;
    }


    public double getTotalPayable() {
        return totalPayable;
    }

    public void setTotalPayable(double totalPayable) {
        this.totalPayable = totalPayable;
    }



    public Address getUserAddress() {return userAddress;}
    public void setUserAddress(Address userAddress) {this.userAddress = userAddress;}

    public List<CartModelCorporate> getUserBook() {
        return userBooks;
    }

    public void setUserBook(List<CartModelCorporate> bookList) {
        if(userBooks==null){
            userBooks=new ArrayList<>();
        }
        this.userBooks = bookList;
    }

    public String getPaymentMethod() {return paymentMethod;}
    public void setPaymentMethod(String paymentMethod) {this.paymentMethod = paymentMethod;}


}

