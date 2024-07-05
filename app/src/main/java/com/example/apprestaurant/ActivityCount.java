package com.example.apprestaurant;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

public class ActivityCount extends Application implements Application.ActivityLifecycleCallbacks {

    private int activityCount = 0; // To keep track of the number of activities
    private SharedPreferences sharedPreferences;

    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(this);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("Table_Session", Context.MODE_PRIVATE);
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        // Increment activity count when a new activity is created
        activityCount++;
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        // Decrement activity count when an activity is destroyed
        activityCount--;

        // Check if no activities are left (indicating the application is closing)
        if (activityCount == 0) {
            // Set qrContent to "0" when the app is closing
            setQrContent("0");
        }
    }

    private void setQrContent(String content) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("qrContent", content);
        editor.apply();
    }

    @Override
    public void onActivityStarted(Activity activity) {}

    @Override
    public void onActivityResumed(Activity activity) {}

    @Override
    public void onActivityPaused(Activity activity) {}

    @Override
    public void onActivityStopped(Activity activity) {}

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {}
}
