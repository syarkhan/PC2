package com.example.sheryarkhan.projectcity.Glide;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;
import com.example.sheryarkhan.projectcity.utils.FirebaseImageLoader;
import com.google.firebase.storage.StorageReference;

import java.io.InputStream;


/**
 * Created by Sheryar Khan on 9/6/2017.
 */

@GlideModule
public class MyAppGlideModule extends AppGlideModule{
    @Override
    public void registerComponents(Context context, Glide glide, Registry registry) {
        // Register FirebaseImageLoader to handle StorageReference
        registry.append(StorageReference.class, InputStream.class,
                new FirebaseImageLoader.Factory());
    }
}