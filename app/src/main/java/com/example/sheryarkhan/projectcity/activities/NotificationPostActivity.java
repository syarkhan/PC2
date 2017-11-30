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
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.sheryarkhan.projectcity.Glide.GlideApp;
import com.example.sheryarkhan.projectcity.R;
import com.example.sheryarkhan.projectcity.adapters.PostContentViewPagerAdapter;
import com.example.sheryarkhan.projectcity.utils.Constants;
import com.example.sheryarkhan.projectcity.utils.FirebasePushNotificationMethods;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import data.Post;
import data.PostsPOJO;
import data.User;

public class NotificationPostActivity extends AppCompatActivity {

    TextView txtName;
    TextView txtTimeStamp;
    TextView txtStatusMsg;
    TextView txtLikes,txtLocation,txtSecondary, txtComments;
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
    int likesCount;
    int commentsCount;
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
        txtLikes = (TextView) findViewById(R.id.txtLikes);
        txtComments = (TextView) findViewById(R.id.txtComments);
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
        String userId = intent.getStringExtra("userId");
        String commentId = intent.getStringExtra("commentId");
        String notificationType = intent.getStringExtra("notificationType");
        String NotificationId = intent.getStringExtra("NotificationId");


        String URL = Constants.protocol + Constants.IP + Constants.getPost+"/"+postId +"&"+commentId
                +"&"+ userId +"&"+ notificationType +"&"+NotificationId;
        Log.d("url",URL);
        //Get userid, username and town from Mongodb database
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Boolean isSuccess=false;
                GsonBuilder gsonBuilder = new GsonBuilder();
                Gson gson = gsonBuilder.create();
                try {
                    isSuccess = response.getBoolean("success");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if(isSuccess){
                    try {
                        //User user = gson.fromJson(String.valueOf(response.getJSONObject("User")), User.class);
                        final Post currentData = gson.fromJson(String.valueOf(response.getJSONObject("Post")), Post.class);
                        Log.d("postdata", currentData.toString());

                        if (currentData.getContentPost() == null) {
                            viewPager.setVisibility(View.GONE);
                            tabLayout.setVisibility(View.GONE);
                        } else if (currentData.getContentPost().size() > 1) {
                            postContentViewPagerAdapter = new PostContentViewPagerAdapter(NotificationPostActivity.this, currentData.getContentPost());
                            viewPager.setAdapter(postContentViewPagerAdapter);
                            viewPager.setVisibility(View.VISIBLE);
                            tabLayout.setVisibility(View.VISIBLE);
                            tabLayout.setupWithViewPager(viewPager, true);
                        } else if (currentData.getContentPost().size() == 1) {
                            postContentViewPagerAdapter = new PostContentViewPagerAdapter(NotificationPostActivity.this, currentData.getContentPost());
                            viewPager.setAdapter(postContentViewPagerAdapter);
                            tabLayout.setVisibility(View.GONE);
                            viewPager.setVisibility(View.VISIBLE);
                        }

                        txtName.setText(currentData.getUserInfo().getUsername());

                        if (currentData.getUserInfo().getProfilePicture() != null) {
                            StorageReference filePath = storageReference.child("profilepictures").child(currentData.getUserInfo().getProfilePicture());
                            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    try {
                                        GlideApp.with(NotificationPostActivity.this)
                                                .load(uri)
                                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                                .circleCrop()
                                                .transition(DrawableTransitionOptions.withCrossFade(1000))
                                                .error(R.color.link)
                                                .into(imgProfilePic);
                                    } catch (Exception ex) {
                                        Log.d("error", ex.toString());
                                    }
                                }
                            });
                        } else {
                            try {
                                GlideApp.with(NotificationPostActivity.this)
                                        .load(R.drawable.circle_image)
                                        .circleCrop()
                                        .transition(DrawableTransitionOptions.withCrossFade(1000))
                                        .error(R.drawable.circle_image)
                                        .into(imgProfilePic);
                            } catch (Exception ex) {
                                Log.d("error", ex.toString());
                            }
                        }

                        likesCount = currentData.getLikesCount();
                        commentsCount = currentData.getCommentsCount();

//        if (currentData.getLikes().containsKey(firebaseUser.getUid())) {
//            btnHelpful.setTag("like_active");
//            btnHelpful.setImageResource(R.mipmap.ic_like_active);
//        }

                        txtLikes.setText(likesCount + " " + NotificationPostActivity.this.getResources().getString(R.string.likes_dot));
                        txtComments.setText(commentsCount + " " + NotificationPostActivity.this.getResources().getString(R.string.comments));

                        txtName.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(NotificationPostActivity.this, CommentsActivity.class);
                                NotificationPostActivity.this.startActivity(intent);
                            }
                        });

                        btnComments.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
