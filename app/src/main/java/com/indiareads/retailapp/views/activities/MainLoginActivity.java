package com.indiareads.retailapp.views.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import com.indiareads.retailapp.R;
import com.indiareads.retailapp.corporate.view.activity.MainActivityCorporate;
import com.indiareads.retailapp.singleton.VolleySingleton;
import com.indiareads.retailapp.utils.DefaultMaxRetries;
import com.indiareads.retailapp.utils.UrlsRetail;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class MainLoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{


    private boolean mIsResolving = false;
    private boolean mShouldResolve = false;
    private static final String TAG = "SignInActivity";
    private static final int RC_SIGN_IN = 9001;
    private GoogleApiClient mGoogleApiClient;

    CallbackManager callbackManager;

    Context context;

    String email,password;
    EditText emailEditText;
    EditText passwordEditText;

    ProgressDialog PD;
    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // Initialize SDK before setContentView(Layout ID)
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        setContentView(R.layout.activity_main_login);

        initializeGoogleApiClient();



        context=this;

        fetchViewsFromXml();
        setClickListeners();

    }

    public void fetchViewsFromXml(){
        emailEditText = (EditText)findViewById(R.id.email);
        passwordEditText = (EditText)findViewById(R.id.password);
    }

    public void setClickListeners(){
        setLoginWithFacebookClickListener();
        setForgotPasswordClickListener();
        setLoginWithGoogleClickListener();
        setSignInClickListener();
        setSignUpClickListener();
    }

    //***************************************************************Google **********************************************

    public void initializeGoogleApiClient(){
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // [END configure_signin]

        // [START build_client]
        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }


    private void sendSocialLoginDetailsToServer(String email,String id,String signUpType) {

        showLoadingDialog();
        JsonObjectRequest jsonObjectRequest;
        try {
            jsonObjectRequest = getSocialLoginJsonRequest(UrlsRetail.SOCIAL_LOGIN, email,id,signUpType);
            VolleySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private JsonObjectRequest getSocialLoginJsonRequest(String url,String email,String id,String signUpType) throws JSONException {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("email", email);
        jsonObject.put("socailID", id);
        jsonObject.put("signupType", signUpType);



        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url,jsonObject, getSocialLoginResponseListener(), new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                PD.dismiss();
                //Log.e("error social","error");
                if(error instanceof NoConnectionError) {
                    showNoInternetConnectionDialogForLogin();
                }else{
                    showLoginErrorDialog();
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
    private Response.Listener<JSONObject> getSocialLoginResponseListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    //Log.e("login suceess",response.toString());
                    JSONObject fetchedJson = new JSONObject(response.toString());
                    checkSocialLoginResponse(fetchedJson);
                } catch (JSONException e) {
                  //  Log.e("error",e.toString());
                    e.printStackTrace();
                }
            }
        };
    }

    private void checkSocialLoginResponse(JSONObject fetchedJson) throws JSONException {
        PD.dismiss();
        if(!fetchedJson.has("error")) {
            String userId = fetchedJson.getString("user_id");

            String email = fetchedJson.getString("email");
            saveRetailUserToSharedPreferences(userId,email);
            startMainActivity();
        }else{
            showSocialLoginErrorDialog();
        }


    }

    public void showSocialLoginErrorDialog(){
        alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle("Oops something went wrong!");
        alertDialog.setMessage("Please try again");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();

    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        //Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    //***************************************************************Google **********************************************


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);



        if (requestCode == RC_SIGN_IN) {
            //Log.e("result  code" ,resultCode+"");
            if(resultCode== -1) {
                try {
                    GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                    handleSignInResult(result);
                } catch (Exception e) {
                    showGoogleLoginErrorDialog();
                }
            }else{
                showGoogleLoginErrorDialog();
            }

            return;

        }

        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void handleSignInResult(GoogleSignInResult result) {
//        Log.d("Login", "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
//            String personName = acct.getDisplayName();
//            String personEmail = acct.getEmail();
//            String personId = acct.getId();
//            Log.d("name is ",  personEmail);
            sendSocialLoginDetailsToServer(acct.getEmail(),acct.getId(),"Google");

        } else {
            // Signed out, show unauthenticated UI.
            showGoogleLoginErrorDialog();
        }
    }

    public void showGoogleLoginErrorDialog(){
        alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle("Oops, something went wrong!");
        alertDialog.setMessage("Please try again.");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();

    }


