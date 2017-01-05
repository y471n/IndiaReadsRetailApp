package com.indiareads.retailapp.corporate.view.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.indiareads.retailapp.R;
import com.indiareads.retailapp.views.activities.MainActivity;

public class ReturnSummaryActivityCorporate extends AppCompatActivity {

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_return_summary_corporate);
        createToolbar();

    }

    private void createToolbar() {
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
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
