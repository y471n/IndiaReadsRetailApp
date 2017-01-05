package com.indiareads.retailapp.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.indiareads.retailapp.R;
import com.indiareads.retailapp.interfaces.ClickListener;
import com.indiareads.retailapp.models.Address;

/**
 * Created by vijay on 9/4/2015.
 */
public class MyOrdersViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{



    TextView  orderDate;
    TextView  orderId;
    TextView bookName;
    TextView authorName;
    TextView orderStatus;

    public TextView getDotText() {
        return dotText;
    }

    TextView dotText;

    public TextView getOrderStatusDate() {
        return orderStatusDate;
    }

    public TextView getOrderStatus() {
        return orderStatus;
    }

    TextView orderStatusDate;

    private ClickListener clickListener;

    public TextView getOrderDate() {
        return orderDate;
    }

    public TextView getOrderId() {
        return orderId;
    }

    public TextView getBookName() {
        return bookName;
    }

    public TextView getAuthorName() {
        return authorName;
    }


    public MyOrdersViewHolder(View itemView, ClickListener clickListener) {
        super(itemView);
        itemView.setOnClickListener(this);
        this.clickListener = clickListener;
        this.orderDate = (TextView) itemView.findViewById(R.id.order_placed_date);
        this.orderId = (TextView) itemView.findViewById(R.id.order_id);
        this.bookName = (TextView) itemView.findViewById(R.id.book_name);
        this.authorName = (TextView) itemView.findViewById(R.id.author_name);
        this.orderStatus = (TextView) itemView.findViewById(R.id.order_status);
        this.orderStatusDate = (TextView) itemView.findViewById(R.id.order_status_date);
        this.dotText = (TextView) itemView.findViewById(R.id.dot_text);
    }

    @Override
    public void onClick(View v) {
        if (clickListener != null) {
            clickListener.itemClicked(v, getAdapterPosition());
        }
    }


}
