package com.indiareads.retailapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.indiareads.retailapp.R;
import com.indiareads.retailapp.interfaces.ClickListener;
import com.indiareads.retailapp.models.Address;
import com.indiareads.retailapp.viewholder.AddressBookViewHolder;

import java.util.Collections;
import java.util.List;

/**
 * Created by vijay on 9/23/2015.
 */
public class AddressBookListAdapter extends RecyclerView.Adapter<AddressBookViewHolder>{

    List<Address> addressList = Collections.emptyList();
    LayoutInflater mLayoutInflater;
    Context context;
    private ClickListener clickListener;

    public AddressBookListAdapter(Context context, List<Address> addressList){
        this.addressList=addressList;
        this.context=context;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public AddressBookViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.address_book_list_item, parent, false);
        AddressBookViewHolder addressBookViewHolder = new AddressBookViewHolder(view, clickListener);


        return addressBookViewHolder;
    }

    @Override
    public void onBindViewHolder(AddressBookViewHolder addressViewHolder, int position) {

        Address addressItem = addressList.get(position);

        //    mImageLoader = VolleySingleton.getInstance(context).getImageLoader();  ***********

        addressViewHolder.getName().setText(addressItem.getFullname());
        addressViewHolder.getAddressLine1().setText(addressItem.getAddress_line1());
        addressViewHolder.getAddressLine2().setText(addressItem.getAddress_line2());
        addressViewHolder.getPincode().setText(addressItem.getPincode());
        addressViewHolder.getPhoneNumber().setText(addressItem.getPhone());
        addressViewHolder.getState().setText(addressItem.getState());
        addressViewHolder.getCity().setText(addressItem.getCity());

    }

    @Override
    public int getItemCount() {
        return addressList.size();
    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }


}
