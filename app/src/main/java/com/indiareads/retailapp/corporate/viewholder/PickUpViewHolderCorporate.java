package com.indiareads.retailapp.corporate.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.indiareads.retailapp.R;
import com.indiareads.retailapp.interfaces.ClickListener;

/**
 * Created by vijay on 8/30/2016.
 */
public class PickUpViewHolderCorporate extends RecyclerView.ViewHolder implements View.OnClickListener {


    private TextView nameOfBook;
    private TextView nameOfAuthor;

    public NetworkImageView getBookItemIcon() {
        return bookItemIcon;
    }

    public void setBookItemIcon(NetworkImageView bookItemIcon) {
        this.bookItemIcon = bookItemIcon;
    }

    private NetworkImageView bookItemIcon;
    private ClickListener clickListener;


    public TextView getNameOfBook() {
        return nameOfBook;
    }
    public TextView getNameOfAuthor() {
        return nameOfAuthor;
    }


    public PickUpViewHolderCorporate(View itemView, ClickListener clickListener) {
        super(itemView);
        itemView.setOnClickListener(this);
        this.clickListener = clickListener;

        this.nameOfBook = (TextView) itemView.findViewById(R.id.pick_up_details_book_name);

        this.nameOfAuthor = (TextView) itemView.findViewById(R.id.pickup_details_author_name);

        this.bookItemIcon = (NetworkImageView) itemView.findViewById(R.id.pickup_details_book_item_imageview);
    }

    @Override
    public void onClick(View v) {
        if (clickListener != null) {
            clickListener.itemClicked(v, getAdapterPosition());
        }
    }
}
