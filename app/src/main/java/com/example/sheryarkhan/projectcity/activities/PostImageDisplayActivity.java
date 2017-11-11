package com.example.sheryarkhan.projectcity.activities;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.sheryarkhan.projectcity.Glide.GlideApp;
import com.example.sheryarkhan.projectcity.R;
import com.example.sheryarkhan.projectcity.utils.TouchImageView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class PostImageDisplayActivity extends AppCompatActivity {

    TouchImageView postImageViewDisplay;
    Toolbar toolbar;
    ImageButton backBtn;
    private StorageReference storageReference;
    private Boolean isToolbar = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_image_display);

        toolbar = (Toolbar)findViewById(R.id.image_toolbar);
        backBtn = (ImageButton) findViewById(R.id.back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        hideToolbarAfterSomeSec(2000);
        //supportPostponeEnterTransition();
        storageReference = FirebaseStorage.getInstance().getReference();

        //  postImageViewDisplay = new TouchImageView(this);
        postImageViewDisplay = (TouchImageView)findViewById(R.id.postImageViewDisplay);
        postImageViewDisplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isToolbar){
                    toolbar.setVisibility(View.GONE);
                    isToolbar = false;
                }else {
                    toolbar.setVisibility(View.VISIBLE);
                    isToolbar = true;
                }
            }
        });


        Intent i = getIntent();

        String url = i.getStringExtra("url");

        StorageReference filePath = storageReference.child("images").child(url);
        GlideApp.with(PostImageDisplayActivity.this)
                .load(filePath)
                .placeholder(R.color.black)
                .error(R.color.link)
                .transition(DrawableTransitionOptions.withCrossFade(1000))
                .into(postImageViewDisplay);
//        filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//            @Override
//            public void onSuccess(Uri uri) {
//
//                GlideApp.with(PostImageDisplayActivity.this)
//                        .load(uri)
//                        .dontAnimate()
//                        .listener(new RequestListener<Drawable>() {
//                            @Override
//                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
//                                supportStartPostponedEnterTransition();
//                                return false;
//                            }
//
//                            @Override
//                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
//                                supportStartPostponedEnterTransition();
//                                return false;
//                            }
//                        }).into(postImageViewDisplay);

                /*try {
                    GlideApp.with(PostImageDisplayActivity.this)
                            .load(uri)
                            .error(R.color.link)
                            .transition(DrawableTransitionOptions.withCrossFade(300))
                            .listener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    Log.d("glideerror",e.toString());
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    //postImageViewDisplay.setImageDrawable(resource);
                                    return false;
                                }
                            }).into(postImageViewDisplay);
                } catch (Exception ex) {
                    Log.d("error", ex.toString());
                }
*/
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Log.d("PostImageError",e.toString());
//            }
//        });


        //postImageViewDisplay.setImageDrawable(NewsFeedRecyclerAdapter.newsFeedItemPOJOs.get(imgIndex).getImage());

    }



    public  void hideToolbarAfterSomeSec(int sec){
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

                toolbar.setVisibility(View.GONE);
                isToolbar = false;
            }
        }, sec);

    }
}
