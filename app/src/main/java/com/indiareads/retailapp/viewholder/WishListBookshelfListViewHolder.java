package com.indiareads.retailapp.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.indiareads.retailapp.R;
import com.indiareads.retailapp.interfaces.ClickListener;
import com.indiareads.retailapp.interfaces.WishListBookshelfClickListener;

/**
 * Created by vijay on 9/2/2015.
 */


public class WishListBookshelfListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


    private TextView bookItemHeading;
    private TextView bookItemAuthor;
//    private TextView bookSummary;
    private NetworkImageView bookItemIcon;
    ImageView deleteButton;

    private WishListBookshelfClickListener clickListener;


    public ImageView getDeleteImageView() {
        return deleteButton;
    }

    public TextView getBookItemHeading() {
        return bookItemHeading;
    }

    public TextView getBookItemAuthor() {
        return bookItemAuthor;
    }

//    public TextView getBookSummary() {
//        return bookSummary;
//    }


    public NetworkImageView getBookItemIcon() {
        return bookItemIcon;
    }

    public WishListBookshelfClickListener getClickListener() {
        return clickListener;
    }

    public void setClickListener(WishListBookshelfClickListener clickListener) {
        this.clickListener = clickListener;
    }



    public WishListBookshelfListViewHolder(View itemView, WishListBookshelfClickListener clickListener) {
        super(itemView);

        this.clickListener = clickListener;
        itemView.setOnClickListener(this);

        this.bookItemHeading = (TextView) itemView.findViewById(R.id.book_name);
        this.bookItemIcon = (NetworkImageView) itemView.findViewById(R.id.book_item_imageview);
        this.bookItemAuthor = (TextView) itemView.findViewById(R.id.book_author_name);
//        this.bookSummary = (TextView) itemView.findViewById(R.id.book_summary);
        this.deleteButton=(ImageView) itemView.findViewById(R.id.delete_button);

        deleteButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (clickListener != null) {
            if(v.getTag()!=null) {
                clickListener.onDelete(v, getAdapterPosition());
            }else{
                clickListener.itemClicked(v, getAdapterPosition());
            }
        }
    }
}
