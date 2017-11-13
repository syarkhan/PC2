package com.example.sheryarkhan.projectcity.activities;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.sheryarkhan.projectcity.Glide.GlideApp;
import com.example.sheryarkhan.projectcity.R;
import com.example.sheryarkhan.projectcity.utils.Constants;
import com.example.sheryarkhan.projectcity.utils.IVolleyResult;
import com.example.sheryarkhan.projectcity.utils.ImageCompression;
import com.example.sheryarkhan.projectcity.utils.SharedPrefs;
import com.example.sheryarkhan.projectcity.utils.VolleyService;
import com.example.sheryarkhan.projectcity.adapters.ShareNewsMediaViewPagerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import data.PostsPOJO;

public class PostNewsActivity extends AppCompatActivity {

    private Button btnMedia;
    private EditText editTextShareNews;

    private Uri mImageUri;
    private ViewPager mediaViewPager;
    private TabLayout mediaPagerTabs;
    private TextView txtName, txtPrimary, txtSecondary, txtPostLocation, txtPostNews;
    private ImageView imgClearLocations, imgProfilePic;

    private DatabaseReference databaseReference;

    private FirebaseAuth firebaseAuth;

    private Map<Integer, ArrayList<String>> hashMap;
    private Map<String, Boolean> media = new HashMap<>();
    private boolean isLastItem = false;

    private static final int REQUEST_OPEN_RESULT_CODE = 0;
    public static final int PERMISSIONS_MULTIPLE_REQUEST = 123;


    private static Integer number_of_posts = 0;

    private IVolleyResult mResultCallback;
    private VolleyService mVolleyService;
    private SharedPrefs sharedPrefs;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_news);
        sharedPrefs = new SharedPrefs(this);
        storageReference = FirebaseStorage.getInstance().getReference();

        btnMedia = (Button) findViewById(R.id.btnMedia);
        txtPostNews = (TextView) findViewById(R.id.txtPostNews);
        editTextShareNews = (EditText) findViewById(R.id.editTextShareNews);
        txtName = (TextView) findViewById(R.id.txtName);
        txtPrimary = (TextView) findViewById(R.id.txtPrimary);
        txtSecondary = (TextView) findViewById(R.id.txtSecondary);
        txtPostLocation = (TextView) findViewById(R.id.editTextPostLocation);
        mediaViewPager = (ViewPager) findViewById(R.id.mediaViewPager);
        mediaPagerTabs = (TabLayout) findViewById(R.id.mediaPagerTabs);
        imgClearLocations = (ImageView) findViewById(R.id.imgClearLocations);
        imgProfilePic = (ImageView) findViewById(R.id.imgProfilePic);

        firebaseAuth = FirebaseAuth.getInstance();

        String profilePicturePath = sharedPrefs.getProfilePictureFromSharedPref();
        String username = sharedPrefs.getUsernameFromSharedPref();
        String userid = sharedPrefs.getUserIdFromSharedPref();

        //databaseReference = FirebaseDatabase.getInstance().getReference();

        if (profilePicturePath.equals("")) {
            StorageReference filePath = storageReference.child("profilepictures").child("profilepic:" + userid);
            try {
                GlideApp.with(PostNewsActivity.this)
                        .load(filePath)
                        .circleCrop()
                        .transition(DrawableTransitionOptions.withCrossFade(1000))
                        .error(R.color.link)
                        .into(imgProfilePic);
            } catch (Exception ex) {
                Log.d("error", ex.toString());
            }
        } else {
            try {
                GlideApp.with(PostNewsActivity.this)
                        .load(profilePicturePath)
                        .circleCrop()
                        .transition(DrawableTransitionOptions.withCrossFade(1000))
                        .error(R.color.link)
                        .into(imgProfilePic);
            } catch (Exception ex) {
                Log.d("error", ex.toString());
            }
        }
//            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                @Override
//                public void onSuccess(Uri uri) {
//                    try {
//                        GlideApp.with(PostNewsActivity.this)
//                                .load(uri)
//                                .circleCrop()
//                                .transition(DrawableTransitionOptions.withCrossFade(1000))
//                                .error(R.color.link)
//                                .into(imgProfilePic);
//                    } catch (Exception ex) {
//                        Log.d("error", ex.toString());
//                    }
//                }
//            });


        txtName.setText(username);

        imgClearLocations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtPrimary.setVisibility(View.GONE);
                txtSecondary.setVisibility(View.GONE);
                imgClearLocations.setVisibility(View.GONE);
                txtPostLocation.setVisibility(View.VISIBLE);
            }
        });

        txtPostLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent = new Intent(PostNewsActivity.this, LocationAutoCompleteActivity.class);
                //intent.putExtra("oldL","newL");
                startActivityForResult(intent, 10);

                //startActivity(intent);
            }
        });


        btnMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                checkAndroidVersion();


