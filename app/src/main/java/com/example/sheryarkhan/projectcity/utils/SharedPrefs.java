package com.example.sheryarkhan.projectcity.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Sheryar Khan on 11/10/2017.
 */

public class SharedPrefs {

    private SharedPreferences sharedPref;
    private final String PREFS_FILE_NAME = "UserData";
    private Context context;

    //final String username = sharedPref.getString("username", "");
    public SharedPrefs(Context context) {
        sharedPref = context.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE);
        this.context = context;
    }

    //GETTERS
    public String getUsernameFromSharedPref() {
        return sharedPref.getString("username", "");
    }

    public String getUserIdFromSharedPref() {
        return sharedPref.getString("userid", "");
    }

    public String getTownFromSharedPref() {
        return sharedPref.getString("town", "");
    }

    public String getProfilePictureFromSharedPref(){
        return sharedPref.getString("profilepicture","");
    }



    //SETTERS
    public void setUsernameToSharedPref(String username) {
        sharedPref.edit().putString("username", username).apply();
    }

    public void setUserIdToSharedPref(String userid) {
        sharedPref.edit().putString("userid", userid).apply();
    }

    public void setTownToSharedPref(String town) {
        sharedPref.edit().putString("town", town).apply();
    }

    public void setProfilePictureFromSharedPref(String path){
        sharedPref.edit().putString("profilepicture",path).apply();
    }

    //CHECK IF FILE EXISTS
    public boolean checkIfSharedPrefsFileExists() {

        if (sharedPref.getBoolean("FileExists", false)) {
            sharedPref.edit().putBoolean("FileExists", true).apply();
            return false;
        } else {
            return true;
        }
    }

    public void deleteSharedPrefsFile() {
        context.getSharedPreferences(PREFS_FILE_NAME, 0).edit().clear().apply();
    }

}
