package com.example.sheryarkhan.projectcity.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
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
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.sheryarkhan.projectcity.Glide.GlideApp;
import com.example.sheryarkhan.projectcity.R;
import com.example.sheryarkhan.projectcity.activities.CommentsActivity;
import com.example.sheryarkhan.projectcity.activities.PostNewsActivity;
import com.example.sheryarkhan.projectcity.activities.ProfileActivity;
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

import java.util.Collections;
import java.util.List;

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
    private final FirebaseUser firebaseUser;

    private static final int TYPE_SHARE_NEWS = 1;
    private static final int TYPE_NEWS_POST = 2;

    private int likes;
    private int noOfComments;
    //private boolean isLiked;

    private List<PostsPOJO> newsFeedItemPOJOs = Collections.emptyList();

    private DatabaseReference databaseReference;

    public CityNewsFeedRecyclerAdapter(List<PostsPOJO> newsFeedItems) {
        //this.context = context;
        this.newsFeedItemPOJOs = newsFeedItems;
        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        //firebaseUser = firebaseAuth.getCurrentUser();

        //inflater = LayoutInflater.from(context);


    }

    @Override
    public int getItemCount() {
        try {
            return (newsFeedItemPOJOs.size() + 1); //Plus 1 for the Share news layout at the top
        } catch (Exception ex) {

        }
        return 0;
    }

    @Override
    public int getItemViewType(int position) {

        if (position == 0) {
            return TYPE_SHARE_NEWS;
        } else {
            return TYPE_NEWS_POST;
        }
    }


    @Override
    public MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        context = parent.getContext();
//        return new OnlyPostImageViewHolder(LayoutInflater.from(context).inflate(R.layout.news_feed_list_item, parent, false));

        switch (viewType) {
            case TYPE_NEWS_POST:
                return new OnlyPostImageViewHolder(LayoutInflater.from(context).inflate(R.layout.news_feed_list_item, parent, false));

            case TYPE_SHARE_NEWS:
                return new ShareNewsPostViewHolder(LayoutInflater.from(context).inflate(R.layout.share_news_item_layout, parent, false));

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
            OnlyPostImageViewHolder mholder = (OnlyPostImageViewHolder) holder;
            setUpPictureView(context, mholder, holder.getAdapterPosition() - 1);
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

    private void setUpPictureView(final Context context, final OnlyPostImageViewHolder mholder, final int position) {

        final PostsPOJO currentData = newsFeedItemPOJOs.get(position);

        if (currentData.getcontent_post().size() == 0) {
            mholder.viewPager.setVisibility(View.GONE);
            mholder.tabLayout.setVisibility(View.GONE);
        } else if (currentData.getcontent_post().size() > 1) {
            postContentViewPagerAdapter = new PostContentViewPagerAdapter(context, currentData.getcontent_post());
            mholder.viewPager.setAdapter(postContentViewPagerAdapter);
            mholder.viewPager.setVisibility(View.VISIBLE);
            mholder.tabLayout.setVisibility(View.VISIBLE);
            mholder.tabLayout.setupWithViewPager(mholder.viewPager, true);
        } else if (currentData.getcontent_post().size() == 1) {
            postContentViewPagerAdapter = new PostContentViewPagerAdapter(context, currentData.getcontent_post());
            mholder.viewPager.setAdapter(postContentViewPagerAdapter);
            mholder.tabLayout.setVisibility(View.GONE);
            mholder.viewPager.setVisibility(View.VISIBLE);
        }

//        final DatabaseReference userDetails = databaseReference.child("Users/"+currentData.getUserID());
//        userDetails.addListenerForSingleValueEvent(new ValueEventListener() {
//
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot1) {
//
//                String username = dataSnapshot1.child("Username").getValue(String.class);
//                String profilePicturePath = dataSnapshot1.child("ProfilePicture").getValue(String.class);

        //mholder.txtName.setBackground(null);
        //mholder.txtName.getLayoutParams().width = ViewGroup.LayoutParams.WRAP_CONTENT;
        //mholder.txtName.setLayoutParams(new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT));
        mholder.txtName.setText(currentData.getusername());

        if (currentData.getprofilepicture() == null) {

        } else {
            StorageReference filePath = storageReference.child("profilepictures").child(currentData.getprofilepicture());
            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    try {
                        GlideApp.with(context)
                                .load(uri)
                                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                                .circleCrop()
                                .transition(DrawableTransitionOptions.withCrossFade(1000))
                                .error(R.color.link)
                                .into(mholder.imgProfilePic);
                    } catch (Exception ex) {
                        Log.d("error", ex.toString());
                    }
                }
            });
        }

        likes = currentData.getLikes() == null ? 0 : currentData.getLikes().size();
        noOfComments = 12;
        if (currentData.getLikes().containsKey(firebaseUser.getUid())) {
            mholder.btnHelpful.setImageResource(R.mipmap.ic_like_active);
        }
        //int comments = currentData.getcomments().size() == 0 ? 0 : currentData.getLikes().size();

        mholder.txtLikes.setText(likes + " " + context.getResources().getString(R.string.likes_dot));
        mholder.txtComments.setText(noOfComments + " Comments");
        //Toast.makeText(getApplicationContext(),username.toString(),Toast.LENGTH_LONG).show();


        //list.add(new PostsPOJO(postsPOJO.getUserID(),profilePicturePath ,username, postsPOJO.getTimestamp(),postsPOJO.getPostText(),postsPOJO.getLocation(),postsPOJO.getcontent_post()));
        //Log.d("datalist2", postsPOJO.getUserID()+","+profilePicturePath +","+username+","+postsPOJO.getTimestamp()+","+postsPOJO.getPostText()+","+postsPOJO.getLocation()+","+postsPOJO.getcontent_post());


