package com.example.sheryarkhan.projectcity.utils;

import com.android.volley.VolleyError;

import org.json.JSONObject;

/**
 * Created by Sheryar Khan on 10/15/2017. s
 */

public interface IVolleyResult {
    void notifySuccess(int requestType,JSONObject response);
    void notifyError(int requestType,VolleyError error);
}
