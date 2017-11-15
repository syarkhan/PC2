package com.example.sheryarkhan.projectcity.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.sheryarkhan.projectcity.R;

import data.UserClass;

import com.example.sheryarkhan.projectcity.utils.Constants;
import com.example.sheryarkhan.projectcity.utils.FirebasePushNotificationMethods;
import com.example.sheryarkhan.projectcity.utils.SharedPrefs;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {


//adas
    private EditText txtUsername;
    private EditText txtEmail;
    private EditText txtPassword;
    private EditText txtConfirmPassword;
    private EditText txtTown;
    private String Town;

    private Button btnRegister;


    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    public String TAG = "tag";

    private ProgressDialog progressDialog;
    private SharedPrefs sharedPrefs;
    private static final int REQUEST_OPEN_RESULT_CODE_FOR_TOWN = 10;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        sharedPrefs = new SharedPrefs(this);

        txtUsername = (EditText) findViewById(R.id.txtName);
        txtEmail = (EditText) findViewById(R.id.txtEmail);
        txtPassword = (EditText) findViewById(R.id.txtPass);
        txtConfirmPassword = (EditText) findViewById(R.id.txtConfirmPass);
        txtTown = (EditText) findViewById(R.id.txtTown);

        btnRegister = (Button)findViewById(R.id.btnRegister);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        sharedPrefs = new SharedPrefs(this);

        txtTown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, TownAutoCompleteActivity.class);
                startActivityForResult(intent, REQUEST_OPEN_RESULT_CODE_FOR_TOWN);
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerNewUser();
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if (requestCode == REQUEST_OPEN_RESULT_CODE_FOR_TOWN) {
            if (resultData != null && resultCode == RESULT_OK) {
                Town = resultData.getStringExtra("Town");
                txtTown.setText(Town);
            } else if (resultCode == RESULT_CANCELED) {

            }
        }
    }

    public void BtnLogInIntentOnClick(View view) {

        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish();

    }

    public void registerNewUser() {

        final String username = txtUsername.getText().toString().trim();

        final String email = txtEmail.getText().toString().trim();
        String password = txtPassword.getText().toString().trim();
        String confirmPassword = txtConfirmPassword.getText().toString().trim();


        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Please Enter Email", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Please Enter Password", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(username)) {
            Toast.makeText(getApplicationContext(), "Please Enter Password", Toast.LENGTH_LONG).show();
            return;
        }
        if(Town.equals("")){
            Toast.makeText(getApplicationContext(), "Please Select Town", Toast.LENGTH_LONG).show();
            return;
        }

        boolean resultOfComparison = password.equals(confirmPassword);

        if (!resultOfComparison) {
            Toast.makeText(getApplicationContext(), "Your Password and Confirm Password does not match", Toast.LENGTH_LONG).show();
            return;
        }


        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Registering new user");
        progressDialog.show();


        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Long DateOfJoining = System.currentTimeMillis();
                    final FirebaseUser user = firebaseAuth.getCurrentUser();

                    //UserClass userClassObj = new UserClass(currentTimeStamp, "", "", true, "", "", "", null, username, email);
                    String URL = Constants.protocol + Constants.IP + Constants.registerNewUser;

                    //TODO : PUT TOWN ALSO IN REGISTRATION
                    Map<String, Object> UserData = new HashMap<>();

                    UserData.put("UserId", user.getUid());
                    UserData.put("Username", username);
                    UserData.put("Town", Town);
                    UserData.put("DateOfJoining", DateOfJoining);
                    UserData.put("Email", email);
                    UserData.put("Status", true);
                    UserData.put("NumberOfPosts", 0);
                    UserData.put("City","Karachi");


                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,URL, new JSONObject(UserData), new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("VolleyUserAdd",response.toString());
                            Boolean isSuccess=false;

                            try {
                                isSuccess = response.getBoolean("success");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            if(isSuccess) {
                                sharedPrefs.setTownToSharedPref(Town);
                                sharedPrefs.setUserIdToSharedPref(user.getUid());
                                sharedPrefs.setUsernameToSharedPref(username);

                                //Subscribe to notifications and go to MainActivity

                                //User subscription
                                FirebaseMessaging.getInstance().subscribeToTopic("user_" + user.getUid());

                                //Town subscription
                                FirebaseMessaging.getInstance().subscribeToTopic(Town.replace(" ", "_"));

                                progressDialog.dismiss();
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class)
                                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                finish();
                                startActivity(intent);
                            }else{
                                progressDialog.dismiss();
                                Toast.makeText(RegisterActivity.this,"Could not create new user, please try again.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("VolleyUserError", error.toString());
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.dismiss();
                                    Toast.makeText(RegisterActivity.this,"Network Error!",Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });


                    RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
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


//                    databaseReference.child("Users/" + user.getUid()).setValue(userClassObj)
//                            .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                @Override
//                                public void onSuccess(Void aVoid) {
//                                    editor.putString("userid", user.getUid());
//                                    editor.putString("username", username);
//                                    editor.putString("profilepicture", "");
//                                    editor.apply();
//                                }
//                            }).addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//
//                        }
//                    });




                } else {
                    progressDialog.dismiss();
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthWeakPasswordException e) {

                        Toast.makeText(getApplicationContext(), "Please select a strong password", Toast.LENGTH_LONG).show();


                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        Toast.makeText(getApplicationContext(), "Please enter valid information", Toast.LENGTH_LONG).show();


                    } catch (FirebaseAuthUserCollisionException e) {

                        Toast.makeText(getApplicationContext(), "Email Already Exists", Toast.LENGTH_LONG).show();


                    } catch (FirebaseNetworkException e) {

                        Toast.makeText(getApplicationContext(), "Network Error!", Toast.LENGTH_LONG).show();


                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "Network Error!", Toast.LENGTH_LONG).show();

                    }

                }
            }
        });
    }


}