//                Intent intent1 = new Intent(Intent.ACTION_OPEN_DOCUMENT);
//                intent1.addCategory(Intent.CATEGORY_OPENABLE);
//                intent1.setType("image/*");
//                startActivityForResult(intent1, REQUEST_OPEN_RESULT_CODE);
            }
        });


        txtPostNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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

                Intent intent = new Intent();
                intent.putExtra("txtPrimary", txtPrimary.getText().toString());
                intent.putExtra("txtSecondary", txtSecondary.getText().toString());
                intent.putExtra("editTextShareNews", editTextShareNews.getText().toString());
                intent.putExtra("hashMap", (Serializable) hashMap);
                //uploadPostDataToFirebase();
                setResult(RESULT_OK, intent);
                //startActivity(new Intent(PostNewsActivity.this, NewsFeedActivity.class));
                finish();

            }
        });


    }

    private void checkAndroidVersion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermission();

        } else {
            Intent intent = new Intent(PostNewsActivity.this, MediaPickerActivity.class);
            startActivityForResult(intent, 11);

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
                        "Please Grant Permissions to select media",
                        Snackbar.LENGTH_INDEFINITE).setAction("ENABLE",
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE},
                                        PERMISSIONS_MULTIPLE_REQUEST);
                            }
                        }).show();
            } else {
                // TODO : show dialog when user chooses to "NEVER SHOW" the permissions
                //Toast.makeText(this,"hello",Toast.LENGTH_SHORT).show();
                requestPermissions(
                        new String[]{Manifest.permission
                                .WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSIONS_MULTIPLE_REQUEST);
            }
        } else {
            Intent intent = new Intent(PostNewsActivity.this, MediaPickerActivity.class);
            startActivityForResult(intent, 11);
        }
    }

    private void uploadPostDataToFirebase() {
        SharedPreferences sharedPref = PostNewsActivity.this.getSharedPreferences("UserData", Context.MODE_PRIVATE);
        final String username = sharedPref.getString("username", "");
        //final String userid = sharedPref.getString("userid", "");

        final String userid = firebaseAuth.getCurrentUser().getUid();
        String imageURI = sharedPref.getString("profilepicture", "");

        final String key = databaseReference.child("posts").push().getKey();

        FirebaseStorage storage = FirebaseStorage.getInstance();

        StorageReference storageRef = storage.getReference();
        final DatabaseReference queryForUserNumberOfPosts = databaseReference.child("Users");
        // Uri file = Uri.fromFile(new File(mImageUri.toString()));

        //final Iterator iterator = hashMap.values().iterator();
        //Map<Integer, ArrayList<Object>> hMap = Collections.emptyMap();
        //int i = 0;
        final List<Media> medias = new ArrayList<>();
        for (ArrayList<String> item : hashMap.values()) {


            //String type = getMimeType(Uri.fromFile(new File(item)));
            if (Integer.parseInt(item.get(0)) == 1) // 1 FOR IMAGE
            {
                Bitmap bmp = ImageCompression.getImageFromResult(PostNewsActivity.this, item.get(1));//your compressed bitmap here
                bmp = ImageCompression.rotate(bmp, Integer.parseInt(item.get(2)));
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] imageByteData = baos.toByteArray();

                try {
                    baos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                bmp.recycle();
                try {
                    Media media = new Media(1, imageByteData, null);
                    medias.add(media);
                } catch (Exception ex) {
                    Log.d("mediaError", ex.toString());
                }
            } else if (Integer.parseInt(item.get(0)) == 2) // 2 FOR VIDEO
            {
                try {
                    byte[] videoByte = convertPathToBytes(String.valueOf(item.get(1)));
                    Media media = new Media(2, null, videoByte);
                    medias.add(media);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.d("VIDEOPOST", "VIDEOPOST");
            }
        }

        if (medias.size() == 0) {
            Long timeStamp = System.currentTimeMillis();
            PostsPOJO postsPOJO = new PostsPOJO(0, userid, key, "profilepic:" + userid, username, timeStamp, editTextShareNews.getText().toString(),
                    txtPrimary.getText().toString(), txtSecondary.getText().toString(), Collections.<String, Boolean>emptyMap());


            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put("/posts/" + key, postsPOJO);
            databaseReference.updateChildren(childUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                    //SEND NOTIFICATION TO USERS
                    sendNotificationToUsers(key);

                    //int number_of_posts=0;
                    queryForUserNumberOfPosts.child(userid).child("number_of_posts").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            number_of_posts = dataSnapshot.getValue(Integer.class);
                            queryForUserNumberOfPosts.child(userid).child("number_of_posts").setValue(number_of_posts + 1);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
            });

        } else {

            final int last_item = (medias.size() - 1);

            for (int i = 0; i < medias.size(); i++) {
                if (medias.get(i).getMediaType() == 1) // IMAGE
                {
                    if (i == last_item) {  //IF Last Image/Video
                        isLastItem = true;
                    }
                    String uniqueId = UUID.randomUUID().toString();
                    media.put("image:" + uniqueId, true);
                    StorageReference mediaRef = storageRef.child("images/image:" + uniqueId);

                    mediaRef.putBytes(medias.get(i).getImageBytes()).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("storageerror", e.toString());
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(getBaseContext(), "uploaded", Toast.LENGTH_SHORT).show();


                            if (isLastItem) {

                                Long timeStamp = System.currentTimeMillis();
                                PostsPOJO postsPOJO = new PostsPOJO(0, userid, key, "profilepic:" + userid, username, timeStamp, editTextShareNews.getText().toString(),
                                        txtPrimary.getText().toString(), txtSecondary.getText().toString(),
                                        media, Collections.<String, Boolean>emptyMap());

                                Map<String, Object> childUpdates = new HashMap<>();
                                childUpdates.put("/posts/" + key, postsPOJO);
//                                mDatabase.child("posts").child(key).child("likes").setValue(0);
                                databaseReference.updateChildren(childUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        //SEND NOTIFICATION TO USERS
                                        sendNotificationToUsers(key);

                                        queryForUserNumberOfPosts.child(userid).child("number_of_posts").addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                number_of_posts = dataSnapshot.getValue(Integer.class);
                                                queryForUserNumberOfPosts.child(userid).child("number_of_posts").setValue(number_of_posts + 1);
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });

                                    }
                                });

                            }
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            Toast.makeText(PostNewsActivity.this, progress + "% done", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("uploadError", e.toString());
                        }
                    });
                } else if (medias.get(i).getMediaType() == 2) { //VIDEO

                    if (i == last_item) {  //IF Last Image/Video
                        isLastItem = true;
                    }

                    String uniqueId = UUID.randomUUID().toString();
                    media.put("video:" + uniqueId, true);
                    StorageReference mediaRef = storageRef.child("images/video:" + uniqueId);

                    mediaRef.putBytes(medias.get(i).getVideoBytes()).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("storageerror", e.toString());
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(getBaseContext(), "uploaded", Toast.LENGTH_SHORT).show();
                            if (isLastItem) {

                                Long timeStamp = System.currentTimeMillis();

                                PostsPOJO postsPOJO = new PostsPOJO(0, userid, key, "profilepic:" + userid, username, timeStamp, editTextShareNews.getText().toString(),
                                        txtPrimary.getText().toString(), txtSecondary.getText().toString(),
                                        media, Collections.<String, Boolean>emptyMap());

                                Map<String, Object> childUpdates = new HashMap<>();
                                childUpdates.put("/posts/" + key, postsPOJO);
                                //mDatabase.child("posts").child(key).child("likes").setValue(0);
                                databaseReference.updateChildren(childUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        //SEND NOTIFICATION TO USERS
                                        sendNotificationToUsers(key);

                                        //int number_of_posts=0;
                                        queryForUserNumberOfPosts.child(userid).child("number_of_posts").addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                number_of_posts = dataSnapshot.getValue(Integer.class);
                                                queryForUserNumberOfPosts.child(userid).child("number_of_posts").setValue(number_of_posts + 1);
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });

                                    }
                                });

                            }
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            Toast.makeText(PostNewsActivity.this, progress + "% done", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("uploadError", e.toString());
                        }
                    });

                }
            }

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(Activity.RESULT_CANCELED);
    }

    private void sendNotificationToUsers(String key) {
        Map<String, String> townAndMessage = new HashMap<>();
        townAndMessage.put("town", txtSecondary.getText().toString().replace(" ", "_"));
        townAndMessage.put("message", editTextShareNews.getText().toString());
        townAndMessage.put("postId", key);
        mVolleyService = new VolleyService(mResultCallback, PostNewsActivity.this);
        mVolleyService.postDataVolley(Request.Method.POST,
                Constants.protocol + Constants.IP + "/sendNotification",
                new JSONObject(townAndMessage));
    }

    private byte[] convertPathToBytes(String path) throws IOException {

        FileInputStream fis = new FileInputStream(path);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] b = new byte[1024];

        for (int readNum; (readNum = fis.read(b)) != -1; ) {
            bos.write(b, 0, readNum);
        }

        byte[] bytes = bos.toByteArray();

        bos.close();

        return bytes;
    }

    public String getMimeType(Uri uri) {
        String mimeType = null;

        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            ContentResolver cr = this.getContentResolver();
            mimeType = cr.getType(uri);
        } else {
            String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri
                    .toString());
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                    fileExtension.toLowerCase());
        }
        return mimeType;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {

        if (requestCode == REQUEST_OPEN_RESULT_CODE) {
            if (resultData != null && resultCode == RESULT_OK) {
                mImageUri = resultData.getData();
                Toast.makeText(getApplicationContext(), mImageUri.toString(), Toast.LENGTH_LONG).show();
//                Glide.with(this)
//                        .load(mImageUri)
//                        .apply(RequestOptions.circleCropTransform())
//                        .into(userImage);
            } else if (resultCode == RESULT_CANCELED) {

            }
        } else if (requestCode == 10) {

            if (resultCode == RESULT_OK) {
                //String address = null;
                //String[] towns = new String[]{};
                //String town;
                //String suburb;
                String primaryLocation = resultData.getStringExtra("primaryLocation");
                String town = resultData.getStringExtra("town");
                txtPostLocation.setVisibility(View.GONE);
                txtPrimary.setText(primaryLocation);
                txtSecondary.setText(town);
                //txtSecondary.setText(towns.get(0));

//                    if (address.has("suburb")) {
//                        suburb = address.getString("suburb");
//                        txtSecondary.setText(suburb);
//                    } else if (address.has("town")) {
//                        town = address.getString("town");
//                        txtSecondary.setText(town);
//                    }

                txtPrimary.setTextColor(ContextCompat.getColor(this, R.color.colorAccent));
                txtPrimary.setVisibility(View.VISIBLE);
                txtSecondary.setVisibility(View.VISIBLE);
                imgClearLocations.setVisibility(View.VISIBLE);


                //editTextPostLocation.setText(editTextValue);
            } else if (resultCode == RESULT_CANCELED) {

            }


        } else if (requestCode == 11) {

            if (resultCode == RESULT_OK) {


                //startPosting(bmp);
                hashMap = (HashMap<Integer, ArrayList<String>>) resultData.getSerializableExtra("hashMap");
                //Map<Integer,Bitmap> hMap = new HashMap<>();
                //int i=0;
//                for (String item:hashMap.values()) {
//
//                    Bitmap bmp = ImageCompression.getImageFromResult(this, resultCode, item);//your compressed bitmap here
//                    hMap.put(i,bmp);
//
//                    i++;
//
//                }


                //Toast.makeText(getApplicationContext(),hashMap.toString(),Toast.LENGTH_LONG).show();
                ShareNewsMediaViewPagerAdapter viewPagerAdapter = new ShareNewsMediaViewPagerAdapter(PostNewsActivity.this, hashMap);
                mediaViewPager.setAdapter(viewPagerAdapter);
                mediaPagerTabs.setupWithViewPager(mediaViewPager, true);
                mediaViewPager.setVisibility(View.VISIBLE);

            } else if (resultCode == RESULT_CANCELED) {

            }


        } else if (requestCode == 100) {
            if (resultCode == RESULT_OK) {

//                Glide.with(this)
//                        .load(mImageUri)
//                        .apply(RequestOptions.circleCropTransform())
//                        .into(userImage);


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

    public static class Media {
        private byte[] imageBytes = null;
        private byte[] videoBytes = null;
        private Integer mediaType;

        public Media(Integer mediaType, byte[] imageBytes, byte[] videoBytes) {
            this.mediaType = mediaType;
            this.imageBytes = imageBytes;
            this.videoBytes = videoBytes;

        }

        public Integer getMediaType() {
            return mediaType;
        }

        public byte[] getImageBytes() {
            return imageBytes;
        }

        public byte[] getVideoBytes() {
            return videoBytes;
        }
    }
}
