package com.indiareads.retailapp.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.indiareads.retailapp.R;
import com.indiareads.retailapp.interfaces.ClickListener;

import org.w3c.dom.Text;

/**
 * Created by vijay on 9/3/2015.
 */
public class ReadingHistoryBookshelfListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private TextView bookItemHeading;
    private TextView bookItemAuthor;
    private NetworkImageView bookItemIcon;
    private TextView bookMrp;
    private TextView bookRentDuration;
    private TextView userRentedThisBookCost;

    TextView rentStartDate;

    public TextView getRentEndDate() {
        return rentEndDate;
    }

    public TextView getRentStartDate() {
        return rentStartDate;
    }

    TextView rentEndDate;

    public TextView getUserRentedThisBookCost() {
        return userRentedThisBookCost;
    }


  //  private TextView bookRentStartDate;
 //   private TextView bookRentEndDate;
    private TextView bookSavePer;
    private ClickListener clickListener;

    public TextView getBookItemHeading() {
        return bookItemHeading;
    }



    public TextView getBookItemAuthor() {
        return bookItemAuthor;
    }

    public NetworkImageView getBookItemIcon() {
        return bookItemIcon;
    }

    public TextView getBookMrp() {
        return bookMrp;
    }


    public TextView getBookRentDuration() {
        return bookRentDuration;
    }


 /*   public TextView getBookRentEndDate() {
        return bookRentEndDate;
    }

    public TextView getBookRentStartDate() {
        return bookRentStartDate;
    }*/

    public TextView getBookSavePer() {
        return bookSavePer;
    }


    public ClickListener getClickListener() {
        return clickListener;
    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public ReadingHistoryBookshelfListViewHolder(View itemView, ClickListener clickListener) {
        super(itemView);

        this.clickListener = clickListener;
        itemView.setOnClickListener(this);

        this.bookItemHeading = (TextView) itemView.findViewById(R.id.book_name);
        this.bookItemIcon = (NetworkImageView) itemView.findViewById(R.id.book_item_imageview);
        this.bookItemAuthor = (TextView) itemView.findViewById(R.id.book_author_name);

        this.bookMrp = (TextView) itemView.findViewById(R.id.book_mrp);
        this.userRentedThisBookCost=(TextView)itemView.findViewById(R.id.user_rented_this_book_cost);

        this.rentStartDate = (TextView) itemView.findViewById(R.id.rent_start_date);
        this.rentEndDate = (TextView) itemView.findViewById(R.id.rent_end_date);

     //   this.bookRentStartDate = (TextView) itemView.findViewById(R.id.book_author_name);
     //   this.bookRentEndDate = (TextView) itemView.findViewById(R.id.book_author_name);

//        this.bookRentDuration = (TextView) itemView.findViewById(R.id.rental_duration_textview);
//
//        this.bookSavePer = (TextView) itemView.findViewById(R.id.save_per_text);


    }

    @Override
    public void onClick(View v) {
        if (clickListener != null) {
            clickListener.itemClicked(v, getAdapterPosition());
        }
    }
}




