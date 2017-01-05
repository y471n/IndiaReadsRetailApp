package com.indiareads.retailapp.views.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.indiareads.retailapp.R;
import com.indiareads.retailapp.adapters.MyCartBooksListAdapter;
import com.indiareads.retailapp.interfaces.MyCartClickListener;
import com.indiareads.retailapp.models.Book;
import com.indiareads.retailapp.models.CartModel;
import com.indiareads.retailapp.models.Rent;
import com.indiareads.retailapp.utils.CommonMethods;
import com.indiareads.retailapp.utils.IndiaReadsApplication;
import com.indiareads.retailapp.views.activities.ChooseDeliveryAddressActivity;
import com.indiareads.retailapp.views.activities.LoginActivity;
import com.indiareads.retailapp.views.activities.MainActivity;
import com.indiareads.retailapp.views.activities.ProductPageActivity;

import java.util.ArrayList;

import java.util.List;


public class MyCartFragment extends Fragment {

    View fragmentRootView;
    private RecyclerView bookRecyclerView;
//    List<CartModel> bookList;
    List<CartModel> cartBookList;



    private MyCartBooksListAdapter myCartBooksListAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        fragmentRootView= inflater.inflate(R.layout.fragment_my_cart, container, false);


        showLoadingScreen();
        fetchBooksFromDatbase();
        setRentNowClickListener();
        setContinueBrowsingClickListener();
        setMoveToHomeButtonClickListener();


