package com.indiareads.retailapp.corporate.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.toolbox.ImageLoader;
import com.indiareads.retailapp.R;
import com.indiareads.retailapp.corporate.viewholder.PickUpViewHolderCorporate;
import com.indiareads.retailapp.interfaces.ClickListener;
import com.indiareads.retailapp.models.Bookshelf;
import com.indiareads.retailapp.singleton.VolleySingleton;
import com.indiareads.retailapp.utils.UrlsRetail;
import com.indiareads.retailapp.viewholder.PickUpViewHolder;

import java.util.Collections;
import java.util.List;

/**
 * Created by vijay on 8/30/2016.
 */
public class PickupDetailsAdapterCorporate extends RecyclerView.Adapter<PickUpViewHolderCorporate> {

    List<Bookshelf> pickupList = Collections.emptyList();
    LayoutInflater mLayoutInflater;
    Context context;
    private ClickListener clickListener;
    private ImageLoader mImageLoader;

    public PickupDetailsAdapterCorporate(Context context, List<Bookshelf> pickupList){
        this.pickupList=pickupList;
        this.context=context;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public PickUpViewHolderCorporate onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.pickup_details_card_corporate, parent, false);
        PickUpViewHolderCorporate pickupViewHolder = new PickUpViewHolderCorporate(view, clickListener);


        return pickupViewHolder;
    }

    @Override
    public void onBindViewHolder(PickUpViewHolderCorporate pickupViewHolder, int position) {

        Bookshelf PickupItem = pickupList.get(position);

        //    mImageLoader = VolleySingleton.getInstance(context).getImageLoader();  ***********

        pickupViewHolder.getNameOfBook().setText(PickupItem.getTitle());
        pickupViewHolder.getNameOfAuthor().setText(PickupItem.getContributor_name1());

        String imageUrl= UrlsRetail.IMAGES+PickupItem.getIsbn13()+".jpg";
        mImageLoader = VolleySingleton.getInstance(context).getImageLoader();
        pickupViewHolder.getBookItemIcon().setDefaultImageResId(R.drawable.test);
        pickupViewHolder.getBookItemIcon().setImageUrl(imageUrl, mImageLoader);

    }

    @Override
    public int getItemCount() {
        return pickupList.size();
    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }
}
