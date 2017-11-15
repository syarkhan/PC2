package com.example.sheryarkhan.projectcity.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
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
import com.example.sheryarkhan.projectcity.activities.NotificationPostActivity;
import com.example.sheryarkhan.projectcity.utils.HelperFunctions;
import com.example.sheryarkhan.projectcity.utils.SharedPrefs;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import data.Notification;

/**
 * Created by Sheryar Khan on 10/26/2017.
 */

public class NotificationsRecyclerAdapter extends RecyclerView.Adapter<NotificationsRecyclerAdapter.NotificationsHolder>
{

    private List<Notification> notificationsList = Collections.emptyList();
    private SharedPrefs sharedPrefs;
    private Context context;
    private StorageReference storageReference;

    public NotificationsRecyclerAdapter(Context context, ArrayList<Notification> notificationsList) {
        this.notificationsList = notificationsList;
        this.context = context;
        sharedPrefs = new SharedPrefs(context);
        storageReference = FirebaseStorage.getInstance().getReference();
    }

    @Override
    public NotificationsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        return new NotificationsRecyclerAdapter.NotificationsHolder(LayoutInflater.from(context).inflate(R.layout.notification_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(NotificationsHolder holder, int position) {
        //final Context context = holder.itemView.getContext();
        NotificationsRecyclerAdapter.NotificationsHolder mholder = holder;
        SetUpNotificationsView(context, mholder, position);
    }

    private void SetUpNotificationsView(final Context context, final NotificationsHolder mholder, int position) {
        final Notification currentData = notificationsList.get(mholder.getAdapterPosition());

        if(currentData.getRead()){
            mholder.parentLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
            mholder.unread_circle.setVisibility(View.GONE);
        }else{
            //mholder.parentLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
            //mholder.unread_circle.setVisibility(View.VISIBLE);
        }
        switch (currentData.getNotificationType()) {
            case "post_comment":
                mholder.txtNotificationMessage.setText(
                        currentData.getUserInfo().getUsername() +
                                " commented on your post: " +
                                "\"" + currentData.getPostInfo().getPostText() + "\"."
                );
                break;
            case "post_like":
                mholder.txtNotificationMessage.setText(
                        currentData.getUserInfo().getUsername() +
                                " liked your post: " +
                                "\"" + currentData.getPostInfo().getPostText() + "\"."
                );
                break;
            case "town_post":
                mholder.txtNotificationMessage.setText(
                        currentData.getUserInfo().getUsername() +
                                " posted in " +
                                sharedPrefs.getTownFromSharedPref()

                );
                break;
            case "post_comment_like":
                mholder.txtNotificationMessage.setText(
                        currentData.getUserInfo().getUsername() +
                                " liked your comment: " +
                                "\"" + currentData.getCommentInfo().getCommentText() + "\"."
                );
                break;
        }

        mholder.txtTimeStamp.setText(HelperFunctions.getTimeAgo(Long.valueOf(currentData.getTimestamp())));
        if (currentData.getUserInfo().getProfilePicture() != null) {
            StorageReference filePath = storageReference.child("profilepictures").child(currentData.getUserInfo().getProfilePicture());
            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    try {
                        GlideApp.with(context)
                                .load(uri)
                                .circleCrop()
                                .transition(DrawableTransitionOptions.withCrossFade(1000))
                                .error(R.color.link)
                                .into(mholder.imgProfilePic);
                    } catch (Exception ex) {
                        Log.d("error", ex.toString());
                    }
                }
            });
        }

//        mholder.parentLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(context, NotificationPostActivity.class);
//                intent.putExtra("postId",currentData.getPostId());
//            }
//        });
        //mholder.txtNotificationMessage.setText(currentData.getNotificationType());
    }

    @Override
    public int getItemCount() {
        return notificationsList.size();

    }

    public String getItem(int position) {
        return notificationsList.get(position).getPostId();
    }


    class NotificationsHolder extends RecyclerView.ViewHolder {

        ImageView imgProfilePic;
        TextView txtNotificationMessage;
        TextView txtTimeStamp;
        View unread_circle;
        ConstraintLayout parentLayout;


        private NotificationsHolder(View itemView) {
            super(itemView);
            imgProfilePic = (ImageView) itemView.findViewById(R.id.imgProfilePic);
            txtNotificationMessage = (TextView) itemView.findViewById(R.id.txtNotificationMessage);
            txtTimeStamp = (TextView) itemView.findViewById(R.id.txtTimeStamp);
            unread_circle = (View) itemView.findViewById(R.id.unread_circle);
            parentLayout = (ConstraintLayout) itemView.findViewById(R.id.parentLayout);

        }


    }
}
