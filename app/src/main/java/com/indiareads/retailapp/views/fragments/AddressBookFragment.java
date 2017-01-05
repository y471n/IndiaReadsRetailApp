package com.indiareads.retailapp.views.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
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
import com.indiareads.retailapp.adapters.AddressBookListAdapter;
import com.indiareads.retailapp.interfaces.ClickListener;
import com.indiareads.retailapp.models.Address;
import com.indiareads.retailapp.singleton.VolleySingleton;
import com.indiareads.retailapp.utils.CommonMethods;
import com.indiareads.retailapp.utils.DefaultMaxRetries;
import com.indiareads.retailapp.utils.UrlsRetail;
import com.indiareads.retailapp.views.activities.AddressBookAddAddressActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class AddressBookFragment extends Fragment {

    View fragmentRootView;

    private RecyclerView addressBookRecyclerView;
    private AddressBookListAdapter addressListAdapter;

    ArrayList<Address> displayAddressesList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentRootView=inflater.inflate(R.layout.fragment_address_book, container, false);

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
//        fragmentRootView.findViewById(R.id.address_book_complete_layout).setVisibility(View.GONE);
    }

    private void executeOtherMethods() {
        showLoadingScreen();
        fetchAddresses();
        setAddAddressClickListener();
    }


    public void showLoadingScreen(){
        fragmentRootView.findViewById(R.id.address_book_loading_screen).setVisibility(View.VISIBLE);
        fragmentRootView.findViewById(R.id.address_book_content).setVisibility(View.GONE);
    }
    public void hideLoadingScreen(){
        fragmentRootView.findViewById(R.id.address_book_loading_screen).setVisibility(View.GONE);
        fragmentRootView.findViewById(R.id.address_book_content).setVisibility(View.VISIBLE);
    }

    private void showNoAddressTextAndHideRecylerView() {
             hideLoadingScreen();
         fragmentRootView.findViewById(R.id.no_address_in_list_text).setVisibility(View.VISIBLE);
        fragmentRootView.findViewById(R.id.address_book_items_recycler_view).setVisibility(View.GONE);

    }
    private void setAddAddressClickListener() {
        fragmentRootView.findViewById(R.id.add_new_address).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), AddressBookAddAddressActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.right_in, R.anim.left_out);
                getActivity().finish();
            }
        });
    }

    public String getUserId(){
        SharedPreferences settings = getActivity().getSharedPreferences(UrlsRetail.SAVED_USER_DETAILS, getActivity().MODE_PRIVATE);
        // Reading from SharedPreferences
        return settings.getString(UrlsRetail.SAVED_USER_ID,"");
    }


    private void fetchAddresses() {
        displayAddressesList  = new ArrayList<Address>();
        fetchAddressFromStorage();
    }

    public void fetchAddressFromStorage(){
        fetchAddressesFromRemoteServer(UrlsRetail.USER_ADDRESS + "/" + getUserId());
    }

    public String getUserToken(){
        SharedPreferences settings = getActivity().getSharedPreferences(UrlsRetail.SAVED_USER_DETAILS, getActivity().MODE_PRIVATE);
        // Reading from SharedPreferences
        return settings.getString(UrlsRetail.SAVED_USER_TOKEN,"");
    }


    private void fetchAddressesFromRemoteServer(String urls) {
        JsonObjectRequest jsonObjectRequest = null;
        try {
            jsonObjectRequest = getAddressJsonRequest(urls);
            jsonObjectRequest.setTag(UrlsRetail.USER_ADDRESS_VOLLEY_REQUEST_TAG);
            VolleySingleton.getInstance(getActivity()).addToRequestQueue(jsonObjectRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private JsonObjectRequest getAddressJsonRequest(String url) throws JSONException {


        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url, getAddressResponseListener(), new Response.ErrorListener() {
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
    private Response.Listener<JSONObject> getAddressResponseListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject fetchedJson = new JSONObject(response.toString());
                    createAddressList(fetchedJson, R.id.address_book_items_recycler_view);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
    }





    private void createAddressList(JSONObject fetchedJson,int recyclerViewResource) throws JSONException {

        JSONArray List = fetchedJson.getJSONArray("data");

        if(List.length()==0){
            showNoAddressTextAndHideRecylerView();
        }else {

            Gson gson = new Gson();
            for (int i = 0; i < List.length(); i++) {
                JSONObject jsonObject = List.getJSONObject(i);
                Address address = gson.fromJson(jsonObject.toString(), Address.class);

                displayAddressesList.add(address);
            }
            setRecycleListView(recyclerViewResource);
            hideLoadingScreen();
        }

    }

    private void setRecycleListView(int recyclerViewResource) {
        addressBookRecyclerView = (RecyclerView) fragmentRootView.findViewById(recyclerViewResource);
        addressListAdapter = new AddressBookListAdapter(getActivity(), displayAddressesList);
        addressBookRecyclerView.setAdapter(addressListAdapter);
        addressListAdapter.setClickListener(new ClickListener() {
            @Override
            public void itemClicked(View view, int position) {




            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        addressBookRecyclerView.setLayoutManager(linearLayoutManager);

    }



}
