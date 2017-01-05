package com.indiareads.retailapp.corporate.view.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.indiareads.retailapp.R;
import com.indiareads.retailapp.corporate.view.activity.AddressBookCorporate;
import com.indiareads.retailapp.corporate.view.activity.ProfilePageActivityCorporatte;
import com.indiareads.retailapp.utils.UrlsRetail;
import com.indiareads.retailapp.views.activities.AddressBook;
import com.indiareads.retailapp.views.activities.MainLoginActivity;
import com.indiareads.retailapp.views.activities.ProfilePageActivity;


public class MyAccountFragmentCorporate extends Fragment {

    View fragmentRootView;

    LinearLayout myAddressesLinearLayout,myProfileInfoLinearLayout,logoutLinearLayout;
    private int email;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentRootView= inflater.inflate(R.layout.fragment_my_account_fragment_corporate, container, false);

        fetchViewsFromXml();
        setUserEmail();

        setClickListeners();
        return fragmentRootView;
    }

    private void setUserEmail() {
        ((TextView)fragmentRootView.findViewById(R.id.profile_username_email)).setText(getEmail());
    }


    public void fetchViewsFromXml(){

        myAddressesLinearLayout=(LinearLayout)fragmentRootView.findViewById(R.id.my_addresses_linear_layout);
        myProfileInfoLinearLayout=(LinearLayout)fragmentRootView.findViewById(R.id.my_profile_linear_layout);
        logoutLinearLayout=(LinearLayout)fragmentRootView.findViewById(R.id.logout_linear_layout);

    }

    public void setClickListeners(){

        setMyProfileInfoLinearLayoutClickListener();
        setLogoutLinearLayoutClickListener();
        setMyAddressesLinearLayoutClickListener();
    }

    private void setMyProfileInfoLinearLayoutClickListener() {
        myProfileInfoLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), ProfilePageActivityCorporatte.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.right_in, R.anim.left_out);
                getActivity().finish();
            }
        });
    }

    private void setMyAddressesLinearLayoutClickListener() {
        myAddressesLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), AddressBookCorporate.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.right_in, R.anim.left_out);
            }
        });
    }

    private void setLogoutLinearLayoutClickListener() {
        logoutLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                deleteSharedPreferences();
                startLoginActivity();
            }
        });
    }

    private void startLoginActivity() {
        Intent intent = new Intent(getActivity(), MainLoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("EXIT", true);
        startActivity(intent);
    }


    private void deleteSharedPreferences() {
        SharedPreferences preferences = getActivity().getSharedPreferences(UrlsRetail.SAVED_USER_DETAILS, getActivity().MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
    }

    public String getEmail() {
        SharedPreferences settings = getActivity().getSharedPreferences(UrlsRetail.SAVED_USER_DETAILS, getActivity().MODE_PRIVATE);
        // Reading from SharedPreferences
        return settings.getString(UrlsRetail.SAVED_EMAIL_ID,"");
    }
}
