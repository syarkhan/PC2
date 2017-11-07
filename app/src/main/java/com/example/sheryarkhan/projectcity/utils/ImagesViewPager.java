package com.example.sheryarkhan.projectcity.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.sheryarkhan.projectcity.Glide.GlideApp;
import com.example.sheryarkhan.projectcity.R;
import com.example.sheryarkhan.projectcity.activities.PlayVideoActivity;
import com.example.sheryarkhan.projectcity.activities.PostImageDisplayActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sheryar Khan on 8/27/2017.
 */


//android.support.v7.widget.AppCompatImageView

public class ImagesViewPager extends android.support.v7.widget.AppCompatImageView{


    Context context;
    Map<String, Boolean> media;
    int position;
    Activity activity;
    ImagesViewPager imagesViewPager;
    ImageView playImageView;
    View view;
    private StorageReference storageReference;


//    public ImagesViewPager()
//    {
//
//    }

    public ImagesViewPager(Context context) {
        super(context);
        this.context = context;
    }

    public ImagesViewPager(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ImagesViewPager(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }



    public void Init(Context context, Map<String, Boolean> media, int position, View view) {


        this.context = context;
        this.media = media;
        this.position = position;
        activity = (Activity) context;
        this.view = view;
        imagesViewPager = (ImagesViewPager)findViewById(R.id.imagesViewPager);
        playImageView = (ImageView)view.findViewById(R.id.play_video_image);
        imagesViewPager.setTransitionName("image");
        storageReference = FirebaseStorage.getInstance().getReference();
        Log.d("imagesdada",media.toString()+" "+position);
        setUpImage();
        //imagesPager = (ImagesViewPager) view.findViewById(R.id.imagesViewPager);
        //imagesPager = new Images(context);
        //videosPager = new Videos(context);
//        if(position == 3)
//        {
//            videosPager.setUpVideo();
//        }
//        imagesPager.setUpImage();
        //LinearLayout linearLayout = (LinearLayout)view.findViewById(R.id.linearLayout);
        //imagesPager = (Images) view.findViewById(R.id.imagesViewPager);
//            videosPager=  (Videos) view.findViewById(R.id.imagesViewPager);

    }

    public void setUpImage() {


        //imageView = view.findViewById(R.id.imagesViewPager);
        //imageView = new ImageView(context);

        imagesViewPager.setVisibility(VISIBLE);
//        DisplayMetrics displayMetrics = new DisplayMetrics();
//        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//        int height = displayMetrics.heightPixels;
//        int width = displayMetrics.widthPixels;
//
//        imagesViewPager.setMinimumHeight(height);
//        imagesViewPager.setMinimumWidth(width);

//        final RequestOptions options = new RequestOptions();
//        options.diskCacheStrategy(DiskCacheStrategy.RESOURCE);

        if(new ArrayList<>(media.keySet()).get(position).contains("image")){
            //imagesViewPager.setClickable(true);

            StorageReference filePath = storageReference.child("images").child(new ArrayList<>(media.keySet()).get(position));
            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    try {
                        GlideApp.with(context)
                                .load(uri)
                                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                                .centerCrop()
                                .error(R.color.link)
                                .transition(DrawableTransitionOptions.withCrossFade(1000))
                                .into(imagesViewPager);
                    } catch (Exception ex) {
                        Log.d("error", ex.toString());
                    }
                }
            });
        }
        else if(new ArrayList<>(media.keySet()).get(position).contains("video")){
            playImageView.setVisibility(VISIBLE);
            //imagesViewPager.setBackgroundColor(getResources().getColor(R.color.black,null));
            /*   GlideApp.with(context)
                    .load(R.drawable.black_color)
                    .error(R.color.link)
                    .into(imagesViewPager);*/
           /* StorageReference filePath = storageReference.child("images").child(new ArrayList<>(media.keySet()).get(position));
            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Bitmap bitmap = retrieveThumbnailFromVideo(String.valueOf(uri));
                    Drawable thumbnail = new BitmapDrawable(getResources(),bitmap);

                    try {
                        GlideApp.with(context)
                                .load(thumbnail)
                                .override(100,100)
                                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                                .centerCrop()
                                .error(R.color.link)
                                .listener(new RequestListener<Drawable>() {
                                    @Override
                                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                        Log.d("imagegliderror",e.toString());
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                        return false;
                                    }
                                }).into(imagesViewPager);
                    } catch (Exception ex) {
                        Log.d("error", ex.toString());
                    }
                    if (bitmap != null && !bitmap.isRecycled()) {
                        bitmap.recycle();
                        bitmap = null;
                    }
                }
            });

*/        }


        playImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String urlVideo = new ArrayList<>(media.keySet()).get(position);
                Intent intent = new Intent(getContext(), PlayVideoActivity.class);
                intent.putExtra("url",urlVideo);
                context.startActivity(intent);
            }
        });

        imagesViewPager.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

