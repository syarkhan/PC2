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

import com.example.sheryarkhan.projectcity.R;
import com.example.sheryarkhan.projectcity.utils.FirebasePushNotificationMethods;
import com.example.sheryarkhan.projectcity.utils.HelperFunctions;
import com.example.sheryarkhan.projectcity.adapters.CommentsListAdapter;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import data.Comment;

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



    public CommentsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();

        commentsList = new ArrayList<>();



        postId = getPostIdFromBundle();


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

        editTextComment = (EditText)view.findViewById(R.id.editTextComment);

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

        sendMessageBtn = (ImageButton) view.findViewById(R.id.sendMessageBtn);
        sendMessageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Send message to firebase

                final String commentText = editTextComment.getText().toString();
                final String userid = firebaseAuth.getCurrentUser().getUid();
                String commentid = databaseReference.child("postcomments").child(postId).child("comments").push().getKey();

                final String toUserId = getUserIdOfPostFromBundle();
                Long timestamp = HelperFunctions.getCurrentTimestamp();
                Comment comment = new Comment(commentText,null,0,timestamp,userid);
                Map<String, Object> childUpdates = new HashMap<>();
                childUpdates.put("/postcomments/" +postId+ "/comments/" + commentid, comment);
                databaseReference.updateChildren(childUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                            HelperFunctions.getToastShort(getActivity(),"comment posted");
                        FirebasePushNotificationMethods.sendPostCommentNotification(toUserId, userid, postId, commentText,getContext());
                    }
                });
            }
        });

        //final Comment comment = new Comment();
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot item : dataSnapshot.getChildren())
                {
                    final Comment comment = item.getValue(Comment.class);
                    comment.setCommentid(item.getKey());
                    Log.d("commentdata",comment.toString());
                    commentsList.add(comment);
//                    comment.setCommentid(item.getKey());
//                    comment.setCommenttext(item.child("commenttext").toString());
//                    //comment.setLikes(item.child("likes"));
//                    comment.setLikesCount(2);
//                    comment.setTimestamp(Long.valueOf(item.child("timestamp")));

                    //final Comment comment = item.getValue(Comment.class);
                }
                commentsListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

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
