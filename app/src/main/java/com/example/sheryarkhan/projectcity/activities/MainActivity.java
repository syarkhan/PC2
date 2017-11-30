package com.example.sheryarkhan.projectcity.activities;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.sheryarkhan.projectcity.R;
import com.example.sheryarkhan.projectcity.utils.Constants;
import com.example.sheryarkhan.projectcity.utils.FirebasePushNotificationMethods;
import com.example.sheryarkhan.projectcity.utils.IVolleyResult;
import com.example.sheryarkhan.projectcity.utils.ImageCompression;
import com.example.sheryarkhan.projectcity.utils.SharedPrefs;
import com.example.sheryarkhan.projectcity.utils.VolleyService;
import com.example.sheryarkhan.projectcity.adapters.SectionsPagerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.iceteck.silicompressorr.SiliCompressor;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Stack;
import java.util.UUID;

import data.NewPostProgress;
import data.PostsPOJO;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_FOR_POST_NEWS = 555;


    private FrameLayout comments_fragment_layout;
    private RelativeLayout main_activity_fragment_layout;
    private ViewPager mViewPager;
    private IVolleyResult mResultCallback;
    private VolleyService mVolleyService;
    private Map<Integer, ArrayList<String>> hashMap = Collections.emptyMap();
    boolean isFirstTime = true;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private boolean isLastItem = false;


    private static Integer number_of_posts = 0;

    private Stack<HashMap<String, Fragment>> mStack;
    private List<Fragment> fragmentList;
    public final String TAB_MAIN = "tab_main";

    public final String TAB_TOWN = "tab_town_news";
    public final String TAB_CITY = "tab_city_news";
    public final String TAB_NOTIFICATIONS = "tab_notifications";
    public final String TAB_SETTINGS = "tab_settings";

    private String mCurrentTab = TAB_TOWN;
    private int mCurrentTabNumber = 0;
    public int counter = -1;
    public boolean isTabClickedTwoTimes = false;
    SharedPrefs sharedPrefs;

    private MainFragment mainFragment;
    private String URL;
    //    final List<MainActivity.Media> mediaClassList = new ArrayList<>();
//    private List<String> media = new ArrayList<>();
    private StorageReference mediaRef = null;
    private FirebaseStorage storage;

    private StorageReference storageRef;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mStack = new Stack<>();
        context = MainActivity.this;
        sharedPrefs = new SharedPrefs(context);

        mainFragment = new MainFragment();

        fragmentList = new ArrayList<>();
        fragmentList.add(new NewsFeedFragment());
        fragmentList.add(new CityNewsFeedFragment());
        fragmentList.add(new NotificationsFragment());
        fragmentList.add(new SettingsFragment());

        comments_fragment_layout = (FrameLayout) findViewById(R.id.comments_fragment_layout);
        main_activity_fragment_layout = (RelativeLayout) findViewById(R.id.main_activity_fragment_layout);

        Log.d("onCreate", "onCreate " + counter++);
        mViewPager = (ViewPager) findViewById(R.id.viewpager_container);
        setupViewPager();
        storage = FirebaseStorage.getInstance();

        storageRef = storage.getReference();
    }

    public void addFragmentToStack(String tabId, int tabNumber) {
        mCurrentTab = tabId;
        mCurrentTabNumber = tabNumber;

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();

        if (mStack.size() == 4) {

//            if(mStack.search(fragmentList.get(mCurrentTabNumber)) == mCurrentTabNumber){
//                return;
//            }
            mStack.pop();
            HashMap<String, Fragment> fragmentHashMap2 = new HashMap<>();
            fragmentHashMap2.put(tabId, fragmentList.get(mCurrentTabNumber));
            mStack.push(fragmentHashMap2);
            ft.replace(R.id.main_container, fragmentList.get(mCurrentTabNumber), tabId).commit();
            return;
        } else {
            //if(mStack.peek() == fragmentList.get(0))
            HashMap<String, Fragment> fragmentHashMap = new HashMap<>();
            fragmentHashMap.put(tabId, fragmentList.get(mCurrentTabNumber));
            mStack.push(fragmentHashMap);
            ft.replace(R.id.main_container, fragmentList.get(mCurrentTabNumber), tabId).commit();

        }
        Log.d("stack2", mStack.toString());

//        if(tabId.equals(TAB_SETTINGS)){
//            //mStack.pop();
//
//        }

    }

    public void gotoFragment(Fragment selectedFragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_container, selectedFragment);
        fragmentTransaction.commit();
    }


    public void selectedTab(String tabId) {
        mCurrentTab = tabId;


    }


    public void onCommentButtonSelected(String userIdPost, int totalLikes, int totalComments, String callingActivity, String postId) {


        CommentsFragment fragment = new CommentsFragment();
        Bundle args = new Bundle();
        args.putInt(getString(R.string.likes), totalLikes);
        args.putInt(getString(R.string.comments), totalComments);
        args.putString(getString(R.string.main_activity), callingActivity);
        args.putString(getString(R.string.comment_post_id), postId);
        args.putString(getString(R.string.userIdPost), userIdPost);
        fragment.setArguments(args);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.comments_fragment_layout, fragment);

        transaction.addToBackStack(getString(R.string.view_comments_fragment));
        transaction.commit();

    }

    public void loadSettingNotificationFragment() {

        SettingNotificationFragment settingNotificationFragment = new SettingNotificationFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.comments_fragment_layout, settingNotificationFragment);
        transaction.commit();
        //  transaction.addToBackStack("");


    }

    public void pushFragments(String tag, Fragment fragment, boolean shouldAdd, boolean isMainFragment) {

    }

