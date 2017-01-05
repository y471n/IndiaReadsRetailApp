package com.indiareads.retailapp.corporate.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.indiareads.retailapp.R;
import com.indiareads.retailapp.interfaces.MyCartClickListener;

/**
 * Created by vijay on 8/22/2016.
 */
public class MyCartBooksListViewHolderCorporate  extends RecyclerView.ViewHolder implements View.OnClickListener{

    private TextView nameOfBook;
    private TextView nameOfAuthor;

    private Button deleteButton;
    private NetworkImageView bookItemIcon;
    private MyCartClickListener clickListener;


    public TextView getBuyBokTotalPrice() {
        return buyBokTotalPrice;
    }


    TextView buyBokTotalPrice;

    public LinearLayout getRentPriceLinearLayout() {
        return rentPriceLinearLayout;
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

    public NetworkImageView getBookItemIcon() {
        return bookItemIcon;
    }

    public MyCartClickListener getClickListener() {
        return clickListener;
    }

    public void setClickListener(MyCartClickListener clickListener) {
        this.clickListener = clickListener;
    }



    public MyCartBooksListViewHolderCorporate(View itemView, MyCartClickListener clickListener) {
        super(itemView);
        itemView.setOnClickListener(this);
        this.clickListener = clickListener;

        this.nameOfBook = (TextView) itemView.findViewById(R.id.cart_book_name_textview);
        this.nameOfAuthor = (TextView) itemView.findViewById(R.id.cart_book_author_name_textview);
        this.bookItemIcon = (NetworkImageView) itemView.findViewById(R.id.cart_book_item_imageview);
        this.deleteButton=(Button) itemView.findViewById(R.id.cart_item_delete_button);

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
