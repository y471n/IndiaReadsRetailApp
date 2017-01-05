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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.indiareads.retailapp.R;
import com.indiareads.retailapp.adapters.WishListBookshelfListAdapter;
import com.indiareads.retailapp.corporate.adapter.WishListBookshelfListAdapterCorporate;
import com.indiareads.retailapp.corporate.view.activity.ProductPageActivityCorp;
import com.indiareads.retailapp.interfaces.WishListBookshelfClickListener;
import com.indiareads.retailapp.models.Bookshelf;
import com.indiareads.retailapp.singleton.VolleySingleton;
import com.indiareads.retailapp.utils.DefaultMaxRetries;
import com.indiareads.retailapp.utils.UrlsCorporate;
import com.indiareads.retailapp.utils.UrlsRetail;
import com.indiareads.retailapp.views.activities.MainActivity;
import com.indiareads.retailapp.views.activities.ProductPageActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class WishListBookshelfFragmentCorporate  extends android.support.v4.app.Fragment  {


    private ArrayList<Bookshelf> bookList;
    private RecyclerView mRecyclerView;
    private View fragmentRootView;
    private WishListBookshelfListAdapterCorporate bookListAdapter;

    LinearLayout wishListDeletePopUp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentRootView= inflater.inflate(R.layout.fragment_wish_list_bookshelf_corporate, container, false);

        showLoadingScreen();
//        deletePopUpTask();
        fetchBooks();
        setRetryButonClickListener();
        setGoToHomeButtonClickListener();
        return fragmentRootView;
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

    private void setRetryButonClickListener() {
        fragmentRootView.findViewById(R.id.wishlist_books_retry_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentRootView.findViewById(R.id.wishlist_msg_linear_layout).setVisibility(View.GONE);
                fragmentRootView.findViewById(R.id.wishlist_books_complete_layout).setVisibility(View.VISIBLE);
                showLoadingScreen();
                fetchBooks();
            }
        });
    }

    public void showLoadingScreen(){
        fragmentRootView.findViewById(R.id.wishlist_books_loading_screen).setVisibility(View.VISIBLE);
        fragmentRootView.findViewById(R.id.wishlist_books_content).setVisibility(View.GONE);
    }
    public void hideLoadingScreen(){
        fragmentRootView.findViewById(R.id.wishlist_books_loading_screen).setVisibility(View.GONE);
        fragmentRootView.findViewById(R.id.wishlist_books_content).setVisibility(View.VISIBLE);
    }


    private void fetchBooks() {
        bookList = new ArrayList<>();
        fetchBookFromStorage();
    }

    public void fetchBookFromStorage(){
        fetchBooksFromRemoteServer(UrlsCorporate.BOOKSHELF_WISHLIST_BOOKS);
    }

    public String getUserToken(){
        SharedPreferences settings = getActivity().getSharedPreferences(UrlsRetail.SAVED_USER_DETAILS, getActivity().MODE_PRIVATE);
        // Reading from SharedPreferences
        return settings.getString(UrlsRetail.SAVED_USER_TOKEN,"");
    }



    private void fetchBooksFromRemoteServer(String urls) {
        JsonObjectRequest jsonObjectRequest = null;
        try {
            jsonObjectRequest = getWishlistBooksJsonRequest(urls);
            jsonObjectRequest.setTag(UrlsCorporate.BOOKSHELF_WISHLIST_BOOKS_VOLLEY_REQUEST_TAG);
            VolleySingleton.getInstance(getActivity()).addToRequestQueue(jsonObjectRequest);
        } catch (JSONException e) {
            // e.printStackTrace();
        }

    }


    private JsonObjectRequest getWishlistBooksJsonRequest(String url) throws JSONException {

        JSONObject obj=new JSONObject();
        obj.put("user_id",getUserId());

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url,obj, getWishlistBooksResponseListener(), new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error instanceof NoConnectionError) {
                    fragmentRootView.findViewById(R.id.wishlist_msg_linear_layout).setVisibility(View.VISIBLE);
                    fragmentRootView.findViewById(R.id.wishlist_books_complete_layout).setVisibility(View.GONE);
                    ((TextView)fragmentRootView.findViewById(R.id.wishlist_books_msg)).setText(R.string.no_internet_connection_msg);
                    fragmentRootView.findViewById(R.id.to_home_button).setVisibility(View.GONE);
                }else{
                    fragmentRootView.findViewById(R.id.wishlist_msg_linear_layout).setVisibility(View.VISIBLE);
                    fragmentRootView.findViewById(R.id.wishlist_books_complete_layout).setVisibility(View.GONE);
                    ((TextView)fragmentRootView.findViewById(R.id.wishlist_books_msg)).setText(R.string.error_in_fetching_wishlist_books);
                    fragmentRootView.findViewById(R.id.to_home_button).setVisibility(View.GONE);
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
    private Response.Listener<JSONObject> getWishlistBooksResponseListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if(getActivity()!=null) {
                        JSONObject fetchedJson = new JSONObject(response.toString());
                        createBooksList(fetchedJson, R.id.wishlist_books_recycler_view);
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
            fragmentRootView.findViewById(R.id.wishlist_msg_linear_layout).setVisibility(View.VISIBLE);
            fragmentRootView.findViewById(R.id.wishlist_books_complete_layout).setVisibility(View.GONE);
            fragmentRootView.findViewById(R.id.wishlist_books_retry_button).setVisibility(View.GONE);
            ((TextView)fragmentRootView.findViewById(R.id.wishlist_books_msg)).setText(R.string.no_book_in_wishlist);
            return false;
        }else{
            return true;
        }
    }


    private void createRecycleListView(int recyclerViewResource) {
        mRecyclerView = (RecyclerView) fragmentRootView.findViewById(recyclerViewResource);
        bookListAdapter = new WishListBookshelfListAdapterCorporate(getActivity(), bookList,false);
        mRecyclerView.setAdapter(bookListAdapter);
        bookListAdapter.setClickListener(new WishListBookshelfClickListener() {

            @Override
            public void onDelete(View deleteView, int position) {
//                fillDetailsOnPopUp(position);
//                wishListDeletePopUp.setVisibility(View.VISIBLE);
//                deleteItemPosition=position;
                deleteBook(position);
            }

            @Override
            public void itemClicked(View view, int position) {
                Intent i = new Intent(getActivity(), ProductPageActivityCorp.class);
                i.putExtra("isbn_num",bookList.get(position).getIsbn13());
                startActivity(i);
            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());

        mRecyclerView.setLayoutManager(linearLayoutManager);
    }

    public void fillDetailsOnPopUp(int positions){
        if(bookList.get(positions).getTitle()==null) {
            ((TextView) wishListDeletePopUp.findViewById(R.id.book_name)).setText("Dummy Title");
        }else{
            ((TextView) wishListDeletePopUp.findViewById(R.id.book_name)).setText(bookList.get(positions).getTitle());
        }
    }

    public String getUserId(){
        SharedPreferences settings = getActivity().getSharedPreferences(UrlsRetail.SAVED_USER_DETAILS, getActivity().MODE_PRIVATE);
        // Reading from SharedPreferences
        return settings.getString(UrlsRetail.SAVED_USER_ID,"");
    }


    // Delete Book

    public void deleteBook(int pos){
        deleteBookFromServer(pos);
    }

    private void deleteBookFromRecyclerView(int pos) {
        bookList.remove(pos);
        bookListAdapter.notifyDataSetChanged();
    }

    public void deleteBookFromServer(int pos){
        deleteBookFromRemoteServer(UrlsCorporate.DELETE_WISH_LIST_BOOKS,pos);
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
                Toast.makeText(getActivity(),"Unable To Delete Book"+error.toString(),Toast.LENGTH_LONG).show();
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
                    deleteBookFromRecyclerView(pos);
                    isBookListSizeZero();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }

    private boolean isBookListSizeZero() {

        if(bookList.size()==0){
            fragmentRootView.findViewById(R.id.wishlist_msg_linear_layout).setVisibility(View.VISIBLE);
            fragmentRootView.findViewById(R.id.wishlist_books_complete_layout).setVisibility(View.GONE);
            fragmentRootView.findViewById(R.id.wishlist_books_retry_button).setVisibility(View.GONE);
            ((TextView)fragmentRootView.findViewById(R.id.wishlist_books_msg)).setText(R.string.no_book_in_wishlist);
            return false;
        }else{
            return true;
        }
    }




}
