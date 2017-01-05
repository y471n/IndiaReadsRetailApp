package com.indiareads.retailapp.views.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.indiareads.retailapp.R;
import com.indiareads.retailapp.singleton.VolleySingleton;
import com.indiareads.retailapp.utils.DefaultMaxRetries;
import com.indiareads.retailapp.utils.UrlsRetail;
import com.indiareads.retailapp.views.activities.ChoosePickUpAddressActivity;

import org.json.JSONException;
import org.json.JSONObject;


public class ReturnBookAddAddressFragment extends Fragment {


    View fragmentRootView;

    EditText pinCode,mobileNumber,fullName,addressLine1,addressLine2,city,state;
    TextInputLayout pinCodeTextInputLayout,mobileNumberTextInputLayout;

    ProgressDialog PD;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentRootView= inflater.inflate(R.layout.fragment_return_book_add_address, container, false);

        fetchViewsFromXml();
        setTextChangeListeners();
        setSaveButtonClickListener();


        return fragmentRootView;
    }

    public void fetchViewsFromXml(){
        pinCode=(EditText)fragmentRootView.findViewById(R.id.pincode);
        mobileNumber=(EditText)fragmentRootView.findViewById(R.id.mobile_number);

        fullName=(EditText)fragmentRootView.findViewById(R.id.full_name);
        addressLine1=(EditText)fragmentRootView.findViewById(R.id.address_line_1);
        addressLine2=(EditText)fragmentRootView.findViewById(R.id.address_line_2);
        city=(EditText)fragmentRootView.findViewById(R.id.city);
        state=(EditText)fragmentRootView.findViewById(R.id.state);

        pinCodeTextInputLayout=(TextInputLayout)fragmentRootView.findViewById(R.id.pincode_text_input_layout);
        mobileNumberTextInputLayout=(TextInputLayout)fragmentRootView.findViewById(R.id.mobile_number_text_input_layout);
    }


    public void setTextChangeListeners(){
        pinCode.addTextChangedListener(new MyTextWatcher(pinCode));
        mobileNumber.addTextChangedListener(new MyTextWatcher(mobileNumber));
    }

    public void showLoadingDialog(){
        PD = new ProgressDialog(getActivity());
        PD.setTitle(getResources().getString(R.string.progress_dialog_please_wait));
        PD.setMessage(getResources().getString(R.string.saving_address));
        PD.setCancelable(false);
        PD.show();
    }

    private void setSaveButtonClickListener() {
        fragmentRootView.findViewById(R.id.proceed_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAllDetailsAreFilled()) {
                    showLoadingDialog();
                    sendAddressToServer(getAddAddressUrl());
                }

            }
        });
    }
    public boolean isAllDetailsAreFilled() {
        if(fullName.getText().toString().isEmpty()){
            Toast.makeText(getActivity(),"Enter Full Name",Toast.LENGTH_SHORT).show();
            return  false;}
        if(addressLine1.getText().toString().isEmpty()){
            Toast.makeText(getActivity(),"Enter Address Line 1",Toast.LENGTH_SHORT).show();
            return false;}
        if(addressLine2.getText().toString().isEmpty()) {
            Toast.makeText(getActivity(), "Enter Address Line 2", Toast.LENGTH_SHORT).show();
            return false;}
        if(city.getText().toString().isEmpty()) {
            Toast.makeText(getActivity(), "Enter City", Toast.LENGTH_SHORT).show();
            return false;}
        if(state.getText().toString().isEmpty()) {
            Toast.makeText(getActivity(), "Enter State", Toast.LENGTH_SHORT).show();
            return false;}
        if(pinCode.getText().toString().isEmpty()) {
            Toast.makeText(getActivity(), "Enter pincode", Toast.LENGTH_SHORT).show();
            return false;}
        if(pinCode.getText().toString().length()!=6){
            Toast.makeText(getActivity(), "In Valid Pincode", Toast.LENGTH_SHORT).show();
            return false;}
        if(mobileNumber.getText().toString().isEmpty()) {
            Toast.makeText(getActivity(), "Enter mobile Number", Toast.LENGTH_SHORT).show();
            return false;}
        if(mobileNumber.getText().toString().length()!=10) {
            Toast.makeText(getActivity(), "In Valid Mobile Number", Toast.LENGTH_SHORT).show();
            return false;}
        return true;
    }

    public String getAddAddressUrl() {
        return UrlsRetail.ADD_ADDRESS;
    }

    public String getUserToken(){
        SharedPreferences settings = getActivity().getSharedPreferences(UrlsRetail.SAVED_USER_DETAILS, getActivity().MODE_PRIVATE);
        // Reading from SharedPreferences
        return settings.getString(UrlsRetail.SAVED_USER_TOKEN,"");
    }


    public void sendAddressToServer(String url){
        JsonObjectRequest jsonObjectRequest = null;
        try {
            jsonObjectRequest = getJsonRequest(url);
            VolleySingleton.getInstance(getActivity()).addToRequestQueue(jsonObjectRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private JsonObjectRequest getJsonRequest(String url) throws JSONException {


        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url,getJsonObject(), getResponseListener(), new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                PD.dismiss();
                if(error instanceof NoConnectionError) {
                    showNoInternetConnectionDialogForAddAddress();
                }else{
                    errorDuringAddingAddress();
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
    private Response.Listener<JSONObject> getResponseListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                PD.dismiss();

               // Log.e("Addd address",response.toString());
                try {
                    String picktype=response.getString("picktype");
                    if(picktype.equals("1")){
                        startNextActivity();
                    }else if(picktype.equals("2")){
                        pincodeAlertDialog("Delivery and pickup available, COD not available!");
                    }else if(picktype.equals("3")){
                        pincodeAlertDialog("Delivery and pickup not available!");
                    }else if(picktype.equals("4")){
                        invalidPincodeAlertDialog("Delivery and pickup not available!");
                    }

                } catch (JSONException e) {
                }
            }
        };
    }

    public void pincodeAlertDialog(String message){
        final  AlertDialog  alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setTitle("");
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {
                Button b = alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL);
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                        startNextActivity();
                    }
                });
            }
        });
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();


    }

    public  void invalidPincodeAlertDialog(String message){
        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setTitle(message);
        alertDialog.setMessage("Please try another pincode!");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        alertDialog.show();

    }



    public  void errorDuringAddingAddress(){
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



    public JSONObject getJsonObject() throws JSONException {
        JSONObject obj= new JSONObject();
        obj.put("user_id",getUserId());
        obj.put("fullname",fullName.getText().toString());
        obj.put("addressline1",addressLine1.getText().toString());
        obj.put("landmark",addressLine2.getText().toString());
        obj.put("city",city.getText().toString());
        obj.put("state", state.getText().toString());
        obj.put("pincode", pinCode.getText().toString());
        obj.put("number", mobileNumber.getText().toString());
        return obj;

    }

    public String getUserId(){
        SharedPreferences settings = getActivity().getSharedPreferences(UrlsRetail.SAVED_USER_DETAILS, getActivity().MODE_PRIVATE);
        // Reading from SharedPreferences
        return settings.getString(UrlsRetail.SAVED_USER_ID,"");
    }

    public void startNextActivity(){

        Intent intent;
        intent = new Intent(getActivity(), ChoosePickUpAddressActivity.class);
        intent.putExtra("list",String.valueOf(getActivity().getIntent().getExtras().get("list")));
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.right_in, R.anim.left_out);
        getActivity().finish();

    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int start, int before, int count) {
        }

        public void onTextChanged(CharSequence charSequence, int start, int count, int after) {
        }

        public void afterTextChanged(Editable editable) {

            switch (view.getId()) {
                case R.id.pincode:
                    validatePinCode();
                    break;
                case R.id.mobile_number:
                    validateMobileNumber();

            }
        }
    }

    public boolean validatePinCode(){
        if (pinCode.getText().toString().length()<6) {
            pinCodeTextInputLayout.setError(getString(R.string.invalid_pincode_error_message));
            // requestFocus(pinCode);
            return false;
        } else {
            pinCodeTextInputLayout.setErrorEnabled(false);
        }

        return true;
    }

    public boolean validateMobileNumber(){
        if (mobileNumber.getText().toString().length()<10) {
            mobileNumberTextInputLayout.setError(getString(R.string.invalid_mobile_number_error_message));
            // requestFocus(pinCode);
            return false;
        } else {
            mobileNumberTextInputLayout.setErrorEnabled(false);
        }

        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    public  void showNoInternetConnectionDialogForAddAddress(){
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
                            showLoadingDialog();
                            sendAddressToServer(getAddAddressUrl());
                        } else {
                            // Log.e("internet not conected", "done");
                            alertDialog.dismiss();
                            showNoInternetConnectionDialogForAddAddress();
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



}