        return fragmentRootView;
    }

    public void showLoadingScreen(){
        fragmentRootView.findViewById(R.id.cart_loading_screen).setVisibility(View.VISIBLE);
        fragmentRootView.findViewById(R.id.cart_content).setVisibility(View.GONE);
    }
    public void hideLoadingScreen(){
        fragmentRootView.findViewById(R.id.cart_loading_screen).setVisibility(View.GONE);
        fragmentRootView.findViewById(R.id.cart_content).setVisibility(View.VISIBLE);
    }

    public void showNoBookInCartTextView(){
        fragmentRootView.findViewById(R.id.cart_content).setVisibility(View.GONE);
        fragmentRootView.findViewById(R.id.cart_loading_screen).setVisibility(View.GONE);
        fragmentRootView.findViewById(R.id.no_book_in_cart_text_view).setVisibility(View.VISIBLE);
    }

    public void hideNoBookInCartTextView(){
        fragmentRootView.findViewById(R.id.cart_content).setVisibility(View.VISIBLE);
        fragmentRootView.findViewById(R.id.cart_loading_screen).setVisibility(View.GONE);
        fragmentRootView.findViewById(R.id.no_book_in_cart_text_view).setVisibility(View.GONE);
    }


    private void setRentNowClickListener() {
        fragmentRootView.findViewById(R.id.checkout_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBookObjectInUserCompleteOrderObject();
                if (CommonMethods.isUserLogedIn(getActivity())) {
                    startChooseDeliveryAddressActivity();
                } else {
                    startLoginActivity();
                }

            }
        });
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
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        getActivity().finish();
    }


    private void startLoginActivity() {
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    public void startChooseDeliveryAddressActivity(){
        Intent intent = new Intent(getActivity(), ChooseDeliveryAddressActivity.class);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    public void setBookObjectInUserCompleteOrderObject(){
        ((IndiaReadsApplication) getActivity().getApplication()).initializeUserCompleteOrderObject();
        ((IndiaReadsApplication) getActivity().getApplication()).getUserCompleteOrderObject().setUserBook(cartBookList);
    }



    private void setRecycleListView() {
        bookRecyclerView = (RecyclerView) fragmentRootView.findViewById(R.id.my_cart_books_recycler_view);
        myCartBooksListAdapter = new MyCartBooksListAdapter(getActivity(), cartBookList);
        bookRecyclerView.setAdapter(myCartBooksListAdapter);
        myCartBooksListAdapter.setClickListener(new MyCartClickListener() {
            @Override
            public void onDelete(View view, int position) {
                deleteItemFromDataBase(position);
                deleteItemFromRecylerView(position);
                checkItemsLeftZero();
                setRemainingDetails();
            }
            @Override
            public void itemClicked(View view, int position) {

                Intent intent = new Intent(getActivity(), ProductPageActivity.class);
                intent.putExtra("isbn_num",cartBookList.get(position).getIsbn13());
//                intent.putExtra("isbn_num",booksList.get(position).getIsbn13());
                startActivity(intent);


            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        bookRecyclerView.setLayoutManager(linearLayoutManager);

    }

    private void deleteItemFromRecylerView(int position) {
        cartBookList.remove(position);
        myCartBooksListAdapter.notifyDataSetChanged();
    }

    private void deleteItemFromDataBase(int position) {
        new Delete().from(CartModel.class).where("isbn13 = ?", cartBookList.get(position).getIsbn13()).execute();
    }

    public void fetchBooksFromDatbase() {
        Select select = new Select();

        // Call select.all() to select all rows from our table which is
        // represented by Book.class and execute the query.

        // It returns an ArrayList of our Book objects where each object
        // contains data corresponding to a row of our database.
        
        cartBookList = select.all().from(CartModel.class).execute();

        if (cartBookList.size()>0) {
           // createBookList(cartBookList);
             setRecycleListView();
            setRemainingDetails();
            hideNoBookInCartTextView();
            sendAnalaytics();
           // hideLoadingScreen();

        }else{
            showNoBookInCartTextView();
            sendEmptyAnalaytics();
        }



    }
    private void sendEmptyAnalaytics() {
        IndiaReadsApplication application = (IndiaReadsApplication) getActivity().getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName("cart page");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    private void sendAnalaytics() {
        StringBuilder sb =new StringBuilder();
        sb.append("Cart Page ");
        for(int i=0;i<cartBookList.size();i++){
            sb.append(cartBookList.get(i).getIsbn13());
            sb.append(" ");
        }
        IndiaReadsApplication application = (IndiaReadsApplication) getActivity().getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName(sb.toString());
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    private void setRemainingDetails() {
        ((TextView)fragmentRootView.findViewById(R.id.total_cost)).setText(String.valueOf(getTotalCost()));
        ((TextView)fragmentRootView.findViewById(R.id.shipping_charges)).setText(String.valueOf(getShippingCharges()));
        ((TextView)fragmentRootView.findViewById(R.id.total_payable)).setText(String.valueOf(getTotalPayable()));
    }

    public long getTotalCost() {
        long totalCost=0;
        for(int i=0;i<cartBookList.size();i++){
            CartModel cart=cartBookList.get(i);
            if(cart.getToBuy().equals("1")){
                long buyFinalPrice= Long.parseLong(cart.getBuy_mrp()) -Long.parseLong(cart.getBuy_discount());
                totalCost+=buyFinalPrice;
            }else {
                totalCost += Integer.parseInt(cart.getInitialPayable());
            }
        }

        return totalCost;
    }

    public int getShippingCharges() {
       if(getTotalCost()>350){
           return 0;
       }else{
           return 50;
       }
    }

    public long getTotalPayable() {
        return getTotalCost()+getShippingCharges();
    }

//    private void createBookList(List<CartModel> cartBookList) {
//        bookList=new ArrayList<>();
//        CartModel cartBook;
//        for(int i=0;i<cartBookList.size();i++){
//            cartBook=cartBookList.get(i);
//            Book book=new Book();
//            book.setTitle(cartBook.getTitle());
//            book.setAuthor1(cartBook.getAuthor1());
//            book.setIsbn13(cartBook.getIsbn13());
//            book.setRent(getRentObject(cartBook));
//
//            bookList.add(book);
//
//        }
//
//    }

    public Rent getRentObject(CartModel cartBook){
        Rent rent=new Rent();
        rent.setMrp(cartBook.getMrp());
        rent.setInitialPayable(cartBook.getInitialPayable());
        rent.setBookId(cartBook.getBookID());
        rent.setMerchantLibrary(cartBook.getMerchantLibrary());
        rent.setBookLibrary(cartBook.getBookLibrary());
        return rent;
    }


    public void checkItemsLeftZero() {

        if(cartBookList.size()==0) {
            showNoBookInCartTextView();
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        refershCart();
    }

    private void refershCart() {
        fetchBooksFromDatbase();

    }

}
