package com.indiareads.retailapp.corporate.view.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
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
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.indiareads.retailapp.R;
import com.indiareads.retailapp.models.UserProfile;
import com.indiareads.retailapp.singleton.VolleySingleton;
import com.indiareads.retailapp.utils.CommonMethods;
import com.indiareads.retailapp.utils.DefaultMaxRetries;
import com.indiareads.retailapp.utils.UrlsCorporate;
import com.indiareads.retailapp.utils.UrlsRetail;
import com.indiareads.retailapp.views.activities.MyAccountActivity;
import com.indiareads.retailapp.views.fragments.DatePickerFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class ProfilePageFragmentCorporate extends Fragment {

    View fragmentRootView;

    EditText firstNameDisplayData, lastNameDisplayData, alternateEmailDisplayData, mobileNumDisplayData, landLineDisplayData, birthDateEditTextDisplayData;
    TextInputLayout firstNameTextInputLayout,lastNameTextInputLayout,alternateEmailTextInputLayout,mobileTextInputLayout,landLineTextInputLayout;
    EditText firstName,lastName,alternateEmail,mobile,landLine,birthDate;
    RelativeLayout birthDateRelativeLayout;

    LinearLayout profilePageLoadingScreen;
    RelativeLayout profilePageContent;

    LinearLayout maleLinearLayout,femaleLinearLayout;
    Button saveButton,datePickerButton;
    ImageView maleImageView,femaleImageView;


    public UserProfile userProfile;

    ProgressDialog PD;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentRootView= inflater.inflate(R.layout.fragment_profile_page_corporate, container, false);
        setHasOptionsMenu(true);

        setRetryButtonClickListener();
        checkInternetConnectivity();

        return fragmentRootView;
    }

    private void checkInternetConnectivity() {
        if(!CommonMethods.hasActiveInternetConnection(getActivity())){
            showNetworkNotAvailable();
        }else{
            executeOtherMethods();
        }
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

    private void setNoInternetAccessBackgroundImage() {
//        fragmentRootView.findViewById(R.id.app_screen_background_image).setVisibility(View.VISIBLE);
//        fragmentRootView.findViewById(R.id.profile_page_complete_layout).setVisibility(View.GONE);
    }

    private void executeOtherMethods() {
        fetchViewsFromXml();
        showLoadingScreen();
        fetchUserProfile();
    }

    public void fetchViewsFromXml() {

        maleImageView = (ImageView) fragmentRootView.findViewById(R.id.male_image_View);
        femaleImageView = (ImageView) fragmentRootView.findViewById(R.id.female_image_View);

        firstNameDisplayData = (EditText) fragmentRootView.findViewById(R.id.profile_first_name_data_display);
        lastNameDisplayData = (EditText) fragmentRootView.findViewById(R.id.profile_last_name_display_text);
        alternateEmailDisplayData = (EditText) fragmentRootView.findViewById(R.id.profile_alternate_email_display_text);
        mobileNumDisplayData = (EditText) fragmentRootView.findViewById(R.id.profile_mobile_edittext_display_text);
        landLineDisplayData = (EditText) fragmentRootView.findViewById(R.id.profile_landline_edittext_display_text);
        birthDateEditTextDisplayData = (EditText) fragmentRootView.findViewById(R.id.profile_birthdate_edittext_display_text);

        firstNameTextInputLayout= (TextInputLayout) fragmentRootView.findViewById(R.id.first_name_text_input_layout);
        lastNameTextInputLayout= (TextInputLayout) fragmentRootView.findViewById(R.id.last_name_text_input_layout);
        alternateEmailTextInputLayout= (TextInputLayout) fragmentRootView.findViewById(R.id.profile_alternate_email_text_input_layout);
        mobileTextInputLayout= (TextInputLayout) fragmentRootView.findViewById(R.id.profile_mobile_text_input_layout);
        landLineTextInputLayout= (TextInputLayout) fragmentRootView.findViewById(R.id.profile_landline_text_input_layout);
        birthDateRelativeLayout= (RelativeLayout) fragmentRootView.findViewById(R.id.birthdate_relative_layout);

        firstName=(EditText)fragmentRootView.findViewById(R.id.profile_first_name);
        lastName=(EditText)fragmentRootView.findViewById(R.id.profile_last_name);
        alternateEmail=(EditText)fragmentRootView.findViewById(R.id.profile_alternate_email);
        mobile=(EditText)fragmentRootView.findViewById(R.id.profile_mobile_edittext);
        landLine=(EditText)fragmentRootView.findViewById(R.id.profile_landline_edittext);
        birthDate=(EditText)fragmentRootView.findViewById(R.id.profile_birthdate_edittext);


        maleLinearLayout=(LinearLayout)fragmentRootView.findViewById(R.id.male_linear_layout);
        femaleLinearLayout=(LinearLayout)fragmentRootView.findViewById(R.id.female_linear_layout);

        maleImageView=(ImageView)fragmentRootView.findViewById(R.id.male_image_View);
        femaleImageView=(ImageView)fragmentRootView.findViewById(R.id.female_image_View);

        saveButton=(Button)fragmentRootView.findViewById(R.id.profile_save_button);
        datePickerButton=(Button)fragmentRootView.findViewById(R.id.datepicker_button);


        profilePageLoadingScreen = (LinearLayout) fragmentRootView.findViewById(R.id.profile_page_loading_screen);
        profilePageContent = (RelativeLayout) fragmentRootView.findViewById(R.id.profile_page_content);

    }

    public void showLoadingScreen() {
        profilePageContent.setVisibility(View.GONE);
        profilePageLoadingScreen.setVisibility(View.VISIBLE);
    }

    public void hideLoadingScreen(){
        profilePageContent.setVisibility(View.VISIBLE);
        profilePageLoadingScreen.setVisibility(View.GONE);
    }


    private void fetchUserProfile() {
        String url = getUserProfileUrl();
        fetchUserProfileFromRemoteServer(url);
    }

    public String getUserProfileUrl() {
        return UrlsCorporate.PROFILE_DETAILS;
    }

    public String getUserToken(){
        SharedPreferences settings = getActivity().getSharedPreferences(UrlsRetail.SAVED_USER_DETAILS, getActivity().MODE_PRIVATE);
        // Reading from SharedPreferences
        return settings.getString(UrlsRetail.SAVED_USER_TOKEN,"");
    }

    private void fetchUserProfileFromRemoteServer(String url) {

        JsonObjectRequest jsonObjectRequest = null;
        try {
            jsonObjectRequest = getUserProfileJsonRequest(url);
            jsonObjectRequest.setTag(UrlsCorporate.PROFILE_DETAILS_VOLLEY_REQUEST_TAG);
            VolleySingleton.getInstance(getActivity()).addToRequestQueue(jsonObjectRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        // createBooksList(R.id.similar_books_recycler_view);

    }

    public String getUserId(){
        SharedPreferences settings = getActivity().getSharedPreferences(UrlsRetail.SAVED_USER_DETAILS, getActivity().MODE_PRIVATE);
        // Reading from SharedPreferences
        return settings.getString(UrlsRetail.SAVED_USER_ID,"");
    }


    private JsonObjectRequest getUserProfileJsonRequest(String url) throws JSONException {
        JSONObject obj=new JSONObject();
        obj.put("user_id",getUserId());
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url,obj, getUserProfileResponseListener(), new Response.ErrorListener() {
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
    private Response.Listener<JSONObject> getUserProfileResponseListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject fetchedJson = new JSONObject(response.toString());

                    setUserProfileInfo(fetchedJson);
                    hideLoadingScreen();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
    }




    public void setUserProfileInfo(JSONObject fetchedJson) {

        try {
            JSONObject data = fetchedJson.getJSONObject("data");

            String status =data.getString("status");

            if(status.equals("0")){
                userProfile=null;
            }else{
                Gson gson = new Gson();
                userProfile = gson.fromJson(data.toString(), UserProfile.class);
                fillUserDetails();
            }



        } catch (JSONException e) {
            Toast.makeText(getActivity(), "error", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }


    public void fillUserDetails() {

        firstNameDisplayData.setText(userProfile.getFirst_name());
        lastNameDisplayData.setText(userProfile.getLast_name());
        alternateEmailDisplayData.setText(userProfile.getAlternate_email());
        mobileNumDisplayData.setText(userProfile.getMobile());
        landLineDisplayData.setText(userProfile.getLandline());
        birthDateEditTextDisplayData.setText(userProfile.getBirthdate());
        if (userProfile.getGender().equals("Male")) {
            maleImageView.setBackgroundResource(R.drawable.checked);
        }
        else  if (userProfile.getGender().equals("Female")) {
            femaleImageView.setBackgroundResource(R.drawable.checked);
        }


    }

    public void setVisibiltyOfDisplayDataField(){

        firstNameDisplayData.setVisibility(View.GONE);
        lastNameDisplayData.setVisibility(View.GONE);
        alternateEmailDisplayData.setVisibility(View.GONE);
        mobileNumDisplayData .setVisibility(View.GONE);
        landLineDisplayData.setVisibility(View.GONE);
        birthDateEditTextDisplayData.setVisibility(View.GONE);

    }

    public void setVisibilityOfEditDataField(){

        firstNameTextInputLayout.setVisibility(View.VISIBLE);
        lastNameTextInputLayout.setVisibility(View.VISIBLE);
        alternateEmailTextInputLayout.setVisibility(View.VISIBLE);
        mobileTextInputLayout.setVisibility(View.VISIBLE);
        landLineTextInputLayout.setVisibility(View.VISIBLE);
        birthDateRelativeLayout.setVisibility(View.VISIBLE);


    }

    public void setClickListeners(){

        setMaleLinearLayoutClickListener();
        setFemaleLinearLayoutClickListener();
        setSaveButtonClickListener();
        setDatePickerButtonClickListener();
    }

    public void setSaveButtonClickListener(){

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isAllDetailsAreFilled()) {
                    String url = getEditProfileUrl();
                    showLoadingDialog();
                    sendEditProfileDataToServer(url);
                }

            }
        });
    }

    private boolean emailValidation(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public boolean isAllDetailsAreFilled() {
        if(alternateEmail.getText().toString().length()>0 && emailValidation(alternateEmail.getText().toString())){
            Toast.makeText(getActivity(),"Enter a valid email",Toast.LENGTH_SHORT).show();
            return  false;}


        if(mobile.getText().toString().length()>0 && mobile.getText().toString().length()!=10){
            Toast.makeText(getActivity(),"Enter a valid Mobile Number",Toast.LENGTH_SHORT).show();
            return  false;}

        return true;
    }


    public void sendSaveProfileDetailsToServer(){
        String url = getEditProfileUrl();
        showLoadingDialog();
        sendEditProfileDataToServer(url);
    }

    public void showLoadingDialog(){
        PD = new ProgressDialog(getActivity());
        PD.setTitle(getResources().getString(R.string.progress_dialog_please_wait));
        PD.setMessage(getResources().getString(R.string.saving_your_details));
        PD.setCancelable(false);
        PD.show();
    }

    public void sendEditProfileDataToServer(String url){
        JsonObjectRequest jsonObjectRequest = null;
        try {
            jsonObjectRequest = getJsonRequest(url);
            VolleySingleton.getInstance(getActivity()).addToRequestQueue(jsonObjectRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public String getEditProfileUrl() {
        return UrlsCorporate.EDIT_PROFILE;
    }




    private JsonObjectRequest getJsonRequest(String url) throws JSONException {

        JSONObject obj= new JSONObject();
        obj.put("user_id",getUserId());
        obj.put("first_name",firstName.getText().toString());
        obj.put("last_name",lastName.getText().toString());
        obj.put("birthdate",birthDate.getText().toString());
        obj.put("alternate_email",alternateEmail.getText().toString());
        obj.put("mobile", mobile.getText().toString());
        obj.put("landline", landLine.getText().toString());

        if(maleImageView.getTag().equals("checked")){
            obj.put("gender","Male");
        }
        else if(femaleImageView.getTag().equals("checked")){
            obj.put("gender","Female");
        }else {
            obj.put("gender","");
        }

        //   Log.e("profile send json ", obj.toString());

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url,obj, getResponseListener(), new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                PD.dismiss();
                if(error instanceof NoConnectionError) {
                    showNoInternetConnectionDialogForSaveProfile();
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
    private Response.Listener<JSONObject> getResponseListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                PD.dismiss();
                startMyAccountActivity();
            }
        };
    }

    public void startMyAccountActivity(){
        Intent i=new Intent(getActivity(),MyAccountActivity.class);
        startActivity(i);
        getActivity().finish();
    }

    public void setMaleLinearLayoutClickListener(){

        maleLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAllUnchecked();
                maleImageView.setBackgroundResource(R.drawable.checked);
                maleImageView.setTag("checked");


            }
        });
    }

    public void setFemaleLinearLayoutClickListener(){

        femaleLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAllUnchecked();
                femaleImageView.setBackgroundResource(R.drawable.checked);
                femaleImageView.setTag("checked");


            }
        });
    }

    public void setDatePickerButtonClickListener(){

        datePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });
    }

    public void setAllUnchecked(){
        femaleImageView.setBackgroundResource(R.drawable.unchecked);
        femaleImageView.setTag("unchecked");
        maleImageView.setBackgroundResource(R.drawable.unchecked);
        maleImageView.setTag("unchecked");
    }


    private void showDatePicker() {
        DatePickerFragment date = new DatePickerFragment();
        /**
         * Set Up Current Date Into dialog
         */
        Calendar calender = Calendar.getInstance();
        Bundle args = new Bundle();
        args.putInt("year", calender.get(Calendar.YEAR));
        args.putInt("month", calender.get(Calendar.MONTH));
        args.putInt("day", calender.get(Calendar.DAY_OF_MONTH));
        date.setArguments(args);
        /**
         * Set Call back to capture selected date
         */
        date.setCallBack(ondate);
        date.show(getFragmentManager(), "Date Picker");
    }

    DatePickerDialog.OnDateSetListener ondate = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            birthDate.setText(new StringBuilder()
                    // Month is 0 based so add 1
                    .append(year).append("-")
                    .append(monthOfYear + 1).append("-")
                    .append(dayOfMonth));

        }
    };


    public void setText(){
        firstName.setText(userProfile.getFirst_name());
        lastName.setText(userProfile.getLast_name());
        alternateEmail.setText(userProfile.getAlternate_email());
        mobile.setText(userProfile.getMobile());
        landLine.setText(userProfile.getLandline());
        birthDate.setText(userProfile.getBirthdate());
    }

    public void changeColorOfSaveButton(){
        saveButton.setBackgroundColor(getResources().getColor(R.color.light_red));
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Do something that differs the Activity's menu here
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_profile:

//                fragmentRootView.findViewById(R.id.profile_first_name).requestFocus();
//                fragmentRootView.findViewById(R.id.profile_last_name).requestFocus();
//                fragmentRootView.findViewById(R.id.profile_birthdate_edittext).requestFocus();
                setClickListeners();
                if(userProfile!=null) {
                    setText();
                }
                changeColorOfSaveButton();
                setVisibiltyOfDisplayDataField();
                setVisibilityOfEditDataField();

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public  void showNoInternetConnectionDialogForSaveProfile(){
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
                            sendSaveProfileDetailsToServer();
                        } else {
                            // Log.e("internet not conected", "done");
                            alertDialog.dismiss();
                            showNoInternetConnectionDialogForSaveProfile();
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