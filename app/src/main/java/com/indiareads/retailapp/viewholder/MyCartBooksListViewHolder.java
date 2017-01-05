package com.indiareads.retailapp.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.LinearLayout;

import com.android.volley.toolbox.NetworkImageView;
import com.indiareads.retailapp.R;
import com.indiareads.retailapp.interfaces.MyCartClickListener;

/**
 * Created by vijay on 8/26/2015.
 */
public class MyCartBooksListViewHolder  extends RecyclerView.ViewHolder implements View.OnClickListener{

    private TextView nameOfBook;
    private TextView nameOfAuthor;
    private TextView price;
    private TextView initialPayable;

    private Button deleteButton;


    private NetworkImageView bookItemIcon;
    private MyCartClickListener clickListener;

    LinearLayout buyPriceLinearLayout;

    TextView buyBookMrp;
    TextView buyBookDiscountPrice;

    public TextView getBuyBokTotalPrice() {
        return buyBokTotalPrice;
    }

    public TextView getBuyBookDiscountPrice() {
        return buyBookDiscountPrice;
    }

    public TextView getBuyBookMrp() {
        return buyBookMrp;
    }

    TextView buyBokTotalPrice;

    public LinearLayout getRentPriceLinearLayout() {
        return rentPriceLinearLayout;
    }

    public LinearLayout getBuyPriceLinearLayout() {
        return buyPriceLinearLayout;
    }

    LinearLayout rentPriceLinearLayout;


    public Button getDeleteButton() {
        return deleteButton;
    }

    public TextView getNameOfBook() {
        return nameOfBook;
    }

    public TextView getNameOfAuthor() {
        return nameOfAuthor;
    }

    public TextView getPrice() {
        return price;
    }

    public TextView getInitialPayable() {
        return initialPayable;
    }

    public NetworkImageView getBookItemIcon() {
        return bookItemIcon;
    }

    public MyCartClickListener getClickListener() {
        return clickListener;
    }

    public void setClickListener(MyCartClickListener clickListener) {
        this.clickListener = clickListener;
    }



    public MyCartBooksListViewHolder(View itemView, MyCartClickListener clickListener) {
        super(itemView);
        itemView.setOnClickListener(this);
        this.clickListener = clickListener;

        this.nameOfBook = (TextView) itemView.findViewById(R.id.cart_book_name_textview);
        this.nameOfAuthor = (TextView) itemView.findViewById(R.id.cart_book_author_name_textview);
        this.price = (TextView) itemView.findViewById(R.id.cart_item_mrp);
        this.initialPayable = (TextView) itemView.findViewById(R.id.cart_book_item_initial_payable_price_textview);
        this.bookItemIcon = (NetworkImageView) itemView.findViewById(R.id.cart_book_item_imageview);
        this.deleteButton=(Button) itemView.findViewById(R.id.cart_item_delete_button);

        this.buyPriceLinearLayout=(LinearLayout) itemView.findViewById(R.id.buy_price_linear_layout);
        this.rentPriceLinearLayout=(LinearLayout) itemView.findViewById(R.id.rent_price_linear_layout);

        this.buyBookMrp = (TextView) itemView.findViewById(R.id.buy_book_mrp);
        this.buyBookDiscountPrice = (TextView) itemView.findViewById(R.id.buy_book_discounted_price);
        this.buyBokTotalPrice = (TextView) itemView.findViewById(R.id.buy_book_total_price);

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