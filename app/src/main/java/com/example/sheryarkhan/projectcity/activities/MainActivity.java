package com.example.sheryarkhan.projectcity.activities;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.sheryarkhan.projectcity.R;
import com.example.sheryarkhan.projectcity.utils.Constants;
import com.example.sheryarkhan.projectcity.utils.FirebasePushNotificationMethods;
import com.example.sheryarkhan.projectcity.utils.IVolleyResult;
import com.example.sheryarkhan.projectcity.utils.ImageCompression;
import com.example.sheryarkhan.projectcity.utils.VolleyService;
import com.example.sheryarkhan.projectcity.adapters.SectionsPagerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.UUID;

import data.Posts;
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
    private List<String> media = new ArrayList<>();

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

    private MainFragment mainFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mStack = new Stack<>();

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
        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        //setupFirebaseAuth();




        //setupBottomNavigationView();
        setupViewPager();
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
    public void loadSettingNotificationFragment(){

        SettingNotificationFragment settingNotificationFragment = new SettingNotificationFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.comments_fragment_layout,settingNotificationFragment);
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
                //query.limitToLast(1).addChildEventListener(mChildListener);


                uploadPostDataToFirebase(txtPrimary, txtSecondary, editTextShareNews);
            } else if (resultCode == RESULT_CANCELED) {

            }
        }

    }


    private void uploadPostDataToFirebase(final String txtPrimary, final String txtSecondary, final String editTextShareNews) {
//        String URL = Constants.protocol + Constants.IP + "/addNewPost";
//        Long timestamp = System.currentTimeMillis();
//        Map<String, String> PostData = new HashMap<>();
//        PostData.put("UserId", firebaseAuth.getCurrentUser().getUid());
//        PostData.put("Location", txtPrimary);
//        PostData.put("PostText", editTextShareNews);
//        PostData.put("timestamp", timestamp.toString());
//        PostData.put("Town", txtSecondary);
//
//
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(URL, new JSONObject(PostData), new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//                Log.d("volleyadd",response.toString());
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.d("VolleyError", error.toString());
//            }
//        });
//
//        RequestQueue queue = Volley.newRequestQueue(this);
//        queue.add(jsonObjectRequest);

        SharedPreferences sharedPref = MainActivity.this.getSharedPreferences("UserData", Context.MODE_PRIVATE);
        final String username = sharedPref.getString("username", "");
        //final String userid = sharedPref.getString("userid", "");

        final String userid = firebaseAuth.getCurrentUser().getUid();
        String imageURI = sharedPref.getString("profilepicture", "");

        final String key = databaseReference.child("posts").push().getKey();


        FirebaseStorage storage = FirebaseStorage.getInstance();

        StorageReference storageRef = storage.getReference();
        final DatabaseReference queryForUserNumberOfPosts = databaseReference.child("Users");
        // Uri file = Uri.fromFile(new File(mImageUri.toString()));

        //final Iterator iterator = hashMap.values().iterator();
        //Map<Integer, ArrayList<Object>> hMap = Collections.emptyMap();
        //int i = 0;
        final List<MainActivity.Media> medias = new ArrayList<>();
        if (hashMap != null) {
            for (ArrayList<String> item : hashMap.values()) {


                //String type = getMimeType(Uri.fromFile(new File(item)));
                if (Integer.parseInt(item.get(0)) == 1) // 1 FOR IMAGE
                {
                    Bitmap bmp = ImageCompression.getImageFromResult(MainActivity.this, item.get(1));//your compressed bitmap here
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
                    try {
                        MainActivity.Media media = new MainActivity.Media(1, imageByteData, null);
                        medias.add(media);
                    } catch (Exception ex) {
                        Log.d("mediaError", ex.toString());
                    }
                } else if (Integer.parseInt(item.get(0)) == 2) // 2 FOR VIDEO
                {
                    try {
                        byte[] videoByte = convertPathToBytes(String.valueOf(item.get(1)));
                        MainActivity.Media media = new MainActivity.Media(2, null, videoByte);
                        medias.add(media);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Log.d("VIDEOPOST", "VIDEOPOST");
                }
            }
        }

        if (medias.size() == 0) {
            //MONGODB INSERT
            String URL = Constants.protocol + Constants.IP + Constants.addNewPost;
            Long timestamp = System.currentTimeMillis();
            Map<String, String> PostData = new HashMap<>();
            PostData.put("UserId", userid);
            PostData.put("Location", txtPrimary);
            PostData.put("PostText", editTextShareNews);
            PostData.put("timestamp", timestamp.toString());
            PostData.put("Town", txtSecondary);


            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(URL, new JSONObject(PostData), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d("volleyadd",response.toString());
                    FirebasePushNotificationMethods.sendTownPostNotification(userid, key, txtPrimary, txtSecondary, editTextShareNews, MainActivity.this);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("VolleyError", error.toString());
                }
            });


            RequestQueue queue = Volley.newRequestQueue(this);
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


            //FIREBASE DB INSERT
            Long timeStamp = System.currentTimeMillis();
            PostsPOJO postsPOJO = new PostsPOJO(0, userid, key, "profilepic:" + userid, username, timeStamp, editTextShareNews,
                    txtPrimary, txtSecondary, Collections.<String, Boolean>emptyMap());


//            Map<String, Object> childUpdates = new HashMap<>();
//            childUpdates.put("/posts/" + key, postsPOJO);
//            databaseReference.updateChildren(childUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
//                @Override
//                public void onSuccess(Void aVoid) {
//
//
//                    //SEND NOTIFICATION TO USERS
//                    //sendNotificationToUsers(key, txtPrimary, txtSecondary, editTextShareNews);
//                    //FirebasePushNotificationMethods.sendTownPostNotification(userid, key, txtPrimary, txtSecondary, editTextShareNews, MainActivity.this);
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

        } else {

            final int last_item = (medias.size() - 1);

            for (int i = 0; i < medias.size(); i++) {
                if (medias.get(i).getMediaType() == 1) // IMAGE
                {
                    if (i == last_item) {  //IF Last Image/Video
                        isLastItem = true;
                    }
                    String uniqueId = UUID.randomUUID().toString();
                    media.add("image:" + uniqueId);
                    final StorageReference mediaRef = storageRef.child("images/image:" + uniqueId);

                    mediaRef.putBytes(medias.get(i).getImageBytes())
                            .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("storageerror", e.toString());
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(getBaseContext(), "uploaded", Toast.LENGTH_SHORT).show();


                            if (isLastItem) {

                                //MONGODB INSERT
                                String URL = Constants.protocol + Constants.IP + Constants.addNewPost;
                                Long timestamp = System.currentTimeMillis();
                                Map<String, Object> PostData = new HashMap<>();
                                PostData.put("UserId", userid);
                                PostData.put("Location", txtPrimary);
                                PostData.put("PostText", editTextShareNews);
                                PostData.put("timestamp", timestamp.toString());
                                PostData.put("Town", txtSecondary);
                                PostData.put("ContentPost",media);


                                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(URL, new JSONObject(PostData), new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        Log.d("volleyadd",response.toString());
                                        FirebasePushNotificationMethods.sendTownPostNotification(userid, key, txtPrimary, txtSecondary, editTextShareNews, MainActivity.this);
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.d("VolleyError", error.toString());
                                    }
                                });


                                RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
                                queue.add(jsonObjectRequest);
                                //                                Long timeStamp = System.currentTimeMillis();
//                                PostsPOJO postsPOJO = new PostsPOJO(0, userid, key, "profilepic:" + userid, username, timeStamp, editTextShareNews,
//                                        txtPrimary, txtSecondary,
//                                        media, Collections.<String, Boolean>emptyMap());
//
//                                Map<String, Object> childUpdates = new HashMap<>();
//                                childUpdates.put("/posts/" + key, postsPOJO);
////                                mDatabase.child("posts").child(key).child("likes").setValue(0);
//                                databaseReference.updateChildren(childUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
//                                    @Override
//                                    public void onSuccess(Void aVoid) {
//
//                                        //SEND NOTIFICATION TO USERS
//                                        //sendNotificationToUsers(key, txtPrimary, txtSecondary, editTextShareNews);
//                                        FirebasePushNotificationMethods.sendTownPostNotification(userid, key, txtPrimary, txtSecondary, editTextShareNews, MainActivity.this);
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

                            }
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            Toast.makeText(MainActivity.this, progress + "% done", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else if (medias.get(i).getMediaType() == 2) { //VIDEO

                    if (i == last_item) {  //IF Last Image/Video
                        isLastItem = true;
                    }

                    String uniqueId = UUID.randomUUID().toString();
                    media.add("video:" + uniqueId);
                    StorageReference mediaRef = storageRef.child("images/video:" + uniqueId);

                    mediaRef.putBytes(medias.get(i).getVideoBytes()).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("storageerror", e.toString());
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(getBaseContext(), "uploaded", Toast.LENGTH_SHORT).show();
                            if (isLastItem) {
                                //MONGODB INSERT
                                String URL = Constants.protocol + Constants.IP + Constants.addNewPost;
                                Long timestamp = System.currentTimeMillis();
                                Map<String, Object> PostData = new HashMap<>();
                                PostData.put("UserId", userid);
                                PostData.put("Location", txtPrimary);
                                PostData.put("PostText", editTextShareNews);
                                PostData.put("timestamp", timestamp.toString());
                                PostData.put("Town", txtSecondary);
                                PostData.put("ContentPost",media);


                                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(URL, new JSONObject(PostData), new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        Log.d("volleyadd",response.toString());
                                        //FirebasePushNotificationMethods.sendTownPostNotification(userid, key, txtPrimary, txtSecondary, editTextShareNews, MainActivity.this);
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.d("VolleyError", error.toString());
                                    }
                                });


                                RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
                                queue.add(jsonObjectRequest);
//                                Long timeStamp = System.currentTimeMillis();
//
//                                PostsPOJO postsPOJO = new PostsPOJO(0, userid, key, "profilepic:" + userid, username, timeStamp, editTextShareNews,
//                                        txtPrimary, txtSecondary,
//                                        media, Collections.<String, Boolean>emptyMap());
//
//                                Map<String, Object> childUpdates = new HashMap<>();
//                                childUpdates.put("/posts/" + key, postsPOJO);
//                                //mDatabase.child("posts").child(key).child("likes").setValue(0);
//                                databaseReference.updateChildren(childUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
//                                    @Override
//                                    public void onSuccess(Void aVoid) {
//
//                                        //SEND NOTIFICATION TO USERS
//                                        //sendNotificationToUsers(key, txtPrimary, txtSecondary, editTextShareNews);
//                                        FirebasePushNotificationMethods.sendTownPostNotification(userid, key, txtPrimary, txtSecondary, editTextShareNews, MainActivity.this);
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

                            }
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            Toast.makeText(MainActivity.this, progress + "% done", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("uploadError", e.toString());
                        }
                    });

                }
            }

        }
    }

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

//    private void sendNotificationToUsers(String key, String txtPrimary, String txtSecondary, String editTextShareNews) {
//        Map<String, String> townAndMessage = new HashMap<>();
//        townAndMessage.put("town", txtSecondary.replace(" ", "_"));
//        townAndMessage.put("message", editTextShareNews);
//        townAndMessage.put("postId", key);
//        mVolleyService = new VolleyService(mResultCallback, MainActivity.this);
//        mVolleyService.postDataVolley(Request.Method.POST,
//                Constants.protocol + Constants.IP + "/sendNotification",
//                new JSONObject(townAndMessage));
//    }


    private static class Media {
        private byte[] imageBytes = null;
        private byte[] videoBytes = null;
        private Integer mediaType;

        private Media(Integer mediaType, byte[] imageBytes, byte[] videoBytes) {
            this.mediaType = mediaType;
            this.imageBytes = imageBytes;
            this.videoBytes = videoBytes;

        }

        public Integer getMediaType() {
            return mediaType;
        }

        public byte[] getImageBytes() {
            return imageBytes;
        }

        public byte[] getVideoBytes() {
            return videoBytes;
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
        Log.d("onDestroy", "onDestroy " + counter++);
    }

}
