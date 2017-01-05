package com.indiareads.retailapp.corporate.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.indiareads.retailapp.R;
import com.indiareads.retailapp.views.activities.ChooseDeliveryAddressActivity;

public class AddAddressActivityCorporate extends AppCompatActivity {

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address_corporate);
        createToolbar();
    }

    private void createToolbar() {
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                startChooseDeliveryAddressActivity();

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startChooseDeliveryAddressActivity();
    }

    public void startChooseDeliveryAddressActivity(){
        Intent i=new Intent(this,ChooseDeliveryAddressActivityCorporate.class);
        startActivity(i);
        finish();
    }



}
