package com.indiareads.retailapp.views.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.android.volley.RequestQueue;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.indiareads.retailapp.R;
import com.indiareads.retailapp.singleton.VolleySingleton;
import com.indiareads.retailapp.utils.CommonMethods;
import com.indiareads.retailapp.utils.IndiaReadsApplication;
import com.indiareads.retailapp.utils.UrlsRetail;

public class ProductPageActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    SearchView searchView;
    LinearLayout productPageLinearLayout;
    Context context;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_page);
        productPageLinearLayout=(LinearLayout)findViewById(R.id.product_page_linear_layout);
        context=this;
        createToolbar();

        sendAnalaytics();
    }

    private void sendAnalaytics() {
      String  isbnNumber=getIntent().getExtras().getString("isbn_num");
        IndiaReadsApplication application = (IndiaReadsApplication) getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName("Product page "+isbnNumber);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }


    private void createToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setLogo(R.drawable.logo);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

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
        if(CommonMethods.isUserLogedIn(context)) {
            getMenuInflater().inflate(R.menu.menu_items_full_main_with_home_and_login, menu);
        }else{
            getMenuInflater().inflate(R.menu.menu_user_with_out_login, menu);
        }
        setUpSearchView(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
//            case R.id.action_login:
//                intent = new Intent(this, MainLoginActivity.class);
//                startActivity(intent);
//                return true;
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
                RequestQueue mQueue= VolleySingleton.getInstance(this).getRequestQueue();
                mQueue.cancelAll(UrlsRetail.BOOK_DETAILS_VOLLEY_TAG);
                mQueue.cancelAll(UrlsRetail.SIMILAR_BOOKS_VOLLEY_REQUEST_TAG);
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
        mQueue.cancelAll(UrlsRetail.BOOK_DETAILS_VOLLEY_TAG);
        mQueue.cancelAll(UrlsRetail.SIMILAR_BOOKS_VOLLEY_REQUEST_TAG);
    }

}
