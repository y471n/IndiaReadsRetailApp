package com.indiareads.retailapp.views.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.indiareads.retailapp.views.activities.ThankYouActivity;
import com.payUMoney.sdk.PayUmoneySdkInitilizer;
import com.paytm.pgsdk.PaytmMerchant;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


public class OrderSummaryFragment extends Fragment {

    View fragmentRootView;
    LinearLayout linearLayoutForDynamicBooks;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentRootView=inflater.inflate(R.layout.fragment_order_summary, container, false);

        setRetryButtonClickListener();
        checkInternetConnectivity();


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
        mTracker.setScreenName("Order Summary");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }


    public void showNetworkNotAvailable(){
        (fragmentRootView.findViewById(R.id.error_linear_layout)).setVisibility(View.VISIBLE);
        ((TextView)fragmentRootView.findViewById(R.id.message)).setText(R.string.no_internet_connection_msg);
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

    private void executeOtherMethods() {
        fetchViewsFromXml();
        setProceedButtonTextAccordingToSelecetdPaymentMethod();
        showLoadingScreen();
        getStoreCredits();
        setProceedToThankuClickListener();
    }

    private void setProceedButtonTextAccordingToSelecetdPaymentMethod() {
        UserCompleteOrder userCompleteOrder =  ((IndiaReadsApplication) getActivity().getApplication()).getUserCompleteOrderObject();
        if(userCompleteOrder.getPaymentMethod().equals(UrlsRetail.CASH_ON_DELIVERY_PAYMENT)){
            ((TextView)fragmentRootView.findViewById(R.id.proceed_to_thanku_page)).setText("Place Order");
            ((TextView)fragmentRootView.findViewById(R.id.proceed_to_thanku_page)).setTextColor(getResources().getColor(R.color.whiteColor));
        }else{
            ((TextView)fragmentRootView.findViewById(R.id.proceed_to_thanku_page)).setText("Make Payment");
            ((TextView)fragmentRootView.findViewById(R.id.proceed_to_thanku_page)).setTextColor(getResources().getColor(R.color.whiteColor));
        }
    }


    public void fetchViewsFromXml(){
        linearLayoutForDynamicBooks=(LinearLayout)fragmentRootView.findViewById(R.id.linear_layout_for_dynamic_books);
    }



    public void showLoadingScreen(){
        fragmentRootView.findViewById(R.id.order_summary_loading_screen).setVisibility(View.VISIBLE);
        fragmentRootView.findViewById(R.id.order_summary_content).setVisibility(View.GONE);
    }
    public void hideLoadingScreen(){
        fragmentRootView.findViewById(R.id.order_summary_loading_screen).setVisibility(View.GONE);
        fragmentRootView.findViewById(R.id.order_summary_content).setVisibility(View.VISIBLE);
    }

    public void setData(){
        UserCompleteOrder userCompleteOrder =  ((IndiaReadsApplication) getActivity().getApplication()).getUserCompleteOrderObject();
        calculateCharges(userCompleteOrder);
        fillAddressDetails(userCompleteOrder);
        fillPaymentDetails(userCompleteOrder);
        fillChargesDetails(userCompleteOrder);
        fillBookDetails(userCompleteOrder);
    }



    public void fillBookDetails(UserCompleteOrder userCompleteOrder){

        List<CartModel> bookList=userCompleteOrder.getUserBook();

        for(int i=0;i<bookList.size();i++){
            View bookView = getActivity().getLayoutInflater().inflate(R.layout.display_book_linear_layout, null);
            TextView bookName=(TextView)bookView.findViewById(R.id.book_name);
            TextView bookCost=(TextView)bookView.findViewById(R.id.book_cost);

            bookName.setText(bookList.get(i).getTitle());
            if(bookList.get(i).getToBuy().equals("1")) {
                long buyFinalPrice= Long.parseLong((bookList.get(i)).getBuy_mrp().trim()) -Long.parseLong(((bookList.get(i)).getBuy_discount().trim()));
                bookCost.setText(String.valueOf(buyFinalPrice));
            }else{
                bookCost.setText(bookList.get(i).getInitialPayable());
            }
            linearLayoutForDynamicBooks.addView(bookView);
        }
    }

    public void calculateCharges( UserCompleteOrder userCompleteOrder){
        calculateShippingCharges(userCompleteOrder);
//        calculateCodCharges(userCompleteOrder);
//        calculateCouponDiscount(userCompleteOrder);
        calculateTotalPayable(userCompleteOrder);
    }

    public void calculateShippingCharges(UserCompleteOrder userCompleteOrder){
        userCompleteOrder.setShippingCharge(getShippingCharges(userCompleteOrder));
    }

//    public int getTotalCost(UserCompleteOrder userCompleteOrder) {
//        int totalCost=0;
//        for(int i=0;i<bookList.size();i++){
//            totalCost+=Integer.parseInt(bookList.get(0).getRent().getInitialPayable());
//        }
//
//        return totalCost;
//    }

    public long getTotalCost(UserCompleteOrder userCompleteOrder) {
        List<CartModel> bookList=userCompleteOrder.getUserBook();
        long totalCost=0;
        for(int i=0;i<bookList.size();i++){
            CartModel cart=bookList.get(i);
            if(cart.getToBuy().equals("1")){
                long buyFinalPrice= Long.parseLong(cart.getBuy_mrp().trim()) -Long.parseLong(cart.getBuy_discount().trim());
                totalCost+=buyFinalPrice;
            }else {
                totalCost += Integer.parseInt(cart.getInitialPayable());
            }
        }

        return totalCost;
    }

    public int getShippingCharges(UserCompleteOrder userCompleteOrder) {
        if(getTotalCost(userCompleteOrder)>350){
            //Log.e("ordr sumary total cost",getTotalCost(userCompleteOrder)+"");
            return 0;
        }else{
            return 50;
        }
    }


//    public void calculateCodCharges(UserCompleteOrder userCompleteOrder){
//        if(userCompleteOrder.getPaymentMethod().equals(UrlsRetail.CASH_ON_DELIVERY_PAYMENT)){
//            userCompleteOrder.setCodCharge(10);
//        }else{
//            userCompleteOrder.setCodCharge(0);
//        }
//    }

//    public void calculateCouponDiscount(UserCompleteOrder userCompleteOrder){
//        userCompleteOrder.setCouponDiscount(0);
//    }

    public void calculateTotalPayable(UserCompleteOrder userCompleteOrder){
        int totalPayable=0;
        totalPayable+=getTotalCost(userCompleteOrder);
        totalPayable+=userCompleteOrder.getShippingCharge();
//        totalPayable+=userCompleteOrder.getCodCharge();


      if(userCompleteOrder.getStoreCredits()>=totalPayable) {
//          Log.e("total payble 1 ",totalPayable+"");
//          Log.e("store credit  1",userCompleteOrder.getStoreCredits()+"");
          userCompleteOrder.setStoreCreditDiscount(totalPayable);
          userCompleteOrder.setCouponDiscount(0);
          totalPayable =0;
      }else{
//          Log.e("total payble 2 ",totalPayable+"");
//          Log.e("store credit 2 ",userCompleteOrder.getStoreCredits()+"");

          userCompleteOrder.setStoreCreditDiscount(userCompleteOrder.getStoreCredits());
          totalPayable-=userCompleteOrder.getStoreCredits();
          if(totalPayable>userCompleteOrder.getCouponDiscount()){
              totalPayable-=userCompleteOrder.getCouponDiscount();
          }else{
             // totalPayable-=userCompleteOrder.getCouponDiscount();
              userCompleteOrder.setCouponDiscount(0);
          }
      }
        userCompleteOrder.setTotalPayable(totalPayable);
    }

    public void fillChargesDetails(UserCompleteOrder userCompleteOrder){
        ((TextView)fragmentRootView.findViewById(R.id.shipping_charge)).setText(String.valueOf(userCompleteOrder.getShippingCharge()));

            ((TextView) fragmentRootView.findViewById(R.id.store_credit)).setText(String.valueOf(userCompleteOrder.getStoreCreditDiscount()));

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



    private void setProceedToThankuClickListener() {
        fragmentRootView.findViewById(R.id.proceed_to_thanku_page).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserCompleteOrder userCompleteOrder =  ((IndiaReadsApplication) getActivity().getApplication()).getUserCompleteOrderObject();
                //Log.e("payment method ",UrlsRetail.CASH_ON_DELIVERY_PAYMENT);


                if(userCompleteOrder.getTotalPayable()==0 || userCompleteOrder.getPaymentMethod().equals(UrlsRetail.CASH_ON_DELIVERY_PAYMENT))
                {
                    startThankuPageActivity("0");
                }else if(userCompleteOrder.getPaymentMethod().equals(UrlsRetail.Pay_U_Money_PAYMENT)){
                    startPayUMoneyPaymentGateWay();

                }else{
                    startPaytmTransaction();
                }

            }
        });
    }

    private String initOrderId() {
        Random r = new Random(System.currentTimeMillis());
        String orderId = "ORDER" + (1 + r.nextInt(2)) * 10000
                + r.nextInt(10000);
       return orderId;
    }


    public void startPaytmTransaction() {
        UserCompleteOrder userCompleteOrder =  ((IndiaReadsApplication) getActivity().getApplication()).getUserCompleteOrderObject();
        PaytmPGService Service = PaytmPGService.getProductionService();
        Map<String, String> paramMap = new HashMap<String, String>();

        // these are mandatory parameters

        paramMap.put("ORDER_ID",initOrderId() );
        paramMap.put("MID", "IndiaR80572048050166");
        paramMap.put("CUST_ID",getUserId() );
        paramMap.put("CHANNEL_ID", "WAP");
        paramMap.put("INDUSTRY_TYPE_ID","Retail115");
        paramMap.put("WEBSITE","indiareadswap");
        paramMap.put("TXN_AMOUNT", String.valueOf(userCompleteOrder.getTotalPayable()));
        paramMap.put("THEME","merchant" );
        paramMap.put("EMAIL", getEmailId());
        paramMap.put("MOBILE_NO","7042082421");
        PaytmOrder Order = new PaytmOrder(paramMap);


        PaytmMerchant Merchant = new PaytmMerchant(
                "http://indiareads.com/api/create-checksum-hash",
                "http://indiareads.com/api/verifyChecksum");

        Service.initialize(Order, Merchant, null);

        Service.startPaymentTransaction(getActivity(), true, true,
                new PaytmPaymentTransactionCallback() {
                    @Override
                    public void someUIErrorOccurred(String inErrorMessage) {
                        // Some UI Error Occurred in Payment Gateway Activity.
                        // // This may be due to initialization of views in
                        // Payment Gateway Activity or may be due to //
                        // initialization of webview. // Error Message details
                        // the error occurred.
                        //Log.d("LOG", "UI Error");
                    }

                    @Override
                    public void onTransactionSuccess(Bundle inResponse) {
                        // After successful transaction this method gets called.
                        // // Response bundle contains the merchant response
                        // parameters.
                      //  Log.d("LOG", "Payment Transaction is successful " + inResponse);
                        Toast.makeText(getActivity(), "Payment Transaction is successful ", Toast.LENGTH_LONG).show();
                        String paymentId=String.valueOf(inResponse.get("TXNID"));
                        //Log.e("paytm response ",inResponse.toString());
                       // Log.e("paytm transaction id ",paymentId);
                        startThankuPageActivity(paymentId);
                    }

                    @Override
                    public void onTransactionFailure(String inErrorMessage,
                                                     Bundle inResponse) {
                        // This method gets called if transaction failed. //
                        // Here in this case transaction is completed, but with
                        // a failure. // Error Message describes the reason for
                        // failure. // Response bundle contains the merchant
                        // response parameters.
                        //Log.d("LOG", "Payment Transaction Failed " + inErrorMessage);
                        Toast.makeText(getActivity(), "Payment Transaction Failed ", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void networkNotAvailable() { // If network is not
                        // available, then this
                        // method gets called.
                        Toast.makeText(getActivity(), "Network not available ", Toast.LENGTH_LONG).show();
                        //Log.d("LOG", "network not available");
                    }

                    @Override
                    public void clientAuthenticationFailed(String inErrorMessage) {
                        // This method gets called if client authentication
                        // failed. // Failure may be due to following reasons //
                        // 1. Server error or downtime. // 2. Server unable to
                        // generate checksum or checksum response is not in
                        // proper format. // 3. Server failed to authenticate
                        // that client. That is value of payt_STATUS is 2. //
                        // Error Message describes the reason for failure.
                        //Log.d("LOG", "client auth failed");
                    }

                    @Override
                    public void onErrorLoadingWebPage(int iniErrorCode,
                                                      String inErrorMessage, String inFailingUrl) {
                      //  Log.d("LOG", "error loading page");

                    }

                    // had to be added: NOTE
                    @Override
                    public void onBackPressedCancelTransaction() {
                        // TODO Auto-generated method stub
                        //Log.d("LOG", "cancel by user");
                    }

                });
}



    private void startPayUMoneyPaymentGateWay() {
        PayUmoneySdkInitilizer.PaymentParam.Builder builder = new PayUmoneySdkInitilizer.PaymentParam.Builder(  );
        builder.setKey("p7tQBU"); //Put your live KEY here
        builder.setSalt("1AqPBb0k"); //Put your live SALT here
        builder.setMerchantId("4851416"); //Put your live MerchantId here


        builder.setIsDebug(false);
//        builder.setDebugKey("SAix7uCw");// Debug Key
//        builder.setDebugMerchantId("4937026");// Debug Merchant ID
//        builder.setDebugSalt("oYQ7LUQ7kQ");// Debug Salt

        UserCompleteOrder userCompleteOrder =  ((IndiaReadsApplication) getActivity().getApplication()).getUserCompleteOrderObject();
//        builder.setAmount(userCompleteOrder.getTotalPayable());// debug
        builder.setAmount(userCompleteOrder.getTotalPayable());// debug
        builder.setTnxId("12345");
        builder.setPhone("7042082421");// debug
        builder.setProductName("India Reads Books");// debug
        builder.setFirstName("Vijay vikram");// debug
        builder.setEmail("vijay.vikram02@gmail.com");// debug
        builder.setsUrl("https://www.payumoney.com/mobileapp/payumoney/success.php");
        builder.setfUrl("https://www.payumoney.com/mobileapp/payumoney/failure.php");
        builder.setUdf1("");
        builder.setUdf2("");
        builder.setUdf3("");
        builder.setUdf4("");
        builder.setUdf5("");
        PayUmoneySdkInitilizer.PaymentParam paymentParam = builder.build();

        PayUmoneySdkInitilizer.startPaymentActivityForResult(getActivity(), paymentParam);
    }

    public static String hashCal(String type, String str) {
        byte[] hashseq = str.getBytes();
        StringBuffer hexString = new StringBuffer();
        try {
            MessageDigest algorithm = MessageDigest.getInstance(type);
            algorithm.reset();
            algorithm.update(hashseq);
            byte messageDigest[] = algorithm.digest();
            for (int i = 0; i < messageDigest.length; i++) {
                String hex = Integer.toHexString(0xFF & messageDigest[i]);
                if (hex.length() == 1) {
                    hexString.append("0");
                }
                hexString.append(hex);
            }
        } catch (NoSuchAlgorithmException nsae) {
        }
        return hexString. toString();
    }

    public void startThankuPageActivity(String paymentId){
        Intent intent = new Intent(getActivity(), ThankYouActivity.class);
        intent.putExtra("paymentId",paymentId);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    public void getStoreCredits(){

        getStoreCreditsFromServer(getStoreCreditsUrl());

    }

    public String getStoreCreditsUrl(){
        return UrlsRetail.STORE_CREDITS_VALUE;
    }

    private void getStoreCreditsFromServer(String urls) {
        try {
            JsonObjectRequest jsonObjectRequest;
            jsonObjectRequest = getStoreCreditsJsonRequest(urls);
            jsonObjectRequest.setTag(UrlsRetail.STORE_CREDITS_VALUE_VOLLEY_REQUEST_TAG);
            VolleySingleton.getInstance(getActivity()).addToRequestQueue(jsonObjectRequest);
        }catch (Exception e){

        }
    }



    private JsonObjectRequest getStoreCreditsJsonRequest(String url) throws JSONException {

        JSONObject obj=new JSONObject();
        obj.put("user_id",getUserId());

        //Log.e("json created ",obj.toString());
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url,obj, getStoreCreditsResponseListener(), new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error instanceof NoConnectionError) {
                    showNetworkNotAvailable();
                }else{
                    (fragmentRootView.findViewById(R.id.error_linear_layout)).setVisibility(View.VISIBLE);
                    ((TextView)fragmentRootView.findViewById(R.id.message)).setText(R.string.error_in_fetching_data);
                    (fragmentRootView.findViewById(R.id.complete_layout)).setVisibility(View.GONE);
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
    private Response.Listener<JSONObject> getStoreCreditsResponseListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                     JSONObject fetchedJson = new JSONObject(response.toString());
                     saveStoreCredits(getStoreCreditsFromJson(fetchedJson));
                     setData();
                     hideLoadingScreen();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
    }





    public void saveStoreCredits(int storeCredits){
        UserCompleteOrder userCompleteOrder =  ((IndiaReadsApplication) getActivity().getApplication()).getUserCompleteOrderObject();
        userCompleteOrder.setStoreCredits(storeCredits);
    }

    public int getStoreCreditsFromJson(JSONObject fetchedJson) throws JSONException {

        int storeCredits=0;

        int sC=Integer.parseInt(fetchedJson.getString("storeCredit"));

//        JSONArray data=fetchedJson.getJSONArray("data");
//        for(int i=0;i<data.length();i++){
//            JSONObject obj=data.getJSONObject(0);
//            storeCredits=Integer.parseInt(obj.getString("store_credit"));
//        }

        //Log.e("store credit extract ",sC+"");

        return sC;
    }



//    public void fetchStoreCredits(){
//      //  Log.e("pre order json ", createDataJsonObject().toString());
//        fetchStoreCreditsServer(UrlsRetail.PRE_ORDER);
//    }
//
//    private void fetchStoreCreditsServer(String urls) {
//        try {
//            JsonObjectRequest jsonObjectRequest;
//            jsonObjectRequest = getJsonRequest(urls);
//            VolleySingleton.getInstance(getActivity()).addToRequestQueue(jsonObjectRequest);
//        }catch(Exception e){
//
//        }
//    }
//
//    private JsonObjectRequest getJsonRequest(String url) throws JSONException {
//
//        JSONObject userIdJsonObject=new JSONObject();
//        userIdJsonObject.put("userID",getUserId());
//
//        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url,userIdJsonObject, getResponseListener(), new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.e("server error", error.toString());
//                Toast.makeText(getActivity(),"error "+error.toString(),Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        jsonRequest.setRetryPolicy(new DefaultRetryPolicy(
//                10000,
//                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//
//        return jsonRequest;
//    }
//
//    @NonNull
//    private Response.Listener<JSONObject> getResponseListener() {
//        return new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//                try {
//                    JSONObject fetchedJson = new JSONObject(response.toString());
//                    Log.e("pre order response", fetchedJson.toString());
//                    storeParentOrderId(fetchedJson);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        };
//    }


    public JSONObject createDataJsonObject(){
        UserCompleteOrder userCompleteOrder =  ((IndiaReadsApplication) getActivity().getApplication()).getUserCompleteOrderObject();
        JSONObject orderDetailsObject=new JSONObject();
        JSONObject rentalCartObject =new JSONObject();
        try {
            orderDetailsObject.put("uid",getUserId());
            orderDetailsObject.put("address_id",userCompleteOrder.getUserAddress().getAddress_book_id());
            orderDetailsObject.put("cart", getCartArray(userCompleteOrder));
            orderDetailsObject.put("total_price",getTotalCost(userCompleteOrder));
            rentalCartObject.put("rental_cart",orderDetailsObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return rentalCartObject;
    }

    public JSONArray getCartArray(UserCompleteOrder userCompleteOrder) throws JSONException {
        JSONArray cart = new JSONArray();
        List<CartModel> bookList=userCompleteOrder.getUserBook();
        for(int i=0;i<bookList.size();i++){
            cart.put(getBookObject(bookList.get(i)));
        }

        return cart;
    }

    public String getUserId(){
        SharedPreferences settings = getActivity().getSharedPreferences(UrlsRetail.SAVED_USER_DETAILS, getActivity().MODE_PRIVATE);
        // Reading from SharedPreferences
        return settings.getString(UrlsRetail.SAVED_USER_ID,"");
    }

    public String getEmailId(){
        SharedPreferences settings = getActivity().getSharedPreferences(UrlsRetail.SAVED_USER_DETAILS, getActivity().MODE_PRIVATE);
        // Reading from SharedPreferences
        return settings.getString(UrlsRetail.SAVED_EMAIL_ID,"");
    }

    public String getUserToken(){
        SharedPreferences settings = getActivity().getSharedPreferences(UrlsRetail.SAVED_USER_DETAILS, getActivity().MODE_PRIVATE);
        // Reading from SharedPreferences
        return settings.getString(UrlsRetail.SAVED_USER_TOKEN,"");
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (resultCode == getActivity().RESULT_OK) {

                Toast.makeText(getActivity(), "success", Toast.LENGTH_LONG).show();
            }
            if (resultCode == getActivity().RESULT_CANCELED) {
//failed
                Toast.makeText(getActivity(), "failed", Toast.LENGTH_LONG).show();
            }

    }




    public void storeParentOrderId(JSONObject preOrderApiResponse) throws JSONException {

      //  ((IndiaReadsApplication)getActivity().getApplication()).getUserCompleteOrderObject().setParentOrderId(Integer.parseInt(preOrderApiResponse.getString("parent_order_id")));
    }

    public JSONObject getBookObject(CartModel book) throws JSONException {
        JSONObject bookObject1=new JSONObject();
//        bookObject1.put("book_id",book.getRent().getBookId());
//        bookObject1.put("book_name",book.getTitle());
//        bookObject1.put("isbn13",book.getIsbn13());
//        bookObject1.put("initial_payable",book.getRent().getInitialPayable());
//        bookObject1.put("mrp",book.getRent().getMrp());
//        bookObject1.put("author",book.getAuthor1());
//        bookObject1.put("book_library",book.getRent().getBookLibrary());
//        bookObject1.put("merchant_library",book.getRent().getMerchantLibrary());


        return bookObject1;
    }



}
