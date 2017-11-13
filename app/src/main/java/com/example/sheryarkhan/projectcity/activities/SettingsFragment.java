package com.example.sheryarkhan.projectcity.activities;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sheryarkhan.projectcity.R;
import com.example.sheryarkhan.projectcity.adapters.SettingsRecyclerAdapter;

import java.util.ArrayList;

import data.Setting;


public class SettingsFragment extends Fragment {





    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ArrayList<Setting> settingsList = new ArrayList<>();
        settingsList.add(new Setting("imgViewLocation","Your Name"));
        settingsList.add(new Setting("imgViewLocation","Edit Profile"));
        settingsList.add(new Setting("imgViewLocation","Notifications"));
        settingsList.add(new Setting("imgViewLocation","Location"));
        settingsList.add(new Setting("imgViewLocation","Change Password"));
        settingsList.add(new Setting("imgViewLocation","Report a Problem"));
        settingsList.add(new Setting("imgViewLocation","Privacy Policy"));
        settingsList.add(new Setting("imgViewLocation","Terms"));
        settingsList.add(new Setting("imgViewLocation","Log Out"));


        LinearLayoutManager linearLayoutManager;
        RecyclerView settingsRecyclerView;
        SettingsRecyclerAdapter settingsRecyclerAdapter;

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        settingsRecyclerView = (RecyclerView) view.findViewById(R.id.settingsRecyclerView);
        settingsRecyclerView.setHasFixedSize(true);
        settingsRecyclerView.setDrawingCacheEnabled(true);
        settingsRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        linearLayoutManager = new LinearLayoutManager(getActivity());
        settingsRecyclerView.setLayoutManager(linearLayoutManager);
        settingsRecyclerAdapter = new SettingsRecyclerAdapter(settingsList,getActivity());
        settingsRecyclerView.setAdapter(settingsRecyclerAdapter);

        return view;
    }
}
