package com.example.sheryarkhan.projectcity.activities;

import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.sheryarkhan.projectcity.R;
import com.example.sheryarkhan.projectcity.utils.Constants;
import com.example.sheryarkhan.projectcity.utils.EndlessRecyclerOnScrollListener;
import com.example.sheryarkhan.projectcity.utils.IVolleyResult;
import com.example.sheryarkhan.projectcity.utils.SharedPrefs;
import com.example.sheryarkhan.projectcity.utils.VolleyService;
import com.example.sheryarkhan.projectcity.adapters.NewsFeedRecyclerAdapter;
import com.facebook.ads.AdSettings;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdsManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import data.Post;
import data.PostsPOJO;


public class NewsFeedFragment extends Fragment {
    private static final int TOTAL_ITEM_EACH_LOAD = 10;
    private int currentPage = 0;

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView newsFeedRecyclerView;
    private NewsFeedRecyclerAdapter newsFeedRecyclerAdapter;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private ChildEventListener mChildListener;
    private ProgressBar recyclerViewProgressBar;
    private TextView txtNetworkError;

    //POST UPLOAD RELATED
    private Map<Integer, ArrayList<String>> hashMap = Collections.emptyMap();
    private boolean isLastItem = false;
    private static Integer number_of_posts = 0;
    private Map<String, Boolean> media = new HashMap<>();
    private IVolleyResult mResultCallback;
    private VolleyService mVolleyService;
    private String URL;
    private int page_num;
    private String town;
    private SharedPrefs sharedPrefs;

    private LinearLayoutManager linearLayoutManager;

    private static final int ACTIVITY_NUM = 0;
    private ArrayList<PostsPOJO> list;
    private String oldestPostId;

    boolean isFirstTime = true;


    int counter = 1;
    Query query;
    View view;
    private boolean mIsLoading = false;


    private List<Post> postList;

    private NativeAdsManager mAds;

    public NewsFeedFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        page_num = 0;
        sharedPrefs = new SharedPrefs(getActivity());
        town = sharedPrefs.getTownFromSharedPref();
        URL = changePageNumberURL(page_num);
        String iid = FirebaseInstanceId.getInstance().getToken();


    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (savedInstanceState != null) {
            Log.d("instance", savedInstanceState.toString());
        }

        if (view != null) {
            if (view.getParent() != null)
                ((ViewGroup) view.getParent()).removeView(view);
            return view;

        }

        view = inflater.inflate(R.layout.fragment_news_feed, container, false);

        list = new ArrayList<>();
        postList = new ArrayList<>();

        newsFeedRecyclerView = (RecyclerView) view.findViewById(R.id.news_feed_recyclerview);

        newsFeedRecyclerView.setHasFixedSize(true);
        newsFeedRecyclerView.setItemViewCacheSize(20);
        newsFeedRecyclerView.setDrawingCacheEnabled(true);
        newsFeedRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        newsFeedRecyclerView.setLayoutManager(linearLayoutManager);
        //loadAds();
        newsFeedRecyclerAdapter = new NewsFeedRecyclerAdapter(postList, getActivity(),mAds);
        newsFeedRecyclerView.setAdapter(newsFeedRecyclerAdapter);



        loadMoreData();
//        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
//        itemAnimator.setAddDuration(1000);
//        itemAnimator.setRemoveDuration(1000);
//        newsFeedRecyclerView.setItemAnimator(itemAnimator);

//        newsFeedRecyclerView.addItemDecoration(
//                new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
//        newsFeedRecyclerAdapter.notifyItemChanged(0);


        recyclerViewProgressBar = (ProgressBar) view.findViewById(R.id.recyclerViewProgressBar);
        txtNetworkError = (TextView) view.findViewById(R.id.txtNetworkError);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        final Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);


        //setUpVolley();
        //
//        Query user = databaseReference.orderByChild("")
//        for(int i=0;i<locations.length;i++)
//        {
//            databaseReference.child("Post").child(String.valueOf(i+3)).child("Location").setValue(locations[i]);
//            databaseReference.child("Post").child(String.valueOf(i+3)).child("PostText").setValue("Lorem ipsum dolor sit amet");
//            databaseReference.child("Post").child(String.valueOf(i+3)).child("Timestamp").setValue(1502179255);
//            databaseReference.child("Post").child(String.valueOf(i+3)).child("UserID").setValue(3);
//        }


        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if (vibrator.hasVibrator()) {
                    vibrator.vibrate(30);
                }
                //recyclerViewProgressBar.setVisibility(View.GONE);
                //txtNetworkError.setVisibility(View.GONE);
                //newsFeedRecyclerView.setVisibility(View.VISIBLE);
                Toast.makeText(getActivity(), "Refreshed", Toast.LENGTH_SHORT).show();

                postList.clear();
                newsFeedRecyclerAdapter.notifyDataSetChanged();
                page_num = 0;
                loadMoreData();
                swipeRefreshLayout.setRefreshing(false);
            }
        });


        //loadData();


