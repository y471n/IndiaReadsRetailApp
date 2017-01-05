package com.indiareads.retailapp.corporate.view.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.indiareads.retailapp.R;
import com.indiareads.retailapp.corporate.view.activity.MainActivityCorporate;
import com.indiareads.retailapp.models.Address;
import com.indiareads.retailapp.models.Bookshelf;
import com.indiareads.retailapp.views.activities.MainActivity;

import java.lang.reflect.Type;
import java.util.List;


public class ReturnSummaryFragmentCorporate extends Fragment {


    View fragmentRootView;

    List<Bookshelf> bookList;
    Address deliveryAddress;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentRootView= inflater.inflate(R.layout.fragment_return_summary_corporate, container, false);


        setContinueShoppingClickListener();

        return fragmentRootView;
    }




    private void setContinueShoppingClickListener() {
        fragmentRootView.findViewById(R.id.return_summary_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startMainActivity();
            }
        });
    }

    private void startMainActivity() {
        Intent intent = new Intent(getActivity(), MainActivityCorporate.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

}
