package com.example.sheryarkhan.projectcity.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.sheryarkhan.projectcity.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class SettingNotificationFragment extends Fragment {


    private Switch toggleBtnTown;
    private Switch toggleBtnPost;
//    private TextView btnSave;


    public SettingNotificationFragment() {
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
        View view = inflater.inflate(R.layout.fragment_setting_notification, container, false);
        toggleBtnPost = (Switch) view.findViewById(R.id.toggleButtonPost);
        toggleBtnTown = (Switch) view.findViewById(R.id.toggleButtonTown);
//        btnSave = (TextView) view.findViewById(R.id.btnSave);

        // toggleBtnTown.setChecked(true);
        // toggleBtnPost.setChecked(true);
        String iid = FirebaseInstanceId.getInstance().getToken();
        Log.d("iidtoken",iid);


        String url = "https://iid.googleapis.com/iid/info/"+iid+"?details=true";

        SharedPreferences sharedPref = getActivity().getSharedPreferences("UserData", Context.MODE_PRIVATE);
        final String town = sharedPref.getString("town", "");
        final String userid = "user_"+ FirebaseAuth.getInstance().getCurrentUser().getUid();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("VolleySuccess",response.toString());
                try {
                    Boolean isTown = response.getJSONObject("rel").getJSONObject("topics").has(town.replace(" ","_"));
                    Boolean isPost = response.getJSONObject("rel").getJSONObject("topics").has(userid);

                    Log.d("isnotitown1",town +" "+ town.replace(" ","_"));
                    Log.d("isnotitown2",isTown.toString());
                    Log.d("isnotipost",isPost.toString());
                    toggleBtnTown.setChecked(isTown);
                    toggleBtnPost.setChecked(isPost);
                   // Toast.makeText(getActivity(),jsonObject.toString(),Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("VolleyError",error.toString());
            }

        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> headers=new HashMap<String,String>();
                headers.put("Content-Type","application/json");
                headers.put("Authorization","key=AAAAQQ9KrNo:APA91bGRet1hbrPzrqoo9FJpF5maAPBlN5tY87pWQ7KvMOYbdf2qVNVw1hXqUhTiK0KtX81nuDVicbUrPSqnWTzbBcIvTtUeZcnhARuDXlUKAZ8xhxQlgXqP45PwqMwvB4guEW37VBWP");
                return headers;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        queue.add(jsonObjectRequest);

        toggleBtnPost.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    //is Enable
                    FirebaseMessaging.getInstance().subscribeToTopic(userid);
                }else {
                    //disable
                    FirebaseMessaging.getInstance().unsubscribeFromTopic(userid);
                }
            }
        });

        toggleBtnTown.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    //is Enable
                    FirebaseMessaging.getInstance().subscribeToTopic(town.replace(" ","_"));
                    Log.d("subtrue","");
                }else {
                    //disable
                    FirebaseMessaging.getInstance().unsubscribeFromTopic(town.replace(" ","_"));
                    Log.d("subtrue","");

                }
            }
        });

        return view;
    }







}
