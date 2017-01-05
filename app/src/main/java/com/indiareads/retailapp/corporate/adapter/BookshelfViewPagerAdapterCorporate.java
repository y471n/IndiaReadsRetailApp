package com.indiareads.retailapp.corporate.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.indiareads.retailapp.corporate.view.fragment.AvailableBookshelfFragmentCorporate;
import com.indiareads.retailapp.corporate.view.fragment.CurrentlyReadingBookshelfFragmentCorporate;
import com.indiareads.retailapp.corporate.view.fragment.ReadingHistoryBookshelfFragmentCorporate;
import com.indiareads.retailapp.corporate.view.fragment.WishListBookshelfFragmentCorporate;

/**
 * Created by vijay on 8/26/2016.
 */
public class BookshelfViewPagerAdapterCorporate  extends FragmentStatePagerAdapter {

    int mNumberOfTabs;
    CharSequence mTitles[];

    public BookshelfViewPagerAdapterCorporate(FragmentManager fm, CharSequence titles[], int numberOfTabs) {
        super(fm);

        this.mTitles = titles;
        this.mNumberOfTabs = numberOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 1:
                return new CurrentlyReadingBookshelfFragmentCorporate();
            case 2:
                return new WishListBookshelfFragmentCorporate();
            case 3:
                return new ReadingHistoryBookshelfFragmentCorporate();
            default:
                return new AvailableBookshelfFragmentCorporate();
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
