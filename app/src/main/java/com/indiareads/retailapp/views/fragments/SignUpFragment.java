package com.indiareads.retailapp.views.fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.activeandroid.query.Delete;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.indiareads.retailapp.R;
import com.indiareads.retailapp.models.CartModel;
import com.indiareads.retailapp.singleton.VolleySingleton;
import com.indiareads.retailapp.utils.DefaultMaxRetries;
import com.indiareads.retailapp.utils.UrlsRetail;
import com.indiareads.retailapp.views.activities.MainActivity;
import com.indiareads.retailapp.views.activities.MainLoginActivity;

import org.json.JSONException;
import org.json.JSONObject;


public class SignUpFragment extends Fragment {

    View fragmentRootView;
    ProgressDialog PD;
    private AlertDialog alertDialog;
    private AlertDialog userAllReadyExitsAlertDialog;
    TextView emailTextView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        fragmentRootView= inflater.inflate(R.layout.fragment_signup, container, false);

        emailTextView=(TextView)fragmentRootView.findViewById(R.id.email);

        setSignUpClickListener();
        setGoBackToSignInClickListener();
//        setContinueWithoutLoginClickListener();


        return fragmentRootView;
    }


    private void setSignUpClickListener() {
        fragmentRootView.findViewById(R.id.signup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = emailTextView.getText().toString();

                if (allFieldsFilled(email) && emailValidation(email)) {
                    showLoadingDialog();
                    sendEmailToServer(email);
                } else {

                    showErrorDialog();
                }
            }
        });
    }

    public void showLoadingDialog(){
        PD = new ProgressDialog(getActivity());
        PD.setTitle(getResources().getString(R.string.progress_dialog_please_wait));
        PD.setMessage(getResources().getString(R.string.signup_you_in));
        PD.setCancelable(false);
        PD.show();
    }
    public void showErrorDialog(){
        alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setTitle("");
        alertDialog.setMessage("Invalid email");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();

    }



    private boolean allFieldsFilled(String email) {
        if (email.isEmpty()) {
            return false;
        }
        return true;
    }

    private boolean emailValidation(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }


    private void setGoBackToSignInClickListener() {
        fragmentRootView.findViewById(R.id.back_to_sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), MainLoginActivity.class);
                startActivity(intent);

            }
        });
    }

//    private void setContinueWithoutLoginClickListener() {
//        fragmentRootView.findViewById(R.id.continue_without_login).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Intent intent = new Intent(getActivity(), MainActivity.class);
//                startActivity(intent);
//
//            }
//        });
//    }


    private void sendEmailToServer(String text) {

        JsonObjectRequest jsonObjectRequest;
        try {
            jsonObjectRequest = getSignUpJsonRequest(UrlsRetail.SIGN_UP,text);
            VolleySingleton.getInstance(getActivity()).addToRequestQueue(jsonObjectRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private JsonObjectRequest getSignUpJsonRequest(String url,String text) throws JSONException {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("email", text);



        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url,jsonObject, getSignUpResponseListener(), new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                PD.dismiss();
                if(error instanceof NoConnectionError) {
                    showNoIntenerErrorDialog();
                }else{
                    showErrorInSignUpDialog();
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
    private Response.Listener<JSONObject> getSignUpResponseListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject fetchedJson = new JSONObject(response.toString());
                    checkSignUpResponse(fetchedJson);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    public void showNoIntenerErrorDialog(){
      AlertDialog  alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setTitle("");
        alertDialog.setMessage("Internet is not connected . Please Reconnect and Try again!");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();

    }

    public void showErrorInSignUpDialog(){
        AlertDialog  alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setTitle("Error");
        alertDialog.setMessage("Error during Sign Up");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();

    }


    private void checkSignUpResponse(JSONObject fetchedJson) throws JSONException {

        PD.dismiss();
        if (!fetchedJson.has("error")) {
            String token = fetchedJson.getString("token");
            String userId = fetchedJson.getString("user_id");
            String email=fetchedJson.getString("email");
            saveUserToSharedPreferences(token, userId, email);
            deleteAllBooksFromDataBase();
            showSignUpCompleteDialog();
        } else {
            setUserAllReadyExitsAlertDialog();
        }
    }

    private void deleteAllBooksFromDataBase() {
        new Delete().from(CartModel.class).execute();
    }

    private void saveUserToSharedPreferences(String token,String userId,String email) {
        SharedPreferences sharedPref = getActivity().getSharedPreferences(UrlsRetail.SAVED_USER_DETAILS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(UrlsRetail.SAVED_USER_TOKEN, token);
        editor.putString(UrlsRetail.SAVED_USER_ID, userId);
        editor.putString(UrlsRetail.SAVED_EMAIL_ID, email);
        editor.apply();
    }

    private void startMainActivity() {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    public void setUserAllReadyExitsAlertDialog(){
        userAllReadyExitsAlertDialog = new AlertDialog.Builder(getActivity()).create();
        userAllReadyExitsAlertDialog.setTitle("");
        userAllReadyExitsAlertDialog.setMessage("User AllReady Exits");
        userAllReadyExitsAlertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        userAllReadyExitsAlertDialog.show();

    }

    public  void showSignUpCompleteDialog(){
        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setTitle("Signup Successfull");
        alertDialog.setMessage("An email has been sent to you. PLease verify your account. Thank you!");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
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
                        startMainActivity();
                    }
                });
            }
        });
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();

    }

}
