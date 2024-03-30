package com.example.compostify;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
        binding.txtForgotPassword.setOnClickListener(this);
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

        }else if(binding.txtForgotPassword.getId() == v.getId()){
            showForgotPasswordDialog();
        }

    }
    private void showForgotPasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.forget_password_dialog, null);
        builder.setView(dialogView);

        EditText emailEditText = dialogView.findViewById(R.id.emailEditText);
        Button resetButton = dialogView.findViewById(R.id.resetButton);
        Button cancelButton = dialogView.findViewById(R.id.cancelButton);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        resetButton.setOnClickListener(view -> {
            String email = emailEditText.getText().toString().trim();
            if (!TextUtils.isEmpty(email)) {
                FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(this, "Password reset email sent!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(this, "Failed to send reset email. Please check your email address.", Toast.LENGTH_SHORT).show();
                            }
                            alertDialog.dismiss();
                        });
            } else {
                Toast.makeText(this, "Please enter your email address.", Toast.LENGTH_SHORT).show();
            }
        });

        cancelButton.setOnClickListener(view -> alertDialog.dismiss());
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