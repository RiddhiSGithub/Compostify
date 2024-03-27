package com.example.compostify.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.compostify.R;
import com.example.compostify.databinding.ActivityEditPostBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class EditPost extends AppCompatActivity {

    ActivityEditPostBinding binding;
    private String postId; // Post ID to edit
    private DatabaseReference postsRef;
    // Assuming you have a reference to the switch
    SwitchMaterial switchActiveDeActive = binding.switchActiveDeActive;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post);
        binding = ActivityEditPostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Get the post ID from intent
        postId = getIntent().getStringExtra("publish_id");

        // Initialize Firebase
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        String userId = auth.getCurrentUser().getUid();
        postsRef = database.getReference().child("Publish").child(userId).child(postId);


        loadData();
        buttonListener();
    }

    private void loadData() {
        // Load post data and populate UI fields
        postsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Get post data from dataSnapshot
                    String typeOfWaste = dataSnapshot.child("typeOfWaste").getValue(String.class);
                    String naturalWeight = dataSnapshot.child("naturalWeight").getValue(String.class);
                    String mixWeight = dataSnapshot.child("mixWeight").getValue(String.class);
                    String weight = dataSnapshot.child("weight").getValue(String.class);
                    String otherDetails = dataSnapshot.child("otherDetails").getValue(String.class);
                    String postStatus = dataSnapshot.child("postStatus").getValue(String.class);

                    // Populate UI fields with the existing data
                    binding.edtTypeOfWaste.setText(typeOfWaste);
                    binding.edtNaturalWeight.setText(naturalWeight);
                    binding.edtMixWeight.setText(mixWeight);
                    binding.edtWeight.setText(weight);
                    binding.edtOtherDetails.setText(otherDetails);
                    // Check the postStatus and set the switch accordingly
                    if (postStatus != null && postStatus.equals("Active")) {
                        switchActiveDeActive.setChecked(true);
                        switchActiveDeActive.setText("Active");
                    } else {
                        switchActiveDeActive.setChecked(false);
                        switchActiveDeActive.setText("InActive");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle potential errors while loading data
                Toast.makeText(EditPost.this, "Failed to load post data", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void buttonListener() {
        // Set onClick listener for the update button
        binding.btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call method to update the post
                updatePost();
            }
        });

        // Set the background tint color to red
        binding.btnDelete.setBackgroundTintList(ContextCompat.getColorStateList(EditPost.this, R.color.red));
        // Set onClick listener for the delete button
        binding.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call method to delete the post
                deletePost();
                //
            }
        });

        // Set onClick listener for the clear photos button
        binding.btnClearPhotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call method to clear photos
                clearPhotos();
            }
        });
    }

    // Method to update the post
    private void updatePost() {
        // Get updated post data from UI fields
        String updatedTypeOfWaste = binding.edtTypeOfWaste.getText().toString();
        String updatedNaturalWeight = binding.edtNaturalWeight.getText().toString();
        String updatedMixWeight = binding.edtMixWeight.getText().toString();
        String updatedWeight = binding.edtWeight.getText().toString();
        String updatedOtherDetails = binding.edtOtherDetails.getText().toString();
        String postStatus = binding.switchActiveDeActive.toString();


        // Create a map to update the post in Firebase
        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put("typeOfWaste", updatedTypeOfWaste);
        updateMap.put("naturalWeight", updatedNaturalWeight);
        updateMap.put("mixWeight", updatedMixWeight);
        updateMap.put("weight", updatedWeight);
        updateMap.put("otherDetails", updatedOtherDetails);
        updateMap.put("postStatus",postStatus);

        // Update the post in Firebase
        postsRef.updateChildren(updateMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Post updated successfully
                            Toast.makeText(EditPost.this, "Post updated successfully", Toast.LENGTH_SHORT).show();
                            // Finish the activity and navigate back
                            finish();
                        } else {
                            // Error occurred while updating post
                            Toast.makeText(EditPost.this, "Failed to update post", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // Method to delete the post
    private void deletePost() {
        // Delete the post from Firebase
        postsRef.removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Post deleted successfully
                            Toast.makeText(EditPost.this, "Post deleted successfully", Toast.LENGTH_SHORT).show();
                            // Finish the activity and navigate back
                            finish();
                        } else {
                            // Error occurred while deleting post
                            Toast.makeText(EditPost.this, "Failed to delete post", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // Method to clear photos
    private void clearPhotos() {
        // Remove the photo data from the Firebase database
        postsRef.child("photoUrls").removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Photos cleared successfully
                            Toast.makeText(EditPost.this, "Photos cleared successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            // Error occurred while clearing photos
                            Toast.makeText(EditPost.this, "Failed to clear photos", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}