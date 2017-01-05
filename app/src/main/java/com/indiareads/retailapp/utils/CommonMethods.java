package com.indiareads.retailapp.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.widget.Button;

/**
 * Created by vijay on 4/8/2016.
 */
public class CommonMethods {

    public static boolean isUserLogedIn(Context context) {
        SharedPreferences settings = context.getSharedPreferences(UrlsRetail.SAVED_USER_DETAILS, context.MODE_PRIVATE);
        if(settings.contains(UrlsRetail.SAVED_USER_ID)){
            return  true;
        }else{
            return  false;
        }
    }

    public static boolean isUserLogedIn(Activity context) {
        SharedPreferences settings = context.getSharedPreferences(UrlsRetail.SAVED_USER_DETAILS, context.MODE_PRIVATE);
        if(settings.contains(UrlsRetail.SAVED_USER_ID)){
            return  true;
        }else{
            return  false;
        }
    }

    public static void showNoInternetConnectionDialog(final Activity activity, final Intent intent){
    final AlertDialog   alertDialog = new AlertDialog.Builder(activity).create();
        alertDialog.setTitle("No Internet Connection");
        alertDialog.setMessage("Sorry, No Internet Connectivity detected. Please reconnect and try again");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "RETRY",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {

                Button b = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (hasActiveInternetConnection(activity)) {
                           // Log.e("internet  conected", "done");
                            //  dialog.dismiss();
                            restartActivity(activity, intent);
                        } else {
                           // Log.e("internet not conected", "done");
                            alertDialog.dismiss();
                            showNoInternetConnectionDialog(activity,intent);
                        }
                    }
                });
            }
        });
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();

    }

    public static void restartActivity(final Activity activity, final Intent intent){
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        activity.startActivity(intent);
        activity.overridePendingTransition(0, 0);
        activity.finish();
    }


    public static boolean hasActiveInternetConnection(Activity context) {
        if (isNetworkAvailable(context)) {
//            try {
//                HttpURLConnection urlc = (HttpURLConnection)
//                        (new URL("http://clients3.google.com/generate_204")
//                                .openConnection());
//                urlc.setRequestProperty("User-Agent", "Android");
//                urlc.setRequestProperty("Connection", "close");
//                urlc.setConnectTimeout(1500);
//                urlc.connect();
//                return (urlc.getResponseCode() == 204 &&
//                        urlc.getContentLength() == 0);
//            } catch (IOException e) {
////                Log.e(TAG, "Error checking internet connection", e);
//            }
            return true;
        } else {
//            Log.d(TAG, "No network available!");
        }
        return false;
    }

    private static boolean isNetworkAvailable(Activity context) {
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            return  true;
        }
        else
            return false;
    }
}
