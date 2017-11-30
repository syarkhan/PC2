package com.example.sheryarkhan.projectcity.activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.example.sheryarkhan.projectcity.Glide.GlideApp;
import com.example.sheryarkhan.projectcity.R;
import com.example.sheryarkhan.projectcity.utils.Constants;
import com.example.sheryarkhan.projectcity.utils.SharedPrefs;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import data.User;
import data.UserClass;

public class EditProfileActivity extends AppCompatActivity {

    TextView txtLocation;
    TextView txtUpdateProfile;
    TextView txtCancel;
    ImageView btnUploadImage, imgUser;
    EditText editTextName, editTextInfo;
    private ConstraintLayout mainLayout;
    private ProgressBar progressBar;
    private static final int REQUEST_OPEN_RESULT_CODE_FOR_TOWN = 10;
    private static final int REQUEST_OPEN_RESULT_CODE = 0;
    public static final int PERMISSIONS_MULTIPLE_REQUEST = 123;
    String Town = null;

    private DatabaseReference query;
    private Uri mImageUri;

    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private SharedPrefs sharedPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        sharedPrefs = new SharedPrefs(this);
        Town = sharedPrefs.getTownFromSharedPref();
        final String userId = sharedPrefs.getUserIdFromSharedPref();
        //final String userId = sharedPref.getString("userid", "");

        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        //FirebaseUser user = firebaseAuth.getCurrentUser();
        //final String userId = user.getUid();
        query = databaseReference.child("Users").child(userId);

        mainLayout = (ConstraintLayout) findViewById(R.id.mainLayout);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        txtLocation = (TextView) findViewById(R.id.txtLocation);
        txtUpdateProfile = (TextView) findViewById(R.id.btnUpdateProfile);
        imgUser = (ImageView) findViewById(R.id.imgUser);

        btnUploadImage = (ImageView) findViewById(R.id.btnUploadImage);
        editTextName = (EditText) findViewById(R.id.editTextName);
        editTextInfo = (EditText) findViewById(R.id.editTextInfo);
        txtCancel = (TextView) findViewById(R.id.txtCancel);


        try {
            String imageURI = sharedPrefs.getProfilePictureFromSharedPref();
            mImageUri = Uri.parse(imageURI);
            GlideApp.with(this)
                    .load(mImageUri)
                    .transition(DrawableTransitionOptions.withCrossFade(1000))
                    .error(R.drawable.circle_image)
                    .apply(RequestOptions.circleCropTransform())
                    .into(imgUser);

        } catch (Exception e) {

        }

        String URL = Constants.protocol + Constants.IP + Constants.getUserDetails+"/"+userId;
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
                        txtUpdateProfile.setEnabled(true);
                        User user = gson.fromJson(String.valueOf(response.getJSONObject("User")), User.class);
                        Log.d("userdata", user.toString());

                        editTextName.setText(user.getUsername());

                        if(user.getBio() != null)
                        {
                            editTextInfo.setText(user.getBio());
                        }

                        if (user.getTown() == null)
                        {
                            txtLocation.setText("No Town Selected");
                        }
                        else {
                            txtLocation.setText(user.getTown());
                        }

                        progressBar.setVisibility(View.GONE);
                        mainLayout.setVisibility(View.VISIBLE);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(EditProfileActivity.this,"Connection Error!",Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("VolleyError", error.toString());
                progressBar.setVisibility(View.GONE);
                Toast.makeText(EditProfileActivity.this,"Connection Error!",Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue queue = Volley.newRequestQueue(EditProfileActivity.this);
        queue.add(jsonObjectRequest);


//        query.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//
//                final UserClass userClass = dataSnapshot.getValue(UserClass.class);
//
//                //Log.d("userdadaedit",userClass.toString());
//                if (userClass.getPrimarylocation() == null)
//                {
//                    txtLocation.setText("Select Town");
//                    txtLocation.setTypeface(null, Typeface.BOLD);
//                }
//                else {
//                    txtLocation.setText(userClass.getPrimarylocation());
//                    txtLocation.setTextColor(ContextCompat.getColor(EditProfileActivity.this,R.color.colorAccent));
//                }
//
//                editTextName.setText(userClass.getUsername());
//                editTextInfo.setText(userClass.getBio());
//
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });

        txtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        txtLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditProfileActivity.this, TownAutoCompleteActivity.class);
                startActivityForResult(intent, REQUEST_OPEN_RESULT_CODE_FOR_TOWN);

            }
        });


        final String updateUserURL = Constants.protocol + Constants.IP +
                Constants.updateUserDetails+"/"+userId;
        txtUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(editTextName.getText().toString().trim()) || TextUtils.isEmpty(txtLocation.getText().toString().trim())) {
                    Toast.makeText(getApplicationContext(), "Name field cannot be empty", Toast.LENGTH_LONG).show();
                } else {
                    Map<String, Object> UserDetails = new HashMap<>();

                    if(mImageUri == null) {
                        UserDetails.put("ProfilePicture", "user_"+userId);
                    }

                    UserDetails.put("Username", editTextName.getText().toString());
                    UserDetails.put("Town", Town);
                    UserDetails.put("Bio", editTextInfo.getText().toString());
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, updateUserURL, new JSONObject(UserDetails), new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Boolean isSuccess=false;
                            try {
                                isSuccess = response.getBoolean("success");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            if(isSuccess){
                                Toast.makeText(EditProfileActivity.this,"Details Updated!",Toast.LENGTH_SHORT).show();
                                finish();
                            }else{
                                Toast.makeText(EditProfileActivity.this,"Details did not update, please try again",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("VolleyError", error.toString());
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(EditProfileActivity.this,"Connection Error!",Toast.LENGTH_SHORT).show();
                        }
                    });
                    RequestQueue queue = Volley.newRequestQueue(EditProfileActivity.this);
                    queue.add(jsonObjectRequest);
//                    final FirebaseUser user = firebaseAuth.getCurrentUser();
//                    databaseReference.child("Users/" + user.getUid()).child("primarylocation")
//                            .setValue(Town).addOnSuccessListener(new OnSuccessListener<Void>() {
//                        @Override
//                        public void onSuccess(Void aVoid) {
//
//                            if (Town.contains(" ")) {
//                                FirebaseMessaging.getInstance().subscribeToTopic(Town.replace(" ","_"));
//                                sharedPrefs.setTownToSharedPref(Town);
//
//                            } else {
//                                FirebaseMessaging.getInstance().subscribeToTopic(Town);
//                                sharedPrefs.setTownToSharedPref(Town);
//                                Log.d("towndada1", Town);
//                            }
//                        }
//                    });
//
//
//                    databaseReference.child("Users/" + user.getUid()).child("city")
//                            .setValue("Karachi");
//
//                    databaseReference.child("Users/" + user.getUid()).child("bio")
//                            .setValue(editTextInfo.getText().toString());
//
//                    databaseReference.child("Users/" + user.getUid()).child("username")
//                            .setValue(editTextName.getText().toString())
//                            .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                @Override
//                                public void onSuccess(Void aVoid) {
//                                    //editor.putString("userid",user.getUid());
//                                    sharedPrefs.setUsernameToSharedPref(editTextName.getText().toString());
//                                    //startActivity(new Intent(EditProfileActivity.this, ProfileActivity.class));
//                                    finish();
//                                }
//                            }).addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//
//                        }
//                    });
                }
            }
        });

        btnUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
                //saveProfilePictureToServerDB();

            }
        });

    }

    public void saveProfilePictureToServerDB() {
//        databaseReference = FirebaseDatabase.getInstance().getReference();
//
//
        final FirebaseUser user = firebaseAuth.getCurrentUser();

        FirebaseStorage storage = FirebaseStorage.getInstance();

        StorageReference storageRef = storage.getReference();

        StorageReference imagesRef = storageRef.child("profilepictures/profilepic:" + user.getUid());
        //StorageReference imagesRef = storageRef.child("images/"+);
        imagesRef.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Toast.makeText(getBaseContext(), "Uploaded", Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(getBaseContext(), "failed", Toast.LENGTH_SHORT).show();

            }

        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                Toast.makeText(EditProfileActivity.this, "Profile picture uploading: "+ progress +" % done", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

//                query.child("profilepicture").setValue("profilepic:" + user.getUid());

                databaseReference.child("Users/" + user.getUid()).child("profilepicture")
                        .setValue("profilepic:" + user.getUid());

            }
        });