//                            userDetails.removeEventListener(mListener);
//            }
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//            }
//        });


        //mholder.viewPager.setPageTransformer(true, new Zoom);
        //viewPager.setAdapter(postContentViewPagerAdapter);

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
//                MainActivity mainActivity = (MainActivity) context;
//                mainActivity.onCommentButtonSelected(likes, noOfComments, context.getString(R.string.main_activity), currentData.getPostid());
//
//                mainActivity.hideLayout();
                //mainActivity.replaceMainFragmentWithComments();
//                Intent intent = new Intent(context, CommentsActivity.class);
//                context.startActivity(intent);
            }
        });

        mholder.btnHelpful.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "liked", Toast.LENGTH_SHORT).show();

                databaseReference.child("posts/" + currentData.getPostid()).runTransaction(new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData mutableData) {
                        PostsPOJO postsPOJO = mutableData.getValue(PostsPOJO.class);

                        if (postsPOJO == null) {
                            return Transaction.success(mutableData);
                        }
                        Log.d("dadalike", postsPOJO.toString());
                        Log.d("dadalike", mutableData.toString());
                        if (postsPOJO.getLikes() != null) {

                            if (postsPOJO.getLikes().containsKey(firebaseUser.getUid())) {
                                // Unstar the post and remove self from stars
                                postsPOJO.likesCount = postsPOJO.likesCount - 1;
                                postsPOJO.getLikes().remove(firebaseUser.getUid());
                                //isLiked = false;
                                //mholder.btnHelpful.setImageResource(R.mipmap.ic_like);
                                likes = likes + 1;
                            } else {
                                // Star the post and add self to stars
                                postsPOJO.likesCount = postsPOJO.likesCount + 1;
                                postsPOJO.getLikes().put(firebaseUser.getUid(), true);
                                //isLiked = true;
                                //mholder.btnHelpful.setImageResource(R.mipmap.ic_like_active);
                            }
                        } else {

                            postsPOJO.getLikes().put(firebaseUser.getUid(), true);
                            postsPOJO.likesCount = postsPOJO.likesCount + 1;
                            //isLiked = true;
                        }
                        // Set value and report transaction success
                        mutableData.setValue(postsPOJO);
                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(DatabaseError databaseError, boolean commited,
                                           DataSnapshot dataSnapshot) {

                        PostsPOJO postsPOJO = dataSnapshot.getValue(PostsPOJO.class);
                        if (commited) {
                            //transaction successfully completed
                            if (postsPOJO.getLikes().containsKey(firebaseUser.getUid())) {
                                mholder.btnHelpful.setImageResource(R.mipmap.ic_like_active);
                            } else {
                                mholder.btnHelpful.setImageResource(R.mipmap.ic_like);
                            }
                        } else {
                            //aborted or an error occurred
                            mholder.btnHelpful.setImageResource(R.mipmap.ic_like);
                        }

                        // Transaction completed
                        Log.d("transaction completed", "postTransaction:onComplete:" + databaseError);
                    }
                });

//                if(isLiked) {
//                    mholder.btnHelpful.setImageResource(R.mipmap.ic_like_active);
//                }else{
//                    mholder.btnHelpful.setImageResource(R.mipmap.ic_like);
//                }
                view.startAnimation(AnimationUtils.loadAnimation(context, R.anim.image_resize));

            }
        });

        // Converting timestamp into X ago format
        CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(
                Long.parseLong(String.valueOf(currentData.gettimestamp())),
                System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);

        mholder.txtTimeStamp.setText(timeAgo);
        mholder.txtLocation.setText(currentData.getlocation());

        mholder.txtSecondary.setText(currentData.getsecondarylocation());

        mholder.txtStatusMsg.setText(currentData.getposttext());


        mholder.imgProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ProfileActivity.class);
                context.startActivity(intent);
            }
        });


        //mholder.imgPost.setImageDrawable(currentData.getImage());

//        mholder.txtUrl.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(context,"URL:"+position,Toast.LENGTH_SHORT).show();
//            }
//        });


//        mholder.imgPost.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                Intent intent = new Intent(context, PostImageDisplayActivity.class);
//                intent.putExtra("imageIndex", position);
//                context.startActivity(intent);
//
//            }
//        });

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


    public class OnlyPostImageViewHolder extends MainViewHolder {

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

        public OnlyPostImageViewHolder(View itemView) {

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
