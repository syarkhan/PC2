package com.example.sheryarkhan.projectcity.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.sheryarkhan.projectcity.Glide.GlideApp;
import com.example.sheryarkhan.projectcity.R;
import com.example.sheryarkhan.projectcity.activities.LoginActivity;
import com.example.sheryarkhan.projectcity.activities.NotificationPostActivity;
import com.example.sheryarkhan.projectcity.activities.ProfileActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Collections;
import java.util.List;

import data.Setting;

/**
 * Created by Sheryar Khan on 11/1/2017.
 */

public class SettingsRecyclerAdapter extends RecyclerView.Adapter<SettingsRecyclerAdapter.MainViewHolder> {

    private List<Setting> settingsList = Collections.emptyList();
    private FirebaseAuth firebaseAuth;
    final SharedPreferences sharedPref;
    private static final int TYPE_SETTING_PROFILE = 1;
    private static final int TYPE_SETTING_OTHERS = 2;


    public SettingsRecyclerAdapter(List<Setting> settingsList, Context context) {
        this.settingsList = settingsList;
         sharedPref = context.getSharedPreferences("UserData", Context.MODE_PRIVATE);
        firebaseAuth = FirebaseAuth.getInstance();
    }
    @Override
    public int getItemViewType(int position) {
        if(position == 0){
            return TYPE_SETTING_PROFILE;
        }else {
            return TYPE_SETTING_OTHERS;
        }

    }
    @Override
    public MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();

        switch (viewType) {
            case TYPE_SETTING_PROFILE:
                return new SettingsRecyclerAdapter.ProfileHolder(LayoutInflater.from(context).inflate(R.layout.setting_profile_list_item, parent, false));

            case TYPE_SETTING_OTHERS:
                return new SettingsRecyclerAdapter.SettingsHolder(LayoutInflater.from(context).inflate(R.layout.setting_list_item, parent, false));

        }

        return null;
    }



    @Override
    public void onBindViewHolder(MainViewHolder holder, int position) {
        Context context = holder.itemView.getContext();
       // SettingsRecyclerAdapter.MainViewHolder mholder = holder;
        if(holder.getItemViewType() == TYPE_SETTING_PROFILE){
            ProfileHolder mholder = (ProfileHolder) holder;
            setUpSettingProfileView(context,mholder,position);
        }else if (holder.getItemViewType() == TYPE_SETTING_OTHERS){
            SettingsHolder mholder = (SettingsHolder) holder;
            SetUpSettingsView(context,mholder,position);
        }

    }

    private void setUpSettingProfileView(final Context context, final ProfileHolder mholder, int position){
        final Setting currentData = settingsList.get(mholder.getAdapterPosition());

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        StorageReference  storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference filePath = storageReference.child("profilepictures").child("profilepic:"+userId);
        filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                try {
                    GlideApp.with(context)
                            .load(uri)
                            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                            .circleCrop()
                            .transition(DrawableTransitionOptions.withCrossFade(1000))
                            .error(R.color.link)
                            .into(mholder.imageViewProfile);
                } catch (Exception ex) {
                    Log.d("error", ex.toString());
                }
            }
        });


        //mholder.imageViewProfile.setImageResource(R.drawable.circle_image);
        mholder.txtUsername.setText(currentData.getTxtSettings());
        mholder.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context , ProfileActivity.class);
                context.startActivity(intent);
            }
        });
    }

    private void SetUpSettingsView(final Context context, final SettingsHolder mholder, int position) {
        final Setting currentData = settingsList.get(mholder.getAdapterPosition());
        mholder.txtSettings.setText(currentData.getTxtSettings());
        //mholder.txtViewProfile.setText("View Profile");
        mholder.imgViewSettings.setImageResource(R.drawable.add_media_icon);
        mholder.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*if(mholder.getAdapterPosition() == 0){
                    //UserProfile
                    Intent intent = new Intent(context , ProfileActivity.class);
                    context.startActivity(intent);


                }*/
                if(mholder.getAdapterPosition() == 1){
                    //Notification
                }
                else if(mholder.getAdapterPosition() == 2 ){
                    //Change Password
                }
                else if(mholder.getAdapterPosition()== 3){
                    //Logout
                    /*final String town = sharedPref.getString("town", "");
                    FirebaseMessaging.getInstance().unsubscribeFromTopic(town);
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("user_"+firebaseAuth.getCurrentUser().getUid());


                    //databaseReference.child(user.getUid()).child("status").setValue(false);
                    firebaseAuth.signOut();

                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.clear();
                    editor.apply();
                    context.startActivity(new Intent(context, LoginActivity.class)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    ((Activity)context).finish();*/

                }
                else {
                    //other settings
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return settingsList.size();
    }

    public class MainViewHolder extends RecyclerView.ViewHolder{
        public MainViewHolder(View v){
            super(v);
        }
    }

   private class SettingsHolder extends MainViewHolder{

        ImageView imgViewSettings;
        TextView txtSettings;
        ConstraintLayout constraintLayout;
        //TextView txtViewProfile;

        public SettingsHolder(View itemView) {
            super(itemView);
            imgViewSettings = (ImageView)itemView.findViewById(R.id.imgViewSettings);
            txtSettings = (TextView) itemView.findViewById(R.id.txtSettings);
            constraintLayout = (ConstraintLayout) itemView.findViewById(R.id.parentLayout);
            //txtViewProfile = (TextView) itemView.findViewById(R.id.txtViewProfile);

        }
    }
    private class ProfileHolder extends MainViewHolder{

        ImageView imageViewProfile;
        TextView txtUsername;
        TextView txtEditProfile;
        ConstraintLayout constraintLayout;
        public ProfileHolder(View v) {
            super(v);
            imageViewProfile = (ImageView) v.findViewById(R.id.imgSettingProfile);
            txtUsername = (TextView) v.findViewById(R.id.txtUsername);
            txtEditProfile = (TextView) v.findViewById(R.id.txtEditProfile);
            constraintLayout = (ConstraintLayout) itemView.findViewById(R.id.setting_profile_list_layout);

        }
    }
}
