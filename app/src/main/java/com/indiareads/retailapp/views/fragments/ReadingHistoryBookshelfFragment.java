package com.indiareads.retailapp.views.fragments;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import com.indiareads.retailapp.adapters.ReadingHistoryBookshelfAdapter;
import com.indiareads.retailapp.corporate.view.activity.ProductPageActivityCorp;
import com.indiareads.retailapp.interfaces.ClickListener;
import com.indiareads.retailapp.models.Bookshelf;
import com.indiareads.retailapp.singleton.VolleySingleton;
import com.indiareads.retailapp.utils.DefaultMaxRetries;
import com.indiareads.retailapp.utils.UrlsRetail;
import com.indiareads.retailapp.views.activities.ProductPageActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class ReadingHistoryBookshelfFragment extends android.support.v4.app.Fragment  {


    private ArrayList<Bookshelf> bookList;
    private RecyclerView mRecyclerView;
    private View fragmentRootView;
    private ReadingHistoryBookshelfAdapter bookListAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        fragmentRootView=inflater.inflate(R.layout.fragment_reading_history_bookshelf, container, false);
        showLoadingScreen();
        fetchBooks();
        setRetryButonClickListener();

        return fragmentRootView;

    }

    private void setRetryButonClickListener() {
        fragmentRootView.findViewById(R.id.reading_history_books_retry_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentRootView.findViewById(R.id.reading_history_msg_linear_layout).setVisibility(View.GONE);
                fragmentRootView.findViewById(R.id.reading_history_books_complete_layout).setVisibility(View.VISIBLE);
                showLoadingScreen();
                fetchBooks();
            }
        });
    }

    public void showLoadingScreen(){
        fragmentRootView.findViewById(R.id.reading_history_loading_screen).setVisibility(View.VISIBLE);
        fragmentRootView.findViewById(R.id.reading_history_content).setVisibility(View.GONE);
    }
    public void hideLoadingScreen(){
        fragmentRootView.findViewById(R.id.reading_history_loading_screen).setVisibility(View.GONE);
        fragmentRootView.findViewById(R.id.reading_history_content).setVisibility(View.VISIBLE);
    }

    private void fetchBooks() {
        bookList = new ArrayList<>();
        fetchBookFromStorage();
    }

    public void fetchBookFromStorage(){
        fetchBooksFromRemoteServer(UrlsRetail.BOOKSHELF_READING_HISTORY_BOOKS);
    }

    public String getUserToken(){
        SharedPreferences settings = getActivity().getSharedPreferences(UrlsRetail.SAVED_USER_DETAILS, getActivity().MODE_PRIVATE);
        // Reading from SharedPreferences
        return settings.getString(UrlsRetail.SAVED_USER_TOKEN,"");
    }



    private void fetchBooksFromRemoteServer(String urls) {
        JsonObjectRequest jsonObjectRequest = null;
        try {
            jsonObjectRequest = getReadingHistoryBooksJsonRequest(urls);
            jsonObjectRequest.setTag(UrlsRetail.BOOKSHELF_READING_HISTORY_BOOKS_VOLLEY_REQUEST_TAG);
            VolleySingleton.getInstance(getActivity()).addToRequestQueue(jsonObjectRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public String getUserId(){
        SharedPreferences settings = getActivity().getSharedPreferences(UrlsRetail.SAVED_USER_DETAILS, getActivity().MODE_PRIVATE);
        // Reading from SharedPreferences
        return settings.getString(UrlsRetail.SAVED_USER_ID,"");
    }


    private JsonObjectRequest getReadingHistoryBooksJsonRequest(String url) throws JSONException {

        JSONObject obj=new JSONObject();
        obj.put("user_id",getUserId());

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url,obj, getReadingHistoryBooksResponseListener(), new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error instanceof NoConnectionError) {
                    fragmentRootView.findViewById(R.id.reading_history_msg_linear_layout).setVisibility(View.VISIBLE);
                    fragmentRootView.findViewById(R.id.reading_history_books_complete_layout).setVisibility(View.GONE);
                    ((TextView)fragmentRootView.findViewById(R.id.reading_history_books_msg)).setText(R.string.no_internet_connection_msg);
                }else{
                    fragmentRootView.findViewById(R.id.reading_history_msg_linear_layout).setVisibility(View.VISIBLE);
                    fragmentRootView.findViewById(R.id.reading_history_books_complete_layout).setVisibility(View.GONE);
                    ((TextView)fragmentRootView.findViewById(R.id.reading_history_books_msg)).setText(R.string.error_in_fetching_available_books);
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
    private Response.Listener<JSONObject> getReadingHistoryBooksResponseListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                  //  Log.e("reading history",response.toString());
                    if(getActivity()!=null) {
                        JSONObject fetchedJson = new JSONObject(response.toString());
                        createBooksList(fetchedJson, R.id.reading_history_books_recycler_view);
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
            fragmentRootView.findViewById(R.id.reading_history_msg_linear_layout).setVisibility(View.VISIBLE);
            fragmentRootView.findViewById(R.id.reading_history_books_complete_layout).setVisibility(View.GONE);
            fragmentRootView.findViewById(R.id.reading_history_books_retry_button).setVisibility(View.GONE);
            ((TextView)fragmentRootView.findViewById(R.id.reading_history_books_msg)).setText(R.string.no_reading_book_prsent);
            return false;
        }else{
            return true;
        }
    }


    private void createRecycleListView(int recyclerViewResource) {
        mRecyclerView = (RecyclerView) fragmentRootView.findViewById(recyclerViewResource);
        bookListAdapter = new ReadingHistoryBookshelfAdapter(getActivity(), bookList);
        mRecyclerView.setAdapter(bookListAdapter);
        bookListAdapter.setClickListener(new ClickListener() {
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

}