/*    public void showSettingNotificationLayout(){
        main_activity_fragment_layout.setVisibility(View.GONE);

    }*/

    public void hideLayout() {
        //Log.d(TAG, "hideLayout: hiding layout");
        main_activity_fragment_layout.setVisibility(View.GONE);
        comments_fragment_layout.setVisibility(View.VISIBLE);
    }


    public void showLayout() {
        //Log.d(TAG, "hideLayout: showing layout");
        main_activity_fragment_layout.setVisibility(View.VISIBLE);
        comments_fragment_layout.setVisibility(View.GONE);
    }

    public void changeBottomNavigationViewIcon() {

    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        if (comments_fragment_layout.getVisibility() == View.VISIBLE) {
            showLayout();
        } else {
            //HashMap<String, Fragment> StackPeek = mStack.peek();
            //hMap.values();
            if (mStack.size() > 1) {
                mStack.pop();
                FragmentManager manager = getSupportFragmentManager();
                FragmentTransaction ft = manager.beginTransaction();
                Fragment fragment = new ArrayList<>(mStack.peek().values()).get(0);
                mCurrentTab = new ArrayList<>(mStack.peek().keySet()).get(0);
                mCurrentTabNumber = mStack.indexOf(mStack.peek());
                Menu menu = mainFragment.bottomNavigationViewEx.getMenu();
                MenuItem menuItem = menu.getItem(mCurrentTabNumber);
                menuItem.setChecked(true);

                ft.replace(R.id.main_container, fragment, mCurrentTab).commit();
                //Fragment fragment = getSupportFragmentManager().getFragments().get(0);
                //fragment
            } else {
                finish();
            }
//            HashMap hMap = mStack.elementAt(mCurrentTabNumber-1);
//            FragmentManager manager = getSupportFragmentManager();
//            FragmentTransaction ft = manager.beginTransaction();
//            //int i =mStack.search("a");
//            if(hMap.size() != 0) {
//                ft.replace(R.id.main_container, new ArrayList<Fragment>(hMap.values()).get(0), mCurrentTab).commit();
//            }
//            else{
//                finish();
//            }
//            if (mStack.peek().get(mCurrentTab) == mStack.search(2)) {
//                finish();
//            }
//            if (mStack.get(mCurrentTabNumber).get(mCurrentTab)) {
//                //popFragments();
//
//                goToPreviousFragment();
//            } else {
//
//            }
        }
//        else{
//            if(getSupportFragmentManager().getBackStackEntryCount() > 0) {
//                getSupportFragmentManager().popBackStack();
//            }else{
//                super.onBackPressed();
//            }
//        }
//        if(mStacks.get(mCurrentTab).size() == 1){
//            // We are already showing first fragment of current tab, so when back pressed, we will finish this activity..
//            finish();
//            return;
//        }
//
//    /* Goto previous fragment in navigation stack of this tab */
//        popFragments();
    }

    /**
     * Responsible for adding the 3 tabs: Unknown, News Feed, Chat Messages
     */
    private void setupViewPager() {
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        //adapter.addFragment(new ChatMessagingFragment()); //index 0

        adapter.addFragment(mainFragment); //index 1
        adapter.addFragment(new ChatMessagingFragment()); //index 2
        mViewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        //tabLayout.getTabAt(0).setIcon(R.drawable.ic_home);
        tabLayout.getTabAt(0).setIcon(R.mipmap.projectcityicon);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_dashboard_black_24dp);

        //navFragmentsHashMap.put(TAB_TOWN_NEWS_FEED,)
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if (requestCode == REQUEST_CODE_FOR_POST_NEWS) {
            if (resultData != null && resultCode == RESULT_OK) {
                String txtPrimary = resultData.getStringExtra("txtPrimary");
                String txtSecondary = resultData.getStringExtra("txtSecondary");
                String editTextShareNews = resultData.getStringExtra("editTextShareNews");
                hashMap = (HashMap<Integer, ArrayList<String>>) resultData.getSerializableExtra("hashMap");
                isFirstTime = false;

                uploadPostDataToFirebase(txtPrimary, txtSecondary, editTextShareNews);
            } else if (resultCode == RESULT_CANCELED) {

            }
        }

    }


    private void uploadPostDataToFirebase(final String txtPrimary, final String txtSecondary, final String editTextShareNews) {

        ((NewsFeedFragment) fragmentList.get(0)).postList.add(1, new NewPostProgress(0));
        ((NewsFeedFragment) fragmentList.get(0)).newsFeedRecyclerAdapter.notifyItemInserted(1);
        ((NewsFeedFragment) fragmentList.get(0)).newsFeedRecyclerAdapter.notifyItemRangeChanged(1,
                ((NewsFeedFragment) fragmentList.get(0)).postList.size());

        final String userid = sharedPrefs.getUserIdFromSharedPref();
        URL = Constants.protocol + Constants.IP + Constants.addNewPost;

        if (hashMap != null) {
            boolean videoContains = false;
            //List<String> videos = new ArrayList<>();
//            for (ArrayList<String> item : hashMap.values()) {
//                if (Integer.parseInt(item.get(0)) == 2) {
//                    videos.add(item.get(1));
//                }
//            }
            for (ArrayList<String> item : hashMap.values()) {
                if (Integer.parseInt(item.get(0)) == 2) { // 2 FOR VIDEO
                    videoContains = true;
                    //videos.add(item.get(1));
                }
            }
//            for (ArrayList<String> item : hashMap.values()) {
//                //String type = getMimeType(Uri.fromFile(new File(item)));
//
//                if (Integer.parseInt(item.get(0)) == 1) // 1 FOR IMAGE
//                {
//                    Bitmap bmp = ImageCompression.getImageFromResult(context, item.get(1));//your compressed bitmap here
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
//                    //String uniqueId = UUID.randomUUID().toString();
//
//                    MainActivity.Media mediaClass = new MainActivity.Media(1, imageByteData);
//                    mediaClassList.add(mediaClass);
//                    //media.add("image:" + uniqueId);
//
//                }
//            }
            ((NewPostProgress) ((NewsFeedFragment) fragmentList.get(0)).postList.get(1)).setProgress(25);
            ((NewsFeedFragment) fragmentList.get(0)).newsFeedRecyclerAdapter.notifyItemChanged(1);
            if (!videoContains) {

                new OnlyImagesUploadAsynTask(context).execute(txtPrimary, txtSecondary, editTextShareNews);
                //uploadImagesAndSavePostDataInMongo(txtPrimary, txtSecondary, editTextShareNews, mediaClassList, media);
            } else {
                //create destination directory
                File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + getPackageName() + "/media/videos");
                if (f.mkdirs() || f.isDirectory()) {
                    //compress and output new video specs
                    new VideoCompressAsyncTask(context).execute(f.getPath(), txtPrimary, txtSecondary, editTextShareNews);

                }
            }

            Log.d("VIDEOPOST", "VIDEOPOST");

        } else {
            Map<String, Map<String, Object>> Data = new HashMap<>();

            Long timestamp = System.currentTimeMillis();
            Map<String, Object> PostData = new HashMap<>();
            PostData.put("UserId", userid);
            PostData.put("Location", txtPrimary);
            PostData.put("PostText", editTextShareNews);
            PostData.put("timestamp", timestamp);
            PostData.put("Town", txtSecondary);
            PostData.put("LikesCount", 0);
            PostData.put("CommentsCount", 0);

            Map<String, Object> NotificationData = new HashMap<>();
            NotificationData.put("ByUserId", userid);
            NotificationData.put("timestamp", timestamp);
            NotificationData.put("Read", false);
            NotificationData.put("NotificationType", "town_post");

            Data.put("PostData", PostData);
            Data.put("NotificationData", NotificationData);


            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, new JSONObject(Data), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d("volleyadd", response.toString());
                    try {
                        if (response.getBoolean("success")) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ((NewsFeedFragment) fragmentList.get(0)).newsFeedRecyclerAdapter.dismissNewPostProgress();
                                }
                            });

                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ((NewsFeedFragment) fragmentList.get(0)).newsFeedRecyclerAdapter.dismissNewPostProgress();
                                    Toast.makeText(context, "Could not upload, try again!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //FirebasePushNotificationMethods.sendTownPostNotification(userid, key, txtPrimary, txtSecondary, editTextShareNews, context);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("VolleyError", error.toString());
                }
            });


            RequestQueue queue = Volley.newRequestQueue(context);
            queue.add(jsonObjectRequest);
            jsonObjectRequest.setRetryPolicy(new RetryPolicy() {
                @Override
                public int getCurrentTimeout() {
                    return 50000;
                }

                @Override
                public int getCurrentRetryCount() {
                    return 50000;
                }

                @Override
                public void retry(VolleyError error) throws VolleyError {

                }
            });

        }
