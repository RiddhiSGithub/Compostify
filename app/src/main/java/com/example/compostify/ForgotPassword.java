package com.example.compostify;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.example.compostify.databinding.ActivityForgotPasswordBinding;

public class ForgotPassword extends AppCompatActivity implements View.OnClickListener {

    ActivityForgotPasswordBinding forgotPasswordBinding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        forgotPasswordBinding= ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        View view = forgotPasswordBinding.getRoot();
        setContentView(view);
        initForgotPassword();
    }

    private void initForgotPassword() {
        forgotPasswordBinding.btnChangePassword.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == forgotPasswordBinding.btnChangePassword.getId()){
            if (validateInput()){
                resetPassword();
            }
        }

    }

    private void resetPassword() {
        String email = forgotPasswordBinding.edtEmail.getText().toString();
    }

    private boolean validateInput() {
        String email = forgotPasswordBinding.edtEmail.getText().toString();

        if (email.isEmpty()) {
            forgotPasswordBinding.txtLayoutEmail.setError("Enter an email address.");
            return false;
        }

        return true;
    }

}