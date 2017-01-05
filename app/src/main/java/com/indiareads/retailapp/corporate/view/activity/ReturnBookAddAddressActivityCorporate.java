package com.indiareads.retailapp.corporate.view.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.indiareads.retailapp.R;
import com.indiareads.retailapp.views.activities.ChoosePickUpAddressActivity;

public class ReturnBookAddAddressActivityCorporate extends AppCompatActivity {


    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_return_book_add_address_corporate);
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
                startAddressBook();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startAddressBook();
    }

    public void startAddressBook(){
        Intent i=new Intent(this,ChoosePickUpAddressActivityCorporate.class);
        i.putExtra("list",String.valueOf(getIntent().getExtras().get("list")));
        startActivity(i);
        finish();
    }


}
