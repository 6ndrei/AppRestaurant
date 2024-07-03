package com.example.apprestaurant;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText utilizator;
    private EditText parola;
    private Button btnLogin;
    private BazaDeDate BD;
    private TextView Inregistrare;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        BD = new BazaDeDate(this);
        BD.insertUser("test", "test2", "admin");

        utilizator = findViewById(R.id.utilizator);
        parola = findViewById(R.id.parola);
        btnLogin = findViewById(R.id.btn_login);
        Inregistrare = findViewById(R.id.Inregistrare);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = utilizator.getText().toString();
                String password = parola.getText().toString();

                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Introdu atat numele, cat si parola", Toast.LENGTH_SHORT).show();
                } else {
                    if (BD.checkUser(username, password)) {
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "Nume sau parola gresita", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        Inregistrare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRegistrationActivity();
            }
        });

    }
    public void openRegistrationActivity() {
        Intent intent = new Intent(this, RegistrationActivity.class);
        startActivity(intent);
    }
    }

