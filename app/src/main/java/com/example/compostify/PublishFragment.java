package com.example.compostify;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.compostify.adapters.PhotoAdapter;
import com.example.compostify.databinding.FragmentPublishBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PublishFragment extends Fragment {

    private String userId;
    private FragmentPublishBinding binding;
    private FirebaseFirestore db;
    private AutoCompleteTextView edtTypeOfUser;
    private AutoCompleteTextView edtTypeOfWaste;
    private TextInputEditText edtWeight;
    private TextInputEditText edtPrice;
    private TextInputEditText edtOtherDetails;
    private TextInputEditText edtBuildingNumber;
    private TextInputEditText edtBuildingName;
    private TextInputEditText edtCity;
    private TextInputEditText edtProvince;
    private TextInputEditText edtPostalCode;

    private TextInputLayout txtLayWeight;
    private TextInputLayout txtLayPrice;
    private TextInputLayout txtLayBuildingNumber;
    private TextInputLayout txtLayBuildingName;
    private TextInputLayout txtLayCity;
    private TextInputLayout txtLayProvince;
    private TextInputLayout txtLayPostalCode;

    //
    private List<String> uploadedImageUrls = new ArrayList<>();

    public PublishFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPublishBinding.inflate(inflater, container, false);  // Initialize in onCreateView

        db = FirebaseFirestore.getInstance();

        edtTypeOfUser = binding.edtTypeOfUser;
        edtTypeOfWaste = binding.edtTypeOfWaste;
        edtWeight = binding.edtWeight;
        edtPrice = binding.edtPrice;
        edtOtherDetails = binding.edtOtherDetails;
        edtBuildingNumber = binding.edtBuildingNumber;
        edtBuildingName = binding.edtBuildingName;
        edtCity = binding.edtCity;
        edtProvince = binding.edtProvince;
        edtPostalCode = binding.edtPostalCode;

        txtLayWeight = binding.txtLayWeight;
        txtLayPrice = binding.txtLayPrice;
        txtLayBuildingNumber = binding.txtLayBuildingNumber;
        txtLayBuildingName = binding.txtLayBuildingName;
        txtLayCity = binding.txtLayCity;
        txtLayProvince = binding.txtLayProvince;
        txtLayPostalCode = binding.txtLayPostalCode;


        //Price will only show when type of user will be Seller
        txtLayPrice.setVisibility(View.GONE);

        //Fill data which were needed
//        fillData();
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


        //Adding image button
        Button btnUploadPhotos = binding.btnUploadPhotos;
        btnUploadPhotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImage();
            }
        });

        Button btnPublish = binding.btnPost;
        btnPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                publishData();
            }
        });

        return binding.getRoot();  // Make sure to return the root view
    }

    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 100);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == getActivity().RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            uploadImage(imageUri);
        }
    }

    private void uploadImage(Uri imageUri) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference imagesRef = storageRef.child("WasteImages/" + imageUri.getLastPathSegment());

        imagesRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        imagesRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String downloadUrl = uri.toString();
                                uploadedImageUrls.add(downloadUrl);
                                Toast.makeText(requireContext(), "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                                displayUploadedImages();
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(requireContext(), "Failed to upload image", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void displayUploadedImages() {
        RecyclerView rvWastePhotos = binding.rvWastePhotos;
        PhotoAdapter adapter = new PhotoAdapter(requireContext(), uploadedImageUrls);
        rvWastePhotos.setAdapter(adapter);
        rvWastePhotos.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
    }

    private void publishData() {
        if (validateInputs()) {
            String typeOfUser = binding.edtTypeOfUser.getText().toString();
            String typeOfWaste = binding.edtTypeOfWaste.getText().toString();
            String weight = binding.edtWeight.getText().toString();
            String otherDetails = binding.edtOtherDetails.getText().toString();
            String buildingNumber = binding.edtBuildingNumber.getText().toString();
            String buildingName = binding.edtBuildingName.getText().toString();
            String city = binding.edtCity.getText().toString();
            String province = binding.edtProvince.getText().toString();
            String postalCode = binding.edtPostalCode.getText().toString();

            Map<String, Object> postDetails = new HashMap<>();
            postDetails.put("typeOfUser", typeOfUser);
            postDetails.put("typeOfWaste", typeOfWaste);
            postDetails.put("weight", weight);
            postDetails.put("otherDetails", otherDetails);
            postDetails.put("building No,", buildingNumber);
            postDetails.put("building Name,", buildingName);
            postDetails.put("city", city);
            postDetails.put("province", province);
            postDetails.put("postalCode", postalCode);
            postDetails.put("imageUrls", uploadedImageUrls);
            postDetails.put("postDateTime", FieldValue.serverTimestamp());

            db.collection("Publish")
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

//    private void fillData() {
//        userId = firebaseAuth.getCurrentUser().getUid();
//        DocumentReference documentReference = firebaseFirestore.collection("users").document(userId);
//        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
//            @Override
//            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
//                binding.edtBuildingName.setText(value.getString("street"));
//                binding.edtBuildingNumber.setText(value.getString("unitNo"));
//                binding.edtCity.setText(value.getString("city"));
//                binding.edtProvince.setText(value.getString("province"));
//                binding.edtPostalCode.setText(value.getString("postalCode"));
//            }
//        });
//    }


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
            txtLayBuildingNumber.setError("Building Number is required");
            isValid = false;
        } else {
            txtLayBuildingNumber.setError(null);
        }

        // Validation for Building Name
        if (edtBuildingName.getText().toString().isEmpty()) {
            txtLayBuildingName.setError("Building Name is required");
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
        edtWeight.setText("");
        edtPrice.setText("");
        edtOtherDetails.setText("");
        uploadedImageUrls.clear();
        binding.rvWastePhotos.getAdapter().notifyDataSetChanged();
    }
}