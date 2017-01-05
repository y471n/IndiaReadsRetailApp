package com.indiareads.retailapp.corporate.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.toolbox.ImageLoader;
import com.indiareads.retailapp.R;
import com.indiareads.retailapp.interfaces.WishListBookshelfClickListener;
import com.indiareads.retailapp.models.Bookshelf;
import com.indiareads.retailapp.singleton.VolleySingleton;
import com.indiareads.retailapp.utils.UrlsRetail;
import com.indiareads.retailapp.viewholder.WishListBookshelfListViewHolder;

import java.util.Collections;
import java.util.List;

/**
 * Created by vijay on 8/27/2016.
 */
public class WishListBookshelfListAdapterCorporate extends RecyclerView.Adapter<WishListBookshelfListViewHolderCorporate>{

    private WishListBookshelfClickListener clickListener;
    LayoutInflater mLayoutInflater;
    List<Bookshelf> bookList = Collections.emptyList();
    Context context;
    private ImageLoader mImageLoader;
    boolean toHide;


    public WishListBookshelfListAdapterCorporate(Context context, List<Bookshelf> bookItems,boolean toHide) {
        mLayoutInflater = LayoutInflater.from(context);
        this.bookList = bookItems;
        this.context = context;
        this.toHide=toHide;
    }

    @Override
    public WishListBookshelfListViewHolderCorporate onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.wishlist_bookshelf_item_corporate, parent, false);
        WishListBookshelfListViewHolderCorporate booksListViewHolder = new WishListBookshelfListViewHolderCorporate(view, clickListener);

        return booksListViewHolder;
    }


    @Override
    public void onBindViewHolder(WishListBookshelfListViewHolderCorporate booksListViewHolder, int position) {

        Bookshelf bookListItem = bookList.get(position);

//        if(bookListItem.getTitle()!=null) {
//            booksListViewHolder.getBookItemHeading().setText(bookListItem.getTitle());
//        }

        if(bookListItem.getTitle()!=null) {
            if(bookListItem.getTitle().length()>53){
                booksListViewHolder.getBookItemHeading().setText(bookListItem.getTitle().substring(0,50)+"...");
            }else{
                booksListViewHolder.getBookItemHeading().setText(bookListItem.getTitle());
            }

        }

        if(bookListItem.getContributor_name1()!=null) {
            booksListViewHolder.getBookItemAuthor().setText(bookListItem.getContributor_name1());
        }

        if(toHide){
            booksListViewHolder.getDeleteImageView().setVisibility(View.GONE);
        }

//        if(bookListItem.getShortDesc()!=null) {
//            booksListViewHolder.getBookSummary().setText(bookListItem.getShortDesc());
//        }


        String imageUrl= UrlsRetail.IMAGES+bookListItem.getIsbn13()+".jpg";
        mImageLoader = VolleySingleton.getInstance(context).getImageLoader();
        booksListViewHolder.getBookItemIcon().setDefaultImageResId(R.drawable.test);
        booksListViewHolder.getBookItemIcon().setImageUrl(imageUrl, mImageLoader);
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    public void setClickListener(WishListBookshelfClickListener clickListener) {
        this.clickListener = clickListener;
    }



}

