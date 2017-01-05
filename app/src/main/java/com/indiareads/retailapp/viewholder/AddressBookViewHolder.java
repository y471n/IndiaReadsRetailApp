package com.indiareads.retailapp.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.indiareads.retailapp.R;
import com.indiareads.retailapp.interfaces.ClickListener;

/**
 * Created by vijay on 9/23/2015.
 */
public class AddressBookViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {




    private TextView name;
    private TextView addressLine1;
    private TextView addressLine2;
    private TextView pincode;
    private TextView phoneNumber;





    public TextView getState() {
        return state;
    }

    public void setState(TextView state) {
        this.state = state;
    }

    public TextView getCity() {
        return city;
    }

    public void setCity(TextView city) {
        this.city = city;
    }

    private TextView state;
    private TextView city;


    private ClickListener clickListener;

    public TextView getName() {
        return name;
    }

    public TextView getAddressLine1() {
        return addressLine1;
    }

    public TextView getAddressLine2() {
        return addressLine2;
    }

    public TextView getPincode() {
        return pincode;
    }

    public TextView getPhoneNumber() {

        return phoneNumber;
    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }


    public AddressBookViewHolder(View itemView, ClickListener clickListener) {
        super(itemView);

        itemView.setOnClickListener(this);
        this.clickListener = clickListener;

        this.name = (TextView) itemView.findViewById(R.id.name);
        this.addressLine1 = (TextView) itemView.findViewById(R.id.address_line_1);
        this.addressLine2 = (TextView) itemView.findViewById(R.id.address_line_2);
        this.pincode = (TextView) itemView.findViewById(R.id.pincode);
        this.phoneNumber = (TextView) itemView.findViewById(R.id.phone_number);
        this.state = (TextView) itemView.findViewById(R.id.state);
        this.city = (TextView) itemView.findViewById(R.id.city);

    }

    @Override
    public void onClick(View v) {
        if (clickListener != null) {
            clickListener.itemClicked(v, getAdapterPosition());
        }
    }

}