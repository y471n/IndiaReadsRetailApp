package com.indiareads.retailapp.views.fragments;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.query.Select;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.indiareads.retailapp.R;
import com.indiareads.retailapp.adapters.AvailableBookshelfListAdapter;
import com.indiareads.retailapp.interfaces.AvailableBookshelfClickListener;
import com.indiareads.retailapp.models.Bookshelf;
import com.indiareads.retailapp.models.CartModel;
import com.indiareads.retailapp.singleton.VolleySingleton;
import com.indiareads.retailapp.utils.DefaultMaxRetries;
import com.indiareads.retailapp.utils.UrlsRetail;
import com.indiareads.retailapp.views.activities.MainActivity;
import com.indiareads.retailapp.views.activities.MyCartActivity;
import com.indiareads.retailapp.views.activities.ProductPageActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class AvailableBookshelfFragment extends android.support.v4.app.Fragment  {


    private ArrayList<Bookshelf> bookList;
    private View fragmentRootView;
    private RecyclerView mRecyclerView;
    private AvailableBookshelfListAdapter bookListAdapter;

    LinearLayout availableBookDeletePopUp;

    ProgressDialog PD;

    List<Integer> selectedItems=new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



        fragmentRootView=inflater.inflate(R.layout.fragment_available_bookshelf, container, false);

        showLoadingScreen();
        fetchBooks();
        setCheckOutClickListener();
//       detePopUpTask();
        setRetryButonClickListener();
        setGoToHomeButtonClickListener();

        return fragmentRootView;

    }

    private void setRetryButonClickListener() {
        fragmentRootView.findViewById(R.id.available_books_retry_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentRootView.findViewById(R.id.available_msg_linear_layout).setVisibility(View.GONE);
                fragmentRootView.findViewById(R.id.available_books_complete_layout).setVisibility(View.VISIBLE);
                showLoadingScreen();
                fetchBooks();
            }
        });
    }

    private void setGoToHomeButtonClickListener() {
        fragmentRootView.findViewById(R.id.to_home_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                getActivity().finish();
            }
        });
    }


    public void showLoadingScreen(){
        fragmentRootView.findViewById(R.id.available_books_loading_screen).setVisibility(View.VISIBLE);
        fragmentRootView.findViewById(R.id.available_books_content).setVisibility(View.GONE);
    }
    public void hideLoadingScreen(){
        fragmentRootView.findViewById(R.id.available_books_loading_screen).setVisibility(View.GONE);
        fragmentRootView.findViewById(R.id.available_books_content).setVisibility(View.VISIBLE);
    }

//    public  void deletePopUpTask(){
//        availableBookDeletePopUp= (LinearLayout)fragmentRootView.findViewById(R.id.delete_pop_up_linear_layout);
//        availableBookDeletePopUp.setVisibility(View.GONE);
//        setUndoButtonClickListener();
//        setCrossButtonClickListener();
//    }


