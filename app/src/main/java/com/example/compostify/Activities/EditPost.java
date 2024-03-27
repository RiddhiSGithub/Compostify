package com.example.compostify.Activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.compostify.R;
import com.example.compostify.databinding.ActivityEditPostBinding;

public class EditPost extends AppCompatActivity {

    ActivityEditPostBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post);
        binding = ActivityEditPostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}