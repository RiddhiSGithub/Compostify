package com.example.compostify;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.compostify.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity
        implements View.OnClickListener {

    ActivityLoginBinding binding;
    FirebaseAuth firebaseAuth;

    String email = null;
    String password = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        firebaseAuth = FirebaseAuth.getInstance();
        setListeners();

    }

    private void setListeners() {
        binding.btnSignUp.setOnClickListener(this);
        binding.btnLogIn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (binding.btnSignUp.getId() == v.getId()) {
            startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
            finish();
        } else if (binding.btnLogIn.getId() == v.getId()) {
            email = String.valueOf(binding.edtUserEmail.getText());
            password = String.valueOf(binding.edtPassword.getText());
            if (hasFieldError()) {

                firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener((task) -> {

                    if (task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "Logged In Successfully", Toast.LENGTH_SHORT).show();
                        
                        startActivity(new Intent(LoginActivity.this, HomePageActivity.class));
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "Invalid email address or password!", Toast.LENGTH_SHORT).show();
                    }

                });
            }

        }
    }

    private boolean hasFieldError() {
        if (TextUtils.isEmpty(email)) {
            binding.edtUserEmail.setError("Email cannot be empty");
            return false;
        } else if (TextUtils.isEmpty(password)) {
            binding.edtPassword.setError("Password cannot be empty");
            return false;
        }
        return true;
    }
}