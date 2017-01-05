package com.indiareads.retailapp.views.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

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

import org.json.JSONException;
import org.json.JSONObject;


public class ForgotPasswordFragment extends Fragment {


    View fragmentRootView;
    String email;

    ProgressDialog PD;
    private AlertDialog alertDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentRootView= inflater.inflate(R.layout.fragment_forgot_password, container, false);

        setForgotPasswordClickListener();

        return fragmentRootView;
    }

    private void setForgotPasswordClickListener() {
        fragmentRootView.findViewById(R.id.reset_your_password_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fetchEmailField();
                if (allFieldsFilled() && emailValidation()) {
                    showLoadingDialog();
                    sendForgotPasswordEmail();
                } else {

                    showErrorDialog();
                }
            }
        });
    }

    public void showLoadingDialog(){
        PD = new ProgressDialog(getActivity());
        PD.setTitle(getResources().getString(R.string.progress_dialog_please_wait));
        PD.setMessage(getResources().getString(R.string.processing));
        PD.setCancelable(false);
        PD.show();
    }

    public void showErrorDialog(){
        alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setTitle("");
        alertDialog.setMessage("Please enter a valid email address!");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();

    }

    private void fetchEmailField() {
        email = ((EditText)fragmentRootView.findViewById(R.id.forgot_password_email)).getText().toString();
    }

    private boolean allFieldsFilled() {
        if(email.isEmpty()) {
            return false;
        }
        return true;
    }

    private boolean emailValidation() {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }


    private void sendForgotPasswordEmail() {
        sendForgotPasswordEmailToServer(UrlsRetail.FORGOT_PASSWORD);
    }




    private void sendForgotPasswordEmailToServer(String urls) {
        JsonObjectRequest jsonObjectRequest = null;
        try {
            jsonObjectRequest = getForgotPasswordJsonRequest(urls);
            VolleySingleton.getInstance(getActivity()).addToRequestQueue(jsonObjectRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private JsonObjectRequest getForgotPasswordJsonRequest(String url) throws JSONException {

        JSONObject obj=new JSONObject();
        obj.put("email",email);

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url,obj, getForgotPasswordResponseListener(), new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                PD.dismiss();
                if(error instanceof NoConnectionError) {
                    showNoIntenerErrorDialog();
                }else{
                    showErrorInForgotPasswordDialog();
                }
                //       Toast.makeText(getActivity(),"error"+error.toString(),Toast.LENGTH_LONG).show();
            }
        });

        jsonRequest.setRetryPolicy(new DefaultRetryPolicy(
                20000,
                DefaultMaxRetries.APP_API_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        return jsonRequest;
    }


    @NonNull
    private Response.Listener<JSONObject> getForgotPasswordResponseListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject fetchedJson = new JSONObject(response.toString());
                    PD.dismiss();
                    showMessage(fetchedJson);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
    }


    public void showErrorInForgotPasswordDialog(){
        AlertDialog  alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setTitle("Oops, something went wrong!");
        alertDialog.setMessage("Please try again");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();

    }


    private void showMessage(JSONObject fetchedJson) throws JSONException {
        String status=fetchedJson.getString("status");
        if(status.equals("1")){
            mailSentSuccessFillDialog();
        }else{
            showUserDoNotExitErrorDialog();
        }
    }

    public void showUserDoNotExitErrorDialog(){
        alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setTitle("");
        alertDialog.setMessage("User Does not Exits");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();

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

    public void mailSentSuccessFillDialog(){
        alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setTitle("");
        alertDialog.setMessage("A password reset link has been sent to your mail id");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        getActivity().finish();
                    }
                });
        alertDialog.show();

    }


}
