package com.indiareads.retailapp.corporate.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.toolbox.ImageLoader;
import com.indiareads.retailapp.R;
import com.indiareads.retailapp.corporate.viewholder.CategoryListingListViewHolderCorporate;
import com.indiareads.retailapp.interfaces.ClickListener;
import com.indiareads.retailapp.models.BookListing;
import com.indiareads.retailapp.singleton.VolleySingleton;
import com.indiareads.retailapp.utils.UrlsRetail;
import com.indiareads.retailapp.viewholder.CategoryListingListViewHolder;

import java.util.Collections;
import java.util.List;

/**
 * Created by vijay on 8/23/2016.
 */
public class CategoryListingAdapterCorporate extends RecyclerView.Adapter<CategoryListingListViewHolderCorporate>{


    private ClickListener clickListener;
    LayoutInflater mLayoutInflater;
    List<BookListing> bookList = Collections.emptyList();
    Context context;
    private ImageLoader mImageLoader;

    public CategoryListingAdapterCorporate(Context context, List<BookListing> bookItems) {
        mLayoutInflater = LayoutInflater.from(context);
        this.bookList = bookItems;
        this.context = context;
    }

    @Override
    public CategoryListingListViewHolderCorporate onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.activity_category_list_item_corporate, parent, false);
        CategoryListingListViewHolderCorporate booksListViewHolder = new CategoryListingListViewHolderCorporate(view, clickListener);
        return booksListViewHolder;
    }

    @Override
    public void onBindViewHolder(CategoryListingListViewHolderCorporate booksListViewHolder, int position) {

        BookListing bookListItem = bookList.get(position);

        if(bookListItem.getTitle()!=null) {
            if(bookListItem.getTitle().length()>53){
                booksListViewHolder.getBookHeading().setText(bookListItem.getTitle().substring(0,50)+"...");
            }else{
                booksListViewHolder.getBookHeading().setText(bookListItem.getTitle());
            }
        }

        if(bookListItem.getContributor_name1()!=null){
            booksListViewHolder.getBookAuthor().setText(bookListItem.getContributor_name1());
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

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }
}

