package com.example.compostify.Activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.compostify.databinding.ActivityMyOrderDetailsBinding;

public class MyOrderDetails extends AppCompatActivity {
    ActivityMyOrderDetailsBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyOrderDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}