//        mVolleyService = new VolleyService(mResultCallback, getActivity());
//        mVolleyService.getDataVolley(Request.Method.GET,
//                Constants.protocol + Constants.IP + "/Post");

//        query = databaseReference.child("posts").orderByChild("secondarylocation").equalTo("Saddar Town");
//        query.limitToFirst(TOTAL_ITEM_EACH_LOAD).addListenerForSingleValueEvent(new ValueEventListener() {
//
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//
//                        if(!dataSnapshot.hasChildren()){
//                            Toast.makeText(getActivity(), "No more posts of Town", Toast.LENGTH_SHORT).show();
//                            currentPage--;
//                        }
//                        for (DataSnapshot item : dataSnapshot.getChildren()) {
//                            final PostsPOJO postsPOJO = item.getValue(PostsPOJO.class);
//                            //images = postsPOJO.getcontent_post();
//                            Log.d("dadauserid", postsPOJO.getuserid());
//
//                            oldestPostId = item.getKey();
//                            list.add(new PostsPOJO(currentPage, postsPOJO.getCommentsCount(), postsPOJO.getuserid(), item.getKey(),
//                                    null, null,
//                                    postsPOJO.gettimestamp(), postsPOJO.getposttext(),
//                                    postsPOJO.getlocation(), postsPOJO.getsecondarylocation()
//                                    , postsPOJO.getcontent_post(), postsPOJO.getLikes()));
//                            Log.d("datalist1", list.toString());
//
//                        }
//
//                        Collections.reverse(list);
//
//                        for (final PostsPOJO item : list) {
//                            databaseReference.child("Users").child(item.getuserid()).addListenerForSingleValueEvent(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(DataSnapshot dataSnapshot) {
//                                    String username = dataSnapshot.child("username").getValue().toString();
//                                    if (dataSnapshot.child("profilepicture").getValue() == null) {
//                                        //String profilepicture = "null;
//                                    } else {
//                                        String profilepicture = dataSnapshot.child("profilepicture").getValue().toString();
//                                        list.get(list.indexOf(item)).setprofilepicture(profilepicture);
//                                    }
//                                    list.get(list.indexOf(item)).setusername(username);
//                                    Log.d("datalist2", list.toString());
//                                    newsFeedRecyclerAdapter.notifyDataSetChanged();
////                            newsFeedRecyclerAdapter.notifyItemInserted(1);
////                            newsFeedRecyclerAdapter.notifyItemRangeChanged(1, list.size());
//
//                                }
//
//                                @Override
//                                public void onCancelled(DatabaseError databaseError) {
//
//                                }
//                            });
//                        }
//
//                        recyclerViewProgressBar.setVisibility(View.GONE);
//                        newsFeedRecyclerView.setVisibility(View.VISIBLE);
//
//                        //newsFeedRecyclerAdapter.notifyItemInserted(1);
//                        //newsFeedRecyclerAdapter.notifyItemRangeChanged(1, list.size());
//
//                        isFirstTime = false;//asdasd
//
//
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//                    }
//                });

        newsFeedRecyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                if (!mIsLoading) {
                    loadMoreData();
                }

            }
        });
        //

        //final boolean mIsLoading = false;


        mChildListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                //if (!isFirstTime) {
                final PostsPOJO postsPOJO = dataSnapshot.getValue(PostsPOJO.class);

                //images = postsPOJO.getcontent_post();

                list.add(0, new PostsPOJO(currentPage, postsPOJO.getCommentsCount(), postsPOJO.getuserid(), dataSnapshot.getKey(),
                        postsPOJO.getprofilepicture(), postsPOJO.getusername(),
                        postsPOJO.gettimestamp(), postsPOJO.getposttext(),
                        postsPOJO.getlocation(), postsPOJO.getsecondarylocation()
                        , postsPOJO.getcontent_post(), postsPOJO.getLikes()));

                newsFeedRecyclerAdapter.notifyItemInserted(1);
                newsFeedRecyclerAdapter.notifyItemRangeChanged(1, list.size());
                //}

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {


            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        return view;
    }

    private void loadAds() {
        AdSettings.addTestDevice("d799c50e3b4c9e58d4412f125770179b");
        String placement_id = "2051713708390959_2051806708381659";
        mAds = new NativeAdsManager(getActivity(), placement_id, 5);
        mAds.loadAds();
        NativeAd ad = mAds.nextNativeAd();
        //ad.getAdBody();

        int i=0;
        int a=1;
    }

    private String changePageNumberURL(int page_num) {
        String townParam;
//        URI uri = null;
//        try {
//            uri = new URI(Constants.protocol ,null, Constants.IP, "/TownPosts/5&"+page_num+"&"+ town.replace("_"," "));
//            String request = uri.toASCIIString();
//            Log.d("urlencode",request);
//        } catch (URISyntaxException e) {
//            e.printStackTrace();
//        }

        try {
            townParam = URLEncoder.encode(town, "UTF-8").replaceAll("\\+", "%20");
        } catch (UnsupportedEncodingException e) {
            throw new AssertionError("UTF-8 is unknown");
            // or 'throw new AssertionError("Impossible things are happening today. " +
            //                              "Consider buying a lottery ticket!!");'
        }

        return Constants.protocol + Constants.IP + "/TownPosts/10&" + page_num + "&" + townParam;
    }

    private void setUpVolley() {
        mResultCallback = new IVolleyResult() {
            @Override
            public void notifySuccess(int requestType, JSONObject response) {
                Log.d("volleydadasuccess", "Volley requester " + String.valueOf(requestType));
                Log.d("volleydadasuccess", "Volley JSON GET" + response);
                GsonBuilder gsonBuilder = new GsonBuilder();
                Gson gson = gsonBuilder.create();

            }

            @Override
            public void notifyError(int requestType, VolleyError error) {
                Log.d("volleydadaerror", "Volley requester " + String.valueOf(requestType));
                Log.d("volleydadaerror", "Volley JSON GET" + "That didn't work! " + error.toString());
            }
        };


    }

    private void loadMoreData() {


        URL = changePageNumberURL(page_num);
        page_num++;
        //currentPage++;

        loadData();
    }

    private void loadData() {
        mIsLoading = true;
        StringRequest stringRequest = new StringRequest(URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                mIsLoading = false;
                recyclerViewProgressBar.setVisibility(View.GONE);
                if (response.equals("false")) {
                    //HelperFunctions.getToastShort(getActivity(),"No more data!");
                    Toast.makeText(getActivity(), "No more data!", Toast.LENGTH_SHORT).show();

                } else {

                    GsonBuilder gsonBuilder = new GsonBuilder();
                    Gson gson = gsonBuilder.create();
                    //JSONArray jsonArray = gson.fromJson(response,JSONArray.class);
                    int lastListSize = postList.size();

                    List<Post> list = gson.fromJson(response, new TypeToken<List<Post>>() {
                    }.getType());
                    //postList.addAll(list);
                    //Log.d("volleyposts", postList.toString());

                    for (int i = 0; i < list.size(); i++) {
                        final Post item = list.get(i);
//                        try {
//                            //JSONObject post = (JSONObject) jsonArray.get(i);
//                            item = gson.fromJson(jsonArray.getJSONObject(i), Post.class);
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                postList.add(item);
                                newsFeedRecyclerAdapter.notifyItemInserted(postList.size());
                            }
                        });

                        Log.d("volleyposts", postList.toString());
                    }
