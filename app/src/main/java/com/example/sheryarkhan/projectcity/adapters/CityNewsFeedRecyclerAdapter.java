package com.example.sheryarkhan.projectcity.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.sheryarkhan.projectcity.activities.CommentsActivity;
import com.example.sheryarkhan.projectcity.activities.MainActivity;
import com.example.sheryarkhan.projectcity.activities.PostNewsActivity;
import com.example.sheryarkhan.projectcity.activities.ProfileActivity;
import com.example.sheryarkhan.projectcity.utils.Constants;
import com.example.sheryarkhan.projectcity.utils.SharedPrefs;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import data.Post;
import data.PostsPOJO;

/**
 * Created by Sheryar Khan on 10/26/2017.
 */


public class CityNewsFeedRecyclerAdapter extends RecyclerView.Adapter<CityNewsFeedRecyclerAdapter.MainViewHolder> {

    public final int REQUEST_CODE_FOR_POST_NEWS = 555;
    //private Activity activity;
    //private LayoutInflater inflater;
    private Context context;
    private Typeface ROBOTO_FONT_THIN;
    private Typeface ROBOTO_FONT_REGULAR;
    private Typeface ROBOTO_FONT_BOLD;
    private Typeface ROBOTO_FONT_LIGHT;

    private PostContentViewPagerAdapter postContentViewPagerAdapter;
    private StorageReference storageReference;
    //private FirebaseAuth firebaseAuth;
    //private final FirebaseUser firebaseUser;

    private static final int TYPE_SHARE_NEWS = 1;
    private static final int TYPE_NEWS_POST = 2;
    private static final int TYPE_FB_AD = 3;
    private static final int TYPE_PROGRESS = 4;

    private int likes;
    private int noOfComments;
    //private boolean isLiked;

    private List<Object> cityPostsList;
    private SharedPrefs sharedPrefs;

    private DatabaseReference databaseReference;

    public CityNewsFeedRecyclerAdapter(List<Object> newsFeedItems, Context context) {
        this.context = context;
        this.cityPostsList = newsFeedItems;
        storageReference = FirebaseStorage.getInstance().getReference();
//        databaseReference = FirebaseDatabase.getInstance().getReference();
//        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        sharedPrefs = new SharedPrefs(context);
        //firebaseUser = firebaseAuth.getCurrentUser();

        //inflater = LayoutInflater.from(context);


    }

    @Override
    public int getItemCount() {
        try {
            return (cityPostsList.size()); //Plus 1 for the Share news layout at the top
        } catch (Exception ex) {

        }
        return 0;
    }

    @Override
    public int getItemViewType(int position) {

        if (position == 0) {
            return TYPE_SHARE_NEWS;
        }
        else if(cityPostsList.get(position)==null){
            return TYPE_PROGRESS;
        }
        else {
            return TYPE_NEWS_POST;
        }
    }


    @Override
    public MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        context = parent.getContext();
//        return new PostViewHolder(LayoutInflater.from(context).inflate(R.layout.news_feed_list_item, parent, false));

        switch (viewType) {
            case TYPE_NEWS_POST:
                return new PostViewHolder(LayoutInflater.from(context).inflate(R.layout.news_feed_list_item, parent, false));

            case TYPE_SHARE_NEWS:
                return new ShareNewsPostViewHolder(LayoutInflater.from(context).inflate(R.layout.share_news_item_layout, parent, false));

            case TYPE_PROGRESS:
                return new ProgressBarViewHolder(LayoutInflater.from(context).inflate(R.layout.progress_list_item, parent, false));
        }
        return null;
//        View view = inflater.inflate(R.layout.news_feed_list_item,parent,false);
//        ViewHolder viewHolder = new ViewHolder(view);
//        return viewHolder;
    }


