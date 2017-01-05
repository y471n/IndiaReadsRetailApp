package com.indiareads.retailapp.models;

import com.activeandroid.Model;

import java.io.Serializable;

/**
 * Created by vijay on 7/29/2015.
 */

public class Book  implements Serializable {



    String title;
    String author1;
    String shortDesc;//
    Rent rent;

    public String getContributor_name1() {
        return contributor_name1;
    }

    public void setContributor_name1(String contributor_name1) {
        this.contributor_name1 = contributor_name1;
    }

    String contributor_name1;

    public String getBuy_status() {
        return buy_status;
    }

    public void setBuy_status(String buy_status) {
        this.buy_status = buy_status;
    }

    String buy_status;

    public Buy getBuy() {
        return buy;
    }

    public void setBuy(Buy buy) {
        this.buy = buy;
    }

    Buy buy;
    String saveRs;
    String bookSummary;
    String authorBio; // author_bio
    String longDesc;//
    String bookReviews;
    String refund;
    String isbn13;
    String productForm;
    String publisherName;
    String textLanguage;
    String imprintName;
    String totalPages;
    String publicationDate;
    boolean inStock;

    public String getBinding() {
        return binding;
    }

    public void setBinding(String binding) {
        this.binding = binding;
    }

    String binding;

    public Book(String title, String isbn13,String author1,Rent rent) {
        super();
        this.title = title;
        this.isbn13 = isbn13;
        this.author1=author1;
        this.rent=rent;
    }

    public Book(){}


    public boolean isInStock() {
        return inStock;
    }

    public void setInStock(boolean inStock) {
        this.inStock = inStock;
    }

     public String getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(String totalPages) {
        this.totalPages = totalPages;
    }

    public String getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(String publicationDate) {
        this.publicationDate = publicationDate;
    }

    public String getImprintName() {
        return imprintName;
    }

    public void setImprintName(String imprintName) {
        this.imprintName = imprintName;
    }

    public String getTextLanguage() {
        return textLanguage;
    }

    public void setTextLanguage(String textLanguage) {
        this.textLanguage = textLanguage;
    }

    public String getPublisherName() {
        return publisherName;
    }

    public void setPublisherName(String publisherName) {
        this.publisherName = publisherName;
    }

    public String getProductForm() {
        return productForm;
    }

    public void setProductForm(String productForm) {
        this.productForm = productForm;
    }




    public void setBookSummary(String bookSummary) {
        this.bookSummary = bookSummary;
    }

    public void setSaveRs(String saveRs) {
        this.saveRs = saveRs;
    }

    public void setBookReviews(String bookReviews) {
        this.bookReviews = bookReviews;
    }

    public void setRefund(String refund) {
        this.refund = refund;
    }




    public String getBookSummary() {
        return bookSummary;
    }

    public String getBookReviews() {
        return bookReviews;
    }

    public String getRefund() {
        return refund;
    }


    public String getSaveRs() {
        return saveRs;
    }

    public String getAuthorBio() {
        return authorBio;
    }

    public void setAuthorBio(String authorBio) {
        this.authorBio = authorBio;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }





    public Rent getRent() {
        return rent;
    }

    public void setRent(Rent rent) {
        this.rent = rent;
    }

    public String getIsbn13() {
        return isbn13;
    }

    public void setIsbn13(String isbn13) {
        this.isbn13 = isbn13;
    }

    public String getShortDesc() {
        return shortDesc;
    }

    public void setShortDesc(String shortDesc) {
        this.shortDesc = shortDesc;
    }

    public String getLongDesc() {
        return longDesc;
    }

    public void setLongDesc(String longDesc) {
        this.longDesc = longDesc;
    }

    public String getAuthor1() {
        return author1;
    }

    public void setAuthor1(String author1) {
        this.author1 = author1;
    }





}
