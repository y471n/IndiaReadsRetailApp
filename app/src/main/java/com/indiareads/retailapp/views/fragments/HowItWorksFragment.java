package com.indiareads.retailapp.views.fragments;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.indiareads.retailapp.R;
import com.indiareads.retailapp.adapters.BannerPagerAdapter;
import com.indiareads.retailapp.adapters.HowItWorksBannerAdapter;

import java.util.ArrayList;
import java.util.List;


public class HowItWorksFragment extends Fragment  {


    View fragmentRootView;

    public HowItWorksBannerAdapter bannerPagerAdapter;
    int[] bannerUrls;

    private ViewPager mViewPager;
    private final static int NUM_PAGES = 6;
    private List<ImageView> dots;


    TextView bannerText1,bannerText2,bannerText3;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        fragmentRootView=inflater.inflate(R.layout.fragment_how_it_works, container, false);

//        mViewPager = (ViewPager) fragmentRootView.findViewById(R.id.how_it_works_banner_viewpager);
//        setBanner();
//        setBannerListeners();
//        fetchViewsFromXml();
//        addDots();
//        selectDot(0);



        return fragmentRootView;

    }


//    public void addDots() {
//        dots = new ArrayList<>();
//        LinearLayout dotsLayout = (LinearLayout)fragmentRootView.findViewById(R.id.dots_linear_layout);
//
//        for(int i = 0; i < NUM_PAGES; i++) {
//            ImageView dot = new ImageView(getActivity());
//            //  View view = getActivity().getLayoutInflater().inflate(R.layout.dot_image_view, null);
//            //   dot=(ImageView) view.findViewById(R.id.dot_image);
//
//            dot.setImageDrawable(getResources().getDrawable(R.drawable.white));
//            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//            lp.setMargins(5, 0, 0, 0);
//            dot.setLayoutParams(lp);
//
//            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
//                    LinearLayout.LayoutParams.WRAP_CONTENT,
//                    LinearLayout.LayoutParams.WRAP_CONTENT
//            );
//            dotsLayout.addView(dot,params);
//
//            dots.add(dot);
//        }
//
//        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//                selectDot(position);
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//            }
//        });
//    }
//
//    public void selectDot(int idx) {
//        Resources res = getResources();
//        for(int i = 0; i < NUM_PAGES; i++) {
//            int drawableId = (i==idx)?(R.drawable.black):(R.drawable.white);
//            Drawable drawable = res.getDrawable(drawableId);
//            dots.get(i).setImageDrawable(drawable);
//        }
//    }
//
//    public void fetchViewsFromXml(){
//        bannerText1=(TextView)fragmentRootView.findViewById(R.id.how_it_works_text1);
//        bannerText2=(TextView)fragmentRootView.findViewById(R.id.how_it_works_text2);
//        bannerText3=(TextView)fragmentRootView.findViewById(R.id.how_it_works_text3);
//    }
//
//    public void setBannerListeners(){
//        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//                changeTextAccordingToHowItWorksBanner(position);
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//
//            }
//        });
//    }
//
//    public void setBanner(){
//        setBannerImageName();
//        bannerPagerAdapter = new HowItWorksBannerAdapter(getActivity(), bannerUrls,fragmentRootView);
//        mViewPager.setAdapter(bannerPagerAdapter);
//    }
//
//    public void setBannerImageName(){
//        bannerUrls = new int[6];
//        bannerUrls[0] = R.drawable.hiw_banner_1;
//        bannerUrls[1] = R.drawable.hiw_banner_2;
//        bannerUrls[2] = R.drawable.hiw_banner_3;
//        bannerUrls[3] = R.drawable.hiw_banner_4;
//        bannerUrls[4] = R.drawable.hiw_banner_5;
//        bannerUrls[5] = R.drawable.hiw_banner_6;
//
//    }
//
//    public void changeTextAccordingToHowItWorksBanner(int position){
//
//        switch(position){
//
//            case 0:
//                              setTextOfHowItWorksBanner1();
//                break;
//
//            case 1:           setTextOfHowItWorksBanner2();
//
//                break;
//
//            case 2:           setTextOfHowItWorksBanner3();
//
//                break;
//
//            case 3:           setTextOfHowItWorksBanner4();
//
//                break;
//
//            case 4:           setTextOfHowItWorksBanner5();
//
//                break;
//
//            case 5:           setTextOfHowItWorksBanner6();
//
//                break;
//
//        }
//
//    }
//
//
//    public void setTextOfHowItWorksBanner1(){
//
//        bannerText1.setText("Choose your favorite books from 1.8 lac titles and 45+ categories");
//        bannerText2.setText("Search by Title, Author name, ISBN");
//        bannerText3.setText("Read what others are reading");
//
//    }
//
//    public void setTextOfHowItWorksBanner2(){
//        bannerText1.setText("Add your favorite book into cart");
//        bannerText2.setText("Choose your delivery address");
//        bannerText3.setText("Pay Initial Payable");
//
//    }
//
//    public void setTextOfHowItWorksBanner3(){
//        bannerText1.setText("Your chosen books will be delivered at your doorstep within 2-3 business days");
//        bannerText2.setText("Keep the spare packet and book with you");
//        bannerText3.setText("Enjoy reading your book");
//
//    }
//
//    public void setTextOfHowItWorksBanner4(){
//
//        bannerText1.setText("Choose your favorite books from 1.8 lac titles and 45+ categories");
//        bannerText2.setText("Search by Title, Author name, ISBN");
//        bannerText3.setText("Read what others are reading");
//    }
//
//    public void setTextOfHowItWorksBanner5(){
//        bannerText1.setText("Done Reading? Click on the Return Books button");
//        bannerText2.setText("Your refund amount will be transferred in your wallet, which can be used to order new book or can be transferred in your bank account");
//        bannerText3.setText("Slip-in your book in the spare packet you received at the time of delivery and handover the packet to pickup person");
//
//    }
//
//    public void setTextOfHowItWorksBanner6(){
//        bannerText1.setText("Other books are waiting, pick up your next book");
//        bannerText2.setText("Pay through your store credits or choose Cash on Delivery");
//        bannerText3.setText("Your next books is delivered, sit and enjoy reading");
//
//    }

}