//****************************************************************Click Listener**************************************

    private void setLoginWithFacebookClickListener() {
        findViewById(R.id.main_login_fb_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initializeFacebookSdk();
            }
        });
    }

    private void setLoginWithGoogleClickListener() {
        findViewById(R.id.main_login_google_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
    }

    private void setSignInClickListener() {
        findViewById(R.id.signin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeKeyboardAndroid();
                fetchLoginFields();

                if (allFieldsFilled() && emailValidation()) {
                    showLoadingDialog();
                    sendLoginDetailsToServer();
                } else {

                    showErrorDialog();
                }

            }
        });
    }
    public void closeKeyboardAndroid(){
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    private void fetchLoginFields() {
        email = emailEditText.getText().toString();
        password = passwordEditText.getText().toString();
    }

    private boolean allFieldsFilled() {
        if(email.isEmpty() || password.isEmpty()) {
            return false;
        }
        return true;
    }

    private boolean emailValidation() {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }


    private void sendLoginDetailsToServer() {

        JsonObjectRequest jsonObjectRequest = null;
        try {
            jsonObjectRequest = getLoginJsonRequest(UrlsRetail.SIGN_IN);
            jsonObjectRequest.setTag(UrlsRetail.SIGN_IN_VOLLEY_REQUEST_TAG);
            VolleySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private JsonObjectRequest getLoginJsonRequest(String url) throws JSONException {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("email", email);
        jsonObject.put("password", password);


        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url,jsonObject, getResponseListener(), new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("login error ", error.toString());
                PD.dismiss();
                if(error instanceof NoConnectionError) {
                    showNoInternetConnectionDialogForLogin();
                }else{
                    showLoginErrorDialog();
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
                try {
                    Log.e("login suceess",response.toString());
                    JSONObject fetchedJson = new JSONObject(response.toString());
                    checkLoginResponse(fetchedJson);
                } catch (JSONException e) {
                   // Log.e("error",e.toString());
                    e.printStackTrace();
                }
            }
        };
    }

    public void showLoginErrorDialog(){
        alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Oops something went wrong!");
        alertDialog.setMessage("Please try again");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "RETRY",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "CANCEL",
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
                        alertDialog.dismiss();
                        fetchLoginFields();

                        if (allFieldsFilled() && emailValidation()) {
                            showLoadingDialog();
                            sendLoginDetailsToServer();
                        } else {

                            showErrorDialog();
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

    private void checkLoginResponse(JSONObject fetchedJson) throws JSONException {


          PD.dismiss();
        if(!fetchedJson.has("error")) {
            if(fetchedJson.getString("user_type").equals("2")){
                saveCorporateUserToSharedPreferences(fetchedJson.getString("user_id"),fetchedJson.getString("email"),fetchedJson.getString("company_name"),fetchedJson.getString("company_id"),fetchedJson.getString("corporate_type"),fetchedJson.getString("rent_type"),fetchedJson.getString("number_of_books"),fetchedJson.getString("delevers_type"),fetchedJson.getString("company_fix_price"));
                startMainActivityCorporate();
            }else{
                saveRetailUserToSharedPreferences(fetchedJson.getString("user_id"),fetchedJson.getString("email"));
                startMainActivity();
            }

        }else{
            showErrorDialog();
        }


    }

    private void startMainActivityCorporate() {
        Intent intent = new Intent(context, MainActivityCorporate.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void saveRetailUserToSharedPreferences(String userId,String email) {
        SharedPreferences sharedPref = context.getSharedPreferences(UrlsRetail.SAVED_USER_DETAILS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        //Log.e("user id ",userId);
      //  editor.putString(UrlsRetail.SAVED_USER_TOKEN, token);
        editor.putString(UrlsRetail.SAVED_USER_ID, userId);
        editor.putString(UrlsRetail.SAVED_EMAIL_ID, email);
        editor.putString(UrlsRetail.SAVED_USER_TYPE, "1");
        editor.apply();
    }

    private void saveCorporateUserToSharedPreferences(String userId,String email,String companyName,String companyId,String corporateType,String rentType,String numOfBook,String deliveryType,String companyFixPrice) {
        SharedPreferences sharedPref = context.getSharedPreferences(UrlsRetail.SAVED_USER_DETAILS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
//        Log.e("saved user id corp",userId);
        //Log.e("user id ",userId);
        //  editor.putString(UrlsRetail.SAVED_USER_TOKEN, token);
        editor.putString(UrlsRetail.SAVED_USER_ID, userId);
        editor.putString(UrlsRetail.SAVED_EMAIL_ID, email);
        editor.putString(UrlsRetail.SAVED_COMPANY_NAME, companyName);
        editor.putString(UrlsRetail.SAVED_COMPANY_ID, companyId);
        editor.putString(UrlsRetail.SAVED_CORPORATE_TYPE, corporateType);
        editor.putString(UrlsRetail.SAVED_RENT_TYPE, rentType);
        editor.putString(UrlsRetail.SAVED_NUMBER_OF_BOOKS, numOfBook);
        editor.putString(UrlsRetail.SAVED_DELIVERY_TYPE, deliveryType);
        editor.putString(UrlsRetail.SAVED_COMPANY_FIX_PRICE, companyFixPrice);
        editor.putString(UrlsRetail.SAVED_USER_TYPE, "2");
        editor.apply();
    }

    private void startMainActivity() {
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }


    private void setSignUpClickListener() {
        findViewById(R.id.main_login_sign_up_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainLoginActivity.this, SignUpActivity.class);
                startActivity(i);
            }
        });
    }

//    private void setContinueWithOutLoginClickListener() {
//        findViewById(R.id.main_login_continue_with_out_login).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Intent intent = new Intent(context, MainActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent);
//                finish();
//            }
//        });
//    }

    private void setForgotPasswordClickListener() {
        findViewById(R.id.forgot_password_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(MainLoginActivity.this, ForgotPasswordActivity.class);
                startActivity(i);
            }
        });
    }

//*****************************************************************Click Listener*************************************


//***************************************************************FACEBOOK **********************************************

    public void initializeFacebookSdk(){

        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList( "email"));

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {

                        OnFaceBookLoginSuccess(loginResult);
                    }

                    @Override
                    public void onCancel() {
                        OnFaceBookLoginCancel();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        OnFaceBookLoginError(exception);
                    }
                });


    }

    // On Login Success
    public void OnFaceBookLoginSuccess(final LoginResult loginResult){

        GraphRequest request=  GraphRequest.newMeRequest(
                loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject json, GraphResponse response) {
                        try {
//                            Bundle bFacebookData = getFacebookData(json);
                            //   Toast.makeText(context, AccessToken.getCurrentAccessToken().getToken() + "", Toast.LENGTH_LONG).show();
                            //   Log.e("access token ", AccessToken.getCurrentAccessToken().getToken());
//                            sendFacebookLoginDetailsToServer(AccessToken.getCurrentAccessToken().getToken());
                            sendSocialLoginDetailsToServer(json.getString("email"),json.getString("id"),"Facebook");

                        }catch (Exception e){

                        }
                    }

                }) ;
        Bundle parameters = new Bundle();
        parameters.putString("fields", "email"); // Parámetros que pedimos a facebook
        request.setParameters(parameters);
        request.executeAsync();
    }

