package com.indiareads.retailapp.corporate.view.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.indiareads.retailapp.R;
import com.indiareads.retailapp.corporate.adapter.MyCartBooksListAdapterCorporate;
import com.indiareads.retailapp.corporate.view.activity.ChooseDeliveryAddressActivityCorporate;
import com.indiareads.retailapp.corporate.view.activity.MainActivityCorporate;
import com.indiareads.retailapp.corporate.view.activity.ProductPageActivityCorp;
import com.indiareads.retailapp.interfaces.MyCartClickListener;
import com.indiareads.retailapp.corporate.models.CartModelCorporate;
import com.indiareads.retailapp.utils.IndiaReadsApplication;
import com.indiareads.retailapp.utils.UrlsRetail;
import com.indiareads.retailapp.views.activities.ChooseDeliveryAddressActivity;
import com.indiareads.retailapp.views.activities.ProductPageActivity;

import java.util.List;


public class MyCartFragmentCorporate extends Fragment {


    View fragmentRootView;
    private RecyclerView bookRecyclerView;
    List<CartModelCorporate> cartBookList;

    private MyCartBooksListAdapterCorporate myCartBooksListAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentRootView= inflater.inflate(R.layout.fragment_my_cart_corporate, container, false);

        showLoadingScreen();
        fetchBooksFromDatbase();
        setRentNowClickListener();
        setContinueBrowsingClickListener();
        setMoveToHomeButtonClickListener();

        return fragmentRootView;
    }

    public void setBookObjectInUserCompleteOrderObject(){
        ((IndiaReadsApplication) getActivity().getApplication()).initializeUserCompleteOrderObjectCorporate();
        ((IndiaReadsApplication) getActivity().getApplication()).getUserCompleteOrderObjectCorporate()
                .setUserBook(cartBookList);
    }


    private void setRentNowClickListener() {
        fragmentRootView.findViewById(R.id.checkout_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkTotalBookUserCanOrder()) {
                    setBookObjectInUserCompleteOrderObject();
                    startChooseDeliveryAddressActivity();
                }

            }
        });
    }

    public boolean checkTotalBookUserCanOrder(){
        boolean value=false;
       try{
            if(cartBookList.size()<=Integer.parseInt(getTotalBooks())){
                value=true;
            }else{
                Toast.makeText(getActivity(),"You are allowed to order only "+getTotalBooks()+" Books",Toast.LENGTH_LONG).show();
            }
       }catch (Exception e){
           Toast.makeText(getActivity(),"Opps something went wrong",Toast.LENGTH_LONG).show();
       }
              return value;
    }

    public String getTotalBooks(){
        SharedPreferences settings = getActivity().getSharedPreferences(UrlsRetail.SAVED_USER_DETAILS, getActivity().MODE_PRIVATE);
        // Reading from SharedPreferences
        return settings.getString(UrlsRetail.SAVED_NUMBER_OF_BOOKS,"");
    }


    public void startChooseDeliveryAddressActivity(){
        Intent intent = new Intent(getActivity(), ChooseDeliveryAddressActivityCorporate.class);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    private void setMoveToHomeButtonClickListener() {
        fragmentRootView.findViewById(R.id.cart_to_home_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startMainActivity();

            }
        });
    }


    private void setContinueBrowsingClickListener() {
        fragmentRootView.findViewById(R.id.continue_browsing).setOnClickListener(new View.OnClickListener() {
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
        getActivity().finish();
    }


    public void showLoadingScreen(){
        fragmentRootView.findViewById(R.id.cart_loading_screen).setVisibility(View.VISIBLE);
        fragmentRootView.findViewById(R.id.cart_content).setVisibility(View.GONE);
    }

    public void fetchBooksFromDatbase() {
        Select select = new Select();

        cartBookList = select.all().from(CartModelCorporate.class).execute();

        if (cartBookList.size()>0) {
            setRecycleListView();
            hideNoBookInCartTextView();
            sendAnalaytics();
        }else{
            showNoBookInCartTextView();
            sendEmptyAnalaytics();
        }
    }
    private void sendEmptyAnalaytics() {
        IndiaReadsApplication application = (IndiaReadsApplication) getActivity().getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName("cart page corporate");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }


    public void showNoBookInCartTextView(){
        fragmentRootView.findViewById(R.id.cart_content).setVisibility(View.GONE);
        fragmentRootView.findViewById(R.id.cart_loading_screen).setVisibility(View.GONE);
        fragmentRootView.findViewById(R.id.no_book_in_cart_text_view).setVisibility(View.VISIBLE);
    }


    private void sendAnalaytics() {
        StringBuilder sb =new StringBuilder();
        sb.append("Cart Page corporate ");
        for(int i=0;i<cartBookList.size();i++){
            sb.append(cartBookList.get(i).getIsbn13());
            sb.append(" ");
        }
        IndiaReadsApplication application = (IndiaReadsApplication) getActivity().getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName(sb.toString());
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }


    public void hideNoBookInCartTextView(){
        fragmentRootView.findViewById(R.id.cart_content).setVisibility(View.VISIBLE);
        fragmentRootView.findViewById(R.id.cart_loading_screen).setVisibility(View.GONE);
        fragmentRootView.findViewById(R.id.no_book_in_cart_text_view).setVisibility(View.GONE);
    }
    private void deleteItemFromRecylerView(int position) {
        cartBookList.remove(position);
        myCartBooksListAdapter.notifyDataSetChanged();
    }

    private void deleteItemFromDataBase(int position) {
        new Delete().from(CartModelCorporate.class).where("isbn13 = ?", cartBookList.get(position).getIsbn13()).execute();
    }

    public void checkItemsLeftZero() {

        if(cartBookList.size()==0) {
            showNoBookInCartTextView();
        }
    }
    private void setRecycleListView() {
        bookRecyclerView = (RecyclerView) fragmentRootView.findViewById(R.id.my_cart_books_recycler_view);
        myCartBooksListAdapter = new MyCartBooksListAdapterCorporate(getActivity(), cartBookList);
        bookRecyclerView.setAdapter(myCartBooksListAdapter);
        myCartBooksListAdapter.setClickListener(new MyCartClickListener() {
            @Override
            public void onDelete(View view, int position) {
                deleteItemFromDataBase(position);
                deleteItemFromRecylerView(position);
                checkItemsLeftZero();
            }
            @Override
            public void itemClicked(View view, int position) {

                Intent intent = new Intent(getActivity(), ProductPageActivityCorp.class);
                intent.putExtra("isbn_num",cartBookList.get(position).getIsbn13());
                startActivity(intent);

            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        bookRecyclerView.setLayoutManager(linearLayoutManager);

    }


}
