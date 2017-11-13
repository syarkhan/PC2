package com.example.sheryarkhan.projectcity.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.sheryarkhan.projectcity.Glide.GlideApp;
import com.example.sheryarkhan.projectcity.R;
import com.example.sheryarkhan.projectcity.utils.HelperFunctions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Sheryar Khan on 9/13/2017.
 */

public class ShareNewsMediaViewPagerAdapter extends PagerAdapter {


    Context context;
    //List<String> media = Collections.emptyList(); a
    Map<Integer,ArrayList<String>> hashMapMedia;
    LayoutInflater layoutInflater;
    Activity activity;
    List<View> mList;
    //private Bitmap bitmap;


    public ShareNewsMediaViewPagerAdapter(Context context, Map<Integer,ArrayList<String>> hashMapMedia) {
        this.context = context;
        this.hashMapMedia = hashMapMedia;
        layoutInflater = LayoutInflater.from(context);
        activity = (Activity) context;
        mList = new ArrayList<>();
        //mList.addAll(hashMapMedia.values());

    }

    public void addView(View view, int index) {
        mList.add(index, view);
        notifyDataSetChanged();
    }

    public void removeView(int index) {
        mList.remove(index);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {

        if(hashMapMedia.size() == 0)
        {
            return 0;
        }
        return hashMapMedia.size();
    }

    @Override
    public int getItemPosition(Object object) {
        if (mList.contains((View) object)) {
            return mList.indexOf((View) object);
        } else {
            return POSITION_NONE;
        }
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(final ViewGroup container, final int position) {
        final View itemView = layoutInflater.inflate(R.layout.media_viewpager_item,container,false);
        final ImageView imageView = (ImageView) itemView.findViewById(R.id.postMediaImageView);
        final ImageView imgClearMedia = (ImageView) itemView.findViewById(R.id.imgClearMedia);
        final ImageView imgVideoPlayButton = (ImageView)itemView.findViewById(R.id.imgVideoPlayButton);
        mList.add(itemView);
//        bitmap = BitmapFactory.decodeFile(hashMapMedia.get(position));
//        imageView.setImageBitmap(bitmap);
//        imageView.setAdjustViewBounds(true);

//        imgClearMedia.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //container.removeView(imageView);
//
//                container.removeView(itemView);
//                removeView(position);
//                HelperFunctions.getToastShort(context,"hello");
//            }
//        });

if((new ArrayList<>(hashMapMedia.values()).get(position).get(0).equals("2"))){
    imgVideoPlayButton.setVisibility(View.VISIBLE);
}

        try {
            GlideApp.with(context)
                    .load((new ArrayList<>(hashMapMedia.values()).get(position).get(1)))
                    .fitCenter()
                    .transition(DrawableTransitionOptions.withCrossFade(1000))
                    .error(R.color.link)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            Log.d("glidefailed",e.toString());
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            imageView.setImageDrawable(resource);
                            return false;
                        }

                    }).into(imageView);
//                    .into(imageView);
//                    .into(new SimpleTarget<Drawable>() {
//                        @Override
//                        public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
//                            imageView.setImageDrawable(resource);
//                        }
//
//                        @Override
//                        public void onLoadFailed(@Nullable Drawable errorDrawable) {
//                            super.onLoadFailed(errorDrawable);
//                            //Toast.makeText(context,"image error "+errorDrawable.toString(),Toast.LENGTH_LONG).show();
//                            imageView.setImageDrawable(errorDrawable);
//                        }
//                    });
        } catch (Exception ex) {
            Log.d("errorglide", ex.toString());
        }

        container.addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
