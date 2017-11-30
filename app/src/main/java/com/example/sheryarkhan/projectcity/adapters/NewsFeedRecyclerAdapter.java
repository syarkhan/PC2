package com.example.sheryarkhan.projectcity.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.example.sheryarkhan.projectcity.activities.SampleActivity;
import com.example.sheryarkhan.projectcity.utils.Constants;
import com.example.sheryarkhan.projectcity.utils.FirebasePushNotificationMethods;
import com.example.sheryarkhan.projectcity.utils.IVolleyResult;
import com.example.sheryarkhan.projectcity.utils.SharedPrefs;
import com.example.sheryarkhan.projectcity.utils.VolleyService;
import com.example.sheryarkhan.projectcity.activities.CommentsActivity;
import com.example.sheryarkhan.projectcity.activities.MainActivity;
import com.example.sheryarkhan.projectcity.activities.PostNewsActivity;
import com.example.sheryarkhan.projectcity.Glide.GlideApp;
import com.example.sheryarkhan.projectcity.activities.ProfileActivity;
import com.example.sheryarkhan.projectcity.R;
import com.facebook.ads.Ad;
import com.facebook.ads.AdChoicesView;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdSettings;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdView;
import com.facebook.ads.NativeAdsManager;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import data.NewPostProgress;
import data.Post;
import data.PostsPOJO;

/**
 * Created by Sheryar Khan on 8/1/2017.
 */

public class NewsFeedRecyclerAdapter extends RecyclerView.Adapter<NewsFeedRecyclerAdapter.MainViewHolder> {

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
    private SharedPrefs sharedPrefs;
    //private FirebaseAuth firebaseAuth;
    //private final FirebaseUser firebaseUser;

    private static final int TYPE_SHARE_NEWS = 1;
    private static final int TYPE_NEWS_POST = 2;
    private static final int TYPE_FB_AD = 3;
    private static final int TYPE_PROGRESS = 4;
    private static final int TYPE_NEW_POST_PROGRESS = 5;
    public static int AD_FORM = 3;

    private NativeAdsManager nativeAdsManager;
    private NativeAd nativeAd;

    private List<Object> postsList;

    //private DatabaseReference databaseReference;

    private IVolleyResult mResultCallback;
    private VolleyService mVolleyService;

    private OnLoadMoreListener onLoadMoreListener;
    private boolean isMoreLoading = true;
    private boolean isLiking_Disliking = false;


    public NewsFeedRecyclerAdapter(List<Object> newsFeedItems, Context context, NativeAdsManager ads) {
        this.context = context;
        this.postsList = newsFeedItems;
        storageReference = FirebaseStorage.getInstance().getReference();
        //databaseReference = FirebaseDatabase.getInstance().getReference();
        //firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        sharedPrefs = new SharedPrefs(context);
        this.nativeAdsManager = ads;
        //this.nativeAd = nativeAd;
        //this.onLoadMoreListener = onLoadMoreListener;
        //firebaseUser = firebaseAuth.getCurrentUser();

        mResultCallback = new IVolleyResult() {
            @Override
            public void notifySuccess(int requestType, JSONObject response) {
                Log.d("volleydadasuccess", "Volley requester " + String.valueOf(requestType));
                Log.d("volleydadasuccess", "Volley JSON post" + response);
            }

            @Override
            public void notifyError(int requestType, VolleyError error) {
                Log.d("volleydadaerror", "Volley requester " + String.valueOf(requestType));
                Log.d("volleydadaerror", "Volley JSON post" + "That didn't work! " + error.toString());
            }
        };
        //inflater = LayoutInflater.from(context);


    }


    public interface OnLoadMoreListener {
        void onLoadMore1();
    }


    public void setMore(boolean isMore) {
        this.isMoreLoading = isMore;
    }

