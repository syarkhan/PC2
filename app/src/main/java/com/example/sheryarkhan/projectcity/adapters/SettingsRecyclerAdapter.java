package com.example.sheryarkhan.projectcity.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sheryarkhan.projectcity.R;

import java.util.Collections;
import java.util.List;

import data.Setting;

/**
 * Created by Sheryar Khan on 11/1/2017.
 */

public class SettingsRecyclerAdapter extends RecyclerView.Adapter<SettingsRecyclerAdapter.SettingsHolder> {

    private List<Setting> settingsList = Collections.emptyList();

    public SettingsRecyclerAdapter(List<Setting> settingsList) {
        this.settingsList = settingsList;
    }

    @Override
    public SettingsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        return new SettingsHolder(LayoutInflater.from(context).inflate(R.layout.setting_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(SettingsHolder holder, int position) {
        Context context = holder.itemView.getContext();
        SettingsRecyclerAdapter.SettingsHolder mholder = holder;
        SetUpSettingsView(context,mholder,position);
    }

    private void SetUpSettingsView(Context context, SettingsHolder mholder, int position) {
        final Setting currentData = settingsList.get(mholder.getAdapterPosition());
        mholder.txtSettings.setText(currentData.getTxtSettings());
        //mholder.txtViewProfile.setText("View Profile");
        mholder.imgViewSettings.setImageResource(R.drawable.add_media_icon);
    }

    @Override
    public int getItemCount() {
        return settingsList.size();
    }

    class SettingsHolder extends RecyclerView.ViewHolder{

        ImageView imgViewSettings;
        TextView txtSettings;
        //TextView txtViewProfile;

        public SettingsHolder(View itemView) {
            super(itemView);
            imgViewSettings = (ImageView)itemView.findViewById(R.id.imgViewSettings);
            txtSettings = (TextView) itemView.findViewById(R.id.txtSettings);
            //txtViewProfile = (TextView) itemView.findViewById(R.id.txtViewProfile);

        }
    }
}
