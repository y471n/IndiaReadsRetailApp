package com.indiareads.retailapp.corporate.view.activity;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.indiareads.retailapp.R;
import com.indiareads.retailapp.utils.CommonMethods;
import com.indiareads.retailapp.utils.IndiaReadsApplication;
import com.indiareads.retailapp.views.activities.BookShelfActivity;
import com.indiareads.retailapp.views.activities.LoginActivityMenu;
import com.indiareads.retailapp.views.activities.MyAccountActivity;
import com.indiareads.retailapp.views.activities.MyCartActivity;
import com.indiareads.retailapp.views.activities.MyOrderActivity;
import com.indiareads.retailapp.views.activities.StoreCreditsActivity;

public class MainActivityCorporate extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private NavigationView navigationView;
    private Toolbar mToolbar;
    SearchView searchView;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_activity_corporate);
        Log.e("inside","corpoarete main activity");

        checkFinishAllActivitiesFromStack();
        context=this;
        initializeNavigationDrawerAndToolbar();
        sendAnalaytics();

    }

    private void sendAnalaytics() {
        IndiaReadsApplication application = (IndiaReadsApplication) getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName("Home Page");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }


    private void checkFinishAllActivitiesFromStack() {
        if (getIntent().hasExtra("EXIT") && getIntent().getBooleanExtra("EXIT", false)) {
            finish();
        }
    }

    private void initializeNavigationDrawerAndToolbar() {
        // Initializing Toolbar and setting it as the actionbar
        mToolbar = setUpToolbar();

        //Initializing NavigationView
        navigationView = (NavigationView) findViewById(R.id.navigation_view);

        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        setUpNavigationClickListeners();

        // Initializing Drawer Layout and ActionBarToggle
        setUpNavDrawer();
    }


    private void setUpNavDrawer() {
        if (mToolbar != null) {
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            mToolbar.setNavigationIcon(R.drawable.drawer_icon);
            mDrawerLayout = (DrawerLayout) findViewById(R.id.main_drawer_layout);
            ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(
                    this,  mDrawerLayout, mToolbar,
                    R.string.navigation_drawer_open, R.string.navigation_drawer_close
            );
            mDrawerLayout.setDrawerListener(mDrawerToggle);
            mDrawerToggle.syncState();

            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                        mDrawerLayout.openDrawer(GravityCompat.START);
                        mDrawerLayout.bringToFront();
                    }else{
                        mDrawerLayout.closeDrawer(GravityCompat.START);
                    }
                }
            });
        }
    }

    private void setUpNavigationClickListeners() {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                switch (menuItem.getItemId()) {
//                    case R.id.category_1:
//                        Toast.makeText(MainActivity.this, "user profile", Toast.LENGTH_SHORT).show();
//                        return true;
//                    case R.id.category_2:
//                        Toast.makeText(MainActivity.this, "queries", Toast.LENGTH_SHORT).show();
//                        return true;
                    default:
                        return true;
                }
            }
        });
    }

    private Toolbar setUpToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        toolbar.setLogo(R.drawable.logo);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        return toolbar;
    }


    private void setUpSearchView(Menu menu) {

        // setSearchListView();
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
        getMenuInflater().inflate(R.menu.menu_items_corporate, menu);
        setUpSearchView(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {

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
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        invalidateOptionsMenu();
    }

}
