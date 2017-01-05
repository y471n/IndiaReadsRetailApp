package com.indiareads.retailapp.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.indiareads.retailapp.views.fragments.AvailableBookshelfFragment;
import com.indiareads.retailapp.views.fragments.CurrentlyReadingBookshelfFragment;
import com.indiareads.retailapp.views.fragments.ReadingHistoryBookshelfFragment;
import com.indiareads.retailapp.views.fragments.WishListBookshelfFragment;

/**
 * Created by vijay on 1/9/15.
 */
public class BookshelfViewPagerAdapter extends FragmentStatePagerAdapter {

    int mNumberOfTabs;
    CharSequence mTitles[];

    public BookshelfViewPagerAdapter(FragmentManager fm, CharSequence titles[], int numberOfTabs) {
        super(fm);

        this.mTitles = titles;
        this.mNumberOfTabs = numberOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 1:
                return new CurrentlyReadingBookshelfFragment();
            case 2:
                return new WishListBookshelfFragment();
            case 3:
                return new ReadingHistoryBookshelfFragment();
            default:
                return new AvailableBookshelfFragment();
        }

    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles[position];
    }

    @Override
    public int getCount() {
        return mNumberOfTabs;
    }
}
