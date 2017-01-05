package com.indiareads.retailapp.views.fragments;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
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
import com.indiareads.retailapp.adapters.SearchListingAdapter;
import com.indiareads.retailapp.interfaces.ClickListener;

import com.indiareads.retailapp.models.Book;
import com.indiareads.retailapp.singleton.VolleySingleton;
import com.indiareads.retailapp.utils.CommonMethods;
import com.indiareads.retailapp.utils.DefaultMaxRetries;
import com.indiareads.retailapp.utils.UrlsRetail;
import com.indiareads.retailapp.views.activities.ProductPageActivity;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class SearchFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private SearchListingAdapter bookListAdapter;
    List<Book> bookList;

    List<String> isbnList;

    private View fragmentRootView;

    LinearLayout searchLoadingScreen,searchContent;

    int firstHit;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentRootView = inflater.inflate(R.layout.fragment_search, container, false);

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
//        fragmentRootView.findViewById(R.id.search_page_complete_layout).setVisibility(View.GONE);
    }


    private void executeOtherMethods() {
        searchLoadingScreen=(LinearLayout)fragmentRootView.findViewById(R.id.search_loading_screen);
        searchContent=(LinearLayout)fragmentRootView.findViewById(R.id.search_content);
        showLoadingScreen();

        fetchSearchBooksIsbn(getActivity().getIntent().getStringExtra(SearchManager.QUERY));
    }

    public void showLoadingScreen(){
        searchLoadingScreen.setVisibility(View.VISIBLE);
        searchContent.setVisibility(View.GONE);
    }

    public void hideLoadingScreen(){
        searchLoadingScreen.setVisibility(View.GONE);
        searchContent.setVisibility(View.VISIBLE);
    }

//    private void setFilterClickListener() {
//        fragmentRootView.findViewById(R.id.search_filter_linear_layout).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getActivity(), SearchFilterActivity.class);
//                startActivity(intent);
//            }
//        });
//    }
//
//    private void setSortByClickListener() {
//        fragmentRootView.findViewById(R.id.search_sortby_linear_layout).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showSortByDialog();
//
//
//            }
//        });
 //   }

//    public  void showSortByDialog(){
//        dialog = new Dialog(getActivity());
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setContentView(R.layout.category_popup_sortby);
//        dialog.setCancelable(true);
//        dialog.show();
//        fetchDialogViews();
//        setSortByDialogViewsClickListeners();
//    }

//    public void fetchDialogViews(){
//        sortByPopularityImageView=(ImageView)dialog.findViewById(R.id.popularity_image_view);
//        sortByPriceImageView=(ImageView)dialog.findViewById(R.id.price_image_view);
//        sortByPopularityLinearLayout=(LinearLayout)dialog.findViewById(R.id.popularity_linear_layout);
//        sortByPriceLinearLayout=(LinearLayout)dialog.findViewById(R.id.price_linear_layout);
//    }
//
//    public void setSortByDialogViewsClickListeners(){
//
//
//        setSortByPopularityClickListener();
//        setSortByPriceClickListener();
//
//    }

//    public void setSortByPriceClickListener(){
//        sortByPriceLinearLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                makeViewsOfDialogUnChecked();
//                sortByPriceImageView.setBackgroundResource(R.drawable.checked);
//                sortByPriceImageView.setTag("checked");
//                dialog.dismiss();
//            }
//        });
//
//    }
//
//    public void setSortByPopularityClickListener(){
//        sortByPopularityLinearLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                makeViewsOfDialogUnChecked();
//
//                sortByPopularityImageView.setBackgroundResource(R.drawable.checked);
//                sortByPopularityImageView.setTag("checked");
//                dialog.dismiss();
//
//
//            }
//        });
//    }

