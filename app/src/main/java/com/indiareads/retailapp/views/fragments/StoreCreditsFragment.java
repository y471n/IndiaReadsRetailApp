package com.indiareads.retailapp.views.fragments;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.indiareads.retailapp.R;
import com.indiareads.retailapp.adapters.StoreCreditsListAdapter;
import com.indiareads.retailapp.interfaces.ClickListener;
import com.indiareads.retailapp.models.StoreCredits;
import com.indiareads.retailapp.singleton.VolleySingleton;
import com.indiareads.retailapp.utils.CommonMethods;
import com.indiareads.retailapp.utils.DefaultMaxRetries;
import com.indiareads.retailapp.utils.UrlsRetail;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class StoreCreditsFragment extends Fragment {

    private ArrayList<StoreCredits> storeCreditList;
    private View fragmentRootView;
    private RecyclerView mRecyclerView;
    private StoreCreditsListAdapter storeCreditListAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        fragmentRootView= inflater.inflate(R.layout.fragment_store_credits, container, false);

        setRetryButtonClickListener();
        checkInternetConnectivity();
        return fragmentRootView;

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
    private void checkInternetConnectivity() {
        if(!CommonMethods.hasActiveInternetConnection(getActivity())){
            showNetworkNotAvailable();
        }else{
            executeOtherMethods();
        }
    }

    private void setNoInternetAccessBackgroundImage() {
//        fragmentRootView.findViewById(R.id.app_screen_background_image).setVisibility(View.VISIBLE);
//        fragmentRootView.findViewById(R.id.store_credit_page_complete_layout).setVisibility(View.GONE);
    }

    private void executeOtherMethods() {
        showLoadingScreens();
        fetchStoreCredits();
    }

    public void showLoadingScreens(){
        fragmentRootView.findViewById(R.id.store_credits_loading_screen).setVisibility(View.VISIBLE);
        fragmentRootView.findViewById(R.id.store_credit_layout).setVisibility(View.GONE);
    }

    public void hideLoadingScreen(){
        fragmentRootView.findViewById(R.id.store_credits_loading_screen).setVisibility(View.GONE);
        fragmentRootView.findViewById(R.id.store_credit_layout).setVisibility(View.VISIBLE);
    }

    private void fetchStoreCredits() {
        storeCreditList = new ArrayList<StoreCredits>();
        fetchStoreCreditsFromStorage();
    }

    public void fetchStoreCreditsFromStorage(){
        fetchStoreCreditsFromRemoteServer(UrlsRetail.STORE_CREDITS_DETAILS);
    }

    public String getUserToken(){
        SharedPreferences settings = getActivity().getSharedPreferences(UrlsRetail.SAVED_USER_DETAILS, getActivity().MODE_PRIVATE);
        // Reading from SharedPreferences
        return settings.getString(UrlsRetail.SAVED_USER_TOKEN,"");
    }

    private void fetchStoreCreditsFromRemoteServer(String urls) {
        JsonObjectRequest jsonObjectRequest ;
        try {
            jsonObjectRequest = getStoreCreditsJsonRequest(urls);
            jsonObjectRequest.setTag(UrlsRetail.STORE_CREDITS_DETAILS_VOLLEY_REQUEST_TAG);
            VolleySingleton.getInstance(getActivity()).addToRequestQueue(jsonObjectRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private JsonObjectRequest getStoreCreditsJsonRequest(String url) throws JSONException {

        JSONObject obj=new JSONObject();
        obj.put("user_id", getUserId());


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
                    //Log.e("store credit response",fetchedJson.toString());
                    createStoreCreditList(fetchedJson, R.id.my_store_credit_recycler_view);
                    hideLoadingScreen();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    public String getUserId(){
        SharedPreferences settings = getActivity().getSharedPreferences(UrlsRetail.SAVED_USER_DETAILS, getActivity().MODE_PRIVATE);
        // Reading from SharedPreferences
        return settings.getString(UrlsRetail.SAVED_USER_ID,"");
    }




    private void createStoreCreditList(JSONObject fetchedJson,int recyclerViewResource) {


        try {
            if(fetchedJson.has("availableCredit")) {
                ((TextView) (fragmentRootView.findViewById(R.id.store_credit_price))).setText(fetchedJson.getString("availableCredit"));
            }else{
                ((TextView) (fragmentRootView.findViewById(R.id.store_credit_price))).setText("0");
            }
            JSONArray List = fetchedJson.getJSONArray("data");
            Gson gson = new Gson();
            for (int i=0; i<List.length(); i++) {
                JSONObject jsonObject = List.getJSONObject(i);
                StoreCredits storeCredits = gson.fromJson(jsonObject.toString(), StoreCredits.class);

                storeCreditList.add(storeCredits);
            }

            createRecycleListView(recyclerViewResource);


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }




    private void createRecycleListView(int recyclerViewResource) {
        mRecyclerView = (RecyclerView) fragmentRootView.findViewById(recyclerViewResource);
        storeCreditListAdapter = new StoreCreditsListAdapter(getActivity(), storeCreditList);
        mRecyclerView.setAdapter(storeCreditListAdapter);
        storeCreditListAdapter.setClickListener(new ClickListener() {
            @Override
            public void itemClicked(View view, int position) {



            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        mRecyclerView.setLayoutManager(linearLayoutManager);

    }
}
