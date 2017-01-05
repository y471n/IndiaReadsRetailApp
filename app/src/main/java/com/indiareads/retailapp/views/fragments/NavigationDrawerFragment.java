package com.indiareads.retailapp.views.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.indiareads.retailapp.R;
import com.indiareads.retailapp.adapters.ExpandableListAdapter;
import com.indiareads.retailapp.models.Categories;
import com.indiareads.retailapp.utils.UrlsRetail;
import com.indiareads.retailapp.views.activities.CategoryListingActivity;
import com.indiareads.retailapp.views.activities.ContactUsActivity;
import com.indiareads.retailapp.views.activities.HowItWorksActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class NavigationDrawerFragment extends Fragment {

    View fragmentRootView;
    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;

    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;

    List<Integer> listDataHeaderId;
    HashMap<Integer, List<Integer>> listDataChildId;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        fragmentRootView=inflater.inflate(R.layout.fragment_navigation_drawer, container, false);


        setUpExpandableListView();
        setHowItWorksClickListener();
        setMakeCallLinearLayout();


        return fragmentRootView;
    }


    private void setMakeCallLinearLayout() {
        fragmentRootView.findViewById(R.id.make_call_linearLayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getActivity(), ContactUsActivity.class);
                startActivity(i);
            }
        });
    }


    private void setHowItWorksClickListener() {
        fragmentRootView.findViewById(R.id.how_it_works_linearLayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(getActivity(), HowItWorksActivity.class);
                startActivity(intent);
            }
        });
    }


    public void  setUpExpandableListView(){

        expListView = (ExpandableListView) fragmentRootView.findViewById(R.id.category_list);
        prepareListData();
        listAdapter = new ExpandableListAdapter(getActivity(), listDataHeader, listDataChild);
        expListView.setAdapter(listAdapter);

        setExpandableListViewClickListener();
    }

    private void setExpandableListViewClickListener() {


        setChildClickListener();

        setGroupClickListener();

    }

    public void setChildClickListener(){
        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Intent intent = new Intent(getActivity(), CategoryListingActivity.class);
                if (listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition).equals("All")) {
                    saveCategoryIdAndApiToSharedPreferences(listDataChildId.get(listDataHeaderId.get(groupPosition)).get(childPosition), UrlsRetail.SUPER_CAT);
                    intent.putExtra("CategoryName",listDataHeader.get(groupPosition));
                } else {
                    saveCategoryIdAndApiToSharedPreferences(listDataChildId.get(listDataHeaderId.get(groupPosition)).get(childPosition), UrlsRetail.PARENT_CAT);
                    intent.putExtra("CategoryName",listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition));
                }


                startActivity(intent);

                return false;
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


    public void setGroupClickListener(){

        expListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {


                return false;
            }
        });
    }


    private void prepareListData() {
        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();

        listDataHeaderId=new ArrayList<>();
        listDataChildId=new HashMap<>();

        listDataHeader= Categories.getSuperCategoryList();
        listDataHeaderId= Categories.getSuperCategoryListId();


        listDataChild.put(listDataHeader.get(0), Categories.getLiteratureList());// Header, Child data
        listDataChildId.put(listDataHeaderId.get(0), Categories.getLiteratureListId());

        listDataChild.put(listDataHeader.get(1), Categories.getFictionList());
        listDataChildId.put(listDataHeaderId.get(1), Categories.getFictionListId());

        listDataChild.put(listDataHeader.get(2), Categories.getChildrenAndTeens());
        listDataChildId.put(listDataHeaderId.get(2), Categories.getChildrenAndTeensId());

        listDataChild.put(listDataHeader.get(3), Categories.getBoardExamPrep());
        listDataChildId.put(listDataHeaderId.get(3), Categories.getBoardExamPrepId());

        listDataChild.put(listDataHeader.get(4), Categories.getTechnologyAndEngineering());
        listDataChildId.put(listDataHeaderId.get(4), Categories.getTechnologyAndEngineeringId());

        listDataChild.put(listDataHeader.get(5), Categories.getNonFiction());
        listDataChildId.put(listDataHeaderId.get(5), Categories.getNonFictionId());

        listDataChild.put(listDataHeader.get(6), Categories.getBusinessAndEconomics());
        listDataChildId.put(listDataHeaderId.get(6), Categories.getBusinessAndEconomicsId());


        listDataChild.put(listDataHeader.get(7), Categories.getOtherTextBooks());
        listDataChildId.put(listDataHeaderId.get(7), Categories.getOtherTextBooksId());


    }

}
