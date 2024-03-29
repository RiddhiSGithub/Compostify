package com.example.compostify;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.compostify.databinding.ActivityAboutUsBinding;

public class AboutUs extends AppCompatActivity {

    ActivityAboutUsBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAboutUsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}