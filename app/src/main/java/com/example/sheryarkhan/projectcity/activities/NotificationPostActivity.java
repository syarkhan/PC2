package com.example.sheryarkhan.projectcity.activities;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.sheryarkhan.projectcity.Glide.GlideApp;
import com.example.sheryarkhan.projectcity.R;
import com.example.sheryarkhan.projectcity.adapters.PostContentViewPagerAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import data.PostsPOJO;

public class NotificationPostActivity extends AppCompatActivity {

    TextView txtName;
    TextView txtTimeStamp;
    TextView txtStatusMsg;
    TextView txtLikesAndComments,txtLocation,txtSecondary;
    ImageView imgProfilePic;
    ImageButton btnHelpful;
    ImageButton btnComments;
    ViewPager viewPager;
    TabLayout tabLayout;
    CardView parentLayout;
    ProgressBar progressBar;

    DatabaseReference databaseReference;
    private StorageReference storageReference;


    private PostContentViewPagerAdapter postContentViewPagerAdapter;
    private int likes=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_post);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("");

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        tabLayout = (TabLayout) findViewById(R.id.pagerTabs);

        txtName = (TextView) findViewById(R.id.txtName);
        txtTimeStamp = (TextView) findViewById(R.id.txtTimeStampAndLocation);
        txtStatusMsg = (TextView) findViewById(R.id.txtStatusMsg);
        txtLikesAndComments = (TextView) findViewById(R.id.txtLikesAndComments);
        parentLayout = (CardView) findViewById(R.id.parentLayout);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        parentLayout.setVisibility(View.GONE);


        txtLocation = (TextView) findViewById(R.id.txtLocation);
        txtSecondary = (TextView) findViewById(R.id.txtSecondary);
        imgProfilePic = (ImageView) findViewById(R.id.imgProfilePic);

        btnHelpful = (ImageButton) findViewById(R.id.btnHelpful);
        btnComments = (ImageButton) findViewById(R.id.btnComments);


        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();

        Intent intent = getIntent();
        String postId = intent.getStringExtra("postId");

        databaseReference.child("posts/"+postId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final PostsPOJO currentData = dataSnapshot.getValue(PostsPOJO.class);
                //currentData.setPostid(dataSnapshot.getKey());
                //Log.d("dadaNotify",currentData.toString());
                if (currentData.getcontent_post().size() == 0)
                {
                    viewPager.setVisibility(View.GONE);
                    tabLayout.setVisibility(View.GONE);
                }
                else if(currentData.getcontent_post().size() > 1)
                {
                    //postContentViewPagerAdapter = new PostContentViewPagerAdapter(NotificationPostActivity.this,currentData.getcontent_post());
                    viewPager.setAdapter(postContentViewPagerAdapter);
                    viewPager.setVisibility(View.VISIBLE);
                    tabLayout.setVisibility(View.VISIBLE);
                    tabLayout.setupWithViewPager(viewPager, true);
                }
                else if(currentData.getcontent_post().size() == 1)
                {
                    //postContentViewPagerAdapter = new PostContentViewPagerAdapter(NotificationPostActivity.this,currentData.getcontent_post());
                    viewPager.setAdapter(postContentViewPagerAdapter);
                    tabLayout.setVisibility(View.GONE);
                    viewPager.setVisibility(View.VISIBLE);
                }

                txtName.setText(currentData.getusername());

                StorageReference filePath = storageReference.child("profilepictures").child(currentData.getprofilepicture());
                filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        try {
                            GlideApp.with(NotificationPostActivity.this)
                                    .load(uri)
                                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                                    .circleCrop()
                                    .transition(DrawableTransitionOptions.withCrossFade(1000))
                                    .error(R.color.link)
                                    .into(imgProfilePic);
                        } catch (Exception ex) {
                            Log.d("error", ex.toString());
                        }
                    }
                });

                likes = currentData.getLikes().size() == 0 ? 0 : currentData.getLikes().size();
                //int comments = currentData.getcomments().size() == 0 ? 0 : currentData.getLikes().size();

                txtLikesAndComments.setText(likes+" Likes • 0 Comments");

                // Converting timestamp into X ago format
                CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(
                        Long.parseLong(String.valueOf(currentData.gettimestamp())),
                        System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);

                txtTimeStamp.setText(timeAgo);
                txtLocation.setText(currentData.getlocation());

                txtSecondary.setText(currentData.getsecondarylocation());

                txtStatusMsg.setText(currentData.getposttext());

                progressBar.setVisibility(View.GONE);
                parentLayout.setVisibility(View.VISIBLE);



                imgProfilePic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(NotificationPostActivity.this, ProfileActivity.class);
                        NotificationPostActivity.this.startActivity(intent);
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);
            }
        });

        //PostsPOJO currentData = newsFeedItemPOJOs.get(position);


    }
}
