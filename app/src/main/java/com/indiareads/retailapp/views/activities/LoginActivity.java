package com.indiareads.retailapp.views.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.activeandroid.query.Select;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
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
import com.indiareads.retailapp.models.CartModel;
import com.indiareads.retailapp.singleton.VolleySingleton;
import com.indiareads.retailapp.utils.CommonMethods;
import com.indiareads.retailapp.utils.DefaultMaxRetries;
import com.indiareads.retailapp.utils.UrlsRetail;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{

   Toolbar toolbar;

    private boolean mIsResolving = false;
    private boolean mShouldResolve = false;
    private static final String TAG = "SignInActivity";
    private static final int RC_SIGN_IN = 9001;
    private GoogleApiClient mGoogleApiClient;

    CallbackManager callbackManager;
    List<CartModel> cartBookList;


    ImageView exitingUserImageView,checkOutAsGuestImageView;

    RelativeLayout passwordFieldRelativeLayout;
    String email,password;

    ProgressDialog PD;
    private AlertDialog alertDialog;
    ProgressDialog PDSignUp;
    private AlertDialog alertDialogSignUp;
    private AlertDialog userAllReadyExitsAlertDialog;
    Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize SDK before setContentView(Layout ID)
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        setContentView(R.layout.activity_login);

        initializeGoogleApiClient();


        createToolbar();
        context=this;
        fetchViewsFromXml();

        setExitingUserClickListener();
        setCheckOutAsGuestClickListener();
        setForgotPasswordClickListener();
        setProceedButtonClickListener();
        setLoginWithFacebookClickListener();
        setLoginWithGoogleClickListener();
    }
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




    private void createToolbar() {
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setLogo(R.drawable.logo);

    }



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

    // On Login Cancel
    public void OnFaceBookLoginCancel(){
        Toast.makeText(context, "Login Cancel", Toast.LENGTH_LONG).show();
    }

    // On Login Error
    public void OnFaceBookLoginError(FacebookException exception){
        showFacebookLoginErrorDialog();
    }

    public void showFacebookLoginErrorDialog(){
        alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle("Error");
        alertDialog.setMessage("Error From Login With Facebook");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();

    }




    //*************************************************Facebook end************************************


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
                    JSONObject fetchedJson = new JSONObject(response.toString());
                    checkSocialLoginResponse(fetchedJson);
                } catch (JSONException e) {
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
            saveUserToSharedPreferences(email,userId);
            startChooseDeliveryAddressActivity();
        }else{
            showSocialLoginErrorDialog();
        }


    }

    public void showSocialLoginErrorDialog(){
        alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle("Error");
        alertDialog.setMessage("Error During Login");
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
      //  Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    //***************************************************************Google **********************************************


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == RC_SIGN_IN) {
            try {
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                handleSignInResult(result);
            }catch(Exception e){
                showGoogleLoginErrorDialog();
            }
            return;

        }

        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void handleSignInResult(GoogleSignInResult result) {
        //Log.d("Login", "handleSignInResult:" + result.isSuccess());
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
        alertDialog.setTitle("Error");
        alertDialog.setMessage("Error From Login With Google");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();

    }



    public void fetchViewsFromXml(){
        exitingUserImageView=(ImageView)findViewById(R.id.exiting_user_image_view);
        checkOutAsGuestImageView=(ImageView)findViewById(R.id.checkout_as_guest_image_view);
        passwordFieldRelativeLayout=(RelativeLayout)findViewById(R.id.password_field);

    }



    private void setExitingUserClickListener() {
        findViewById(R.id.exiting_user_linear_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                exitingUserImageView.setBackgroundResource(R.drawable.checkoutselected);
                checkOutAsGuestImageView.setBackgroundResource(R.drawable.checkout_not_selected);
                passwordFieldRelativeLayout.setVisibility(View.VISIBLE);
                setExitingUserTagChecked();
            }
        });
    }

    private void setExitingUserTagChecked() {
        exitingUserImageView.setTag("checked");
        checkOutAsGuestImageView.setTag("unchecked");
    }

    private void setCheckOutAsGuestTagChecked() {
        exitingUserImageView.setTag("unchecked");
        checkOutAsGuestImageView.setTag("checked");
    }

    private void setCheckOutAsGuestClickListener() {
        findViewById(R.id.checkout_as_guest_linear_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                exitingUserImageView.setBackgroundResource(R.drawable.checkout_not_selected);
                checkOutAsGuestImageView.setBackgroundResource(R.drawable.checkoutselected);
                passwordFieldRelativeLayout.setVisibility(View.GONE);
                setCheckOutAsGuestTagChecked();

            }
        });
    }

    private void setForgotPasswordClickListener() {
        findViewById(R.id.forgot_password_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(context, ForgotPasswordActivity.class);
                startActivity(i);

            }
        });
    }

    private void setProceedButtonClickListener() {
        findViewById(R.id.login_proceed_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (exitingUserImageView.getTag().equals("checked")) {
                    runLoginApi();
                } else {
                    runSignUpApi();
                }

            }
        });
    }


    private void runLoginApi() {
        fetchLoginFields();

        if (allFieldsFilled() && emailValidation()) {
            showLoadingDialog();
            sendLoginDetailsToServer();
        } else {

            showErrorDialog();
        }
    }

    private void fetchLoginFields() {
        email = ((EditText)findViewById(R.id.email)).getText().toString();
        password = ((EditText)findViewById(R.id.password)).getText().toString();
    }

    private boolean allFieldsFilled() {
        if (email.isEmpty() || password.isEmpty()) {
            return false;
        }
        return true;
    }

    private boolean emailValidation() {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public void showLoadingDialog(){
        PD = new ProgressDialog(context);
        PD.setTitle(getResources().getString(R.string.progress_dialog_please_wait));
        PD.setMessage(getResources().getString(R.string.logging_you_in));
        PD.setCancelable(false);
        PD.show();
    }

    public void showErrorDialog(){
        alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle("Error");
        alertDialog.setMessage("Invalid email or password");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();

    }

    private void sendLoginDetailsToServer() {

        JsonObjectRequest jsonObjectRequest;
        try {
            jsonObjectRequest = getLoginJsonRequest(UrlsRetail.SIGN_IN);
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
                PD.dismiss();
                Log.e("error isss ",error.toString());
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
                    JSONObject fetchedJson = new JSONObject(response.toString());
                    //Log.e("login ",fetchedJson.toString());
                    checkLoginResponse(fetchedJson);
                } catch (JSONException e) {
                  //  Log.e("error", e.toString());
                    e.printStackTrace();
                }
            }
        };
    }

    private void checkLoginResponse(JSONObject fetchedJson) throws JSONException {
        PD.dismiss();
        if(!fetchedJson.has("error")) {
            String email = fetchedJson.getString("email");
            String userId = fetchedJson.getString("user_id");
            saveUserToSharedPreferences(email, userId);
            startChooseDeliveryAddressActivity();
            //  addBookToBookShelf();
            // startMyCartActivity();
        }else{
            showErrorDialog();
        }
    }

    public void showLoginErrorDialog(){
        alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle("Error");
        alertDialog.setMessage("Error in Login ");
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
                        runLoginApi();
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

    public void showSignUpErrorDialog(){
        alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle("Error");
        alertDialog.setMessage("Error in SignUp ");
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
                        runSignUpApi();
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



    public  void showNoInternetConnectionDialogForLogin(){
        final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
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
                        runLoginApi();
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

    public  void showNoInternetConnectionDialogForSignUp(){
        final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
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
                        runSignUpApi();
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

    public void startChooseDeliveryAddressActivity(){
        addBookToBookShelf();
        Intent intent = new Intent(context, ChooseDeliveryAddressActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.right_in, R.anim.left_out);
        finish();
    }

    private void saveUserToSharedPreferences(String email,String userId) {
        SharedPreferences sharedPref = context.getSharedPreferences(UrlsRetail.SAVED_USER_DETAILS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(UrlsRetail.SAVED_EMAIL_ID, email);
        editor.putString(UrlsRetail.SAVED_USER_ID, userId);
        editor.apply();
    }

    public void startMyCartActivity(){
        Intent intent = new Intent(context, MyCartActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    private void runSignUpApi() {
        fetchSignUpFields();

        if (allSignUpFieldsFilled() && emailValidation()) {
            showSignUpLoadingDialog();
            sendEmailToServer(email);
        } else {

            showNoEmailSignUpErrorDialog();
        }
    }

    public void showNoEmailSignUpErrorDialog(){
        alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle("Error");
        alertDialog.setMessage("Invalid email");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();

    }

    private boolean allSignUpFieldsFilled() {
        if (email.isEmpty()) {
            return false;
        }
        return true;
    }
    private void fetchSignUpFields() {
        email = ((EditText)findViewById(R.id.email)).getText().toString();
    }

    public void showSignUpLoadingDialog(){
        PDSignUp = new ProgressDialog(context);
        PDSignUp.setTitle(getResources().getString(R.string.progress_dialog_please_wait));
        PDSignUp.setMessage(getResources().getString(R.string.signup_you_in));
        PDSignUp.setCancelable(false);
        PDSignUp.show();
    }
//    public void showSignUpErrorDialog(){
//        alertDialogSignUp = new AlertDialog.Builder(getActivity()).create();
//        alertDialogSignUp.setTitle("Error");
//        alertDialogSignUp.setMessage("Invalid email");
//        alertDialogSignUp.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                });
//        alertDialogSignUp.show();
//
//    }

    private void sendEmailToServer(String text) {

        JsonObjectRequest jsonObjectRequest;
        try {
            jsonObjectRequest = getSignUpJsonRequest(UrlsRetail.SIGN_UP,text);
            VolleySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private JsonObjectRequest getSignUpJsonRequest(String url,String text) throws JSONException {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("email", text);


        //Log.e("json",jsonObject.toString());

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url,jsonObject, getSignUpResponseListener(), new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                PDSignUp.dismiss();
                if(error instanceof NoConnectionError) {
                    showNoInternetConnectionDialogForSignUp();
                }else{
                    showSignUpErrorDialog();
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
                    //Log.e("sign up ",fetchedJson.toString());

                    checkSignUpResponse(fetchedJson);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    private void checkSignUpResponse(JSONObject fetchedJson) throws JSONException {

        PDSignUp.dismiss();
        if (!fetchedJson.has("error")) {
        //    String token = fetchedJson.getString("token");
            String userId = fetchedJson.getString("user_id");
            String email = fetchedJson.getString("email");
            saveUserToSharedPreferences(email, userId);
//            addBookToBookShelf();
//            startMyCartActivity();
            startChooseDeliveryAddressActivity();
        } else {
            setUserAllReadyExitsAlertDialog();
        }
    }

    public void setUserAllReadyExitsAlertDialog(){
        userAllReadyExitsAlertDialog = new AlertDialog.Builder(context).create();
        userAllReadyExitsAlertDialog.setTitle("Error");
        userAllReadyExitsAlertDialog.setMessage("User AllReady Exits");
        userAllReadyExitsAlertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        userAllReadyExitsAlertDialog.show();

    }


    private void addBookToBookShelf() {
        try {
            fetchBooksFromDatbase();
            for (int i = 0; i < cartBookList.size(); i++) {
                CartModel book = cartBookList.get(i);
                if (book.getToBuy().equals("1")) {
//                    Log.e("adding book buy ",book.getTitle());
//                    sendBookDetailsToServerToAddInBookShelf("1", UrlsRetail.ADD_TO_BOOKSHELF_FOR_BUY, book);
                } else {
                    sendBookDetailsToServerToAddInBookShelf(UrlsRetail.CHECK_BOOK_AVAILABILITY_FOR_RENT, book);
                }
            }
        }catch(Exception e){

        }
    }

    public void fetchBooksFromDatbase() {
        Select select = new Select();
        cartBookList = select.all().from(CartModel.class).execute();

    }


    private void sendBookDetailsToServerToAddInBookShelf(String url,CartModel book) throws JSONException {

        JsonObjectRequest jsonObjectRequest = getAddToBookShelfJsonRequest(url, book);
        VolleySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);

    }

    private JsonObjectRequest getAddToBookShelfJsonRequest(String url,CartModel book) throws JSONException {

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url,createAddToBookShelfJsonData(book), getAddToBookShelfResponseListener(), new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });

        jsonRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        return jsonRequest;
    }

    @NonNull
    private Response.Listener<JSONObject> getAddToBookShelfResponseListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject fetchedJson = new JSONObject(response.toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
    }


    public JSONObject createAddToBookShelfJsonData(CartModel book) throws JSONException {
        JSONObject obj=new JSONObject();
        obj.put("isbn13",book.getIsbn13());
        obj.put("init_pay",book.getInitialPayable());
        obj.put("mrp",book.getMrp());
        obj.put("title",book.getTitle());
        obj.put("contributor_name_1",book.getAuthor1());

        if(CommonMethods.isUserLogedIn(this)){
            obj.put("is_user_login","1");
        }else{
            obj.put("is_user_login","0");
        }
        if(CommonMethods.isUserLogedIn(this)) {
            obj.put("user_id", getUserId());
        }


//        obj.put("email",getEmailId());
//        obj.put("isbn13",book.getIsbn13());
//        obj.put("title",book.getTitle());
//        obj.put("author",book.getAuthor1());
//        obj.put("user_id",getUserId());
//        obj.put("status",status);
//        Log.e("add to bookshelf json",obj.toString());
        return obj;
    }

    public String getUserId(){
        SharedPreferences settings = context.getSharedPreferences(UrlsRetail.SAVED_USER_DETAILS, context.MODE_PRIVATE);
        // Reading from SharedPreferences
        return settings.getString(UrlsRetail.SAVED_USER_ID,"");
    }

    public String getEmailId(){
        SharedPreferences settings = context.getSharedPreferences(UrlsRetail.SAVED_USER_DETAILS, context.MODE_PRIVATE);
        // Reading from SharedPreferences
        return settings.getString(UrlsRetail.SAVED_EMAIL_ID,"");
    }




    public String getUserToken(){
        SharedPreferences settings = context.getSharedPreferences(UrlsRetail.SAVED_USER_DETAILS, context.MODE_PRIVATE);
        // Reading from SharedPreferences
        return settings.getString(UrlsRetail.SAVED_USER_TOKEN,"");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
     //   getMenuInflater().inflate(R.menu.menu_items_short, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.action_book_shelf:
                intent = new Intent(this, BookShelfActivity.class);
                intent.putExtra("OpenFragment", "0");
                startActivity(intent);
                return true;
            case R.id.action_return_books:
                intent = new Intent(this, BookShelfActivity.class);
                intent.putExtra("OpenFragment", "1");
                startActivity(intent);
                return true;
            case R.id.action_my_orders:
                intent = new Intent(this, MyOrderActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_store_credits:
                intent = new Intent(this, StoreCreditsActivity.class);
                startActivity(intent);
                return true;
            case R.id.my_profile:
                intent = new Intent(this, ProfilePageActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_cart:
                intent = new Intent(this, MyCartActivity.class);
                startActivity(intent);
                return true;

            case android.R.id.home:
                finish();

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