//                newsFeedRecyclerAdapter = new NewsFeedRecyclerAdapter(postList);
//                newsFeedRecyclerView.setAdapter(newsFeedRecyclerAdapter);
                    //newsFeedRecyclerAdapter.notifyItemChanged(0);
//                    if(lastListSize == 0) {
                    //newsFeedRecyclerAdapter.notifyDataSetChanged();
//                    }
//                    else {
//                        newsFeedRecyclerAdapter.notifyItemRangeInserted(0, 5);
//
//                    }
                }
                //newsFeedRecyclerAdapter.notifyItemInserted(1);
                //newsFeedRecyclerAdapter.notifyItemRangeChanged(1,postList.size());
                //newsFeedRecyclerAdapter.notifyDataSetChanged();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("VolleyError", error.toString());
            }
        });

        RequestQueue queue = Volley.newRequestQueue(getActivity());
        queue.add(stringRequest);
        stringRequest.setRetryPolicy(new RetryPolicy() {
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
                recyclerViewProgressBar.setVisibility(View.GONE);
            }
        });
    }

    public void clearPostsList() {
        postList.clear();
    }


    @Override
    public void onPause() {
        super.onPause();

        if (getActivity().getSupportFragmentManager().findFragmentByTag("tab_town_news") != null) {
            getActivity().getSupportFragmentManager().findFragmentByTag("tab_town_news").setRetainInstance(true);
            Log.d("onPause123", "Instance found");
        } else {
            Log.d("onPause123", "no Instance found");
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        if (getActivity().getSupportFragmentManager().findFragmentByTag("tab_town_news") != null) {
            getActivity().getSupportFragmentManager().findFragmentByTag("tab_town_news").getRetainInstance();
            Log.d("onResume123", "Instance found");
        } else {
            Log.d("onResume123", "no Instance found");
        }
    }
}
