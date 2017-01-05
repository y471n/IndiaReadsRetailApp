package com.indiareads.retailapp.models;


public class Pickup {


    String nameOfBook;
    String typeOfBook;
    String nameOfAuthor;
    String initialpayable;
    String rent;
    String refund;


    public String getNameOfBook() {
        return nameOfBook;
    }
    public void setNameOfBook(String nameOfBook) {
        this.nameOfBook = nameOfBook;
    }
    public String getNameOfAuthor() {
        return nameOfAuthor;
    }
    public void setNameOfAuthor(String nameOfAuthor) {
        this.nameOfAuthor = nameOfAuthor;
    }
    public String getTypeOfBook() {

        return typeOfBook;
    }
    public void setTypeOfBook(String typeOfBook) {
        this.typeOfBook = typeOfBook;
    }
    public String getInitialpayable() {

        return initialpayable;
    }
    public void setInitialpayable(String initialpayable) {

        this.initialpayable = initialpayable;
    }
    public String getRent()
    {
        return rent;
    }
    public void setRent(String rent)
    {
        this.rent = rent;
    }

    public String getRefund() {
        return refund;
    }
    public void setRefund(String refund)
    {
        this.refund = refund;
    }

}
