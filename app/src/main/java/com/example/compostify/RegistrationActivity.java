package com.example.compostify;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.compostify.databinding.ActivityRegistrationBinding;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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

    LatLng latLng;

    Uri logoURI;

    private FirebaseAuth firebaseAuth;
    private String userID;

    private FirebaseFirestore firebaseFireStore;

    private final int PICK_IMAGE_REQUEST = 1;
    private String address;
    private String downloadUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityRegistrationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.progressBar.setVisibility(View.INVISIBLE);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFireStore = FirebaseFirestore.getInstance();

        Places.initialize(getApplicationContext(), "AIzaSyBTqCW_QQhBwo6hyVIsAGJ66jtZbtecyC0");

        applyAutoComplete();


        setListeners();


    }


    private void applyAutoComplete() {
        binding.edtStreetAddress.setFocusable(false);

    }

    private void setListeners() {
        binding.btnRegistration.setOnClickListener(this);
        binding.edtBusinessPhoneNumber.addTextChangedListener(new CanadianPhoneNumberTextWatcher(binding.edtBusinessPhoneNumber));
        binding.edtStreetAddress.setOnClickListener(this);
        binding.btnSelectImage.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (binding.btnRegistration.getId() == v.getId()) {
            getUserInput();


            if (hasFieldError()) {
                if (password.equals(confirmPassword)) {
                    binding.progressBar.setVisibility(View.VISIBLE);
                    uploadImage(logoURI);


                } else {
                    Toast.makeText(getApplicationContext(), "Password and Confirm Password dosen't Match", Toast.LENGTH_LONG).show();

                }
            }


        } else if (binding.edtStreetAddress.getId() == v.getId()) {
            List<Place.Field> fieldList = Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.NAME);
            Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fieldList).build(RegistrationActivity.this);
            startActivityForResult(intent, 100);
        } else if (binding.btnSelectImage.getId() == v.getId()) {
            openImageChooser();

        }

    }

    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Logo"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == RESULT_OK) {
            Place place = Autocomplete.getPlaceFromIntent(data);
            binding.edtStreetAddress.setText(place.getAddress());
            address = place.getAddress();
            latLng = place.getLatLng();


        } else if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            logoURI = data.getData();
            Glide.with(this).load(logoURI).into(binding.imgLogo);
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

        password = String.valueOf(binding.edtPassword.getText());
        confirmPassword = String.valueOf(binding.edtCPassword.getText());
    }

    //Adding image button


    public void createUser(Context context) {


        firebaseAuth.createUserWithEmailAndPassword(businessEmail, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            userID = firebaseAuth.getCurrentUser().getUid();


                            firebaseAuth.getCurrentUser().reload().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()) {


                                        DocumentReference documentReference = firebaseFireStore.collection("users").document(userID);
                                        Map<String, Object> newUser = new HashMap<>();
                                        newUser.put("userName", name);
                                        newUser.put("userEmail", email);
                                        newUser.put("businessName", businessName);
                                        newUser.put("businessEmail", businessEmail);
                                        newUser.put("businessContactNumber", contactNumber);
                                        newUser.put("address", address);
                                        newUser.put("unitNo", unitNo);
                                        newUser.put("latLng", latLng);
                                        newUser.put("downloadUrl", downloadUrl);
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
                                        Toast.makeText(getApplicationContext(), "Registration failed: " + task.getException().getCause(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }

                });


    }

    private void uploadImage(Uri imageUri) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        int len = logoURI.getLastPathSegment().split("/").length;
        String imageName = logoURI.getLastPathSegment().split("/")[len - 1];

        StorageReference imagesRef = storageRef.child("LogoImages/" + imageName);

        UploadTask uploadTask = imagesRef.putFile(imageUri);

        uploadTask.addOnSuccessListener(taskSnapshot -> {
            imagesRef.getDownloadUrl().addOnSuccessListener(uri -> {
                downloadUrl = uri.toString();
                Toast.makeText(RegistrationActivity.this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                createUser(RegistrationActivity.this);
            });
        }).addOnFailureListener(e ->
                Toast.makeText(RegistrationActivity.this, "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}