//    private Bundle getFacebookData(JSONObject object) throws JSONException {
//        Bundle bundle = new Bundle();
//        try {
//
//            String id = object.getString("id");
//            Log.e("here id is ",id);
//
////                URL profile_pic = new URL("https://graph.facebook.com/" + id + "/picture?width=200&height=150");
////                Log.i("profile_pic", profile_pic + "");
////                bundle.putString("profile_pic", profile_pic.toString());
//
////            bundle.putString("idFacebook", id);
////            if (object.has("first_name"))
////                bundle.putString("first_name", object.getString("first_name"));
////            if (object.has("last_name"))
////                bundle.putString("last_name", object.getString("last_name"));
//            if (object.has("email")) {
//                bundle.putString("email", object.getString("email"));
//                Log.e("email is ",object.getString("email"));
//            }else{
//                Log.e("email is ","false");
//            }
//
////            if (object.has("gender"))
////                bundle.putString("gender", object.getString("gender"));
////            if (object.has("birthday"))
////                bundle.putString("birthday", object.getString("birthday"));
////            if (object.has("location"))
////                bundle.putString("location", object.getJSONObject("location").getString("name"));
//
//
//        } catch (Exception e) {
//           Log.e("error us ",e.toString());
//        }
//        return bundle;
//    }

    // On Login Cancel
    public void OnFaceBookLoginCancel(){
        Toast.makeText(MainLoginActivity.this, "Login Cancel", Toast.LENGTH_LONG).show();
    }

    // On Login Error
    public void OnFaceBookLoginError(FacebookException exception){
        showFacebookLoginErrorDialog();
    }

    public void showFacebookLoginErrorDialog(){
        alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle("Oops something went wrong!");
        alertDialog.setMessage("Please try again");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();

    }