//
//
//        if (hashMap == null) {
//            //MONGODB INSERT
//
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
//                    media.add("image:" + uniqueId);
//                    final StorageReference mediaRef = storageRef.child("images/image:" + uniqueId);
//
//                    mediaRef.putBytes(medias.get(i).getImageBytes())
//                            .addOnFailureListener(new OnFailureListener() {
//                                @Override
//                                public void onFailure(@NonNull Exception e) {
//                                    Log.d("storageerror", e.toString());
//                                }
//                            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                        @Override
//                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                            Toast.makeText(getBaseContext(), "uploaded", Toast.LENGTH_SHORT).show();
//
//
//                            if (isLastItem) {
//
//                                //MONGODB INSERT
//                                Map<String, Map<String, Object>> Data = new HashMap<>();
//
//                                Long timestamp = System.currentTimeMillis();
//                                Map<String, Object> PostData = new HashMap<>();
//                                PostData.put("UserId", userid);
//                                PostData.put("Location", txtPrimary);
//                                PostData.put("PostText", editTextShareNews);
//                                PostData.put("timestamp", timestamp.toString());
//                                PostData.put("Town", txtSecondary);
//                                PostData.put("ContentPost", media);
//                                PostData.put("LikesCount", 0);
//                                PostData.put("CommentsCount", 0);
//
//
//                                Map<String, Object> NotificationData = new HashMap<>();
//                                NotificationData.put("ByUserId", userid);
//                                NotificationData.put("timestamp", timestamp);
//                                NotificationData.put("Read", false);
//                                NotificationData.put("NotificationType", "town_post");
//
//                                Data.put("PostData", PostData);
//                                Data.put("NotificationData", NotificationData);
//
//
//                                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(URL, new JSONObject(Data), new Response.Listener<JSONObject>() {
//                                    @Override
//                                    public void onResponse(JSONObject response) {
//                                        Log.d("volleyadd", response.toString());
//                                        //FirebasePushNotificationMethods.sendTownPostNotification(userid, key, txtPrimary, txtSecondary, editTextShareNews, context);
//                                    }
//                                }, new Response.ErrorListener() {
//                                    @Override
//                                    public void onErrorResponse(VolleyError error) {
//                                        Log.d("VolleyError", error.toString());
//                                    }
//                                });
//
//
//                                RequestQueue queue = Volley.newRequestQueue(context);
//                                queue.add(jsonObjectRequest);
//                                //                                Long timeStamp = System.currentTimeMillis();
////                                PostsPOJO postsPOJO = new PostsPOJO(0, userid, key, "profilepic:" + userid, username, timeStamp, editTextShareNews,
////                                        txtPrimary, txtSecondary,
////                                        media, Collections.<String, Boolean>emptyMap());
////
////                                Map<String, Object> childUpdates = new HashMap<>();
////                                childUpdates.put("/posts/" + key, postsPOJO);
//////                                mDatabase.child("posts").child(key).child("likes").setValue(0);
////                                databaseReference.updateChildren(childUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
////                                    @Override
////                                    public void onSuccess(Void aVoid) {
////
////                                        //SEND NOTIFICATION TO USERS
////                                        //sendNotificationToUsers(key, txtPrimary, txtSecondary, editTextShareNews);
////                                        FirebasePushNotificationMethods.sendTownPostNotification(userid, key, txtPrimary, txtSecondary, editTextShareNews, context);
////
////                                        queryForUserNumberOfPosts.child(userid).child("number_of_posts").addListenerForSingleValueEvent(new ValueEventListener() {
////                                            @Override
////                                            public void onDataChange(DataSnapshot dataSnapshot) {
////                                                number_of_posts = dataSnapshot.getValue(Integer.class);
////                                                queryForUserNumberOfPosts.child(userid).child("number_of_posts").setValue(number_of_posts + 1);
////                                            }
////
////                                            @Override
////                                            public void onCancelled(DatabaseError databaseError) {
////
////                                            }
////                                        });
////
////                                    }
////                                });
//
//                            }
//                        }
//                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
//                        @Override
//                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
//                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
//                            Toast.makeText(context, progress + "% done", Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                } else if (medias.get(i).getMediaType() == 2) { //VIDEO
//
//                    if (i == last_item) {  //IF Last Image/Video
//                        isLastItem = true;
//                    }
//
//                    String uniqueId = UUID.randomUUID().toString();
//                    media.add("video:" + uniqueId);
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
//                                //MONGODB INSERT
//                                Map<String, Map<String, Object>> Data = new HashMap<>();
//
//                                Long timestamp = System.currentTimeMillis();
//                                Map<String, Object> PostData = new HashMap<>();
//                                PostData.put("UserId", userid);
//                                PostData.put("Location", txtPrimary);
//                                PostData.put("PostText", editTextShareNews);
//                                PostData.put("timestamp", timestamp.toString());
//                                PostData.put("Town", txtSecondary);
//                                PostData.put("ContentPost", media);
//                                PostData.put("LikesCount", 0);
//                                PostData.put("CommentsCount", 0);
//
//                                Map<String, Object> NotificationData = new HashMap<>();
//                                NotificationData.put("ByUserId", userid);
//                                NotificationData.put("timestamp", timestamp);
//                                NotificationData.put("Read", false);
//                                NotificationData.put("NotificationType", "town_post");
//
//                                Data.put("PostData", PostData);
//                                Data.put("NotificationData", NotificationData);
//
//                                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(URL, new JSONObject(Data), new Response.Listener<JSONObject>() {
//                                    @Override
//                                    public void onResponse(JSONObject response) {
//                                        Log.d("volleyadd", response.toString());
//                                        //FirebasePushNotificationMethods.sendTownPostNotification(userid, key, txtPrimary, txtSecondary, editTextShareNews, context);
//                                    }
//                                }, new Response.ErrorListener() {
//                                    @Override
//                                    public void onErrorResponse(VolleyError error) {
//                                        Log.d("VolleyError", error.toString());
//                                    }
//                                });
//
//
//                                RequestQueue queue = Volley.newRequestQueue(context);
//                                queue.add(jsonObjectRequest);
////                                Long timeStamp = System.currentTimeMillis();
////
////                                PostsPOJO postsPOJO = new PostsPOJO(0, userid, key, "profilepic:" + userid, username, timeStamp, editTextShareNews,
////                                        txtPrimary, txtSecondary,
////                                        media, Collections.<String, Boolean>emptyMap());
////
////                                Map<String, Object> childUpdates = new HashMap<>();
////                                childUpdates.put("/posts/" + key, postsPOJO);
////                                //mDatabase.child("posts").child(key).child("likes").setValue(0);
////                                databaseReference.updateChildren(childUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
////                                    @Override
////                                    public void onSuccess(Void aVoid) {
////
////                                        //SEND NOTIFICATION TO USERS
////                                        //sendNotificationToUsers(key, txtPrimary, txtSecondary, editTextShareNews);
////                                        FirebasePushNotificationMethods.sendTownPostNotification(userid, key, txtPrimary, txtSecondary, editTextShareNews, context);
////
////                                        //int number_of_posts=0;
////                                        queryForUserNumberOfPosts.child(userid).child("number_of_posts").addListenerForSingleValueEvent(new ValueEventListener() {
////                                            @Override
////                                            public void onDataChange(DataSnapshot dataSnapshot) {
////                                                number_of_posts = dataSnapshot.getValue(Integer.class);
////                                                queryForUserNumberOfPosts.child(userid).child("number_of_posts").setValue(number_of_posts + 1);
////                                            }
////
////                                            @Override
////                                            public void onCancelled(DatabaseError databaseError) {
////
////                                            }
////                                        });
////
////                                    }
////                                });
//
//                            }
//                        }
//                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
//                        @Override
//                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
//                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
//                            Toast.makeText(context, progress + "% done", Toast.LENGTH_SHORT).show();
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
    }

