package com.indiareads.retailapp.corporate.view.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import com.indiareads.retailapp.corporate.models.CartModelCorporate;
import com.indiareads.retailapp.corporate.models.UserCompleteOrderCorporate;
import com.indiareads.retailapp.corporate.view.activity.OrderSummaryActivityCorporate;
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

public class SelectPaymentMethodFragmentCorporate extends Fragment {

    View fragmentRootView;
    ImageView payOnlineImageView,paytmImageView,payUMoneyImageView;

    ProgressDialog PD;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentRootView= inflater.inflate(R.layout.fragment_select_payment_method_corporate, container, false);


        fetchViewsFromXml();
        setAmountPayable();
        setClickListeners();

        return fragmentRootView;
    }

    public void setAmountPayable(){

        ((TextView)fragmentRootView.findViewById(R.id.amount_payable)).setText(String.valueOf(getTotalPayable()));
    }

    public double getTotalCost(List<CartModelCorporate> bookList) {
        double totalCost=0;
        for(int i=0;i<bookList.size();i++){
           totalCost+=Double.parseDouble(getCompanyFixedPrice());
        }

        Log.e("total cost is ",totalCost+"");

        return totalCost;
    }

    public String getCompanyFixedPrice(){
        SharedPreferences settings = getActivity().getSharedPreferences(UrlsRetail.SAVED_USER_DETAILS, getActivity().MODE_PRIVATE);
        // Reading from SharedPreferences
        return settings.getString(UrlsRetail.SAVED_COMPANY_FIX_PRICE,"");
    }


    public double getTotalPayable() {
        UserCompleteOrderCorporate userCompleteOrder =  ((IndiaReadsApplication) getActivity().getApplication()).getUserCompleteOrderObjectCorporate();
        return getTotalCost(userCompleteOrder.getUserBook());
    }


    public void setClickListeners(){

        setProceedToOrderSummaryClickListener();
        setPayOnlineClickListener();
        setPaytmClickListener();
        setPayUMoneyClickListener();
    }


    public void fetchViewsFromXml(){
        payOnlineImageView=(ImageView)fragmentRootView.findViewById(R.id.pay_online_image_view);
        paytmImageView=(ImageView)fragmentRootView.findViewById(R.id.paytm_image_view);
        payUMoneyImageView=(ImageView)fragmentRootView.findViewById(R.id.pay_u_money_image_view);
    }


    private void setProceedToOrderSummaryClickListener() {
        fragmentRootView.findViewById(R.id.place_order_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addPaymentMethodToUserCompleteOrderObject();
                    Intent intent = new Intent(getActivity(), OrderSummaryActivityCorporate.class);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.right_in, R.anim.left_out);

            }
        });
    }


    public void addPaymentMethodToUserCompleteOrderObject(){
        ((IndiaReadsApplication) getActivity().getApplication()).getUserCompleteOrderObjectCorporate().setPaymentMethod(getPaymentMethod());

    }

    public String getPaymentMethod(){

        if(payOnlineImageView.getTag().equals("checked")){
            return UrlsRetail.PAY_ONLINE_PAYMENT;
        }
        else if(paytmImageView.getTag().equals("checked")){
            return UrlsRetail.PAY_TM_PAYMENT;
        }
        else if(payUMoneyImageView.getTag().equals("checked")) {
            return UrlsRetail.Pay_U_Money_PAYMENT;
        }
        return null;
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
            }
        });
    }






    public void makeAllUnchecked(){
        paytmImageView.setBackgroundResource(R.drawable.unchecked);
        payOnlineImageView.setBackgroundResource(R.drawable.unchecked);
        payUMoneyImageView.setBackgroundResource(R.drawable.unchecked);

        makeAllTagUnchecked();
    }

    public void makeAllTagUnchecked(){
        paytmImageView.setTag("unchecked");
        payOnlineImageView.setTag("unchecked");
        payUMoneyImageView.setTag("unchecked");
    }




}


