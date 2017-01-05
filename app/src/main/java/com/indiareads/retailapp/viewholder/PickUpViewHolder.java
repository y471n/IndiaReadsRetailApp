package com.indiareads.retailapp.viewholder;


import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.indiareads.retailapp.R;
import com.indiareads.retailapp.interfaces.ClickListener;

public class PickUpViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


    private TextView nameOfBook;
    private TextView typeOfBook;
    private TextView nameOfAuthor;
    private TextView initialPayable;
    private TextView rent;
    private TextView refund;

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

    public TextView getTypeOfBook() {
        return typeOfBook;
    }

    public TextView getNameOfAuthor() {
        return nameOfAuthor;
    }

    public TextView getInitialPayable() {
        return initialPayable;
    }

    public TextView getRent() {
        return rent;
    }

    public TextView getRefund() {
        return refund;
    }

    public PickUpViewHolder(View itemView, ClickListener clickListener) {
        super(itemView);
        itemView.setOnClickListener(this);
        this.clickListener = clickListener;

        this.nameOfBook = (TextView) itemView.findViewById(R.id.pick_up_details_book_name);
       // this.typeOfBook = (TextView) itemView.findViewById(R.id.pickup_details_type_of_book);
        this.nameOfAuthor = (TextView) itemView.findViewById(R.id.pickup_details_author_name);
        this.initialPayable = (TextView) itemView.findViewById(R.id.pickup_details_book_mrp);
        this.refund = (TextView) itemView.findViewById(R.id.pickup_details_refund_amount);
        this.rent = (TextView) itemView.findViewById(R.id.pickup_details_rent_applicable);
        this.bookItemIcon = (NetworkImageView) itemView.findViewById(R.id.pickup_details_book_item_imageview);
    }

    @Override
    public void onClick(View v) {
        if (clickListener != null) {
            clickListener.itemClicked(v, getAdapterPosition());
        }
    }
}
