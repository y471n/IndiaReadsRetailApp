package com.indiareads.retailapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.toolbox.ImageLoader;
import com.indiareads.retailapp.R;
import com.indiareads.retailapp.interfaces.ClickListener;
import com.indiareads.retailapp.models.Bookshelf;
import com.indiareads.retailapp.singleton.VolleySingleton;
import com.indiareads.retailapp.utils.UrlsRetail;
import com.indiareads.retailapp.viewholder.ReadingHistoryBookshelfListViewHolder;

import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by vijay on 9/3/2015.
 */
public class ReadingHistoryBookshelfAdapter extends RecyclerView.Adapter<ReadingHistoryBookshelfListViewHolder>{

    private ClickListener clickListener;
    LayoutInflater mLayoutInflater;
    List<Bookshelf> bookList = Collections.emptyList();
    Context context;
    private ImageLoader mImageLoader;

    public ReadingHistoryBookshelfAdapter(Context context, List<Bookshelf> bookItems) {
        mLayoutInflater = LayoutInflater.from(context);
        this.bookList = bookItems;
        this.context = context;
    }


    @Override
    public ReadingHistoryBookshelfListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.reading_history_list_item, parent, false);
        ReadingHistoryBookshelfListViewHolder booksListViewHolder = new ReadingHistoryBookshelfListViewHolder(view, clickListener);

        return booksListViewHolder;
    }


    @Override
    public void onBindViewHolder(ReadingHistoryBookshelfListViewHolder booksListViewHolder, int position) {

        Bookshelf bookListItem = bookList.get(position);

        if(bookListItem.getTitle()!=null) {
            booksListViewHolder.getBookItemHeading().setText(bookListItem.getTitle());
        }

        if(bookListItem.getContributor_name1()!=null) {
            booksListViewHolder.getBookItemAuthor().setText(bookListItem.getContributor_name1());
        }


        if(bookListItem.getUserRentedThisBookCost()!=null) {
            booksListViewHolder.getUserRentedThisBookCost().setText(bookListItem.getUserRentedThisBookCost());
        }

        if(bookListItem.getBookMrp()!=null) {
            booksListViewHolder.getBookMrp().setText(bookListItem.getBookMrp());
        }

        if(bookListItem.getRentStartDate()!=null){
            booksListViewHolder.getRentStartDate().setText((new StringTokenizer(bookListItem.getRentStartDate()).nextToken()));
        }

        if(bookListItem.getRentEndDate()!=null){
            booksListViewHolder.getRentEndDate().setText((new StringTokenizer(bookListItem.getRentEndDate()).nextToken()));
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