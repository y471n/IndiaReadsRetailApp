package com.indiareads.retailapp.views.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.indiareads.retailapp.R;
import com.indiareads.retailapp.models.CartModel;
import com.indiareads.retailapp.models.UserCompleteOrder;
import com.indiareads.retailapp.singleton.VolleySingleton;
import com.indiareads.retailapp.utils.CommonMethods;
import com.indiareads.retailapp.utils.DefaultMaxRetries;
import com.indiareads.retailapp.utils.IndiaReadsApplication;
import com.indiareads.retailapp.utils.UrlsRetail;
import com.indiareads.retailapp.views.activities.OrderSummaryActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


public class SelectPaymentMethodFragment extends Fragment {

    View fragmentRootView;
    ImageView payOnlineImageView,paytmImageView,payUMoneyImageView,cashOnDeliveryImageView;

    int couponDiscount=0;
    int couponStatus=0;

    ProgressDialog PD;

    TextView couponEditText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        fragmentRootView= inflater.inflate(R.layout.fragment_select_payment_method, container, false);

        fetchViewsFromXml();
        setAmountPayable();
        setClickListeners();
        setTextChangeListeners();

        return fragmentRootView;
    }

    public void setAmountPayable(){

        ((TextView)fragmentRootView.findViewById(R.id.amount_payable)).setText(String.valueOf(getTotalPayable()));
    }

    public double getTotalCost(List<CartModel> bookList) {
        double totalCost=0;
        for(int i=0;i<bookList.size();i++){
            CartModel cart=bookList.get(i);
            if(cart.getToBuy().equals("1")){
                double buyFinalPrice= Math.ceil(Double.parseDouble(cart.getBuy_mrp().trim()) -Double.parseDouble(cart.getBuy_discount().trim()));
                totalCost+=buyFinalPrice;
            }else {
                totalCost += Integer.parseInt(cart.getInitialPayable());
            }
        }

        return totalCost;
    }

    public double getTotalPayable() {
        UserCompleteOrder userCompleteOrder =  ((IndiaReadsApplication) getActivity().getApplication()).getUserCompleteOrderObject();
        return getTotalCost(userCompleteOrder.getUserBook())+getShippingCharges(userCompleteOrder);
    }

    public int getShippingCharges(UserCompleteOrder userCompleteOrder) {
        if(getTotalCost(userCompleteOrder.getUserBook())>350){
            return 0;
        }else{
            return 50;
        }
    }

    public void setClickListeners(){

        setProceedToOrderSummaryClickListener();
        setApplyButtonClickListener();
        setRemoveCouponButtonClickListener();

        setPayOnlineClickListener();
        setPaytmClickListener();
        setPayUMoneyClickListener();
        setCashOnDeliveryClickListener();
    }


    public void fetchViewsFromXml(){
        payOnlineImageView=(ImageView)fragmentRootView.findViewById(R.id.pay_online_image_view);
        paytmImageView=(ImageView)fragmentRootView.findViewById(R.id.paytm_image_view);
        payUMoneyImageView=(ImageView)fragmentRootView.findViewById(R.id.pay_u_money_image_view);
        cashOnDeliveryImageView=(ImageView)fragmentRootView.findViewById(R.id.cash_on_delivery_image_view);
         couponEditText=(TextView)fragmentRootView.findViewById(R.id.coupon_code_edit_text);
    }

    public void setTextChangeListeners(){
        couponEditText.addTextChangedListener(new MyTextWatcher(couponEditText));
    }

