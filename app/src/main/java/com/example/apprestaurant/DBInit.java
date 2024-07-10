package com.example.apprestaurant;

import android.app.Application;

import com.google.firebase.FirebaseApp;

public class DBInit extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
    }
}
