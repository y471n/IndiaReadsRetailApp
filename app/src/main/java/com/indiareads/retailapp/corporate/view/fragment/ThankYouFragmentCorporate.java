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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.activeandroid.query.Delete;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.indiareads.retailapp.R;
import com.indiareads.retailapp.corporate.models.CartModelCorporate;
import com.indiareads.retailapp.corporate.models.UserCompleteOrderCorporate;
import com.indiareads.retailapp.corporate.view.activity.ContactUsActivityCorporate;
import com.indiareads.retailapp.corporate.view.activity.MainActivityCorporate;
import com.indiareads.retailapp.models.Address;
import com.indiareads.retailapp.models.CartModel;
import com.indiareads.retailapp.models.UserCompleteOrder;
import com.indiareads.retailapp.singleton.VolleySingleton;
import com.indiareads.retailapp.utils.CommonMethods;
import com.indiareads.retailapp.utils.DefaultMaxRetries;
import com.indiareads.retailapp.utils.IndiaReadsApplication;
import com.indiareads.retailapp.utils.UrlsCorporate;
import com.indiareads.retailapp.utils.UrlsRetail;
import com.indiareads.retailapp.views.activities.ContactUsActivity;
import com.indiareads.retailapp.views.activities.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

public class ThankYouFragmentCorporate extends Fragment {

    View fragmentRootView;
    ProgressDialog PD;
    String paymentId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentRootView= inflater.inflate(R.layout.fragment_thank_you_corporate, container, false);

        setRetryButtonClickListener();
        checkInternetConnectivity();
        setContactUsClickListener();