    public void showLoading() {
        if (postsList != null) {
            //isMoreLoading = false;
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    postsList.add(null);
                    Log.d("postsobject",postsList.toString());
                    notifyItemInserted(postsList.size() - 1);
                    //onLoadMoreListener.onLoadMore1();
                }
            });
        }
    }

    public void dismissLoading() {
        if (postsList != null && postsList.size() > 0) {
            postsList.remove(postsList.size() - 1);
            notifyItemRemoved(postsList.size());
        }
    }

    public void dismissNewPostProgress() {
        if (postsList != null && postsList.size() > 0) {
            postsList.remove(1);
            notifyItemRemoved(postsList.size());
        }
    }

    public void updateNewPostProgress(int progress, NewPostProgressViewHolder holder) {

    }


    public void addAll(List<Post> lst) {
        postsList.clear();
        postsList.addAll(lst);
        notifyDataSetChanged();
    }

    public void addItemMore(List<Object> lst) {
        int sizeInit = lst.size();
        postsList.addAll(lst);
        notifyItemRangeChanged(sizeInit, postsList.size());
    }

    public void clear() {
        postsList.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        try {
            return (postsList.size()); //Plus 1 for the Share news layout at the top
        } catch (Exception ex) {

        }
        return 0;
    }

    @Override
    public int getItemViewType(int position) {

        if (position == 0) {
            return TYPE_SHARE_NEWS;
        } else if (postsList.get(position) == null) {
            return TYPE_PROGRESS;
        } else if ( position == 11 || position == 21) {
            return TYPE_FB_AD;
        } else if(postsList.get(position) instanceof NewPostProgress){
            return TYPE_NEW_POST_PROGRESS;
        }
        else {
            return TYPE_NEWS_POST;
        }

    }


    @Override
    public MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        //context = parent.getContext();
