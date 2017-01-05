package com.indiareads.retailapp.adapters;

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
 * Created by vijay on 9/3/2015.
 */
public class WishListBookshelfListAdapter extends RecyclerView.Adapter<WishListBookshelfListViewHolder>{

    private WishListBookshelfClickListener clickListener;
    LayoutInflater mLayoutInflater;
    List<Bookshelf> bookList = Collections.emptyList();
    Context context;
    private ImageLoader mImageLoader;


    public WishListBookshelfListAdapter(Context context, List<Bookshelf> bookItems) {
        mLayoutInflater = LayoutInflater.from(context);
        this.bookList = bookItems;
        this.context = context;
    }

    @Override
    public WishListBookshelfListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.wishlist_bookshelf_item, parent, false);
        WishListBookshelfListViewHolder booksListViewHolder = new WishListBookshelfListViewHolder(view, clickListener);

        return booksListViewHolder;
    }


    @Override
    public void onBindViewHolder(WishListBookshelfListViewHolder booksListViewHolder, int position) {

        Bookshelf bookListItem = bookList.get(position);

        if(bookListItem.getTitle()!=null) {
            booksListViewHolder.getBookItemHeading().setText(bookListItem.getTitle());
        }

        if(bookListItem.getContributor_name1()!=null) {
            booksListViewHolder.getBookItemAuthor().setText(bookListItem.getContributor_name1());
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