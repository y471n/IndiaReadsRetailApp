package com.indiareads.retailapp.views.fragments;

import android.app.Fragment;
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
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;
import com.indiareads.retailapp.R;
import com.indiareads.retailapp.adapters.MyOrderListAdapter;
import com.indiareads.retailapp.interfaces.ClickListener;
import com.indiareads.retailapp.models.Order;
import com.indiareads.retailapp.singleton.VolleySingleton;
import com.indiareads.retailapp.utils.CommonMethods;
import com.indiareads.retailapp.utils.DefaultMaxRetries;
import com.indiareads.retailapp.utils.IndiaReadsApplication;
import com.indiareads.retailapp.utils.UrlsRetail;
import com.indiareads.retailapp.views.activities.MainActivity;
import com.indiareads.retailapp.views.activities.OrderDetailsActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class MyOrderFragment extends Fragment {


    private ArrayList<Order> ordersList;
    private View fragmentRootView;
    private RecyclerView mRecyclerView;
    private MyOrderListAdapter myOrderListAdapter;

    private int  bookListingPageNo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        fragmentRootView= inflater.inflate(R.layout.fragment_my_order_list_page, container, false);
        setRetryButtonClickListener();
        setGoToHomeButtonClickListener();
        checkInternetConnectivity();

        return fragmentRootView;

    }

    private void sendAnalaytics() {
        IndiaReadsApplication application = (IndiaReadsApplication) getActivity().getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName("MyOrder ");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }


    private void setRetryButtonClickListener() {
        fragmentRootView.findViewById(R.id.orders_retry_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoadingScreen();
                fetchOrders();
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




    private void checkInternetConnectivity() {
        if(!CommonMethods.hasActiveInternetConnection(getActivity())){
            ((TextView)fragmentRootView.findViewById(R.id.message)).setText(R.string.no_internet_connection_msg);
            ((Button)fragmentRootView.findViewById(R.id.orders_retry_button)).setVisibility(View.VISIBLE);
            (fragmentRootView.findViewById(R.id.my_orders_recycler_view)).setVisibility(View.GONE);
            (fragmentRootView.findViewById(R.id.to_home_button)).setVisibility(View.GONE);
        }else{
            executeOtherMethods();
            sendAnalaytics();
        }
    }

    private void setNoInternetAccessBackgroundImage() {
//        fragmentRootView.findViewById(R.id.app_screen_background_image).setVisibility(View.VISIBLE);
//        fragmentRootView.findViewById(R.id.my_orders_page_complete_layout).setVisibility(View.GONE);
    }

    private void executeOtherMethods() {
        showLoadingScreen();
        fetchOrders();
    }


    public void showLoadingScreen(){
        fragmentRootView.findViewById(R.id.orders_loading_screen).setVisibility(View.VISIBLE);
        fragmentRootView.findViewById(R.id.my_orders_content).setVisibility(View.GONE);
    }
    public void hideLoadingScreen(){
        fragmentRootView.findViewById(R.id.orders_loading_screen).setVisibility(View.GONE);
        fragmentRootView.findViewById(R.id.my_orders_content).setVisibility(View.VISIBLE);
    }


    private void fetchOrders() {
        ordersList=new ArrayList<>();
        bookListingPageNo=1;
        fetchOrdersFromStorage();
    }

    public void fetchOrdersFromStorage(){
        fetchOrdersFromRemoteServer(UrlsRetail.MY_ORDER);
    }

    public String getUserToken(){
        SharedPreferences settings = getActivity().getSharedPreferences(UrlsRetail.SAVED_USER_DETAILS, getActivity().MODE_PRIVATE);
        // Reading from SharedPreferences
        return settings.getString(UrlsRetail.SAVED_USER_TOKEN,"");
    }

    private void fetchOrdersFromRemoteServer(String urls) {
        JsonObjectRequest jsonObjectRequest;
        try {
            jsonObjectRequest = getOrdersJsonRequest(urls);
            jsonObjectRequest.setTag(UrlsRetail.MY_ORDER_VOLLEY_REQUEST_TAG);
            VolleySingleton.getInstance(getActivity()).addToRequestQueue(jsonObjectRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private JsonObjectRequest getOrdersJsonRequest(String url) throws JSONException {

        JSONObject obj=new JSONObject();
        obj.put("user_id", getUserId());
        obj.put("pageNumber", bookListingPageNo);

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url,obj, getOrdersResponseListener(), new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideLoadingScreen();
                if(error instanceof NoConnectionError) {
                    ((TextView)fragmentRootView.findViewById(R.id.message)).setText(R.string.no_internet_connection_msg);
                    ((Button)fragmentRootView.findViewById(R.id.orders_retry_button)).setVisibility(View.VISIBLE);
                    (fragmentRootView.findViewById(R.id.my_orders_recycler_view)).setVisibility(View.GONE);
                    (fragmentRootView.findViewById(R.id.to_home_button)).setVisibility(View.GONE);
                }else{
                    ((TextView)fragmentRootView.findViewById(R.id.message)).setText(R.string.error_in_fetching_data);
                    ((Button)fragmentRootView.findViewById(R.id.orders_retry_button)).setVisibility(View.VISIBLE);
                    (fragmentRootView.findViewById(R.id.my_orders_recycler_view)).setVisibility(View.GONE);
                    (fragmentRootView.findViewById(R.id.to_home_button)).setVisibility(View.GONE);

                }
            }
        });

        jsonRequest.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultMaxRetries.APP_API_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        return jsonRequest;
    }


    @NonNull
    private Response.Listener<JSONObject> getOrdersResponseListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject fetchedJson = new JSONObject(response.toString());
                    createOrdersList(fetchedJson);
                    hideLoadingScreen();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
    }


    public String getUserId(){
        SharedPreferences settings = getActivity().getSharedPreferences(UrlsRetail.SAVED_USER_DETAILS, getActivity().MODE_PRIVATE);
        // Reading from SharedPreferences
        return settings.getString(UrlsRetail.SAVED_USER_ID,"");
    }


    private void createOrdersList(JSONObject fetchedJson) {

        try {
            JSONArray List = fetchedJson.getJSONArray("data");
            if (List.length()==0 && bookListingPageNo==1) {
                ((TextView)fragmentRootView.findViewById(R.id.message)).setText(R.string.no_orders);
                ((Button)fragmentRootView.findViewById(R.id.orders_retry_button)).setVisibility(View.GONE);
                (fragmentRootView.findViewById(R.id.my_orders_recycler_view)).setVisibility(View.GONE);
                (fragmentRootView.findViewById(R.id.to_home_button)).setVisibility(View.VISIBLE);
            }else{
                ((TextView)fragmentRootView.findViewById(R.id.message)).setVisibility(View.GONE);
                ((Button)fragmentRootView.findViewById(R.id.orders_retry_button)).setVisibility(View.GONE);
                (fragmentRootView.findViewById(R.id.to_home_button)).setVisibility(View.GONE);
            Gson gson = new Gson();
            for (int i = 0; i < List.length(); i++) {
                JSONObject jsonObject = List.getJSONObject(i);
                Order order = gson.fromJson(jsonObject.toString(), Order.class);

                ordersList.add(order);
            }

                if(bookListingPageNo==1) {
                    createRecycleListView();
                    createEndlessScrolling();
                }else{
                    myOrderListAdapter.notifyDataSetChanged();
                    fragmentRootView.findViewById(R.id.orders_infinite_scrolling_progressBar).setVisibility(View.GONE);
                }

        }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void createRecycleListView() {
        mRecyclerView = (RecyclerView) fragmentRootView.findViewById(R.id.my_orders_recycler_view);
        myOrderListAdapter = new MyOrderListAdapter(getActivity(), ordersList);
        mRecyclerView.setAdapter(myOrderListAdapter);
        myOrderListAdapter.setClickListener(new ClickListener() {
            @Override
            public void itemClicked(View view, int position) {

                Intent intent = new Intent(getActivity(), OrderDetailsActivity.class);
                intent.putExtra("order", ordersList.get(position));
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.right_in, R.anim.left_out);

            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);

    }

    private void createEndlessScrolling(){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.addOnScrollListener(new EndlessScrollListener(linearLayoutManager));
    }

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

                fetchOrdersFromStorage();

                fragmentRootView.findViewById(R.id.orders_infinite_scrolling_progressBar).setVisibility(View.GONE);

               // Toast.makeText(getActivity(), "Test1 passed "+bookListingPageNo, Toast.LENGTH_SHORT).show();


            }
        }

    }





}
