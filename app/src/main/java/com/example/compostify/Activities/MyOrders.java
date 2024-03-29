package com.example.compostify.Activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.compostify.databinding.ActivityMyOrdersBinding;

public class MyOrders extends AppCompatActivity {

    ActivityMyOrdersBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyOrdersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}