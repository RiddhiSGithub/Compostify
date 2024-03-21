package com.example.compostify;

import android.content.ClipData;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
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
import androidx.core.content.ContextCompat;
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
    private TextInputEditText edtNaturalWeight;
    private TextInputEditText edtMixWeight;
    private TextInputEditText edtOtherDetails;

    private TextInputLayout txtLayWeight;
    private TextInputLayout txtLayNaturalWeight;
    private TextInputLayout txtLayMixWeight;
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

        initView();

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

    private void initView() {
        edtTypeOfUser = binding.edtTypeOfUser;
        edtTypeOfWaste = binding.edtTypeOfWaste;
        edtWeight = binding.edtWeight;
        edtNaturalWeight = binding.edtNaturalWeight;
        edtMixWeight = binding.edtMixWeight;
        edtOtherDetails = binding.edtOtherDetails;

        txtLayWeight = binding.txtLayWeight;
        txtLayNaturalWeight = binding.txtLayNaturalWeight;
        txtLayMixWeight = binding.txtLayMixWeight;

        //Natural and mix weight will only show when type of user will select typeof waste "both"
        txtLayNaturalWeight.setVisibility(View.GONE);
        txtLayMixWeight.setVisibility(View.GONE);

        //Fill data which were needed
//        fillData();
        // Set up AutoCompleteTextView with predefined options
        ArrayAdapter<String> wasteAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                new String[]{"Natural Waste (5$ per 10Kg)", "Mix Waste (3$ per 10Kg)", "Both"}
        );
        edtTypeOfWaste.setAdapter(wasteAdapter);

        ArrayAdapter<String> userAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                new String[]{"Buyer(Fertilizer Companies, etc.)", "Seller(Restaurants, cafe, Super Markets, etc.)"}
        );
        edtTypeOfUser.setAdapter(userAdapter);

        // Add TextWatcher to edtTypeOfWaste
        edtTypeOfWaste.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int before, int count) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Check the selected type of user
                String wasteType = editable.toString();
                if ("Both".equals(wasteType)) {
                    // Show the "Natural weight and Mix weight" field
                    txtLayNaturalWeight.setVisibility(View.VISIBLE);
                    txtLayMixWeight.setVisibility(View.VISIBLE);
                }else {
                    txtLayNaturalWeight.setVisibility(View.GONE);
                    txtLayMixWeight.setVisibility(View.GONE);
                }
            }
        });

        //changing background color of dropdown menus
        AutoCompleteTextView autoCompleteTOWaste = binding.edtTypeOfWaste;
        autoCompleteTOWaste.setDropDownBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(requireContext(), R.color.secondary)));
        AutoCompleteTextView autoCompleteTOUser = binding.edtTypeOfUser;
        autoCompleteTOUser.setDropDownBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(requireContext(), R.color.secondary)));
    }

    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 100);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == getActivity().RESULT_OK && data != null) {
            ClipData clipData = data.getClipData();
            if (clipData != null) {
                for (int i = 0; i < clipData.getItemCount(); i++) {
                    Uri imageUri = clipData.getItemAt(i).getUri();
                    uploadImage(imageUri);
                }
            } else if (data.getData() != null) { // Handle single image selection
                Uri imageUri = data.getData();
                uploadImage(imageUri);
            }else {
                Uri imageUri = data.getData();
                uploadImage(imageUri);
            }
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
            String totalWeight = binding.edtWeight.getText().toString();
            String naturalWeight = binding.edtNaturalWeight.getText().toString();
            String mixWeight = binding.edtMixWeight.getText().toString();
            String otherDetails = binding.edtOtherDetails.getText().toString();

            Map<String, Object> postDetails = new HashMap<>();
            postDetails.put("typeOfUser", typeOfUser);
            postDetails.put("typeOfWaste", typeOfWaste);
            postDetails.put("totalWeight", totalWeight);
            postDetails.put("naturalWasteWeight", naturalWeight);
            postDetails.put("mixWasteWeight", mixWeight);
            postDetails.put("otherDetails", otherDetails);
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
            int totalWeight = Integer.parseInt(weight);
            if (totalWeight < 10) { // Minimum total weight requirement
                txtLayWeight.setError("Minimum total weight should be 10 kg");
                isValid = false;
            } else {
                txtLayWeight.setError(null);
            }
        }

        // Validation for Price (only if the user is a seller)
        String userType = edtTypeOfUser.getText().toString();
        if ("Seller(Restaurants, cafe, Super Markets, etc.)".equals(userType)) {
            String naturalWeight = edtNaturalWeight.getText().toString();
            String mixWeight = edtMixWeight.getText().toString();
            if (naturalWeight.isEmpty()) {
                txtLayNaturalWeight.setError("Price is required for sellers");
                isValid = false;
            } else {
                int naturalWeightValue = Integer.parseInt(naturalWeight);
                if (naturalWeightValue < 5) { // Minimum natural waste weight requirement
                    txtLayNaturalWeight.setError("Minimum natural waste weight should be 5 kg");
                    isValid = false;
                } else {
                    txtLayNaturalWeight.setError(null);
                }
            }
            if (mixWeight.isEmpty()) {
                txtLayMixWeight.setError("Price is required for sellers");
                isValid = false;
            } else {
                int mixWeightValue = Integer.parseInt(mixWeight);
                if (mixWeightValue < 5) { // Minimum mix waste weight requirement
                    txtLayMixWeight.setError("Minimum mix waste weight should be 5 kg");
                    isValid = false;
                } else {
                    txtLayMixWeight.setError(null);
                }
            }
        }
        return isValid;
    }
    private void clearFormFields() {
        edtTypeOfUser.setText("");
        edtTypeOfWaste.setText("");
        edtWeight.setText("");
        edtNaturalWeight.setText("");
        edtMixWeight.setText("");
        edtOtherDetails.setText("");
        uploadedImageUrls.clear();
        binding.rvWastePhotos.getAdapter().notifyDataSetChanged();
    }
}