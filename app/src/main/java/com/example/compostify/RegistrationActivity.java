package com.example.compostify;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.compostify.databinding.ActivityRegistrationBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Console;
import java.util.HashMap;
import java.util.Map;

public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener {


    ActivityRegistrationBinding binding;

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://compostify-a5e7a-default-rtdb.firebaseio.com");


    String name;
    String email;
    String businessEmail;
    String businessName;

    String password;
    String confirmPassword;

    String contactNumber;

    String street;
    String unitNo;
    String city;
    String province;
    String postalCode;
    private FirebaseAuth firebaseAuth;
    private String userID;
    private DocumentReference documentReference;
    private FirebaseFirestore firebaseFireStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityRegistrationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.progressBar.setVisibility(View.INVISIBLE);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFireStore = FirebaseFirestore.getInstance();

        setListeners();
    }

    private void setListeners() {
        binding.btnRegistration.setOnClickListener(this);
        binding.edtBusinessPhoneNumber.addTextChangedListener(new CanadianPhoneNumberTextWatcher(binding.edtBusinessPhoneNumber));
    }

    @Override
    public void onClick(View v) {
        if (binding.btnRegistration.getId() == v.getId()) {


            getUserInput();


            if (hasFieldError())
                if (password.equals(confirmPassword)) {
                    binding.progressBar.setVisibility(View.VISIBLE);
                    createUser(getApplicationContext());

                } else {
                    Toast.makeText(getApplicationContext(), "Password and Confirm Password dosen't Match", Toast.LENGTH_LONG).show();

                }

        }
    }

    private boolean hasFieldError() {
        if (TextUtils.isEmpty(name)) {
            binding.edtUserName.setError("Name Cannot be empty");
            return false;
        } else if (TextUtils.isEmpty(email)) {
            binding.edtUserEmail.setError("Email Cannot be empty");
            return false;
        } else if (TextUtils.isEmpty(businessName)) {
            binding.edtBusinessName.setError("Business Name Cannot be empty");
            return false;
        } else if (TextUtils.isEmpty(businessEmail)) {
            binding.edtBusinessEmail.setError("Business Email Cannot be empty");
            return false;
        } else if (TextUtils.isEmpty(password)) {
            binding.edtPassword.setError("Password Cannot be empty");
            return false;
        } else if (TextUtils.isEmpty(contactNumber)) {
            binding.edtBusinessPhoneNumber.setError("Contact Cannot be empty");
            return false;
        } else if (TextUtils.isEmpty(street)) {
            binding.edtStreetAddress.setError("Street Name Cannot be empty");
            return false;
        } else if (TextUtils.isEmpty(city)) {
            binding.edtCity.setError("City Cannot be empty");
            return false;
        } else if (TextUtils.isEmpty(province)) {
            binding.edtProvince.setError("Province Cannot be empty");
            return false;
        } else if (TextUtils.isEmpty(city)) {
            binding.edtPostalCode.setError("Postal Code Cannot be empty");
            return false;
        }


        return true;

    }


    private void clearAll() {
        binding.edtUserName.setText(null);
        binding.edtUserEmail.setText(null);
        binding.edtBusinessName.setText(null);
        binding.edtBusinessEmail.setText(null);
        binding.edtPassword.setText(null);
        binding.edtCPassword.setText(null);
        binding.edtBusinessPhoneNumber.setText(null);
        binding.edtPostalCode.setText(null);
        binding.edtProvince.setText(null);
        binding.edtCity.setText(null);
        binding.edtStreetAddress.setText(null);
        binding.edtUnitNumber.setText(null);
    }

    private void getUserInput() {
        name = String.valueOf(binding.edtUserName.getText());
        email = String.valueOf(binding.edtUserEmail.getText());
        businessEmail = String.valueOf(binding.edtBusinessEmail.getText());
        businessName = String.valueOf(binding.edtBusinessName.getText());
        contactNumber = String.valueOf(binding.edtBusinessPhoneNumber.getText());
        street = String.valueOf(binding.edtStreetAddress.getText());
        unitNo = String.valueOf(binding.edtUnitNumber.getText());
        city = String.valueOf(binding.edtCity.getText());
        postalCode = String.valueOf(binding.edtPostalCode.getText());
        province = String.valueOf(binding.edtProvince.getText());
        password = String.valueOf(binding.edtPassword.getText());
        confirmPassword = String.valueOf(binding.edtCPassword.getText());
    }

    public void createUser(Context context) {


        firebaseAuth.createUserWithEmailAndPassword(businessEmail, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            userID = firebaseAuth.getCurrentUser().getUid();
                            DocumentReference documentReference = firebaseFireStore.collection("users").document(userID);
                            Map<String, Object> newUser = new HashMap<>();
                            newUser.put("userName", name);
                            newUser.put("userEmail", email);
                            newUser.put("businessName", businessName);
                            newUser.put("businessEmail", businessEmail);
                            newUser.put("businessContactNumber", contactNumber);
                            newUser.put("street", street);
                            newUser.put("unitNo", unitNo);
                            newUser.put("city", city);
                            newUser.put("province", province);
                            newUser.put("postalCode", postalCode);
                            documentReference.set(newUser)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(context, "You are registered Successfully", Toast.LENGTH_LONG).show();
                                            clearAll();
                                            startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
                                            finish();
                                        }
                                    });
                        } else {
                            // If sign in fails, display a message to the user.
                            binding.progressBar.setVisibility(View.GONE);
                            Log.w("createUser", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(context, "Registration failed: " + task.getException().getCause(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }
}