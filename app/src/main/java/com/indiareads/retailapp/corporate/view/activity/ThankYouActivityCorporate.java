package com.indiareads.retailapp.corporate.view.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

import com.indiareads.retailapp.R;
import com.indiareads.retailapp.views.activities.MainActivity;

public class ThankYouActivityCorporate extends AppCompatActivity {

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thank_you_corporate);


        createToolbar();


    }

    private void createToolbar() {
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //   getMenuInflater().inflate(R.menu.menu_items_short, menu);
        return true;
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startMainActivity();
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivityCorporate.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
