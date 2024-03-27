package com.example.compostify;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PublishFragment extends Fragment {

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

    private TextInputLayout txtLayQuantity;
    private TextInputLayout txtLayWeight;
    private TextInputLayout txtLayPrice;
    private TextInputLayout txtLayBuildingNumber;
    private TextInputLayout txtLayBuildingName;
    private TextInputLayout txtLayCity;
    private TextInputLayout txtLayProvince;
    private TextInputLayout txtLayPostalCode;
    private FirebaseFirestore db;
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private List<String> uploadedImageUrls = new ArrayList<>();
    private String userType;
    private String userId;
    private String userEmail;
    FirebaseAuth firebaseAuth;
    public PublishFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPublishBinding.inflate(inflater, container, false);  // Initialize in onCreateView

        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        getTypeOfUser();
        initView();

        //Adding image button
        binding.btnSelectPhotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImage();
            }
        });

        //Post Button
        binding.btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                publishData();
            }
        });

        //reset the entire field
        binding.btnClearAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearFormFields();
                binding.txtLayPhoto.setVisibility(View.GONE);
                binding.btnSelectPhotos.setVisibility(View.GONE);
            }
        });

        return binding.getRoot();  // Make sure to return the root view
    }

    private void getTypeOfUser() {

        userId = firebaseAuth.getCurrentUser().getUid();
        DocumentReference documentReference = db.collection("users").document(userId);
        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Fetching data...");
        progressDialog.setCancelable(false); // Set whether the dialog can be canceled with the back key
        progressDialog.show();

            documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                    userType = value.getString("typeOfUser");
                    Log.e("User type", userType + "");
                    progressDialog.dismiss();
                    try {
                        visibilityOfContent();
                    }catch (NullPointerException e){

                    }


                }
            });

        }


    private void initView() {

        //Natural and mix weight will only show when type of user will select typeof waste "both"
        binding.txtLayNaturalWeight.setVisibility(View.GONE);
        binding.txtLayMixWeight.setVisibility(View.GONE);

        //changing background color of dropdown menus
        AutoCompleteTextView autoCompleteTOWaste = binding.edtTypeOfWaste;
        autoCompleteTOWaste.setDropDownBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(requireContext(), R.color.secondary)));
    }

    private void visibilityOfContent() {

//        if (currentUser != null) {
//            String userType = currentUser.get("userType").toString(); // Assuming user type is stored in Firestore
         //for test
        userType = "Seller";
        if (userType.equals("Seller")) {
            binding.txtLayPhoto.setVisibility(View.VISIBLE);
            binding.btnSelectPhotos.setVisibility(View.VISIBLE);
            // Set up AutoCompleteTextView with predefined options
            ArrayAdapter<String> wasteAdapter = new ArrayAdapter<>(
                    requireContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    new String[]{"Natural Waste (5$ per 10Kg)", "Mix Waste (3$ per 10Kg)", "Both"}
            );
            binding.edtTypeOfWaste.setAdapter(wasteAdapter);
        } else {
            binding.txtLayPhoto.setVisibility(View.GONE);
            binding.btnSelectPhotos.setVisibility(View.GONE);
            // Set up AutoCompleteTextView with predefined options
            ArrayAdapter<String> wasteAdapter = new ArrayAdapter<>(
                    requireContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    new String[]{"Natural Waste (5$ per 10Kg)", "Mix Waste (3$ per 10Kg)"}
            );
            binding.edtTypeOfWaste.setAdapter(wasteAdapter);
        }
//        }

        binding.edtTypeOfWaste.addTextChangedListener(new TextWatcher() {
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
                    binding.txtLayNaturalWeight.setVisibility(View.VISIBLE);
                    binding.txtLayMixWeight.setVisibility(View.VISIBLE);

                    // Add TextWatcher to edtNaturalWeight and edtMixWeight to calculate total weight
                    binding.edtNaturalWeight.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int start, int before, int count) {
                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                        }

                        @Override
                        public void afterTextChanged(Editable editable) {
                            calculateTotalWeight();
                        }
                    });

                    binding.edtMixWeight.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int start, int before, int count) {
                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                        }

                        @Override
                        public void afterTextChanged(Editable editable) {
                            calculateTotalWeight();
                        }
                    });
                } else {
                    binding.txtLayNaturalWeight.setVisibility(View.GONE);
                    binding.txtLayMixWeight.setVisibility(View.GONE);
                }
            }
        });
    }

    //calculate natural weight and mix weight
    private void calculateTotalWeight() {
        String naturalWeightStr = binding.edtNaturalWeight.getText().toString();
        String mixWeightStr = binding.edtMixWeight.getText().toString();

        // Remove the "Kg" suffix from the strings
        String naturalWeight = naturalWeightStr.replaceAll("[^\\d.]", "");
        String mixWeight = mixWeightStr.replaceAll("[^\\d.]", "");

        if (!naturalWeight.isEmpty() && !mixWeight.isEmpty()) {
            int naturalWeightValue = Integer.parseInt(naturalWeight);
            int mixWeightValue = Integer.parseInt(mixWeight);
            int totalWeight = naturalWeightValue + mixWeightValue;
            binding.edtWeight.setText(String.valueOf(totalWeight) + " kg");
        }
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
            if (data.getClipData() != null) {
                ClipData clipData = data.getClipData();
                for (int i = 0; i < clipData.getItemCount(); i++) {
                    Uri imageUri = clipData.getItemAt(i).getUri();
                    uploadImage(imageUri);
                }
            } else if (data.getData() != null) {
                Uri imageUri = data.getData();
                uploadImage(imageUri);
            }
        }
    }

    private void uploadImage(Uri imageUri) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        String imageName = "WasteImage_" + System.currentTimeMillis(); // Generate a unique name for each image
        StorageReference imagesRef = storageRef.child("WasteImages").child(imageName);

        imagesRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get the download URL of the uploaded image
                        imagesRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String downloadUrl = uri.toString();
                                uploadedImageUrls.add(downloadUrl);
