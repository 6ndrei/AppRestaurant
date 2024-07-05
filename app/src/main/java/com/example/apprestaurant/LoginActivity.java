package com.example.apprestaurant;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameLogin;
    private EditText passwordLogin;
    private Button btnLogin;
    private DataBase BD;
    private TextView Register;
    private TextView ContinueAsGuest;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
     /*   getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            getWindow().getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        } */

        // Set fullscreen flag
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

// Check if the device is running on Android P or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            // Set layout in display cutout mode to LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            WindowManager.LayoutParams attributes = getWindow().getAttributes();
            attributes.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            getWindow().setAttributes(attributes);
        }

// Ensure that the navigation bar (bottom bar) is not hidden
        View decorView = getWindow().getDecorView();
        int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
        decorView.setSystemUiVisibility(flags);

        BD = new DataBase(this);
        BD.insertUser("test", "test2", "admin", "0");
        BD.insertUser("andrei2","andrei2","aa","0");

        usernameLogin = findViewById(R.id.username);
        passwordLogin = findViewById(R.id.password);
        btnLogin = findViewById(R.id.btn_login);
        Register = findViewById(R.id.Register);
        ContinueAsGuest = findViewById(R.id.ContinueAsGuest);


        SharedPreferences preferences = getSharedPreferences("user_session", MODE_PRIVATE);
        if (preferences.getBoolean("isLoggedIn", false)) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameLogin.getText().toString();
                String password = passwordLogin.getText().toString();
                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Introdu atat numele, cat si parola", Toast.LENGTH_SHORT).show();
                } else {
                    if (BD.checkUser(username, password)) {
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putBoolean("isLoggedIn", true);
                        editor.putString("username", username);
                        editor.apply();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "Nume sau password gresita", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
//.
        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRegistrationActivity();
            }
        });
        ContinueAsGuest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String guestUsername = "Vizitator";
                String guestPassword = "GuestPassword";
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra("username", guestUsername);
                intent.putExtra("password", guestPassword);
                startActivity(intent);
                finish();
            }
        });

    }
    public void openRegistrationActivity() {
        Intent intent = new Intent(this, RegistrationActivity.class);
        startActivity(intent);
    }
    }

