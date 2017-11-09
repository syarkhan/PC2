package com.example.sheryarkhan.projectcity.activities;

import android.content.Context;
import android.os.Bundle;
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

import com.example.sheryarkhan.projectcity.R;
import com.example.sheryarkhan.projectcity.utils.Constants;
import com.example.sheryarkhan.projectcity.utils.IVolleyResult;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import data.PostsPOJO;

public class CityNewsFeedFragment extends Fragment {

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView cityNewsFeedRecyclerView;
    private CityNewsFeedRecyclerAdapter cityNewsFeedRecyclerAdapter;
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

    private LinearLayoutManager linearLayoutManager;

    private static final int ACTIVITY_NUM = 0;
    private ArrayList<PostsPOJO> list;
    private String oldestPostId;

    boolean isFirstTime = true;
    int counter = 1;
    Query query;
    View view;

    public CityNewsFeedFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        final List<Map<String, Object>> TOWN = Constants.TOWN_DETAILS();
        Log.d("dadaTowns", TOWN.toString());

        //setupBottomNavigationView(view);
        recyclerViewProgressBar = (ProgressBar) view.findViewById(R.id.recyclerViewProgressBar);
        txtNetworkError = (TextView) view.findViewById(R.id.txtNetworkError);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        final Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        //
//        Query user = databaseReference.orderByChild("")
//        for(int i=0;i<locations.length;i++)
//        {
//            databaseReference.child("Posts").child(String.valueOf(i+3)).child("Location").setValue(locations[i]);
//            databaseReference.child("Posts").child(String.valueOf(i+3)).child("PostText").setValue("Lorem ipsum dolor sit amet");
//            databaseReference.child("Posts").child(String.valueOf(i+3)).child("Timestamp").setValue(1502179255);
//            databaseReference.child("Posts").child(String.valueOf(i+3)).child("UserID").setValue(3);
//        }

        list = new ArrayList<>();

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                //cityNewsFeedRecyclerView.getRecycledViewPool().clear();
//                list.clear();
//                cityNewsFeedRecyclerAdapter.notifyDataSetChanged();

