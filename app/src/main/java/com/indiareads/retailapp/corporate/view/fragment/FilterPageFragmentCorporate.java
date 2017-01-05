package com.indiareads.retailapp.corporate.view.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.indiareads.retailapp.R;
import com.indiareads.retailapp.adapters.ExpandableListAdapter;
import com.indiareads.retailapp.corporate.view.activity.CategoryListingActivityCorp;
import com.indiareads.retailapp.singleton.VolleySingleton;
import com.indiareads.retailapp.utils.DefaultMaxRetries;
import com.indiareads.retailapp.utils.UrlsRetail;
import com.indiareads.retailapp.views.activities.CategoryListingActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FilterPageFragmentCorporate extends Fragment {


    View fragmentRootView;
    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    String categoryName="";

    LinearLayout subCategoriesButtonLinearLayout,excludeOfStockButtonLinearLayout; //hotDealsButtonLinearLayout
    TextView subCategoriesTextView,excludeOfStockTextView;//,hotDealsTextView
    ImageView subCategoriesImageView;
    //   ImageView hotDealsImageView;
    ImageView excludeOfStockImageView;
    ImageView hotDealsSelectImageView,excludeOfStockSelectImageView;

    public static final String SUB_CATEGORIES="sub_categories";
    public static final String HOT_DEALS="hot_deals";
    public static final String EXCLUDE_OF_STOCK="exclude_of_stock";

    List<String> allSubCatsListDataHeader=new ArrayList<>();
    List<Integer> allSubCatsListDataHeaderId=new ArrayList<>();

    HashMap<String, List<String>> allSubCatsListDataChild=new HashMap<>();
    HashMap<Integer, List<Integer>> allSubCatsListDataChildId=new HashMap<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentRootView= inflater.inflate(R.layout.fragment_filter_page_corporate, container, false);
        showLoadingScreen();
        fetchViewsFromXml();
        setClickListeners();
        setRetryButonClickListener();
        fetchSubCat1SubCat2();

       return fragmentRootView;
    }

    private void setRetryButonClickListener() {
        fragmentRootView.findViewById(R.id.category_filter_retry_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                fragmentRootView.findViewById(R.id.currently_reading_msg_linear_layout).setVisibility(View.GONE);
//                fragmentRootView.findViewById(R.id.currently_books_complete_layout).setVisibility(View.VISIBLE);
                showLoadingScreen();
                fetchSubCat1SubCat2();
            }
        });
    }


    public void showLoadingScreen(){
        fragmentRootView.findViewById(R.id.filter_page_loading_screen).setVisibility(View.VISIBLE);
        fragmentRootView.findViewById(R.id.filter_page_content).setVisibility(View.GONE);

    }

    private void fetchViewsFromXml(){

        subCategoriesButtonLinearLayout=(LinearLayout) fragmentRootView.findViewById(R.id.sub_categories_button_linear_layout);
        // hotDealsButtonLinearLayout=(LinearLayout) fragmentRootView.findViewById(R.id.hot_deals_button_linear_layout);
        excludeOfStockButtonLinearLayout=(LinearLayout) fragmentRootView.findViewById(R.id.exclude_of_stocks_button_linear_layout);
        subCategoriesTextView=(TextView) fragmentRootView.findViewById(R.id.sub_category_textview);
        //  hotDealsTextView=(TextView) fragmentRootView.findViewById(R.id.hot_deals_textview);
        excludeOfStockTextView=(TextView) fragmentRootView.findViewById(R.id.exclude_of_stock_textview);
        subCategoriesImageView=(ImageView) fragmentRootView.findViewById(R.id.sub_category_imageview);
        //   hotDealsImageView=(ImageView) fragmentRootView.findViewById(R.id.hot_deals_imageview);
        excludeOfStockImageView=(ImageView) fragmentRootView.findViewById(R.id.exclude_of_stock_imageview);
        //   hotDealsSelectImageView=(ImageView) fragmentRootView.findViewById(R.id.hot_deals_select_image_view);
        excludeOfStockSelectImageView=(ImageView) fragmentRootView.findViewById(R.id.exclude_of_stock_select_image_view);

    }

    //    private void setHotDealsContentLinearLayoutClickListener() {
