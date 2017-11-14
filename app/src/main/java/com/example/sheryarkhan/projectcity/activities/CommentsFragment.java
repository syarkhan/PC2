package com.example.sheryarkhan.projectcity.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.sheryarkhan.projectcity.R;
import com.example.sheryarkhan.projectcity.utils.Constants;
import com.example.sheryarkhan.projectcity.utils.EndlessRecyclerOnScrollListener;
import com.example.sheryarkhan.projectcity.utils.FirebasePushNotificationMethods;
import com.example.sheryarkhan.projectcity.utils.HelperFunctions;
import com.example.sheryarkhan.projectcity.adapters.CommentsListAdapter;
import com.example.sheryarkhan.projectcity.utils.SharedPrefs;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import data.Comment;
import data.Post;
import data.UserInfo;

public class CommentsFragment extends Fragment {
    private ImageButton backButton;
    private RecyclerView commentsRecyclerView;
    private CommentsListAdapter commentsListAdapter;
    private ImageButton sendMessageBtn;
    private EditText editTextComment;

    private LinearLayoutManager linearLayoutManager;
    private ArrayList<Comment> commentsList;

    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private Query query;

    private String postId;
    private SharedPrefs sharedPrefs;
    private String URL;
    private int page_num;
    private boolean mIsLoading = false;


    public CommentsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        page_num=0;
        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        sharedPrefs = new SharedPrefs(getActivity());

        commentsList = new ArrayList<>();


        postId = getPostIdFromBundle();

        URL = changePageNumberURL(page_num);
        query = databaseReference.child("postcomments").child(postId).child("comments");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_comments, container, false);

        commentsRecyclerView = (RecyclerView) view.findViewById(R.id.commentsRecyclerView);
        commentsRecyclerView.setHasFixedSize(true);
        commentsRecyclerView.setItemViewCacheSize(20);
        commentsRecyclerView.setDrawingCacheEnabled(true);
        commentsRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        commentsRecyclerView.setLayoutManager(linearLayoutManager);
        commentsListAdapter = new CommentsListAdapter(commentsList);
        commentsRecyclerView.setAdapter(commentsListAdapter);

        commentsRecyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                if(!mIsLoading) {
                    loadMoreData();
                }

            }
        });

        editTextComment = (EditText) view.findViewById(R.id.editTextComment);

        backButton = (ImageButton) view.findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getCallingActivityFromBundle().equals(getString(R.string.main_activity))) {
                    getActivity().getSupportFragmentManager().popBackStack();
                    ((MainActivity) getActivity()).showLayout();
                } else {
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            }
        });

        loadMoreData();

        sendMessageBtn = (ImageButton) view.findViewById(R.id.sendMessageBtn);
        sendMessageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Send message to firebase

                Toast.makeText(getActivity(), "Posting Comment", Toast.LENGTH_SHORT).show();
                final String userid = sharedPrefs.getUserIdFromSharedPref();
                final String username = sharedPrefs.getUsernameFromSharedPref();
                final String profilepic = sharedPrefs.getProfilePictureFromSharedPref();
                final String likeAddOrRemoveURL = Constants.protocol + Constants.IP +
                        Constants.addNewPostComment;

                final String commentText = editTextComment.getText().toString();
                Long timestamp = System.currentTimeMillis();
                //Comment.UserInfo userInfo = new Comment.UserInfo()
                Comment comment = new Comment(null, postId, commentText, timestamp, userid, 0, null, new UserInfo(username, profilepic));
                commentsList.add(0, comment);
                commentsListAdapter.notifyItemInserted(0);
                commentsRecyclerView.smoothScrollToPosition(0);


                //Comment comment = new Comment(postId, commentText, timestamp.toString(), userid, 0,null);
                GsonBuilder gsonBuilder = new GsonBuilder();
                Gson gson = gsonBuilder.create();
                //String jsonObject = gson.toJson(comment);

                Map<String, Object> PostCommentData = new HashMap<>();
                PostCommentData.put("UserId", userid);
                PostCommentData.put("PostId", postId);
                PostCommentData.put("CommentText", commentText);
                PostCommentData.put("timestamp", timestamp.toString());
                PostCommentData.put("LikesCount", 0);

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, likeAddOrRemoveURL, new JSONObject(PostCommentData), new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("volleyadd", response.toString());
                        Toast.makeText(getActivity(), "Posting Done", Toast.LENGTH_SHORT).show();
                        //FirebasePushNotificationMethods.sendTownPostNotification(userid, key, txtPrimary, txtSecondary, editTextShareNews, MainActivity.this);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("VolleyError", error.toString());
                    }
                });


                RequestQueue queue = Volley.newRequestQueue(getActivity());
                queue.add(jsonObjectRequest);


                //final String userid = firebaseAuth.getCurrentUser().getUid();
