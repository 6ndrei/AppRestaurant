package com.example.apprestaurant;

import android.content.Intent;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class Account extends Fragment {
    private Button logOutButton, BackToLogIn;
    private TextView nameShow;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private AnimationDrawable animationDrawable;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.activity_cont, container, false);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        nameShow = root.findViewById(R.id.AccWelcomeText);

        ConstraintLayout constraintLayout = root.findViewById(R.id.AccountActivity);
        ConstraintLayout UserConstraint = root.findViewById(R.id.UserConstraint);
        ConstraintLayout AnonymousConstraint = root.findViewById(R.id.AnonymousConstraint);
        animationDrawable = (AnimationDrawable) constraintLayout.getBackground();
        animationDrawable.setEnterFadeDuration(10);
        animationDrawable.setExitFadeDuration(2500);


        if (currentUser != null && currentUser.isAnonymous()) {
            AnonymousConstraint.setVisibility(View.VISIBLE);
            UserConstraint.setVisibility(View.GONE);
        } else {
            if (currentUser != null && !currentUser.isAnonymous())
                AnonymousConstraint.setVisibility(View.GONE);
            UserConstraint.setVisibility(View.VISIBLE);
        }

        BackToLogIn = root.findViewById(R.id.BackToLogIn);
        BackToLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        String userId = currentUser.getUid();
        DocumentReference userRef = db.collection("users").document(userId);

        userRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String name = documentSnapshot.getString("name");
                        if (name != null) {
                            nameShow.setText("Buna, " + name + "!");
                        } else {
                            nameShow.setText("Buna, utilizator");
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("FirestoreError", "Eroare la preluarea documentului", e);
                    nameShow.setText("Eroare la preluarea datelor");
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

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Pornește animația
        if (animationDrawable != null && !animationDrawable.isRunning()) {
            animationDrawable.start();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // Oprește animația
        if (animationDrawable != null && animationDrawable.isRunning()) {
            animationDrawable.stop();
        }
    }
}
