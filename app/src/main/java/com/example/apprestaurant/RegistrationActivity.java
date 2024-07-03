package com.example.apprestaurant;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class RegistrationActivity extends AppCompatActivity {

    private EditText utilizator;
    private EditText parola;
    private BazaDeDate database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        utilizator = findViewById(R.id.utilizatorInregistrare);
        parola = findViewById(R.id.parolaInregistrare);

        database = new BazaDeDate(this);
    }

    public void registerUser(View view) {
        String username = utilizator.getText().toString().trim();
        String password = parola.getText().toString().trim();
        String rank = "User";

        Log.d("RegistrationActivity", "Registering user: " + username + ", Password: " + password);

        boolean inserted = database.insertUser(username, password, rank);

        if (username.length() > 3 && password.length() >= 6) {
            if (inserted) {
                Toast.makeText(this, "Utilizator înregistrat cu succes!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Înregistrarea a eșuat!", Toast.LENGTH_SHORT).show();
            }
        }
        if(username.length()<=3) Toast.makeText(this, "Numele de utilizator trebuie sa fie mai lung de 3 litere", Toast.LENGTH_SHORT).show();
        if(password.length()<6) Toast.makeText(this, "Parola trebuie sa fie minim 6 litere", Toast.LENGTH_SHORT).show();
    }
}
