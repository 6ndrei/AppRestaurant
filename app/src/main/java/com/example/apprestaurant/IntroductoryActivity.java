package com.example.apprestaurant;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;

public class IntroductoryActivity extends AppCompatActivity {

    ImageView logo, splashImg;
    LottieAnimationView lottieAnimationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_introductory);

        logo = findViewById(R.id.intrologo);
        lottieAnimationView = findViewById(R.id.lottie);

        int animationDuration = 500;
        int startDelay = 4000;

        logo.animate().translationY(-3400).setDuration(animationDuration)
                .setStartDelay(startDelay);
        lottieAnimationView.animate().translationY(3400).setDuration(animationDuration)
                .setStartDelay(startDelay).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(IntroductoryActivity.this, LoginActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        finish();
                    }
                });
    }

}
