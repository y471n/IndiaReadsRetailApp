package com.indiareads.retailapp.views.fragments;

import android.app.Dialog;
import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.indiareads.retailapp.R;
import com.indiareads.retailapp.adapters.CategoryListingAdapter;
import com.indiareads.retailapp.interfaces.ClickListener;
import com.indiareads.retailapp.models.BookListing;
import com.indiareads.retailapp.singleton.VolleySingleton;
import com.indiareads.retailapp.utils.CommonMethods;
import com.indiareads.retailapp.utils.DefaultMaxRetries;
import com.indiareads.retailapp.utils.UrlsRetail;
import com.indiareads.retailapp.views.activities.FilterPageActivity;
import com.indiareads.retailapp.views.activities.ProductPageActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class CategoryListingFragment extends Fragment {


    private RecyclerView mRecyclerView;
    private View fragmentRootView;
    private CategoryListingAdapter bookListAdapter;
    Dialog dialog;

    private int  bookListingPageNo;
    ArrayList<BookListing> bookList;
    ImageView sortByPopularityImageView,sortByPriceImageView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        fragmentRootView= inflater.inflate(R.layout.fragment_category_listing_page, container, false);

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
//        fragmentRootView.findViewById(R.id.category_listing_msg_linear_layout).setVisibility(View.VISIBLE);
//        fragmentRootView.findViewById(R.id.category_listing_message).setVisibility(View.GONE);
//        fragmentRootView.findViewById(R.id.category_listing_complete_layout).setVisibility(View.GONE);
    }

    private void executeOtherMethods() {
        showLoadingScreen();
        fetchData();
        setFilterClickListener();

//        setSortByClickListener();
    }





    public void fetchData(){
        if(getApi().equals(UrlsRetail.SUPER_CAT) || getApi().equals(UrlsRetail.PARENT_CAT)) {
            fetchBooks();
        }else{
            fetchFilterBooks();
        }
    }

    public String getApi(){
        SharedPreferences settings = getActivity().getSharedPreferences(UrlsRetail.SAVED_BOOK_CATEGORY_DETAILS, getActivity().MODE_PRIVATE);
        return settings.getString(UrlsRetail.SAVED_API,"");
    }

    public void showLoadingScreen(){
        fragmentRootView.findViewById(R.id.category_listing_loading_screen).setVisibility(View.VISIBLE);
        fragmentRootView.findViewById(R.id.category_listing_page_content).setVisibility(View.GONE);
    }




    private void setFilterClickListener() {
        FloatingActionButton fab = (FloatingActionButton) fragmentRootView.findViewById(R.id.category_listing_filter_linear_layout);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), FilterPageActivity.class);
                intent.putExtra("categoryId", getFilterId());
                intent.putExtra("CategoryName", getActivity().getIntent().getExtras().getString("CategoryName"));

                startActivity(intent);
            }
        });
    }

    public String getCategoryId(){
        SharedPreferences settings = getActivity().getSharedPreferences(UrlsRetail.SAVED_BOOK_CATEGORY_DETAILS, getActivity().MODE_PRIVATE);
        return settings.getString(UrlsRetail.SAVED_CATEGORY_ID,"");
    }

    public String getFilterId(){
        SharedPreferences settings = getActivity().getSharedPreferences(UrlsRetail.SAVED_BOOK_CATEGORY_DETAILS, getActivity().MODE_PRIVATE);
        return settings.getString(UrlsRetail.SAVED_FILTER_ID,"");
    }

