package com.indiareads.retailapp.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.indiareads.retailapp.R;
import com.indiareads.retailapp.interfaces.ClickListener;
import com.indiareads.retailapp.interfaces.CurrentlyReadingClickListener;

/**
 * Created by vijay on 9/5/2015.
 */
public class CategoryListingListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


    private TextView bookHeading;
//    private TextView bookType;
    private TextView bookAuthor;
    private TextView bookMrp;
    private TextView bookRent;
//    private TextView bookDescription;

    private NetworkImageView bookItemIcon;
    private ClickListener clickListener;


    public TextView getBookHeading() {
        return bookHeading;
    }

//    public TextView getBookType() {
//        return bookType;
//    }

    public TextView getBookAuthor() {
        return bookAuthor;
    }

    public TextView getBookMrp() {
        return bookMrp;
    }

    public TextView getBookRent() {
        return bookRent;
    }

//    public TextView getBookDescription() {
//        return bookDescription;
//    }

    public NetworkImageView getBookItemIcon() {
        return bookItemIcon;
    }

    public ClickListener getClickListener() {
        return clickListener;
    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public CategoryListingListViewHolder(View itemView, ClickListener clickListener) {
        super(itemView);
        itemView.setOnClickListener(this);
        this.clickListener = clickListener;

        this.bookHeading = (TextView) itemView.findViewById(R.id.book_name);
//        this.bookType = (TextView) itemView.findViewById(R.id.type_of_book);
        this.bookAuthor = (TextView) itemView.findViewById(R.id.author_name);
        this.bookMrp = (TextView) itemView.findViewById(R.id.book_mrp);
        this.bookRent = (TextView) itemView.findViewById(R.id.rent_price);
//        this.bookDescription = (TextView) itemView.findViewById(R.id.book_description);

        this.bookItemIcon = (NetworkImageView) itemView.findViewById(R.id.book_item_imageview);


    }

    @Override
    public void onClick(View v) {
        if (clickListener != null) {
            clickListener.itemClicked(v, getAdapterPosition());
        }
    }

}