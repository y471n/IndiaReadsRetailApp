package com.indiareads.retailapp.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.indiareads.retailapp.R;


/**
 * Created by vijay on 10/6/2015.
 */
public class HowItWorksBannerAdapter extends PagerAdapter {

    LayoutInflater inflater;
    Context context;
    int[] photoUrls;
    private ImageView mImageView;

    public HowItWorksBannerAdapter(Context context, int[] photoUrls, View fragmentRootView) {
        this.context = context;
        this.photoUrls = photoUrls;
    }

    @Override
    public int getCount() {
        return photoUrls.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((LinearLayout)object);
    }

    @Override
    public Object instantiateItem(final ViewGroup container, int position) {

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.fragment_banner, container,
                false);

        mImageView = (ImageView) itemView.findViewById(R.id.banner_image);
        mImageView.setImageResource(photoUrls[position]);

    //    int id = context.getResources().getIdentifier("drawable/" + photoUrls[position], null, null);

    //    mImageView.setImageResource(id);


        container.addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        final View view = (View) object;
        container.removeView(view);
    }


}
