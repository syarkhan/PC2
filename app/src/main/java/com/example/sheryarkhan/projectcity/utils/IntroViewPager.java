package com.example.sheryarkhan.projectcity.utils;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Irfan on 11/30/2017.
 */

public class IntroViewPager extends PagerAdapter
{

    private Context context;
    private int[] layouts;
    private LayoutInflater layoutInflater;

    public IntroViewPager(Context context, int[] layouts) {
        this.context = context;
        this.layouts = layouts;

        layoutInflater = LayoutInflater.from(context);
    }




    @Override
    public int getCount() {
        return layouts.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = layoutInflater.inflate(layouts[position],container,false);
        container.addView(itemView);
        return  itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
