package com.example.compostify;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        // Get the ImageView
        ImageView logoImageView = findViewById(R.id.logoImageView);

        // Load animation
        Animation fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.splash_animation);

        // Set animation to ImageView
        logoImageView.startAnimation(fadeInAnimation);

        // Delay for 2 seconds and then start the MainActivity
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent mainIntent = new Intent(SplashScreenActivity.this, HomePageActivity.class);
                startActivity(mainIntent);
                finish();
            }
        }, 2000);  // Adjust the delay time as needed
    }
}