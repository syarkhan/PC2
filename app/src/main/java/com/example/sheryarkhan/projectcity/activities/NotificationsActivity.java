package com.example.sheryarkhan.projectcity.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.sheryarkhan.projectcity.R;

public class NotificationsActivity extends AppCompatActivity {

    private static final int ACTIVITY_NUM=2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        //setupBottomNavigationView();
    }

//    private void setupBottomNavigationView() {
//        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx) findViewById(R.id.my_toolbar_bottom);
//        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx,this);
//        Menu menu = bottomNavigationViewEx.getMenu();
//        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
//        menuItem.setChecked(true);
//    }
}
