package com.example.sheryarkhan.projectcity.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sheryarkhan.projectcity.R;
import com.example.sheryarkhan.projectcity.utils.HelperFunctions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import data.Notification;

/**
 * Created by Sheryar Khan on 10/26/2017.
 */

public class NotificationsRecyclerAdapter extends RecyclerView.Adapter<NotificationsRecyclerAdapter.NotificationsHolder>{

    private List<Notification> notificationsList = Collections.emptyList();

    public NotificationsRecyclerAdapter(ArrayList<Notification> notificationsList) {
        this.notificationsList = notificationsList;
    }

    @Override
    public NotificationsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        return new NotificationsRecyclerAdapter.NotificationsHolder(LayoutInflater.from(context).inflate(R.layout.notification_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(NotificationsHolder holder, int position) {
        Context context = holder.itemView.getContext();
        NotificationsRecyclerAdapter.NotificationsHolder mholder = holder;
        SetUpNotificationsView(context,mholder,position);
    }

    private void SetUpNotificationsView(Context context, NotificationsHolder mholder, int position) {
        final Notification currentData = notificationsList.get(mholder.getAdapterPosition());
        mholder.txtNotificationMessage.setText("Faizan Khan liked your post");
        mholder.txtTimeStamp.setText(HelperFunctions.getTimeAgo(currentData.getTimestamp()));
        mholder.txtNotificationMessage.setText(currentData.getNotification());
    }

    @Override
    public int getItemCount() {
        return notificationsList.size();
    }

    class NotificationsHolder extends RecyclerView.ViewHolder{

        ImageView imgProfilePic;
        TextView txtNotificationMessage;
        TextView txtTimeStamp;


        private NotificationsHolder(View itemView) {
            super(itemView);
            imgProfilePic = (ImageView) itemView.findViewById(R.id.imgProfilePic);
            //txtNotificationMessage = (TextView) itemView.findViewById(R.id.txtNotificationMessage);
            txtTimeStamp = (TextView) itemView.findViewById(R.id.txtTimeStamp);
        }
    }
}
