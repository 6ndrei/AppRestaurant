package com.example.apprestaurant;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameLogin;
    private EditText passwordLogin;
    private Button btnLogin;
    private TextView Register;
    private TextView ContinueAsGuest;
    private FirebaseAuth mAuth;
    private SharedPreferences sharedPreferences;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        findViewById(R.id.loadingPanel).setVisibility(View.GONE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        db = FirebaseFirestore.getInstance();

        sharedPreferences = getSharedPreferences("Table_Session", Context.MODE_PRIVATE);
        clearSharedPreferences();

        if (currentUser != null) {
            if (currentUser.isAnonymous()) {
                String userId = currentUser.getUid();
                currentUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("LoginActivity", "contul anonim a fost sters cu succes");
                            db.collection("users").document(userId)
                                    .delete()
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d("LoginActivity", "documentul a fost sters din firestore");
                                        mAuth.signOut();
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("LoginActivity", "documentul nu s-a putut sterge din firestore", e);
                                    });
                        } else {
                            Log.e("LoginActivity", "nu s-a putut sterge", task.getException());
                        }
                    }
                });
            } else {
                navigateToMainActivity();
            }
        }

        usernameLogin = findViewById(R.id.username);
        passwordLogin = findViewById(R.id.password);
        btnLogin = findViewById(R.id.btn_login);
        Register = findViewById(R.id.Register);
        ContinueAsGuest = findViewById(R.id.ContinueAsGuest);

        if (!isNetworkConnected()) {
            showNoInternetDialog();
        }

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameLogin.getText().toString();
                String password = passwordLogin.getText().toString();
                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Introdu atât numele, cât și parola", Toast.LENGTH_SHORT).show();
                } else {
                    checkInternetConnection(username, password);
                }
            }
        });

        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRegistrationActivity();
            }
        });

        ContinueAsGuest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginAsGuest();
            }
        });
    }

    private void clearSharedPreferences() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    private void loginUser(String username, String password) {
        mAuth.signInWithEmailAndPassword(username, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            navigateToMainActivity();
                        } else {
                            Toast.makeText(LoginActivity.this, "Nume sau parolă greșită", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void loginAsGuest() {
        mAuth.signInAnonymously()
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            assignRoleToAnonymousUser();
                        } else {
                            Toast.makeText(LoginActivity.this, "Eroare la autentificarea ca vizitator", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void assignRoleToAnonymousUser() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DocumentReference userRef = db.collection("users").document(userId);

            userRef.get().addOnSuccessListener(documentSnapshot -> {
                if (!documentSnapshot.exists()) {
                    // Setăm rolul utilizatorului anonim la "User"
                    Map<String, Object> user = new HashMap<>();
                    user.put("role", "User");

                    userRef.set(user).addOnSuccessListener(aVoid -> {
                        navigateToMainActivity();
                    }).addOnFailureListener(e -> {
                        Toast.makeText(LoginActivity.this, "Eroare la setarea rolului utilizatorului anonim", Toast.LENGTH_SHORT).show();
                        Log.e("LoginActivity", "Error setting role for anonymous user", e);
                    });
                } else {
                    String role = documentSnapshot.getString("role");
                    if (role == null) {
                        userRef.update("role", "User").addOnSuccessListener(aVoid -> {
                            navigateToMainActivity();
                        }).addOnFailureListener(e -> {
                            Toast.makeText(LoginActivity.this, "Eroare la actualizarea rolului utilizatorului anonim", Toast.LENGTH_SHORT).show();
                            Log.e("LoginActivity", "Error updating role for anonymous user", e);
                        });
                    } else {
                        navigateToMainActivity();
                    }
                }
            }).addOnFailureListener(e -> {
                Toast.makeText(LoginActivity.this, "Eroare la verificarea utilizatorului anonim în Firestore", Toast.LENGTH_SHORT).show();
                Log.e("LoginActivity", "Error fetching anonymous user from Firestore", e);
            });
        }
    }

    private void navigateToMainActivity() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        findViewById(R.id.cardView).setVisibility(View.GONE);
        findViewById(R.id.Register).setVisibility(View.GONE);
        findViewById(R.id.ContinueAsGuest).setVisibility(View.GONE);
        findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DocumentReference userRef = db.collection("users").document(userId);

            userRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    String role = documentSnapshot.getString("role");

                    if (role != null) {
                        if (role.equals("admin")) {
                            Intent adminIntent = new Intent(LoginActivity.this, AdminActivity.class);
                            startActivity(adminIntent);
                        } else if (role.equals("employee")) {
                            Intent employeeIntent = new Intent(LoginActivity.this, OrdersActivity.class);
                            startActivity(employeeIntent);
                        } else {
                            Intent defaultIntent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(defaultIntent);
                        }
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "Rolul utilizatorului nu este definit", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Documentul utilizatorului nu există", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(e -> {
                Toast.makeText(LoginActivity.this, "Eroare la citirea rolului utilizatorului din Firestore", Toast.LENGTH_SHORT).show();
                Log.e("LoginActivity", "Error fetching user role from Firestore", e);
            });
        }
    }



    public void openRegistrationActivity() {
        Intent intent = new Intent(this, RegistrationActivity.class);
        startActivity(intent);
    }

    private void checkInternetConnection(final String username, final String password) {
        if (!isNetworkConnected()) {
            showNoInternetDialog();
        } else {
            loginUser(username, password);
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        }
        return false;
    }

    private void showNoInternetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Nu există conexiune la internet. Verificați setările rețelei.")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (isNetworkConnected()) {
                            String username = usernameLogin.getText().toString();
                            String password = passwordLogin.getText().toString();
                            loginUser(username, password);
                        } else {
                            showNoInternetDialog();
                        }
                    }
                })
                .setNegativeButton("Anulare", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
