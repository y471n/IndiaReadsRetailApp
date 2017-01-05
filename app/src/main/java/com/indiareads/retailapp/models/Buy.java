package com.indiareads.retailapp.models;

/**
 * Created by vijay on 4/6/2016.
 */
public class Buy {
    String buy_mrp;
    String discount;

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getBuy_mrp() {
        return buy_mrp;
    }

    public void setBuy_mrp(String buy_mrp) {
        this.buy_mrp = buy_mrp;
    }
}
