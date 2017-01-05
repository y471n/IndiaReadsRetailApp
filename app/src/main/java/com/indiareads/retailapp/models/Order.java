package com.indiareads.retailapp.models;

import java.io.Serializable;

/**
 * Created by vijay on 8/11/2015.
 */
public class Order implements Serializable{

    String ISBN13;
    String title;
    String contributor_name1;
    String order_date;
    String expected_return_date;

    String d_track_id;
    String carrier;
    String init_pay;
    String payment_option;
    String order_address_id;

    public String getOrder_address_id() {
        return order_address_id;
    }

    public void setOrder_address_id(String order_address_id) {
        this.order_address_id = order_address_id;
    }

    public String getCarrier() {
        return carrier;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    public String getD_track_id() {
        return d_track_id;
    }

    public void setD_track_id(String d_track_id) {
        this.d_track_id = d_track_id;
    }



    public String getPayment_option() {
        return payment_option;
    }

    public void setPayment_option(String payment_option) {
        this.payment_option = payment_option;
    }

    public String getInit_pay() {
        return init_pay;
    }

    public void setInit_pay(String init_pay) {
        this.init_pay = init_pay;
    }



    public String getBookshelf_order_id() {
        return bookshelf_order_id;
    }

    public void setBookshelf_order_id(String bookshelf_order_id) {
        this.bookshelf_order_id = bookshelf_order_id;
    }

    public String getISBN13() {
        return ISBN13;
    }

    public void setISBN13(String ISBN13) {
        this.ISBN13 = ISBN13;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContributor_name1() {
        return contributor_name1;
    }

    public void setContributor_name1(String contributor_name1) {
        this.contributor_name1 = contributor_name1;
    }

    public String getOrder_date() {
        return order_date;
    }

    public void setOrder_date(String order_date) {
        this.order_date = order_date;
    }

    public String getExpected_return_date() {
        return expected_return_date;
    }

    public void setExpected_return_date(String expected_return_date) {
        this.expected_return_date = expected_return_date;
    }

    public String getOrder_status() {
        return order_status;
    }

    public void setOrder_status(String order_status) {
        this.order_status = order_status;
    }

    public String getNew_delivery() {
        return new_delivery;
    }

    public void setNew_delivery(String new_delivery) {
        this.new_delivery = new_delivery;
    }

    public String getDelivery_processing() {
        return delivery_processing;
    }

    public void setDelivery_processing(String delivery_processing) {
        this.delivery_processing = delivery_processing;
    }

    public String getDispatched() {
        return dispatched;
    }

    public void setDispatched(String dispatched) {
        this.dispatched = dispatched;
    }

    public String getDelivered() {
        return delivered;
    }

    public void setDelivered(String delivered) {
        this.delivered = delivered;
    }

    public String getNew_pickup() {
        return new_pickup;
    }

    public void setNew_pickup(String new_pickup) {
        this.new_pickup = new_pickup;
    }

    public String getPickup_processing() {
        return pickup_processing;
    }

    public void setPickup_processing(String pickup_processing) {
        this.pickup_processing = pickup_processing;
    }

    public String getReq_to_fedex() {
        return req_to_fedex;
    }

    public void setReq_to_fedex(String req_to_fedex) {
        this.req_to_fedex = req_to_fedex;
    }

    public String getReturned() {
        return returned;
    }

    public void setReturned(String returned) {
        this.returned = returned;
    }

    String bookshelf_order_id;
    String order_status;
    String new_delivery;
    String delivery_processing;
    String dispatched;
    String delivered;
    String new_pickup;
    String pickup_processing;
    String req_to_fedex;
    String returned;







}
