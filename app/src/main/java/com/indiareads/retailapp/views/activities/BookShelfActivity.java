package com.indiareads.retailapp.views.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.android.volley.RequestQueue;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.indiareads.retailapp.R;
import com.indiareads.retailapp.adapters.BookshelfViewPagerAdapter;
import com.indiareads.retailapp.singleton.VolleySingleton;
import com.indiareads.retailapp.slidingtab.SlidingTabLayout;
import com.indiareads.retailapp.utils.IndiaReadsApplication;
import com.indiareads.retailapp.utils.UrlsRetail;

public class BookShelfActivity extends AppCompatActivity {

    private SlidingTabLayout mSlidingTabLayout;
    private ViewPager mViewPager;
    private CharSequence[] tabTitles = {"Available Books", "Currently Reading", "Wishlist", "Reading History"};
    private int numberOfTabs = 4;
    private BookshelfViewPagerAdapter bookshelfViewPagerAdapter;
    private Toolbar toolbar;
    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_shelf);

        createTabbedView();
        createToolbarWithLogo();
        sendAnalaytics();

    }

    private void sendAnalaytics() {
        IndiaReadsApplication application = (IndiaReadsApplication) getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName("BookShelf");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

//    private void checkInternetConnectivity() {
//        if(!CommonMethods.hasActiveInternetConnection(this)){
//            CommonMethods.showNoInternetConnectionDialog(this, this.getIntent());
//            setNoInternetAccessBackgroundImage();
//        }else{
//
//        }
//    }

    private void setNoInternetAccessBackgroundImage() {
//        findViewById(R.id.app_screen_background_image).setVisibility(View.VISIBLE);
//        findViewById(R.id.book_shelf_complete_layout).setVisibility(View.GONE);
    }

    private void createToolbarWithLogo() {
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setLogo(R.drawable.logo);
    }


    private void createTabbedView() {
        createViewPager();
        createSlidingTabs();

        // Setting the ViewPager For the SlidingTabsLayout
        mSlidingTabLayout.setViewPager(mViewPager);
    }

    private void createSlidingTabs() {
        // Assign the Sliding Tab Layout View
        mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tab_bookshelf_activity);
        mSlidingTabLayout.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width

        // Setting Custom Color for the Scroll bar indicator of the Tab View
        mSlidingTabLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.sliding_tab);
            }
        });
    }

    private void createViewPager() {
        // Creating The ViewPagerAdapter and Passing Fragment Manager, Titles fot the Tabs and Number Of Tabs.
        bookshelfViewPagerAdapter =  new BookshelfViewPagerAdapter(getSupportFragmentManager(), tabTitles, numberOfTabs);
        // Assigning ViewPager View and setting the adapter
        mViewPager = (ViewPager) findViewById(R.id.view_pager_bookshelf_activity);
        mViewPager.setAdapter(bookshelfViewPagerAdapter);
        Bundle extras = getIntent().getExtras();
       if((extras.getString("OpenFragment").equals("1"))){
           mViewPager.setCurrentItem(1);
       }else{
           mViewPager.setCurrentItem(0);
       }


    }


    private void setUpSearchView(Menu menu) {
        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(true);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_items_full_main_with_home_and_login, menu);
        setUpSearchView(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.action_book_shelf:
                intent = new Intent(this, BookShelfActivity.class);
                intent.putExtra("OpenFragment", "0");
                startActivity(intent);
                return true;
            case R.id.action_return_books:
                intent = new Intent(this, BookShelfActivity.class);
                intent.putExtra("OpenFragment", "1");
                startActivity(intent);
                return true;
            case R.id.action_my_orders:
                intent = new Intent(this, MyOrderActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_store_credits:
                intent = new Intent(this, StoreCreditsActivity.class);
                startActivity(intent);
                return true;
            case R.id.my_profile:
                intent = new Intent(this, MyAccountActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_cart:
                intent = new Intent(this, MyCartActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_login:
                Intent i =new Intent(this,LoginActivityMenu.class);
                startActivity(i);
                return true;
            case R.id.action_home:
                cancelRequest();
                intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;
            case android.R.id.home:
                cancelRequest();
                finish();

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        cancelRequest();
        finish();
    }

    public void cancelRequest(){
        RequestQueue mQueue= VolleySingleton.getInstance(this).getRequestQueue();
        mQueue.cancelAll(UrlsRetail.BOOKSHELF_AVAILABLE_BOOKS_VOLLEY_REQUEST_TAG);
        mQueue.cancelAll(UrlsRetail.BOOKSHELF_CURRENTLY_READING_BOOKS_VOLLEY_REQUEST_TAG);
        mQueue.cancelAll(UrlsRetail.BOOKSHELF_WISHLIST_BOOKS_VOLLEY_REQUEST_TAG);
        mQueue.cancelAll(UrlsRetail.BOOKSHELF_READING_HISTORY_BOOKS_VOLLEY_REQUEST_TAG);
    }
}
