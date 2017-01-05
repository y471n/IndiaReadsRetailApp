package com.indiareads.retailapp.corporate.view.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.indiareads.retailapp.R;
import com.indiareads.retailapp.adapters.BannerPagerAdapter;
import com.indiareads.retailapp.adapters.HorizontalBookListAdapter;
import com.indiareads.retailapp.corporate.view.activity.CategoryListingActivityCorp;
import com.indiareads.retailapp.corporate.view.activity.ProductPageActivityCorp;
import com.indiareads.retailapp.interfaces.ClickListener;
import com.indiareads.retailapp.models.Book;
import com.indiareads.retailapp.singleton.VolleySingleton;
import com.indiareads.retailapp.utils.CommonMethods;
import com.indiareads.retailapp.utils.DefaultMaxRetries;
import com.indiareads.retailapp.utils.UrlsRetail;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class HomeFragmentCorporate extends Fragment {

    View fragmentRootView;
    private RecyclerView mRecyclerView;
    private HorizontalBookListAdapter bookListAdapter;
    public static final String RECOMMENDATION = "recommendation";
    public static final String TRENDING = "trending";
    public static final String NEW = "new";

    LinearLayout loadingScreenForRecommendationBooks,loadingScreenForTrendingBooks,loadingScreenForNewBooks,loadingScreenForHomeBanner;
    RecyclerView recommendationRecyclerView,trendingRecyclerView,newBooksRecyclerView;
    ImageView departmentCategory1,departmentCategory2,departmentCategory3,departmentCategory4;


    public BannerPagerAdapter bannerPagerAdapter;
    String[] bannerUrls;

    private ViewPager mViewPager;
    private final static int NUM_PAGES = 4;
    private List<ImageView> dots;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentRootView= inflater.inflate(R.layout.fragment_home_corporate, container, false);

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
                checkInternetConnectivity();
            }
        });
    }

