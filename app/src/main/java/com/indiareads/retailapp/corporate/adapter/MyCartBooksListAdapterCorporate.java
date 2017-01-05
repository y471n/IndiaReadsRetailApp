package com.indiareads.retailapp.corporate.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.toolbox.ImageLoader;
import com.indiareads.retailapp.R;
import com.indiareads.retailapp.corporate.viewholder.MyCartBooksListViewHolderCorporate;
import com.indiareads.retailapp.interfaces.MyCartClickListener;
import com.indiareads.retailapp.corporate.models.CartModelCorporate;
import com.indiareads.retailapp.singleton.VolleySingleton;
import com.indiareads.retailapp.utils.UrlsRetail;

import java.util.Collections;
import java.util.List;

/**
 * Created by vijay on 8/22/2016.
 */
public class MyCartBooksListAdapterCorporate extends RecyclerView.Adapter<MyCartBooksListViewHolderCorporate> {

    List<CartModelCorporate> bookList = Collections.emptyList();
    LayoutInflater mLayoutInflater;
    Context context;
    private MyCartClickListener clickListener;
    private ImageLoader mImageLoader;

    public MyCartBooksListAdapterCorporate(Context context, List<CartModelCorporate> bookList){
        this.bookList=bookList;
        this.context=context;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public MyCartBooksListViewHolderCorporate onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.cart_book_item_corporate, parent, false);
        MyCartBooksListViewHolderCorporate myCartBooksListViewHolder = new MyCartBooksListViewHolderCorporate(view, clickListener);

        return myCartBooksListViewHolder;
    }

    @Override
    public void onBindViewHolder(MyCartBooksListViewHolderCorporate myCartBooksListViewHolder, int position) {

        CartModelCorporate bookListItem = bookList.get(position);
        mImageLoader = VolleySingleton.getInstance(context).getImageLoader();
        if(bookListItem.getTitle().length()>53){
            myCartBooksListViewHolder.getNameOfBook().setText(bookListItem.getTitle().substring(0,50)+"...");
        }else {
            myCartBooksListViewHolder.getNameOfBook().setText(bookListItem.getTitle());
        }
        myCartBooksListViewHolder.getNameOfAuthor().setText(bookListItem.getAuthor1());
        myCartBooksListViewHolder.getNameOfAuthor().setText(bookListItem.getAuthor1());

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

