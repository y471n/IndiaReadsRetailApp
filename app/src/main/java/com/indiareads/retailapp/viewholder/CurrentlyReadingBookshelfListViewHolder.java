package com.indiareads.retailapp.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.indiareads.retailapp.R;
import com.indiareads.retailapp.interfaces.CurrentlyReadingClickListener;

/**
 * Created by mac on 29/06/15.
 */
public class CurrentlyReadingBookshelfListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private TextView bookItemHeading;


    public TextView getBookRefundCost() {
        return bookRefundCost;
    }

    private TextView bookRefundCost;
    private LinearLayout returnBookLinearLayout;
    private LinearLayout refundDetailsLinearLayout;
    private TextView bookItemAuthor;
    private NetworkImageView bookItemIcon;
    private ImageView returnBook;
    private CurrentlyReadingClickListener clickListener;

    public LinearLayout getOrderStatusLinearLayout() {
        return orderStatusLinearLayout;
    }

    private LinearLayout orderStatusLinearLayout;

    public TextView getOrderStatusDate() {
        return orderStatusDate;
    }

    private TextView orderStatusDate;

    public TextView getOrderStatus() {
        return orderStatus;
    }

    private TextView orderStatus;


    public LinearLayout getRefundDetailsLinearLayout() {
        return refundDetailsLinearLayout;
    }


    public ImageView getReturnBook() {
        return returnBook;
    }
    public TextView getBookItemHeading() {
        return bookItemHeading;
    }




    public LinearLayout getReturnBookLinearLayout() {
        return returnBookLinearLayout;
    }

   public TextView getBookItemAuthor() {
        return bookItemAuthor;
    }


    public NetworkImageView getBookItemIcon() {
        return bookItemIcon;
    }

   public CurrentlyReadingClickListener getClickListener() {
        return clickListener;
    }

    public void setClickListener(CurrentlyReadingClickListener clickListener) {
        this.clickListener = clickListener;
    }






    public CurrentlyReadingBookshelfListViewHolder(View itemView, CurrentlyReadingClickListener clickListener) {
        super(itemView);
        itemView.setOnClickListener(this);
        this.clickListener = clickListener;
        this.bookItemHeading = (TextView) itemView.findViewById(R.id.book_name);
        this.bookRefundCost = (TextView) itemView.findViewById(R.id.book_refund_cost);
         this.returnBookLinearLayout = (LinearLayout) itemView.findViewById(R.id.return_book_linear_layout);
        this.refundDetailsLinearLayout = (LinearLayout) itemView.findViewById(R.id.refund_details_linear_layout);
        this.orderStatusLinearLayout = (LinearLayout) itemView.findViewById(R.id.order_status_layout);

        this.bookItemIcon = (NetworkImageView) itemView.findViewById(R.id.book_item_imageview);
        this.bookItemAuthor = (TextView) itemView.findViewById(R.id.book_author_name);

        this.returnBook = (ImageView) itemView.findViewById(R.id.return_book_image_view);

        this.orderStatus = (TextView) itemView.findViewById(R.id.order_status);
        this.orderStatusDate = (TextView) itemView.findViewById(R.id.order_status_date);


        returnBookLinearLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (clickListener != null) {
            if(v.getTag()!=null) {
                if (v.getTag().toString().equalsIgnoreCase("return_book")) {
                    clickListener.onReturn(v, getAdapterPosition());
                }
            }else{
                clickListener.itemClicked(v, getAdapterPosition());
            }

        }
    }
}