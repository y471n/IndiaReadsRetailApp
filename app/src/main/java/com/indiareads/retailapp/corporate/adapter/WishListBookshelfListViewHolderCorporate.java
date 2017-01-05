package com.indiareads.retailapp.corporate.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.indiareads.retailapp.R;
import com.indiareads.retailapp.interfaces.WishListBookshelfClickListener;
import android.widget.LinearLayout;

/**
 * Created by vijay on 8/27/2016.
 */
public class WishListBookshelfListViewHolderCorporate extends RecyclerView.ViewHolder implements View.OnClickListener {


    private TextView bookItemHeading;
    private TextView bookItemAuthor;
    //    private TextView bookSummary;
    private NetworkImageView bookItemIcon;
    ImageView deleteButton;

    public LinearLayout getDeleteButtonLayout() {
        return deleteButtonLayout;
    }

    LinearLayout deleteButtonLayout;

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



    public WishListBookshelfListViewHolderCorporate(View itemView, WishListBookshelfClickListener clickListener) {
        super(itemView);

        this.clickListener = clickListener;
        itemView.setOnClickListener(this);

        this.bookItemHeading = (TextView) itemView.findViewById(R.id.book_name);
        this.bookItemIcon = (NetworkImageView) itemView.findViewById(R.id.book_item_imageview);
        this.bookItemAuthor = (TextView) itemView.findViewById(R.id.book_author_name);
//        this.bookSummary = (TextView) itemView.findViewById(R.id.book_summary);
        this.deleteButton=(ImageView) itemView.findViewById(R.id.delete_button);
        this.deleteButtonLayout=(LinearLayout) itemView.findViewById(R.id.delete_button_layout);

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

