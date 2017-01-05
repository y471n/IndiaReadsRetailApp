package com.indiareads.retailapp.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.indiareads.retailapp.R;
import com.indiareads.retailapp.singleton.VolleySingleton;

public class BannerPagerAdapter extends PagerAdapter {

    LayoutInflater inflater;
    Context context;
    String[] photoUrls;
    private ImageView mImageView;
    View fragmentRootView;

    public BannerPagerAdapter(Context context, String[] photoUrls, View fragmentRootView) {
        this.context = context;
        this.photoUrls = photoUrls;
        this.fragmentRootView=fragmentRootView;

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

        if(context==null){
            return null;
        }

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
       final  View itemView = inflater.inflate(R.layout.fragment_banner, container,
                false);

        mImageView = (ImageView) itemView.findViewById(R.id.banner_image);

   //     Got Url; Save Image
        ImageRequest request = new ImageRequest(photoUrls[position],
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap bitmap) {


                        ((ImageView) itemView.findViewById(R.id.banner_image)).setImageBitmap(bitmap);
                        fragmentRootView.findViewById(R.id.banner_loading_screen).setVisibility(View.GONE);

                    }
                }, 600, 600, null, null,
                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        // Some error occurred
//                        ((ImageView) itemView.findViewById(R.id.banner_image))
//                                .setImageResource(R.drawable.logo);
                        fragmentRootView.findViewById(R.id.banner_loading_screen).setVisibility(View.GONE);
                    }
                });
        VolleySingleton.getInstance(context).addToRequestQueue(request);

        container.addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        final View view = (View) object;
        container.removeView(view);
    }


}
