package com.example.sheryarkhan.projectcity.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sheryarkhan.projectcity.R;
import com.example.sheryarkhan.projectcity.utils.BottomNavigationViewHelper;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

public class MainFragment extends Fragment {



//    private static final int INDEX_TOWN_NEWS = FragNavController.TAB1;
//    private static final int INDEX_CITY_NEWS = FragNavController.TAB2;
//    private static final int INDEX_NOTIFICATIONS = FragNavController.TAB3;
//    private static final int INDEX_SETTINGS = FragNavController.TAB4;



    //private FragNavController mNavController;
    //private FragNavController.Builder builder;
    //List<Fragment> fragments = new ArrayList<>(5);
    //FragmentsHandler fragmentsHandler;
    public BottomNavigationViewEx bottomNavigationViewEx;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //fragmentsHandler = new FragmentsHandler()

//        builder.rootFragments(fragments);
//
//        builder.rootFragmentListener(this, 4);
//
//        mNavController = builder.build();
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

//        if(mainActivity==null)
//        {
//            mainActivity = (MainActivity) getActivity();
//            mainActivity.loadNewsFeedFragmentIntoContainer("tab_town");
//            //mainActivity.addFragments("tab_town",);
//        }
//        else
//        {
//            mainActivity.loadNewsFeedFragmentIntoContainer("tab_town");
//        }

        //mainActivity.
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        setupBottomNavigationView(view);
        return view;
    }



    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    private void setupBottomNavigationView(View view) {

        //FragmentManager fragmentManager = getFragmentManager();
        bottomNavigationViewEx = (BottomNavigationViewEx) view.findViewById(R.id.my_toolbar_bottom);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx, getActivity());
//        Menu menu = bottomNavigationViewEx.getMenu();
//        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
//        menuItem.setChecked(true);
    }

//    @Override
//    public Fragment getRootFragment(int index) {
//        switch (index) {
//            case INDEX_TOWN_NEWS:
//                return NewsFeedFragment.newInstance();
//            case INDEX_CITY_NEWS:
//                return ChatMessagingFragment.newInstance();
//            case INDEX_NOTIFICATIONS:
//                return ChatMessagingFragment.newInstance();
//            case INDEX_SETTINGS:
//                return NewsFeedFragment.newInstance();
//
//        }
//        throw new IllegalStateException("Need to send an index that we know");
//    }
}
