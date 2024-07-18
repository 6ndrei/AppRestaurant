package com.example.apprestaurant;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.apprestaurant.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegistrationActivity extends AppCompatActivity {

    private EditText usernameRegister;
    private EditText passwordRegister;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        FirebaseApp.initializeApp(this);

        usernameRegister = findViewById(R.id.RegisterUsername);
        passwordRegister = findViewById(R.id.RegisterPassword);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
    }

    public void registerUser(View view) {
        String username = usernameRegister.getText().toString().trim();
        String password = passwordRegister.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Introdu numele de utilizator și parola", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(username, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        // Adăugăm utilizatorul în Firestore și îi atribuim un rol
                        addUserToFirestore(user.getUid(), username, "user");
                    } else {
                        Toast.makeText(RegistrationActivity.this, "Înregistrare eșuată", Toast.LENGTH_SHORT).show();
                        Log.e("RegistrationActivity", "Error creating user", task.getException());
                    }
                });
    }

    private void addUserToFirestore(String userId, String username, String role) {
        Map<String, Object> user = new HashMap<>();
        user.put("username", username);
        user.put("role", role); // Adăugăm câmpul pentru rol

        db.collection("users").document(userId)
                .set(user)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(RegistrationActivity.this, "Utilizator înregistrat cu succes!", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(RegistrationActivity.this, "Înregistrarea a eșuat!", Toast.LENGTH_SHORT).show();
                            Log.e("RegistrationActivity", "Error adding user to Firestore", task.getException());
                        }
                    }
                });
    }
}