//        return new PostViewHolder(LayoutInflater.from(context).inflate(R.layout.news_feed_list_item, parent, false));

        switch (viewType) {
            case TYPE_NEWS_POST:
                return new PostViewHolder(LayoutInflater.from(context).inflate(R.layout.news_feed_list_item, parent, false));

            case TYPE_SHARE_NEWS:
                return new ShareNewsPostViewHolder(LayoutInflater.from(context).inflate(R.layout.share_news_item_layout, parent, false));

            case TYPE_PROGRESS:
                return new ProgressBarViewHolder(LayoutInflater.from(context).inflate(R.layout.progress_list_item, parent, false));

            case TYPE_FB_AD:
                return new FBAdViewHolder(LayoutInflater.from(context).inflate(R.layout.fb_ad_layout, parent, false));

            case TYPE_NEW_POST_PROGRESS:
                return new NewPostProgressViewHolder(LayoutInflater.from(context).inflate(R.layout.new_post_progress_item, parent, false));

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

        //context = holder.itemView.getContext();
        //final Context context = holder.itemView.getContext();
        if (holder.getItemViewType() == TYPE_NEWS_POST) {

            PostViewHolder mholder = (PostViewHolder) holder;
            setUpPictureView(context, mholder, holder.getAdapterPosition());
        } else if (holder.getItemViewType() == TYPE_SHARE_NEWS) {


            final ShareNewsPostViewHolder mholder = (ShareNewsPostViewHolder) holder;

            setUpShareNewsView(context, mholder);


        } else if (holder.getItemViewType() == TYPE_FB_AD) {
            final FBAdViewHolder mholder = (FBAdViewHolder) holder;
            setUpFBAdView(context, mholder);
//            if (mAd != null) {
//                setUpFBAdView(context, mholder, mAd);
//            }else if (mAds != null && mAds.isLoaded()) {
//                mAd = mAds.nextNativeAd();
//                setUpFBAdView(context, mholder, mAd);
//            }
//            else {
//                setUpFBAdView(context, mholder, null);
//            }

        }else if(holder.getItemViewType() == TYPE_NEW_POST_PROGRESS){
            final NewPostProgressViewHolder mholder = (NewPostProgressViewHolder) holder;
            setUpNewPostProgress(mholder, context);
        }
//        //set height in proportion to screen size
//        int proportionalHeight = UIUtil.containerHeight((MainActivity) mCntx);
//        TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, proportionalHeight); // (width, height)
//        holder.container.setLayoutParams(params);

    }

    public void setUpNewPostProgress(NewPostProgressViewHolder mholder, Context context) {
        final NewPostProgress currentData = (NewPostProgress) postsList.get(mholder.getAdapterPosition());
        mholder.progressBar.setProgress(currentData.getProgress());
    }


    public void setUpFBAdView(final Context context, final FBAdViewHolder mholder) {

        //nativeAd = nativeAdsManager.nextNativeAd();

        //NativeAdView.render()

//        try {
//            mholder.templateContainer.removeViewInLayout(mholder.adView);
//        }catch (Exception ex){
//            ex.printStackTrace();
//        }
//
//        mholder.templateContainer.addView(mholder.adView);

        AdSettings.addTestDevice("d799c50e3b4c9e58d4412f125770179b");
        final NativeAd nativeAd = new NativeAd(context, "2051713708390959_2051806708381659");
        nativeAd.setAdListener(new AdListener() {
            @Override
            public void onError(Ad ad, AdError adError) {
                // Ad error callback
                Log.d("AdError", adError.toString());
            }

            @Override
            public void onAdLoaded(Ad ad) {
                Log.d("AdLoaded", ad.toString());
                if (ad != nativeAd) {
                    return;
                }

                mholder.mAdTitle.setText(nativeAd.getAdTitle());
                mholder.mAdBody.setText(nativeAd.getAdBody());
                mholder.mAdSocialContext.setText(nativeAd.getAdSocialContext());
                mholder.mAdCallToAction.setText(nativeAd.getAdCallToAction());
                mholder.mAdMedia.setNativeAd(nativeAd);
                NativeAd.Image adIcon = nativeAd.getAdIcon();
                NativeAd.downloadAndDisplayImage(adIcon, mholder.mAdIcon);


                AdChoicesView adChoicesView = new AdChoicesView(context, nativeAd, true);
                mholder.adChoicesContainer.addView(adChoicesView);


                // Register the Title and CTA button to listen for clicks.
                List<View> clickableViews = new ArrayList<>();
                clickableViews.add(mholder.mAdTitle);
                clickableViews.add(mholder.mAdCallToAction);
                //nativeAd.registerViewForInteraction(mholder.adView);
                //nativeAd.registerViewForInteraction(mholder.nativeAdContainer,clickableViews);

            }

            @Override
            public void onAdClicked(Ad ad) {
                // Ad clicked callback
                Log.d("AdClicked", ad.toString());
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // Ad impression logged callback
                Log.d("AdImpression", ad.toString());
            }
        });

        nativeAd.loadAd();
//        if (ad == null) {
//            if (AD_FORM == 2) {
//                mholder.mAdSocialContext.setText("No Ad");
//            }
//            else {
//                mholder.mAdTitle.setText("No Ad");
//                mholder.mAdBody.setText("Ad is not loaded.");
//            }
//        }else {
//            if (AD_FORM == 2) {
//                mholder.mAdSocialContext.setText(ad.getAdSocialContext());
//                mholder.mAdCallToAction.setText(ad.getAdCallToAction());
//                mholder.mAdMedia.setNativeAd(ad);
//            }
//            else {
//                mholder.mAdTitle.setText(ad.getAdTitle());
//                mholder.mAdBody.setText(ad.getAdBody());
//                mholder.mAdSocialContext.setText(ad.getAdSocialContext());
//                mholder.mAdCallToAction.setText(ad.getAdCallToAction());
//                mholder.mAdMedia.setNativeAd(ad);
//                NativeAd.Image adIcon = ad.getAdIcon();
//                NativeAd.downloadAndDisplayImage(adIcon, mholder.mAdIcon);
//            }
//        }
    }

    public void loadFBAd() {

    }

    private void setUpShareNewsView(final Context context, ShareNewsPostViewHolder mholder) {
        if (sharedPrefs.getProfilePictureFromSharedPref().equals("")) {
            StorageReference filePath = storageReference.child("profilepictures").child("profilepic:" + sharedPrefs.getUserIdFromSharedPref());
            try {
                GlideApp.with(context)
                        .load(filePath)
                        .circleCrop()
                        .transition(DrawableTransitionOptions.withCrossFade(1000))
                        .error(R.color.link)
                        .into(mholder.imgProfilePic);
            } catch (Exception ex) {
                Log.d("error", ex.toString());
            }
        } else {
            try {
                GlideApp.with(context)
                        .load(mholder.profilePicturePath)
                        .circleCrop()
                        .transition(DrawableTransitionOptions.withCrossFade(1000))
                        .error(R.drawable.circle_image)
                        .into(mholder.imgProfilePic);
            } catch (Exception ex) {
                Log.d("error", ex.toString());
            }
        }
        mholder.imgProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ProfileActivity.class);
                context.startActivity(intent);
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

