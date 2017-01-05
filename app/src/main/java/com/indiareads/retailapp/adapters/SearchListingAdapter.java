package com.indiareads.retailapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.toolbox.ImageLoader;
import com.indiareads.retailapp.R;
import com.indiareads.retailapp.interfaces.ClickListener;
import com.indiareads.retailapp.models.Book;
import com.indiareads.retailapp.singleton.VolleySingleton;
import com.indiareads.retailapp.utils.UrlsRetail;
import com.indiareads.retailapp.viewholder.CategoryListingListViewHolder;

import java.util.Collections;
import java.util.List;

/**
 * Created by vijay on 5/9/2016.
 */
public class SearchListingAdapter extends RecyclerView.Adapter<CategoryListingListViewHolder>{


    private ClickListener clickListener;
    LayoutInflater mLayoutInflater;
    List<Book> bookList = Collections.emptyList();
    Context context;
    private ImageLoader mImageLoader;

    public SearchListingAdapter(Context context, List<Book> bookItems) {
        mLayoutInflater = LayoutInflater.from(context);
        this.bookList = bookItems;
        this.context = context;
    }

    @Override
    public CategoryListingListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.activity_category_list_item, parent, false);
        CategoryListingListViewHolder booksListViewHolder = new CategoryListingListViewHolder(view, clickListener);
        return booksListViewHolder;
    }

    @Override
    public void onBindViewHolder(CategoryListingListViewHolder booksListViewHolder, int position) {

        Book bookListItem = bookList.get(position);
        if(bookListItem.getTitle()!=null) {
            booksListViewHolder.getBookHeading().setText(bookListItem.getTitle());
        }

        if(bookListItem.getAuthor1()!=null){
            booksListViewHolder.getBookAuthor().setText(bookListItem.getAuthor1());
        }


        if(bookListItem.getRent()!=null) {
            if (bookListItem.getRent().getMrp() != null) {
                booksListViewHolder.getBookMrp().setText(bookListItem.getRent().getMrp());
            }
        }

        if (bookListItem.getRent() != null) {
            if (bookListItem.getRent().getRent() != null) {
                booksListViewHolder.getBookRent().setText(bookListItem.getRent().getRent()[0]);
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

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }
}
