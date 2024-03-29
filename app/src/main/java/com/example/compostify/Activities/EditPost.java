package com.example.compostify.Activities;

import android.content.ClipData;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.compostify.R;
import com.example.compostify.adapters.PhotoAdapter;
import com.example.compostify.databinding.ActivityEditPostBinding;
import com.example.compostify.db.UserRecentActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EditPost extends AppCompatActivity {

    ActivityEditPostBinding binding;
    FirebaseFirestore db;
    FirebaseAuth firebaseAuth;
    // You can fetch post data similarly by using the post ID passed from the previous activity
    UserRecentActivity post;
    private List<Uri> newImageUri = new ArrayList<>();

    private SwitchMaterial switchActiveDeActivate;

    private String postStatus;
    private boolean isPostActive;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditPostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        post = (UserRecentActivity) getIntent().getSerializableExtra("userPost");

        //changing background color of dropdown menus
        AutoCompleteTextView autoCompleteTOWaste = binding.edtTypeOfWaste;
        autoCompleteTOWaste.setDropDownBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(EditPost.this, R.color.secondary)));

        // Set up a listener for type of waste changes
        autoCompleteTOWaste.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedTypeOfWaste = (String) parent.getItemAtPosition(position);
                handleTypeOfWasteChange(selectedTypeOfWaste);
            }
        });

        //methods
        setUpTextChangeListeners();
        loadData();
        buttonBinding();
    }

    private void buttonBinding() {
        //select Image Button
        binding.btnSelectPhotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //selectImages
                pickImage();
            }
        });

        //clear photo Button
        binding.btnClearPhotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearImagesUrl();
            }
        });

        //save button
        binding.btnSavePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //update data on same postID in publish
                if(newImageUri.isEmpty()) {
                    updatePostData(new ArrayList<String>());
                } else {
                    updatePost();
                }
            }
        });

        //delete post button
        binding.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletePost();
            }
        });
    }

    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            // Check if postId is not null
            String postId = post.getPublishId();
            if (postId != null) {
                List<String> imageUrls = post.getImageUrl();
//                List<String> newImageUris = new ArrayList<>();

                ClipData clipData = data.getClipData();

                if (clipData != null) {
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        Uri imageUri = clipData.getItemAt(i).getUri();
                        newImageUri.add(imageUri);
                    }
                    List<String> tempMergeUrls = new ArrayList<>();
                    for (String url : imageUrls) {
                        tempMergeUrls.add(url);
                    }
                    for (Uri uri: newImageUri){
                        tempMergeUrls.add(uri.toString());
                    }
                    setupRecyclerView(tempMergeUrls);
                } else if (data.getData() != null) {
                    Uri imageUri = data.getData();
                    List<String> tempMergeUrls = new ArrayList<>();
                    for (String url : imageUrls) {
                        tempMergeUrls.add(url);
                    }
                    tempMergeUrls.add(imageUri.toString());
                    newImageUri.add(imageUri);
                    setupRecyclerView(tempMergeUrls);
                }
            }
        }
    }


    private void uploadImage(Uri imageUri, OnSuccessListener<Uri> onSuccessListener) {
        // Get a reference to the Firebase Storage
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        String imageName = "WasteImage_" + System.currentTimeMillis(); // Generate a unique name for each image
        StorageReference imagesRef = storageRef.child("WasteImages").child(imageName);

        imagesRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get the download URL of the uploaded image
                        imagesRef.getDownloadUrl().addOnSuccessListener(onSuccessListener);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Show a toast indicating the failure
                        Toast.makeText(EditPost.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void clearImagesUrl() {
        //delete all Images from firebase database and storage of this post
        post.getImageUrl().clear();
        newImageUri.clear();
        DocumentReference postRef = db.collection("Publish").document(post.getPublishId());
        postRef.update("imageUrls",post.getImageUrl())
                .addOnSuccessListener(aVoid ->{
                    Toast.makeText(EditPost.this, "Images Deleted successfully", Toast.LENGTH_SHORT).show();
                });
    }

    private void updatePost(){
        List<String> uploadedUrls = new ArrayList<>();
        for (Uri uri: newImageUri){
            uploadImage(uri, new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    // Add the downloaded URL to the list
                    uploadedUrls.add(uri.toString());

                    // If all images are uploaded, update the post
                    if (uploadedUrls.size() == newImageUri.size()) {
                        // Update post data in Firestore
                        updatePostData(uploadedUrls);
                    }
                }
            });
        }
    }

    private void updatePostData(List<String> uploadedUrls) {
        String typeOfWaste = binding.edtTypeOfWaste.getText().toString().trim();
        String naturalWasteWeight = binding.edtNaturalWeight.getText().toString().trim();
        String mixWasteWeight = binding.edtMixWeight.getText().toString().trim();
        String totalWeight = binding.edtWeight.getText().toString().trim();
        String otherDetails = binding.edtOtherDetails.getText().toString().trim();
        if(switchActiveDeActivate.isChecked()){
            postStatus = "Active";
        }else {
            postStatus = "Deactivate";
        }
        List<String> imageUrls = post.getImageUrl();
        imageUrls.addAll(uploadedUrls);
        // Update post data in Firestore
        DocumentReference postRef = db.collection("Publish").document(post.getPublishId());
        FieldValue postDateTime = FieldValue.serverTimestamp();
        postRef.update("typeOfWaste", typeOfWaste,
                        "naturalWasteWeight", naturalWasteWeight,
                        "mixWasteWeight", mixWasteWeight,
                        "totalWeight", totalWeight,
                        "otherDetails", otherDetails,
                        "imageUrls", imageUrls,
                        "postStatus", postStatus,
                        "postDateTime", postDateTime)
                .addOnSuccessListener(aVoid -> {
                    // Data updated successfully
                    Toast.makeText(EditPost.this, "Post updated successfully", Toast.LENGTH_SHORT).show();
                    finish(); // Finish the activity after successful update
                })
                .addOnFailureListener(e -> {
                    // Failed to update data
                    Toast.makeText(EditPost.this, "Failed to update post: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });

    }

//    tims@reginastreet.ca
    private void deletePost() {
        // Reference to the post document
        DocumentReference postRef = db.collection("Publish").document(post.getPublishId());
        // Delete the post document
        postRef.delete()
                .addOnSuccessListener(aVoid -> {
                    // Post deleted successfully
                    Toast.makeText(EditPost.this, "Post deleted successfully", Toast.LENGTH_SHORT).show();
                    finish(); // Finish the activity after successful deletion
                })
                .addOnFailureListener(e -> {
                    // Failed to delete post
                    Toast.makeText(EditPost.this, "Failed to delete post: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void loadData() {
        String postId = post.getPublishId();
        String typeOfUser = post.getTypeOfUser();
        String typeOfWaste = post.getTypeOfWaste();
        String NaturalWasteWeight = post.getNaturalWasteWeight();
        String MixWasteWeight = post.getMixWasteWeight();
        String totalWeight = post.getWeight();
        String otherDetails = post.getOtherDetails();
        List<String> imageUrls = post.getImageUrl();
        postStatus = post.getPostStatus();

        if (postId != null) {
            // Populate UI fields with post data
            binding.edtTypeOfWaste.setText(typeOfWaste);
            binding.edtNaturalWeight.setText(NaturalWasteWeight);
            binding.edtMixWeight.setText(MixWasteWeight);
            binding.edtWeight.setText(totalWeight);
            binding.edtOtherDetails.setText(otherDetails);

            // Show images in RecyclerView
            if (!imageUrls.isEmpty()) {
                setupRecyclerView(imageUrls);

            }
            // Add null check for typeOfUser

            if (typeOfUser != null && typeOfUser.equalsIgnoreCase("seller")) {
                binding.txtLayPhoto.setVisibility(View.VISIBLE);
                binding.rvWastePhotos.setVisibility(View.VISIBLE);
                binding.btnSelectPhotos.setVisibility(View.VISIBLE);
                binding.btnClearPhotos.setVisibility(View.VISIBLE);
                ArrayAdapter<String> wasteAdapter = new ArrayAdapter<>(
                        EditPost.this,
                        android.R.layout.simple_dropdown_item_1line,
                        new String[]{"Natural Waste (5$ per 10Kg)", "Mix Waste (3$ per 10Kg)", "Both"}
                );
                binding.edtTypeOfWaste.setAdapter(wasteAdapter);
            } else {
                binding.txtLayPhoto.setVisibility(View.GONE);
                binding.rvWastePhotos.setVisibility(View.GONE);
                binding.btnSelectPhotos.setVisibility(View.GONE);
                binding.btnClearPhotos.setVisibility(View.GONE);
                // Set up AutoCompleteTextView with predefined options
                ArrayAdapter<String> wasteAdapter = new ArrayAdapter<>(
                        EditPost.this,
                        android.R.layout.simple_dropdown_item_1line,
                        new String[]{"Natural Waste (5$ per 10Kg)", "Mix Waste (3$ per 10Kg)"}
                );
                binding.edtTypeOfWaste.setAdapter(wasteAdapter);
            }

            if (typeOfWaste.equalsIgnoreCase("both")) {
                binding.txtLayNaturalWeight.setVisibility(View.VISIBLE);
                binding.txtLayMixWeight.setVisibility(View.VISIBLE);
                calculateTotalWeight();
            } else {
                binding.txtLayNaturalWeight.setVisibility(View.GONE);
                binding.txtLayMixWeight.setVisibility(View.GONE);
            }


            switchActiveDeActivate = binding.switchActiveDeActivate;
            switchActiveDeActivate.setChecked(postStatus.equalsIgnoreCase("Active"));
//            switchActiveDeActivate.setText(isPostActive? R.string.active : R.string.deactivate);
            switchActiveDeActivate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    // Update post status based on switch state
                    postStatus = isChecked ? "Active" : "Deactivate";
                    switchActiveDeActivate.setText(isChecked ? R.string.active : R.string.deactivate);
                }
            });
        }else {
            // Handle the case when post is null, perhaps by displaying an error message or finishing the activity
            Toast.makeText(this, "Post data is null", Toast.LENGTH_SHORT).show();
            finish(); // Finish the activity or handle it accordingly
        }
    }

    private void setupRecyclerView(List<String> imageUrls) {
        RecyclerView rvWastePhotos = binding.rvWastePhotos;
        PhotoAdapter adapter = new PhotoAdapter(EditPost.this, imageUrls);
        rvWastePhotos.setAdapter(adapter);
        rvWastePhotos.setLayoutManager(new LinearLayoutManager(EditPost.this, LinearLayoutManager.HORIZONTAL, false));
    }


    private void handleTypeOfWasteChange(String selectedTypeOfWaste) {
        if (selectedTypeOfWaste.equalsIgnoreCase("both")) {
            binding.txtLayNaturalWeight.setVisibility(View.VISIBLE);
            binding.txtLayMixWeight.setVisibility(View.VISIBLE);
            calculateTotalWeight();
        } else {
            binding.txtLayNaturalWeight.setVisibility(View.GONE);
            binding.txtLayMixWeight.setVisibility(View.GONE);
        }
    }
    private void setUpTextChangeListeners() {
        // Set up text change listeners for natural weight and mix weight
        binding.edtNaturalWeight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                calculateTotalWeight();
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Not needed
            }
        });

        binding.edtMixWeight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                calculateTotalWeight();
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Not needed
            }
        });
    }
    //calculate natural weight and mix weight
    private void calculateTotalWeight() {
        String naturalWeightStr = Objects.requireNonNull(binding.edtNaturalWeight.getText()).toString();
        String mixWeightStr = Objects.requireNonNull(binding.edtMixWeight.getText()).toString();

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
}
