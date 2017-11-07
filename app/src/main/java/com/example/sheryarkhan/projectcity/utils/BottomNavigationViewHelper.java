package com.example.sheryarkhan.projectcity.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.view.Menu;
import android.view.MenuItem;

import com.example.sheryarkhan.projectcity.R;
import com.example.sheryarkhan.projectcity.activities.MainActivity;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

/**
 * Created by Sheryar Khan on 9/5/2017.
 */

public class BottomNavigationViewHelper {
    private static Font font;
//    private static Fragment selectedFragment=null;

    public static void setupBottomNavigationView(final BottomNavigationViewEx bottomNavigationViewEx,final Context context)
    {
        final MainActivity mainActivity = (MainActivity) context;
        bottomNavigationViewEx.enableAnimation(true);
        //mainActivity.stack.push(new NewsFeedFragment());

        mainActivity.addFragmentToStack(mainActivity.TAB_TOWN,0);
        mainActivity.counter++;
        //mainActivity.pushFragments(mainActivity.TAB_TOWN, new NewsFeedFragment(),true,true);
        //bottomNavigationViewEx.enableItemShiftingMode(false);
        bottomNavigationViewEx.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                Menu menu;
                MenuItem menuItem;
                menu = bottomNavigationViewEx.getMenu();
                //FragmentManager fragmentManager=;
                switch (item.getItemId()){
                    case R.id.navigation_home:
                        if(!mainActivity.isTabClickedTwoTimes) {
                            //mainActivity.selectedTab(mainActivity.TAB_TOWN);
                            mainActivity.counter++;
                            mainActivity.isTabClickedTwoTimes = false;
                            mainActivity.addFragmentToStack(mainActivity.TAB_TOWN, 0);

                        }
                        else {
                            mainActivity.isTabClickedTwoTimes=false;
                        }
                        //if(selectedFragment == null) {
                            //selectedFragment = new ChatMessagingFragment();

                            //mainActivity.loadNewsFeedFragmentIntoContainer("tab_town");
                        //}
                        //fragmentManager.beginTransaction().replace(R.id.main_container, selectedFragment).commit();

                        //context.startActivity(new Intent(context.getApplicationContext(), NewsFeedActivity.class));
                        //bottomNavigationViewEx.setItemBackground(0,R.drawable.gradient_background);
                        //bottomNavigationViewEx.setItemBackground(1,R.color.white);
                        //Toast.makeText(context,"hello",Toast.LENGTH_SHORT).show();

                        menuItem = menu.getItem(0);
                        menuItem.setChecked(true);
                        break;
                    case R.id.navigation_city:
                        if(!mainActivity.isTabClickedTwoTimes) {
                            //mainActivity.selectedTab(mainActivity.TAB_TOWN);
                            mainActivity.counter++;
                            mainActivity.isTabClickedTwoTimes = false;
                            mainActivity.addFragmentToStack(mainActivity.TAB_CITY, 1);

                        }
                        else {
                            mainActivity.isTabClickedTwoTimes=false;
                        }
                        //MainActivity mainActivity = (MainActivity) context;
                       // mainActivity.loadChatMessagesFragmentIntoContainer("tab_city");

                        menuItem = menu.getItem(1);
                        menuItem.setChecked(true);
                        //context.startActivity(new Intent(context.getApplicationContext(), CityNewsFeedActivity.class));
                        //bottomNavigationViewEx.setItemBackground(1,R.drawable.gradient_background);
                        break;
                    case R.id.navigation_notifications:
                        if(!mainActivity.isTabClickedTwoTimes) {
                            //mainActivity.selectedTab(mainActivity.TAB_TOWN);
                            mainActivity.counter++;
                            mainActivity.isTabClickedTwoTimes = false;
                            mainActivity.addFragmentToStack(mainActivity.TAB_NOTIFICATIONS, 2);

                        }
                        else {
                            mainActivity.isTabClickedTwoTimes=true;
                        }
                        //context.startActivity(new Intent(context.getApplicationContext(), NotificationsActivity.class));
                        //bottomNavigationViewEx.setItemBackground(2,R.drawable.gradient_background);
                        menuItem = menu.getItem(2);
                        menuItem.setChecked(true);
                        break;
                    case R.id.navigation_settings:
                        if(!mainActivity.isTabClickedTwoTimes) {
                            //mainActivity.selectedTab(mainActivity.TAB_TOWN);
                            mainActivity.counter++;
                            mainActivity.isTabClickedTwoTimes = false;
                            mainActivity.addFragmentToStack(mainActivity.TAB_SETTINGS, 3);

                        }
                        else {
                            mainActivity.isTabClickedTwoTimes=false;
                        }
                        //context.startActivity(new Intent(context.getApplicationContext(), SettingsActivity.class));
                        //bottomNavigationViewEx.setItemBackground(3,R.drawable.gradient_background);
                        menuItem = menu.getItem(3);
                        menuItem.setChecked(true);
                        break;
                }
                return false;
            }
        });
        //bottomNavigationViewEx.setItemBackground(1, R.drawable.gradient_background);
        bottomNavigationViewEx.enableShiftingMode(false);
        bottomNavigationViewEx.setIconSize(25,25);
        bottomNavigationViewEx.setTextSize(10);
        //bottomNavigationViewEx.setItemTextColor(ColorStateList.createFromXml(,));

        //bottomNavigationViewEx.setTextVisibility(false);
        font = new Font("Regular",context);
        bottomNavigationViewEx.setTypeface(font.getRobotoFont());
    }

}
