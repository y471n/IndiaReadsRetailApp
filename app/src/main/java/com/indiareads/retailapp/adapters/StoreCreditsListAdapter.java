package com.indiareads.retailapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.indiareads.retailapp.R;
import com.indiareads.retailapp.interfaces.ClickListener;
import com.indiareads.retailapp.models.Address;
import com.indiareads.retailapp.models.StoreCredits;
import com.indiareads.retailapp.viewholder.AddressViewHolder;
import com.indiareads.retailapp.viewholder.StoreCreditsViewHolder;

import java.util.Collections;
import java.util.List;

/**
 * Created by vijay on 9/4/2015.
 */
public class StoreCreditsListAdapter extends RecyclerView.Adapter<StoreCreditsViewHolder> {


    List<StoreCredits> storeCreditsList = Collections.emptyList();
    LayoutInflater mLayoutInflater;
    Context context;
    private ClickListener clickListener;

    public StoreCreditsListAdapter(Context context, List<StoreCredits> storeCreditsList){
        this.storeCreditsList=storeCreditsList;
        this.context=context;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public StoreCreditsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.activity_store_credits_list_items, parent, false);
        StoreCreditsViewHolder storeCreditsViewHolder = new StoreCreditsViewHolder(view, clickListener);


        return storeCreditsViewHolder;
    }


    @Override
    public void onBindViewHolder(StoreCreditsViewHolder storeCreditsViewHolder, int position) {

        StoreCredits storeCreditItem = storeCreditsList.get(position);


        storeCreditsViewHolder.getIssueDate()
                .setText(storeCreditItem.getWhen());

        storeCreditsViewHolder.getAmount().setText(storeCreditItem.getStore_credit());

        storeCreditsViewHolder.getOrderId().
                setText(storeCreditItem.getBookshelf_order_id());

        if(storeCreditItem.getWhy_id().equals("4")){
            if(storeCreditItem.getStatus().equals("1")){
                storeCreditsViewHolder.getStatus().setText("On Hold");
            }
            else if(storeCreditItem.getStatus().equals("2")){
                storeCreditsViewHolder.getStatus().setText("Approved");
            }else{
                storeCreditsViewHolder.getStatus().setText("NA");
            }
        }else if(storeCreditItem.getWhy_id().equals("8")){
            if(storeCreditItem.getStatus().equals("1")){
                storeCreditsViewHolder.getStatus().setText("Return Pending");
            }
            else if(storeCreditItem.getStatus().equals("2")){
                storeCreditsViewHolder.getStatus().setText("Approved");
            }else{
                storeCreditsViewHolder.getStatus().setText("NA");
            }
        }else if(storeCreditItem.getWhy_id().equals("11")){
            storeCreditsViewHolder.getStatus().setText("Order Cancelled");
        }
    }

    @Override
    public int getItemCount() {
        return storeCreditsList.size();
    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }


}
