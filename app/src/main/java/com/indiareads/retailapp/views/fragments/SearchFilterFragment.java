package com.indiareads.retailapp.views.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.indiareads.retailapp.R;
import com.indiareads.retailapp.adapters.FilterSearchExpandableListAdapter;
import com.indiareads.retailapp.models.Categories;
import com.indiareads.retailapp.views.activities.CategoryListingActivity;
import com.indiareads.retailapp.views.activities.SearchableActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class SearchFilterFragment{
//        extends Fragment {

//    View fragmentRootView;
//    FilterSearchExpandableListAdapter listAdapter;
//    ExpandableListView expListView;
//    List<String> listDataHeader;
//    HashMap<String, List<String>> listDataChild;
//
//    LinearLayout categoriesContentLinearLayout,excludeOfStockContentLinearLayout;
//
//    LinearLayout categoriesButtonLinearLayout,excludeOfStockButtonLinearLayout;
//
//    TextView categoriesTextView,excludeOfStockTextView;
//
//    ImageView categoriesImageView,excludeOfStockImageView;
//
//    public static final String SUB_CATEGORIES="sub_categories";
//    public static final String EXCLUDE_OF_STOCK="exclude_of_stock";



//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
////        // Inflate the layout for this fragment
//        fragmentRootView=inflater.inflate(R.layout.fragment_search_filter, container, false);
////
////        setUpExpandableListView();
////
////        fetchViewsFromXml();
////
////        setClickListeners();
//
//        return fragmentRootView;
//
//
//    }

//    private void fetchViewsFromXml(){
//        categoriesContentLinearLayout=(LinearLayout) fragmentRootView.findViewById(R.id.categories_content_linear_layout);
//        excludeOfStockContentLinearLayout=(LinearLayout) fragmentRootView.findViewById(R.id.exclude_out_of_stock_content_linear_layout);
//
//        categoriesButtonLinearLayout=(LinearLayout) fragmentRootView.findViewById(R.id.categories_button_linear_layout);
//        excludeOfStockButtonLinearLayout=(LinearLayout) fragmentRootView.findViewById(R.id.exclude_of_stocks_button_linear_layout);
//
//        categoriesTextView=(TextView) fragmentRootView.findViewById(R.id.category_textview);
//        excludeOfStockTextView=(TextView) fragmentRootView.findViewById(R.id.exclude_of_stock_textview);
//
//        categoriesImageView=(ImageView) fragmentRootView.findViewById(R.id.category_imageview);
//        excludeOfStockImageView=(ImageView) fragmentRootView.findViewById(R.id.exclude_of_stock_imageview);
//
//
//
//    }
//
//    public void setClickListeners(){
//        setCategoriesButtonClickListener();
//        setExcludeOutOfStockButtonClickListener();
//
//        setApplyButtonClickListener();
//    }
//
//    public void  setUpExpandableListView(){
//
//        expListView = (ExpandableListView) fragmentRootView.findViewById(R.id.search_filter_page_category_list);
//        prepareListData();
//        listAdapter = new FilterSearchExpandableListAdapter(getActivity(), listDataHeader, listDataChild);
//        expListView.setAdapter(listAdapter);
//
//        setExpandableListViewClickListener();
//    }
//
//    private void prepareListData() {
//        listDataHeader = new ArrayList<>();
//        listDataChild = new HashMap<>();
//
//        listDataHeader= Categories.getSuperCategoryList();
//
//        List<String> emptyList=new ArrayList<String>();
//
//        listDataChild.put(listDataHeader.get(0), emptyList);// Header, Child data
//        listDataChild.put(listDataHeader.get(1), emptyList);
//        listDataChild.put(listDataHeader.get(2), emptyList);
//        listDataChild.put(listDataHeader.get(3), emptyList);
//        listDataChild.put(listDataHeader.get(4), emptyList);
//        listDataChild.put(listDataHeader.get(5), emptyList);
//        listDataChild.put(listDataHeader.get(6), emptyList);
//        listDataChild.put(listDataHeader.get(7), emptyList);
//
//    }
//
//    private void setExpandableListViewClickListener() {
//
//        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
//
//            @Override
//                public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
//
//                    Intent intent = new Intent(getActivity(), CategoryListingActivity.class);
//                    startActivity(intent);
//
//                    return false;
//            }
//        });
//
//
//
//        expListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
//            @Override
//            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
//
//               ImageView selectCategoryImageView=(ImageView)v.findViewById(R.id.select_category_image_view);
//
//                if(selectCategoryImageView.getTag()=="checked"){
//                    selectCategoryImageView.setBackgroundResource(R.drawable.unchecked);
//                    selectCategoryImageView.setTag("unchecked");
//                }else{
//                    selectCategoryImageView.setBackgroundResource(R.drawable.checked);
//                    selectCategoryImageView.setTag("checked");
//                }
//
//                return false;
//            }
//        });
//
//
//    }
//
//
//
//    private void setCategoriesButtonClickListener() {
//        categoriesButtonLinearLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                changeColorOfSelectButton(SUB_CATEGORIES);
//                makeVisibleSelectButtonContent(SUB_CATEGORIES);
//            }
//        });
//    }
//
//
//    private void setExcludeOutOfStockButtonClickListener() {
//        excludeOfStockButtonLinearLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                changeColorOfSelectButton(EXCLUDE_OF_STOCK);
//                makeVisibleSelectButtonContent(EXCLUDE_OF_STOCK);
//
//
//            }
//        });
//    }
//
//    private void setApplyButtonClickListener() {
//        fragmentRootView.findViewById(R.id.apply_button).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//
//                Intent intent = new Intent(getActivity(), SearchableActivity.class);
//                startActivity(intent);
//
//
//            }
//        });
//    }
//
//    public void changeColorOfSelectButton(String text){
//
//        makeAllButtonBlack();
//        makeAllTextWhite();
//        makeAllImageWhite();
//        switch(text){
//
//
//
//            case SUB_CATEGORIES:
//
//                categoriesButtonLinearLayout.setBackgroundColor(getResources().getColor(R.color.greybackground));
//                categoriesTextView.setTextColor(getResources().getColor(R.color.black));
//                categoriesImageView.setImageResource(R.drawable.sub_categories_grey);
//                break;
//
//
//            case EXCLUDE_OF_STOCK:
//
//                excludeOfStockButtonLinearLayout.setBackgroundColor(getResources().getColor(R.color.greybackground));
//                excludeOfStockTextView.setTextColor(getResources().getColor(R.color.black));
//                excludeOfStockImageView.setImageResource(R.drawable.exclude_out_of_stock_grey);
//                break;
//
//
//        }
//
//
//    }
//
//
//
//
//
//    public void makeVisibleSelectButtonContent(String text){
//
//        makeAllContentInvisible();
//        switch(text){
//
//            case SUB_CATEGORIES:
//
//                categoriesContentLinearLayout.setVisibility(View.VISIBLE);
//
//                break;
//
//
//            case EXCLUDE_OF_STOCK:
//
//                excludeOfStockContentLinearLayout.setVisibility(View.VISIBLE);
//                break;
//
//
//        }
//
//
//    }
//
//
//    public void makeAllButtonBlack(){
//        categoriesButtonLinearLayout.setBackgroundColor(getResources().getColor(R.color.filter_left_panel_color));
//        excludeOfStockButtonLinearLayout.setBackgroundColor(getResources().getColor(R.color.filter_left_panel_color));
//    }
//
//
//    public void makeAllContentInvisible(){
//        categoriesContentLinearLayout.setVisibility(View.GONE);
//        excludeOfStockContentLinearLayout.setVisibility(View.GONE);
//    }
//
//    public void makeAllTextWhite(){
//        categoriesTextView.setTextColor(getResources().getColor(R.color.whiteColor));
//        excludeOfStockTextView.setTextColor(getResources().getColor(R.color.whiteColor));
//    }
//
//    public void makeAllImageWhite(){
//        categoriesImageView.setImageResource(R.drawable.sub_categories_white);
//        excludeOfStockImageView.setImageResource(R.drawable.exclude_out_of_stock_white);
//    }
//

}
