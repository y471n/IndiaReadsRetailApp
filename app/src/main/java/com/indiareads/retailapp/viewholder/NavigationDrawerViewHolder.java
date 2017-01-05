package com.indiareads.retailapp.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.indiareads.retailapp.R;
import com.indiareads.retailapp.interfaces.ClickListener;

/**
 * Created by vijay on 8/17/2015.
 */
public class NavigationDrawerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    private TextView navigationItemHeading;
    private ClickListener clickListener;

    public NavigationDrawerViewHolder(View itemView, ClickListener clickListener) {
        super(itemView);
        itemView.setOnClickListener(this);
        this.navigationItemHeading = (TextView) itemView.findViewById(R.id.navigation_drawer_item_heading);
        this.clickListener = clickListener;
    }

    public TextView getNavigationItemHeading() {
        return navigationItemHeading;
    }

    @Override
    public void onClick(View v) {
        if(clickListener != null) {
            clickListener.itemClicked(v, getAdapterPosition());
        }
    }
}
