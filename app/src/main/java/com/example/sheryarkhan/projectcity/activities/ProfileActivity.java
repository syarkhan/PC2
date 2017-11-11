package com.example.sheryarkhan.projectcity.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;


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
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
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
import org.w3c.dom.Text;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import data.PostsPOJO;
import data.User;
import data.UserClass;

public class ProfileActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private TextView txtUsername, txtLocation, txtPointsValue, txtTotalPosts, txtAbout, txtProfileBio;
    private ImageView userImage;
    private ConstraintLayout constraintLayoutScroll;
    private ProgressBar progressBar;
    //private ImageView btnUploadImage;
    private TextView  btnEditProfile;
    private TextView btnBack;
    private static final int REQUEST_OPEN_RESULT_CODE = 0;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private Uri mImageUri;
    private ProgressDialog progressDialog;
    //public static final int MY_PERMISSIONS_REQUEST_CAMERA = 100;
    public static final int PERMISSIONS_MULTIPLE_REQUEST = 123;


    private DatabaseReference databaseReference;
    private DatabaseReference query;



    private FirebaseAuth firebaseAuth;
    private SharedPrefs sharedPrefs;


    //private Uri uriFilePath;
    //private Button btnEditProfile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        sharedPrefs = new SharedPrefs(this);
        final String userid = sharedPrefs.getUserIdFromSharedPref();

        Toast.makeText(getApplicationContext(),"retreiving user data",Toast.LENGTH_SHORT).show();

        databaseReference = FirebaseDatabase.getInstance().getReference();

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        //Log.d("useriddada",user.getUid());
        query = databaseReference.child("Users").child(user.getUid());

        txtUsername = (TextView) findViewById(R.id.txtUsername);
        txtLocation = (TextView) findViewById(R.id.txtLocation);
        txtPointsValue = (TextView) findViewById(R.id.txtPointsValue);
        txtAbout = (TextView) findViewById(R.id.txtAbout);
        txtProfileBio = (TextView) findViewById(R.id.txtProfileBio);
        txtTotalPosts = (TextView) findViewById(R.id.txtTotalPosts);
        btnBack = (TextView) findViewById(R.id.btnBack);

        constraintLayoutScroll = (ConstraintLayout)findViewById(R.id.constraintLayoutScroll);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

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
                        Log.d("userdata", user.toString());
                        if (user.getTotalNumberOfPosts() == null) {
                            txtTotalPosts.setText(String.valueOf(0));
                        } else {
                            txtTotalPosts.setText(user.getTotalNumberOfPosts());
                        }

                        if (user.getPoints() == null) {
                            txtPointsValue.setText(String.valueOf(0));
                        } else {
                            txtPointsValue.setText(user.getPoints());
                        }

                        txtUsername.setText(user.getUsername());

                        txtAbout.setText("About " + user.getUsername());

                        if(user.getBio() == null)
                        {
                            txtProfileBio.setText("Write something about yourself");
                        }
                        txtProfileBio.setText(user.getBio());

                        if (user.getTown() == null)
                        {
                            txtLocation.setText("No Town Selected");
                        }
                        else {
                            txtLocation.setText(user.getTown() +", Karachi");
                        }

                        progressBar.setVisibility(View.GONE);
                        constraintLayoutScroll.setVisibility(View.VISIBLE);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(ProfileActivity.this,"Connection Error!",Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("VolleyError", error.toString());
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ProfileActivity.this,"Connection Error!",Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue queue = Volley.newRequestQueue(ProfileActivity.this);
        queue.add(jsonObjectRequest);


//        query.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//
//                final UserClass userClass = dataSnapshot.getValue(UserClass.class);
//                Log.d("userdada", userClass.toString());
//
//
//                if (!dataSnapshot.child("number_of_posts").exists()) {
//                    txtTotalPosts.setText(String.valueOf(0));
//                } else {
//                    txtTotalPosts.setText(dataSnapshot.child("number_of_posts").getValue().toString());
//                }
//
//                if (!dataSnapshot.child("totalpoints").exists()) {
//                    txtPointsValue.setText(String.valueOf(0));
//                } else {
//                    txtPointsValue.setText(dataSnapshot.child("totalpoints").getValue().toString());
//                }
//
//                txtUsername.setText(dataSnapshot.child("username").getValue().toString());
//
//                txtAbout.setText("About " + userClass.getUsername());
//
//                txtProfileBio.setText(userClass.getBio());
//
//                //Set town from shared prefs
//
//
//                if (userClass.getPrimarylocation() == null)
//                {
//                    txtLocation.setText("No Town Selected");
//                }
//                else {
//                    txtLocation.setText(dataSnapshot.child("primarylocation").getValue().toString() + ", Karachi");
//                }
//
//
//
//
//                //DataSnapshot dataSnapshot1 = dataSnapshot.getC;
//
//
//                //Toast.makeText(getApplicationContext(),"done",Toast.LENGTH_SHORT).show();
//                progressBar.setVisibility(View.GONE);
//                constraintLayoutScroll.setVisibility(View.VISIBLE);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnEditProfile = (TextView) findViewById(R.id.btnEditProfile);

//        btnLogout = (Button) findViewById(R.id.btnLogout);

//        btnLogout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                //FirebaseUser user = firebaseAuth.getCurrentUser();
//
//                FirebaseMessaging.getInstance().unsubscribeFromTopic(town);
//                FirebaseMessaging.getInstance().unsubscribeFromTopic("user_"+firebaseAuth.getCurrentUser().getUid());
//
//
//                //databaseReference.child(user.getUid()).child("status").setValue(false);
//                firebaseAuth.signOut();
//
//                SharedPrefs.Editor editor = sharedPref.edit();
//                editor.clear();
//                editor.apply();
//                startActivity(new Intent(ProfileActivity.this, LoginActivity.class)
//                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
//                finish();
//
////                finish();
////                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
////                startActivity(intent);
//            }
//        });


        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
                startActivity(intent);
            }
        });


