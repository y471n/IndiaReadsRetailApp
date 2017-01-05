package com.indiareads.retailapp.corporate.view.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.indiareads.retailapp.R;
import com.indiareads.retailapp.models.Address;
import com.indiareads.retailapp.models.Order;
import com.indiareads.retailapp.singleton.VolleySingleton;
import com.indiareads.retailapp.utils.DefaultMaxRetries;
import com.indiareads.retailapp.utils.UrlsCorporate;
import com.indiareads.retailapp.utils.UrlsRetail;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.StringTokenizer;

public class OrderDetailsFragmentCorporate extends Fragment {

    View fragmentRootView;
    Order order;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentRootView= inflater.inflate(R.layout.fragment_order_details_corporate, container, false);

        order=(Order)getActivity().getIntent().getSerializableExtra("order");
        showLoadingScreen();
        fetchAddress();
        fillOrderDetails(order);


//        fillDetails();

        return fragmentRootView;
    }

    public void showLoadingScreen(){
        fragmentRootView.findViewById(R.id.order_detail_complete_layout).setVisibility(View.GONE);
        fragmentRootView.findViewById(R.id.order_detail_page_loading_screen).setVisibility(View.VISIBLE);
    }

    private void hideLoadingScreen() {
        fragmentRootView.findViewById(R.id.order_detail_complete_layout).setVisibility(View.VISIBLE);
        fragmentRootView.findViewById(R.id.order_detail_page_loading_screen).setVisibility(View.GONE);
    }


    private void fetchAddress() {
        fetchAddressFromStorage();
    }

    public void fetchAddressFromStorage(){

        fetchAddressFromRemoteServer(UrlsCorporate.GET_PARTICULAR_ADDRESS);
    }

    public String getUserToken(){
        SharedPreferences settings = getActivity().getSharedPreferences(UrlsRetail.SAVED_USER_DETAILS, getActivity().MODE_PRIVATE);
        // Reading from SharedPreferences
        return settings.getString(UrlsRetail.SAVED_USER_TOKEN,"");
    }

    private void fetchAddressFromRemoteServer(String urls) {
        JsonObjectRequest jsonObjectRequest;
        try {
            jsonObjectRequest = getAddressJsonRequest(urls);
            jsonObjectRequest.setTag(UrlsCorporate.GET_PARTICULAR_ADDRESS_VOLLEY_TAG);
            VolleySingleton.getInstance(getActivity()).addToRequestQueue(jsonObjectRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private JsonObjectRequest getAddressJsonRequest(String url) throws JSONException {

        JSONObject obj=new JSONObject();
        obj.put("address_id", order.getOrder_address_id());
        //Log.e("json is ",obj.toString());

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url,obj, getAddressResponseListener(), new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "error" + error.toString(), Toast.LENGTH_LONG).show();
            }
        });

        jsonRequest.setRetryPolicy(new DefaultRetryPolicy(
                20000,
                DefaultMaxRetries.APP_API_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        return jsonRequest;
    }


    @NonNull
    private Response.Listener<JSONObject> getAddressResponseListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    JSONObject fetchedJson = new JSONObject(response.toString());
                    Log.e("particular address",fetchedJson.toString());
                    fetchData(fetchedJson);
                    hideLoadingScreen();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    private void fetchData(JSONObject fetchedJson) {
        try{
            Gson gson = new Gson();
            JSONObject data=fetchedJson.getJSONObject("data");
            Address address = gson.fromJson(data.toString(), Address.class);
            fillAddressDetails(address);


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void fillAddressDetails(Address address){
        ((TextView)fragmentRootView.findViewById(R.id.name)).setText(address.getFullname());
        ((TextView)fragmentRootView.findViewById(R.id.address_line_1)).setText(address.getAddress_line1());
        ((TextView)fragmentRootView.findViewById(R.id.address_line_2)).setText(address.getAddress_line2());
        ((TextView)fragmentRootView.findViewById(R.id.pincode)).setText(address.getPincode());
        ((TextView)fragmentRootView.findViewById(R.id.phone_number)).setText(address.getPhone());
        ((TextView)fragmentRootView.findViewById(R.id.city)).setText(address.getCity());
        ((TextView)fragmentRootView.findViewById(R.id.state)).setText(address.getState());
    }


    public String getUserId(){
        SharedPreferences settings = getActivity().getSharedPreferences(UrlsRetail.SAVED_USER_DETAILS, getActivity().MODE_PRIVATE);
        // Reading from SharedPreferences
        return settings.getString(UrlsRetail.SAVED_USER_ID,"");
    }


    public void fillOrderDetails(Order order){
        ((TextView)fragmentRootView.findViewById(R.id.order_id)).setText(order.getBookshelf_order_id());
        ((TextView)fragmentRootView.findViewById(R.id.order_placed_date)).setText(new StringTokenizer(order.getOrder_date()).nextToken());
        ((TextView)fragmentRootView.findViewById(R.id.my_order_book_name)).setText(order.getTitle());
        ((TextView)fragmentRootView.findViewById(R.id.my_order_author_name)).setText(order.getContributor_name1());
        if(order.getD_track_id()!=null) {
            ((TextView) fragmentRootView.findViewById(R.id.tracking_id)).setText(order.getD_track_id());
        }
        if(order.getCarrier()!=null) {
            ((TextView) fragmentRootView.findViewById(R.id.carrier_name)).setText(order.getCarrier());
        }


        if(order.getOrder_status()!=null) {
            if(order.getOrder_status().equals("0")){
                ((TextView)fragmentRootView.findViewById(R.id.status_name)).setText("Incomplete Order");
                (fragmentRootView.findViewById(R.id.status_date)).setVisibility(View.GONE);
                (fragmentRootView.findViewById(R.id.dot_text)).setVisibility(View.GONE);
            }else if(order.getOrder_status().equals("-1")){
                ((TextView)fragmentRootView.findViewById(R.id.status_name)).setText("Cancelled");
                (fragmentRootView.findViewById(R.id.status_date)).setVisibility(View.GONE);
                (fragmentRootView.findViewById(R.id.dot_text)).setVisibility(View.GONE);
            }else if(order.getOrder_status().equals("8")) {
                ((TextView)fragmentRootView.findViewById(R.id.status_name)).setText("Returned");
                ((TextView)fragmentRootView.findViewById(R.id.status_date)).setText((new StringTokenizer(order.getReturned()).nextToken()));
            } else if(order.getOrder_status().equals("7")) {
                ((TextView)fragmentRootView.findViewById(R.id.status_name)).setText("Send To Courier");
                ((TextView)fragmentRootView.findViewById(R.id.status_date)).setText((new StringTokenizer(order.getReq_to_fedex()).nextToken()));
            } else if(order.getOrder_status().equals("6")) {
                ((TextView)fragmentRootView.findViewById(R.id.status_name)).setText("Pick Up Processing");
                ((TextView)fragmentRootView.findViewById(R.id.status_date)).setText((new StringTokenizer(order.getPickup_processing()).nextToken()));
            }else if(order.getOrder_status().equals("5")) {
                ((TextView)fragmentRootView.findViewById(R.id.status_name)).setText("Pick Up request Receveied");
                ((TextView)fragmentRootView.findViewById(R.id.status_date)).setText((new StringTokenizer(order.getNew_pickup()).nextToken()));
            }else if(order.getOrder_status().equals("4")) {
                ((TextView)fragmentRootView.findViewById(R.id.status_name)).setText("Delivered");
                ((TextView)fragmentRootView.findViewById(R.id.status_date)).setText((new StringTokenizer(order.getDelivered()).nextToken()));
            }else if(order.getOrder_status().equals("3")) {
                ((TextView)fragmentRootView.findViewById(R.id.status_name)).setText("Dispatched");
                ((TextView)fragmentRootView.findViewById(R.id.status_date)).setText((new StringTokenizer(order.getDispatched()).nextToken()));
            }else if(order.getOrder_status().equals("2")) {
                ((TextView)fragmentRootView.findViewById(R.id.status_name)).setText("Preparing For Dispatch");
                ((TextView)fragmentRootView.findViewById(R.id.status_date)).setText((new StringTokenizer(order.getDelivery_processing()).nextToken()));
            }else if(order.getOrder_status().equals("1")) {
                ((TextView)fragmentRootView.findViewById(R.id.status_name)).setText("Request Received");
                ((TextView)fragmentRootView.findViewById(R.id.status_date)).setText((new StringTokenizer(order.getNew_delivery()).nextToken()));
            }
        }


    }

}
