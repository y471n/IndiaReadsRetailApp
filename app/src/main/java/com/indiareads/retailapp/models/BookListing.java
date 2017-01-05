package com.indiareads.retailapp.models;

/**
 * Created by vijay on 5/9/2016.
 */
public class BookListing {

    String title;
    String contributor_name1;
    String isbn13;
    String mrp;

    public String getRentStartsFrom() {
        return rentStartsFrom;
    }

    public void setRentStartsFrom(String rentStartsFrom) {
        this.rentStartsFrom = rentStartsFrom;
    }

    String rentStartsFrom;

    public boolean isInStock() {
        return inStock;
    }

    public void setInStock(boolean inStock) {
        this.inStock = inStock;
    }

    boolean inStock;

    public String getRent() {
        return rent;
    }

    public void setRent(String rent) {
        this.rent = rent;
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

    public String getIsbn13() {
        return isbn13;
    }

    public void setIsbn13(String isbn13) {
        this.isbn13 = isbn13;
    }

    public String getMrp() {
        return mrp;
    }

    public void setMrp(String mrp) {
        this.mrp = mrp;
    }


    String rent;
}