//    @Override
//    public int getItemViewType(int position) {
//        return super.getItemViewType(position);
//    }


    @Override
    public void onBindViewHolder(final MainViewHolder holder, final int position) {

        context = holder.itemView.getContext();
        //final Context context = holder.itemView.getContext();
        if (holder.getItemViewType() == TYPE_NEWS_POST) {


//            notifyDataSetChanged();
            PostViewHolder mholder = (PostViewHolder) holder;
            setUpPictureView(context, mholder, holder.getAdapterPosition());
        } else if (holder.getItemViewType() == TYPE_SHARE_NEWS) {

            final ShareNewsPostViewHolder mholder = (ShareNewsPostViewHolder) holder;

            try {
                GlideApp.with(context)
                        .load(mholder.profilePicturePath)
                        .circleCrop()
                        .transition(DrawableTransitionOptions.withCrossFade(1000))
                        .error(R.color.link)
                        .into(mholder.imgProfilePic);
            } catch (Exception ex) {
                Log.d("error", ex.toString());
            }
            mholder.imgProfilePic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ProfileActivity.class);
                    context.startActivity(intent);

                    //Toast.makeText(context,"KJAHDKJHASKJD",Toast.LENGTH_SHORT).show();
                }
            });
            mholder.shareNewsLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(context, PostNewsActivity.class);
                    ((Activity) context).startActivityForResult(intent, REQUEST_CODE_FOR_POST_NEWS);
                    Toast.makeText(context, "Share news post", Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

    private void setUpPictureView(final Context context, final PostViewHolder mholder, final int position) {

        final Post currentData = (Post) cityPostsList.get(position);

        if (currentData.getContentPost() == null) {
            mholder.viewPager.setVisibility(View.GONE);
            mholder.tabLayout.setVisibility(View.GONE);
        } else if (currentData.getContentPost().size() > 1) {
            postContentViewPagerAdapter = new PostContentViewPagerAdapter(context, currentData.getContentPost());
            mholder.viewPager.setAdapter(postContentViewPagerAdapter);
            mholder.viewPager.setVisibility(View.VISIBLE);
            mholder.tabLayout.setVisibility(View.VISIBLE);
            mholder.tabLayout.setupWithViewPager(mholder.viewPager, true);
        } else if (currentData.getContentPost().size() == 1) {
            postContentViewPagerAdapter = new PostContentViewPagerAdapter(context, currentData.getContentPost());
            mholder.viewPager.setAdapter(postContentViewPagerAdapter);
            mholder.tabLayout.setVisibility(View.GONE);
            mholder.viewPager.setVisibility(View.VISIBLE);
        }

        mholder.txtName.setText(currentData.getUserInfo().getUsername());

        if (currentData.getUserInfo().getProfilePicture() != null) {
            StorageReference filePath = storageReference.child("profilepictures").child(currentData.getUserInfo().getProfilePicture());
            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    try {
                        GlideApp.with(context)
                                .load(uri)
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .circleCrop()
                                .transition(DrawableTransitionOptions.withCrossFade(1000))
                                .error(R.color.link)
                                .into(mholder.imgProfilePic);
                    } catch (Exception ex) {
                        Log.d("error", ex.toString());
                    }
                }
            });
        } else {
            try {
                GlideApp.with(context)
                        .load(R.drawable.circle_image)
                        .circleCrop()
                        .transition(DrawableTransitionOptions.withCrossFade(1000))
                        .error(R.drawable.circle_image)
                        .into(mholder.imgProfilePic);
            } catch (Exception ex) {
                Log.d("error", ex.toString());
            }
        }

        mholder.likesCount = currentData.getLikesCount();
        mholder.commentsCount = currentData.getCommentsCount();

        if(currentData.getLikes().size() > 0) {

            //mholder.isLiked = currentData.getLikes().contains(sharedPrefs.getUserIdFromSharedPref());

            if (currentData.getLikes().contains(sharedPrefs.getUserIdFromSharedPref())) {
                mholder.btnHelpful.setTag("like_active");
                mholder.btnHelpful.setImageResource(R.mipmap.ic_like_active);
                //mholder.isLiked = true;
            }
        }

        mholder.txtLikes.setText(mholder.likesCount + " " + context.getResources().getString(R.string.likes_dot));
        mholder.txtComments.setText(mholder.commentsCount + " " + context.getResources().getString(R.string.comments));

        mholder.txtName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, CommentsActivity.class);
                context.startActivity(intent);
            }
        });

        mholder.btnComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity mainActivity = (MainActivity) context;
                mainActivity.onCommentButtonSelected(currentData.getUserId(), mholder.likesCount, mholder.commentsCount, context.getString(R.string.main_activity), currentData.getPostId());
                mainActivity.hideLayout();
            }
        });

        mholder.btnHelpful.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                WifiManager wifi = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