//                ActivityOptionsCompat optionsCompat = ActivityOptionsCompat
//                        .makeSceneTransitionAnimation(context,imagesViewPager,"myImage");
//
//                context.startActivity(intent, optionsCompat.toBundle());

                if(new ArrayList<>(media.keySet()).get(position).contains("image")) {
                    String url = new ArrayList<>(media.keySet()).get(position);
                    Intent intent = new Intent(context, PostImageDisplayActivity.class);
                    intent.putExtra("url", url);
                    ActivityOptionsCompat options = ActivityOptionsCompat.
                            makeSceneTransitionAnimation((Activity) context,
                                    imagesViewPager,
                                    "image");
                    context.startActivity(intent, options.toBundle());
                }else if(new ArrayList<>(media.keySet()).get(position).contains("video")){
                    String urlVideo = new ArrayList<>(media.keySet()).get(position);
                    Intent intent = new Intent(getContext(), PlayVideoActivity.class);
                    intent.putExtra("url",urlVideo);
                    context.startActivity(intent);
                }

//                String url = new ArrayList<>(images.keySet()).get(position);
//                Toast.makeText(context,"Hi : "+new ArrayList<>(images.keySet()).get(position),Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(context, PostImageDisplayActivity.class);
//                intent.putExtra("url",url);
//                context.startActivity(intent);


            }
        });

//        try {
//            Glide.with(context)
//                    .load(getImage(images.get(position)))
//                    .into(imagesViewPager);
//        } catch (Exception ex) {
//            Log.d("error", ex.toString());
//        }



    }

    private static Bitmap retrieveThumbnailFromVideo(String videoPath)
    {
        Bitmap bitmap = null;
        MediaMetadataRetriever mediaMetadataRetriever = null;

        try{
            mediaMetadataRetriever = new MediaMetadataRetriever();
            if (Build.VERSION.SDK_INT >= 14)
                mediaMetadataRetriever.setDataSource(videoPath, new HashMap<String, String>());
            else
                mediaMetadataRetriever.setDataSource(videoPath);

            bitmap = mediaMetadataRetriever.getFrameAtTime(2000);
            //   mediaMetadataRetriever.setDataSource(videoPath);

            if (mediaMetadataRetriever != null) {
                mediaMetadataRetriever.release();
            }

        }catch ( Exception e){

        }
        return bitmap;
    }

    public int getImage(String imageName) {


        return context.getResources().getIdentifier(imageName, "drawable", context.getPackageName());
    }

//    public void Init(Context context, String[] images, int position, View view) {
//        //imagesViewPager = (ImagesViewPager) findViewById(R.id.imagesViewPager);
//        //video1 = (videoPager) findViewById(R.id.imagesViewPager);
//        this.context = context;
//        this.images = images;
//        this.position = position;
//        activity = (Activity) context;
//        this.view = view;
//        imagesPager = (Images) findViewById(R.id.imagesViewPager);
//        videosPager =  (Videos) findViewById(R.id.imagesViewPager);
//
//        //LinearLayout linearLayout = (LinearLayout)view.findViewById(R.id.linearLayout);
//
//        if (position == 3) {
//
//            Videos video = new Videos(context);
//            video.setUpVideo();
//            videosPager.start();
//            //setUpVideo();
//        }
//        Images images1 = new Images(context);
//        images1.setUpImage();
//        //setUpImage();
//        //String abc = "1";
//    }