//        mGoogleApiClient = new GoogleApiClient
//                .Builder(this)
//                .addApi(Places.GEO_DATA_API)
//                .addApi(Places.PLACE_DETECTION_API)
//                .enableAutoManage(this, this)
//                .build();

        //PlaceAutocomplete.IntentBuilder


//
        userImage = (ImageView) findViewById(R.id.imgUser);

        try {
            String imageURI = sharedPrefs.getProfilePictureFromSharedPref();
            mImageUri = Uri.parse(imageURI);
            GlideApp.with(this)
                    .load(mImageUri)
                    .transition(DrawableTransitionOptions.withCrossFade(1000))
                    .error(R.drawable.circle_image)
                    .apply(RequestOptions.circleCropTransform())
                    .into(userImage);

        } catch (Exception e) {

        }

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void BtnSaveOnClick() {
//        databaseReference = FirebaseDatabase.getInstance().getReference();
//
//
//        FirebaseUser user = firebaseAuth.getInstance().getCurrentUser();

        FirebaseStorage storage = FirebaseStorage.getInstance();

        StorageReference storageRef = storage.getReference();

        // Uri file = Uri.fromFile(new File(mImageUri.toString()));
        String uniqueId = UUID.randomUUID().toString();
        StorageReference imagesRef = storageRef.child("images/profilepic:" + uniqueId);
        //StorageReference imagesRef = storageRef.child("images/"+);
        imagesRef.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Toast.makeText(getBaseContext(), "uploaded", Toast.LENGTH_SHORT).show();

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
                Toast.makeText(ProfileActivity.this, progress + "% done", Toast.LENGTH_SHORT).show();
            }
        });


