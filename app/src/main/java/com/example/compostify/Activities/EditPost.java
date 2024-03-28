package com.example.compostify.Activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.compostify.adapters.PhotoAdapter;
import com.example.compostify.databinding.ActivityEditPostBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class EditPost extends AppCompatActivity {

    ActivityEditPostBinding binding;
    FirebaseFirestore db;
    FirebaseAuth firebaseAuth;
    // You can fetch post data similarly by using the post ID passed from the previous activity
    String postId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditPostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        postId = getIntent().getStringExtra("publish_id");

//        checkData();
        loadData();
        buttonBinding();
    }

    private void buttonBinding() {
        binding.btnSavePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //update data on same postID in publish
                updatePost();
            }
        });
    }

    private void updatePost() {
        String typeOfWaste = binding.edtTypeOfWaste.getText().toString().trim();
        String naturalWasteWeight = binding.edtNaturalWeight.getText().toString().trim();
        String mixWasteWeight = binding.edtMixWeight.getText().toString().trim();
        String totalWeight = binding.edtWeight.getText().toString().trim();
        String otherDetails = binding.edtOtherDetails.getText().toString().trim();

        // Update post data in Firestore
        DocumentReference postRef = db.collection("Publish").document(postId);
        postRef.update("typeOfWaste", typeOfWaste,
                        "naturalWasteWeight", naturalWasteWeight,
                        "mixWasteWeight", mixWasteWeight,
                        "totalWeight", totalWeight,
                        "otherDetails", otherDetails)
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


    private void checkData() {

    }

    private void loadData() {
        if (postId != null) {
            DocumentReference postRef = db.collection("Publish").document(postId);
            postRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        // Retrieve post data and populate UI accordingly
                        String typeOfWaste = document.getString("typeOfWaste");
                        String NaturalWasteWeight = document.getString("naturalWasteWeight");
                        String MixWasteWeight = document.getString("mixWasteWeight");
                        String totalWeight = document.getString("totalWeight");
                        String otherDetails = document.getString("otherDetails");

                        // Use null check to handle potential null values
                        typeOfWaste = typeOfWaste != null ? typeOfWaste : "";
                        NaturalWasteWeight = NaturalWasteWeight != null ? NaturalWasteWeight : "";
                        MixWasteWeight = MixWasteWeight != null ? MixWasteWeight : "";
                        totalWeight = totalWeight != null ? totalWeight : "";
                        otherDetails = otherDetails != null ? otherDetails : "";

                        //retrieve array imageUrl from firebase
                        List<String> imageUrls = (List<String>) document.get("imageUrl");

                        // Populate UI fields with post data
                        binding.edtTypeOfWaste.setText(typeOfWaste);
                        binding.edtNaturalWeight.setText(NaturalWasteWeight);
                        binding.edtMixWeight.setText(MixWasteWeight);
                        binding.edtWeight.setText(totalWeight);
                        binding.edtOtherDetails.setText(otherDetails);

                        // Show images in RecyclerView
                        if (imageUrls != null && !imageUrls.isEmpty()) {
                            setupRecyclerView(imageUrls);
                        }

                        // Add null check for typeOfUser
                        String typeOfUser = document.getString("typeOfUser");
                        Log.e("typeOfUser","this is user type "+typeOfUser);
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
                    }
                }
            });
        }
    }

    private void setupRecyclerView(List<String> imageUrls) {
        RecyclerView rvWastePhotos = binding.rvWastePhotos;
        PhotoAdapter adapter = new PhotoAdapter(EditPost.this, imageUrls);
        rvWastePhotos.setAdapter(adapter);
        rvWastePhotos.setLayoutManager(new LinearLayoutManager(EditPost.this, LinearLayoutManager.HORIZONTAL, false));
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
}
