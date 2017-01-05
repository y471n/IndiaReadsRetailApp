package com.indiareads.retailapp.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.indiareads.retailapp.R;
import com.indiareads.retailapp.interfaces.AvailableBookshelfClickListener;
import com.indiareads.retailapp.interfaces.CurrentlyReadingClickListener;

/**
 * Created by vijay on 9/2/2015.
 */
public class AvailableBookshelfListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


    private TextView bookItemHeading;
    private TextView bookMrp;
    private TextView bookInitialPaid;
    private TextView bookRentStartsFrom;
    private LinearLayout addToCartLinearLayout;
    private TextView bookItemAuthor;
    private NetworkImageView bookItemIcon;
    private LinearLayout deleteBook;

    public LinearLayout getMrpLayout() {
        return mrpLayout;
    }

    public LinearLayout getIntialPayableLayout() {
        return intialPayableLayout;
    }

    public LinearLayout getRentStartFromLayout() {
        return rentStartFromLayout;
    }

    private LinearLayout mrpLayout;
    private LinearLayout intialPayableLayout;
    private LinearLayout rentStartFromLayout;

    public ImageView getAddToCartImageView() {
        return addToCartImageView;
    }

    private ImageView addToCartImageView;
    private AvailableBookshelfClickListener clickListener;


    public TextView getBookItemHeading() {
        return bookItemHeading;
    }

    public LinearLayout getDeleteBook() {
        return deleteBook;
    }

    public TextView getBookMrp() {
        return bookMrp;
    }

    public TextView getBookInitialPaid() {
        return bookInitialPaid;
    }

    public TextView getBookRentStartsFrom() {
        return bookRentStartsFrom;
    }

    public LinearLayout getAddToCartLinearLayout() {
        return addToCartLinearLayout;
    }

    public TextView getBookItemAuthor() {
        return bookItemAuthor;
    }


    public NetworkImageView getBookItemIcon() {
        return bookItemIcon;
    }

    public AvailableBookshelfClickListener getClickListener() {
        return clickListener;
    }

    public void setClickListener(AvailableBookshelfClickListener clickListener) {
        this.clickListener = clickListener;
    }



    public AvailableBookshelfListViewHolder(View itemView, AvailableBookshelfClickListener clickListener) {
        super(itemView);
        itemView.setOnClickListener(this);
        this.clickListener = clickListener;

        this.bookItemHeading = (TextView) itemView.findViewById(R.id.book_name);
        this.bookMrp = (TextView) itemView.findViewById(R.id.book_mrp);
        this.bookInitialPaid = (TextView) itemView.findViewById(R.id.book_initial_payable_cost);
        this.bookRentStartsFrom = (TextView) itemView.findViewById(R.id.book_rent_starts_from_cost);
        this.addToCartLinearLayout = (LinearLayout) itemView.findViewById(R.id.add_to_cart_linear_layout);
        this.bookItemIcon = (NetworkImageView) itemView.findViewById(R.id.book_item_imageview);
        this.bookItemAuthor = (TextView) itemView.findViewById(R.id.book_author_name);
        this.deleteBook = (LinearLayout) itemView.findViewById(R.id.delete_book_image_view);
        this.addToCartImageView = (ImageView) itemView.findViewById(R.id.add_to_cart_image_view);
        this.mrpLayout = (LinearLayout) itemView.findViewById(R.id.mrp_layout);
        this.intialPayableLayout = (LinearLayout) itemView.findViewById(R.id.rent_starts_from_layout);
        this.rentStartFromLayout = (LinearLayout) itemView.findViewById(R.id.initial_pay_layout);


        deleteBook.setOnClickListener(this);
        addToCartLinearLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (clickListener != null) {
          if(v.getTag()!=null) {
              if (v.getTag().toString().equalsIgnoreCase("delete")) {
                  clickListener.onDelete(v, getAdapterPosition());
              } else if (v.getTag().toString().equalsIgnoreCase("add_to_cart")) {
                  clickListener.addToCart(v, getAdapterPosition());
              }
          }else{
              clickListener.itemClicked(v, getAdapterPosition());
          }

        }
    }
}