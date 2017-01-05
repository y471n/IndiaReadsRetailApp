package com.indiareads.retailapp.corporate.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.indiareads.retailapp.R;
import com.indiareads.retailapp.interfaces.ClickListener;

/**
 * Created by vijay on 8/23/2016.
 */
public class CategoryListingListViewHolderCorporate extends RecyclerView.ViewHolder implements View.OnClickListener {


    private TextView bookHeading;
    private TextView bookAuthor;
    private NetworkImageView bookItemIcon;
    private ClickListener clickListener;


    public TextView getBookHeading() {
        return bookHeading;
    }

    public TextView getBookAuthor() {
        return bookAuthor;
    }


    public NetworkImageView getBookItemIcon() {
        return bookItemIcon;
    }

    public ClickListener getClickListener() {
        return clickListener;
    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public CategoryListingListViewHolderCorporate(View itemView, ClickListener clickListener) {
        super(itemView);
        itemView.setOnClickListener(this);
        this.clickListener = clickListener;

        this.bookHeading = (TextView) itemView.findViewById(R.id.book_name);
        this.bookAuthor = (TextView) itemView.findViewById(R.id.author_name);
        this.bookItemIcon = (NetworkImageView) itemView.findViewById(R.id.book_item_imageview);


    }

    @Override
    public void onClick(View v) {
        if (clickListener != null) {
            clickListener.itemClicked(v, getAdapterPosition());
        }
    }

}