//                                Toast.makeText(requireContext(), "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                                displayUploadedImages();
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Show a toast indicating the failure
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

    private void clearFormFields() {
        binding.edtTypeOfWaste.setText("");
        binding.edtWeight.setText("");
        binding.edtNaturalWeight.setText("");
        binding.edtMixWeight.setText("");
        binding.edtOtherDetails.setText("");

        // Clear uploaded image URLs and notify adapter
        uploadedImageUrls.clear();
        RecyclerView.Adapter adapter = binding.rvWastePhotos.getAdapter();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    private void publishData() {
        if (validateInputs()) {
            String postStatus = "Active";

            if (currentUser != null) {
                // Get user ID, email and user type;
                userId = currentUser.getUid();
                userEmail = currentUser.getEmail();
//                userType = currentUser.getTypeOfUser();

                String typeOfWaste = binding.edtTypeOfWaste.getText().toString();
                String totalWeight = binding.edtWeight.getText().toString();
                String naturalWeight = binding.edtNaturalWeight.getText().toString();
                String mixWeight = binding.edtMixWeight.getText().toString();
                String otherDetails = binding.edtOtherDetails.getText().toString();

                Map<String, Object> postDetails = new HashMap<>();
                postDetails.put("userId", userId);
                postDetails.put("userEmail", userEmail);
                postDetails.put("typeOfUser", userType);
                postDetails.put("typeOfWaste", typeOfWaste);
                postDetails.put("totalWeight", totalWeight);
                postDetails.put("naturalWasteWeight", naturalWeight);
                postDetails.put("mixWasteWeight", mixWeight);
                postDetails.put("otherDetails", otherDetails);
                postDetails.put("imageUrls", uploadedImageUrls);
                postDetails.put("postDateTime", FieldValue.serverTimestamp());
                postDetails.put("postStatus", postStatus);


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
                                Toast.makeText(requireContext(), "Failed to publish post: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                    });
            }
        }
    }

    private boolean validateInputs() {
        boolean isValid = true;

        // Validation for Type of Waste
        if (binding.edtTypeOfWaste.getText().toString().isEmpty()) {
            binding.edtTypeOfWaste.setError("Type of Waste is required");
            isValid = false;
        }

        // Validation for Weight
        String weight = binding.edtWeight.getText().toString();
        if (weight.isEmpty()) {
            binding.txtLayWeight.setError("Weight is required");
            isValid = false;
        } else {
            // Remove the "kg" suffix from the string
            weight = weight.replaceAll("[^\\d.]", "");

            int totalWeight = Integer.parseInt(weight);
            if (totalWeight < 10) { // Minimum total weight requirement
                binding.txtLayWeight.setError("Minimum total weight should be 10 kg");
                isValid = false;
            } else {
                binding.txtLayWeight.setError(null);
            }
        }

        // Validation for Price (only if the user is a seller)
        String userType = binding.edtTypeOfWaste.getText().toString();
        if ("Both".equals(userType)) {
            String naturalWeight = binding.edtNaturalWeight.getText().toString();
            String mixWeight = binding.edtMixWeight.getText().toString();
            boolean isNaturalWeightValid = true;
            boolean isMixWeightValid = true;

            if (!naturalWeight.isEmpty()) {
                // Remove the "kg" suffix from the string
                naturalWeight = naturalWeight.replaceAll("[^\\d.]", "");

                int naturalWeightValue = Integer.parseInt(naturalWeight);
                if (naturalWeightValue < 5) { // Minimum natural waste weight requirement
                    binding.txtLayNaturalWeight.setError("Minimum natural waste weight should be 5 kg");
                    isNaturalWeightValid = false;
                } else {
                    binding.txtLayNaturalWeight.setError(null);
                }
            }

            if (!mixWeight.isEmpty()) {
                // Remove the "kg" suffix from the string
                mixWeight = mixWeight.replaceAll("[^\\d.]", "");

                int mixWeightValue = Integer.parseInt(mixWeight);
                if (mixWeightValue < 5) { // Minimum mix waste weight requirement
                    binding.txtLayMixWeight.setError("Minimum mix waste weight should be 5 kg");
                    isMixWeightValid = false;
                } else {
                    binding.txtLayMixWeight.setError(null);
                }
            }

            // Check if both natural weight and mix weight are valid
            isValid = isNaturalWeightValid && isMixWeightValid;
        }

        return isValid;
    }
}