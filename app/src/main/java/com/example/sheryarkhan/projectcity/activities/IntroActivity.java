package com.example.sheryarkhan.projectcity.activities;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.sheryarkhan.projectcity.R;
import com.example.sheryarkhan.projectcity.utils.IntroViewPager;

public class IntroActivity extends AppCompatActivity {


    ViewPager viewPager ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        final int[] layouts = new int[]{
                R.layout.intro_layout_page1,
                R.layout.intro_layout_page2,
                R.layout.intro_layout_page3
        };

        final TextView txtSkip = (TextView) findViewById(R.id.txtSkip);
        final TextView txtNext = (TextView)findViewById(R.id.txtNext);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        viewPager = (ViewPager)findViewById(R.id.viewPager);

        IntroViewPager introViewPager = new IntroViewPager(this,layouts);
        viewPager.setAdapter(introViewPager);
        tabLayout.setupWithViewPager(viewPager);


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                if(position == layouts.length-1){
                    txtNext.setText("Proceed");
                    txtSkip.setVisibility(View.GONE);
                }else {
                    txtNext.setText("Next");
                    txtSkip.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        txtNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int current = viewPager.getCurrentItem() + 1;
                if(current < layouts.length){
                    viewPager.setCurrentItem(current);
                }else {
                    Intent intent = new Intent(IntroActivity.this,LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

        txtSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(IntroActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }


}