                if (vibrator.hasVibrator()) {
                    vibrator.vibrate(30);
                }
                //newsFeedRecyclerAdapter.notifyDataSetChanged();
//
//                query.limitToLast(15).addListenerForSingleValueEvent(new ValueEventListener() {
//
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//
//
//                        for (DataSnapshot item : dataSnapshot.getChildren()) {
//                            final PostsPOJO postsPOJO = item.getValue(PostsPOJO.class);
//                            //images = postsPOJO.getcontent_post();
//                            Log.d("dadauserid", postsPOJO.getuserid());
//
//                            oldestPostId = item.getKey();
//                            list.add(new PostsPOJO(postsPOJO.getCommentsCount(),postsPOJO.getuserid(), item.getKey(),
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
//                                    cityNewsFeedRecyclerAdapter.notifyDataSetChanged();
////                                    cityNewsFeedRecyclerAdapter.notifyItemInserted(1);
////                                    cityNewsFeedRecyclerAdapter.notifyItemRangeChanged(1, list.size());
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
//                        cityNewsFeedRecyclerView.setVisibility(View.VISIBLE);
//
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
                recyclerViewProgressBar.setVisibility(View.GONE);
                txtNetworkError.setVisibility(View.GONE);
                cityNewsFeedRecyclerView.setVisibility(View.VISIBLE);
                Toast.makeText(getActivity(), "Refreshed", Toast.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        cityNewsFeedRecyclerView = (RecyclerView) view.findViewById(R.id.news_feed_recyclerview);
        //feedItemsList = new ArrayList<>();

        //newsFeedRecyclerAdapter = new NewsFeedRecyclerAdapter(this,generateDummyData(list));
        //newsFeedRecyclerView.setAdapter(newsFeedRecyclerAdapter);


        cityNewsFeedRecyclerView.setHasFixedSize(true);
        cityNewsFeedRecyclerView.setItemViewCacheSize(20);
        cityNewsFeedRecyclerView.setDrawingCacheEnabled(true);
        cityNewsFeedRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        cityNewsFeedRecyclerView.setLayoutManager(linearLayoutManager);
        cityNewsFeedRecyclerAdapter = new CityNewsFeedRecyclerAdapter(list);
        cityNewsFeedRecyclerView.setAdapter(cityNewsFeedRecyclerAdapter);
        cityNewsFeedRecyclerAdapter.notifyItemChanged(0);


        query = databaseReference.child("posts").orderByChild("timestamp");
        //query1 = databaseReference.child("posts").orderByChild("timestamp").limitToLast(1);


//        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
//        connectedRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot snapshot) {
//                boolean connected = snapshot.getValue(Boolean.class);
//                if (connected) {
//                    System.out.println("connected");
//
//                } else {
//                    System.out.println("not connected");
//                    Toast.makeText(NewsFeedActivity.this,"No Internet Connection!",Toast.LENGTH_SHORT).show();
//                    recyclerViewProgressBar.setVisibility(View.GONE);
//                    txtNetworkError.setVisibility(View.VISIBLE);
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError error) {
//                System.err.println("Listener was cancelled");
//            }
//        });


        query.limitToLast(40).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                for (DataSnapshot item : dataSnapshot.getChildren()) {
                    final PostsPOJO postsPOJO = item.getValue(PostsPOJO.class);
                    //images = postsPOJO.getcontent_post();
                    Log.d("dadauserid", postsPOJO.getuserid());
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

                    oldestPostId = item.getKey();
                    list.add(new PostsPOJO(1,postsPOJO.getCommentsCount(), postsPOJO.getuserid(), item.getKey(),
                            null, null,
                            postsPOJO.gettimestamp(), postsPOJO.getposttext(),
                            postsPOJO.getlocation(), postsPOJO.getsecondarylocation()
                            , postsPOJO.getcontent_post(), postsPOJO.getLikes()));
                    Log.d("datalist1", list.toString());

                }

                Collections.reverse(list);

                for (final PostsPOJO item : list) {
                    databaseReference.child("Users").child(item.getuserid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String username = dataSnapshot.child("username").getValue().toString();
                            if (dataSnapshot.child("profilepicture").getValue() == null) {
                                //String profilepicture = "null;
                            } else {
                                String profilepicture = dataSnapshot.child("profilepicture").getValue().toString();
                                list.get(list.indexOf(item)).setprofilepicture(profilepicture);
                            }
                            list.get(list.indexOf(item)).setusername(username);
                            Log.d("datalist2", list.toString());

                            cityNewsFeedRecyclerAdapter.notifyDataSetChanged();
//                            cityNewsFeedRecyclerAdapter.notifyItemInserted(1);
//                            cityNewsFeedRecyclerAdapter.notifyItemRangeChanged(1, list.size());

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }

                recyclerViewProgressBar.setVisibility(View.GONE);
                cityNewsFeedRecyclerView.setVisibility(View.VISIBLE);

                //newsFeedRecyclerAdapter.notifyItemInserted(1);
                //newsFeedRecyclerAdapter.notifyItemRangeChanged(1, list.size());

                isFirstTime = false;//asdasd


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        final boolean mIsLoading = false;
        cityNewsFeedRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            private int currentVisibleItemCount;
            private int currentScrollState;
            private int currentFirstVisibleItem;
            private int totalItem;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                this.currentScrollState = newState;
                //this.isScrollCompleted();
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (mIsLoading)
                    return;

                int visibleItemCount = linearLayoutManager.getChildCount();
                int totalItemCount = linearLayoutManager.getItemCount();
                int pastVisibleItems = linearLayoutManager.findFirstVisibleItemPosition();
                if (pastVisibleItems + visibleItemCount >= totalItemCount) {
                    //End of list
                    //Toast.makeText(getApplicationContext(),"end of list",Toast.LENGTH_SHORT).show();
                }
            }
        });


        mChildListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                //if (!isFirstTime) {
                final PostsPOJO postsPOJO = dataSnapshot.getValue(PostsPOJO.class);
                //images = postsPOJO.getcontent_post();

                list.add(0, new PostsPOJO(1,postsPOJO.getCommentsCount(),postsPOJO.getuserid(), dataSnapshot.getKey(),
                        postsPOJO.getprofilepicture(), postsPOJO.getusername(),
                        postsPOJO.gettimestamp(), postsPOJO.getposttext(),
                        postsPOJO.getlocation(), postsPOJO.getsecondarylocation()
                        , postsPOJO.getcontent_post(), postsPOJO.getLikes()));

                cityNewsFeedRecyclerAdapter.notifyItemInserted(1);
                cityNewsFeedRecyclerAdapter.notifyItemRangeChanged(1, list.size());
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

}
