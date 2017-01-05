package com.indiareads.retailapp.models;

import java.io.Serializable;

/**
 * Created by sunil on 20/6/15.
 */
public class Bookshelf implements Serializable {


    String title;
    String contributor_name1;
    String isbn13;
    String orderID;
    String shelf_type;
    String mrp;
    String init_pay;
    String user_id;
    String refund_amount;
    String rent;

    String rentStartDate;

    public String getBookMrp() {
        return bookMrp;
    }

    String bookMrp;

    public String getRentEndDate() {
        return rentEndDate;
    }

    public String getRentStartDate() {
        return rentStartDate;
    }

    String rentEndDate;




    String book_status;
    String bookshelf_order_id;
    String order_status;
    String new_delivery;
    String delivery_processing;
    String dispatched;
    String delivered;
    String new_pickup;
    String pickup_processing;
    String req_to_courier;

    public String getBookID() {
        return bookID;
    }

    public void setBookID(String bookID) {
        this.bookID = bookID;
    }

    String bookID;

    public String getReturned() {
        return returned;
    }

    public String getBook_status() {
        return book_status;
    }

    public String getBookshelf_order_id() {
        return bookshelf_order_id;
    }

    public String getOrder_status() {
        return order_status;
    }

    public String getNew_delivery() {
        return new_delivery;
    }

    public String getDelivery_processing() {
        return delivery_processing;
    }

    public String getDispatched() {
        return dispatched;
    }

    public String getDelivered() {
        return delivered;
    }

    public String getNew_pickup() {
        return new_pickup;
    }

    public String getPickup_processing() {
        return pickup_processing;
    }

    public String getReq_to_courier() {
        return req_to_courier;
    }

    String returned;






    public boolean isSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    boolean isSelected;


    String shortDesc;
    String userRentedThisBookCost;

    String rentStartsFrom;
    String rentCost;
    String savedPer;

    public String getOrderID() {
        return orderID;
    }

    public String getRent() {
        return rent;
    }


    public String getUserRentedThisBookCost() {
        return userRentedThisBookCost;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getIsbn13() {
        return isbn13;
    }

    public String getContributor_name1() {
        return contributor_name1;
    }



    public String getInit_pay() {
        return init_pay;
    }


    public String getShelf_type() {
        return shelf_type;
    }





    public String getSavedPer(){
        return savedPer;
    }
    public void setSavedPer(String savedPer){
        this.savedPer=savedPer;
    }


    public String getRentCost(){
        return rentCost;
    }


    public String getRentStartsFrom(){
        return rentStartsFrom;
    }

    public String getShortDesc(){
        return shortDesc;
    }


    public String getMrp() {
        return mrp;
    }



    public String getRefund_amount() {
        return refund_amount;
    }

    public void setRefund_amount(String refund_amount) {
        this.refund_amount = refund_amount;
    }



    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }





}
