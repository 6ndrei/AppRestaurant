package com.example.apprestaurant;

import android.util.Log;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

public class DataBase {

    private static final String TAG = "DataBase";
    private FirebaseFirestore db;
    private CollectionReference userCollection;

    public DataBase() {
        db = FirebaseFirestore.getInstance();
        userCollection = db.collection("users");
    }

    public void insertUser(String username, String password, String rank, String table, OnCompleteListener<Void> listener) {
        Map<String, Object> user = new HashMap<>();
        user.put("username", username);
        user.put("password", password);
        user.put("rank", rank);
        user.put("table", table);

        userCollection.document(username).set(user)
                .addOnCompleteListener(listener)
                .addOnFailureListener(e -> Log.e(TAG, "Error inserting user", e));
    }

    public void isUsernameExists(String username, OnCompleteListener<DocumentSnapshot> listener) {
        userCollection.document(username).get()
                .addOnCompleteListener(listener)
                .addOnFailureListener(e -> Log.e(TAG, "Error checking if username exists", e));
    }

    public void checkUser(String username, String password, OnCompleteListener<QuerySnapshot> listener) {
        userCollection.whereEqualTo("username", username)
                .whereEqualTo("password", password)
                .get()
                .addOnCompleteListener(listener)
                .addOnFailureListener(e -> Log.e(TAG, "Error checking user", e));
    }
}
