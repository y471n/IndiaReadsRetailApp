package com.indiareads.retailapp.interfaces;

import android.view.View;

/**
 * Created by vijay on 9/25/2015.
 */
public interface WishListBookshelfClickListener {
    public void onDelete(View deleteView, int position);
    public void itemClicked(View view, int position);
}
