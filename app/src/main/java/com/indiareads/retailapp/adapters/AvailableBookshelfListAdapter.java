package com.indiareads.retailapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.toolbox.ImageLoader;
import com.indiareads.retailapp.R;
import com.indiareads.retailapp.interfaces.AvailableBookshelfClickListener;
import com.indiareads.retailapp.models.Bookshelf;
import com.indiareads.retailapp.singleton.VolleySingleton;
import com.indiareads.retailapp.utils.UrlsRetail;
import com.indiareads.retailapp.viewholder.AvailableBookshelfListViewHolder;

import java.util.Collections;
import java.util.List;

/**
 * Created by vijay on 9/2/2015.
 */
public class AvailableBookshelfListAdapter  extends RecyclerView.Adapter<AvailableBookshelfListViewHolder>{

    private AvailableBookshelfClickListener clickListener;
    LayoutInflater mLayoutInflater;
    List<Bookshelf> bookList = Collections.emptyList();
    Context context;
    private ImageLoader mImageLoader;

    public AvailableBookshelfListAdapter(Context context, List<Bookshelf> bookItems) {
        mLayoutInflater = LayoutInflater.from(context);
        this.bookList = bookItems;
        this.context = context;
    }

    @Override
    public AvailableBookshelfListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.available_book_list_item, parent, false);
        AvailableBookshelfListViewHolder booksListViewHolder = new AvailableBookshelfListViewHolder(view, clickListener);

        return booksListViewHolder;
    }

    @Override
    public void onBindViewHolder(AvailableBookshelfListViewHolder booksListViewHolder, int position) {

        Bookshelf bookListItem = bookList.get(position);

        if(bookListItem.isSelected()){
            booksListViewHolder.getAddToCartImageView().setImageResource(R.drawable.checked);
        }else{
            booksListViewHolder.getAddToCartImageView().setImageResource(R.drawable.unchecked);
        }

        if(bookListItem.getTitle()!=null) {
            if(bookListItem.getTitle().length()>53){
                booksListViewHolder.getBookItemHeading().setText(bookListItem.getTitle().substring(0,50)+"...");
            }else{
                booksListViewHolder.getBookItemHeading().setText(bookListItem.getTitle());
            }

        }

        if(bookListItem.getMrp()!=null) {
            booksListViewHolder.getBookMrp().setText(bookListItem.getMrp());
        }

        if(bookListItem.getRentStartsFrom()!=null) {
            booksListViewHolder.getBookRentStartsFrom().setText(bookListItem.getRentStartsFrom());
        }
        if(bookListItem.getInit_pay()!=null) {
            booksListViewHolder.getBookInitialPaid().setText(bookListItem.getInit_pay());
        }

        if(bookListItem.getContributor_name1()!=null) {
            booksListViewHolder.getBookItemAuthor().setText(bookListItem.getContributor_name1());
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

    public void setClickListener(AvailableBookshelfClickListener clickListener) {
        this.clickListener = clickListener;
    }


}