//    private void setUndoButtonClickListener() {
//        fragmentRootView.findViewById(R.id.undo_button).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                availableBookDeletePopUp.setVisibility(View.GONE);
//                Toast.makeText(getActivity(), "Undo", Toast.LENGTH_LONG).show();
//            }
//        });
//    }
//
//
//    private void setCrossButtonClickListener() {
//        fragmentRootView.findViewById(R.id.cross_button_hide_pop_up).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                availableBookDeletePopUp.setVisibility(View.GONE);
//                //deleteBook();
//                Toast.makeText(getActivity(), "Deleted", Toast.LENGTH_LONG).show();
//
//            }
//        });
//    }


    private void setCheckOutClickListener() {
        fragmentRootView.findViewById(R.id.checkout_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            for(int i=0;i<bookList.size();i++){
              if(bookList.get(i).isSelected()) {
                  selectedItems.add(i);
              }
            }
                if(selectedItems.size()>0) {
                    saveSelectedBookToDataBase();
                    Intent intent = new Intent(getActivity(), MyCartActivity.class);
                    startActivity(intent);
                }

            }


        });
    }

    private void saveSelectedBookToDataBase() {

        for(int i=0;i<selectedItems.size();i++) {
            Bookshelf item=bookList.get(selectedItems.get(i));
            CartModel existingBook =
                    new Select().from(CartModel.class).where("isbn13 = ?", item.getIsbn13())
                            .where("toBuy = ?", "0")
                            .executeSingle();
            if (existingBook == null) {
               CartModel cartItem = new CartModel(item.getTitle(), item.getContributor_name1(), item.getIsbn13(), item.getMrp(), item.getInit_pay(), item.getBookID(), null, null, "0", null, null);
                cartItem.save();
            }
        }
    }

    private void fetchBooks() {
        bookList = new ArrayList<>();
        fetchBookFromStorage();
    }

    public void fetchBookFromStorage(){
        fetchBooksFromRemoteServer(UrlsRetail.BOOKSHELF_AVAILABLE_BOOKS);
    }

    public String getUserToken(){
        SharedPreferences settings = getActivity().getSharedPreferences(UrlsRetail.SAVED_USER_DETAILS, getActivity().MODE_PRIVATE);
        // Reading from SharedPreferences
        return settings.getString(UrlsRetail.SAVED_USER_TOKEN,"");
    }


    private void fetchBooksFromRemoteServer(String urls) {
        JsonObjectRequest jsonObjectRequest;
        try {
            jsonObjectRequest = getAvailableBooksJsonRequest(urls);
            jsonObjectRequest.setTag(UrlsRetail.BOOKSHELF_AVAILABLE_BOOKS_VOLLEY_REQUEST_TAG);
            VolleySingleton.getInstance(getActivity()).addToRequestQueue(jsonObjectRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private JsonObjectRequest getAvailableBooksJsonRequest(String url) throws JSONException {

        JSONObject obj=new JSONObject();
        obj.put("user_id",getUserId());

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url,obj, getAvailableBooksResponseListener(), new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error instanceof NoConnectionError) {
                    fragmentRootView.findViewById(R.id.available_msg_linear_layout).setVisibility(View.VISIBLE);
                    fragmentRootView.findViewById(R.id.available_books_complete_layout).setVisibility(View.GONE);
                    ((TextView)fragmentRootView.findViewById(R.id.available_books_msg)).setText(R.string.no_internet_connection_msg);
                    ((Button)fragmentRootView.findViewById(R.id.to_home_button)).setVisibility(View.GONE);
                }else{
                    fragmentRootView.findViewById(R.id.available_msg_linear_layout).setVisibility(View.VISIBLE);
                    fragmentRootView.findViewById(R.id.available_books_complete_layout).setVisibility(View.GONE);
                    ((TextView)fragmentRootView.findViewById(R.id.available_books_msg)).setText(R.string.error_in_fetching_available_books);
                    ((Button)fragmentRootView.findViewById(R.id.to_home_button)).setVisibility(View.GONE);
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
    private Response.Listener<JSONObject> getAvailableBooksResponseListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if(getActivity()!=null){
                    JSONObject fetchedJson = new JSONObject(response.toString());
                    createBooksList(fetchedJson, R.id.available_books_recycler_view);
                    hideLoadingScreen();
                }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
    }


    private void createBooksList(JSONObject fetchedJson,int recyclerViewResource) {

        try {
            JSONArray List = fetchedJson.getJSONArray("data");
            if(isListNotZero(List)) {
                Gson gson = new Gson();
                for (int i = 0; i < List.length(); i++) {
                    JSONObject jsonObject = List.getJSONObject(i);
                    Bookshelf book = gson.fromJson(jsonObject.toString(), Bookshelf.class);
                    book.setIsSelected(false);
                    bookList.add(book);
                }

                createRecycleListView(recyclerViewResource);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private boolean isListNotZero(JSONArray list) {

        if(list.length()==0){
            fragmentRootView.findViewById(R.id.available_msg_linear_layout).setVisibility(View.VISIBLE);
            fragmentRootView.findViewById(R.id.available_books_complete_layout).setVisibility(View.GONE);
            fragmentRootView.findViewById(R.id.available_books_retry_button).setVisibility(View.GONE);
            ((TextView)fragmentRootView.findViewById(R.id.available_books_msg)).setText(R.string.no_available_book_prsent);
            ((Button)fragmentRootView.findViewById(R.id.to_home_button)).setVisibility(View.VISIBLE);
            return false;
        }else{
            return true;
        }
    }


    private void createRecycleListView(int recyclerViewResource) {
        mRecyclerView = (RecyclerView) fragmentRootView.findViewById(recyclerViewResource);
        bookListAdapter = new AvailableBookshelfListAdapter(getActivity(), bookList);
        mRecyclerView.setAdapter(bookListAdapter);
        bookListAdapter.setClickListener(new AvailableBookshelfClickListener() {
            @Override
            public void itemClicked(View view, int position) {
                Intent i = new Intent(getActivity(), ProductPageActivity.class);
                i.putExtra("isbn_num",bookList.get(position).getIsbn13());
                startActivity(i);
            }

            @Override
            public void onDelete(View deleteView, int position) {
//                Log.e("isbn",bookList.get(position).getIsbn13());
//                fillDetailsOnPopUp(position);
//                availableBookDeletePopUp.setVisibility(View.VISIBLE);
                deleteBookLoadingDialog();
                deleteBook(position);
            }

            @Override
            public void addToCart(View addToCartView, int position) {


                if(bookList.get(position).isSelected()){
                    bookList.get(position).setIsSelected(false);
                  //  selectedItems.remove(selectedItems.indexOf(position));
                }else{
                    bookList.get(position).setIsSelected(true);
                  //  selectedItems.add(position);
                }
                bookListAdapter.notifyDataSetChanged();

                checkCheckOutButtonStatus();

//                ImageView addToCartImageView= (ImageView)addToCartView.findViewById(R.id.add_to_cart_image_view);
//                if(addToCartImageView.getTag()=="checked"){
//                    addToCartImageView.setBackgroundResource(R.drawable.unchecked);
//                    addToCartImageView.setTag("unchecked");
//                }else{
//                    addToCartImageView.setBackgroundResource(R.drawable.checked);
//                    addToCartImageView.setTag("checked");
//                }
            }


        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());

        mRecyclerView.setLayoutManager(linearLayoutManager);
    }


    public void deleteBookLoadingDialog(){
        PD = new ProgressDialog(getActivity());
        PD.setTitle(getResources().getString(R.string.progress_dialog_please_wait));
        PD.setMessage(getResources().getString(R.string.deleting_book));
        PD.setCancelable(false);
        PD.show();
    }

    private void checkCheckOutButtonStatus() {
        int count=0;
        for(int i=0;i<bookList.size();i++){
            if(bookList.get(i).isSelected()){
                count++;
            }
        }
        if(count>0){
            enableCheckOutButton();
        }else{
            disableCheckoutButton();
        }
    }

    public void enableCheckOutButton(){
        fragmentRootView.findViewById(R.id.checkout_button).setBackgroundColor(getResources().getColor(R.color.red));
    }

    public void disableCheckoutButton(){
        fragmentRootView.findViewById(R.id.checkout_button).setBackgroundColor(getResources().getColor(R.color.grey));
    }



    public void fillDetailsOnPopUp(int positions){
        if(bookList.get(positions).getTitle()==null) {
            ((TextView) availableBookDeletePopUp.findViewById(R.id.book_name)).setText("Dummy Title");
        }else{
            ((TextView) availableBookDeletePopUp.findViewById(R.id.book_name)).setText(bookList.get(positions).getTitle());
        }
    }

    public String getUserId(){
        SharedPreferences settings = getActivity().getSharedPreferences(UrlsRetail.SAVED_USER_DETAILS, getActivity().MODE_PRIVATE);
        // Reading from SharedPreferences
        return settings.getString(UrlsRetail.SAVED_USER_ID,"");
    }


    // Delete Book

    public void deleteBook(int position){
        deleteBookFromServer(position);
    }

    private void deleteBookFromRecyclerView(int pos) {
        bookList.remove(pos);
        bookListAdapter.notifyDataSetChanged();
    }

    public void deleteBookFromServer(int position){
        deleteBookFromRemoteServer(UrlsRetail.DELETE_AVAILABLE_BOOKS,position);
    }


    private void deleteBookFromRemoteServer(String urls,int pos) {
        JsonObjectRequest jsonObjectRequest;
        try {
            jsonObjectRequest = getDeleteBookJsonRequest(urls,pos);
            VolleySingleton.getInstance(getActivity()).addToRequestQueue(jsonObjectRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private JsonObjectRequest getDeleteBookJsonRequest(String url,int pos) throws JSONException {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("user_id", getUserId());
        jsonObject.put("isbn13", bookList.get(pos).getIsbn13());


        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url,jsonObject, getDeleteBookResponseListener(pos), new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(),"Unable To Delete Book",Toast.LENGTH_LONG).show();
                PD.dismiss();
            }
        });

        jsonRequest.setRetryPolicy(new DefaultRetryPolicy(
                20000,
                DefaultMaxRetries.APP_API_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        return jsonRequest;
    }


    @NonNull
    private Response.Listener<JSONObject> getDeleteBookResponseListener(final int pos) {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    PD.dismiss();
                    deleteBookFromRecyclerView(pos);
                    Toast.makeText(getActivity(),"Book Deleted",Toast.LENGTH_LONG).show();
                    isBookListZero();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }


    private boolean isBookListZero() {

        if(bookList.size()==0){
            fragmentRootView.findViewById(R.id.available_msg_linear_layout).setVisibility(View.VISIBLE);
            fragmentRootView.findViewById(R.id.available_books_complete_layout).setVisibility(View.GONE);
            fragmentRootView.findViewById(R.id.available_books_retry_button).setVisibility(View.GONE);
            ((TextView)fragmentRootView.findViewById(R.id.available_books_msg)).setText(R.string.no_available_book_prsent);
            return false;
        }else{
            return true;
        }
    }



}