//    public void makeViewsOfDialogUnChecked(){
//        sortByPopularityImageView.setBackgroundResource(R.drawable.unchecked);
//        sortByPopularityImageView.setTag("unchecked");
//        sortByPriceImageView.setBackgroundResource(R.drawable.unchecked);
//        sortByPriceImageView.setTag("unchecked");
//    }



    private void fetchSearchBooksIsbn(String query)
    {     firstHit=1;
        isbnList=new ArrayList<String>();
        fetchBooksIsbnFromRemoteServer(UrlsRetail.SEARCH, query);
    }

    private void fetchBooksIsbnFromRemoteServer(String urls,String query) {
        JsonObjectRequest jsonObjectRequest = null;
        try {
            jsonObjectRequest = getSearchIsbnJsonRequest(urls, query);
            jsonObjectRequest.setTag(UrlsRetail.SEARCH_VOLLEY_REQUEST_TAG);
            VolleySingleton.getInstance(getActivity()).addToRequestQueue(jsonObjectRequest);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(),"error",Toast.LENGTH_LONG).show();
        }

    }

    private JsonObjectRequest getSearchIsbnJsonRequest(String url,String query) throws JSONException {

        JSONObject search=new JSONObject();
        search.put("query", query);


        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url,search, getSearchIsbnResponseListener(), new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
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
    private Response.Listener<JSONObject> getSearchIsbnResponseListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject fetchedJson = new JSONObject(response.toString());
                    getIsbnList(fetchedJson);

                    if(isbnList.size()>=1){
                        fetchBookDetails();
                    }else{
                        hideLoadingScreen();
                        (fragmentRootView.findViewById(R.id.error_linear_layout)).setVisibility(View.VISIBLE);
                        ((TextView)fragmentRootView.findViewById(R.id.message)).setText(R.string.no_result_founds);
                        (fragmentRootView.findViewById(R.id.complete_layout)).setVisibility(View.GONE);
                        fragmentRootView.findViewById(R.id.retry_button).setVisibility(View.GONE);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
    }



    public void getIsbnList(JSONObject fetchedJson) throws JSONException {

        JSONArray arr=fetchedJson.getJSONArray("isbnList");
        for(int i=0;i<arr.length();i++){
            isbnList.add(arr.get(i).toString());
        }

//          JSONObject data = fetchedJson.getJSONObject("data");
//          JSONObject ISBN13 = data.getJSONObject("ISBN13");
//          JSONArray groups = ISBN13.getJSONArray("groups");
//          for (int i = 0; i < groups.length(); i++) {
//              JSONObject obj1 = groups.getJSONObject(i);
//              JSONObject docList = obj1.getJSONObject("doclist");
//              JSONArray docs = docList.getJSONArray("docs");
//              for (int j = 0; j < docs.length(); j++) {
//                  JSONObject bookObj = docs.getJSONObject(j);
//                  String isbn =bookObj.getString("ISBN13");
//                  isbnList.add(isbn);
//              }
//          }


    }


    private void fetchBookDetails() {
        bookList=new ArrayList<>();
       for(int i=0;i<isbnList.size();i++) {
           String url = getBookDetailsUrl(i);
           fetchBookDetailsFromRemoteServer(url);
       }
    }

    public String getBookDetailsUrl(int i){
       // Log.e("url",UrlsRetail.BOOK_DETAILS+"/"+ isbnList.get(i));
        return UrlsRetail.BOOK_DETAILS+"/"+isbnList.get(i);
    }


    private void fetchBookDetailsFromRemoteServer(String url) {
       try {
           JsonObjectRequest jsonObjectRequest = getBooksDetailsJsonRequest(url);
           jsonObjectRequest.setTag(UrlsRetail.BOOK_DETAILS_VOLLEY_TAG);
           VolleySingleton.getInstance(getActivity()).addToRequestQueue(jsonObjectRequest);
       }catch(Exception e){

       }
    }

    private JsonObjectRequest getBooksDetailsJsonRequest(String url) {

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url, getBookDetailsResponseListener(), new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(getActivity(),"error 3 ",Toast.LENGTH_SHORT).show();
//                Log.e("staring ",error.toString());
            }
        });

        jsonRequest.setRetryPolicy(new DefaultRetryPolicy(
                20000,
                DefaultMaxRetries.APP_API_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        return jsonRequest;
    }

    @NonNull
    private Response.Listener<JSONObject> getBookDetailsResponseListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject fetchedJson = new JSONObject(response.toString());
                  //  Log.e("search response ",fetchedJson.toString());

                    if(firstHit++==1){
                        createBookInfo(fetchedJson);
                        createRecycleListView();
                        hideLoadingScreen();
                    }else{
                        createBookInfo(fetchedJson);
                        bookListAdapter.notifyDataSetChanged();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    public void createBookInfo(JSONObject fetchedJson){

        try {
            JSONObject List = fetchedJson.getJSONObject("data");
            Gson gson = new Gson();
            Book book = gson.fromJson(List
                    .toString(), Book.class);
            bookList.add(book);
        } catch (JSONException e) {
            Toast.makeText(getActivity(),"error",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }




    private void createRecycleListView() {
        mRecyclerView = (RecyclerView) fragmentRootView.findViewById(R.id.search_listing_recycler_view);
        bookListAdapter = new SearchListingAdapter(getActivity(), bookList);
        mRecyclerView.setAdapter(bookListAdapter);
        bookListAdapter.setClickListener(new ClickListener() {
            @Override
            public void itemClicked(View view, int position) {
                Intent i = new Intent(getActivity(), ProductPageActivity.class);
                i.putExtra("isbn_num",bookList.get(position).getIsbn13());
                startActivity(i);
            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);
    }




}
