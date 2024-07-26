package com.example.apprestaurant;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apprestaurant.adapters.ItemCategAdapter;
import com.example.apprestaurant.models.ItemCategModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.Query;


import java.util.ArrayList;
import java.util.List;

public class Account extends Fragment {

    private Button logOutButton, backToLogIn;
    private TextView nameShow;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private AnimationDrawable animationDrawable;
    private RecyclerView recyclerView;
    private ItemCategAdapter adapter;
    private List<ItemCategModel> itemList;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.activity_cont, container, false);
//
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        nameShow = root.findViewById(R.id.AccWelcomeText);
        recyclerView = root.findViewById(R.id.LastOrderRec);

        ConstraintLayout constraintLayout = root.findViewById(R.id.AccountActivity);
        ConstraintLayout userConstraint = root.findViewById(R.id.UserConstraint);
        ConstraintLayout anonymousConstraint = root.findViewById(R.id.AnonymousConstraint);
        animationDrawable = (AnimationDrawable) constraintLayout.getBackground();
        animationDrawable.setEnterFadeDuration(10);
        animationDrawable.setExitFadeDuration(2500);

        String userId = currentUser != null ? currentUser.getUid() : "";
        String email = currentUser.getEmail();
        SharedPreferences sharedPref = requireContext().getSharedPreferences("user_name", Context.MODE_PRIVATE);
        String userName = sharedPref.getString("userName", "Default Name");

        nameShow.setText("Buna, " + userName + "!");

        if (currentUser != null && currentUser.isAnonymous()) {
            anonymousConstraint.setVisibility(View.VISIBLE);
            userConstraint.setVisibility(View.GONE);
        } else {
            if (currentUser != null && !currentUser.isAnonymous())
                anonymousConstraint.setVisibility(View.GONE);
            userConstraint.setVisibility(View.VISIBLE);
        }

        backToLogIn = root.findViewById(R.id.BackToLogIn);
        backToLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        logOutButton = root.findViewById(R.id.LogOutButton);
        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("AccountFragment", "onClick: Logout button clicked");
                mAuth.signOut();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
        itemList = new ArrayList<>();
        adapter = new ItemCategAdapter(getContext(), itemList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        fetchLastOrders(email);

        return root;
    }

    private void fetchLastOrders(String userId) {
        db.collection("orders_history")
                .whereEqualTo("user", userId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(5)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        itemList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String items = document.getString("items");
                            String[] itemNames = items.split(",");

                            for (String itemName : itemNames) {
                                if (!itemName.trim().isEmpty()) {
                                    fetchItemDetails(itemName.trim());
                                }
                            }
                        }
                    } else {
                        Log.d("AccountFragment", "Error fetching orders: ", task.getException());
                    }
                });
    }

    private void fetchItemDetails(String itemName) {
        db.collection("items")
                .whereEqualTo("name", itemName)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            ItemCategModel item = document.toObject(ItemCategModel.class);
                            itemList.add(item);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Log.d("AccountFragment", "Error getting item details: ", task.getException());
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (animationDrawable != null && !animationDrawable.isRunning()) {
            animationDrawable.start();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (animationDrawable != null && animationDrawable.isRunning()) {
            animationDrawable.stop();
        }
    }
}