//        databaseReference.child(user.getUid()).child("latitude").setValue(lat);
//        databaseReference.child(user.getUid()).child("longitude").setValue(lng);


    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case PERMISSIONS_MULTIPLE_REQUEST:
                if (grantResults.length > 0) {

                    boolean readExternalFile = grantResults[2] == PackageManager.PERMISSION_GRANTED;

                    boolean cameraPermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    boolean writeExternalFile = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                    if (cameraPermission && readExternalFile && writeExternalFile) {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                        mImageUri = Uri.fromFile(getOutputMediaFile());
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);

                        startActivityForResult(intent, 100);
                    } else {
                        Snackbar.make(this.findViewById(android.R.id.content),
                                "Please grant permissions to upload profile photo",
                                Snackbar.LENGTH_SHORT).setAction("ENABLE",
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        requestPermissions(
                                                new String[]{Manifest.permission
                                                        .WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE},
                                                PERMISSIONS_MULTIPLE_REQUEST);
                                    }
                                }).show();
                    }
                }
                break;
        }
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
                Environment.DIRECTORY_PICTURES), "CameraDemo");

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
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        SharedPreferences sharedPref = this.getSharedPreferences("UserData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        if (requestCode == REQUEST_OPEN_RESULT_CODE && resultCode == Activity.RESULT_OK) {
            if (resultData != null) {
                mImageUri = resultData.getData();

                /*String[] columns = {MediaStore.Images.Media.ORIENTATION};
                Cursor cursor = getApplicationContext().getContentResolver().query(mImageUri, columns, null, null, null);
                if (cursor == null)
                {

                }
                else {
                    cursor.moveToFirst();
                    int orientationColumnIndex = cursor.getColumnIndex(columns[0]);
                    int rotation = cursor.getInt(orientationColumnIndex);
                    cursor.close();
                }*/


                Glide.with(this)
                        .load(mImageUri)
                        .apply(RequestOptions.circleCropTransform())
                        .into(userImage);

                editor.putString("profilepicture", mImageUri.toString());
                editor.apply();

            }
        } else if (requestCode == 100) {
            if (resultCode == RESULT_OK) {
                Glide.with(this)
                        .load(mImageUri)
                        .apply(RequestOptions.circleCropTransform())
                        .into(userImage);
                editor.putString("profilepicture", mImageUri.toString());
                editor.apply();

//                userImage.setImageURI(mImageUri);
            }
        }

        //if (requestCode == REQUEST_IMAGE_CAPTURE  && resultCode == Activity.RESULT_OK) {
//            if (resultData != null) {
//
//                try{
//                mImageUri = resultData.getData();}
//                catch (Exception e ){
//                    Log.i("masla",e.toString());
//                }
//                Glide.with(this)
//                        .load(mImageUri)
//                        .apply(RequestOptions.circleCropTransform())
//                        .into(userImage);
//
//
//                // Here is path of your captured image, so you can create bitmap from it, etc.
//
//
//            }
    }

    public void selectImage() {
        final CharSequence[] options = {"Take Photo", "Choose from Gallery"};
        final AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
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


//        public void UploadUserImage(View view){
//
////        PackageManager packageManager = getApplication().getPackageManager();
////        if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
////            File mainDirectory = new File(Environment.getExternalStorageDirectory(), "ProjectCity/Images");
////            if (!mainDirectory.exists())
////                mainDirectory.mkdirs();
////
////            Calendar calendar = Calendar.getInstance();
////
////            mImageUri = Uri.fromFile(new File(mainDirectory, "IMG_" + calendar.getTimeInMillis()));
////            Intent intent1 = new Intent(Intent.ACTION_OPEN_DOCUMENT);
////            intent1.addCategory(Intent.CATEGORY_OPENABLE);
////            intent1.setType("image/*");
////
//////            intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
////            startActivityForResult(intent1, REQUEST_OPEN_RESULT_CODE);
////
////        }
////
////
//        Intent intent1 = new Intent(Intent.ACTION_OPEN_DOCUMENT);
//        intent1.addCategory(Intent.CATEGORY_OPENABLE);
//        intent1.setType("image/*");
//        startActivityForResult(intent1, REQUEST_OPEN_RESULT_CODE);
//
//
//
//
//    }


//    public void dispatchTakePictureIntent(View view) {
//        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
//            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
//        }


//        PackageManager packageManager = getApplication().getPackageManager();
//        if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
//            File mainDirectory = new File(Environment.getExternalStorageDirectory(), "ProjectCity/Images");
//            if (!mainDirectory.exists())
//                mainDirectory.mkdirs();
//
//            Calendar calendar = Calendar.getInstance();
//
//            mImageUri = Uri.fromFile(new File(mainDirectory, "IMG_" + calendar.getTimeInMillis()));
//           Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//            intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
//            if (intent.resolveActivity(getPackageManager()) != null) {
//                startActivityForResult(intent, 1);
//        }
//        }

//        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
//            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
//        }


//    @Override
//    public void onSaveInstanceState(Bundle outState) {
//
//        if (mImageUri != null){
//            outState.putString("imageuri", mImageUri.toString());
//        }
//        super.onSaveInstanceState(outState);
//    }



}







