package com.indiareads.retailapp.utils;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Configuration;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.indiareads.retailapp.R;
import com.indiareads.retailapp.corporate.models.UserCompleteOrderCorporate;
import com.indiareads.retailapp.models.Address;
import com.indiareads.retailapp.models.Book;
import com.indiareads.retailapp.models.UserCompleteOrder;

/**
 * Created by vijay on 11/3/2015.
 */
public class IndiaReadsApplication extends android.app.Application {

    UserCompleteOrder userCompleteOrderObject;
    public UserCompleteOrder getUserCompleteOrderObject(){
        return userCompleteOrderObject;
    }

    public UserCompleteOrderCorporate getUserCompleteOrderObjectCorporate() {
        return userCompleteOrderObjectCorporate;
    }

    UserCompleteOrderCorporate userCompleteOrderObjectCorporate;



    private Tracker mTracker;


    public void setUserCompleteOrderObject(UserCompleteOrder userCompleteOrderObject){
        this.userCompleteOrderObject=userCompleteOrderObject;
    }

    public void initializeUserCompleteOrderObject(){
        userCompleteOrderObject=new UserCompleteOrder();
    }

    public void initializeUserCompleteOrderObjectCorporate(){
        userCompleteOrderObjectCorporate=new UserCompleteOrderCorporate();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        FontsOverride.setDefaultFont(this, "MONOSPACE", "Roboto-Regular.ttf");
        ActiveAndroid.initialize(this);
//        Configuration dbConfiguration = new Configuration.Builder(this).setDatabaseName("ireads_database.db").create();
//        ActiveAndroid.initialize(dbConfiguration);
    }

    synchronized public Tracker getDefaultTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
            mTracker = analytics.newTracker(R.xml.global_tracker);
        }
        return mTracker;
    }
}