package com.example.compostify;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.compostify.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity
implements View.OnClickListener {

    ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setListeners();

    }

    private void setListeners() {
        binding.btnSignUp.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(binding.btnSignUp.getId() == v.getId())
        {
            startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
            finish();
        }
    }
}