package com.indiareads.retailapp.views.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
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

import org.json.JSONException;
import org.json.JSONObject;


public class ContactUsFragment extends Fragment implements AdapterView.OnItemSelectedListener {

  View fragmentRootView;

    EditText name;
    EditText email;
    EditText phoneNumber;
    EditText Query;

    ProgressDialog PD;

    Spinner queryTypeSpinner;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        fragmentRootView= inflater.inflate(R.layout.fragment_contact_us, container, false);

        setSpinner();
        fetchViewsFromXml();
        setMakeCallClickListener();
        setSubmitButtonClickListener();
        return fragmentRootView;
    }

    public void fetchViewsFromXml(){
        name=(EditText)fragmentRootView.findViewById(R.id.name);
        email=(EditText)fragmentRootView.findViewById(R.id.email);

        phoneNumber=(EditText)fragmentRootView.findViewById(R.id.phone);
        Query=(EditText)fragmentRootView.findViewById(R.id.query);

    }

    private void setSubmitButtonClickListener() {
        fragmentRootView.findViewById(R.id.contact_us_submit_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAllDetailsAreFilled() && emailValidation() && phoneNumberValidation()) {
                    showLoadingDialog();
                    sendQueryDetailsToServer(UrlsRetail.CONTACT_US);
                }
            }

        });
    }

    private boolean phoneNumberValidation() {
        if(phoneNumber.getText().toString().length()!=10){
            Toast.makeText(getActivity(),"Enter A valid Phone Number",Toast.LENGTH_LONG).show();
            return false;
        }else {
              return true;
        }
    }
    private boolean emailValidation() {
        if(android.util.Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()) {
            return true;
        }else{
            Toast.makeText(getActivity(),"Enter A valid Email",Toast.LENGTH_LONG).show();
            return false;
        }
    }


    public void showLoadingDialog(){
        PD = new ProgressDialog(getActivity());
        PD.setTitle(getResources().getString(R.string.progress_dialog_please_wait));
        PD.setMessage(getResources().getString(R.string.sending_your_query));
        PD.setCancelable(false);
        PD.show();
    }

    public boolean isAllDetailsAreFilled() {
        if(name.getText().toString().isEmpty()){
            Toast.makeText(getActivity(),"Enter Full Name",Toast.LENGTH_SHORT).show();
            return  false;}
        if(email.getText().toString().isEmpty()){
            Toast.makeText(getActivity(),"Enter Email",Toast.LENGTH_SHORT).show();
            return  false;}
        if(phoneNumber.getText().toString().isEmpty()){
            Toast.makeText(getActivity(),"Enter Phone Number",Toast.LENGTH_SHORT).show();
            return  false;}
        if(Query.getText().toString().isEmpty()){
            Toast.makeText(getActivity(),"Enter Query",Toast.LENGTH_SHORT).show();
            return  false;}


        return true;
    }



    public void setSpinner(){
         queryTypeSpinner = (Spinner) fragmentRootView.findViewById(R.id.issue_type_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.issue_types_array, R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        queryTypeSpinner.setAdapter(adapter);
        queryTypeSpinner.setOnItemSelectedListener(this);

    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void setMakeCallClickListener() {
        fragmentRootView.findViewById(R.id.contact_us_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Intent intent = new Intent(Intent.ACTION_CALL);
//                intent.setData(Uri.parse("tel:" + UrlsRetail.PHONE_NUMBER));
//                startActivity(intent);
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", UrlsRetail.PHONE_NUMBER, null));
                startActivity(intent);

            }
        });
    }


    public void sendQueryDetailsToServer(String url){
        JsonObjectRequest jsonObjectRequest = null;
        try {
            jsonObjectRequest = getJsonRequest(url);
            VolleySingleton.getInstance(getActivity()).addToRequestQueue(jsonObjectRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    public  void errorInSendingYourQuery(){
        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setTitle("Oops Something went wrong!");
        alertDialog.setMessage("Unable to submit your query");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        alertDialog.show();

    }


    private JsonObjectRequest getJsonRequest(String url) throws JSONException {


        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, getJsonObject(), getResponseListener(), new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                PD.dismiss();
             //   Log.e("error from add Query ", error.toString());
                if (error instanceof NoConnectionError) {
                    showNoInternetConnectionDialogForAddAddress();
                } else {
                    errorInSendingYourQuery();
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
                //Log.e("resp from add address ",response.toString());
                PD.dismiss();
                clearAllData();
                showQueryAddedSucessfullyDialog();
            }
        };
    }

    private void clearAllData() {
        name.setText("");
        email.setText("");
        phoneNumber.setText("");
        Query.setText("");
        queryTypeSpinner.setSelection(0);
    }

    public  void showQueryAddedSucessfullyDialog(){
        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setTitle("Successful");
        alertDialog.setMessage("We have received your query!");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        alertDialog.show();

    }


    public  void showNoInternetConnectionDialogForAddAddress(){
        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setTitle("No Internet Connection");
        alertDialog.setMessage("Sorry, No Internet Connectivity detected. Please reconnect and try again");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        alertDialog.show();

    }



    public JSONObject getJsonObject() throws JSONException {

        JSONObject obj=new JSONObject();
        obj.put("name",name.getText().toString());
        obj.put("email",email.getText().toString());
        obj.put("phone",phoneNumber.getText().toString());
        obj.put("query_type",queryTypeSpinner.getSelectedItem().toString());
        obj.put("query",Query.getText().toString());
        return obj;
    }
}