//                                MainActivity mainActivity = (MainActivity) context;
//                                mainActivity.onCommentButtonSelected(currentData.getUserId(), likesCount, commentsCount,
//                                        NotificationPostActivity.this.getString(R.string.main_activity), currentData.getPostId());
//                                mainActivity.hideLayout();
                            }
                        });

                        btnHelpful.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

//                WifiManager wifi = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
//                if (!wifi.isWifiEnabled()) {
//                    //wifi is not enabled
//                    HelperFunctions.getToastShort(context, "No internet connection!");
//                    return;
//                }
                                view.startAnimation(AnimationUtils.loadAnimation(NotificationPostActivity.this, R.anim.image_resize));
                                if (btnHelpful.getTag().equals("like")) {
                                    btnHelpful.setImageResource(R.mipmap.ic_like_active);
                                    btnHelpful.setTag("like_active");
                                } else if (btnHelpful.getTag().equals("like_active")) {
                                    btnHelpful.setImageResource(R.mipmap.ic_like);
                                    btnHelpful.setTag("like");
                                }

//                final String likeAddOrRemoveURL = Constants.protocol + Constants.IP +
//                        Constants.addOrRemoveUserLikeToPost+"/"+sharedPrefs.getUserIdFromSharedPref()+"/"+currentData.getPostId();
//
//                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, likeAddOrRemoveURL, null, new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        Log.d("volleyadd",response.toString());
//
//                        //FirebasePushNotificationMethods.sendTownPostNotification(userid, key, txtPrimary, txtSecondary, editTextShareNews, MainActivity.this);
//                    }
//                }, new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        Log.d("VolleyError", error.toString());
//                    }
//                });
//
//                RequestQueue queue = Volley.newRequestQueue(context);
//                queue.add(jsonObjectRequest);
//                                databaseReference.child("posts/" + currentData.getPostId()).runTransaction(new Transaction.Handler() {
//                                    @Override
//                                    public Transaction.Result doTransaction(MutableData mutableData) {
//                                        PostsPOJO postsPOJO = mutableData.getValue(PostsPOJO.class);
//
//                                        if (postsPOJO == null) {
//                                            return Transaction.success(mutableData);
//                                        }
//
//                                        if (postsPOJO.getLikes() != null) {
//
//                                            if (postsPOJO.getLikes().containsKey(firebaseUser.getUid())) {
//                                                // Unstar the post and remove self from stars
//                                                postsPOJO.likesCount = postsPOJO.likesCount - 1;
//                                                likesCount = likesCount - 1;
//                                                postsPOJO.getLikes().remove(firebaseUser.getUid());
//                                                //isLiked = false;
////                                ((MainActivity) context).runOnUiThread(new Runnable() {
////                                    @Override
////                                    public void run() {
////                                        btnHelpful.setImageResource(R.mipmap.ic_like);
////                                    }
////                                });
//                                                //txtLikes.setText(likesCount +" "+ context.getResources().getString(R.string.likes_dot));
//                                            } else {
//                                                // Star the post and add self to stars
//                                                postsPOJO.likesCount = postsPOJO.likesCount + 1;
//                                                likesCount = likesCount + 1;
//                                                postsPOJO.getLikes().put(firebaseUser.getUid(), true);
//                                                //isLiked = true;
////                                ((MainActivity) context).runOnUiThread(new Runnable() {
////                                    @Override
////                                    public void run() {
////                                        btnHelpful.setImageResource(R.mipmap.ic_like_active);
////                                    }
////                                });
//                                            }
//                                        }
//                                        // Set value and report transaction success
//                                        mutableData.setValue(postsPOJO);
//                                        return Transaction.success(mutableData);
//                                    }
//
//                                    @Override
//                                    public void onComplete(DatabaseError databaseError, boolean commited,
//                                                           DataSnapshot dataSnapshot) {
//
//                                        PostsPOJO postsPOJO = dataSnapshot.getValue(PostsPOJO.class);
//                                        if (commited) {
//                                            //transaction successfully completed
//
//                                            if (postsPOJO.getLikes() != null) {
//                                                txtLikes.setText(likesCount + " " + context.getResources().getString(R.string.likes_dot));
//                                                if (postsPOJO.getLikes().containsKey(firebaseUser.getUid())) {
//                                                    //btnHelpful.setImageResource(R.mipmap.ic_like_active);
//
//                                                    if (!firebaseUser.getUid().equals(postsPOJO.getuserid())) {
//                                                        //sendNotificationToUsers(postsPOJO.getPostid(), postsPOJO.getuserid());
//                                                        FirebasePushNotificationMethods.sendPostLikeNotification(currentData.getUserId(),
//                                                                firebaseUser.getUid(), currentData.getPostText(), currentData.getPostId(), context);
//                                                    }
//                                                    Toast.makeText(context, "liked", Toast.LENGTH_SHORT).show();
//                                                } else {
//                                                    Toast.makeText(context, "unliked", Toast.LENGTH_SHORT).show();
//                                                    //btnHelpful.setImageResource(R.mipmap.ic_like);
//                                                }
//                                            }
//                                        } else {
//                                            //aborted or an error occurred
////
//                                            Toast.makeText(context, "liked revoked", Toast.LENGTH_SHORT).show();
////                            btnHelpful.setImageResource(R.mipmap.ic_like);
//                                        }
//
//                                        // Transaction completed
//                                        Log.d("transaction completed", "postTransaction:onComplete:" + databaseError);
//                                    }
//                                });


                            }
                        });

                        // Converting timestamp into X ago format
                        CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(
                                Long.parseLong(String.valueOf(currentData.getTimestamp())),
                                System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);

                        txtTimeStamp.setText(timeAgo);
                        txtLocation.setText(currentData.getLocation());

                        txtSecondary.setText(currentData.getTown());

                        txtStatusMsg.setText(currentData.getPostText());


                        imgProfilePic.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(NotificationPostActivity.this, ProfileActivity.class);
                                NotificationPostActivity.this.startActivity(intent);
                            }
                        });
                        
                        
                        progressBar.setVisibility(View.GONE);
                        parentLayout.setVisibility(View.VISIBLE);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(NotificationPostActivity.this,"Error retreiving post data!",Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("VolleyError", error.toString());
                progressBar.setVisibility(View.GONE);
                Toast.makeText(NotificationPostActivity.this,"Connection Error!",Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue queue = Volley.newRequestQueue(NotificationPostActivity.this);
        queue.add(jsonObjectRequest);


//        databaseReference.child("posts/"+postId).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                final PostsPOJO currentData = dataSnapshot.getValue(PostsPOJO.class);
//                //currentData.setPostid(dataSnapshot.getKey());
//                //Log.d("dadaNotify",currentData.toString());
//                if (currentData.getcontent_post().size() == 0)
//                {
//                    viewPager.setVisibility(View.GONE);
//                    tabLayout.setVisibility(View.GONE);
//                }
//                else if(currentData.getcontent_post().size() > 1)
//                {
//                    //postContentViewPagerAdapter = new PostContentViewPagerAdapter(NotificationPostActivity.this,currentData.getcontent_post());
//                    viewPager.setAdapter(postContentViewPagerAdapter);
//                    viewPager.setVisibility(View.VISIBLE);
//                    tabLayout.setVisibility(View.VISIBLE);
//                    tabLayout.setupWithViewPager(viewPager, true);
//                }
//                else if(currentData.getcontent_post().size() == 1)
//                {
//                    //postContentViewPagerAdapter = new PostContentViewPagerAdapter(NotificationPostActivity.this,currentData.getcontent_post());
//                    viewPager.setAdapter(postContentViewPagerAdapter);
//                    tabLayout.setVisibility(View.GONE);
//                    viewPager.setVisibility(View.VISIBLE);
//                }
//
//                txtName.setText(currentData.getusername());
//
//                StorageReference filePath = storageReference.child("profilepictures").child(currentData.getprofilepicture());
//                filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                    @Override
//                    public void onSuccess(Uri uri) {
//                        try {
//                            GlideApp.with(NotificationPostActivity.this)
//                                    .load(uri)
//                                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
//                                    .circleCrop()
//                                    .transition(DrawableTransitionOptions.withCrossFade(1000))
//                                    .error(R.color.link)
//                                    .into(imgProfilePic);
//                        } catch (Exception ex) {
//                            Log.d("error", ex.toString());
//                        }
//                    }
//                });
//
//                likes = currentData.getLikes().size() == 0 ? 0 : currentData.getLikes().size();
//                //int comments = currentData.getcomments().size() == 0 ? 0 : currentData.getLikes().size();
//
//                txtLikesAndComments.setText(likes+" Likes • 0 Comments");
//
//                // Converting timestamp into X ago format
//                CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(
//                        Long.parseLong(String.valueOf(currentData.gettimestamp())),
//                        System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
//
//                txtTimeStamp.setText(timeAgo);
//                txtLocation.setText(currentData.getlocation());
//
//                txtSecondary.setText(currentData.getsecondarylocation());
//
//                txtStatusMsg.setText(currentData.getposttext());
//
//                progressBar.setVisibility(View.GONE);
//                parentLayout.setVisibility(View.VISIBLE);
//
//
//
//                imgProfilePic.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        Intent intent = new Intent(NotificationPostActivity.this, ProfileActivity.class);
//                        NotificationPostActivity.this.startActivity(intent);
//                    }
//                });
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                progressBar.setVisibility(View.GONE);
//            }
//        });

        //PostsPOJO currentData = newsFeedItemPOJOs.get(position);


    }
}