//    private void uploadImagesAndSavePostDataInMongo(final String location, final String town, final String postText
//            , List<Media> mediaClassList, final List<String> media) {
//
//
//        for (int i = 0; i < mediaClassList.size(); i++) {
//            String uniqueId = UUID.randomUUID().toString();
//
//            media.add("image:" + uniqueId);
//            mediaRef = storageRef.child("images/image:" + uniqueId);
//
//
//            mediaRef.putBytes(mediaClassList.get(i).getbytes())
//                    .addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            Log.d("storageerror", e.toString());
//                        }
//                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                @Override
//                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                    Toast.makeText(getBaseContext(), "uploaded", Toast.LENGTH_SHORT).show();
//                    contentCount++;
//                    //MONGODB INSERT
//                    Map<String, Map<String, Object>> Data = new HashMap<>();
//
//                    Long timestamp = System.currentTimeMillis();
//                    String userid = sharedPrefs.getUserIdFromSharedPref();
//                    Map<String, Object> PostData = new HashMap<>();
//                    PostData.put("UserId", userid);
//                    PostData.put("Location", location);
//                    PostData.put("PostText", postText);
//                    PostData.put("timestamp", timestamp);
//                    PostData.put("Town", town);
//                    PostData.put("ContentPost", media);
//                    PostData.put("LikesCount", 0);
//                    PostData.put("CommentsCount", 0);
//
//
//                    Map<String, Object> NotificationData = new HashMap<>();
//                    NotificationData.put("ByUserId", userid);
//                    NotificationData.put("timestamp", timestamp);
//                    NotificationData.put("Read", false);
//                    NotificationData.put("NotificationType", "town_post");
//
//                    Data.put("PostData", PostData);
//                    Data.put("NotificationData", NotificationData);
//
//
//                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(URL, new JSONObject(Data), new Response.Listener<JSONObject>() {
//                        @Override
//                        public void onResponse(JSONObject response) {
//                            Log.d("volleyadd", response.toString());
//                            try {
//                                if (response.getBoolean("success")) {
//                                    runOnUiThread(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            ((NewsFeedFragment) fragmentList.get(0)).newsFeedRecyclerAdapter.dismissNewPostProgress();
//                                        }
//                                    });
//
//                                } else {
//                                    runOnUiThread(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            ((NewsFeedFragment) fragmentList.get(0)).newsFeedRecyclerAdapter.dismissNewPostProgress();
//                                            Toast.makeText(context, "Could not upload, try again!", Toast.LENGTH_SHORT).show();
//                                        }
//                                    });
//                                }
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                            //FirebasePushNotificationMethods.sendTownPostNotification(userid, key, txtPrimary, txtSecondary, editTextShareNews, context);
//                        }
//                    }, new Response.ErrorListener() {
//                        @Override
//                        public void onErrorResponse(VolleyError error) {
//                            Log.d("VolleyError", error.toString());
//                        }
//                    });
//
//
//                    RequestQueue queue = Volley.newRequestQueue(context);
//                    queue.add(jsonObjectRequest);
//                }
//            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
//                @Override
//                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
//
//                }
//            });
//        }
//
//    }

    private byte[] convertPathToBytes(String path) throws IOException {

        FileInputStream fis = new FileInputStream(path);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] b = new byte[1024];

        for (int readNum; (readNum = fis.read(b)) != -1; ) {
            bos.write(b, 0, readNum);
        }

        byte[] bytes = bos.toByteArray();

        bos.close();

        return bytes;
    }

    private static class Media {
        private byte[] bytes = null;
        private Integer mediaType;

        private Media(Integer mediaType, byte[] bytes) {
            this.mediaType = mediaType;
            this.bytes = bytes;

        }

        public Integer getMediaType() {
            return mediaType;
        }

        public byte[] getbytes() {
            return bytes;
        }

    }


    @Override
    protected void onPause() {
        super.onPause();
//        if (linearLayoutManager.findFirstCompletelyVisibleItemPosition() == 0) {
//            //this is the top of the RecyclerView
//
//
//        }

        Log.d("onPause", "onPause " + counter++);


    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("onResume", "onResume " + counter++);
        //query.limitToLast(1).addChildEventListener(mChildListener);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("onStart", "onStart " + counter++);
        //query.limitToLast(1).addChildEventListener(mChildListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("onStop", "onStop " + counter++);
        //query.limitToLast(1).removeEventListener(mChildListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ((NewsFeedFragment) fragmentList.get(0)).clearPostsList();
        Log.d("onDestroy", "onDestroy " + counter++);
    }

    private class OnlyImagesUploadAsynTask extends AsyncTask<Object, String, ArrayList<Object>> {
        Context mContext;
        int contentCount = 0;

        public OnlyImagesUploadAsynTask(Context context) {
            this.mContext = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ArrayList<Object> doInBackground(Object... objects) {
            List<Media> mediaClassList = new ArrayList<>();
            List<String> mediaList = new ArrayList<>();

            for (ArrayList<String> item : hashMap.values()) {
                //String type = getMimeType(Uri.fromFile(new File(item)));

                if (Integer.parseInt(item.get(0)) == 1) // 1 FOR IMAGE
                {
                    Bitmap bmp = ImageCompression.getImageFromResult(context, item.get(1));//your compressed bitmap here
                    bmp = ImageCompression.rotate(bmp, Integer.parseInt(item.get(2)));
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] imageByteData = baos.toByteArray();

                    try {
                        baos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    bmp.recycle();
                    //String uniqueId = UUID.randomUUID().toString();

                    MainActivity.Media mediaClass = new MainActivity.Media(1, imageByteData);
                    mediaClassList.add(mediaClass);
                    //media.add("image:" + uniqueId);

                }
            }
            ArrayList<Object> arrayList = new ArrayList<>();

            arrayList.add(objects[0]); //0, primary
            arrayList.add(objects[1]); //1, secondary
            arrayList.add(objects[2]); //2, postText
            arrayList.add(mediaClassList); //3, mediaClassList
            arrayList.add(mediaList); //4, mediaList

            return arrayList;
        }

        @Override
        protected void onPostExecute(final ArrayList<Object> arrayList) {
            super.onPostExecute(arrayList);
            ((NewPostProgress) ((NewsFeedFragment) fragmentList.get(0)).postList.get(1)).setProgress(70);
            ((NewsFeedFragment) fragmentList.get(0)).newsFeedRecyclerAdapter.notifyItemChanged(1);
            Log.d("postarray", arrayList.toString());
            Toast.makeText(context, "On Post Execute", Toast.LENGTH_SHORT).show();
            final List<String> mediaList = ((List<String>) arrayList.get(4));
            final List<Media> mediaClassList = ((List<Media>) arrayList.get(3));
            for (int i = 0; i < mediaClassList.size(); i++) {

                String uniqueId = UUID.randomUUID().toString();

                mediaList.add("image:" + uniqueId);
                mediaRef = storageRef.child("images/image:" + uniqueId);

                mediaRef.putBytes(mediaClassList.get(i).getbytes())
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("storageerror", e.toString());
                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        contentCount++;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getBaseContext(), "uploaded", Toast.LENGTH_SHORT).show();
                                ((NewPostProgress) ((NewsFeedFragment) fragmentList.get(0)).postList.get(1)).setProgress(80);
                                ((NewsFeedFragment) fragmentList.get(0)).newsFeedRecyclerAdapter.notifyItemChanged(1);
                                Log.d("ContentCount", String.valueOf(contentCount));
                            }
                        });
                        if (contentCount == mediaList.size()) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    Toast.makeText(getBaseContext(), "uploaded", Toast.LENGTH_SHORT).show();
                                    ((NewPostProgress) ((NewsFeedFragment) fragmentList.get(0)).postList.get(1)).setProgress(90);
                                    ((NewsFeedFragment) fragmentList.get(0)).newsFeedRecyclerAdapter.notifyItemChanged(1);

                                    Log.d("ContentCount", String.valueOf(contentCount));

                                }

                            });

                            //((NewsFeedFragment) fragmentList.get(0)).newsFeedRecyclerAdapter.dismissNewPostProgress();
                            Toast.makeText(context, "Last Item uploaded", Toast.LENGTH_SHORT).show();
                            //MONGODB INSERT
                            Map<String, Map<String, Object>> Data = new HashMap<>();

                            Long timestamp = System.currentTimeMillis();
                            String userid = sharedPrefs.getUserIdFromSharedPref();
                            Map<String, Object> PostData = new HashMap<>();
                            PostData.put("UserId", userid);
                            PostData.put("Location", arrayList.get(0));
                            PostData.put("PostText", arrayList.get(2));
                            PostData.put("timestamp", timestamp);
                            PostData.put("Town", arrayList.get(1));
                            PostData.put("ContentPost", mediaList);
                            PostData.put("LikesCount", 0);
                            PostData.put("CommentsCount", 0);


                            Map<String, Object> NotificationData = new HashMap<>();
                            NotificationData.put("ByUserId", userid);
                            NotificationData.put("timestamp", timestamp);
                            NotificationData.put("Read", false);
                            NotificationData.put("NotificationType", "town_post");

                            Data.put("PostData", PostData);
                            Data.put("NotificationData", NotificationData);


                            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(URL, new JSONObject(Data), new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    Log.d("volleyadd", response.toString());
                                    try {
                                        if (response.getBoolean("success")) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    ((NewsFeedFragment) fragmentList.get(0)).newsFeedRecyclerAdapter.dismissNewPostProgress();
                                                }
                                            });

                                        } else {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    ((NewsFeedFragment) fragmentList.get(0)).newsFeedRecyclerAdapter.dismissNewPostProgress();
                                                    Toast.makeText(context, "Could not upload, try again!", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    //FirebasePushNotificationMethods.sendTownPostNotification(userid, key, txtPrimary, txtSecondary, editTextShareNews, context);
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.d("VolleyError", error.toString());
                                }
                            });
                            RequestQueue queue = Volley.newRequestQueue(context);
                            queue.add(jsonObjectRequest);
                        }
                    }

                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getBaseContext(), "uploading", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }

        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }
    }

    private class VideoCompressAsyncTask extends AsyncTask<Object, String, ArrayList<Object>> {

        Context mContext;
        int contentCount = 0;

        public VideoCompressAsyncTask(Context context) {
            mContext = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
          /*  imageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_photo_camera_white_48px));
            compressionMsg.setVisibility(View.VISIBLE);
            picDescription.setVisibility(View.GONE);*/


        }

        @Override
        protected ArrayList<Object> doInBackground(final Object... paths) {
            List<String> filePaths = new ArrayList<>();
            //publishProgress("50");
            List<MainActivity.Media> mediaClassList = new ArrayList<>();
            List<String> mediaList = new ArrayList<>();

            for (ArrayList<String> item : hashMap.values()) {
                //String type = getMimeType(Uri.fromFile(new File(item)));


                if (Integer.parseInt(item.get(0)) == 1) // 1 FOR IMAGE
                {
                    Bitmap bmp = ImageCompression.getImageFromResult(context, item.get(1));//your compressed bitmap here
                    bmp = ImageCompression.rotate(bmp, Integer.parseInt(item.get(2)));
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] imageByteData = baos.toByteArray();

                    try {
                        baos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    bmp.recycle();
                    //String uniqueId = UUID.randomUUID().toString();

                    MainActivity.Media mediaClass = new MainActivity.Media(1, imageByteData);
                    mediaClassList.add(mediaClass);
                    //media.add("image:" + uniqueId);

                }else{
                    try {
                        Log.d("CompressVideo", "Compress InProgress");
                        String filePath = SiliCompressor.with(mContext).compressVideo(item.get(1), (String) paths[0]);
                        Log.d("CompressVideo", "Compress Complete");
                        filePaths.add(filePath);
                        byte[] videoByte = convertPathToBytes(String.valueOf(filePath));
                        final MainActivity.Media mediaClass = new MainActivity.Media(2, videoByte);
                        //mediaClassList.add(mediaClass);
                        mediaClassList.add(mediaClass);
                        try {
                            new File(filePath).delete();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }


//            for (String video : (List<String>) paths[0]) {
//                try {
//                    Log.d("CompressVideo", "Compress InProgress");
//                    String filePath = SiliCompressor.with(mContext).compressVideo(video, (String) paths[1]);
//                    Log.d("CompressVideo", "Compress Complete");
//                    filePaths.add(filePath);
//                    byte[] videoByte = convertPathToBytes(String.valueOf(filePath));
//                    final MainActivity.Media mediaClass = new MainActivity.Media(2, videoByte);
//                    //mediaClassList.add(mediaClass);
//                    mediaClassList.add(mediaClass);
//                    try {
//                        new File(filePath).delete();
//                    } catch (Exception ex) {
//                        ex.printStackTrace();
//                    }
//                } catch (URISyntaxException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
            Log.d("progress%", String.valueOf(((NewPostProgress) ((NewsFeedFragment) fragmentList.get(0)).postList.get(1)).getProgress()));
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((NewPostProgress) ((NewsFeedFragment) fragmentList.get(0)).postList.get(1)).setProgress(50);
                    ((NewsFeedFragment) fragmentList.get(0)).newsFeedRecyclerAdapter.notifyItemChanged(1);
                }
            });


//            for (int i = 0; i < ((List<Media>) paths[5]).size(); i++) {
//                String uniqueId = UUID.randomUUID().toString();
//                mediaList.add("video:" + uniqueId);
//                mediaRef = storageRef.child("images/video:" + uniqueId);
//
//                mediaRef.putBytes(((List<Media>) paths[5]).get(i).getbytes())
//                        .addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                Log.d("storageerror", e.toString());
//                            }
//                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                Toast.makeText(getBaseContext(), "uploaded", Toast.LENGTH_SHORT).show();
//                                contentCount++;
//                                Log.d("ContentCount", String.valueOf(contentCount));
//                                if (contentCount == mediaList.size()) {
//                                    Toast.makeText(context, "Last Item uploaded", Toast.LENGTH_SHORT).show();
//                                    //MONGODB INSERT
//                                    Map<String, Map<String, Object>> Data = new HashMap<>();
//
//                                    Long timestamp = System.currentTimeMillis();
//                                    String userid = sharedPrefs.getUserIdFromSharedPref();
//                                    Map<String, Object> PostData = new HashMap<>();
//                                    PostData.put("UserId", userid);
//                                    PostData.put("Location", paths[2]);
//                                    PostData.put("PostText", paths[4]);
//                                    PostData.put("timestamp", timestamp);
//                                    PostData.put("Town", paths[3]);
//                                    PostData.put("ContentPost", mediaList);
//                                    PostData.put("LikesCount", 0);
//                                    PostData.put("CommentsCount", 0);
//
//
//                                    Map<String, Object> NotificationData = new HashMap<>();
//                                    NotificationData.put("ByUserId", userid);
//                                    NotificationData.put("timestamp", timestamp);
//                                    NotificationData.put("Read", false);
//                                    NotificationData.put("NotificationType", "town_post");
//
//                                    Data.put("PostData", PostData);
//                                    Data.put("NotificationData", NotificationData);
//
//
//                                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(URL, new JSONObject(Data), new Response.Listener<JSONObject>() {
//                                        @Override
//                                        public void onResponse(JSONObject response) {
//                                            Log.d("volleyadd", response.toString());
//                                            //FirebasePushNotificationMethods.sendTownPostNotification(userid, key, txtPrimary, txtSecondary, editTextShareNews, context);
//                                        }
//                                    }, new Response.ErrorListener() {
//                                        @Override
//                                        public void onErrorResponse(VolleyError error) {
//                                            Log.d("VolleyError", error.toString());
//                                        }
//                                    });
//
//
//                                    RequestQueue queue = Volley.newRequestQueue(context);
//                                    queue.add(jsonObjectRequest);
//
//
//                                }
//
//                            }
//                        });
//
//                    }
//                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                Toast.makeText(getBaseContext(), "uploading", Toast.LENGTH_SHORT).show();
//                            }
//                        });
//                    }
//                });
//            }

            //String filePath = null;
            ArrayList<Object> arrayList = new ArrayList<>();
//new VideoCompressAsyncTask(context).execute(f.getPath(), txtPrimary, txtSecondary, editTextShareNews);
            //File uri = new File(paths[0]);
            //Uri uri = Uri.parse(paths[0]);

            //Uri baseUri = Uri.parse("content://media/external/video/media");
            //Uri uri = Uri.withAppendedPath(baseUri, "" + paths[0]);
            //Log.d("CompressUri",uri.toString());
            //filePath = SiliCompressor.with(mContext).compressVideo((String) paths[0],(String) paths[1]);
            arrayList.add(filePaths); //0
            arrayList.add(paths[1]); //1, location
            arrayList.add(paths[2]); //2, town
            arrayList.add(paths[3]); //3, textnews
            arrayList.add(mediaClassList); //4, mediaClassList
            arrayList.add(mediaList); //5, mediaList

            return arrayList;

        }


        @Override
        protected void onPostExecute(final ArrayList<Object> arrayList) {
            super.onPostExecute(arrayList);
            ((NewPostProgress) ((NewsFeedFragment) fragmentList.get(0)).postList.get(1)).setProgress(70);
            ((NewsFeedFragment) fragmentList.get(0)).newsFeedRecyclerAdapter.notifyItemChanged(1);
            Log.d("postarray", arrayList.toString());
            Toast.makeText(context, "On Post Execute", Toast.LENGTH_SHORT).show();
            final List<String> mediaList = ((List<String>) arrayList.get(5));
            final List<Media> mediaClassList = ((List<Media>) arrayList.get(4));
            for (int i = 0; i < mediaClassList.size(); i++) {

                String uniqueId = UUID.randomUUID().toString();
                if (mediaClassList.get(i).getMediaType() == 1) {
                    mediaList.add("image:" + uniqueId);
                    mediaRef = storageRef.child("images/image:" + uniqueId);
                } else {
                    mediaList.add("video:" + uniqueId);
                    mediaRef = storageRef.child("images/video:" + uniqueId);
                }
                //mediaList.add("video:" + uniqueId);
                //mediaRef = storageRef.child("images/video:" + uniqueId);

                mediaRef.putBytes(mediaClassList.get(i).getbytes())
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("storageerror", e.toString());
                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        contentCount++;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getBaseContext(), "uploaded", Toast.LENGTH_SHORT).show();
                                ((NewPostProgress) ((NewsFeedFragment) fragmentList.get(0)).postList.get(1)).setProgress(80);
                                ((NewsFeedFragment) fragmentList.get(0)).newsFeedRecyclerAdapter.notifyItemChanged(1);
                                Log.d("ContentCount", String.valueOf(contentCount));
                            }
                        });
                        if (contentCount == mediaList.size()) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    Toast.makeText(getBaseContext(), "uploaded", Toast.LENGTH_SHORT).show();
                                    ((NewPostProgress) ((NewsFeedFragment) fragmentList.get(0)).postList.get(1)).setProgress(90);
                                    ((NewsFeedFragment) fragmentList.get(0)).newsFeedRecyclerAdapter.notifyItemChanged(1);

                                    Log.d("ContentCount", String.valueOf(contentCount));

                                }

                            });

                            //((NewsFeedFragment) fragmentList.get(0)).newsFeedRecyclerAdapter.dismissNewPostProgress();
                            Toast.makeText(context, "Last Item uploaded", Toast.LENGTH_SHORT).show();
                            //MONGODB INSERT
                            Map<String, Map<String, Object>> Data = new HashMap<>();

                            Long timestamp = System.currentTimeMillis();
                            String userid = sharedPrefs.getUserIdFromSharedPref();
                            Map<String, Object> PostData = new HashMap<>();
                            PostData.put("UserId", userid);
                            PostData.put("Location", arrayList.get(1));
                            PostData.put("PostText", arrayList.get(3));
                            PostData.put("timestamp", timestamp);
                            PostData.put("Town", arrayList.get(2));
                            PostData.put("ContentPost", mediaList);
                            PostData.put("LikesCount", 0);
                            PostData.put("CommentsCount", 0);


                            Map<String, Object> NotificationData = new HashMap<>();
                            NotificationData.put("ByUserId", userid);
                            NotificationData.put("timestamp", timestamp);
                            NotificationData.put("Read", false);
                            NotificationData.put("NotificationType", "town_post");

                            Data.put("PostData", PostData);
                            Data.put("NotificationData", NotificationData);


                            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(URL, new JSONObject(Data), new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    Log.d("volleyadd", response.toString());
                                    try {
                                        if (response.getBoolean("success")) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    ((NewsFeedFragment) fragmentList.get(0)).newsFeedRecyclerAdapter.dismissNewPostProgress();
                                                }
                                            });

                                        } else {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    ((NewsFeedFragment) fragmentList.get(0)).newsFeedRecyclerAdapter.dismissNewPostProgress();
                                                    Toast.makeText(context, "Could not upload, try again!", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    //FirebasePushNotificationMethods.sendTownPostNotification(userid, key, txtPrimary, txtSecondary, editTextShareNews, context);
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.d("VolleyError", error.toString());
                                }
                            });
                            RequestQueue queue = Volley.newRequestQueue(context);
                            queue.add(jsonObjectRequest);
                        }
                    }

                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getBaseContext(), "uploading", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