//                if (!wifi.isWifiEnabled()) {
//                    //wifi is not enabled
//                    HelperFunctions.getToastShort(context, "No internet connection!");
//                    return;
//                }

                Map<String,Map<String, Object>> Data = new HashMap<>();

                Map<String, Object> PostData = new HashMap<>();
                PostData.put("UserId", sharedPrefs.getUserIdFromSharedPref());
                PostData.put("PostId", currentData.getPostId());
                PostData.put("PostText", currentData.getPostText());
                PostData.put("timestamp", currentData.getTimestamp());

                Map<String, Object> NotificationData = new HashMap<>();
                NotificationData.put("ByUserId", sharedPrefs.getUserIdFromSharedPref());
                NotificationData.put("ToUserId", currentData.getUserId());
                NotificationData.put("PostId", currentData.getPostId());
                NotificationData.put("NotificationType", "post_like");
                NotificationData.put("Read", false);
                NotificationData.put("timestamp", currentData.getTimestamp());
                //PostCommentData.put("CommentId", timestamp.toString());


                Data.put("PostData",PostData);
                Data.put("NotificationData",NotificationData);

                view.startAnimation(AnimationUtils.loadAnimation(context, R.anim.image_resize));
                if (mholder.btnHelpful.getTag().equals("like")) {
                    mholder.btnHelpful.setImageResource(R.mipmap.ic_like_active);
                    mholder.btnHelpful.setTag("like_active");
                } else if (mholder.btnHelpful.getTag().equals("like_active")) {
                    mholder.btnHelpful.setImageResource(R.mipmap.ic_like);
                    mholder.btnHelpful.setTag("like");
                }

                final String likeAddOrRemoveURL = Constants.protocol + Constants.IP +
                        Constants.addOrRemoveUserLikeToPost + "/" + sharedPrefs.getUserIdFromSharedPref() + "&" + currentData.getPostId();

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, likeAddOrRemoveURL, new JSONObject(Data), new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("volleyadd", response.toString());

                        Boolean isSuccess = false;
                        try {
                            isSuccess = response.getBoolean("success");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (!isSuccess) {
                            //change icon like-active to like
                            ((MainActivity) context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mholder.btnHelpful.setImageResource(R.mipmap.ic_like);
                                }
                            });
                            Toast.makeText(context, "Did not succeed", Toast.LENGTH_SHORT).show();
                        } else {
                            //notifyItemChanged(position);
                            //change icon like to like-active
                            try {
                                int state = response.getInt("state");
                                // state 1 for ADDITION OF USER TO LIKES
                                // state 2 for REMOVAL OF USER FROM LIKES

                                if (state == 1) {

                                    mholder.likesCount=mholder.likesCount+1;
                                    ((Post)cityPostsList.get(position)).setLikesCount(mholder.likesCount);
                                    ((Post)cityPostsList.get(position)).addUserToLikesList(sharedPrefs.getUserIdFromSharedPref());
                                    mholder.txtLikes.setText(currentData.getLikesCount() + " " +
                                            context.getResources().getString(R.string.likes_dot));
                                    notifyItemChanged(position);

                                    //change icon like to like-active
//                                    ((MainActivity) context).runOnUiThread(new Runnable() {
//                                        @Override
//                                        public void run() {
                                    mholder.btnHelpful.setImageResource(R.mipmap.ic_like_active);
//                                        }
//                                    });
                                } else if(state == 2) {

                                    mholder.likesCount=mholder.likesCount-1;
                                    ((Post)cityPostsList.get(position)).setLikesCount(mholder.likesCount);
                                    ((Post)cityPostsList.get(position)).removeUserFromLikesList(sharedPrefs.getUserIdFromSharedPref());
                                    mholder.txtLikes.setText(currentData.getLikesCount() + " " +
                                            context.getResources().getString(R.string.likes_dot));
                                    notifyItemChanged(position);
                                    //change icon like-active to like
//                                    ((MainActivity) context).runOnUiThread(new Runnable() {
//                                        @Override
//                                        public void run() {
                                    mholder.btnHelpful.setImageResource(R.mipmap.ic_like);
//                                        }
//                                    });
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        //FirebasePushNotificationMethods.sendTownPostNotification(userid, key, txtPrimary, txtSecondary, editTextShareNews, MainActivity.this);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("VolleyError", error.toString());
                        ((MainActivity) context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mholder.btnHelpful.setImageResource(R.mipmap.ic_like);
                            }
                        });
                    }
                });

                RequestQueue queue = Volley.newRequestQueue(context);
                queue.add(jsonObjectRequest);

            }
        });

        // Converting timestamp into X ago format
        CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(
                Long.parseLong(String.valueOf(currentData.getTimestamp())),
                System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);

        mholder.txtTimeStamp.setText(timeAgo);
        mholder.txtLocation.setText(currentData.getLocation());

        mholder.txtSecondary.setText(currentData.getTown());

        mholder.txtStatusMsg.setText(currentData.getPostText());


        mholder.imgProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ProfileActivity.class);
                context.startActivity(intent);
            }
        });
    }


    public void showLoading() {
        if (cityPostsList != null) {
            //isMoreLoading = false;
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    cityPostsList.add(null);
                    notifyItemInserted(cityPostsList.size() - 1);
                    //onLoadMoreListener.onLoadMore1();
                }
            });
        }
    }
    public void dismissLoading() {
        if (cityPostsList != null && cityPostsList.size() > 0) {
            cityPostsList.remove(cityPostsList.size() - 1);
            notifyItemRemoved(cityPostsList.size());
        }
    }

    public class ShareNewsPostViewHolder extends MainViewHolder {

        TextView txtShareNews;
        ImageView imgProfilePic;
        ConstraintLayout shareNewsLayout;
        String profilePicturePath = "";

        public ShareNewsPostViewHolder(View itemView) {
            super(itemView);

            txtShareNews = (TextView) itemView.findViewById(R.id.txtShareNews);
            shareNewsLayout = (ConstraintLayout) itemView.findViewById(R.id.shareNewsLayout);
            imgProfilePic = (ImageView) itemView.findViewById(R.id.imgProfilePic);
            txtShareNews.setTypeface(ROBOTO_FONT_THIN);
            SharedPreferences sharedPref = context.getSharedPreferences("UserData", Context.MODE_PRIVATE);
            profilePicturePath = sharedPref.getString("profilepicture", "");
        }

//        ImageView imgProfilePic;


    }


    private class ProgressBarViewHolder extends CityNewsFeedRecyclerAdapter.MainViewHolder {

        ProgressBar progressBar;

        private ProgressBarViewHolder(View itemView) {
            super(itemView);

            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar);
        }

    }


    private class PostViewHolder extends MainViewHolder {

        int Id;
        TextView txtName;
        TextView txtTimeStamp;
        TextView txtStatusMsg;
        TextView txtLikes, txtLocation, txtSecondary, txtComments;
        ImageView imgProfilePic;
        ImageButton btnHelpful;
        ImageButton btnComments;
        ViewPager viewPager;
        TabLayout tabLayout;
        int likesCount;
        int commentsCount;
        boolean isLiked;

        private PostViewHolder(View itemView) {

            super(itemView);

            //itemView.setOnClickListener(this);
            Id = 0;
            viewPager = (ViewPager) itemView.findViewById(R.id.viewPager);
            tabLayout = (TabLayout) itemView.findViewById(R.id.pagerTabs);

            txtName = (TextView) itemView.findViewById(R.id.txtName);
            txtTimeStamp = (TextView) itemView.findViewById(R.id.txtTimeStampAndLocation);
            txtStatusMsg = (TextView) itemView.findViewById(R.id.txtStatusMsg);
            txtLikes = (TextView) itemView.findViewById(R.id.txtLikes);
            txtComments = (TextView) itemView.findViewById(R.id.txtComments);

            txtLocation = (TextView) itemView.findViewById(R.id.txtLocation);
            txtSecondary = (TextView) itemView.findViewById(R.id.txtSecondary);
            imgProfilePic = (ImageView) itemView.findViewById(R.id.imgProfilePic);

            btnHelpful = (ImageButton) itemView.findViewById(R.id.btnHelpful);
            btnComments = (ImageButton) itemView.findViewById(R.id.btnComments);


            txtName.setTypeface(ROBOTO_FONT_REGULAR);
            txtSecondary.setTypeface(ROBOTO_FONT_BOLD);
            txtStatusMsg.setTypeface(ROBOTO_FONT_LIGHT);
            txtTimeStamp.setTypeface(ROBOTO_FONT_LIGHT);
            txtLikes.setTypeface(ROBOTO_FONT_LIGHT);
            txtComments.setTypeface(ROBOTO_FONT_LIGHT);
            txtLocation.setTypeface(ROBOTO_FONT_LIGHT);

        }
    }


    public class MainViewHolder extends RecyclerView.ViewHolder {
        public MainViewHolder(View v) {
            super(v);
            ROBOTO_FONT_THIN = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Thin.ttf");
            ROBOTO_FONT_REGULAR = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Regular.ttf");
            ROBOTO_FONT_BOLD = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Bold.ttf");
            ROBOTO_FONT_LIGHT = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Light.ttf");
        }
    }
}
