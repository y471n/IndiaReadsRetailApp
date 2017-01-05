package com.indiareads.retailapp.interfaces;

import android.view.View;

/**
 * Created by yatin on 29/06/15.
 */
public interface CurrentlyReadingClickListener {
    public void itemClicked(View view, int position);
    public void onReturn(View deleteView, int position);
}