//        fragmentRootView.findViewById(R.id.hot_deals_select_content_linear_layout).setOnClickListener(new View.OnClickListener() {
//             @Override
//             public void onClick(View v) {
//
//
//                 if (hotDealsSelectImageView.getTag() == "checked") {
//                     hotDealsSelectImageView.setBackgroundResource(R.drawable.unchecked);
//                     hotDealsSelectImageView.setTag("unchecked");
//                 } else {
//                     hotDealsSelectImageView.setBackgroundResource(R.drawable.checked);
//                     hotDealsSelectImageView.setTag("checked");
//                 }
//
//             }
//         });
//    }
    private void setExcludeOfStockContentLinearLayoutClickListener() {
        fragmentRootView.findViewById(R.id.exclude_out_of_stock_content_linear_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (excludeOfStockSelectImageView.getTag() == "checked") {
                    excludeOfStockSelectImageView.setBackgroundResource(R.drawable.unchecked);
                    excludeOfStockSelectImageView.setTag("unchecked");
                    disableApplyButton();
                } else {
                    excludeOfStockSelectImageView.setBackgroundResource(R.drawable.checked);
                    excludeOfStockSelectImageView.setTag("checked");
                    enableApplyButton();
                }


            }
        });
    }

    public void setClickListeners(){
        setSubCategoriesButtonClickListener();
        //setHotDealsButtonClickListener();
        setExcludeOutOfStockButtonClickListener();
        setApplyButtonClickListener();
        setExcludeOfStockContentLinearLayoutClickListener();

    }

    public void  setUpExpandableListView(){

        expListView = (ExpandableListView) fragmentRootView.findViewById(R.id.filter_page_category_list);
        listAdapter = new ExpandableListAdapter(getActivity(), allSubCatsListDataHeader, allSubCatsListDataChild);
        expListView.setAdapter(listAdapter);

        setExpandableListViewClickListener();
    }


    private void setExpandableListViewClickListener() {

        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                enableApplyButton();

                if (v.findViewById(R.id.checked_arrow).getVisibility() == View.INVISIBLE) {
                    v.findViewById(R.id.checked_arrow).setVisibility(View.VISIBLE);
                    addSelectItemIdToList(groupPosition, childPosition);
                    //  startCategoryListingActivity();
                } else {
                    v.findViewById(R.id.checked_arrow).setVisibility(View.INVISIBLE);
                    deleteSelectItemIdFromList();
                    // startCategoryListingActivity();
                }


                return false;
            }
        });
    }

    private void enableApplyButton() {
        Button b=(Button)fragmentRootView.findViewById(R.id.apply_button);
        b.setBackgroundResource(R.drawable.full_screen_width_button_background);
        b.setHintTextColor(getResources().getColor(R.color.whiteColor));
        b.setEnabled(true);
        b.setClickable(true);
    }

    private void disableApplyButton() {
        Button b=(Button)fragmentRootView.findViewById(R.id.apply_button);
        b.setBackgroundResource(R.drawable.full_screen_width_disable_button_background);
        b.setHintTextColor(getResources().getColor(R.color.black));
        b.setEnabled(false);
        b.setClickable(false);
    }


    public void addSelectItemIdToList(int groupPosition, int childPosition){
        if (allSubCatsListDataChild.get(allSubCatsListDataHeader.get(groupPosition)).get(childPosition).equals("All")) {
            saveCategoryIdAndApiToSharedPreferences(allSubCatsListDataChildId.get(allSubCatsListDataHeaderId.get(groupPosition)).get(childPosition), UrlsRetail.CAT_LEVEL_1);
            categoryName=allSubCatsListDataHeader.get(groupPosition);
            // Toast.makeText(getActivity(),"All",Toast.LENGTH_LONG).show();
        }else{
            // Toast.makeText(getActivity(),"notAll",Toast.LENGTH_LONG).show();
            saveCategoryIdAndApiToSharedPreferences(allSubCatsListDataChildId.get(allSubCatsListDataHeaderId.get(groupPosition)).get(childPosition), UrlsRetail.CAT_LEVEL_2);
            categoryName=allSubCatsListDataChild.get(allSubCatsListDataHeader.get(groupPosition)).get(childPosition);
        }
    }

    private void saveCategoryIdAndApiToSharedPreferences(int categoryId,String api) {
        SharedPreferences sharedPref = getActivity().getSharedPreferences(UrlsRetail.SAVED_BOOK_CATEGORY_DETAILS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(UrlsRetail.SAVED_CATEGORY_ID, String.valueOf(categoryId));
        editor.putString(UrlsRetail.SAVED_API, api);
        editor.apply();
    }

    public void deleteSelectItemIdFromList(){


    }

    public void startCategoryListingActivity(){
//
        Intent intent = new Intent(getActivity(), CategoryListingActivityCorp.class);
        if(excludeOfStockSelectImageView.getTag().equals("checked")){
            intent.putExtra("excludeOutOfStock",true);
            intent.putExtra("CategoryName",getActivity().getIntent().getExtras().getString("CategoryName"));
        }else{
            intent.putExtra("CategoryName",categoryName);
        }
        startActivity(intent);
        getActivity().finish();
    }



    private void setSubCategoriesButtonClickListener() {
        subCategoriesButtonLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                changeColorOfSelectButton(SUB_CATEGORIES);
                makeVisibleSelectButtonContent(SUB_CATEGORIES);
            }
        });
    }

