//package com.example.sheryarkhan.projectcity.activities;
//
//import android.app.Activity;
//import android.content.Context;
//import android.content.Intent;
//import android.content.SharedPrefs;
//import android.graphics.Bitmap;
//import android.os.Vibrator;
//import android.support.annotation.NonNull;
//import android.support.design.widget.TabLayout;
//import android.support.v4.content.ContextCompat;
//import android.support.v4.view.ViewPager;
//import android.support.v4.widget.SwipeRefreshLayout;
//import android.support.v7.app.AppCompatActivity;
//import android.os.Bundle;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.util.Log;
//import android.view.View;
//import android.widget.ProgressBar;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.android.volley.Request;
//import com.bumptech.glide.Glide;
//import com.bumptech.glide.request.RequestOptions;
//import com.example.sheryarkhan.projectcity.Glide.GlideApp;
//import com.example.sheryarkhan.projectcity.R;
//import com.example.sheryarkhan.projectcity.utils.BottomNavigationViewHelper;
//import com.example.sheryarkhan.projectcity.utils.Constants;
//import com.example.sheryarkhan.projectcity.utils.IVolleyResult;
//import com.example.sheryarkhan.projectcity.utils.ImageCompression;
//import com.example.sheryarkhan.projectcity.utils.VolleyService;
//import com.example.sheryarkhan.projectcity.adapters.NewsFeedRecyclerAdapter;
//import com.example.sheryarkhan.projectcity.adapters.SectionsPagerAdapter;
//import com.google.android.gms.tasks.OnFailureListener;
//import com.google.android.gms.tasks.OnSuccessListener;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.database.ChildEventListener;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.Query;
//import com.google.firebase.database.ValueEventListener;
//import com.google.firebase.storage.FirebaseStorage;
//
//import java.io.ByteArrayOutputStream;
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.UUID;
//
//import com.google.firebase.storage.OnProgressListener;
//import com.google.firebase.storage.StorageReference;
//import com.google.firebase.storage.UploadTask;
//import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
//
//import org.json.JSONObject;
//
//import data.NewsFeedItemPOJO;
//import data.PostsPOJO;
//
//public class NewsFeedActivity extends AppCompatActivity {
//
//    private static final int REQUEST_CODE_FOR_POST_NEWS = 555;
//    private ViewPager mViewPager;
//    int counter = 1;
//    private IVolleyResult mResultCallback;
//    private VolleyService mVolleyService;
//    private Map<Integer, ArrayList<String>> hashMap = Collections.emptyMap();
//    boolean isFirstTime = true;
//    private DatabaseReference databaseReference;
//    private FirebaseAuth firebaseAuth;
//    private boolean isLastItem = false;
//    private Map<String, Boolean> media = new HashMap<>();
//    private static Integer number_of_posts = 0;
//
//    /*private NewsFeedRecyclerAdapter newsFeedRecyclerAdapter;*/
//    /*private SwipeRefreshLayout swipeRefreshLayout;
//    private RecyclerView newsFeedRecyclerView;
//    private NewsFeedRecyclerAdapter newsFeedRecyclerAdapter;
//    private DatabaseReference databaseReference;
//    private FirebaseAuth firebaseAuth;
//    private ChildEventListener mChildListener;
//    private ProgressBar recyclerViewProgressBar;
//    private TextView txtNetworkError;
//
//    //POST UPLOAD RELATED
//    private Map<Integer, ArrayList<String>> hashMap = Collections.emptyMap();
//    private boolean isLastItem = false;
//    private static Integer number_of_posts = 0;
//    private Map<String, Boolean> media = new HashMap<>();
//    private IVolleyResult mResultCallback;
//    private VolleyService mVolleyService;
//
//    private LinearLayoutManager linearLayoutManager;
//
//
//    private ArrayList<PostsPOJO> list;
//    private String oldestPostId;
//
//    boolean isFirstTime = true;
//    int counter = 1;
//    Query query;
//*/    //Query query1;
////    ArrayList<NewsFeedItemPOJO> list3;
////    private ViewPager viewPager;
////    private PostContentViewPagerAdapter viewPagerAdapter;
//
//
////    private String[] images = {"btnbackground","camera","q1bg"};
//
////    private String[] locations = {"Clifton Block 2","Clifton Block 6","Shahrah e Faisal","Clifton Block 5",
////    "Clifton Block 3","Clifton Block 9","Clifton Block 4","Clifton Block 7"
////    };
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_news_feed);
//        Log.d("onCreate", "onCreate " + counter++);
//        mViewPager = (ViewPager) findViewById(R.id.viewpager_container);
//        databaseReference = FirebaseDatabase.getInstance().getReference();
//        firebaseAuth = FirebaseAuth.getInstance();
//        //setupFirebaseAuth();
//
//        //setupBottomNavigationView();
//        setupViewPager();
///*
//
//        final List<Map<String, Object>> TOWN = Constants.TOWN_DETAILS();
//        Log.d("dadaTowns", TOWN.toString());
//
//
//        recyclerViewProgressBar = (ProgressBar) findViewById(R.id.recyclerViewProgressBar);
//        txtNetworkError = (TextView) findViewById(R.id.txtNetworkError);
//
//        databaseReference = FirebaseDatabase.getInstance().getReference();
//        firebaseAuth = FirebaseAuth.getInstance();
//        final Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
//
////
////        Query user = databaseReference.orderByChild("")
////        for(int i=0;i<locations.length;i++)
////        {
////            databaseReference.child("Post").child(String.valueOf(i+3)).child("Location").setValue(locations[i]);
////            databaseReference.child("Post").child(String.valueOf(i+3)).child("PostText").setValue("Lorem ipsum dolor sit amet");
////            databaseReference.child("Post").child(String.valueOf(i+3)).child("Timestamp").setValue(1502179255);
////            databaseReference.child("Post").child(String.valueOf(i+3)).child("UserID").setValue(3);
////        }
//
//        list = new ArrayList<>();
//
//        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
//        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//
//
//                if (vibrator.hasVibrator()) {
//                    vibrator.vibrate(30);
//                }
//                //newsFeedRecyclerAdapter.notifyDataSetChanged();
//                recyclerViewProgressBar.setVisibility(View.GONE);
//                txtNetworkError.setVisibility(View.GONE);
//                newsFeedRecyclerView.setVisibility(View.VISIBLE);
//                Toast.makeText(getBaseContext(), "Refreshed", Toast.LENGTH_SHORT).show();
//                swipeRefreshLayout.setRefreshing(false);
//            }
//        });
//
//        newsFeedRecyclerView = (RecyclerView) findViewById(R.id.news_feed_recyclerview);
//        //feedItemsList = new ArrayList<>();
//
//        //newsFeedRecyclerAdapter = new NewsFeedRecyclerAdapter(this,generateDummyData(list));
//        //newsFeedRecyclerView.setAdapter(newsFeedRecyclerAdapter);
//
//
//        newsFeedRecyclerView.setHasFixedSize(true);
//        newsFeedRecyclerView.setItemViewCacheSize(20);
//        newsFeedRecyclerView.setDrawingCacheEnabled(true);
//        newsFeedRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
//        linearLayoutManager = new LinearLayoutManager(this);
//        newsFeedRecyclerView.setLayoutManager(linearLayoutManager);
//        newsFeedRecyclerAdapter = new NewsFeedRecyclerAdapter(list);
//        newsFeedRecyclerView.setAdapter(newsFeedRecyclerAdapter);
//        newsFeedRecyclerAdapter.notifyItemChanged(0);
//
//
//        query = databaseReference.child("posts").orderByChild("timestamp");
//        //query1 = databaseReference.child("posts").orderByChild("timestamp").limitToLast(1);
//
//
////        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
////        connectedRef.addListenerForSingleValueEvent(new ValueEventListener() {
////            @Override
////            public void onDataChange(DataSnapshot snapshot) {
////                boolean connected = snapshot.getValue(Boolean.class);
////                if (connected) {
////                    System.out.println("connected");
////
////                } else {
////                    System.out.println("not connected");
////                    Toast.makeText(NewsFeedActivity.this,"No Internet Connection!",Toast.LENGTH_SHORT).show();
////                    recyclerViewProgressBar.setVisibility(View.GONE);
////                    txtNetworkError.setVisibility(View.VISIBLE);
////                }
////            }
////
////            @Override
////            public void onCancelled(DatabaseError error) {
////                System.err.println("Listener was cancelled");
////            }
////        });
//
//
//        query.limitToLast(15).addListenerForSingleValueEvent(new ValueEventListener() {
//
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//
//
//                for (DataSnapshot item : dataSnapshot.getChildren()) {
//                    final PostsPOJO postsPOJO = item.getValue(PostsPOJO.class);
//                    //images = postsPOJO.getcontent_post();
//                    Log.d("dadauserid",postsPOJO.getuserid());
//                    Query userDetails = databaseReference.child("Users").child(postsPOJO.getuserid()).orderByChild(postsPOJO.getuserid()).limitToLast(1);
//
//                    userDetails.addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(DataSnapshot dataSnapshot) {
//                            Log.d("dadauserid2",dataSnapshot.child("username").toString());
//                        }
//
//                        @Override
//                        public void onCancelled(DatabaseError databaseError) {
//
//                        }
//                    });
//
//                    oldestPostId = item.getKey();
//                    list.add(new PostsPOJO(postsPOJO.getuserid(),postsPOJO.getPostid(),
//                            postsPOJO.getprofilepicture(), postsPOJO.getusername(),
//                            postsPOJO.gettimestamp(), postsPOJO.getposttext(),
//                            postsPOJO.getlocation(), postsPOJO.getsecondarylocation()
//                            , postsPOJO.getcontent_post(), postsPOJO.getLikes()));
//                    Log.d("datalist", list.toString());
//
//                }
//                Collections.reverse(list);
//                recyclerViewProgressBar.setVisibility(View.GONE);
//                newsFeedRecyclerView.setVisibility(View.VISIBLE);
//                //newsFeedRecyclerAdapter.notifyItemInserted(1);
//                //newsFeedRecyclerAdapter.notifyItemRangeChanged(1, list.size());
//                newsFeedRecyclerAdapter.notifyDataSetChanged();
//                isFirstTime = false;//asdasd
//
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//            }
//        });
//
//        final boolean mIsLoading = false;
//        newsFeedRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            private int currentVisibleItemCount;
//            private int currentScrollState;
//            private int currentFirstVisibleItem;
//            private int totalItem;
//
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                this.currentScrollState = newState;
//                //this.isScrollCompleted();
//            }
//
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                if (mIsLoading)
//                    return;
//
//                int visibleItemCount = linearLayoutManager.getChildCount();
//                int totalItemCount = linearLayoutManager.getItemCount();
//                int pastVisibleItems = linearLayoutManager.findFirstVisibleItemPosition();
//                if (pastVisibleItems + visibleItemCount >= totalItemCount) {
//                    //End of list
//                    //Toast.makeText(getApplicationContext(),"end of list",Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//
//
//        mChildListener = new ChildEventListener() {
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                //if (!isFirstTime) {
//                    final PostsPOJO postsPOJO = dataSnapshot.getValue(PostsPOJO.class);
//                    //images = postsPOJO.getcontent_post();
//
//                    list.add(0, new PostsPOJO(postsPOJO.getuserid(),postsPOJO.getPostid(),
//                            postsPOJO.getprofilepicture(), postsPOJO.getusername(),
//                            postsPOJO.gettimestamp(), postsPOJO.getposttext(),
//                            postsPOJO.getlocation(), postsPOJO.getsecondarylocation()
//                            , postsPOJO.getcontent_post(), postsPOJO.getLikes()));
//
//                    newsFeedRecyclerAdapter.notifyItemInserted(1);
//                    newsFeedRecyclerAdapter.notifyItemRangeChanged(1, list.size());
//                //}
//
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onChildRemoved(DataSnapshot dataSnapshot) {
//
//
//            }
//
//            @Override
//            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        };
//*/
//
//    }
//
//    /**
//     * Responsible for adding the 3 tabs: Unknown, News Feed, Chat Messages
//     */
//    private void setupViewPager(){
//        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
//        //adapter.addFragment(new ChatMessagingFragment()); //index 0
//        adapter.addFragment(new NewsFeedFragment()); //index 1
//        adapter.addFragment(new ChatMessagingFragment()); //index 2
//        mViewPager.setAdapter(adapter);
//
//        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
//        tabLayout.setupWithViewPager(mViewPager);
//
//        //tabLayout.getTabAt(0).setIcon(R.drawable.ic_home);
//        tabLayout.getTabAt(0).setIcon(R.mipmap.projectcityicon);
//        tabLayout.getTabAt(1).setIcon(R.drawable.ic_dashboard_black_24dp);
//    }
//
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent resultData) {
//        if (requestCode == REQUEST_CODE_FOR_POST_NEWS) {
//            if (resultData != null && resultCode == RESULT_OK) {
//                String txtPrimary = resultData.getStringExtra("txtPrimary");
//                String txtSecondary = resultData.getStringExtra("txtSecondary");
//                String editTextShareNews = resultData.getStringExtra("editTextShareNews");
//                hashMap = (HashMap<Integer, ArrayList<String>>) resultData.getSerializableExtra("hashMap");
//                isFirstTime = false;
//                //query.limitToLast(1).addChildEventListener(mChildListener);
//
//
//                uploadPostDataToFirebase(txtPrimary,txtSecondary, editTextShareNews);
//            } else if (resultCode == RESULT_CANCELED) {
//
//            }
//        }
//
//    }
//
//
//    private void uploadPostDataToFirebase(final String txtPrimary, final String txtSecondary, final String editTextShareNews)
//    {
//        SharedPrefs sharedPref = NewsFeedActivity.this.getSharedPreferences("UserData", Context.MODE_PRIVATE);
//        final String username = sharedPref.getString("username", "");
//        //final String userid = sharedPref.getString("userid", "");
//
//        final String userid = firebaseAuth.getCurrentUser().getUid();
//        String imageURI = sharedPref.getString("profilepicture", "");
//
//        final String key = databaseReference.child("posts").push().getKey();
//
//        FirebaseStorage storage = FirebaseStorage.getInstance();
//
//        StorageReference storageRef = storage.getReference();
//        final DatabaseReference queryForUserNumberOfPosts = databaseReference.child("Users");
//        // Uri file = Uri.fromFile(new File(mImageUri.toString()));
//
//        //final Iterator iterator = hashMap.values().iterator();
//        //Map<Integer, ArrayList<Object>> hMap = Collections.emptyMap();
//        //int i = 0;
//        final List<Media> medias = new ArrayList<>();
//        if(hashMap != null) {
//            for (ArrayList<String> item : hashMap.values()) {
//
//
//                //String type = getMimeType(Uri.fromFile(new File(item)));
//                if (Integer.parseInt(item.get(0)) == 1) // 1 FOR IMAGE
//                {
//                    Bitmap bmp = ImageCompression.getImageFromResult(NewsFeedActivity.this, item.get(1));//your compressed bitmap here
//                    bmp = ImageCompression.rotate(bmp, Integer.parseInt(item.get(2)));
//                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                    bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//                    byte[] imageByteData = baos.toByteArray();
//
//                    try {
//                        baos.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    bmp.recycle();
//                    try {
//                        Media media = new Media(1, imageByteData, null);
//                        medias.add(media);
//                    } catch (Exception ex) {
//                        Log.d("mediaError", ex.toString());
//                    }
//                } else if (Integer.parseInt(item.get(0)) == 2) // 2 FOR VIDEO
//                {
//                    try {
//                        byte[] videoByte = convertPathToBytes(String.valueOf(item.get(1)));
//                        Media media = new Media(2, null, videoByte);
//                        medias.add(media);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    Log.d("VIDEOPOST", "VIDEOPOST");
//                }
//            }
//        }
//
//        if (medias.size() == 0) {
//            Long timeStamp = System.currentTimeMillis();
//            PostsPOJO postsPOJO = new PostsPOJO(userid,key, "profilepic:" + userid, username, timeStamp, editTextShareNews,
//                    txtPrimary, txtSecondary,Collections.<String, Boolean>emptyMap());
//
//
//            Map<String, Object> childUpdates = new HashMap<>();
//            childUpdates.put("/posts/" + key, postsPOJO);
//            databaseReference.updateChildren(childUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
//                @Override
//                public void onSuccess(Void aVoid) {
//
////                    databaseReference.child("posts").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
////                        @Override
////                        public void onDataChange(DataSnapshot dataSnapshot) {
////                            final PostsPOJO postsPOJO = dataSnapshot.getValue(PostsPOJO.class);
////                            //images = postsPOJO.getcontent_post();
////
////                            list.add(0, new PostsPOJO(postsPOJO.getuserid(),postsPOJO.getPostid(),
////                                    postsPOJO.getprofilepicture(), postsPOJO.getusername(),
////                                    postsPOJO.gettimestamp(), postsPOJO.getposttext(),
////                                    postsPOJO.getlocation(), postsPOJO.getsecondarylocation()
////                                    , postsPOJO.getcontent_post(), postsPOJO.getLikes()));
////
////                            newsFeedRecyclerAdapter.notifyItemInserted(1);
////                            newsFeedRecyclerAdapter.notifyItemRangeChanged(1, list.size());
////                        }
////
////                        @Override
////                        public void onCancelled(DatabaseError databaseError) {
////
////                        }
////                    });
//
//                    //SEND NOTIFICATION TO USERS
//                    sendNotificationToUsers(key,txtPrimary,txtSecondary,editTextShareNews);
//
//                    //int number_of_posts=0;
//                    queryForUserNumberOfPosts.child(userid).child("number_of_posts").addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(DataSnapshot dataSnapshot) {
//                            number_of_posts = dataSnapshot.getValue(Integer.class);
//                            queryForUserNumberOfPosts.child(userid).child("number_of_posts").setValue(number_of_posts + 1);
//                        }
//
//                        @Override
//                        public void onCancelled(DatabaseError databaseError) {
//
//                        }
//                    });
//
//                }
//            });
//
//        } else {
//
//            final int last_item = (medias.size() - 1);
//
//            for (int i = 0; i < medias.size(); i++) {
//                if (medias.get(i).getMediaType() == 1) // IMAGE
//                {
//                    if (i == last_item) {  //IF Last Image/Video
//                        isLastItem = true;
//                    }
//                    String uniqueId = UUID.randomUUID().toString();
//                    media.put("image:" + uniqueId, true);
//                    StorageReference mediaRef = storageRef.child("images/image:" + uniqueId);
//
//                    mediaRef.putBytes(medias.get(i).getImageBytes()).addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            Log.d("storageerror", e.toString());
//                        }
//                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                        @Override
//                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                            Toast.makeText(getBaseContext(), "uploaded", Toast.LENGTH_SHORT).show();
//
//
//                            if (isLastItem) {
//
//                                Long timeStamp = System.currentTimeMillis();
//                                PostsPOJO postsPOJO = new PostsPOJO(userid,key, "profilepic:" + userid, username, timeStamp, editTextShareNews,
//                                        txtPrimary, txtSecondary,
//                                        media,Collections.<String, Boolean>emptyMap());
//
//                                Map<String, Object> childUpdates = new HashMap<>();
//                                childUpdates.put("/posts/" + key, postsPOJO);
////                                mDatabase.child("posts").child(key).child("likes").setValue(0);
//                                databaseReference.updateChildren(childUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
//                                    @Override
//                                    public void onSuccess(Void aVoid) {
//
//                                        //SEND NOTIFICATION TO USERS
//                                        sendNotificationToUsers(key,txtPrimary,txtSecondary,editTextShareNews);
//
//                                        queryForUserNumberOfPosts.child(userid).child("number_of_posts").addListenerForSingleValueEvent(new ValueEventListener() {
//                                            @Override
//                                            public void onDataChange(DataSnapshot dataSnapshot) {
//                                                number_of_posts = dataSnapshot.getValue(Integer.class);
//                                                queryForUserNumberOfPosts.child(userid).child("number_of_posts").setValue(number_of_posts + 1);
//                                            }
//
//                                            @Override
//                                            public void onCancelled(DatabaseError databaseError) {
//
//                                            }
//                                        });
//
//                                    }
//                                });
//
//                            }
//                        }
//                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
//                        @Override
//                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
//                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
//                            Toast.makeText(NewsFeedActivity.this, progress + "% done", Toast.LENGTH_SHORT).show();
//                        }
//                    }).addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            Log.d("uploadError", e.toString());
//                        }
//                    });
//                } else if (medias.get(i).getMediaType() == 2) { //VIDEO
//
//                    if (i == last_item) {  //IF Last Image/Video
//                        isLastItem = true;
//                    }
//
//                    String uniqueId = UUID.randomUUID().toString();
//                    media.put("video:" + uniqueId, true);
//                    StorageReference mediaRef = storageRef.child("images/video:" + uniqueId);
//
//                    mediaRef.putBytes(medias.get(i).getVideoBytes()).addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            Log.d("storageerror", e.toString());
//                        }
//                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                        @Override
//                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                            Toast.makeText(getBaseContext(), "uploaded", Toast.LENGTH_SHORT).show();
//                            if (isLastItem) {
//
//                                Long timeStamp = System.currentTimeMillis();
//
//                                PostsPOJO postsPOJO = new PostsPOJO(userid,key, "profilepic:" + userid, username, timeStamp, editTextShareNews,
//                                        txtPrimary, txtSecondary,
//                                        media,Collections.<String, Boolean>emptyMap());
//
//                                Map<String, Object> childUpdates = new HashMap<>();
//                                childUpdates.put("/posts/" + key, postsPOJO);
//                                //mDatabase.child("posts").child(key).child("likes").setValue(0);
//                                databaseReference.updateChildren(childUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
//                                    @Override
//                                    public void onSuccess(Void aVoid) {
//
//                                        //SEND NOTIFICATION TO USERS
//                                        sendNotificationToUsers(key,txtPrimary,txtSecondary,editTextShareNews);
//
//                                        //int number_of_posts=0;
//                                        queryForUserNumberOfPosts.child(userid).child("number_of_posts").addListenerForSingleValueEvent(new ValueEventListener() {
//                                            @Override
//                                            public void onDataChange(DataSnapshot dataSnapshot) {
//                                                number_of_posts = dataSnapshot.getValue(Integer.class);
//                                                queryForUserNumberOfPosts.child(userid).child("number_of_posts").setValue(number_of_posts + 1);
//                                            }
//
//                                            @Override
//                                            public void onCancelled(DatabaseError databaseError) {
//
//                                            }
//                                        });
//
//                                    }
//                                });
//
//                            }
//                        }
//                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
//                        @Override
//                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
//                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
//                            Toast.makeText(NewsFeedActivity.this, progress + "% done", Toast.LENGTH_SHORT).show();
//                        }
//                    }).addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            Log.d("uploadError", e.toString());
//                        }
//                    });
//
//                }
//            }
//
//        }
//    }
//
//    private byte[] convertPathToBytes(String path) throws IOException {
//
//        FileInputStream fis = new FileInputStream(path);
//        ByteArrayOutputStream bos = new ByteArrayOutputStream();
//        byte[] b = new byte[1024];
//
//        for (int readNum; (readNum = fis.read(b)) != -1; ) {
//            bos.write(b, 0, readNum);
//        }
//
//        byte[] bytes = bos.toByteArray();
//
//        bos.close();
//
//        return bytes;
//    }
//
//    private void sendNotificationToUsers(String key, String txtPrimary , String txtSecondary , String editTextShareNews)
//    {
//        Map<String, String> townAndMessage = new HashMap<>();
//        townAndMessage.put("town", txtSecondary.replace(" ", "_"));
//        townAndMessage.put("message", editTextShareNews);
//        townAndMessage.put("postId", key);
//        mVolleyService = new VolleyService(mResultCallback, NewsFeedActivity.this);
//        mVolleyService.postDataVolley(Request.Method.POST,
//                Constants.protocol + Constants.IP + "/sendNotification",
//                new JSONObject(townAndMessage));
//    }
//
//    private static class Media {
//        private byte[] imageBytes = null;
//        private byte[] videoBytes = null;
//        private Integer mediaType;
//
//        private Media(Integer mediaType, byte[] imageBytes, byte[] videoBytes) {
//            this.mediaType = mediaType;
//            this.imageBytes = imageBytes;
//            this.videoBytes = videoBytes;
//
//        }
//
//        public Integer getMediaType() {
//            return mediaType;
//        }
//
//        public byte[] getImageBytes() {
//            return imageBytes;
//        }
//
//        public byte[] getVideoBytes() {
//            return videoBytes;
//        }
//    }
//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//
//        //newsFeedRecyclerView.smoothScrollToPosition(0);
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
////        if (linearLayoutManager.findFirstCompletelyVisibleItemPosition() == 0) {
////            //this is the top of the RecyclerView
////
////
////        }
//
//        Log.d("onPause", "onPause " + counter++);
//
//
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        Log.d("onResume", "onResume " + counter++);
//        //query.limitToLast(1).addChildEventListener(mChildListener);
//    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//        Log.d("onStart", "onStart " + counter++);
//        //query.limitToLast(1).addChildEventListener(mChildListener);
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        Log.d("onStop", "onStop " + counter++);
//        //query.limitToLast(1).removeEventListener(mChildListener);
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        Log.d("onDestroy", "onDestroy " + counter++);
//    }
//
////    private void setupBottomNavigationView() {
////        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx) findViewById(R.id.my_toolbar_bottom);
////        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx,NewsFeedActivity.this);
////    }
//
//        //query.limitToLast(1).addChildEventListener(mChildListener);
//
//
////query.addChildEventListener(new ChildEventListener() {
////    @Override
////    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
////
////        final PostsPOJO postsPOJO = dataSnapshot.getValue(PostsPOJO.class);
////        //images = postsPOJO.getcontent_post();
////
////        list.add(new PostsPOJO(postsPOJO.getuserid(),
////                postsPOJO.getprofilepicture() ,postsPOJO.getusername(),
////                postsPOJO.gettimestamp(),postsPOJO.getposttext(),
////                postsPOJO.getlocation(),postsPOJO.getsecondarylocation()
////                ,postsPOJO.getcontent_post(),postsPOJO.getLikes()));
////
////        newsFeedRecyclerAdapter.notifyItemInserted(0);
////
////    }
////
////    @Override
////    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
////
////    }
////
////    @Override
////    public void onChildRemoved(DataSnapshot dataSnapshot) {
////
//////        final PostsPOJO postsPOJO = dataSnapshot.getValue(PostsPOJO.class);
//////        list.remove(postsPOJO);
//////        newsFeedRecyclerAdapter.notifyItemRemoved();
////
////    }
////
////    @Override
////    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
////
////    }
////
////    @Override
////    public void onCancelled(DatabaseError databaseError) {
////
////    }
////});
//
//
////                query.addChildEventListener(new ChildEventListener() {
////                    @Override
////                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
////
////
////                        final PostsPOJO postsPOJO = dataSnapshot.getValue(PostsPOJO.class);
////
////                        list.add(postsPOJO);
////
//////                Query userDetails = databaseReference.child("Users/"+postsPOJO.getUserID());
//////                String u = String.valueOf(userDetails);
//////                userDetails.addValueEventListener(new ValueEventListener() {
//////                    @Override
//////                    public void onDataChange(DataSnapshot dataSnapshot) {
//////
//////                        String username = dataSnapshot.child("Username").getValue(String.class);
//////
//////                        list.add(new PostsPOJO(postsPOJO.getUserID(), username, postsPOJO.getTimestamp(),postsPOJO.getPostText(),postsPOJO.getLocation()));
//////
//////                    }
//////
//////                    @Override
//////                    public void onCancelled(DatabaseError databaseError) {
//////
//////                    }
//////                });
////
////                        //PostsPOJO newsFeedItemPOJO = dataSnapshot.getValue(PostsPOJO.class);
////                        //list.add(newsFeedItemPOJO);
////
////                        Log.d("dadalist", list.toString());
////
////                        newsFeedRecyclerAdapter.notifyDataSetChanged();
//////                newsFeedRecyclerAdapter = new NewsFeedRecyclerAdapter(NewsFeedActivity.this,generateDummyData(newsFeedItemPOJO));
//////                newsFeedRecyclerView.setAdapter(newsFeedRecyclerAdapter);
////                        //newsFeedRecyclerAdapter.notifyDataSetChanged();
////
////
////                    }
////
////                    @Override
////                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
////
////                    }
////
////                    @Override
////                    public void onChildRemoved(DataSnapshot dataSnapshot) {
////
////                    }
////
////                    @Override
////                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
////
////                    }
////
////                    @Override
////                    public void onCancelled(DatabaseError databaseError) {
////
////                    }
////                });
//
//
//        //newsFeedRecyclerAdapter.notifyDataSetChanged();
////        viewPager = (ViewPager)findViewById(R.id.viewPager);
////        viewPagerAdapter = new PostContentViewPagerAdapter(this,images);
////        viewPager.setAdapter(viewPagerAdapter);
//
//
//        //newsFeedListView = (ListView)findViewById(R.id.news_feed_listView);
//
//
////        getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#3b5998")));
////        getActionBar().setIcon(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
//
//
////        Bitmap profilePic = BitmapFactory.decodeResource(getResources(), R.drawable.loginbg);
////        Bitmap postPic = BitmapFactory.decodeResource(getResources(), R.drawable.discoverbtn);
//
////        for(int i=0; i < 12;i++)
////        {
////
////            NewsFeedItemPOJO item = new NewsFeedItemPOJO();
////            item.setName("Sheryar Khan");
////            item.setStatus("Hello how are you? I am fine. What about you?");
////            item.setId(i);
////            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
////                item.setImage(getDrawable(R.drawable.camera));
////            }
////            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
////                item.setProfilePic(getDrawable(R.drawable.confirm));
////            }
////            //item.setProfilePic(profilePic);
////            item.setTimeStamp("1501583198");
////            item.setUrl("www.google.com");
////
////            feedItemsList.add(item);
////
////
////        }
//
//        //newsFeedRecyclerAdapter.notifyDataSetChanged();
//
//
////        newsFeedListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
////            @Override
////            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
////
////                String gg = adapterView.getAdapter().getItem(position).toString();
////
////                Toast.makeText(getBaseContext(),gg,Toast.LENGTH_SHORT).show();
////
////            }
////        });
//
//
//    //    @Override
////    public void onConfigurationChanged(Configuration newConfig) {
////        super.onConfigurationChanged(newConfig);
////        VideosViewPager questionVideo = new VideosViewPager(NewsFeedActivity.this);
////        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
////            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
////            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
////
////            questionVideo.setDimensions(1920, 1080);
////            questionVideo.getHolder().setFixedSize(1920, 1080);
////
////        } else {
////            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
////
////            questionVideo.setDimensions(1920, 1080);
////            questionVideo.getHolder().setFixedSize(1920, 1080);
////
////        }
////    }
//
////    private ArrayList<NewsFeedItemPOJO> generateDummyData(ArrayList<PostsPOJO> list)
////    {
////        ArrayList<NewsFeedItemPOJO> list2 = new ArrayList<>();
////
////        for(int i=0; i < list.size();i++)
////        {
////
//////list.get(i).getUserID();
//////            Query query = databaseReference.child("UserID");
//////            query.addListenerForSingleValueEvent(new ValueEventListener() {
//////                @Override
//////                public void onDataChange(DataSnapshot dataSnapshot) {
//////
//////                }
//////
//////                @Override
//////                public void onCancelled(DatabaseError databaseError) {
//////
//////                }
//////            });
////            NewsFeedItemPOJO item = new NewsFeedItemPOJO();
////
////            item.setName(list.get(i).getLocation());
////            item.setStatus(list.get(i).getPostText());
////            item.setId(i);
////            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
////                item.setImage(getDrawable(R.drawable.q1bg));
////            }
////            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
////                item.setProfilePic(getDrawable(R.drawable.confirm));
////            }
////            //item.setProfilePic(profilePic);
////            item.setTimeStamp(list.get(i).getTimestamp().toString());
////            item.setUrl("www.google.com");
////
//////            if(i == 1 || i == 3 || i == 5 || i == 7 || i == 9) {
//////                item.setName("Sheryar Khan");
//////                item.setStatus("Hello how are you? I am fine. What about you?");
//////                item.setId(i);
//////                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//////                    item.setImage(getDrawable(R.drawable.q1bg));
//////                }
//////                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//////                    item.setProfilePic(getDrawable(R.drawable.confirm));
//////                }
//////                //item.setProfilePic(profilePic);
//////                item.setTimeStamp("1401723773793");
//////                item.setUrl("www.google.com");
//////            }
//////            else{
//////                item.setName("Dawood Khan");
//////                item.setStatus("Bad news!! Today I saw a very horrible accident near Bakery.");
//////                item.setId(i);
//////                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//////                    item.setImage(getDrawable(R.drawable.discoverbtn));
//////                }
//////                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//////                    item.setProfilePic(getDrawable(R.drawable.camera));
//////                }
//////                //item.setProfilePic(profilePic);
//////                item.setTimeStamp("1401723873793");
//////                item.setUrl("www.youtube.com");
//////            }
////
////
////            list2.add(item);
////
////
////        }
////
////        return list2;
////    }
//
//
//}
