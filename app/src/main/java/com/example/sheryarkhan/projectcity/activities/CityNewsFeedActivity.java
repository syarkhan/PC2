package com.example.sheryarkhan.projectcity.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.sheryarkhan.projectcity.R;

public class CityNewsFeedActivity extends AppCompatActivity {


    private static final int ACTIVITY_NUM=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_news_feed);
        //setupBottomNavigationView();
    }

//    private void setupBottomNavigationView() {
//        //FragmentManager fragmentManager = getFragmentManager();
//        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx) findViewById(R.id.my_toolbar_bottom);
//        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx,this, );
//        Menu menu = bottomNavigationViewEx.getMenu();
//        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
//        menuItem.setChecked(true);
//    }
}
