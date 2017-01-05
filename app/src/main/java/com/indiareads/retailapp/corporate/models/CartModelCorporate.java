package com.indiareads.retailapp.corporate.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by vijay on 8/22/2016.
 */
@Table(name = "CartModelCorporate")
public class CartModelCorporate extends Model {
    @Column(name = "title")
    String title;
    @Column(name = "contributor_name1")
    String author1;
    @Column(name = "isbn13")
    String isbn13;
    @Column(name = "bookID")
    String bookID;
    @Column(name = "merchantLibrary")
    String merchantLibrary;

    public String getBookLibrary() {
        return bookLibrary;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor1() {
        return author1;
    }

    public String getIsbn13() {
        return isbn13;
    }

    public String getBookID() {
        return bookID;
    }

    public String getMerchantLibrary() {
        return merchantLibrary;
    }

    @Column(name = "bookLibrary")
    String bookLibrary;

    public CartModelCorporate(){}

    public CartModelCorporate(String title,String author1,String isbn13,String bookID,String merchantLibrary,String bookLibrary){
        this.title=title;
        this.author1=author1;
        this.isbn13=isbn13;
        this.bookID=bookID;
        this.merchantLibrary=merchantLibrary;
        this.bookLibrary=bookLibrary;
    }

}