        return fragmentRootView;
    }


    private void checkInternetConnectivity() {
        if(!CommonMethods.hasActiveInternetConnection(getActivity())){
            showNetworkNotAvailable();
        }else{
            executeOtherMethods();
            sendAnalaytics();
        }
    }
    public String getCorporateType(){
        SharedPreferences settings = getActivity().getSharedPreferences(UrlsRetail.SAVED_USER_DETAILS, getActivity().MODE_PRIVATE);
        // Reading from SharedPreferences
        return settings.getString(UrlsRetail.SAVED_CORPORATE_TYPE,"");
    }

    public String getRentType(){
        SharedPreferences settings = getActivity().getSharedPreferences(UrlsRetail.SAVED_USER_DETAILS, getActivity().MODE_PRIVATE);
        // Reading from SharedPreferences
        return settings.getString(UrlsRetail.SAVED_RENT_TYPE,"");
    }


    private void sendAnalaytics() {
        IndiaReadsApplication application = (IndiaReadsApplication) getActivity().getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName("Thanku Page corporate");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    public void showNetworkNotAvailable(){
        (fragmentRootView.findViewById(R.id.error_linear_layout)).setVisibility(View.VISIBLE);
        ((TextView)fragmentRootView.findViewById(R.id.message)).setText(R.string.no_internet_connection_placing_order);
        (fragmentRootView.findViewById(R.id.complete_layout)).setVisibility(View.GONE);
    }

    private void setRetryButtonClickListener() {
        fragmentRootView.findViewById(R.id.retry_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentRootView.findViewById(R.id.error_linear_layout).setVisibility(View.GONE);
                fragmentRootView.findViewById(R.id.complete_layout).setVisibility(View.VISIBLE);
                executeOtherMethods();
            }
        });
    }

    private void setContactUsClickListener() {
        fragmentRootView.findViewById(R.id.contact_us).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), ContactUsActivityCorporate
                        .class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        });
    }



    private void executeOtherMethods() {

        if(getCorporateType().equals("1") && getRentType().equals("2")) {
            paymentId=getActivity().getIntent().getExtras().getString("paymentId");
            showLoadingScreen();
            showOrderPlacingDialog();
            runCodApi();
            setProceedToHomePageClickListener();
        }else{
            showLoadingScreen();
            showOrderPlacingDialog();
            setProceedToHomePageClickListener();
            runCodApi();
        }

    }


    public void showOrderPlacingDialog(){
        PD = new ProgressDialog(getActivity());
        PD.setTitle(getResources().getString(R.string.progress_dialog_please_wait));
        PD.setMessage(getResources().getString(R.string.placing_your_order));
        PD.setCancelable(false);
        PD.show();
    }

    private void setProceedToHomePageClickListener() {
        fragmentRootView.findViewById(R.id.thank_you_continue_shopping_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startMainActivity();
            }
        });
    }
    private void startMainActivity() {
        Intent intent = new Intent(getActivity(), MainActivityCorporate.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


    public void showLoadingScreen(){
        (fragmentRootView.findViewById(R.id.thank_you_loading_screen)).setVisibility(View.VISIBLE);
        (fragmentRootView.findViewById(R.id.thank_you_content)).setVisibility(View.GONE);
    }
    public void hideLoadingScreen(){
        (fragmentRootView.findViewById(R.id.thank_you_loading_screen)).setVisibility(View.GONE);
        (fragmentRootView.findViewById(R.id.thank_you_content)).setVisibility(View.VISIBLE);
    }

    public void setData(JSONObject fetchedJson) throws JSONException {
        UserCompleteOrder userCompleteOrder =  ((IndiaReadsApplication) getActivity().getApplication()).getUserCompleteOrderObject();
        fillAddressDetails(userCompleteOrder);
        fillChargesDetails(userCompleteOrder);
        fillPaymentDetails(userCompleteOrder);
        fillBookDetails(userCompleteOrder);
        fillOtherDetails(userCompleteOrder, fetchedJson);
    }

    public void fillOtherDetails(UserCompleteOrder userCompleteOrder,JSONObject fetchedJson) throws JSONException {

        ((TextView)fragmentRootView.findViewById(R.id.order_amount)).setText(String.valueOf(userCompleteOrder.getTotalPayable()));
        ((TextView) fragmentRootView.findViewById(R.id.order_id)).setText(fetchedJson.getString("order_id"));
        ((TextView)fragmentRootView.findViewById(R.id.order_date)).setText(getCurrentSystemDate());

    }

    public String getCurrentSystemDate(){
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        //get current date time with Date()
        Date date = new Date();
        StringTokenizer st=new StringTokenizer(dateFormat.format(date));

        return st.nextToken();
    }


    public void fillBookDetails(UserCompleteOrder userCompleteOrder){

        List<CartModel> bookList=userCompleteOrder.getUserBook();
        for(int i=0;i<bookList.size();i++) {
            View bookView = getActivity().getLayoutInflater().inflate(R.layout.display_book_linear_layout, null);
            TextView bookName = (TextView) bookView.findViewById(R.id.book_name);
            TextView bookCost = (TextView) bookView.findViewById(R.id.book_cost);
            bookName.setText(bookList.get(i).getTitle());
            if(bookList.get(i).getToBuy().equals("1")) {
                long buyFinalPrice= Long.parseLong((bookList.get(i)).getBuy_mrp().trim()) -Long.parseLong(((bookList.get(i)).getBuy_discount().trim()));
                bookCost.setText(String.valueOf(buyFinalPrice));
            }else{
                bookCost.setText(bookList.get(i).getInitialPayable());
            }

            ((LinearLayout) fragmentRootView.findViewById(R.id.linear_layout_for_dynamic_books)).addView(bookView);
        }

    }

    public void fillAddressDetails(UserCompleteOrder userCompleteOrder){

        Address address=userCompleteOrder.getUserAddress();
        ((TextView)fragmentRootView.findViewById(R.id.name)).setText(address.getFullname());
        ((TextView)fragmentRootView.findViewById(R.id.address_line_1)).setText(address.getAddress_line1());
        ((TextView)fragmentRootView.findViewById(R.id.address_line_2)).setText(address.getAddress_line2());
        ((TextView)fragmentRootView.findViewById(R.id.pincode)).setText(address.getPincode());
        ((TextView)fragmentRootView.findViewById(R.id.phone_number)).setText(address.getPhone());
        ((TextView)fragmentRootView.findViewById(R.id.city)).setText(address.getCity());
        ((TextView)fragmentRootView.findViewById(R.id.state)).setText(address.getState());
    }

    public void fillChargesDetails(UserCompleteOrder userCompleteOrder){
        ((TextView)fragmentRootView.findViewById(R.id.shipping_charge)).setText(String.valueOf(userCompleteOrder.getShippingCharge()));
        ((TextView)fragmentRootView.findViewById(R.id.store_credit)).setText(String.valueOf(userCompleteOrder.getStoreCreditDiscount()));
//        ((TextView)fragmentRootView.findViewById(R.id.cod_charge)).setText(String.valueOf(userCompleteOrder.getCodCharge()));
        ((TextView)fragmentRootView.findViewById(R.id.coupon_discount)).setText(String.valueOf(userCompleteOrder.getCouponDiscount()));
        ((TextView)fragmentRootView.findViewById(R.id.total_payable)).setText(String.valueOf(userCompleteOrder.getTotalPayable()));

    }

    public void fillPaymentDetails(UserCompleteOrder userCompleteOrder){
        if(userCompleteOrder.getTotalPayable()==0) {
            ((TextView) fragmentRootView.findViewById(R.id.payment_mode)).setText(UrlsRetail.STORE_CREDIT_PAYMENT);

        }else{
            ((TextView) fragmentRootView.findViewById(R.id.payment_mode)).setText(userCompleteOrder.getPaymentMethod());
        }
//        ((TextView)fragmentRootView.findViewById(R.id.payment_mode)).setText(userCompleteOrder.getPaymentMethod());
    }

    public void runCodApi(){
        sendCodDetailsToServer(UrlsCorporate.PLACE_ORDER);
    }

    private void sendCodDetailsToServer(String urls) {
        JsonObjectRequest jsonObjectRequest = null;
        jsonObjectRequest = getJsonRequest(urls);
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(jsonObjectRequest);
    }

    private JsonObjectRequest getJsonRequest(String url) {
        JSONObject obj= createJsonObject();
//        if(getCorporateType().equals("1") && getRentType().equals("2")) {

        Log.e("created object",obj.toString());

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url,obj, getResponseListener(), new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                PD.dismiss();
                if(error instanceof NoConnectionError) {
                    showNetworkNotAvailable();
                }else{
                    (fragmentRootView.findViewById(R.id.error_linear_layout)).setVisibility(View.VISIBLE);
                    ((TextView)fragmentRootView.findViewById(R.id.message)).setText(R.string.error_in_placing_order);
                    (fragmentRootView.findViewById(R.id.complete_layout)).setVisibility(View.GONE);
                }
            }
        });

        jsonRequest.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultMaxRetries.APP_API_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        return jsonRequest;
    }



    @NonNull
    private Response.Listener<JSONObject> getResponseListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    PD.dismiss();

                    JSONObject fetchedJson = new JSONObject(response.toString());
                    setData(fetchedJson);
                    hideLoadingScreen();
                    makeTheCartEmpty();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }



    private void makeTheCartEmpty() {
        new Delete().from(CartModel.class).execute();
    }




    public JSONObject createJsonObject(){

        UserCompleteOrderCorporate userCompleteOrder =  ((IndiaReadsApplication) getActivity().getApplication()).getUserCompleteOrderObjectCorporate();
        JSONObject completeDetails=new JSONObject();
        try{
            completeDetails.put("uid",getUserId());
            completeDetails.put("from","AndroidApp");
            completeDetails.put("address_id",userCompleteOrder.getUserAddress().getAddress_book_id());
            //    completeDetails.put("mrp", getMrp(userCompleteOrder) + "");

            if(getCorporateType().equals("1") && getRentType().equals("2")) {
                if (userCompleteOrder.getTotalPayable() == 0) {
                    completeDetails.put("payment_status", "1");
                    completeDetails.put("payment_option", "5");
                } else if (userCompleteOrder.getPaymentMethod().equals(UrlsRetail.CASH_ON_DELIVERY_PAYMENT)) {
                    completeDetails.put("payment_status", "0");
                    completeDetails.put("payment_option", "1");
                } else if (userCompleteOrder.getPaymentMethod().equals(UrlsRetail.PAY_ONLINE_PAYMENT)) {
                    completeDetails.put("payment_status", "1");
                    completeDetails.put("payment_option", "2");
                } else if (userCompleteOrder.getPaymentMethod().equals(UrlsRetail.PAY_TM_PAYMENT)) {
                    completeDetails.put("payment_status", "1");
                    completeDetails.put("payment_option", "3");
                } else if (userCompleteOrder.getPaymentMethod().equals(UrlsRetail.Pay_U_Money_PAYMENT)) {
                    completeDetails.put("payment_status", "1");
                    completeDetails.put("payment_option", "4");
                }
                completeDetails.put("transaction_id",paymentId);
                completeDetails.put("net_pay",userCompleteOrder.getTotalPayable());
            }else {
                completeDetails.put("payment_status", "");
                completeDetails.put("payment_option", "");
                completeDetails.put("transaction_id","");
                completeDetails.put("net_pay","");
            }

            completeDetails.put("coupon_discount","");
            completeDetails.put("store_credit_discount","");
            completeDetails.put("shipping_charge","");
            completeDetails.put("price","");
            completeDetails.put("coupon_code", "");


            completeDetails.put("cart",getCartArray(userCompleteOrder));


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return  completeDetails;
    }



    public JSONArray getCartArray(UserCompleteOrderCorporate userCompleteOrder) throws JSONException {
        JSONArray cart = new JSONArray();
        List<CartModelCorporate> bookList=userCompleteOrder.getUserBook();
        for(int i=0;i<bookList.size();i++){
            cart.put(getBookObject(bookList.get(i)));
        }

        return cart;
    }


    public JSONObject getBookObject(CartModelCorporate book) throws JSONException {
        JSONObject bookObject1 = new JSONObject();
        bookObject1.put("book_id", book.getBookID());
        bookObject1.put("book_name", book.getTitle());
        bookObject1.put("isbn13", book.getIsbn13());
        bookObject1.put("author", book.getAuthor1());
        if(getCorporateType().equals("1") && getRentType().equals("2")) {
            bookObject1.put("amount_pay", getFixedPrice());
        }else{
            bookObject1.put("amount_pay","");
        }

        bookObject1.put("buy_status", "");
        bookObject1.put("discount_pay", "");
        bookObject1.put("store_pay",  "");
        bookObject1.put("initial_payable", "");
        bookObject1.put("mrp","");

        return bookObject1;
    }

    public String getFixedPrice(){
        SharedPreferences settings = getActivity().getSharedPreferences(UrlsRetail.SAVED_USER_DETAILS, getActivity().MODE_PRIVATE);
        // Reading from SharedPreferences
        return settings.getString(UrlsRetail.SAVED_COMPANY_FIX_PRICE,"");
    }


    public String getUserId(){
        SharedPreferences settings = getActivity().getSharedPreferences(UrlsRetail.SAVED_USER_DETAILS, getActivity().MODE_PRIVATE);
        // Reading from SharedPreferences
        return settings.getString(UrlsRetail.SAVED_USER_ID,"");
    }

//*****************************************************************************************************
//    private JSONObject createJsonObjectWithOutPrice() {
//        UserCompleteOrderCorporate userCompleteOrder =  ((IndiaReadsApplication) getActivity().getApplication()).getUserCompleteOrderObjectCorporate();
//        JSONObject completeDetails=new JSONObject();
//        try{
//            completeDetails.put("uid",getUserId());
//            completeDetails.put("from","AndroidApp");
//            completeDetails.put("address_id",userCompleteOrder.getUserAddress().getAddress_book_id());
//            completeDetails.put("cart",getCartArrayWithOutPrice(userCompleteOrder));
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        return  completeDetails;
//    }

    public JSONArray getCartArrayWithOutPrice(UserCompleteOrderCorporate userCompleteOrder) throws JSONException {
        JSONArray cart = new JSONArray();
        List<CartModelCorporate> bookList=userCompleteOrder.getUserBook();
        for(int i=0;i<bookList.size();i++){
            cart.put(getBookObjectWithOutPrice(bookList.get(i)));
        }

        return cart;
    }

    public JSONObject getBookObjectWithOutPrice(CartModelCorporate book) throws JSONException {
        JSONObject bookObject1 = new JSONObject();
        bookObject1.put("book_id", book.getBookID());
        bookObject1.put("book_name", book.getTitle());
        bookObject1.put("isbn13", book.getIsbn13());
        bookObject1.put("author", book.getAuthor1());
        return bookObject1;
    }

}
