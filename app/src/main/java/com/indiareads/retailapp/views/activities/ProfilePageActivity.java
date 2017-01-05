package com.indiareads.retailapp.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.android.volley.RequestQueue;
import com.indiareads.retailapp.R;
import com.indiareads.retailapp.singleton.VolleySingleton;
import com.indiareads.retailapp.utils.UrlsRetail;


public class ProfilePageActivity extends AppCompatActivity {



      Toolbar toolbar;
    LinearLayout profilePageFragment;

//   public static Boolean dataAppears=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);
     //   invalidateOptionsMenu();

        profilePageFragment=(LinearLayout)findViewById(R.id.profile_page_fragment);

        createToolbar();

    }

    private void createToolbar() {
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setLogo(R.drawable.logo);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_profile:

                  return false;
            case android.R.id.home:
                RequestQueue mQueue= VolleySingleton.getInstance(this).getRequestQueue();
                mQueue.cancelAll(UrlsRetail.PROFILE_DETAILS_VOLLEY_REQUEST_TAG);
                startMyAccountActivity();

            default:
                break;
        }

        return false;
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        RequestQueue mQueue= VolleySingleton.getInstance(this).getRequestQueue();
        mQueue.cancelAll(UrlsRetail.PROFILE_DETAILS_VOLLEY_REQUEST_TAG);
        startMyAccountActivity();
    }

    public void startMyAccountActivity(){
        Intent i=new Intent(this,MyAccountActivity.class);
        startActivity(i);
        finish();
    }

//    @Override
//    public boolean onPrepareOptionsMenu (Menu menu) {
//        if (!dataAppears) {
//            menu.getItem(0).setEnabled(false);
//        }
//        else{
//            menu.getItem(0).setEnabled(true);
//        }
//        return true;
//    }

}