//            File imageFile = new File( new List<>((List<String>) arrayList.get(0)).get(0); //Compressed File
//            float length = imageFile.length() / 1024f; // Size in KB
//            String value;
//            if (length >= 1024)
//                value = length / 1024f + " MB";
//            else
//                value = length + " KB";
//            String text = String.format(Locale.US, "%s\nName: %s\nSize: %s", getString(R.string.video_compression_complete), imageFile.getName(), value);
//            Log.d("CompressComplete", text);
//            Log.i("Silicompressor", "Path: " + arrayList.get(0));

            //MONGODB INSERT
            /*Map<String, Map<String, Object>> Data = new HashMap<>();

            Long timestamp = System.currentTimeMillis();
            String userid = sharedPrefs.getUserIdFromSharedPref();
            Map<String, Object> PostData = new HashMap<>();
            PostData.put("UserId", userid);
            PostData.put("Location", arrayList.get(1));
            PostData.put("PostText", arrayList.get(3));
            PostData.put("timestamp", timestamp);
            PostData.put("Town", arrayList.get(2));
            PostData.put("ContentPost", arrayList.get(4));
            PostData.put("LikesCount", 0);
            PostData.put("CommentsCount", 0);


            Map<String, Object> NotificationData = new HashMap<>();
            NotificationData.put("ByUserId", userid);
            NotificationData.put("timestamp", timestamp);
            NotificationData.put("Read", false);
            NotificationData.put("NotificationType", "town_post");

            Data.put("PostData", PostData);
            Data.put("NotificationData", NotificationData);


            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(URL, new JSONObject(Data), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d("volleyadd", response.toString());
                    //FirebasePushNotificationMethods.sendTownPostNotification(userid, key, txtPrimary, txtSecondary, editTextShareNews, context);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("VolleyError", error.toString());
                }
            });


            RequestQueue queue = Volley.newRequestQueue(context);
            queue.add(jsonObjectRequest);
*/

