package com.example.sheryarkhan.projectcity.utils;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sheryar Khan on 10/27/2017.
 */

public class FirebasePushNotificationMethods {
    //private static String username;
    //private static String postText;

    private static DatabaseReference databaseReference;

    private static IVolleyResult mResultCallback;

    private static void setUpVolleyResultCallback() {
        mResultCallback = new IVolleyResult() {
            @Override
            public void notifySuccess(int requestType, JSONObject response) {
                Log.d("volleydadasuccess", "Volley requester " + String.valueOf(requestType));
                Log.d("volleydadasuccess", "Volley JSON post" + response);
            }

            @Override
            public void notifyError(int requestType, VolleyError error) {
                Log.d("volleydadaerror", "Volley requester " + String.valueOf(requestType));
                Log.d("volleydadaerror", "Volley JSON post" + "That didn't work! " + error.toString());
            }
        };
    }

//    private static String getUsernameFromFirebase(String userId) {
//
//        Query query = databaseReference.child("Users").child(userId);
//        query.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for (DataSnapshot item : dataSnapshot.getChildren()) {
//                    username = item.child("username").getValue(String.class);
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//        return username;
//    }
//
//    private static String getPostTextFromFirebase(String postId) {
//
//        Query query = databaseReference.child("posts").child(postId);
//        query.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for (DataSnapshot item : dataSnapshot.getChildren()) {
//                    postText = item.child("posttext").getValue(String.class);
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//        return postText;
//    }

    private static void setUpFirebaseDatabase() {
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    public static void sendTownPostNotification(String userId, String postId, String location, String town, String postText, Context context) {

        setUpVolleyResultCallback();
        setUpFirebaseDatabase();
        //username = getUsernameFromFirebase(userId);
        Map<String, String> townAndMessage = new HashMap<>();
        townAndMessage.put("userId", userId);
        townAndMessage.put("location", location);
        townAndMessage.put("town", town.replace(" ", "_"));
        townAndMessage.put("message", postText);
        townAndMessage.put("postId", postId);
        VolleyService mVolleyService = new VolleyService(mResultCallback, context);
        mVolleyService.postDataVolley(Request.Method.POST,
                Constants.protocol + Constants.IP + Constants.sendTownPostNotification,
                new JSONObject(townAndMessage));
    }


    public static void sendPostLikeNotification(final String toUserId, final String fromUserId,final String postText, final String postId, final Context context) {

        setUpFirebaseDatabase();
        setUpVolleyResultCallback();
        //String toUsername = getUsernameFromFirebase(toUserId);
        Query query = databaseReference.child("Users").child(fromUserId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String fromUsername = dataSnapshot.child("username").getValue(String.class);
                Map<String, String> townAndMessage = new HashMap<>();
                townAndMessage.put("toUserId", toUserId);
                townAndMessage.put("fromUserId", fromUserId);
                townAndMessage.put("fromUsername", fromUsername);
                townAndMessage.put("postText", postText); // CHANGE IT LATER OR REMOVE IT MAYBE??
                townAndMessage.put("postId", postId);
                VolleyService mVolleyService = new VolleyService(mResultCallback, context);
                mVolleyService.postDataVolley(Request.Method.POST,
                        Constants.protocol + Constants.IP + Constants.sendPostLikeNotification,
                        new JSONObject(townAndMessage));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    public static void sendPostCommentNotification(final String toUserId, final String fromUserId, final String postId, final String comment, final Context context) {

        setUpFirebaseDatabase();
        setUpVolleyResultCallback();
        Query query = databaseReference.child("Users").child(fromUserId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String fromUsername = dataSnapshot.child("username").getValue(String.class);
                Map<String, String> townAndMessage = new HashMap<>();
                townAndMessage.put("toUserId", toUserId);
                townAndMessage.put("fromUserId", fromUserId);
                townAndMessage.put("fromUsername", fromUsername);
                townAndMessage.put("comment", comment);
                townAndMessage.put("postId", postId);
                VolleyService mVolleyService = new VolleyService(mResultCallback, context);
                mVolleyService.postDataVolley(Request.Method.POST,
                        Constants.protocol + Constants.IP + Constants.sendPostCommentNotification,
                        new JSONObject(townAndMessage));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    public static void sendPostCommentLikeNotification(String userId, String postId, String location, String town, String postText, Context context) {

        setUpFirebaseDatabase();
        setUpVolleyResultCallback();
        Map<String, String> townAndMessage = new HashMap<>();
        townAndMessage.put("userId", userId);
        townAndMessage.put("location", location);
        townAndMessage.put("town", town.replace(" ", "_"));
        townAndMessage.put("message", postText);
        townAndMessage.put("postId", postId);
        VolleyService mVolleyService = new VolleyService(mResultCallback, context);
        mVolleyService.postDataVolley(Request.Method.POST,
                Constants.protocol + Constants.IP + Constants.sendPostCommentLikeNotification,
                new JSONObject(townAndMessage));
    }


}
