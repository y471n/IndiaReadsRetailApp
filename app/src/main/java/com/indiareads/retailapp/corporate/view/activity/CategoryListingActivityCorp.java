package com.indiareads.retailapp.corporate.view.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
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
import com.indiareads.retailapp.singleton.VolleySingleton;
import com.indiareads.retailapp.utils.CommonMethods;
import com.indiareads.retailapp.utils.IndiaReadsApplication;
import com.indiareads.retailapp.utils.UrlsRetail;
import com.indiareads.retailapp.views.activities.BookShelfActivity;
import com.indiareads.retailapp.views.activities.LoginActivityMenu;
import com.indiareads.retailapp.views.activities.MainActivity;
import com.indiareads.retailapp.views.activities.MyAccountActivity;
import com.indiareads.retailapp.views.activities.MyCartActivity;
import com.indiareads.retailapp.views.activities.MyOrderActivity;
import com.indiareads.retailapp.views.activities.StoreCreditsActivity;

public class CategoryListingActivityCorp extends AppCompatActivity {


    Toolbar toolbar;
    SearchView searchView;
    Context context;
    String categoryName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_listing_corp);
        context=this;
        fetchCategoryNameFromIntent();
        createToolbar();
    }
    private void fetchCategoryNameFromIntent() {
//        if() {
        categoryName = getIntent().getExtras().getString("CategoryName");
        sendAnalaytics();
//        }
    }

    private void sendAnalaytics() {
        IndiaReadsApplication application = (IndiaReadsApplication) getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName("Category Page corporate "+categoryName);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    private void createToolbar() {
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(categoryName);
        toolbar.setLogo(R.drawable.logo);
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
        getMenuInflater().inflate(R.menu.menu_items_corporate_with_home, menu);

        setUpSearchView(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
//
            case R.id.action_book_shelf:
                intent = new Intent(this, BookShelfActivityCorporate.class);
                intent.putExtra("OpenFragment", "0");
                startActivity(intent);
                return true;
            case R.id.action_return_books:
                intent = new Intent(this, BookShelfActivityCorporate.class);
                intent.putExtra("OpenFragment", "1");
                startActivity(intent);
                return true;
            case R.id.action_my_orders:
                intent = new Intent(this, MyOrderActivityCorporate.class);
                startActivity(intent);
                return true;
            case R.id.my_profile:
                intent = new Intent(this, MyAccountActivityCorporate.class);
                startActivity(intent);
                return true;
            case R.id.action_cart:
                intent = new Intent(this, MyCartActivityCorporate.class);
                startActivity(intent);
                return true;
            case R.id.action_home:
                cancelRequest();
                intent = new Intent(this, MainActivityCorporate.class);
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

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        invalidateOptionsMenu();
    }

    public void cancelRequest(){
        RequestQueue mQueue= VolleySingleton.getInstance(this).getRequestQueue();
        mQueue.cancelAll(UrlsRetail.CATEGORY_LISTING_VOLLEY_REQUEST_TAG);
    }
}

