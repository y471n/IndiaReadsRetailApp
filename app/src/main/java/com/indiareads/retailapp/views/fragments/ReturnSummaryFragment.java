package com.indiareads.retailapp.views.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.indiareads.retailapp.R;
import com.indiareads.retailapp.models.Address;
import com.indiareads.retailapp.models.Bookshelf;
import com.indiareads.retailapp.views.activities.MainActivity;

import java.lang.reflect.Type;
import java.util.List;

public class ReturnSummaryFragment extends Fragment {

    View fragmentRootView;

    List<Bookshelf> bookList;
    Address deliveryAddress;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        fragmentRootView= inflater.inflate(R.layout.fragment_return_summary, container, false);


        fetchBookListAndAddressFromIntent();
        setData();
        setContinueShoppingClickListener();

        return fragmentRootView;
    }


    private void fetchBookListAndAddressFromIntent() {
        String json=(String)getActivity().getIntent().getExtras().get("list");
        deliveryAddress=(Address)getActivity().getIntent().getExtras().get("address");
        Type listType = new TypeToken<List<Bookshelf>>() {}.getType();
        bookList=new Gson().fromJson(json,listType);
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
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void setData() {
        fillAddressDetails();
        fillBookDetails();
        fillTotalRefund();
    }

    public void fillTotalRefund(){
        int totalRefundAmount=0;
        for(int i=0;i<bookList.size();i++){
            totalRefundAmount+=Integer.parseInt(bookList.get(i).getRefund_amount());
        }
        ((TextView)fragmentRootView.findViewById(R.id.total_refund)).setText(totalRefundAmount+"");
    }

    public void fillBookDetails(){

        for(int i=0;i<bookList.size();i++) {
            View bookView = getActivity().getLayoutInflater().inflate(R.layout.display_book_linear_layout, null);
            TextView bookName = (TextView) bookView.findViewById(R.id.book_name);
            TextView bookCost = (TextView) bookView.findViewById(R.id.book_cost);
            bookName.setText(bookList.get(i).getTitle());
            bookCost.setText(bookList.get(i).getRefund_amount());

            ((LinearLayout) fragmentRootView.findViewById(R.id.linear_layout_to_add_return_books_dynamically)).addView(bookView);
        }

    }

    public void fillAddressDetails(){

        Address address=deliveryAddress;
        ((TextView)fragmentRootView.findViewById(R.id.name)).setText(address.getFullname());
        ((TextView)fragmentRootView.findViewById(R.id.address_line_1)).setText(address.getAddress_line1());
        ((TextView)fragmentRootView.findViewById(R.id.address_line_2)).setText(address.getAddress_line2());
        ((TextView)fragmentRootView.findViewById(R.id.pincode)).setText(address.getPincode());
        ((TextView)fragmentRootView.findViewById(R.id.phone_number)).setText(address.getPhone());
        ((TextView)fragmentRootView.findViewById(R.id.city)).setText(address.getCity());
        ((TextView)fragmentRootView.findViewById(R.id.state)).setText(address.getState());
    }

}