//        databaseReference.child(user.getUid()).child("latitude").setValue(lat);
//        databaseReference.child(user.getUid()).child("longitude").setValue(lng);


    }

    public void selectImage() {
        final CharSequence[] options = {"Take Photo", "Choose from Gallery"};
        final AlertDialog.Builder builder = new AlertDialog.Builder(EditProfileActivity.this);
        builder.setTitle("Add Photo!");
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                dialogInterface.dismiss();
            }
        });
        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                if (options[which].equals("Take Photo")) {

                    checkAndroidVersion();


                } else if (options[which].equals("Choose from Gallery")) {
                    Intent intent1 = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent1.addCategory(Intent.CATEGORY_OPENABLE);
                    intent1.setType("image/*");
                    startActivityForResult(intent1, REQUEST_OPEN_RESULT_CODE);

                }
//                else if(options[which].equals("Cancel"))
//                {
//                    dialog.dismiss();
//                }

            }
        });
        builder.show();
    }

    private void checkAndroidVersion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermission();

        } else {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            mImageUri = Uri.fromFile(getOutputMediaFile());
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
            startActivityForResult(intent, 100);
        }

    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) + ContextCompat
                .checkSelfPermission(this,
                        Manifest.permission.CAMERA) + ContextCompat
                .checkSelfPermission(this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale
                    (this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
                    ActivityCompat.shouldShowRequestPermissionRationale
                            (this, Manifest.permission.CAMERA) ||
                    ActivityCompat.shouldShowRequestPermissionRationale
                            (this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

                Snackbar.make(this.findViewById(android.R.id.content),
                        "Please Grant Permissions to upload profile photo",
                        Snackbar.LENGTH_INDEFINITE).setAction("ENABLE",
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_MULTIPLE_REQUEST);
                            }
                        }).show();
            } else {
                requestPermissions(
                        new String[]{Manifest.permission
                                .WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSIONS_MULTIPLE_REQUEST);
            }
        } else {

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            mImageUri = Uri.fromFile(getOutputMediaFile());
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
            startActivityForResult(intent, 100);
//            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
//                mImageUri = Uri.fromFile(getOutputMediaFile());
//                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
//
//                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
//            }
        }
    }

    private static File getOutputMediaFile() {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "ProjectCity");

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("CameraDemo", "failed to create directory");
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return new File(mediaStorageDir.getPath() + File.separator +
                "IMG_" + timeStamp + ".jpg");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if (requestCode == REQUEST_OPEN_RESULT_CODE_FOR_TOWN) {
            if (resultData != null && resultCode == RESULT_OK) {
                Town = resultData.getStringExtra("Town");

                sharedPrefs.setTownToSharedPref(Town);

                txtLocation.setText(Town);
                txtLocation.setTextColor(ContextCompat.getColor(this,R.color.colorAccent));
            } else if (resultCode == RESULT_CANCELED) {

            }
        }
        else if (requestCode == REQUEST_OPEN_RESULT_CODE && resultCode == Activity.RESULT_OK) {
            if (resultData != null) {
                mImageUri = resultData.getData();
                GlideApp.with(this)
                        .load(mImageUri)
                        .circleCrop()
                        .into(imgUser);

                sharedPrefs.setProfilePictureFromSharedPref(mImageUri.toString());
                saveProfilePictureToServerDB();

            }
        }
        else if (requestCode == 100) {
            if (resultCode == RESULT_OK) {
                GlideApp.with(this)
                        .load(mImageUri)
                        .circleCrop()
                        .into(imgUser);
                sharedPrefs.setProfilePictureFromSharedPref(mImageUri.toString());
                saveProfilePictureToServerDB();

//                userImage.setImageURI(mImageUri);
            }
        }
    }
}