//    public class Images extends ImageView{
//
////        Context context;
////        String[] images;
////        int position;
////        Activity activity;
////        View view;
////        Images imagesPager;
////
////        private View imageView;
//
//
//
//        public Images(Context context) {
//            super(context);
////            this.context = context;
////            this.images = images;
////            this.position = position;
////            activity = (Activity) context;
////            this.view = view;
////            //LinearLayout linearLayout = (LinearLayout)view.findViewById(R.id.linearLayout);
////            imagesPager = (Images) view.findViewById(R.id.imagesViewPager);
////            videosPager=  (Videos) view.findViewById(R.id.imagesViewPager);
//
//        }
//        public void Init() {
////            imagesViewPager = (ImagesViewPager) findViewById(R.id.imagesViewPager);
////            video1 = (videoPager) findViewById(R.id.imagesViewPager);
////            this.context = context;
////            this.images = images;
////            this.position = position;
////            activity = (Activity) context;
////            this.view = view;
//            //LinearLayout linearLayout = (LinearLayout)view.findViewById(R.id.linearLayout);
//
//            if (position == 3) {
//
////                videoPager video = new videoPager(context);
////                video.setUpVideo();
//                //video1.start();
//                //setUpVideo();
//            }
//            setUpImage();
//            //String abc = "1";
//        }
//        public void setUpImage() {
//
//
//            //imageView = view.findViewById(R.id.imagesViewPager);
//            //imageView = new ImageView(context);
//
//            DisplayMetrics displayMetrics = new DisplayMetrics();
//            activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//            int height = displayMetrics.heightPixels;
//            int width = displayMetrics.widthPixels;
//
//            imagesViewPager.setMinimumHeight(height);
//            imagesViewPager.setMinimumWidth(width);
//
//
//            try {
//                Glide.with(context)
//                        .load(getImage(images[position]))
//                        .into(imagesViewPager);
//            } catch (Exception ex) {
//                Log.d("error", ex.toString());
//            }
//
//
//        }
//
//        public Images(Context context, @Nullable AttributeSet attrs) {
//            super(context, attrs);
//        }
//
//        public Images(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
//            super(context, attrs, defStyleAttr);
//        }
//
//
////        public void Init(Context context) {
////            //imagesViewPager = (ImagesViewPager) findViewById(R.id.imagesViewPager);
////            //video1 = (videoPager) findViewById(R.id.imagesViewPager);
//////            this.context = context;
//////            this.images = images;
//////            this.position = position;
//////            activity = (Activity) context;
//////            this.view = view;
////            //LinearLayout linearLayout = (LinearLayout)view.findViewById(R.id.linearLayout);
////
////            if (position == 3) {
////
//////                videoPager video = new videoPager(context);
//////                video.setUpVideo();
////                //video1.start();
////                //setUpVideo();
////            }
////            setUpImage();
////            //String abc = "1";
////        }
//
//
//
//
//
//
//
//    }
//
//    public class Videos extends VideoView {
//
//        //video video1;
//        //Videos videosPager;
//        public Videos(Context context) {
//            super(context);
//            //video1 = (video)findViewById(R.id.imagesViewPager);
//        }
//
//        public Videos(Context context, AttributeSet attrs) {
//            super(context, attrs);
//        }
//
//        public Videos(Context context, AttributeSet attrs, int defStyleAttr) {
//            super(context, attrs, defStyleAttr);
//        }
//
//        public void setUpVideo() {
//
//            //video1 = (video)findViewById(R.id.imagesViewPager);
//            //imageView = view.findViewById(R.id.imagesViewPager);
//            //imageView = new ImageView(context);
//
//            String videoPath = "android.resource://com.example.sheryarkhan/" + R.raw.video1;
//            Uri uri = Uri.parse(videoPath);
//
//            DisplayMetrics displayMetrics = new DisplayMetrics();
//            //activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//            int height = displayMetrics.heightPixels;
//            int width = displayMetrics.widthPixels;
//
//            videosPager.setMinimumHeight(height);
//            videosPager.setMinimumWidth(width);
//            videosPager.setVideoURI(uri);
//
//
//
//        }
//
//    }

}





