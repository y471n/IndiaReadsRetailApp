package com.indiareads.retailapp.corporate.view.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.indiareads.retailapp.R;
import com.indiareads.retailapp.adapters.PickupDetailsAdapter;
import com.indiareads.retailapp.corporate.adapter.PickupDetailsAdapterCorporate;
import com.indiareads.retailapp.corporate.view.activity.ReturnSummaryActivityCorporate;
import com.indiareads.retailapp.interfaces.ClickListener;
import com.indiareads.retailapp.models.Address;
import com.indiareads.retailapp.models.Bookshelf;
import com.indiareads.retailapp.singleton.VolleySingleton;
import com.indiareads.retailapp.utils.DefaultMaxRetries;
import com.indiareads.retailapp.utils.UrlsCorporate;
import com.indiareads.retailapp.utils.UrlsRetail;
import com.indiareads.retailapp.views.activities.ReturnSummaryActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;

public class PickUpDetailsFragmentCorporate extends Fragment {

    private View fragmentRootView;
    private RecyclerView mRecyclerView;
    private PickupDetailsAdapterCorporate pickupDetailsAdapter;

    List<Bookshelf> bookList;
    Address deliveryAddress;

    ProgressDialog PD;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentRootView= inflater.inflate(R.layout.fragment_pick_up_details_corporate, container, false);

        fetchBookListAndAddressFromIntent();
        createBookList();
        setPickUpDetailsClickListener();
        return fragmentRootView;
    }

    private void fetchBookListAndAddressFromIntent() {
        String json=(String)getActivity().getIntent().getExtras().get("list");
        deliveryAddress=(Address)getActivity().getIntent().getExtras().get("address");
        Type listType = new TypeToken<List<Bookshelf>>() {}.getType();
        bookList=new Gson().fromJson(json,listType);
    }

    private void setPickUpDetailsClickListener() {
        fragmentRootView.findViewById(R.id.pickup_details_proceed_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendReturnDetailsToServer();
            }
        });
    }

    private void createBookList() {
        createRecycleListView();
    }



    private void createRecycleListView() {
        mRecyclerView = (RecyclerView) fragmentRootView.findViewById(R.id.pickup_details_recycler_view);
        pickupDetailsAdapter = new PickupDetailsAdapterCorporate(getActivity(), bookList);
        mRecyclerView.setAdapter(pickupDetailsAdapter);
        pickupDetailsAdapter.setClickListener(new ClickListener() {
            @Override
            public void itemClicked(View view, int position) {
            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);

    }

    private void sendReturnDetailsToServer() {
        JsonObjectRequest jsonObjectRequest;
        showLoadingDialog();
        try {
            jsonObjectRequest = getReturnBookJsonRequest(UrlsCorporate.RETURN_BOOKS);
            VolleySingleton.getInstance(getActivity()).addToRequestQueue(jsonObjectRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void showLoadingDialog(){
        PD = new ProgressDialog(getActivity());
        PD.setTitle(getResources().getString(R.string.progress_dialog_please_wait));
        PD.setMessage(getResources().getString(R.string.puting_the_return_request));
        PD.setCancelable(false);
        PD.show();
    }

    private JsonObjectRequest getReturnBookJsonRequest(String url) throws JSONException {

        JSONObject obj=getJsonContainsOrderIdAndUserId();

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url,obj, getReturnBookResponseListener(), new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                PD.dismiss();
                  Log.e("error from pickup ", error.toString());
                if(error instanceof NoConnectionError) {
                    showNoInternetConnectionDialogForPutingPickUpRequest();
                }else{
                    errorDuringReturn();
                }
            }
        });

        jsonRequest.setRetryPolicy(new DefaultRetryPolicy(
                20000,
                DefaultMaxRetries.APP_API_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        return jsonRequest;
    }

    @NonNull
    private Response.Listener<JSONObject> getReturnBookResponseListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject fetchedJson = new JSONObject(response.toString());
                      Log.e("response pickup server ",fetchedJson.toString());
                    PD.dismiss();
                    startReturnSummaryActivity();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
    }



    private void startReturnSummaryActivity() {
        Intent intent = new Intent(getActivity(), ReturnSummaryActivityCorporate.class);
        Gson gson = new Gson();
        String json = gson.toJson(bookList);
        intent.putExtra("list", json);
        intent.putExtra("address", deliveryAddress);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    public JSONObject getJsonContainsOrderIdAndUserId() throws JSONException {
        JSONObject obj=new JSONObject();

        // puting user id object
        obj.put("user_id",bookList.get(0).getUser_id());
        obj.put("address_id",deliveryAddress.getAddress_book_id());
        obj.put("corporate_type",getCorporateType());

        JSONArray orderIdArray=new JSONArray();
        for(int i=0;i<bookList.size();i++){
            orderIdArray.put(bookList.get(i).getOrderID());
        }
        obj.put("book_self_order_id",orderIdArray);

        return obj;
    }

    public void errorDuringReturn(){
        AlertDialog dialog = new AlertDialog.Builder(getActivity()).create();
        dialog.setTitle("Oops! Something went wrong.");
        dialog.setMessage("Unable to place your request.");
        dialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        dialog.show();

    }

    public  void showNoInternetConnectionDialogForPutingPickUpRequest(){
        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setTitle("No Internet Connection");
        alertDialog.setMessage("Sorry, No Internet Connectivity detected. Please reconnect and try again");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "RETRY",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {

                Button b = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                Button b2 = alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL);
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (hasActiveInternetConnection(getActivity())) {
                            alertDialog.dismiss();
                            sendReturnDetailsToServer();
                        } else {
                            alertDialog.dismiss();
                            showNoInternetConnectionDialogForPutingPickUpRequest();
                        }
                    }
                });
                b2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
                });
            }
        });
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();

    }


    public  boolean hasActiveInternetConnection(Activity context) {
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


    public String getCorporateType(){
        SharedPreferences settings = getActivity().getSharedPreferences(UrlsRetail.SAVED_USER_DETAILS, getActivity().MODE_PRIVATE);
        // Reading from SharedPreferences
        return settings.getString(UrlsRetail.SAVED_CORPORATE_TYPE,"");
    }


}
