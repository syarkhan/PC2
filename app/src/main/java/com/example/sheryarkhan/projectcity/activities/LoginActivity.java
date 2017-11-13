package com.example.sheryarkhan.projectcity.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.sheryarkhan.projectcity.R;
import com.example.sheryarkhan.projectcity.utils.Constants;
import com.example.sheryarkhan.projectcity.utils.SharedPrefs;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import data.User;

public class LoginActivity extends AppCompatActivity {

    private EditText email;
    private EditText password;

    private FirebaseAuth firebaseAuth;

    private ProgressDialog progressDialog;
    private SharedPrefs sharedPrefs;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = (EditText) findViewById(R.id.txtEmail);
        password = (EditText) findViewById(R.id.txtPassword);

        firebaseAuth = FirebaseAuth.getInstance();
        sharedPrefs = new SharedPrefs(this);


        //if(firebaseAuth.getCurrentUser() != null){
        if(!sharedPrefs.getUserIdFromSharedPref().equals("")) {
            FirebaseMessaging.getInstance().subscribeToTopic("user_"+sharedPrefs.getUserIdFromSharedPref());
            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    public  void BtnRegisterIntentOnClick(View view){
        Intent intent = new Intent(getApplicationContext(),RegisterActivity.class);
        startActivity(intent);
    }

    public void BtnLoginOnClick(View view){

        String strEmail =  email.getText().toString().trim();
        String strPassword = password.getText().toString().trim();

        if(TextUtils.isEmpty(strEmail)){
            Toast.makeText(getApplicationContext(),"Please Enter Email",Toast.LENGTH_LONG).show();
            return;
        }

        if(TextUtils.isEmpty(strPassword)){
            Toast.makeText(getApplicationContext(),"Please Enter Password",Toast.LENGTH_LONG).show();
            return;
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Signing In");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(strEmail,strPassword).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){
                    //if(!sharedPrefs.checkIfSharedPrefsFileExists()) {
                        String URL = Constants.protocol + Constants.IP + Constants.getUserDetails+"/"+firebaseAuth.getCurrentUser().getUid();
                        Log.d("url",URL);
                        //Get userid, username and town from Mongodb database
                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Boolean isSuccess=false;
                                GsonBuilder gsonBuilder = new GsonBuilder();
                                Gson gson = gsonBuilder.create();
                                try {
                                    isSuccess = response.getBoolean("success");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                if(isSuccess){
                                    try {
                                        User user = gson.fromJson(String.valueOf(response.getJSONObject("User")), User.class);
                                        sharedPrefs.setUserIdToSharedPref(user.getUserId());
                                        sharedPrefs.setUsernameToSharedPref(user.getUsername());
                                        sharedPrefs.setTownToSharedPref(user.getTown());
                                        sharedPrefs.setProfilePictureFromSharedPref("");
                                        Log.d("userdata", user.toString());
                                        progressDialog.dismiss();
                                        finish();
                                        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                                        startActivity(intent);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d("VolleyError", error.toString());
                            }
                        });
                        RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
                        queue.add(jsonObjectRequest);
                    //}

                }else {
                    progressDialog.dismiss();
                    try {
                        throw task.getException();
                    }  catch (FirebaseAuthInvalidCredentialsException e) {
                        Toast.makeText(getApplicationContext(), "please enter valid information", Toast.LENGTH_LONG).show();

                    }  catch (FirebaseAuthInvalidUserException e) {
                        Toast.makeText(getApplicationContext(), "User not found", Toast.LENGTH_LONG).show();

                    }catch (FirebaseNetworkException e) {
                        Toast.makeText(getApplicationContext(), "Network Error", Toast.LENGTH_LONG).show();

                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "Server Error", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });



    }

}
