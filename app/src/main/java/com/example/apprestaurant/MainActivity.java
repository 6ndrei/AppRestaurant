package com.example.apprestaurant;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.apprestaurant.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;


public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private Button LogOutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        findViewById(R.id.ScaneazaButon).setOnClickListener(view -> startQRScanner());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_acasa, R.id.navigation_catalog, R.id.navigation_cont)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        View navHostFragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main).getView();
    }

    private void startQRScanner() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setPrompt("");
        integrator.setOrientationLocked(false); // Permite orientarea ecranului în mod liber
        integrator.initiateScan(); // Inițiază scanarea codului QR
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Scanare anulată", Toast.LENGTH_SHORT).show();
            } else {
                if (result.getFormatName().equals("QR_CODE")) {
                    String qrContent = result.getContents();
                    Toast.makeText(this, "Cod QR scanat: " + qrContent, Toast.LENGTH_SHORT).show();
                    // Poți face orice dorești cu rezultatul QR aici
                } else {
                    Toast.makeText(this, "Codul scanat nu este un QR", Toast.LENGTH_SHORT).show();
                    // Acțiuni pentru alte tipuri de coduri de bare sau gestiune alternativă
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
