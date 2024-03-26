package com.example.compostify;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.compostify.databinding.ActivityLoginBinding;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

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


                        startActivity(new Intent(LoginActivity.this, HomePageActivity.class));
                        finish();
                    } else {
                        try {
                            throw task.getException();
                        } catch (FirebaseAuthInvalidUserException invalidUserException) {
                            createAlertErrorBox("Hmm... We couldn't find an account with that email.", LoginActivity.this);
                        } catch (FirebaseAuthInvalidCredentialsException invalidCredentialsException) {
                            createAlertErrorBox("Oops! It seems like the username or password you entered is incorrect", LoginActivity.this);
                        } catch (FirebaseNetworkException networkException) {
                            createAlertErrorBox("Uh-oh! It looks like there's a problem with your internet connection", LoginActivity.this);
                        } catch (Exception e) {
                            createAlertErrorBox("Oops! Something went wrong. Please try again later.", LoginActivity.this);
                        }
                    }

                });

            }

        }
    }

    private void createAlertErrorBox(String errorMessage, LoginActivity loginActivity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setMessage(errorMessage)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK button
                        dialog.dismiss(); // Dismiss the dialog
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
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