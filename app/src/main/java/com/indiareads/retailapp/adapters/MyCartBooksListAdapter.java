package com.indiareads.retailapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.toolbox.ImageLoader;
import com.indiareads.retailapp.R;
import com.indiareads.retailapp.interfaces.MyCartClickListener;
import com.indiareads.retailapp.models.CartModel;
import com.indiareads.retailapp.singleton.VolleySingleton;
import com.indiareads.retailapp.utils.UrlsRetail;
import com.indiareads.retailapp.viewholder.MyCartBooksListViewHolder;

import java.util.Collections;
import java.util.List;

/**
 * Created by vijay on 8/26/2015.
 */
public class MyCartBooksListAdapter extends RecyclerView.Adapter<MyCartBooksListViewHolder> {

    List<CartModel> bookList = Collections.emptyList();
    LayoutInflater mLayoutInflater;
    Context context;
    private MyCartClickListener clickListener;
    private ImageLoader mImageLoader;

    public MyCartBooksListAdapter(Context context, List<CartModel> bookList){
        this.bookList=bookList;
        this.context=context;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public MyCartBooksListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.cart_book_item, parent, false);
        MyCartBooksListViewHolder myCartBooksListViewHolder = new MyCartBooksListViewHolder(view, clickListener);

        return myCartBooksListViewHolder;
    }

    @Override
    public void onBindViewHolder(MyCartBooksListViewHolder myCartBooksListViewHolder, int position) {

        CartModel bookListItem = bookList.get(position);

        mImageLoader = VolleySingleton.getInstance(context).getImageLoader();

        if(bookListItem.getTitle().length()>53){
            myCartBooksListViewHolder.getNameOfBook().setText(bookListItem.getTitle().substring(0,50)+"...");
        }else {
            myCartBooksListViewHolder.getNameOfBook().setText(bookListItem.getTitle());
        }


        if(bookListItem.getToBuy().equals("1")){
            myCartBooksListViewHolder.getBuyPriceLinearLayout().setVisibility(View.VISIBLE);
            myCartBooksListViewHolder.getRentPriceLinearLayout().setVisibility(View.GONE);

            myCartBooksListViewHolder.getNameOfAuthor().setText(bookListItem.getAuthor1());
            myCartBooksListViewHolder.getBuyBookMrp().setText(bookListItem.getBuy_mrp());
            myCartBooksListViewHolder.getBuyBookDiscountPrice().setText(bookListItem.getBuy_discount());
            double buyFinalPrice= Math.ceil(Double.parseDouble(bookListItem.getBuy_mrp()) -Double.parseDouble(bookListItem.getBuy_discount()));
            myCartBooksListViewHolder.getBuyBokTotalPrice().setText(String.valueOf(buyFinalPrice));

        }else{
            myCartBooksListViewHolder.getBuyPriceLinearLayout().setVisibility(View.GONE);
            myCartBooksListViewHolder.getRentPriceLinearLayout().setVisibility(View.VISIBLE);

            myCartBooksListViewHolder.getNameOfAuthor().setText(bookListItem.getAuthor1());
            myCartBooksListViewHolder.getPrice().setText(bookListItem.getMrp());
            myCartBooksListViewHolder.getInitialPayable().setText(bookListItem.getInitialPayable());
        }



        String imageUrl= UrlsRetail.IMAGES+bookListItem.getIsbn13()+".jpg";
        mImageLoader = VolleySingleton.getInstance(context).getImageLoader();
        myCartBooksListViewHolder.getBookItemIcon().setDefaultImageResId(R.drawable.test);
        myCartBooksListViewHolder.getBookItemIcon().setImageUrl(imageUrl, mImageLoader);

    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    public void setClickListener(MyCartClickListener clickListener) {
        this.clickListener = clickListener;
    }

}
