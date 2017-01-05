package com.indiareads.retailapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.toolbox.ImageLoader;
import com.indiareads.retailapp.R;
import com.indiareads.retailapp.interfaces.CurrentlyReadingClickListener;
import com.indiareads.retailapp.models.Bookshelf;
import com.indiareads.retailapp.singleton.VolleySingleton;
import com.indiareads.retailapp.utils.UrlsRetail;
import com.indiareads.retailapp.viewholder.CurrentlyReadingBookshelfListViewHolder;

import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by yatin on 29/06/15.
 */
public class CurrentlyReadingBookshelfAdapter extends RecyclerView.Adapter<CurrentlyReadingBookshelfListViewHolder>{

    private CurrentlyReadingClickListener clickListener;
    LayoutInflater mLayoutInflater;
    List<Bookshelf> bookList = Collections.emptyList();
    Context context;
    private ImageLoader mImageLoader;


    public CurrentlyReadingBookshelfAdapter(Context context, List<Bookshelf> bookItems) {
        mLayoutInflater = LayoutInflater.from(context);
        this.bookList = bookItems;
        this.context = context;
    }

    @Override
    public CurrentlyReadingBookshelfListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.currently_reading_list_item, parent, false);
        CurrentlyReadingBookshelfListViewHolder booksListViewHolder = new CurrentlyReadingBookshelfListViewHolder(view, clickListener);

        return booksListViewHolder;
    }

    @Override
    public void onBindViewHolder(CurrentlyReadingBookshelfListViewHolder booksListViewHolder, int position) {

        Bookshelf bookListItem = bookList.get(position);

        if(bookListItem.isSelected()){
            booksListViewHolder.getReturnBook().setImageResource(R.drawable.checked);
        }else{
            booksListViewHolder.getReturnBook().setImageResource(R.drawable.unchecked);
        }

        if(bookListItem.getTitle()!=null) {
            if(bookListItem.getTitle().length()>53) {
                booksListViewHolder.getBookItemHeading().setText(bookListItem.getTitle().substring(0,50)+"...");
            }else{
                booksListViewHolder.getBookItemHeading().setText(bookListItem.getTitle());
            }
        }

        if(bookListItem.getContributor_name1()!=null) {
            booksListViewHolder.getBookItemAuthor().setText(bookListItem.getContributor_name1());
       }

//        if(bookListItem.getRefund_amount()!=null) {
//            booksListViewHolder.getBookRefundCost().setText(bookListItem.getRefund_amount());
//        }

        //Log.e("currently reading ","1");
        if(Integer.parseInt(bookListItem.getBook_status())==1){
            //Log.e("currently reading ","2");
            booksListViewHolder.getReturnBookLinearLayout().setVisibility(View.GONE);
            booksListViewHolder.getRefundDetailsLinearLayout().setVisibility(View.GONE);
            booksListViewHolder.getOrderStatusLinearLayout().setVisibility(View.GONE);
           // booksListViewHolder.getBookRefundCost().setText(bookListItem.getRefund_amount());s
        }
        else if(Integer.parseInt(bookListItem.getOrder_status())==4 ){
          //  Log.e("currently reading ","3");
          //  Log.e("here ","status 4");
            booksListViewHolder.getReturnBookLinearLayout().setVisibility(View.VISIBLE);
            booksListViewHolder.getRefundDetailsLinearLayout().setVisibility(View.VISIBLE);
            booksListViewHolder.getOrderStatusLinearLayout().setVisibility(View.GONE);
            booksListViewHolder.getBookRefundCost().setText(bookListItem.getRefund_amount());
        }else{
          //  Log.e("currently reading ","4");
            booksListViewHolder.getReturnBookLinearLayout().setVisibility(View.GONE);
            booksListViewHolder.getRefundDetailsLinearLayout().setVisibility(View.GONE);
            booksListViewHolder.getOrderStatusLinearLayout().setVisibility(View.VISIBLE);
            if(bookListItem.getOrder_status().equals("8")) {
                booksListViewHolder.getOrderStatus().setText("Returned");
                booksListViewHolder.getOrderStatusDate().setText((new StringTokenizer(bookListItem.getReturned()).nextToken()));
            } else if(bookListItem.getOrder_status().equals("7")) {
                booksListViewHolder.getOrderStatus().setText("Sent To Courier");
                booksListViewHolder.getOrderStatusDate().setText((new StringTokenizer(bookListItem.getReq_to_courier()).nextToken()));
            } else if(bookListItem.getOrder_status().equals("6")) {
                booksListViewHolder.getOrderStatus().setText("Pick Up Processing");
                booksListViewHolder.getOrderStatusDate().setText((new StringTokenizer(bookListItem.getPickup_processing()).nextToken()));
            }else if(bookListItem.getOrder_status().equals("5")) {
                booksListViewHolder.getOrderStatus().setText("Pick Up request Receveied");
                booksListViewHolder.getOrderStatusDate().setText((new StringTokenizer(bookListItem.getNew_pickup()).nextToken()));
            }else if(bookListItem.getOrder_status().equals("4")) {
                booksListViewHolder.getOrderStatus().setText("Delivered");
                booksListViewHolder.getOrderStatusDate().setText((new StringTokenizer(bookListItem.getDelivered()).nextToken()));
            }else if(bookListItem.getOrder_status().equals("3")) {
                booksListViewHolder.getOrderStatus().setText("Dispatched");
                booksListViewHolder.getOrderStatusDate().setText((new StringTokenizer(bookListItem.getDispatched()).nextToken()));
            }else if(bookListItem.getOrder_status().equals("2")) {
                booksListViewHolder.getOrderStatus().setText("Preparing For Dispatch");
                booksListViewHolder.getOrderStatusDate().setText((new StringTokenizer(bookListItem.getDelivery_processing()).nextToken()));
            }else if(bookListItem.getOrder_status().equals("1")) {
                booksListViewHolder.getOrderStatus().setText("Request Received");
                booksListViewHolder.getOrderStatusDate().setText((new StringTokenizer(bookListItem.getNew_delivery()).nextToken()));
            }
        }






        String imageUrl= UrlsRetail.IMAGES+bookListItem.getIsbn13()+".jpg";
        mImageLoader = VolleySingleton.getInstance(context).getImageLoader();
        booksListViewHolder.getBookItemIcon().setDefaultImageResId(R.drawable.test);
        booksListViewHolder.getBookItemIcon().setImageUrl(imageUrl, mImageLoader);




    }



    @Override
    public int getItemCount() {
        return bookList.size();
    }

    public void setClickListener(CurrentlyReadingClickListener clickListener) {
        this.clickListener = clickListener;
    }


}
