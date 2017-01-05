package com.indiareads.retailapp.views.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.indiareads.retailapp.R;

import java.util.ArrayList;
import java.util.List;


public class MyAccountActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    SearchView searchView;


    LinearLayout homeLinearLayout;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);
        homeLinearLayout=(LinearLayout)findViewById(R.id.my_account_fragment);
        context=this;
        createToolbar();

    }

    private void createToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setLogo(R.drawable.logo);


    }

    private void setUpSearchView(Menu menu) {
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
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
            case R.id.action_home:
                intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            case android.R.id.home:
                finish();

            default:
                return super.onOptionsItemSelected(item);
        }


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
