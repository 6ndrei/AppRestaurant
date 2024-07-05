package com.example.apprestaurant;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class RegistrationActivity extends AppCompatActivity {

    private EditText usernameRegister;
    private EditText passwordRegister;
    private DataBase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        usernameRegister = findViewById(R.id.RegisterUsername);
        passwordRegister = findViewById(R.id.RegisterPassword);

        database = new DataBase(this);
    }

    public void registerUser(View view) {
        String username = usernameRegister.getText().toString().trim();
        String password = passwordRegister.getText().toString().trim();
        String rank = "User";
        String taable = "0";
//.
        Log.d("RegistrationActivity", "Registering user: " + username + ", Password: " + password);

        if(username.length()<=3) { Toast.makeText(this, "Numele de utilizator trebuie sa fie mai lung de 3 litere", Toast.LENGTH_SHORT).show();
            return; }
        if(password.length()<6) { Toast.makeText(this, "Parola trebuie sa fie minim 6 litere", Toast.LENGTH_SHORT).show();
            return; }
        if(database.isUsernameExists(username)) { Toast.makeText(this, "Numele de utilizator există deja", Toast.LENGTH_SHORT).show();
            return; }

        boolean inserted = database.insertUser(username, password, rank, taable);
            if (inserted) {
                Toast.makeText(this, "Utilizator înregistrat cu succes!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Înregistrarea a eșuat!", Toast.LENGTH_SHORT).show();
                Log.e("Registration", "Failed to insert user into database.");
            }
    }
}