//    public void showLoadingScreen(){
//        selectPaymentMethodLoadingScreen.setVisibility(View.VISIBLE);
//        selectPaymentMethodPageContent.setVisibility(View.GONE);
//    }
//    public void hideLoadingScreen(){
//        selectPaymentMethodLoadingScreen.setVisibility(View.GONE);
//        selectPaymentMethodPageContent.setVisibility(View.VISIBLE);
//    }


    private void setProceedToOrderSummaryClickListener() {
        fragmentRootView.findViewById(R.id.place_order_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (couponStatus == 4 && getPaymentMethod().equals(UrlsRetail.CASH_ON_DELIVERY_PAYMENT)) {
                    showErrorDialog();
                }else if(getPaymentMethod().equals(UrlsRetail.CASH_ON_DELIVERY_PAYMENT)){
                    showLoadingDialog2();
                    checkPinCodeFromServer();
                }else {
                    addPaymentMethodAndCouponDiscountToUserCompleteOrderObject();
                    Intent intent = new Intent(getActivity(), OrderSummaryActivity.class);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.right_in, R.anim.left_out);
               }

            }
        });
    }
    public void showLoadingDialog2(){
        PD = new ProgressDialog(getActivity());
        PD.setTitle(getResources().getString(R.string.progress_dialog_please_wait));
      //  PD.setMessage(getResources().getString(R.string.Processing_your_request));
        PD.setCancelable(false);
        PD.show();
    }

    public void showErrorDialog(){
       AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setTitle("Error");
        alertDialog.setMessage("Coupons are not available for Cod. Please select another Payment method or Remove the Coupon Code.");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();

    }

    public void addPaymentMethodAndCouponDiscountToUserCompleteOrderObject(){
        ((IndiaReadsApplication) getActivity().getApplication()).getUserCompleteOrderObject().setPaymentMethod(getPaymentMethod());
        ((IndiaReadsApplication) getActivity().getApplication()).getUserCompleteOrderObject().setCouponDiscount(couponDiscount);
        if(couponStatus==4){
            ((IndiaReadsApplication) getActivity().getApplication()).getUserCompleteOrderObject().setCouponCode(((EditText) fragmentRootView.findViewById(R.id.coupon_code_edit_text)).getText().toString());
        }
    }

    public String getPaymentMethod(){

        if(payOnlineImageView.getTag().equals("checked")){
            return UrlsRetail.PAY_ONLINE_PAYMENT;
        }
        else if(paytmImageView.getTag().equals("checked")){
            return UrlsRetail.PAY_TM_PAYMENT;
        }
        else if(payUMoneyImageView.getTag().equals("checked")){
            return UrlsRetail.Pay_U_Money_PAYMENT;
        }else if(cashOnDeliveryImageView.getTag().equals("checked")){
            return UrlsRetail.CASH_ON_DELIVERY_PAYMENT;
        }
        return null;
    }

    private void setApplyButtonClickListener() {
        fragmentRootView.findViewById(R.id.apply_coupon_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String couponCode = ((EditText) fragmentRootView.findViewById(R.id.coupon_code_edit_text)).getText().toString();
                if(couponCode.length()==0){
                    Toast.makeText(getActivity(),"No coupon code entered",Toast.LENGTH_LONG).show();
                }else {
                    if (getPaymentMethod().equals(UrlsRetail.CASH_ON_DELIVERY_PAYMENT)) {
                        Toast.makeText(getActivity(), "Coupon is not available for Cod", Toast.LENGTH_LONG).show();
                    } else {
                        hideKeyboard();
                        showLoadingDialog();
                        ((TextView) fragmentRootView.findViewById(R.id.coupon_message)).setVisibility(View.GONE);
                        checkCouponValidityFromServer(couponCode);
                    }
                }

            }
        });
    }

    public void hideKeyboard(){
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    public void showLoadingDialog(){
        PD = new ProgressDialog(getActivity());
        PD.setTitle(getResources().getString(R.string.progress_dialog_please_wait));
        PD.setMessage(getResources().getString(R.string.checking_coupon_discount));
        PD.setCancelable(false);
        PD.show();
    }

    private void setRemoveCouponButtonClickListener() {
        fragmentRootView.findViewById(R.id.remove_coupon_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeCouponDiscountZero();
                couponStatus = 0;
                ((TextView) fragmentRootView.findViewById(R.id.coupon_code_edit_text)).setText(null);
                (fragmentRootView.findViewById(R.id.coupon_message)).setVisibility(View.GONE);
                ((TextView) fragmentRootView.findViewById(R.id.coupon_code_edit_text)).setFocusable(true);
                ((TextView) fragmentRootView.findViewById(R.id.coupon_code_edit_text)).setFocusableInTouchMode(true);
            }
        });
    }


    private void checkCouponValidityFromServer(String couponCode) {
        JsonObjectRequest jsonObjectRequest = null;
        try {
            jsonObjectRequest = getcheckCouponValidityJsonRequest(UrlsRetail.CHECK_COUPON_CODE_VALIDITY_FROM_SERVER,couponCode);
            jsonObjectRequest.setTag(UrlsRetail.CHECK_COUPON_CODE_VOLLEY_REQUEST_TAG);
            VolleySingleton.getInstance(getActivity()).addToRequestQueue(jsonObjectRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void checkInternetConnectivity() {
        if(!CommonMethods.hasActiveInternetConnection(getActivity())) {
            CommonMethods.showNoInternetConnectionDialog(getActivity(), getActivity().getIntent());
        }
    }

    private JsonObjectRequest getcheckCouponValidityJsonRequest(String url,String couponCode) throws JSONException {
        UserCompleteOrder userCompleteOrder =  ((IndiaReadsApplication) getActivity().getApplication()).getUserCompleteOrderObject();

        JSONObject obj=new JSONObject();
        obj.put("user_id",getUserId());
        obj.put("couponCode",couponCode);
        obj.put("total_payable",getTotalCost(userCompleteOrder.getUserBook()));

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url,obj, getcheckCouponValidityResponseListener(), new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                PD.dismiss();
                if(error instanceof NoConnectionError) {
                    noInternetConnectivityPopup();
                }else{
                    errorInApplyingCouponPopup();
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
    private Response.Listener<JSONObject> getcheckCouponValidityResponseListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject fetchedJson = new JSONObject(response.toString());
                   checkCouponResponse(fetchedJson);
                    PD.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
    }


    public  void errorInApplyingCouponPopup(){
        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setTitle("Opps Something Went Wrong!");
        alertDialog.setMessage("Please Retry.");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        alertDialog.show();

    }
    public  void noInternetConnectivityPopup(){
        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setTitle("No Internet Connection!");
        alertDialog.setMessage("Sorry,No internet connectivity detected. Please reconnect and try again.");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        alertDialog.show();

    }

    private void checkCouponResponse(JSONObject fetchedJson) throws JSONException {
        String status=fetchedJson.getString("responseCode");
        ((TextView)fragmentRootView.findViewById(R.id.coupon_code_edit_text)).setFocusable(false);
        ((TextView)fragmentRootView.findViewById(R.id.coupon_code_edit_text)).setFocusableInTouchMode(false);
        if(status.equals("1")){
            ((TextView)fragmentRootView.findViewById(R.id.coupon_message)).setText("Coupon does not exits");
            ((TextView)fragmentRootView.findViewById(R.id.coupon_message)).setTextColor(getResources().getColor(R.color.red));
            ((TextView)fragmentRootView.findViewById(R.id.coupon_message)).setVisibility(View.VISIBLE);
            couponStatus=1;
        }else if(status.equals("2")){
            ((TextView)fragmentRootView.findViewById(R.id.coupon_message)).setText("Invalid coupon");
            ((TextView)fragmentRootView.findViewById(R.id.coupon_message)).setTextColor(getResources().getColor(R.color.red));
            ((TextView)fragmentRootView.findViewById(R.id.coupon_message)).setVisibility(View.VISIBLE);
            couponStatus=2;
        }else if(status.equals("3")){
            ((TextView)fragmentRootView.findViewById(R.id.coupon_message)).setText("This is one time coupon and you have already used it");
            ((TextView)fragmentRootView.findViewById(R.id.coupon_message)).setTextColor(getResources().getColor(R.color.red));
            ((TextView)fragmentRootView.findViewById(R.id.coupon_message)).setVisibility(View.VISIBLE);
            couponStatus=3;
        }else if(status.equals("5")){
            ((TextView)fragmentRootView.findViewById(R.id.coupon_message)).setText("Total order value is less or this coupon");
            ((TextView)fragmentRootView.findViewById(R.id.coupon_message)).setTextColor(getResources().getColor(R.color.red));
            ((TextView)fragmentRootView.findViewById(R.id.coupon_message)).setVisibility(View.VISIBLE);
            couponStatus=5;
        }else{
               couponDiscount=Integer.parseInt(fetchedJson.getString("couponDiscount"));
            ((TextView)fragmentRootView.findViewById(R.id.coupon_message)).setText("Congrats! you just saved Rs "+couponDiscount);
            ((TextView)fragmentRootView.findViewById(R.id.coupon_message)).setTextColor(getResources().getColor(R.color.darkGreen));
            ((TextView)fragmentRootView.findViewById(R.id.coupon_message)).setVisibility(View.VISIBLE);
            couponStatus=4;
            updateTotalPayable();
        }
    }

    public String getUserId(){
        SharedPreferences settings = getActivity().getSharedPreferences(UrlsRetail.SAVED_USER_DETAILS, getActivity().MODE_PRIVATE);
        // Reading from SharedPreferences
        return settings.getString(UrlsRetail.SAVED_USER_ID,"");
    }


    private void setPayOnlineClickListener() {
        fragmentRootView.findViewById(R.id.pay_online_linear_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                makeAllUnchecked();
                payOnlineImageView.setBackgroundResource(R.drawable.checked);
                payOnlineImageView.setTag("checked");
                resetEveryThing();
            }
        });
    }



    private void setPaytmClickListener() {
        fragmentRootView.findViewById(R.id.paytm_linear_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                makeAllUnchecked();
                    paytmImageView.setBackgroundResource(R.drawable.checked);
                    paytmImageView.setTag("checked");


            }
        });
    }

    private void setPayUMoneyClickListener() {
        fragmentRootView.findViewById(R.id.pay_u_money_linear_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                makeAllUnchecked();
                    payUMoneyImageView.setBackgroundResource(R.drawable.checked);
                    payUMoneyImageView.setTag("checked");
                resetEveryThing();
            }
        });
    }

    private void setCashOnDeliveryClickListener() {
        fragmentRootView.findViewById(R.id.cash_on_delivery_linear_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    makeAllUnchecked();
                    cashOnDeliveryImageView.setBackgroundResource(R.drawable.checked);
                    cashOnDeliveryImageView.setTag("checked");

                resetEveryThing();

            }
        });
    }

    public void resetEveryThing(){
        makeCouponDiscountZero();
        ((TextView)fragmentRootView.findViewById(R.id.coupon_code_edit_text)).setText(null);
        (fragmentRootView.findViewById(R.id.coupon_message)).setVisibility(View.GONE);
        (fragmentRootView.findViewById(R.id.remove_coupon_button)).setVisibility(View.GONE);
        ((TextView)fragmentRootView.findViewById(R.id.coupon_code_edit_text)).setFocusable(true);
        ((TextView)fragmentRootView.findViewById(R.id.coupon_code_edit_text)).setFocusableInTouchMode(true);
    }

    private void makeCouponDiscountZero() {
        couponDiscount=0;
        couponStatus=0;
        updateTotalPayable();
    }

    private void updateTotalPayable() {
        UserCompleteOrder userCompleteOrder =  ((IndiaReadsApplication) getActivity().getApplication()).getUserCompleteOrderObject();
        double totPay=getTotalCost(userCompleteOrder.getUserBook())+getShippingCharges(userCompleteOrder)-couponDiscount;
        ((TextView)fragmentRootView.findViewById(R.id.amount_payable)).setText(String.valueOf(totPay));
    }

    public void makeAllUnchecked(){
        paytmImageView.setBackgroundResource(R.drawable.unchecked);
        payOnlineImageView.setBackgroundResource(R.drawable.unchecked);
        payUMoneyImageView.setBackgroundResource(R.drawable.unchecked);
        cashOnDeliveryImageView.setBackgroundResource(R.drawable.unchecked);

        makeAllTagUnchecked();
    }

    public void makeAllTagUnchecked(){
        paytmImageView.setTag("unchecked");
        payOnlineImageView.setTag("unchecked");
        payUMoneyImageView.setTag("unchecked");
        cashOnDeliveryImageView.setTag("unchecked");

    }



    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int start, int before, int count) {
        }

        public void onTextChanged(CharSequence charSequence, int start, int count, int after) {
            switch (view.getId()) {
                case R.id.coupon_code_edit_text:
                                 if(charSequence.length()>0){
                                     ((ImageView)fragmentRootView.findViewById(R.id.remove_coupon_button)).setVisibility(View.VISIBLE);
                                 }else{
                                     ((ImageView)fragmentRootView.findViewById(R.id.remove_coupon_button)).setVisibility(View.GONE);
                                 }
                    break;
            }
        }

        public void afterTextChanged(Editable editable) {

        }
    }

    public void checkPinCodeFromServer(){
        JsonObjectRequest jsonObjectRequest = null;
        try {
            jsonObjectRequest = getPinCodeJsonRequest(UrlsRetail.CHECK_PIN_CODE_VALIDITY);
            VolleySingleton.getInstance(getActivity()).addToRequestQueue(jsonObjectRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public  void showNoInternetConnectionDialogForPinCodeCheck(){
        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setTitle("No Internet Connection");
        alertDialog.setMessage("Sorry, No internet connectivity detected. Please reconnect and try again");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        alertDialog.show();

    }

    public  void errorInCheckingPinCode(){
        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setTitle("Oops something went wrong!");
        alertDialog.setMessage("Please try Again");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        alertDialog.show();

    }

    public  void pincodeNotValid(){
        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setTitle("COD is not available at this pincode!");
        alertDialog.setMessage("Please select a different address");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        alertDialog.show();

    }


    private JsonObjectRequest getPinCodeJsonRequest(String url) throws JSONException {

        JSONObject obj=new JSONObject();
        UserCompleteOrder userCompleteOrder =  ((IndiaReadsApplication) getActivity().getApplication()).getUserCompleteOrderObject();
        obj.put("pincode",userCompleteOrder.getUserAddress().getPincode());
      //  Log.e("pincode is ",userCompleteOrder.getUserAddress().getPincode());
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url,obj, getPinCodeResponseListener(), new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                PD.dismiss();
                if(error instanceof NoConnectionError) {
                    showNoInternetConnectionDialogForPinCodeCheck();
                }else{
                    errorInCheckingPinCode();
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
    private Response.Listener<JSONObject> getPinCodeResponseListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                PD.dismiss();
                try {
                    String status=String.valueOf(response.get("value"));
                    if(status.equals("1")) {
                        addPaymentMethodAndCouponDiscountToUserCompleteOrderObject();
                        Intent intent = new Intent(getActivity(), OrderSummaryActivity.class);
                        startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.right_in, R.anim.left_out);
                    }else{
                        pincodeNotValid();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        };
    }



}

