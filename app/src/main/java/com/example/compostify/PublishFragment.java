package com.example.compostify;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.compostify.databinding.FragmentPublishBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PublishFragment extends Fragment {

    private String userId;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private FirebaseStorage storage;
    private StorageReference storageReference;

    private static final int REQUEST_IMAGE_PICK = 1;

    private FragmentPublishBinding binding;
    private AutoCompleteTextView edtTypeOfUser;
    private AutoCompleteTextView edtTypeOfWaste;
    private TextInputEditText edtQuantity;
    private TextInputEditText edtWeight;
    private TextInputEditText edtPrice;
    private TextInputEditText edtOtherDetails;
    private TextInputEditText edtBuildingNumber;
    private TextInputEditText edtBuildingName;
    private TextInputEditText edtCity;
    private TextInputEditText edtProvince;
    private TextInputEditText edtPostalCode;
    private String imageUrl; // Declare imageUrl variable

    private TextInputLayout txtLayQuantity;
    private TextInputLayout txtLayWeight;
    private TextInputLayout txtLayPrice;
    private TextInputLayout txtLayBuildingNumber;
    private TextInputLayout txtLayBuildingName;
    private TextInputLayout txtLayCity;
    private TextInputLayout txtLayProvince;
    private TextInputLayout txtLayPostalCode;

    public PublishFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPublishBinding.inflate(inflater, container, false);  // Initialize in onCreateView


        firebaseAuth = FirebaseAuth.getInstance(); //Initialize firebaseAuth
        firebaseFirestore = FirebaseFirestore.getInstance(); //Initialize firebaseFirestore
        storage = FirebaseStorage.getInstance(); // Initialize FirebaseStorage
        storageReference = storage.getReference(); // Initialize storageReference


        edtTypeOfUser = binding.edtTypeOfUser;
        edtTypeOfWaste = binding.edtTypeOfWaste;
        edtQuantity = binding.edtQuantity;
        edtWeight = binding.edtWeight;
        edtPrice = binding.edtPrice;
        edtOtherDetails = binding.edtOtherDetails;
        edtBuildingNumber = binding.edtBuildingNumber;
        edtBuildingName = binding.edtBuildingName;
        edtCity = binding.edtCity;
        edtProvince = binding.edtProvince;
        edtPostalCode = binding.edtPostalCode;

        txtLayQuantity = binding.txtLayQuantity;
        txtLayWeight = binding.txtLayWeight;
        txtLayPrice = binding.txtLayPrice;
        txtLayBuildingNumber = binding.txtLayBuildingNumber;
        txtLayBuildingNumber = binding.txtLayBuildingName;
        txtLayCity = binding.txtLayCity;
        txtLayProvince = binding.txtLayProvince;
        txtLayPostalCode = binding.txtLayPostalCode;



        //Price will only show when type of user will be Seller
        txtLayPrice.setVisibility(View.GONE);

        //Fill data which were needed
        fillData();
        // Set up AutoCompleteTextView with predefined options
        ArrayAdapter<String> wasteAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                new String[]{"Natural Waste", "Mix Waste", "Both"}
        );
        edtTypeOfWaste.setAdapter(wasteAdapter);

        ArrayAdapter<String> userAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                new String[]{"Buyer(Fertilizer Companies, etc.)", "Seller(Restaurants, cafe, Super Markets, etc.)"}
        );
        edtTypeOfUser.setAdapter(userAdapter);


        // Add TextWatcher to edtTypeOfUser
        edtTypeOfUser.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int before, int count) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Check the selected type of user
                String userType = editable.toString();
                if ("Seller(Restaurants, cafe, Super Markets, etc.)".equals(userType)) {
                    // Show the "Price" field
                    txtLayPrice.setVisibility(View.VISIBLE);
                } else {
                    // Hide the "Price" field
                    txtLayPrice.setVisibility(View.GONE);
                    // Clear the "Price" field value
                    edtPrice.getText().clear();
                }
            }
        });

        Button btnUploadPhotos = binding.btnUploadPhotos;
        btnUploadPhotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Launch intent to select an image
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_IMAGE_PICK);
            }
        });

        Button btnPublish = binding.btnPost;
        btnPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadPostDetailsToFirestore(imageUrl);
            }
        });

        return binding.getRoot();  // Make sure to return the root view
    }

    // Override onActivityResult() method to handle image selection result
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Inside onActivityResult()
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImageUri = data.getData();
            // Upload the selected image to Firebase Storage
            uploadImageToFirebase(selectedImageUri);
        }
    }

    // Inside PublishFragment class
    private void uploadImageToFirebase(Uri imageUri) {
        if (imageUri != null) {
            StorageReference photoRef = storageReference.child("images/" + UUID.randomUUID().toString());
            photoRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        // Image uploaded successfully
                        // Get the download URL
                        photoRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            // Handle the download URL
                            imageUrl = uri.toString(); // Assign the download URL to imageUrl variable
                            // After obtaining the download URL, call the method to upload post details to Firestore
                            uploadPostDetailsToFirestore(imageUrl);
                        });
                    })
                    .addOnFailureListener(e -> {
                        // Handle unsuccessful upload
                        Toast.makeText(requireContext(), "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(requireContext(), "No image selected", Toast.LENGTH_SHORT).show();
        }
    }
    // Upload other post details along with image URL to Firestore
    private void uploadPostDetailsToFirestore(String imageUrl) {
        if (validateInputs()) {
            // Get logged-in user details
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

            if (currentUser != null) {
                // Get user ID and email
                String userId = currentUser.getUid();
                String userEmail = currentUser.getEmail();

                // Retrieve other details from EditText fields
                String typeOfUser = edtTypeOfUser.getText().toString();
                String typeOfWaste = edtTypeOfWaste.getText().toString();
                String quantity = edtQuantity.getText().toString();
                String weight = edtWeight.getText().toString();
                String otherDetails = edtOtherDetails.getText().toString();
                String buildingNumber = edtBuildingNumber.getText().toString();
                String buildingName = edtBuildingName.getText().toString();
                String city = edtCity.getText().toString();
                String province = edtProvince.getText().toString();
                String postalCode = edtPostalCode.getText().toString();

                // Create a map to store post details
                Map<String, Object> postDetails = new HashMap<>();
                postDetails.put("userId", userId);
                postDetails.put("userEmail", userEmail);
                postDetails.put("typeOfUser", typeOfUser);
                postDetails.put("typeOfWaste", typeOfWaste);
                postDetails.put("quantity", quantity);
                postDetails.put("weight", weight);
                postDetails.put("otherDetails", otherDetails);
                postDetails.put("building No,", buildingNumber);
                postDetails.put("building Name,", buildingName);
                postDetails.put("city", city);
                postDetails.put("province", province);
                postDetails.put("postalCode", postalCode);
                postDetails.put("imageUrl", imageUrl); // Add image URL to post details

                // Add current date and time
                postDetails.put("postDateTime", FieldValue.serverTimestamp());

                // Now, you can add this postDetails map to your Firebase Firestore
                // For example, assuming you have a "posts" collection in Firestore
                FirebaseFirestore.getInstance().collection("Publish")
                        .add(postDetails)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Toast.makeText(requireContext(), "Post published successfully", Toast.LENGTH_SHORT).show();
                                clearFormFields();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(requireContext(), "Failed to publish post", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }
    }

    private void fillData() {
        userId = firebaseAuth.getCurrentUser().getUid();
        DocumentReference documentReference = firebaseFirestore.collection("users").document(userId);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                binding.edtBuildingName.setText(value.getString("street"));
                binding.edtBuildingNumber.setText(value.getString("unitNo"));
                binding.edtCity.setText(value.getString("city"));
                binding.edtProvince.setText(value.getString("province"));
                binding.edtPostalCode.setText(value.getString("postalCode"));
            }
        });
    }


    private boolean validateInputs() {
        boolean isValid = true;

        // Validation for Type of User
        if (edtTypeOfUser.getText().toString().isEmpty()) {
            edtTypeOfUser.setError("Type of User is required");
            isValid = false;
        }

        // Validation for Type of Waste
        if (edtTypeOfWaste.getText().toString().isEmpty()) {
            edtTypeOfWaste.setError("Type of Waste is required");
            isValid = false;
        }

        // Validation for Quantity
        String quantity = edtQuantity.getText().toString();
        if (quantity.isEmpty()) {
            txtLayQuantity.setError("Quantity is required");
            isValid = false;
        } else {
            txtLayQuantity.setError(null);
        }

        // Validation for Weight
        String weight = edtWeight.getText().toString();
        if (weight.isEmpty()) {
            txtLayWeight.setError("Weight is required");
            isValid = false;
        } else {
            txtLayWeight.setError(null);
        }

        // Validation for Price (only if the user is a seller)
        String userType = edtTypeOfUser.getText().toString();
        if ("Seller(Restaurants, cafe, Super Markets, etc.)".equals(userType)) {
            String price = edtPrice.getText().toString();
            if (price.isEmpty()) {
                txtLayPrice.setError("Price is required for sellers");
                isValid = false;
            } else {
                txtLayPrice.setError(null);
            }
        }

        // Validation for Building Number
        if (edtBuildingNumber.getText().toString().isEmpty()) {
            txtLayBuildingNumber.setError("Building is required");
            isValid = false;
        } else {
            txtLayBuildingNumber.setError(null);
        }

        // Validation for Building Name
        if (edtBuildingName.getText().toString().isEmpty()) {
            txtLayBuildingName.setError("Building is required");
            isValid = false;
        } else {
            txtLayBuildingName.setError(null);
        }

        // Validation for City
        if (edtCity.getText().toString().isEmpty()) {
            txtLayCity.setError("City is required");
            isValid = false;
        } else {
            txtLayCity.setError(null);
        }

        // Validation for Province
        if (edtProvince.getText().toString().isEmpty()) {
            txtLayProvince.setError("Province is required");
            isValid = false;
        } else {
            txtLayProvince.setError(null);
        }

        // Validation for Postal Code
        if (edtPostalCode.getText().toString().isEmpty()) {
            txtLayPostalCode.setError("Postal Code is required");
            isValid = false;
        } else {
            txtLayPostalCode.setError(null);
        }

        return isValid;
    }

    private void clearFormFields() {
        edtTypeOfUser.setText("");
        edtTypeOfWaste.setText("");
        edtQuantity.setText("");
        edtWeight.setText("");
        edtPrice.setText("");
        edtOtherDetails.setText("");
    }
}