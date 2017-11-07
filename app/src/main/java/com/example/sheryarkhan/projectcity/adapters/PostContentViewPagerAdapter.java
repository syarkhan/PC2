package com.example.sheryarkhan.projectcity.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sheryarkhan.projectcity.R;
import com.example.sheryarkhan.projectcity.utils.ImagesViewPager;

import java.util.Collections;
import java.util.Map;

/**
 * Created by Sheryar Khan on 8/7/2017.
 */

public class PostContentViewPagerAdapter extends PagerAdapter {



    private Context context; //aa
    private Map<String, Boolean> media = Collections.emptyMap();
    private LayoutInflater layoutInflater;
    private Activity activity;


    public PostContentViewPagerAdapter(Context context, Map<String, Boolean> media) {
        this.context = context;
        this.media = media;
        layoutInflater = LayoutInflater.from(context);
        activity = (Activity) context;

    }

    @Override
    public int getCount() {
        try {
            return media.size();
        }catch (Exception ex)
        {

        }
        return 0;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = layoutInflater.inflate(R.layout.viewpager_item,container,false);
        ImagesViewPager imagesViewPager; //ONLY IMAGES
        imagesViewPager = (ImagesViewPager) itemView.findViewById(R.id.imagesViewPager);
        imagesViewPager.Init(context, media, position, itemView);

        //VideosViewPager videosViewPager; //ONLY VIDEOS
        //videosViewPager = (VideosViewPager) itemView.findViewById(R.id.videosViewPager);

//        if(new ArrayList<>(media.keySet()).get(position).contains("image")){
//
//            videosViewPager.setVisibility(View.GONE);
//            imagesViewPager.Init(context, media, position, itemView);
//        }
//        else if(new ArrayList<>(media.keySet()).get(position).contains("video")){

            //imagesViewPager.setVisibility(View.GONE);
            //videosViewPager.Init(context,media,position,itemView);
        //}

//        VideosViewPager videosViewPager;
//        videosViewPager = (VideosViewPager) itemView.findViewById(R.id.videosViewPager);


//
//        if(position == 2) {
//
//
//            //
//
//        }
//        else{



        //}




        //imageView = (ImageView)itemView.findViewById(R.id.mediaAndVideosViewPager);
        //magesAndVideosViewPager.setUpImage(mediaAndVideosViewPager);
//        videoView = (VideoView)itemView.findViewById(R.id.videoView);
//        DisplayMetrics displayMetrics = new DisplayMetrics();
//        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//        int height = displayMetrics.heightPixels;
//        int width = displayMetrics.widthPixels;

//        imageView.setMinimumHeight(height);
//        imageView.setMinimumWidth(width);
//
//        try{
//            Glide.with(context)
//                    .load(getImage(media[position]))
//                    .into(imageView);
//        }catch (Exception ex)
//        {
//            Log.d("error",ex.toString());
//        }

        container.addView(itemView);


        return itemView;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }


}
