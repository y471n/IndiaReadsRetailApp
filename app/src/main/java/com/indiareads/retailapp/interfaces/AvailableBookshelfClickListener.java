package com.indiareads.retailapp.interfaces;

import android.view.View;

/**
 * Created by yatin on 29/06/15.
 */
public interface AvailableBookshelfClickListener {
    public void itemClicked(View view, int position);
    public void onDelete(View deleteView, int position);
    public void addToCart(View addToCartView, int position);

}