//    protected void postAndNotifyAdapter(final Handler handler, final RecyclerView recyclerView, final RecyclerView.Adapter adapter) {
//        handler.post(new Runnable() {
//            @Override
//            public void run() {
//                if (!recyclerView.isComputingLayout()) {
//                    adapter.notifyDataSetChanged();
//                } else {
//                    postAndNotifyAdapter(handler, recyclerView, adapter);
//                }
//            }
//        });
//    }

//    @Override
//    public void onViewRecycled(MainViewHolder holder) {
//        super.onViewRecycled(holder);
//        int position = holder.getAdapterPosition();
//
////        switch (getItemViewType(position))
////        {
////            case TYPE_NEWS_POST:
////                PostViewHolder mholder = (PostViewHolder) holder;
////                mholder.txtName.setText("");
////
////            case TYPE_SHARE_NEWS:
////
////        }
//        if(position > 0)
//        {
//            PostViewHolder mholder = (PostViewHolder) holder;
//            mholder.txtName.setText("");
//        }
//
//
//    }


    private void setUpPictureView(final Context context, final PostViewHolder mholder, final int position) {

        final Post currentData = (Post) postsList.get(position);

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

        if (currentData.getLikes().size() > 0) {

            //mholder.isLiked = currentData.getLikes().contains(sharedPrefs.getUserIdFromSharedPref());

            if (currentData.getLikes().contains(sharedPrefs.getUserIdFromSharedPref())) {
                mholder.btnHelpful.setTag("like_active");
                mholder.btnHelpful.setImageResource(R.mipmap.ic_like_active);
                //mholder.isLiked = true;
            } else {
                mholder.btnHelpful.setTag("like");
                mholder.btnHelpful.setImageResource(R.mipmap.ic_like);
            }
        } else {
            mholder.btnHelpful.setTag("like");
            mholder.btnHelpful.setImageResource(R.mipmap.ic_like);
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


                //boolean isLiking_Disliking = false;
//                WifiManager wifi = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
//                if (!wifi.isWifiEnabled()) {
//                    //wifi is not enabled
//                    HelperFunctions.getToastShort(context, "No internet connection!");
//                    return;
//                }

                Map<String, Map<String, Object>> Data = new HashMap<>();

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


                Data.put("PostData", PostData);
                Data.put("NotificationData", NotificationData);

                view.startAnimation(AnimationUtils.loadAnimation(context, R.anim.image_resize));
                if (mholder.btnHelpful.getTag().equals("like")) {
                    mholder.btnHelpful.setImageResource(R.mipmap.ic_like_active);
                    mholder.btnHelpful.setTag("like_active");
                } else if (mholder.btnHelpful.getTag().equals("like_active")) {
                    mholder.btnHelpful.setImageResource(R.mipmap.ic_like);
                    mholder.btnHelpful.setTag("like");
                }

//                final String likeAddURL = Constants.protocol + Constants.IP +
//                        Constants.addUserToPostLikes + "/" + sharedPrefs.getUserIdFromSharedPref() + "&" + currentData.getPostId();

                String likeAddOrRemoveURL;
                final int state;
                Log.d("likescountbefore1",String.valueOf(mholder.likesCount));

                if (((Post) postsList.get(position)).getLikes().contains(sharedPrefs.getUserIdFromSharedPref())) {

                    state = 2; // state 2 for REMOVAL OF USER FROM LIKES
                    likeAddOrRemoveURL = Constants.protocol + Constants.IP +
                            Constants.addOrRemoveUserLikeToPost + "/" + sharedPrefs.getUserIdFromSharedPref() + "&" +
                            currentData.getPostId() + "&" + state;


                } else {
                    state = 1; // state 1 for ADDITION OF USER TO LIKES
                    likeAddOrRemoveURL = Constants.protocol + Constants.IP +
                            Constants.addOrRemoveUserLikeToPost + "/" + sharedPrefs.getUserIdFromSharedPref() + "&" +
                            currentData.getPostId() + "&" + state;
                }


                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, likeAddOrRemoveURL, new JSONObject(Data), new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("volleyadd", response.toString());

                        //isLiking_Disliking=false;
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
                            mholder.btnHelpful.setTag("like");
                            Toast.makeText(context, "Did not succeed", Toast.LENGTH_SHORT).show();
                        } else {

                            int status = 2;
                            try {
                                status = response.getInt("status");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            if (status == 0) {
                                return;
                            } else {
                                //notifyItemChanged(position);
                                //change icon like to like-active
                                Log.d("beforelikes2", ((Post) postsList.get(position)).getLikes().toString());
                                // state 1 for ADDITION OF USER TO LIKES
                                // state 2 for REMOVAL OF USER FROM LIKES

                                if (state == 2) {
                                    mholder.likesCount = mholder.likesCount - 1;
                                    Log.d("likescount--3",String.valueOf(mholder.likesCount));
                                    ((Post) postsList.get(position)).setLikesCount(mholder.likesCount);
                                    ((Post) postsList.get(position)).removeUserFromLikesList(sharedPrefs.getUserIdFromSharedPref());
                                    mholder.txtLikes.setText(currentData.getLikesCount() + " " +
                                            context.getResources().getString(R.string.likes_dot));
                                    mholder.btnHelpful.setImageResource(R.mipmap.ic_like);
                                    mholder.btnHelpful.setTag("like");
                                    notifyItemChanged(position);
                                    //change icon like-active to like
//                                    ((MainActivity) context).runOnUiThread(new Runnable() {
//                                        @Override
//                                        public void run() {

//                                        }
//                                    });
                                } else if (state == 1) {
                                    mholder.likesCount = mholder.likesCount + 1;
                                    Log.d("likescount++3",String.valueOf(mholder.likesCount));
                                    ((Post) postsList.get(position)).setLikesCount(mholder.likesCount);
                                    ((Post) postsList.get(position)).addUserToLikesList(sharedPrefs.getUserIdFromSharedPref());
                                    mholder.txtLikes.setText(currentData.getLikesCount() + " " +
                                            context.getResources().getString(R.string.likes_dot));
                                    mholder.btnHelpful.setImageResource(R.mipmap.ic_like_active);
                                    mholder.btnHelpful.setTag("like_active");
                                    notifyItemChanged(position);

                                    //change icon like to like-active
//                                    ((MainActivity) context).runOnUiThread(new Runnable() {
//                                        @Override
//                                        public void run() {

//                                        }
//                                    });
                                }
                            }

                            Log.d("afterlikes4", ((Post) postsList.get(position)).getLikes().toString());
//                                if (state == 1) {
//                                    mholder.likesCount=mholder.likesCount+1;
//                                    ((Post)postsList.get(position)).setLikesCount(mholder.likesCount);
//                                    ((Post)postsList.get(position)).addUserToLikesList(sharedPrefs.getUserIdFromSharedPref());
//                                    mholder.txtLikes.setText(currentData.getLikesCount() + " " +
//                                            context.getResources().getString(R.string.likes_dot));
//                                    notifyItemChanged(position);
//
//                                    //change icon like to like-active
////                                    ((MainActivity) context).runOnUiThread(new Runnable() {
////                                        @Override
////                                        public void run() {
//                                            mholder.btnHelpful.setImageResource(R.mipmap.ic_like_active);
////                                        }
////                                    });
//                                } else if(state == 2) {
//
//                                    mholder.likesCount=mholder.likesCount-1;
//                                    ((Post)postsList.get(position)).setLikesCount(mholder.likesCount);
//                                    ((Post)postsList.get(position)).removeUserFromLikesList(sharedPrefs.getUserIdFromSharedPref());
//                                    mholder.txtLikes.setText(currentData.getLikesCount() + " " +
//                                            context.getResources().getString(R.string.likes_dot));
//                                    notifyItemChanged(position);
//                                    //change icon like-active to like
////                                    ((MainActivity) context).runOnUiThread(new Runnable() {
////                                        @Override
////                                        public void run() {
//                                            mholder.btnHelpful.setImageResource(R.mipmap.ic_like);
////                                        }
////                                    });
//                                }
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
                    //isLiking_Disliking = true;




//                databaseReference.child("posts/" + currentData.getPostId()).runTransaction(new Transaction.Handler() {
//                    @Override
//                    public Transaction.Result doTransaction(MutableData mutableData) {
//                        PostsPOJO postsPOJO = mutableData.getValue(PostsPOJO.class);
//
//                        if (postsPOJO == null) {
//                            return Transaction.success(mutableData);
//                        }
//
//                        if (postsPOJO.getLikes() != null) {
//
//                            if (postsPOJO.getLikes().containsKey(firebaseUser.getUid())) {
//                                // Unstar the post and remove self from stars
//                                postsPOJO.likesCount = postsPOJO.likesCount - 1;
//                                mholder.likesCount = mholder.likesCount - 1;
//                                postsPOJO.getLikes().remove(firebaseUser.getUid());
//                                //isLiked = false;
////                                ((MainActivity) context).runOnUiThread(new Runnable() {
////                                    @Override
////                                    public void run() {
////                                        mholder.btnHelpful.setImageResource(R.mipmap.ic_like);
////                                    }
////                                });
//                                //mholder.txtLikes.setText(likesCount +" "+ context.getResources().getString(R.string.likes_dot));
//                            } else {
//                                // Star the post and add self to stars
//                                postsPOJO.likesCount = postsPOJO.likesCount + 1;
//                                mholder.likesCount = mholder.likesCount + 1;
//                                postsPOJO.getLikes().put(firebaseUser.getUid(), true);
//                                //isLiked = true;
////                                ((MainActivity) context).runOnUiThread(new Runnable() {
////                                    @Override
////                                    public void run() {
////                                        mholder.btnHelpful.setImageResource(R.mipmap.ic_like_active);
////                                    }
////                                });
//                            }
//                        }
//                        // Set value and report transaction success
//                        mutableData.setValue(postsPOJO);
//                        return Transaction.success(mutableData);
//                    }
//
//                    @Override
//                    public void onComplete(DatabaseError databaseError, boolean commited,
//                                           DataSnapshot dataSnapshot) {
//
//                        PostsPOJO postsPOJO = dataSnapshot.getValue(PostsPOJO.class);
//                        if (commited) {
//                            //transaction successfully completed
//
//                            if (postsPOJO.getLikes() != null) {
//                                mholder.txtLikes.setText(mholder.likesCount + " " + context.getResources().getString(R.string.likes_dot));
//                                if (postsPOJO.getLikes().containsKey(firebaseUser.getUid())) {
//                                    //mholder.btnHelpful.setImageResource(R.mipmap.ic_like_active);
//
//                                    if (!firebaseUser.getUid().equals(postsPOJO.getuserid())) {
//                                        //sendNotificationToUsers(postsPOJO.getPostid(), postsPOJO.getuserid());
//                                        FirebasePushNotificationMethods.sendPostLikeNotification(currentData.getUserId(),
//                                                firebaseUser.getUid(), currentData.getPostText(), currentData.getPostId(), context);
//                                    }
//                                    Toast.makeText(context, "liked", Toast.LENGTH_SHORT).show();
//                                } else {
//                                    Toast.makeText(context, "unliked", Toast.LENGTH_SHORT).show();
//                                    //mholder.btnHelpful.setImageResource(R.mipmap.ic_like);
//                                }
//                            }
//                        } else {
//                            //aborted or an error occurred
////
//                            Toast.makeText(context, "liked revoked", Toast.LENGTH_SHORT).show();
////                            mholder.btnHelpful.setImageResource(R.mipmap.ic_like);
//                        }
//
//                        // Transaction completed
//                        Log.d("transaction completed", "postTransaction:onComplete:" + databaseError);
//                    }
//                });


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

    private void sendNotificationToUsers(String key, String userid) {
        Map<String, String> townAndMessage = new HashMap<>();
        townAndMessage.put("town", "user_" + userid);
        townAndMessage.put("message", "Like/Comment Notifcation");
        townAndMessage.put("postId", key);
        mVolleyService = new VolleyService(mResultCallback, context);
        mVolleyService.postDataVolley(Request.Method.POST,
                Constants.protocol + Constants.IP + "/sendNotification",
                new JSONObject(townAndMessage));
    }

    private class ShareNewsPostViewHolder extends MainViewHolder {

        TextView txtShareNews;
        ImageView imgProfilePic;
        ConstraintLayout shareNewsLayout;
        String profilePicturePath = "";

        private ShareNewsPostViewHolder(View itemView) {
            super(itemView);

            txtShareNews = (TextView) itemView.findViewById(R.id.txtShareNews);
            shareNewsLayout = (ConstraintLayout) itemView.findViewById(R.id.shareNewsLayout);
            imgProfilePic = (ImageView) itemView.findViewById(R.id.imgProfilePic);
            txtShareNews.setTypeface(ROBOTO_FONT_THIN);
            profilePicturePath = sharedPrefs.getProfilePictureFromSharedPref();
        }

    }


    private class ProgressBarViewHolder extends MainViewHolder {

        ProgressBar progressBar;

        private ProgressBarViewHolder(View itemView) {
            super(itemView);

            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar);
        }

    }


    private class FBAdViewHolder extends MainViewHolder {

        private CardView nativeAdContainer;
        protected LinearLayout templateContainer;
        private CardView adView;
        private MediaView mAdMedia;
        private ImageView mAdIcon;
        private TextView mAdTitle;
        private TextView mAdBody;
        private TextView mAdSocialContext;
        private Button mAdCallToAction;
        private LinearLayout adChoicesContainer;

        private FBAdViewHolder(View itemView) {
            super(itemView);

//            if (AD_FORM == 2) {
//                mAdMedia = (MediaView) itemView.findViewById(R.id.native_ad_media);
//                mAdSocialContext = (TextView) itemView.findViewById(R.id.native_ad_social_context);
//                mAdCallToAction = (Button) itemView.findViewById(R.id.native_ad_call_to_action);
//            } else {
//            nativeAdContainer = (CardView) itemView.findViewById(R.id.main);
//            LayoutInflater inflater = LayoutInflater.from(context);
//            adView = (CardView) inflater.inflate(R.layout.fb_ad_layout, nativeAdContainer, false);
//            nativeAdContainer.addView(adView);

            mAdMedia = (MediaView) itemView.findViewById(R.id.native_ad_media);
            mAdTitle = (TextView) itemView.findViewById(R.id.native_ad_title);
            mAdBody = (TextView) itemView.findViewById(R.id.native_ad_body);
            mAdSocialContext = (TextView) itemView.findViewById(R.id.native_ad_social_context);
            mAdCallToAction = (Button) itemView.findViewById(R.id.native_ad_call_to_action);
            mAdIcon = (ImageView) itemView.findViewById(R.id.native_ad_icon);
            adChoicesContainer = (LinearLayout) itemView.findViewById(R.id.ad_choices_container);
            //}
        }

    }

    private class NewPostProgressViewHolder extends MainViewHolder {
        ProgressBar progressBar;
        int progress = 0;

        public NewPostProgressViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar)itemView.findViewById(R.id.progressBar);
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

//    public class OnlyPostVideoViewHolder extends MainViewHolder {
//
//        int Id;
//        TextView txtName;
//        TextView txtTimeStamp;
//        TextView txtStatusMsg;
//        TextView txtUrl;
//        ImageView imgProfilePic;
//        ImageView imgPost;
//
//
//        public OnlyPostVideoViewHolder(View itemView) {
//
//            super(itemView);
//
//            //itemView.setOnClickListener(this);
//
//            Id = 0;
//            txtName = (TextView) itemView.findViewById(R.id.txtName);
//            txtTimeStamp = (TextView) itemView.findViewById(R.id.txtTimeStamp);
//            txtStatusMsg = (TextView) itemView.findViewById(R.id.txtStatusMsg);
//            txtUrl = (TextView) itemView.findViewById(R.id.txtUrl);
//            imgPost = (ImageView) itemView.findViewById(R.id.imgPost);
//            imgProfilePic = (ImageView) itemView.findViewById(R.id.imgProfilePic);
//
//
//        }
//
//
//    }


