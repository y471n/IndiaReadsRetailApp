package com.indiareads.retailapp.corporate.view.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import com.indiareads.retailapp.corporate.adapter.CurrentlyReadingBookshelfAdapterCorporate;
import com.indiareads.retailapp.corporate.view.activity.MainActivityCorporate;
import com.indiareads.retailapp.corporate.view.activity.ProductPageActivityCorp;
import com.indiareads.retailapp.interfaces.CurrentlyReadingClickListener;
import com.indiareads.retailapp.models.Bookshelf;
import com.indiareads.retailapp.singleton.VolleySingleton;
import com.indiareads.retailapp.utils.DefaultMaxRetries;
import com.indiareads.retailapp.utils.UrlsCorporate;
import com.indiareads.retailapp.utils.UrlsRetail;
import com.indiareads.retailapp.corporate.view.activity.ChoosePickUpAddressActivityCorporate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CurrentlyReadingBookshelfFragmentCorporate extends android.support.v4.app.Fragment {



    private ArrayList<Bookshelf> bookList;
    private RecyclerView mRecyclerView;
    private CurrentlyReadingBookshelfAdapterCorporate bookListAdapter;

    List<Integer> selectedItems=new ArrayList<>();
    View fragmentRootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentRootView= inflater.inflate(R.layout.fragment_currently_reading_bookshelf_corporate, container, false);

        showLoadingScreen();
        fetchBooks();
        setReturnBookClickListener();
        setRetryButonClickListener();
        setGoToHomeButtonClickListener();

        return fragmentRootView;
    }

    private void setGoToHomeButtonClickListener() {
        fragmentRootView.findViewById(R.id.to_home_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MainActivityCorporate.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                getActivity().finish();
            }
        });
    }


    private void setRetryButonClickListener() {
        fragmentRootView.findViewById(R.id.currently_books_retry_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentRootView.findViewById(R.id.currently_reading_msg_linear_layout).setVisibility(View.GONE);
                fragmentRootView.findViewById(R.id.currently_books_complete_layout).setVisibility(View.VISIBLE);
                showLoadingScreen();
                fetchBooks();
            }
        });
    }

    public void showLoadingScreen(){
        fragmentRootView.findViewById(R.id.currently_reading_books_loading_screen).setVisibility(View.VISIBLE);
        fragmentRootView.findViewById(R.id.currently_reading_books_content).setVisibility(View.GONE);
    }
    public void hideLoadingScreen(){
        fragmentRootView.findViewById(R.id.currently_reading_books_loading_screen).setVisibility(View.GONE);
        fragmentRootView.findViewById(R.id.currently_reading_books_content).setVisibility(View.VISIBLE);
    }


    private void setReturnBookClickListener() {
        fragmentRootView.findViewById(R.id.return_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedItems.size() > 0) {
                    Intent intent = new Intent(getActivity(), ChoosePickUpAddressActivityCorporate.class);
                    Gson gson = new Gson();
                    String json = gson.toJson(getSelectedBookList());
                    intent.putExtra("list", json);
                    startActivity(intent);
                }
            }
        });
    }



    public List<Bookshelf> getSelectedBookList(){
        List<Bookshelf> list = new ArrayList<>();

        int i=0;
        while (i<selectedItems.size()){
            list.add(bookList.get(selectedItems.get(i)));
            i++;
        }

        return list;

    }

    public String getUserId(){
        SharedPreferences settings = getActivity().getSharedPreferences(UrlsRetail.SAVED_USER_DETAILS, getActivity().MODE_PRIVATE);
        // Reading from SharedPreferences
        return settings.getString(UrlsRetail.SAVED_USER_ID,"");
    }


    private void fetchBooks() {
        bookList = new ArrayList<>();
        fetchBookFromStorage();
    }

    public void fetchBookFromStorage(){
        //Log.e("currently url",UrlsRetail.BOOKSHELF_CURRENTLY_READING_BOOKS);
        fetchBooksFromRemoteServer(UrlsCorporate.BOOKSHELF_CURRENTLY_READING_BOOKS);
    }


    public String getUserToken(){
        SharedPreferences settings = getActivity().getSharedPreferences(UrlsRetail.SAVED_USER_DETAILS, getActivity().MODE_PRIVATE);
        // Reading from SharedPreferences
        return settings.getString(UrlsRetail.SAVED_USER_TOKEN,"");
    }

    private void fetchBooksFromRemoteServer(String urls) {
        JsonObjectRequest jsonObjectRequest;
        try {
            jsonObjectRequest = getCurrentlyReadingBooksJsonRequest(urls);
            jsonObjectRequest.setTag(UrlsCorporate.BOOKSHELF_CURRENTLY_READING_BOOKS_VOLLEY_REQUEST_TAG);
            VolleySingleton.getInstance(getActivity()).addToRequestQueue(jsonObjectRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private JsonObjectRequest getCurrentlyReadingBooksJsonRequest(String url) throws JSONException {

        JSONObject obj=new JSONObject();
        obj.put("user_id",getUserId());
        //Log.e("curren read json",obj.toString());

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url,obj, getCurrentlyReadingBooksResponseListener(), new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error instanceof NoConnectionError) {
                    fragmentRootView.findViewById(R.id.currently_reading_msg_linear_layout).setVisibility(View.VISIBLE);
                    fragmentRootView.findViewById(R.id.currently_books_complete_layout).setVisibility(View.GONE);
                    ((TextView)fragmentRootView.findViewById(R.id.currently_books_msg)).setText(R.string.no_internet_connection_msg);
                    fragmentRootView.findViewById(R.id.to_home_button).setVisibility(View.GONE);
                }else{
                    fragmentRootView.findViewById(R.id.currently_reading_msg_linear_layout).setVisibility(View.VISIBLE);
                    fragmentRootView.findViewById(R.id.currently_books_complete_layout).setVisibility(View.GONE);
                    ((TextView)fragmentRootView.findViewById(R.id.currently_books_msg)).setText(R.string.error_in_fetching_currently_reading_books);
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
    private Response.Listener<JSONObject> getCurrentlyReadingBooksResponseListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject fetchedJson = new JSONObject(response.toString());
                    Log.e("currently reading ",fetchedJson.toString());
                    createBooksList(fetchedJson, R.id.currently_books_recycler_view);
                    hideLoadingScreen();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
    }


    private void createBooksList(JSONObject fetchedJson,int recyclerViewResource) {

        try {
            if(getActivity()!=null) {
                JSONArray List = fetchedJson.getJSONArray("data");
                if (isListNotZero(List)) {
                    Gson gson = new Gson();
                    for (int i = 0; i < List.length(); i++) {
                        JSONObject jsonObject = List.getJSONObject(i);
                        Bookshelf book = gson.fromJson(jsonObject.toString(), Bookshelf.class);
                        book.setIsSelected(false);
                        bookList.add(book);
                    }

                    createRecycleListView(recyclerViewResource);
                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    private boolean isListNotZero(JSONArray list) {

        if(list.length()==0){
            fragmentRootView.findViewById(R.id.currently_reading_msg_linear_layout).setVisibility(View.VISIBLE);
            fragmentRootView.findViewById(R.id.currently_books_complete_layout).setVisibility(View.GONE);
            fragmentRootView.findViewById(R.id.currently_books_retry_button).setVisibility(View.GONE);
            ((TextView)fragmentRootView.findViewById(R.id.currently_books_msg)).setText(R.string.no_currently_reading_book_prsent);
            return false;
        }else{
            return true;
        }
    }



    private void createRecycleListView(int recyclerViewResource) {

        mRecyclerView = (RecyclerView) fragmentRootView.findViewById(recyclerViewResource);
        bookListAdapter = new CurrentlyReadingBookshelfAdapterCorporate(getActivity(), bookList);
        mRecyclerView.setAdapter(bookListAdapter);
        bookListAdapter.setClickListener(new CurrentlyReadingClickListener() {
            @Override
            public void itemClicked(View view, int position) {
                Intent i = new Intent(getActivity(), ProductPageActivityCorp.class);
                i.putExtra("isbn_num",bookList.get(position).getIsbn13());
                startActivity(i);
            }

            @Override
            public void onReturn(View returnView, int position) {

                if(bookList.get(position).isSelected()){
                    bookList.get(position).setIsSelected(false);
                    selectedItems.remove(selectedItems.indexOf(position));
                }else{
                    bookList.get(position).setIsSelected(true);
                    selectedItems.add(position);
                }
                bookListAdapter.notifyDataSetChanged();


                checkReturnBookButtonStatus();

            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());

        mRecyclerView.setLayoutManager(linearLayoutManager);
    }

    private void checkReturnBookButtonStatus() {
        if(selectedItems.size()>0){
            enableReturnBookButton();
        }else{
            disableReturnBookButton();
        }
    }

    public void enableReturnBookButton(){
        fragmentRootView.findViewById(R.id.return_button).setBackgroundColor(getResources().getColor(R.color.red));
    }

    public void disableReturnBookButton(){
        fragmentRootView.findViewById(R.id.return_button).setBackgroundColor(getResources().getColor(R.color.grey));
    }



}

