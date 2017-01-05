package com.indiareads.retailapp.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.indiareads.retailapp.R;
import com.indiareads.retailapp.interfaces.ClickListener;

/**
 * Created by vijay on 7/29/2015.
 */
public class HorizontalBooksListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    private TextView bookItemHeading;
    private NetworkImageView bookItemIcon;
    private ClickListener clickListener;

    public TextView getBookItemHeading() {
        return bookItemHeading;
    }

    public NetworkImageView getBookItemIcon() {
        return bookItemIcon;
    }

    public HorizontalBooksListViewHolder(View itemView, ClickListener clickListener) {
        super(itemView);
        itemView.setOnClickListener(this);
        this.clickListener = clickListener;
        this.bookItemHeading = (TextView) itemView.findViewById(R.id.horizontal_books_item_heading);
        this.bookItemIcon = (NetworkImageView) itemView.findViewById(R.id.horizontal_books_item_icon);
    }

    @Override
    public void onClick(View v) {
        if (clickListener != null) {
            clickListener.itemClicked(v, getAdapterPosition());
        }
    }
}
