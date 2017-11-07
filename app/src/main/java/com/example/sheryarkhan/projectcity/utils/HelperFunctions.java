package com.example.sheryarkhan.projectcity.utils;

import android.content.Context;
import android.text.format.DateUtils;
import android.widget.Toast;

/**
 * Created by Sheryar Khan on 10/25/2017.
 */

public class HelperFunctions {

    public static CharSequence getTimeAgo(Long timestamp) {
        return DateUtils.getRelativeTimeSpanString(
                Long.parseLong(String.valueOf(timestamp)),
                System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
    }

    public static Long getCurrentTimestamp()
    {
        return System.currentTimeMillis();
    }

    public static void getToastShort(Context context,String message)
    {
        Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
    }

    public static void getToastLong(Context context,String message)
    {
        Toast.makeText(context,message,Toast.LENGTH_LONG).show();
    }
}
