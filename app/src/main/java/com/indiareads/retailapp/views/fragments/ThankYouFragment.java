package com.indiareads.retailapp.views.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.indiareads.retailapp.models.Address;
import com.indiareads.retailapp.models.CartModel;
import com.indiareads.retailapp.models.UserCompleteOrder;
import com.indiareads.retailapp.singleton.VolleySingleton;
import com.indiareads.retailapp.utils.CommonMethods;
import com.indiareads.retailapp.utils.DefaultMaxRetries;
import com.indiareads.retailapp.utils.IndiaReadsApplication;
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


public class ThankYouFragment extends Fragment {

    View fragmentRootView;
    ProgressDialog PD;
    String paymentId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        fragmentRootView= inflater.inflate(R.layout.fragment_thank_you, container, false);

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

    private void sendAnalaytics() {
        IndiaReadsApplication application = (IndiaReadsApplication) getActivity().getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName("Thanku Page");
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
                Intent i = new Intent(getActivity(), ContactUsActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        });
    }

    private void setNoInternetAccessBackgroundImage() {
//        fragmentRootView.findViewById(R.id.app_screen_background_image).setVisibility(View.VISIBLE);
//        fragmentRootView.findViewById(R.id.thanku_page_complete_layout).setVisibility(View.GONE);
    }


    private void executeOtherMethods() {
        paymentId=getActivity().getIntent().getExtras().getString("paymentId");
        showLoadingScreen();
        showOrderPlacingDialog();
        runCodApi();
        setProceedToHomePageClickListener();
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
        Intent intent = new Intent(getActivity(), MainActivity.class);
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
        sendCodDetailsToServer(UrlsRetail.PLACE_ORDER);
    }

    private void sendCodDetailsToServer(String urls) {
        JsonObjectRequest jsonObjectRequest = null;
        jsonObjectRequest = getJsonRequest(urls);
        jsonObjectRequest.setTag(UrlsRetail.CASH_ON_DELIVERY_VOLLEY_REQUEST_TAG);
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(jsonObjectRequest);
    }

    private JsonObjectRequest getJsonRequest(String url) {

        JSONObject obj=createJsonObject();
        Log.e("json is ",obj.toString());

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url,obj, getResponseListener(), new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.e("error thnaku us ",error.toString());
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
                    //Log.e("response thanku is ",fetchedJson.toString());
                  //  String status=fetchedJson.getString("status");
//                    if(status.equals("success")) {
                        setData(fetchedJson);
                        hideLoadingScreen();
                        makeTheCartEmpty();
//                    }else{
//                        startOrderFailedActivity();
//                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }


    private void startOrderFailedActivity() {
//        Intent intent = new Intent(getActivity(), OrderFailedActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(intent);
    }
    private void makeTheCartEmpty() {
        new Delete().from(CartModel.class).execute();
    }




    public JSONObject createJsonObject(){


        UserCompleteOrder userCompleteOrder =  ((IndiaReadsApplication) getActivity().getApplication()).getUserCompleteOrderObject();
        JSONObject completeDetails=new JSONObject();

        try{
            completeDetails.put("uid",getUserId());
            completeDetails.put("from","AndroidApp");
            completeDetails.put("address_id",userCompleteOrder.getUserAddress().getAddress_book_id());
        //    completeDetails.put("mrp", getMrp(userCompleteOrder) + "");
            if(userCompleteOrder.getTotalPayable()==0){
                completeDetails.put("payment_status","1");
                completeDetails.put("payment_option","5");
            }else if(userCompleteOrder.getPaymentMethod().equals(UrlsRetail.CASH_ON_DELIVERY_PAYMENT)){
                completeDetails.put("payment_status","0");
                completeDetails.put("payment_option","1");
            }else if(userCompleteOrder.getPaymentMethod().equals(UrlsRetail.PAY_ONLINE_PAYMENT)){
                completeDetails.put("payment_status","1");
                completeDetails.put("payment_option","2");
            }else  if(userCompleteOrder.getPaymentMethod().equals(UrlsRetail.PAY_TM_PAYMENT)){
                completeDetails.put("payment_status","1");
                completeDetails.put("payment_option","3");
            }
            else  if(userCompleteOrder.getPaymentMethod().equals(UrlsRetail.Pay_U_Money_PAYMENT)){
                completeDetails.put("payment_status","1");
                completeDetails.put("payment_option","4");
            }

            completeDetails.put("net_pay",userCompleteOrder.getTotalPayable());
            completeDetails.put("coupon_discount",userCompleteOrder.getCouponDiscount());
            completeDetails.put("store_credit_discount",userCompleteOrder.getStoreCreditDiscount());
            completeDetails.put("shipping_charge",userCompleteOrder.getShippingCharge());
            completeDetails.put("price",getSumOfAllInitPay(userCompleteOrder.getUserBook()));
            completeDetails.put("transaction_id",paymentId);
            if(userCompleteOrder.getCouponCode()!=null) {
                completeDetails.put("coupon_code", userCompleteOrder.getCouponCode());
            }else{
                completeDetails.put("coupon_code", "");
            }

            completeDetails.put("cart",getCartArray(userCompleteOrder));

        //    completeDetails.put("parent_order_id",userCompleteOrder.getParentOrderId());
            // payment mode
            // copon code amount
            // store crdeit amount used
            // bok

//            completeDetails.put("shipping_price",userCompleteOrder.getShippingCharge());
//        //    completeDetails.put("cod_charge",userCompleteOrder.getCodCharge());
//        //    completeDetails.put("store_credit",userCompleteOrder.getStoreCredits());
//            completeDetails.put("total_price",userCompleteOrder.getTotalPayable());
//
//            Log.e("cod json is ", completeDetails.toString());
//
//            orderDetails.put("order_details",completeDetails.toString());
//
//            Log.e("cod json is ", orderDetails.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return  completeDetails;
    }



//    public int getMrp(UserCompleteOrder userCompleteOrder){
//        List<CartModel> bookList=userCompleteOrder.getUserBook();
//        int tot=0;
//        for(int i=0;i<bookList.size();i++){
//           tot+=Integer.parseInt(bookList.get(i).getMrp());
//        }
//        return tot;
//    }

    public JSONArray getCartArray(UserCompleteOrder userCompleteOrder) throws JSONException {
        JSONArray cart = new JSONArray();
        List<CartModel> bookList=userCompleteOrder.getUserBook();
        long sumOfAllInitPay=getSumOfAllInitPay(bookList);
        for(int i=0;i<bookList.size();i++){
            cart.put(getBookObject(bookList.get(i),sumOfAllInitPay));
        }

        return cart;
    }

    private long getSumOfAllInitPay(List<CartModel> bookList) {
        long sum=0;
        for(int i=0;i<bookList.size();i++){
            CartModel b=bookList.get(i);
            if (b.getToBuy().equals("1")) {
                sum+=(Long.parseLong(b.getBuy_mrp())-Long.parseLong(b.getBuy_discount()));
            } else {
                sum+=Integer.parseInt(b.getInitialPayable());
            }
        }
        return sum;
    }

    public JSONObject getBookObject(CartModel book,long sum) throws JSONException {
        JSONObject bookObject1 = new JSONObject();
        bookObject1.put("book_id", book.getBookID());
        bookObject1.put("book_name", book.getTitle());
        bookObject1.put("isbn13", book.getIsbn13());
        bookObject1.put("buy_status", book.getToBuy());
        bookObject1.put("author", book.getAuthor1());
        bookObject1.put("discount_pay", String.valueOf(getDiscountPay(book,sum)));
        bookObject1.put("store_pay",  String.valueOf(getStorePay(book,sum)));
       double amount_pay;
        if (book.getToBuy().equals("1")) {
            bookObject1.put("buy_mrp", book.getBuy_mrp());
            bookObject1.put("discount", book.getBuy_discount());
            long ss=Long.parseLong(book.getMrp())-Long.parseLong(book.getBuy_discount());
            bookObject1.put("initial_payable", ss+"");
            amount_pay=ss-(getDiscountPay(book,sum)+getStorePay(book,sum));
        } else {
            bookObject1.put("initial_payable", book.getInitialPayable());
            bookObject1.put("mrp", book.getMrp());
            amount_pay=Long.parseLong(book.getInitialPayable())-((int)getDiscountPay(book,sum)+((int)getStorePay(book,sum)));
        }

        bookObject1.put("amount_pay", amount_pay);

//        bookObject1.put("book_library",book.getBookLibrary());
//        bookObject1.put("merchant_library",book.getRent().getMerchantLibrary());
        return bookObject1;
    }

    private double getDiscountPay(CartModel book, long sum) {
        double total=0;
        UserCompleteOrder userCompleteOrder =  ((IndiaReadsApplication) getActivity().getApplication()).getUserCompleteOrderObject();
        if (book.getToBuy().equals("1")) {
           double bookInit=(Long.parseLong(book.getBuy_mrp())-Long.parseLong(book.getBuy_discount()));
            double couponDiscount=userCompleteOrder.getCouponDiscount();
            total=(bookInit/sum)*couponDiscount;
        } else {
            double bookInit=Integer.parseInt(book.getInitialPayable());
            double couponDiscount=userCompleteOrder.getCouponDiscount();
            total=(bookInit/sum)*couponDiscount;
        }

        return total;
    }

    private double getStorePay(CartModel book, long sum) {
        double total=0;
        double storeDiscount=0;
        double bookInit;
        UserCompleteOrder userCompleteOrder =  ((IndiaReadsApplication) getActivity().getApplication()).getUserCompleteOrderObject();
        if (book.getToBuy().equals("1")) {
             bookInit=(Long.parseLong(book.getBuy_mrp())-Long.parseLong(book.getBuy_discount()));
        } else {
             bookInit=Integer.parseInt(book.getInitialPayable());
        }
        storeDiscount=userCompleteOrder.getStoreCreditDiscount();


        if((sum-userCompleteOrder.getCouponDiscount())>=storeDiscount){
            total=(bookInit/sum)*(storeDiscount);
        }else{
            total=(bookInit/sum)*(sum-userCompleteOrder.getCouponDiscount());
        }

        return total;
    }



    public String getUserId(){
        SharedPreferences settings = getActivity().getSharedPreferences(UrlsRetail.SAVED_USER_DETAILS, getActivity().MODE_PRIVATE);
        // Reading from SharedPreferences
        return settings.getString(UrlsRetail.SAVED_USER_ID,"");
    }


    public  void showNoInternetConnectionDialogForThankuPage(){
        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setTitle("No Internet Connection");
        alertDialog.setMessage("Sorry, Your Order Has Not Yet Placed!. Please Retry to Place the Order.");
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
                            restartActivity(getActivity(),getActivity().getIntent());
                        } else {
                            // Log.e("internet not conected", "done");
                            alertDialog.dismiss();
                            showNoInternetConnectionDialogForThankuPage();
                        }
                    }
                });
                b2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                        startOrderFailedActivity();
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



}