//                String commentid = databaseReference.child("postcomments").child(postId).child("comments").push().getKey();
//
//                final String toUserId = getUserIdOfPostFromBundle();
//                //Long timestamp = HelperFunctions.getCurrentTimestamp();
//                //Comment comment = new Comment(commentText, null, 0, timestamp, userid);
//                Map<String, Object> childUpdates = new HashMap<>();
//                childUpdates.put("/postcomments/" + postId + "/comments/" + commentid, comment);
//                databaseReference.updateChildren(childUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        HelperFunctions.getToastShort(getActivity(), "comment posted");
//                        FirebasePushNotificationMethods.sendPostCommentNotification(toUserId, userid, postId, commentText, getContext());
//                    }
//                });
            }
        });

        //final Comment comment = new Comment();
//        query.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for (DataSnapshot item : dataSnapshot.getChildren()) {
//                    final Comment comment = item.getValue(Comment.class);
//                    comment.setCommentid(item.getKey());
//                    Log.d("commentdata", comment.toString());
//                    commentsList.add(comment);
////                    comment.setCommentid(item.getKey());
////                    comment.setCommenttext(item.child("commenttext").toString());
////                    //comment.setLikes(item.child("likes"));
////                    comment.setLikesCount(2);
////                    comment.setTimestamp(Long.valueOf(item.child("timestamp")));
//
//                    //final Comment comment = item.getValue(Comment.class);
//                }
//                commentsListAdapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });



        return view;
    }

    private String getCallingActivityFromBundle() {
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            return bundle.getString(getString(R.string.main_activity));
        } else {
            return null;
        }

    }

    private String changePageNumberURL(int page_num){
        //String townParam;
//        URI uri = null;
//        try {
//            uri = new URI(Constants.protocol ,null, Constants.IP, "/TownPosts/5&"+page_num+"&"+ town.replace("_"," "));
//            String request = uri.toASCIIString();
//            Log.d("urlencode",request);
//        } catch (URISyntaxException e) {
//            e.printStackTrace();
//        }

//        try {
//            townParam = URLEncoder.encode(town,"UTF-8").replaceAll("\\+", "%20");
//        } catch (UnsupportedEncodingException e) {
//            throw new AssertionError("UTF-8 is unknown");
//            // or 'throw new AssertionError("Impossible things are happening today. " +
//            //                              "Consider buying a lottery ticket!!");'
//        }

        return Constants.protocol + Constants.IP + Constants.getPostComments+"/10&"+page_num+"&"+postId;
    }

    private void loadMoreData() {


        URL = changePageNumberURL(page_num);
        Log.d("commenturl",URL);
        page_num++;
        //currentPage++;

        loadData();
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

                    List<Comment> list = gson.fromJson(response, new TypeToken<List<Comment>>() {
                    }.getType());
                    //postList.addAll(list);
                    //Log.d("volleyposts", postList.toString());

                    for (int i = 0; i < list.size(); i++) {
                        Comment item = list.get(i);
//                        try {
//                            //JSONObject post = (JSONObject) jsonArray.get(i);
//                            item = gson.fromJson(jsonArray.getJSONObject(i), Post.class);
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
                        commentsList.add(item);
                        commentsListAdapter.notifyItemInserted(commentsList.size());
                        Log.d("volleyposts", commentsList.toString());
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

    private String getTotalLikesFromBundle() {
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            return bundle.getString(getString(R.string.likes));
        } else {
            return null;
        }

    }

    private String getUserIdOfPostFromBundle() {
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            return bundle.getString(getString(R.string.userIdPost));
        } else {
            return null;
        }

    }

    private String getTotalCommentsFromBundle() {
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            return bundle.getString(getString(R.string.comments));
        } else {
            return null;
        }

    }

    private String getPostIdFromBundle() {
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            return bundle.getString(getString(R.string.comment_post_id));
        } else {
            return null;
        }

    }

}
