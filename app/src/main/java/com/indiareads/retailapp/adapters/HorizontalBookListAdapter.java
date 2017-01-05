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
import com.indiareads.retailapp.viewholder.HorizontalBooksListViewHolder;

import java.util.Collections;
import java.util.List;

/**
 * Created by vijay on 7/29/2015.
 */
public class HorizontalBookListAdapter extends RecyclerView.Adapter<HorizontalBooksListViewHolder>{


    List<Book> bookList = Collections.emptyList();
    LayoutInflater mLayoutInflater;
    Context context;
    private ClickListener clickListener;
    private ImageLoader mImageLoader;


    public HorizontalBookListAdapter(Context context, List<Book> bookList){
           this.bookList=bookList;
           this.context=context;
           mLayoutInflater = LayoutInflater.from(context);

    }


    @Override
    public HorizontalBooksListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.horizontal_book_list_item, parent, false);
        HorizontalBooksListViewHolder horizontalBooksListViewHolder = new HorizontalBooksListViewHolder(view, clickListener);

        return horizontalBooksListViewHolder;
    }

    @Override
    public void onBindViewHolder(HorizontalBooksListViewHolder booksListViewHolder, int position) {

        Book bookListItem = bookList.get(position);

        mImageLoader = VolleySingleton.getInstance(context).getImageLoader();


        if(bookListItem.getTitle()!=null) {
            booksListViewHolder.getBookItemHeading().setText(bookListItem.getTitle());
        }


//
//        if(bookListItem.getTitle()!=null) {
//            if(bookListItem.getTitle().length()>25){
//                booksListViewHolder.getBookItemHeading().setText(bookListItem.getTitle().substring(0,24)+"..");
//            }else{
//                booksListViewHolder.getBookItemHeading().setText(bookListItem.getTitle());
//            }
//
//        }



        String imageUrl= UrlsRetail.IMAGES+bookListItem.getIsbn13()+".jpg";

        booksListViewHolder.getBookItemIcon().setDefaultImageResId(R.drawable.test);
        booksListViewHolder.getBookItemIcon().setImageUrl(imageUrl, mImageLoader);



      //  booksListViewHolder.getBookItemIcon().setDefaultImageResId(R.drawable.test);
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

}