//    private void setSortByClickListener() {
//        fragmentRootView.findViewById(R.id.category_listing_sortby_linear_layout).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                showSortByDialog();
//
//            }
//        });
//    }

    public  void showSortByDialog(){
        dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.category_popup_sortby);
        dialog.setCancelable(true);
        dialog.show();
        fetchDialogViews();
        setSortByDialogViewsClickListeners();
    }

    public void fetchDialogViews(){
        sortByPopularityImageView=(ImageView)dialog.findViewById(R.id.popularity_image_view);
        sortByPriceImageView=(ImageView)dialog.findViewById(R.id.price_image_view);
    }

    public void setSortByDialogViewsClickListeners(){


        setSortByPopularityClickListener();
        setSortByPriceClickListener();

    }

    public void setSortByPriceClickListener(){
        dialog.findViewById(R.id.price_linear_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeViewsOfDialogUnChecked();
                sortByPriceImageView.setBackgroundResource(R.drawable.checked);
                sortByPriceImageView.setTag("checked");
                dialog.dismiss();
            }
        });

    }

    public void setSortByPopularityClickListener(){
        dialog.findViewById(R.id.popularity_linear_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                makeViewsOfDialogUnChecked();

                sortByPopularityImageView.setBackgroundResource(R.drawable.checked);
                sortByPopularityImageView.setTag("checked");
                dialog.dismiss();


            }
        });
    }

    public void makeViewsOfDialogUnChecked(){
        sortByPopularityImageView.setBackgroundResource(R.drawable.unchecked);
        sortByPopularityImageView.setTag("unchecked");
        sortByPriceImageView.setBackgroundResource(R.drawable.unchecked);
        sortByPriceImageView.setTag("unchecked");
    }


    private void fetchBooks() {
        bookListingPageNo=1;
        bookList = new ArrayList<>();
        fetchBookFromStorage();
    }

    public void fetchBookFromStorage(){
        String url=getUrl();
        fetchBooksFromRemoteServer(url);
    }

    public String getUrl(){
       if(getApi().equals(UrlsRetail.SUPER_CAT)){
          //  Log.e("url",UrlsRetail.SUPER_CATEGORY_BOOKS + "/" + getCategoryId()+"?page="+bookListingPageNo);
            return UrlsRetail.SUPER_CATEGORY_BOOKS + "/" + getCategoryId()+"/"+bookListingPageNo;
        }else{
         //   Log.e("url",UrlsRetail.PARENT_CATEGORY_BOOKS + "/" + getCategoryId()+"?page="+bookListingPageNo);
            return UrlsRetail.PARENT_CATEGORY_BOOKS + "/" + getCategoryId()+"/"+bookListingPageNo;
        }

    }

    private void fetchFilterBooks() {
        bookListingPageNo=1;
        bookList = new ArrayList<>();
        fetchFilterBookFromStorage();
    }
    public void fetchFilterBookFromStorage(){
        String url=getFilterBookFromUrl();
        fetchBooksFromRemoteServer(url);
    }

    public String getFilterBookFromUrl(){
        if(getApi().equals(UrlsRetail.CAT_LEVEL_1)){
          //  Log.e("url", UrlsRetail.CAT_LEVEL1 + "/" + getCategoryId() + "/" + bookListingPageNo);
            return UrlsRetail.CAT_LEVEL1 + "/" + getCategoryId()+"/"+bookListingPageNo;
        }else{
           // Log.e("url", UrlsRetail.CAT_LEVEL2 + "/" + getCategoryId() + "/" + bookListingPageNo);
            return UrlsRetail.CAT_LEVEL2 + "/" + getCategoryId()+"/"+bookListingPageNo;
        }

    }



    private void fetchBooksFromRemoteServer(String url) {
        JsonObjectRequest jsonObjectRequest = getBooksJsonRequest(url);
        jsonObjectRequest.setTag(UrlsRetail.CATEGORY_LISTING_VOLLEY_REQUEST_TAG);
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(jsonObjectRequest);

    }


    private JsonObjectRequest getBooksJsonRequest(String url) {
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url, getResponseListener(), new Response.ErrorListener() {
         //   private Request.Priority mPriority = Request.Priority.IMMEDIATE;
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
    private Response.Listener<JSONObject> getResponseListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                     JSONObject fetchedJson = new JSONObject(response.toString());
                //     Log.e("category listing", fetchedJson.toString());
                     createBooksList(fetchedJson,R.id.category_listing_recycler_view);
                     hideLoadingScreen();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
    }




    public void hideLoadingScreen(){
        fragmentRootView.findViewById(R.id.category_listing_loading_screen).setVisibility(View.GONE);
        fragmentRootView.findViewById(R.id.category_listing_page_content).setVisibility(View.VISIBLE);
    }

    private void createBooksList(JSONObject fetchedJson,int recyclerViewResource) {

        try {
            Gson gson = new Gson();
//            JSONObject List = fetchedJson.getJSONObject("data");
            JSONArray inStock=fetchedJson.getJSONArray("instock");
            JSONArray outOfStock=fetchedJson.getJSONArray("outofstock");
            if(isListNotZero(inStock, outOfStock)) {
                for (int i = 0; i < inStock.length(); i++) {
                    JSONObject jsonObject = inStock.getJSONObject(i);
                    BookListing book = gson.fromJson(jsonObject.toString(), BookListing.class);
                    book.setInStock(true);
                    bookList.add(book);
                }
                if (!checkExcludeOutOfStock()) {
                    for (int i = 0; i < outOfStock.length(); i++) {
                        JSONObject jsonObject = outOfStock.getJSONObject(i);
                        BookListing book = gson.fromJson(jsonObject.toString(), BookListing.class);
                        book.setInStock(false);
                        bookList.add(book);
                    }
                }else{
                    if(bookList.size()==0){
                        (fragmentRootView.findViewById(R.id.error_linear_layout)).setVisibility(View.VISIBLE);
                        ((TextView)fragmentRootView.findViewById(R.id.message)).setText(R.string.no_book_found);
                        (fragmentRootView.findViewById(R.id.complete_layout)).setVisibility(View.GONE);
                        (fragmentRootView.findViewById(R.id.retry)).setVisibility(View.GONE);
//                        fragmentRootView.findViewById(R.id.category_listing_complete_layout).setVisibility(View.GONE);
//                        fragmentRootView.findViewById(R.id.category_listing_msg_linear_layout).setVisibility(View.VISIBLE);
//                        fragmentRootView.findViewById(R.id.category_listing_message).setVisibility(View.VISIBLE);
//                        fragmentRootView.findViewById(R.id.app_screen_background_image).setVisibility(View.GONE);
//                        ((TextView)fragmentRootView.findViewById(R.id.category_listing_message)).setText(R.string.no_book_available_in_this_category);

                    }
                }
            }


         if(bookListingPageNo==1) {
             createRecycleListView(recyclerViewResource, bookList);
             createEndlessScrolling();
         }else{
             bookListAdapter.notifyDataSetChanged();
             fragmentRootView.findViewById(R.id.category_listing_infinite_scrolling_progressBar).setVisibility(View.GONE);
         }



        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private boolean isListNotZero(JSONArray inStock, JSONArray outOfStock) {

        if(inStock.length()==0 && outOfStock.length()==0 && bookListingPageNo==1){
            (fragmentRootView.findViewById(R.id.error_linear_layout)).setVisibility(View.VISIBLE);
            ((TextView)fragmentRootView.findViewById(R.id.message)).setText(R.string.no_book_found);
            (fragmentRootView.findViewById(R.id.complete_layout)).setVisibility(View.GONE);
            (fragmentRootView.findViewById(R.id.retry_button)).setVisibility(View.GONE);
//            fragmentRootView.findViewById(R.id.category_listing_complete_layout).setVisibility(View.GONE);
//            fragmentRootView.findViewById(R.id.category_listing_msg_linear_layout).setVisibility(View.VISIBLE);
//            fragmentRootView.findViewById(R.id.category_listing_message).setVisibility(View.VISIBLE);
//            fragmentRootView.findViewById(R.id.app_screen_background_image).setVisibility(View.GONE);
//            ((TextView)fragmentRootView.findViewById(R.id.category_listing_message)).setText(R.string.no_book_available_in_this_category);

            return false;
        }else{
            return true;
        }

    }

    public boolean checkExcludeOutOfStock(){
        if(getActivity().getIntent().hasExtra("excludeOutOfStock")){
            return true;
        }else{
            return false;
        }
    }

    private void createEndlessScrolling(){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.addOnScrollListener(new EndlessScrollListener(linearLayoutManager));
    }

    private void createRecycleListView(int recyclerViewResource,final List<BookListing> bookList) {
        mRecyclerView = (RecyclerView) fragmentRootView.findViewById(recyclerViewResource);
        bookListAdapter = new CategoryListingAdapter(getActivity(), bookList);
        mRecyclerView.setAdapter(bookListAdapter);
        bookListAdapter.setClickListener(new ClickListener() {
            @Override
            public void itemClicked(View view, int position) {
                //Log.e("psition is ",position+"");
                Intent i = new Intent(getActivity(), ProductPageActivity.class);
                i.putExtra("isbn_num",bookList.get(position).getIsbn13());
                startActivity(i);

            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);
    }

//https://gist.github.com/ssinss/e06f12ef66c51252563e
    public class EndlessScrollListener extends RecyclerView.OnScrollListener {

        public String TAG = EndlessScrollListener.class.getSimpleName();

        private int previousTotal = 0; // The total number of items in the dataset after the last load
        private boolean loading = true; // True if we are still waiting for the last set of data to load.
        private int visibleThreshold = 5; // The minimum amount of items to have below your current scroll position before loading more.
        int firstVisibleItem, visibleItemCount, totalItemCount;

        private int current_page = 1;

        private LinearLayoutManager mLinearLayoutManager;

        public EndlessScrollListener(LinearLayoutManager linearLayoutManager) {
            this.mLinearLayoutManager = linearLayoutManager;
        }

        @Override
        public final void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            visibleItemCount = mRecyclerView.getChildCount();
            totalItemCount = mLinearLayoutManager.getItemCount();
            firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition();
            if (loading) {
                if (totalItemCount > previousTotal) {
                    loading = false;
                    previousTotal = totalItemCount;
                }
            }

            if (!loading && (totalItemCount - visibleItemCount)
                    <= firstVisibleItem+ visibleThreshold){
                // End has been reached
                // Do something
                loading = true;

            //    nextBookList.setVisibility(View.VISIBLE);

                bookListingPageNo = bookListingPageNo + 1;

                if(getApi().equals(UrlsRetail.SUPER_CAT) || getApi().equals(UrlsRetail.PARENT_CAT)) {
                  fetchBookFromStorage();
              }else{
                  fetchFilterBookFromStorage();
              }

                fragmentRootView.findViewById(R.id.category_listing_infinite_scrolling_progressBar).setVisibility(View.VISIBLE);

            //    Toast.makeText(getActivity(), "Test1 passed "+bookListingPageNo, Toast.LENGTH_SHORT).show();


            }
        }

    }




}
