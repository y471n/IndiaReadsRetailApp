package com.indiareads.retailapp.corporate.view.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.indiareads.retailapp.R;
import com.indiareads.retailapp.adapters.AddressListAdapter;
import com.indiareads.retailapp.corporate.view.activity.PickupDetailsActivityCorporate;
import com.indiareads.retailapp.corporate.view.activity.ReturnBookAddAddressActivityCorporate;
import com.indiareads.retailapp.interfaces.ClickListener;
import com.indiareads.retailapp.models.Address;
import com.indiareads.retailapp.singleton.VolleySingleton;
import com.indiareads.retailapp.utils.CommonMethods;
import com.indiareads.retailapp.utils.DefaultMaxRetries;
import com.indiareads.retailapp.utils.UrlsCorporate;
import com.indiareads.retailapp.utils.UrlsRetail;
import com.indiareads.retailapp.views.activities.PickupDetailsActivity;
import com.indiareads.retailapp.views.activities.ReturnBookAddAddressActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class ChoosePickUpAddressFragmentCorporate extends Fragment {

    View fragmentRootView;
    private RecyclerView addressRecyclerView;
    private AddressListAdapter addressListAdapter;
    ArrayList<Address> displayAddressesList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentRootView= inflater.inflate(R.layout.fragment_choose_pick_up_address_corporate, container, false);

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
//        fragmentRootView.findViewById(R.id.choose_pickup_Address_complete_layout).setVisibility(View.GONE);
    }

    private void executeOtherMethods() {
        showLoadingScreen();
        setAddAddressClickListener();
        setConfirmClickListener();
        fetchAddresses();
    }

    public void showLoadingScreen(){
        fragmentRootView.findViewById(R.id.pick_up_address_loading_screen).setVisibility(View.VISIBLE);
        fragmentRootView.findViewById(R.id.pick_up_address_content).setVisibility(View.GONE);
    }
    public void hideLoadingScreen(){
        fragmentRootView.findViewById(R.id.pick_up_address_loading_screen).setVisibility(View.GONE);
        fragmentRootView.findViewById(R.id.pick_up_address_content).setVisibility(View.VISIBLE);
    }

    private void showNoAddressTextAndHideRecylerView() {
        fragmentRootView.findViewById(R.id.pick_up_address_loading_screen).setVisibility(View.GONE);
        fragmentRootView.findViewById(R.id.pick_up_address_content).setVisibility(View.VISIBLE);
        fragmentRootView.findViewById(R.id.no_address_in_list_text).setVisibility(View.VISIBLE);
        fragmentRootView.findViewById(R.id.address_items_recycler_view).setVisibility(View.GONE);

    }

    private void setAddAddressClickListener() {
        fragmentRootView.findViewById(R.id.add_new_address).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ReturnBookAddAddressActivityCorporate.class);
                intent.putExtra("list",String.valueOf(getActivity().getIntent().getExtras().get("list")));
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.right_in, R.anim.left_out);
                getActivity().finish();
            }
        });
    }


    private void setConfirmClickListener() {
        fragmentRootView.findViewById(R.id.proceed_to_pickup_Details).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPickUpDetailsActivity();
            }
        });
    }

    public Address getSelectedAddress(){

        Address address=null;

        for(int i=0;i<displayAddressesList.size();i++){
            Address addressItem = displayAddressesList.get(i);
            if(addressItem.isSelected()){
                address=addressItem;
            }
        }

        return  address;
    }

    public void startPickUpDetailsActivity(){
        Intent intent;
        intent = new Intent(getActivity(), PickupDetailsActivityCorporate.class);
        intent.putExtra("list",String.valueOf(getActivity().getIntent().getExtras().get("list")));
        intent.putExtra("address", getSelectedAddress());
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.right_in, R.anim.left_out);
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
        fetchAddressesFromRemoteServer(UrlsCorporate.USER_ADDRESS);
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
            jsonObjectRequest.setTag(UrlsCorporate.USER_ADDRESS_VOLLEY_REQUEST_TAG);
            VolleySingleton.getInstance(getActivity()).addToRequestQueue(jsonObjectRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private JsonObjectRequest getAddressJsonRequest(String url) throws JSONException {
        JSONObject obj=new JSONObject();
        obj.put("user_id",getUserId());


        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url,obj, getAddressResponseListener(), new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error is",error.toString());
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
                    createAddressList(fetchedJson, R.id.address_items_recycler_view);

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
            disableProceedButton();
        }else {

            Gson gson = new Gson();
            for (int i = 0; i < List.length(); i++) {
                JSONObject jsonObject = List.getJSONObject(i);
                Address address = gson.fromJson(jsonObject.toString(), Address.class);

                displayAddressesList.add(address);
            }
            if (List.length() > 0) {
                setFirstAddressSelected();
            }
            setRecycleListView(recyclerViewResource);
            hideLoadingScreen();
        }

    }

    public void setFirstAddressSelected(){
        Address addressItem = displayAddressesList.get(0);
        addressItem.setIsSelected(true);
    }


    private void setRecycleListView(int recyclerViewResource) {
        addressRecyclerView = (RecyclerView) fragmentRootView.findViewById(recyclerViewResource);
        addressListAdapter = new AddressListAdapter(getActivity(), displayAddressesList);
        addressRecyclerView.setAdapter(addressListAdapter);
        addressListAdapter.setClickListener(new ClickListener() {
            @Override
            public void itemClicked(View view, int position) {
                makeAllAddressUnSelect();
                makeSelectedItemChecked(position);
                addressListAdapter.notifyDataSetChanged();
                //  Log.e("position ", position + "");

            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        addressRecyclerView.setLayoutManager(linearLayoutManager);

    }

    public void makeAllAddressUnSelect(){
        for(int i=0;i<addressListAdapter.addressList.size();i++){
            Address addressItem = addressListAdapter.addressList.get(i);
            addressItem.setIsSelected(false);
        }
    }

    public void makeSelectedItemChecked(int position){

        Address addressItem = addressListAdapter.addressList.get(position);
        addressItem.setIsSelected(true);

    }

    private void disableProceedButton() {
        Button b=(Button)fragmentRootView.findViewById(R.id.proceed_to_pickup_Details);
        b.setBackgroundResource(R.drawable.full_screen_width_disable_button_background);
        b.setHintTextColor(getResources().getColor(R.color.black));
        b.setEnabled(false);
        b.setClickable(false);
    }

}
