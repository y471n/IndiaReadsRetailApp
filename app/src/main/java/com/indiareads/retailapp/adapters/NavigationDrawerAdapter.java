package com.indiareads.retailapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.indiareads.retailapp.R;
import com.indiareads.retailapp.interfaces.ClickListener;
import com.indiareads.retailapp.models.NavigationItem;
import com.indiareads.retailapp.viewholder.NavigationDrawerViewHolder;

import java.util.Collections;
import java.util.List;

/**
 * Created by vijay on 8/17/2015.
 */
public class NavigationDrawerAdapter extends RecyclerView.Adapter<NavigationDrawerViewHolder> {

    LayoutInflater mLayoutInflater;
    List<NavigationItem> navigationItems = Collections.emptyList();
    private ClickListener clickListener;

    public NavigationDrawerAdapter(Context context, List<NavigationItem> navigationItems) {
        mLayoutInflater = LayoutInflater.from(context);
        this.navigationItems = navigationItems;
    }

    @Override
    public NavigationDrawerViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = mLayoutInflater.inflate(R.layout.navigation_drawer_item, viewGroup, false);
        NavigationDrawerViewHolder navigationDrawerViewHolder = new NavigationDrawerViewHolder(view, clickListener);

        return navigationDrawerViewHolder;
    }

    @Override
    public void onBindViewHolder(NavigationDrawerViewHolder navigationDrawerViewHolder, int position) {
        NavigationItem currentNavigationDrawerItem = navigationItems.get(position);

        navigationDrawerViewHolder.getNavigationItemHeading().setText(currentNavigationDrawerItem.getHeading());
    }

    @Override
    public int getItemCount() {
        return navigationItems.size();
    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }
}

