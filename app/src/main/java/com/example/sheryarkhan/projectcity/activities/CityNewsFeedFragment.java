package com.example.sheryarkhan.projectcity.activities;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
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

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.sheryarkhan.projectcity.R;
import com.example.sheryarkhan.projectcity.utils.Constants;
import com.example.sheryarkhan.projectcity.utils.EndlessRecyclerOnScrollListener;
import com.example.sheryarkhan.projectcity.utils.IVolleyResult;
import com.example.sheryarkhan.projectcity.utils.SharedPrefs;
import com.example.sheryarkhan.projectcity.utils.VolleyService;
import com.example.sheryarkhan.projectcity.adapters.CityNewsFeedRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import data.Post;
import data.PostsPOJO;

public class CityNewsFeedFragment extends Fragment {

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView cityNewsFeedRecyclerView;
    private CityNewsFeedRecyclerAdapter cityNewsFeedRecyclerAdapter;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private ChildEventListener mChildListener;
    //private ProgressBar recyclerViewProgressBar;
    private TextView txtNetworkError;

    //POST UPLOAD RELATED
    private Map<Integer, ArrayList<String>> hashMap = Collections.emptyMap();
    private boolean isLastItem = false;
    private static Integer number_of_posts = 0;
    private Map<String, Boolean> media = new HashMap<>();
    private IVolleyResult mResultCallback;
    private VolleyService mVolleyService;

    private LinearLayoutManager linearLayoutManager;

    private static final int ACTIVITY_NUM = 0;
    //private ArrayList<PostsPOJO> list;
    private String oldestPostId;
    private SharedPrefs sharedPrefs;
    private int page_num;
    private String URL;
    private List<Object> postList;
    private String town;
    private boolean mIsLoading = false;

    boolean isFirstTime = true;
    int counter = 1;
    Query query;
    View view;
    private Context context;

    public CityNewsFeedFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        page_num = 0;
        context = getActivity();
        sharedPrefs = new SharedPrefs(context);
        town = sharedPrefs.getTownFromSharedPref();
        URL = changePageNumberURL(page_num);
        String iid = FirebaseInstanceId.getInstance().getToken();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (savedInstanceState != null) {
            Log.d("instance", savedInstanceState.toString());
        }

        if (view != null) {
            if (view.getParent() != null)
                ((ViewGroup) view.getParent()).removeView(view);
            return view;

        }

        view = inflater.inflate(R.layout.fragment_news_feed, container, false);


        postList = new ArrayList<>();

        cityNewsFeedRecyclerView = (RecyclerView) view.findViewById(R.id.news_feed_recyclerview);

        cityNewsFeedRecyclerView.setHasFixedSize(true);
        cityNewsFeedRecyclerView.setItemViewCacheSize(20);
        cityNewsFeedRecyclerView.setDrawingCacheEnabled(true);
        cityNewsFeedRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        linearLayoutManager = new LinearLayoutManager(context);
        cityNewsFeedRecyclerView.setLayoutManager(linearLayoutManager);
        //loadAds();
        cityNewsFeedRecyclerAdapter = new CityNewsFeedRecyclerAdapter(postList, context);
        cityNewsFeedRecyclerView.setAdapter(cityNewsFeedRecyclerAdapter);

        postList.add("Search");
        cityNewsFeedRecyclerAdapter.notifyItemChanged(0);


        loadMoreData();

        txtNetworkError = (TextView) view.findViewById(R.id.txtNetworkError);
        final Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if (vibrator.hasVibrator()) {
                    vibrator.vibrate(30);
                }
                postList.subList(1, postList.size()).clear();
                cityNewsFeedRecyclerAdapter.notifyDataSetChanged();
                //recyclerViewProgressBar.setVisibility(View.GONE);
                //txtNetworkError.setVisibility(View.GONE);
                //newsFeedRecyclerView.setVisibility(View.VISIBLE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        swipeRefreshLayout.setRefreshing(false);
                        page_num = 0;
                        loadMoreData();

                    }
                }, 2000);

            }
        });

        cityNewsFeedRecyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                Log.d("scrolled1", "scrolled1");
                if (!mIsLoading) {
                    loadMoreData();
                }
            }
        });

        return view;
    }


    private String changePageNumberURL(int page_num) {
        String townParam;

        try {
            townParam = URLEncoder.encode(town, "UTF-8").replaceAll("\\+", "%20");
        } catch (UnsupportedEncodingException e) {
            throw new AssertionError("UTF-8 is unknown");
            // or 'throw new AssertionError("Impossible things are happening today. " +
            //                              "Consider buying a lottery ticket!!");'
        }

        return Constants.protocol + Constants.IP + Constants.getCityPosts + "/10&" + page_num + "&" + townParam;
    }

    private void loadMoreData() {


        mIsLoading = true;
        if (mIsLoading) {
            cityNewsFeedRecyclerAdapter.showLoading();
        }
        URL = changePageNumberURL(page_num);
        Log.d("URL", URL);
        page_num++;
        //currentPage++;

        loadData();

        //loadData1();
    }

    private void loadData() {

        StringRequest stringRequest = new StringRequest(URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                mIsLoading = false;

                ((Activity)context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        cityNewsFeedRecyclerAdapter.dismissLoading();
                        //recyclerViewProgressBar.setVisibility(View.GONE);
                    }
                });

                if (response.equals("false")) {
                    //HelperFunctions.getToastShort(getActivity(),"No more data!");
                    Toast.makeText(context, "No more data!", Toast.LENGTH_SHORT).show();

                } else {

                    GsonBuilder gsonBuilder = new GsonBuilder();
                    Gson gson = gsonBuilder.create();
                    //JSONArray jsonArray = gson.fromJson(response,JSONArray.class);
                    int lastListSize = postList.size();

                    List<Post> list = gson.fromJson(response, new TypeToken<List<Post>>() {
                    }.getType());
                    //postList.addAll(list);
                    //Log.d("volleyposts", postList.toString());

                    //List<Object> posts = new ArrayList<>();
                    for (int i = 0; i < list.size(); i++) {
                        final Post item = list.get(i);

                        postList.add(item);
                        ((Activity)context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                cityNewsFeedRecyclerAdapter.notifyItemInserted(postList.size());
                            }
                        });

                        Log.d("volleyposts", postList.toString());
                    }

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("VolleyError", error.toString());
                ((Activity)context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //recyclerViewProgressBar.setVisibility(View.GONE);
                        cityNewsFeedRecyclerAdapter.dismissLoading();
                    }
                });
            }
        });

        RequestQueue queue = Volley.newRequestQueue(context);

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(stringRequest);
//        stringRequest.setRetryPolicy(new RetryPolicy() {
//            @Override
//            public int getCurrentTimeout() {
//                return 50000;
//            }
//
//            @Override
//            public int getCurrentRetryCount() {
//                return 50000;
//            }
//
//            @Override
//            public void retry(VolleyError error) throws VolleyError {
//                Log.d("VolleyRetryError", error.toString());
////                getActivity().runOnUiThread(new Runnable() {
////                    @Override
////                    public void run() {
////                        recyclerViewProgressBar.setVisibility(View.GONE);
////                    }
////                });
//
//            }
//        });
    }

}