//    private void setHotDealsButtonClickListener() {
//        hotDealsButtonLinearLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//
//                changeColorOfSelectButton(HOT_DEALS);
//                makeVisibleSelectButtonContent(HOT_DEALS);
//
//
//            }
//        });
//    }

    private void setExcludeOutOfStockButtonClickListener() {
        excludeOfStockButtonLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                changeColorOfSelectButton(EXCLUDE_OF_STOCK);
                makeVisibleSelectButtonContent(EXCLUDE_OF_STOCK);


            }
        });
    }

    private void setApplyButtonClickListener() {
        fragmentRootView.findViewById(R.id.apply_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startCategoryListingActivity();

            }
        });
    }

    public void changeColorOfSelectButton(String text){

        makeAllButtonBlack();
        makeAllTextWhite();
        makeAllImageWhite();
        switch(text){



            case SUB_CATEGORIES:

                subCategoriesButtonLinearLayout.setBackgroundColor(getResources().getColor(R.color.greybackground));
                subCategoriesTextView.setTextColor(getResources().getColor(R.color.black));
                subCategoriesImageView.setImageResource(R.drawable.sub_categories_grey);
                break;

//            case HOT_DEALS:
//
//                hotDealsButtonLinearLayout.setBackgroundColor(getResources().getColor(R.color.greybackground));
//                hotDealsTextView.setTextColor(getResources().getColor(R.color.black));
//                hotDealsImageView.setImageResource(R.drawable.hot_deals_grey);
//                break;

            case EXCLUDE_OF_STOCK:

                excludeOfStockButtonLinearLayout.setBackgroundColor(getResources().getColor(R.color.greybackground));
                excludeOfStockTextView.setTextColor(getResources().getColor(R.color.black));
                excludeOfStockImageView.setImageResource(R.drawable.exclude_out_of_stock_grey);
                break;


        }


    }





    public void makeVisibleSelectButtonContent(String text){

        makeAllContentInvisible();
        switch(text){

            case SUB_CATEGORIES:

                fragmentRootView.findViewById(R.id.msg_and_cat_list_complete_layout).setVisibility(View.VISIBLE);


                break;

//            case HOT_DEALS:
//                fragmentRootView.findViewById(R.id.hot_deals_select_content_linear_layout).setVisibility(View.VISIBLE);
//                break;

            case EXCLUDE_OF_STOCK:
                fragmentRootView.findViewById(R.id.exclude_out_of_stock_content_linear_layout).setVisibility(View.VISIBLE);
                break;


        }


    }


    public void makeAllButtonBlack(){
        subCategoriesButtonLinearLayout.setBackgroundColor(getResources().getColor(R.color.filter_left_panel_color));
        //hotDealsButtonLinearLayout.setBackgroundColor(getResources().getColor(R.color.filter_left_panel_color));
        excludeOfStockButtonLinearLayout.setBackgroundColor(getResources().getColor(R.color.filter_left_panel_color));
    }


    public void makeAllContentInvisible(){
        fragmentRootView.findViewById(R.id.msg_and_cat_list_complete_layout).setVisibility(View.GONE);
        //  fragmentRootView.findViewById(R.id.category_filter_msg_linear_layout).setVisibility(View.GONE);
        fragmentRootView.findViewById(R.id.exclude_out_of_stock_content_linear_layout).setVisibility(View.GONE);
    }

    public void makeAllTextWhite(){
        subCategoriesTextView.setTextColor(getResources().getColor(R.color.whiteColor));
        //hotDealsTextView.setTextColor(getResources().getColor(R.color.whiteColor));
        excludeOfStockTextView.setTextColor(getResources().getColor(R.color.whiteColor));
    }

    public void makeAllImageWhite(){
        subCategoriesImageView.setImageResource(R.drawable.sub_categories_white);
        //hotDealsImageView.setImageResource(R.drawable.hot_deal_white);
        excludeOfStockImageView.setImageResource(R.drawable.exclude_out_of_stock_white);
    }



    private void fetchSubCat1SubCat2() {
        fetchCategoriesFromStorage();
    }

    public void fetchCategoriesFromStorage(){
        String url=getSubCat1SubCat2Url();
        fetchCategoriesFromRemoteServer(url);
    }

    public String getSubCat1SubCat2Url(){
        //  Log.e("sub cat 1 2 ", UrlsRetail.SUB_CAT1_CAT2 + "/" + getFilterId());
        if(getSavedApi().equals(UrlsRetail.SUPER_CAT)) {
            //Log.e("sub cat 1 2 ", UrlsRetail.SUB_CAT1_CAT2 + "/" + getFilterId()+"/"+1);
            return UrlsRetail.SUB_CAT1_CAT2 + "/" + getFilterId()+"/"+1;
        }else{
            // Log.e("sub cat 1 2 ", UrlsRetail.SUB_CAT1_CAT2 + "/" + getFilterId()+"/"+2);
            return UrlsRetail.SUB_CAT1_CAT2 + "/" + getFilterId()+"/"+2;
        }
    }

    public String getFilterId(){
        SharedPreferences settings = getActivity().getSharedPreferences(UrlsRetail.SAVED_BOOK_CATEGORY_DETAILS, getActivity().MODE_PRIVATE);
        return settings.getString(UrlsRetail.SAVED_FILTER_ID,"");
    }

    public String getSavedApi(){
        SharedPreferences settings = getActivity().getSharedPreferences(UrlsRetail.SAVED_BOOK_CATEGORY_DETAILS, getActivity().MODE_PRIVATE);
        return settings.getString(UrlsRetail.SAVED_API,"");
    }

    private void fetchCategoriesFromRemoteServer(String urls) {
        JsonObjectRequest jsonObjectRequest;
        try {
            jsonObjectRequest = getCategoriesJsonRequest(urls);
            jsonObjectRequest.setTag(UrlsRetail.SUB_CAT1_CAT2_VOLLEY_REQUEST_TAG);
            VolleySingleton.getInstance(getActivity()).addToRequestQueue(jsonObjectRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private JsonObjectRequest getCategoriesJsonRequest(String url) throws JSONException {

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url, getCategoriesResponseListener(), new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error instanceof NoConnectionError) {
                    (fragmentRootView.findViewById(R.id.category_filter_msg_linear_layout)).setVisibility(View.VISIBLE);
                    (fragmentRootView.findViewById(R.id.sub_categories_content_linear_layout)).setVisibility(View.GONE);
                    (fragmentRootView.findViewById(R.id.exclude_out_of_stock_content_linear_layout)).setVisibility(View.GONE);
                    ((TextView)fragmentRootView.findViewById(R.id.category_filter_msg)).setText(R.string.no_internet_connection_msg);
                }else{
                    (fragmentRootView.findViewById(R.id.category_filter_msg_linear_layout)).setVisibility(View.VISIBLE);
                    (fragmentRootView.findViewById(R.id.sub_categories_content_linear_layout)).setVisibility(View.GONE);
                    (fragmentRootView.findViewById(R.id.exclude_out_of_stock_content_linear_layout)).setVisibility(View.GONE);
                    ((TextView)fragmentRootView.findViewById(R.id.category_filter_msg)).setText(R.string.error_in_fetching_sub_categories);
                }
                hideLoadingScreen();
            }
        });

        jsonRequest.setRetryPolicy(new DefaultRetryPolicy(
                20000,
                DefaultMaxRetries.APP_API_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        return jsonRequest;
    }


    @NonNull
    private Response.Listener<JSONObject> getCategoriesResponseListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject fetchedJson = new JSONObject(response.toString());
                    //Log.e("sub cat 1 2 categories", fetchedJson.toString());
                    createCategoryList(fetchedJson);
                    hideLoadingScreen();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    public void hideLoadingScreen(){
        fragmentRootView.findViewById(R.id.filter_page_loading_screen).setVisibility(View.GONE);
        fragmentRootView.findViewById(R.id.filter_page_content).setVisibility(View.VISIBLE);
    }

    public void createCategoryList(JSONObject fetchedJson){

        // Log.e("result ",fetchedJson.toString());

        List<String> dummyList;
        List<Integer> dummyListId;

        try {
            JSONArray List = fetchedJson.getJSONArray("data");

            if(List.length()==0){
                (fragmentRootView.findViewById(R.id.category_filter_msg_linear_layout)).setVisibility(View.VISIBLE);
                (fragmentRootView.findViewById(R.id.sub_categories_content_linear_layout)).setVisibility(View.GONE);
                (fragmentRootView.findViewById(R.id.exclude_out_of_stock_content_linear_layout)).setVisibility(View.GONE);
                (fragmentRootView.findViewById(R.id.category_filter_retry_button)).setVisibility(View.GONE);
                ((TextView)fragmentRootView.findViewById(R.id.category_filter_msg)).setText(R.string.no_sub_cat_text);
                return;
            }

            for (int i=0; i<List.length(); i++) {
                JSONObject jsonObject = List.getJSONObject(i);

                allSubCatsListDataHeader.add(jsonObject.getString("category"));
                allSubCatsListDataHeaderId.add(Integer.parseInt(jsonObject.getString("catID1")));

                dummyList=new ArrayList<>();
                dummyListId=new ArrayList<>();

                dummyList.add("All");
                dummyListId.add(Integer.parseInt(jsonObject.getString("catID1")));

                JSONArray subList = jsonObject.getJSONArray("subCategory");
                for(int j=0;j<subList.length();j++){
                    JSONObject subJsonObject = subList.getJSONObject(j);

                    dummyList.add(subJsonObject.getString("category"));
                    dummyListId.add(Integer.parseInt(subJsonObject.getString("catID2")));
                }

                allSubCatsListDataChild.put(jsonObject.getString("category"),dummyList);
                allSubCatsListDataChildId.put(Integer.parseInt(jsonObject.getString("catID1")),dummyListId);

            }

            setUpExpandableListView();

            (fragmentRootView.findViewById(R.id.category_filter_msg_linear_layout)).setVisibility(View.GONE);
            (fragmentRootView.findViewById(R.id.sub_categories_content_linear_layout)).setVisibility(View.VISIBLE);
            (fragmentRootView.findViewById(R.id.exclude_out_of_stock_content_linear_layout)).setVisibility(View.GONE);
            (fragmentRootView.findViewById(R.id.category_filter_retry_button)).setVisibility(View.GONE);

        } catch (JSONException e) {
            //Log.e("error",e.toString());
            e.printStackTrace();
        }

    }


}