//    private void sendFacebookLoginDetailsToServer(String email,String id) {
//
//        Log.e("facebook email is ",email);
//        Log.e("facebook id is ",id);
//
//        JsonObjectRequest jsonObjectRequest = null;
//        try {
////            jsonObjectRequest = getFacebookLoginJsonRequest(UrlsRetail.SIGN_IN, email);
////            VolleySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private JsonObjectRequest getFacebookLoginJsonRequest(String url,String accessToken) throws JSONException {
//
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("accessToken", accessToken);
//
//
//        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url,jsonObject, getResponseListener(), new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//
//            }
//        });
//
//        jsonRequest.setRetryPolicy(new DefaultRetryPolicy(
//                20000,
//                DefaultMaxRetries.APP_API_MAX_RETRIES,
//                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//
//        return jsonRequest;
//    }





//***************************************************************FACEBOOK**********************************************


    public void showLoadingDialog(){
        PD = new ProgressDialog(context);
        PD.setTitle(getResources().getString(R.string.progress_dialog_please_wait));
        PD.setMessage(getResources().getString(R.string.logging_you_in));
        PD.setCancelable(false);
        PD.show();
    }

    public void showErrorDialog(){
        alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle("");
        alertDialog.setMessage("Invalid email or password");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();

    }



    public  void showNoInternetConnectionDialogForLogin(){
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("No Internet Connection");
        alertDialog.setMessage("Sorry, No Internet Connectivity detected. Please reconnect and try again");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "RETRY",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "CANCEL",
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
                        alertDialog.dismiss();
                        fetchLoginFields();

                        if (allFieldsFilled() && emailValidation()) {
                            showLoadingDialog();
                            sendLoginDetailsToServer();
                        } else {

                            showErrorDialog();
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



    public  boolean hasActiveInternetConnection(Context context) {
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

    private static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            return  true;
        }
        else
            return false;
    }



    @Override
    public void onStart() {
        super.onStart();
        //    mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        //  mGoogleApiClient.disconnect();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        RequestQueue mQueue= VolleySingleton.getInstance(this).getRequestQueue();
        mQueue.cancelAll(UrlsRetail.SIGN_IN_VOLLEY_REQUEST_TAG);
        finish();
    }



}