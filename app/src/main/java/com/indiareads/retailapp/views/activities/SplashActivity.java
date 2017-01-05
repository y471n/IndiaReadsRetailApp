
package com.indiareads.retailapp.views.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.View;

import com.indiareads.retailapp.R;
import com.indiareads.retailapp.corporate.view.activity.MainActivityCorporate;
import com.indiareads.retailapp.utils.CommonMethods;
import com.indiareads.retailapp.utils.UrlsRetail;


public class SplashActivity extends AppCompatActivity {

    private View leftView,centerView,rightView;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

       context=this;


        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        Log.e("width is",width+"");
        Log.e("height is",height+"");

        fetchView();  // to fetch the view
//        showHashKey(this);
        startThread(); // to start a new thread


    }


    private void fetchView(){
        leftView=(View)findViewById(R.id.progress_bar_left_view);
        centerView=(View)findViewById(R.id.progress_bar_center_view);
        rightView=(View)findViewById(R.id.progress_bar_right_view);
    }

    private void startThread() {

        Thread thread = new Thread(){
            public void run(){
                try {

                    changeColorLeftView();
                    sleep(1000);
                    changeColorCenterView();
                    sleep(1000);
                    changeColorRightView();
                    sleep(1000);
                    checkUserIsAlreadyLoginOrNot();
                }catch (Exception e){
                    e.printStackTrace();

                }
            }
        };
        thread.start();
    }

    private void startLoginActivity() {
          Intent i =new Intent(SplashActivity.this,MainLoginActivity.class);
          startActivity(i);
          finish();
    }

    public void checkUserIsAlreadyLoginOrNot(){
       // SharedPreferences settings = getSharedPreferences(UrlsRetail.SAVED_USER_DETAILS, MODE_PRIVATE);
        if(CommonMethods.isUserLogedIn(context)){
            if(getUserType().equals("1")) {
                startRetailHomeActivity();
            }else{
                startCorporateHomeActivity();
            }

        }else{
            startLoginActivity();
        }
    }

    public String getUserType(){
        SharedPreferences settings = getSharedPreferences(UrlsRetail.SAVED_USER_DETAILS, MODE_PRIVATE);
        // Reading from SharedPreferences
        return settings.getString(UrlsRetail.SAVED_USER_TYPE,"");
    }

    private void startRetailHomeActivity() {
        Intent intent = new Intent(context, MainActivity.class);
        startActivity(intent);
        finish();
    }
    private void startCorporateHomeActivity() {
        Intent intent = new Intent(context, MainActivityCorporate.class);
        startActivity(intent);
        finish();
    }

    private boolean checkForLoggedInUser() {
        String loggedInUser = getLoggedInUser();
        if(loggedInUser.equals("-1") || loggedInUser.equals("")){
            return false;
        }
        return true;
    }

    @NonNull
    private String getLoggedInUser() {

        return null;
    }

    private void changeColorLeftView(){

        SplashActivity.this.runOnUiThread(new Runnable() {
            @Override
            public final void run(){
                leftView.setBackgroundColor(getResources().getColor(R.color.red));
                centerView.setBackgroundColor(getResources().getColor(R.color.black));
                rightView.setBackgroundColor(getResources().getColor(R.color.black));
            }
        });
    }




    private void changeColorCenterView(){
        SplashActivity.this.runOnUiThread(new Runnable() {
            @Override
            public final void run() {
                leftView.setBackgroundColor(getResources().getColor(R.color.black));
                centerView.setBackgroundColor(getResources().getColor(R.color.red));
                rightView.setBackgroundColor(getResources().getColor(R.color.black));
            }
        });

    }

    private void changeColorRightView(){

        SplashActivity.this.runOnUiThread(new Runnable() {
            @Override
            public final void run() {
                leftView.setBackgroundColor(getResources().getColor(R.color.black));
                centerView.setBackgroundColor(getResources().getColor(R.color.black));
                rightView.setBackgroundColor(getResources().getColor(R.color.red));
            }
        });


    }
    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    //        DisplayMetrics displaymetrics = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
//        int height = displaymetrics.heightPixels;
//        int width = displaymetrics.widthPixels;
    //   density = getResources().getDisplayMetrics().density;

    //  Log.e("density is ", getResources().getDisplayMetrics().density+"");


//  public static void showHashKey(Context context) {
//        try {
//            PackageInfo info = context.getPackageManager().getPackageInfo(
//                    "com.indiareads.retailapp", PackageManager.GET_SIGNATURES); //Your package name here
//            for (Signature signature : info.signatures) {
//                MessageDigest md = null;
//                try {
//                    md = MessageDigest.getInstance("SHA");
//                } catch (NoSuchAlgorithmException e) {
//                    e.printStackTrace();
//                }
//                md.update(signature.toByteArray());
//                Log.v("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
//            }
//        } catch (PackageManager.NameNotFoundException e) {
//        }
//    }


}