//    private void setNoInternetAccessBackgroundImage() {
//        fragmentRootView.findViewById(R.id.app_screen_background_image).setVisibility(View.VISIBLE);
//        fragmentRootView.findViewById(R.id.home_complete_layout).setVisibility(View.GONE);
//    }

    private void executeOtherMethods() {

        fetchViewsFromXml();
        setVisibility();
        fetchBanner();
        fetchBooks();
        setClickListeners();
        addDots();
        selectDot(0);
    }


    public void addDots() {
        dots = new ArrayList<>();
        LinearLayout dotsLayout = (LinearLayout)fragmentRootView.findViewById(R.id.dots_linear_layout);

        for(int i = 0; i < NUM_PAGES; i++) {
            ImageView dot = new ImageView(getActivity());
            //  View view = getActivity().getLayoutInflater().inflate(R.layout.dot_image_view, null);
            //   dot=(ImageView) view.findViewById(R.id.dot_image);

            dot.setImageDrawable(getResources().getDrawable(R.drawable.white));
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(5, 0, 0, 0);
            dot.setLayoutParams(lp);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            dotsLayout.addView(dot,params);

            dots.add(dot);
        }

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                selectDot(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    public void selectDot(int idx) {
        Resources res = getResources();
        for(int i = 0; i < NUM_PAGES; i++) {
            int drawableId = (i==idx)?(R.drawable.black):(R.drawable.white);
            Drawable drawable = res.getDrawable(drawableId);
            dots.get(i).setImageDrawable(drawable);
        }
    }
    public void setVisibility(){

        setVisibilityOfRecyclerView();
        setVisibilityOfLoadingScreen();

    }

    public void setVisibilityOfLoadingScreen(){
        loadingScreenForHomeBanner.setVisibility(View.VISIBLE);
        loadingScreenForRecommendationBooks.setVisibility(View.VISIBLE);
        loadingScreenForTrendingBooks.setVisibility(View.VISIBLE);
        loadingScreenForNewBooks.setVisibility(View.VISIBLE);

    }

    public void setVisibilityOfRecyclerView(){
        recommendationRecyclerView.setVisibility(View.GONE);
        trendingRecyclerView.setVisibility(View.GONE);
        newBooksRecyclerView.setVisibility(View.GONE);
        //  mViewPager.setVisibility(View.GONE);
    }

    public void fetchViewsFromXml(){
        departmentCategory1 = (ImageView) fragmentRootView.findViewById(R.id.department_category1_imageview);
        departmentCategory2 = (ImageView) fragmentRootView.findViewById(R.id.department_category2_imageview);
        departmentCategory3 = (ImageView) fragmentRootView.findViewById(R.id.department_category3_imageview);
        departmentCategory4 = (ImageView) fragmentRootView.findViewById(R.id.department_category4_imageview);

        mViewPager = (ViewPager) fragmentRootView.findViewById(R.id.homepage_banner_viewpager);

        loadingScreenForRecommendationBooks = (LinearLayout) fragmentRootView.findViewById(R.id.recommendation_loading_screen);
        loadingScreenForTrendingBooks = (LinearLayout) fragmentRootView.findViewById(R.id.trending_loading_screen);
        loadingScreenForNewBooks = (LinearLayout) fragmentRootView.findViewById(R.id.new_books_loading_screen);
        loadingScreenForHomeBanner= (LinearLayout) fragmentRootView.findViewById(R.id.banner_loading_screen);

        recommendationRecyclerView=(RecyclerView) fragmentRootView.findViewById(R.id.recommendation_recycler_view);
        trendingRecyclerView=(RecyclerView) fragmentRootView.findViewById(R.id.trending_recycler_view);
        newBooksRecyclerView=(RecyclerView) fragmentRootView.findViewById(R.id.new_books_recycler_view);


    }

    public void setClickListeners(){
        setDepartmentCategory1ClickListener();
        setDepartmentCategory2ClickListener();
        setDepartmentCategory3ClickListener();
        setDepartmentCategory4ClickListener();
    }

    private void setDepartmentCategory4ClickListener() {
        departmentCategory4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCategoryIdAndApiToSharedPreferences(33, UrlsRetail.PARENT_CAT);
                startCategoryListingActivity("Biography");
            }
        });
    }

    private void setDepartmentCategory1ClickListener() {
        departmentCategory1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCategoryIdAndApiToSharedPreferences(6, UrlsRetail.PARENT_CAT);
                startCategoryListingActivity("Fiction");
            }
        });
    }

    private void setDepartmentCategory2ClickListener() {
        departmentCategory2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCategoryIdAndApiToSharedPreferences(1, UrlsRetail.PARENT_CAT);
                startCategoryListingActivity("Academics");
            }
        });
    }
    private void setDepartmentCategory3ClickListener() {
        departmentCategory3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCategoryIdAndApiToSharedPreferences(8, UrlsRetail.PARENT_CAT);
                startCategoryListingActivity("Comics");
            }
        });
    }

    private void saveCategoryIdAndApiToSharedPreferences(int categoryId,String api) {
        SharedPreferences sharedPref = getActivity().getSharedPreferences(UrlsRetail.SAVED_BOOK_CATEGORY_DETAILS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(UrlsRetail.SAVED_CATEGORY_ID, String.valueOf(categoryId));
        editor.putString(UrlsRetail.SAVED_API, api);
        editor.putString(UrlsRetail.SAVED_FILTER_ID, String.valueOf(categoryId));
        editor.apply();
    }

    public void startCategoryListingActivity(String name){
        Intent intent = new Intent(getActivity(), CategoryListingActivityCorp.class);
        intent.putExtra("CategoryName",name);
        startActivity(intent);
    }


    public void fetchBanner(){

        fetchBannerUrlFromRemoteServer();
        setBanner();

    }

    public void setBanner(){
        bannerPagerAdapter = new BannerPagerAdapter(getActivity(), bannerUrls,fragmentRootView);
        mViewPager.setAdapter(bannerPagerAdapter);
    }

    public void fetchBannerUrlFromRemoteServer(){
        bannerUrls = new String[4];
        bannerUrls[0] = "http://indiareads.com/images/mob1.gif";
        bannerUrls[1] = "http://indiareads.com/images/mob2.gif";
        bannerUrls[2] = "http://indiareads.com/images/mob3.gif";
        bannerUrls[3] = "http://indiareads.com/images/mob4.gif";

//        bannerUrls[0] = "http://www.indiareads.info/images/books/9789381626184.jpg";
//        bannerUrls[1] = "http://www.indiareads.info/images/books/9789381626184.jpg";
//        bannerUrls[2] = "http://www.indiareads.info/images/books/9780553176988.jpg";

    }

    private void fetchBooks() {
        fetchRecommendationBooks();
        fetchTrendingBooks();
        fetchNewBooks();
    }

    private void fetchRecommendationBooks() {
        fetchBooksFromRemoteServer(UrlsRetail.RECOMMENDATION_BOOKS, RECOMMENDATION);
    }

    private void fetchTrendingBooks() {
        fetchBooksFromRemoteServer(UrlsRetail.TRENDING_BOOKS, TRENDING);
    }



    private void fetchNewBooks() {
        fetchBooksFromRemoteServer(UrlsRetail.NEW_BOOKS,NEW);
    }



    private void fetchBooksFromRemoteServer(String url,String booksType) {
        JsonObjectRequest jsonObjectRequest=null;
        switch (booksType) {
            case RECOMMENDATION:
                jsonObjectRequest = getRecommendationBookJsonRequest(url);

                break;
            case TRENDING:
                jsonObjectRequest = getTrendingBookJsonRequest(url);

                break;

            case NEW:
                jsonObjectRequest = getJsonRequest(url);

                break;
        }
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(jsonObjectRequest);
    }

    // Json Request for new Books
    private JsonObjectRequest getJsonRequest(String url) {

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, getNewBookResponseListener(), new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                if(error instanceof NoConnectionError) {
//                    checkInternetConnectivity();
//                }
            }
        });
        request.setRetryPolicy(new DefaultRetryPolicy(
                20000,
                DefaultMaxRetries.HOME_BOOKS_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        return request;
    }

    // response listener for new books
    private Response.Listener<JSONObject> getNewBookResponseListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONObject fetchedJson = null;
                try {
                    fetchedJson = new JSONObject(response.toString());
                    createBooksList(fetchedJson, R.id.new_books_recycler_view);

                    loadingScreenForNewBooks.setVisibility(View.GONE);
                    newBooksRecyclerView.setVisibility(View.VISIBLE);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
    }



    // Json request for Trending Request
    private JsonObjectRequest getTrendingBookJsonRequest(String url) {

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, getTrendingBookResponseListener(), new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                if(error instanceof NoConnectionError) {
//                    checkInternetConnectivity();
//                }

            }
        });
        request.setRetryPolicy(new DefaultRetryPolicy(
                20000,
                DefaultMaxRetries.HOME_BOOKS_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        return request;
    }


    // response listener for trending books
    private Response.Listener<JSONObject> getTrendingBookResponseListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONObject fetchedJson = null;
                try {
                    fetchedJson = new JSONObject(response.toString());
                    createBooksList(fetchedJson, R.id.trending_recycler_view);

                    loadingScreenForTrendingBooks.setVisibility(View.GONE);
                    trendingRecyclerView.setVisibility(View.VISIBLE);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
    }


    // Json Request for Recommendation Books
    private JsonObjectRequest getRecommendationBookJsonRequest(String url) {

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, getRecommendationBookResponseListener(), new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        request.setRetryPolicy(new DefaultRetryPolicy(
                20000,
                DefaultMaxRetries.HOME_BOOKS_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        return request;
    }


    // response listener for Recommendation books
    private Response.Listener<JSONObject> getRecommendationBookResponseListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONObject fetchedJson = null;
                try {
                    fetchedJson = new JSONObject(response.toString());
                    createBooksList(fetchedJson, R.id.recommendation_recycler_view);
                    loadingScreenForRecommendationBooks.setVisibility(View.GONE);
                    recommendationRecyclerView.setVisibility(View.VISIBLE);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
    }




//    private void createBooksList2(int recyclerView)  {
//
//        ArrayList<Book> displayBookList = new ArrayList<>();
//
//        Book obj=new Book();                        //dummy data need to be deleted
//        obj.set("Book1");
//        displayBookList.add(obj);
//        displayBookList.add(obj);
//        displayBookList.add(obj);
//        displayBookList.add(obj);
//
//
//        setRecycleListView(recyclerView, displayBookList);
//    }

    private void createBooksList(JSONObject fetchedJson, int recyclerView) throws JSONException {

        ArrayList<Book> displayBookList = new ArrayList<>();

        try {
            JSONArray booksList = fetchedJson.getJSONArray("data");
            Gson gson = new Gson();

            for (int i=0; i<booksList.length(); i++) {
                JSONObject jsonObject = booksList.getJSONObject(i);
                Book book = gson.fromJson(jsonObject.toString(), Book.class);
                displayBookList.add(book);
            }

            if(getActivity()!=null) {
                setRecycleListView(recyclerView, displayBookList);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setRecycleListView(int recyclerViewResource, final List<Book> booksList) {
        if(getActivity()==null){
            return;
        }
        mRecyclerView = (RecyclerView) fragmentRootView.findViewById(recyclerViewResource);
        bookListAdapter = new HorizontalBookListAdapter(getActivity(), booksList);
        mRecyclerView.setAdapter(bookListAdapter);
        bookListAdapter.setClickListener(new ClickListener() {
            @Override
            public void itemClicked(View view, int position) {

                Intent intent = new Intent(getActivity(), ProductPageActivityCorp.class);
                intent.putExtra("isbn_num",booksList.get(position).getIsbn13());
                startActivity(intent);

            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);

    }




}