//            try {
//                byte[] videoByte = convertPathToBytes(String.valueOf(imageFile));
//                final MainActivity.Media mediaClass = new MainActivity.Media(2, videoByte);
//                //mediaClassList.add(mediaClass);
//                ((List<Media>)arrayList.get(4)).add(mediaClass);
//
//                final List<String> mediaList = ((List<String>)arrayList.get(5));
//
//
//
//                if (isLastItem) {
//                    for (int i = 0; i < ((List<Media>)arrayList.get(4)).size(); i++) {
//                        String uniqueId = UUID.randomUUID().toString();
//
//                        if (((List<Media>)arrayList.get(4)).get(i).getMediaType() == 1) // IMAGE
//                        {
//                            mediaList.add("image:" + uniqueId);
//                            mediaRef = storageRef.child("images/image:" + uniqueId);
//                        } else {
//                            mediaList.add("video:" + uniqueId);
//                            mediaRef = storageRef.child("images/video:" + uniqueId);
//                        }
//
//                        mediaRef.putBytes(((List<Media>)arrayList.get(4)).get(i).getbytes())
//                                .addOnFailureListener(new OnFailureListener() {
//                                    @Override
//                                    public void onFailure(@NonNull Exception e) {
//                                        Log.d("storageerror", e.toString());
//                                    }
//                                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                            @Override
//                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                                Toast.makeText(getBaseContext(), "uploaded", Toast.LENGTH_SHORT).show();
//
//
//
//                                //MONGODB INSERT
//                                Map<String, Map<String, Object>> Data = new HashMap<>();
//
//                                Long timestamp = System.currentTimeMillis();
//                                String userid = sharedPrefs.getUserIdFromSharedPref();
//                                Map<String, Object> PostData = new HashMap<>();
//                                PostData.put("UserId", userid);
//                                PostData.put("Location", arrayList.get(1));
//                                PostData.put("PostText", arrayList.get(3));
//                                PostData.put("timestamp", timestamp.toString());
//                                PostData.put("Town", arrayList.get(2));
//                                PostData.put("ContentPost", mediaList);
//                                PostData.put("LikesCount", 0);
//                                PostData.put("CommentsCount", 0);
//
//
//                                Map<String, Object> NotificationData = new HashMap<>();
//                                NotificationData.put("ByUserId", userid);
//                                NotificationData.put("timestamp", timestamp);
//                                NotificationData.put("Read", false);
//                                NotificationData.put("NotificationType", "town_post");
//
//                                Data.put("PostData", PostData);
//                                Data.put("NotificationData", NotificationData);
//
//
//                                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(URL, new JSONObject(Data), new Response.Listener<JSONObject>() {
//                                    @Override
//                                    public void onResponse(JSONObject response) {
//                                        Log.d("volleyadd", response.toString());
//                                        //FirebasePushNotificationMethods.sendTownPostNotification(userid, key, txtPrimary, txtSecondary, editTextShareNews, context);
//                                    }
//                                }, new Response.ErrorListener() {
//                                    @Override
//                                    public void onErrorResponse(VolleyError error) {
//                                        Log.d("VolleyError", error.toString());
//                                    }
//                                });
//
//
//                                RequestQueue queue = Volley.newRequestQueue(context);
//                                queue.add(jsonObjectRequest);
//
//                            }
//
//                        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
//                            @Override
//                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
//
//                            }
//                        });
//                    }
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            Log.d("progress", values.toString());

            //Toast.makeText(context,values.toString(),Toast.LENGTH_SHORT).show();
        }
    }
}
