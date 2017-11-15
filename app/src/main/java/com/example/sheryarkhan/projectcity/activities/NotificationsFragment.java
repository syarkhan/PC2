package com.example.sheryarkhan.projectcity.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.sheryarkhan.projectcity.R;
import com.example.sheryarkhan.projectcity.adapters.CommentsListAdapter;
import com.example.sheryarkhan.projectcity.adapters.NotificationsRecyclerAdapter;
import com.example.sheryarkhan.projectcity.utils.Constants;
import com.example.sheryarkhan.projectcity.utils.EndlessRecyclerOnScrollListener;
import com.example.sheryarkhan.projectcity.utils.SharedPrefs;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import data.Comment;
import data.Notification;


public class NotificationsFragment extends Fragment {

    private ArrayList<Notification> notificationsList;
    private String URL;
    private int page_num;
    private boolean mIsLoading = false;
    private SharedPrefs sharedPrefs;
    private NotificationsRecyclerAdapter notificationsRecyclerAdapter;
    private RecyclerView notificationsRecyclerView;
    private LinearLayoutManager linearLayoutManager;
    public NotificationsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        page_num=0;
        notificationsList = new ArrayList<>();
        sharedPrefs = new SharedPrefs(getActivity());
        URL = changePageNumberURL(page_num);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_notifications, container, false);

        notificationsRecyclerView = (RecyclerView) view.findViewById(R.id.notificationsRecyclerView);
        notificationsRecyclerView.setHasFixedSize(true);
        notificationsRecyclerView.setItemViewCacheSize(20);
        notificationsRecyclerView.setDrawingCacheEnabled(true);
        notificationsRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        notificationsRecyclerView.setLayoutManager(linearLayoutManager);
        notificationsRecyclerAdapter = new NotificationsRecyclerAdapter(getActivity(), notificationsList);
        notificationsRecyclerView.setAdapter(notificationsRecyclerAdapter);

        loadMoreData();


        notificationsRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Log.d("itemclicked1","ad");
                final String postid = notificationsRecyclerAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), NotificationPostActivity.class);
                intent.putExtra("postId",postid);
                intent.putExtra("userId",sharedPrefs.getUserIdFromSharedPref());
                getActivity().startActivity(intent);
            }
        }));

        notificationsRecyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                if(!mIsLoading) {
                    loadMoreData();
                }

            }
        });

        return view;
    }

    private void loadMoreData() {
        URL = changePageNumberURL(page_num);
        Log.d("commenturl",URL);
        page_num++;
        //currentPage++;

        loadData();
    }

    private String changePageNumberURL(int page_num){
        String townParam;
        try {
            townParam = URLEncoder.encode(sharedPrefs.getTownFromSharedPref(), "UTF-8").replaceAll("\\+", "%20");
        } catch (UnsupportedEncodingException e) {
            throw new AssertionError("UTF-8 is unknown");
            // or 'throw new AssertionError("Impossible things are happening today. " +
            //                              "Consider buying a lottery ticket!!");'
        }
        return Constants.protocol + Constants.IP + Constants.getNotifications+"/10&"+page_num+"&"
                +sharedPrefs.getUserIdFromSharedPref()+"&"+townParam;
    }

    private void loadData() {
        mIsLoading = true;
        //URL = Constants.protocol + Constants.IP + Constants.getPostComments + "/" + postId;
        StringRequest stringRequest = new StringRequest(URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                mIsLoading = false;
                //recyclerViewProgressBar.setVisibility(View.GONE);
                if (response.equals("false")) {
                    //HelperFunctions.getToastShort(getActivity(),"No more data!");
                    Toast.makeText(getActivity(), "No more data!", Toast.LENGTH_SHORT).show();
                } else {

                    GsonBuilder gsonBuilder = new GsonBuilder();
                    Gson gson = gsonBuilder.create();
                    //JSONArray jsonArray = gson.fromJson(response,JSONArray.class);
                    //int lastListSize = postList.size();

                    List<Notification> list = gson.fromJson(response, new TypeToken<List<Notification>>() {
                    }.getType());

                    for (int i = 0; i < list.size(); i++) {
                        Notification item = list.get(i);
                        notificationsList.add(item);
                        notificationsRecyclerAdapter.notifyItemInserted(notificationsList.size());
                        Log.d("volleyposts", notificationsList.toString());
                    }
                }
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
                //recyclerViewProgressBar.setVisibility(View.GONE);
            }
        });
    }



}
