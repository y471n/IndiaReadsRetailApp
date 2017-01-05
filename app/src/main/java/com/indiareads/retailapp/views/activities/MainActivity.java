
package com.indiareads.retailapp.views.activities;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.indiareads.retailapp.R;
import com.indiareads.retailapp.utils.CommonMethods;
import com.indiareads.retailapp.utils.IndiaReadsApplication;

public class MainActivity extends AppCompatActivity  {

    private DrawerLayout mDrawerLayout;
    private NavigationView navigationView;
    private Toolbar mToolbar;
    SearchView searchView;
  //  private ListView searchListView;
  //  ArrayAdapter<String> adapter;
    // LinearLayout homeLinearLayout;

//    String books[] = {"fiction", "history", "prayer", "series", "health",
//            "comics", "festival",
//            "Sherbal", "humor", "children", "cooking"};

//    List<String> results =new ArrayList<String>();

    Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

      //  homeLinearLayout=(LinearLayout)findViewById(R.id.home_linear_layout);
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
      //  setClickListenerOnSearchView();
    }


//    public void setClickListenerOnSearchView(){
//        SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
//            public boolean onQueryTextChange(String newText) {
//
//                getSearchResult(newText);
////                createSearchList(newText);
//
//                return true;
//            }
//
//            public boolean onQueryTextSubmit(String query) {
//
//
//
//
//                return  true;
//            }
//        };
//        searchView.setOnQueryTextListener(queryTextListener);
//    }


//    public void createSearchList(String newText){
//        if(TextUtils.isEmpty(newText)) {
//            searchListView.setVisibility(View.GONE);
//            homeLinearLayout.setVisibility(View.VISIBLE);
//
//        }else{
//            homeLinearLayout.setVisibility(View.GONE);
//            results =new ArrayList<String>();
//            int i = 0;
//            while (i++ < books.length-1) {
//                if (books[i] .startsWith(newText)) {
//                    results.add(books[i]);
//                }
//            }
//            adapter = new ArrayAdapter<String>(context,android.R.layout.simple_list_item_1, results);
//            searchListView.setAdapter(adapter);
//            searchListView.setVisibility(View.VISIBLE);
//
//        }
//    }


//    public void setSearchListView(){
//
//        searchListView = (ListView) findViewById(R.id.search_list_view);
//        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, books);
//        searchListView.setAdapter(adapter);
//        setSearchListViewClickListener();
//    }
//
//    public void setSearchListViewClickListener(){
//
//        searchListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            public void onItemClick(AdapterView<?> listView, View itemView, int itemPosition, long itemId) {
//                Intent intent = new Intent(context, SearchableActivity.class);
//                startActivity(intent);
//            }
//        });
//    }

    public void displayDialog(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        // Setting Dialog Title
      //  alertDialog.setTitle("Confirm Delete...");

        // Setting Dialog Message
        alertDialog.setMessage("To Access this feature you have to sign in First!");

        // Setting Icon to Dialog
     //   alertDialog.setIcon(R.drawable.delete);

        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {

                // Write your code here to invoke YES event
                Toast.makeText(getApplicationContext(), "You clicked on YES", Toast.LENGTH_SHORT).show();
            }
        });

        // Setting Negative "NO" Button
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to invoke NO event
                Toast.makeText(getApplicationContext(), "You clicked on NO", Toast.LENGTH_SHORT).show();
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

//    private boolean isUserLogedIn() {
//        SharedPreferences settings = this.getSharedPreferences(UrlsRetail.SAVED_USER_DETAILS, this.MODE_PRIVATE);
//        if(settings.contains(UrlsRetail.SAVED_USER_TOKEN)){
//            return  true;
//        }else{
//            return  false;
//        }
//    }

//    public void bookShelfAction(){
//        if (isUserLogedIn()) {
//
//        } else {
//            displayDialog();
//        }
//    }

   public void startBookShelfActivity(){
      Intent  intent = new Intent(this, BookShelfActivity.class);
       intent.putExtra("OpenFragment", "0");
       startActivity(intent);
    }

//    public void returnBookAction(){
//        if (isUserLogedIn()) {
//            startReturnBookActivity();
//        } else {
//            displayDialog();
//        }
//    }

    public void startReturnBookActivity(){
       Intent intent = new Intent(this, BookShelfActivity.class);
        intent.putExtra("OpenFragment", "1");
        startActivity(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_items_full_main, menu);
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
                startBookShelfActivity();
                return true;
            case R.id.action_return_books:
                startReturnBookActivity();

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