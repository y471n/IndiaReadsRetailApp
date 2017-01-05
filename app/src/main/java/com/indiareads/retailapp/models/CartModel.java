package com.indiareads.retailapp.models;


import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by vijay on 12/9/2015.
 */
@Table(name = "CartBooks")
public class CartModel extends Model {

    @Column(name = "title")
    String title;
    @Column(name = "contributor_name1")
    String author1;
    @Column(name = "mrp")
    String mrp;
    @Column(name = "init_pay")
    String initialPayable;
    @Column(name = "isbn13")
    String isbn13;
    @Column(name = "bookID")
    String bookID;
    @Column(name = "merchantLibrary")
    String merchantLibrary;
    @Column(name = "bookLibrary")
    String bookLibrary;
    @Column(name = "toBuy")
    String toBuy;
    @Column(name = "buy_mrp")
    String buy_mrp;
    @Column(name = "buy_discount")
    String buy_discount;


    public String getToBuy() {
        return toBuy;
    }

    public void setToBuy(String toBuy) {
        this.toBuy = toBuy;
    }



    public String getBuy_discount() {
        return buy_discount;
    }

    public void setBuy_discount(String buy_discount) {
        this.buy_discount = buy_discount;
    }




    public String getBuy_mrp() {
        return buy_mrp;
    }

    public void setBuy_mrp(String buy_mrp) {
        this.buy_mrp = buy_mrp;
    }






    public CartModel(String title,String author1,String isbn13,String mrp,String initialPayable,String bookID,String merchantLibrary,String bookLibrary,String toBuy,String buy_mrp,String buy_discount){
        this.title=title;
        this.mrp=mrp;
        this.initialPayable=initialPayable;
        this.author1=author1;
        this.isbn13=isbn13;
        this.bookID=bookID;
        this.merchantLibrary=merchantLibrary;
        this.bookLibrary=bookLibrary;
        this.toBuy=toBuy;
        this.buy_mrp=buy_mrp;
        this.buy_discount=buy_discount;
    }



    public String getBookID() {
        return bookID;
    }

    public void setBookID(String bookID) {
        this.bookID = bookID;
    }

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



    public String getIsbn13() {
        return isbn13;
    }

    public void setIsbn13(String isbn13) {
        this.isbn13 = isbn13;
    }

    public CartModel(){}



    public String getAuthor1() {
        return author1;
    }

    public void setAuthor1(String author1) {
        this.author1 = author1;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMrp() {
        return mrp;
    }

    public void setMrp(String mrp) {
        this.mrp = mrp;
    }

    public String getInitialPayable() {
        return initialPayable;
    }

    public void setInitialPayable(String initialPayable) {
        this.initialPayable = initialPayable;
    }



}
