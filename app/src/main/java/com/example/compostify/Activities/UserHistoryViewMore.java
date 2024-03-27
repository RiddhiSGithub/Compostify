package com.example.compostify.Activities;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.compostify.R;
import com.example.compostify.adapters.UserRecentActivityAdapter;
import com.example.compostify.db.UserRecentActivity;
import com.example.compostify.databinding.ActivityUserHistoryViewMoreBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class UserHistoryViewMore extends AppCompatActivity {

    private ActivityUserHistoryViewMoreBinding binding;
    private FirebaseFirestore db;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserHistoryViewMoreBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        context = this; // Use 'this' context

        fetchRecentActivityData();
    }

    private void fetchRecentActivityData() {
        // Get the current user ID (assuming you are using Firebase Authentication)
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Fetch user's recent activity data from Firestore
        db.collection("Publish")
                .whereEqualTo("userId", currentUserId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<UserRecentActivity> recentActivityList = new ArrayList<>();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        SimpleDateFormat stf = new SimpleDateFormat("HH:mm", Locale.getDefault());

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String publishId = document.getId(); // Get the publishId
                            String typeOfUser = document.getString("typeOfUser");
                            String typeOfWaste = document.getString("typeOfWaste");
                            String weight = "Weight: "+ document.getString("totalWeight");
//                            String imageUrls = document.getString("imageUrls");

                            // Retrieve the image URLs array
                            List<String> imageUrls = (List<String>) document.get("imageUrls");
                            String firstImageUrl = "";

                            // Check if the imageUrls array is not empty
                            if (imageUrls != null && !imageUrls.isEmpty()) {
                                // Get the first image URL from the array
                                firstImageUrl = imageUrls.get(0);
                            }

                            // Convert timestamp to date string
                            String date;
                            if (document.contains("postDateTime")) {
                                long timestamp = document.getDate("postDateTime").getTime();
                                date = "Date: "+sdf.format(timestamp);
                            } else {
                                date = "";
                            }

                            String time;
                            if (document.contains("postDateTime")) {
                                long timestamp = document.getDate("postDateTime").getTime();
                                time = "Time: "+stf.format(timestamp);
                            } else {
                                time = "";
                            }

                            UserRecentActivity recentActivity = new UserRecentActivity(publishId, typeOfUser, typeOfWaste, date, time, weight, firstImageUrl);
                            recentActivityList.add(recentActivity);
                        }
                        initializeRecyclerView(recentActivityList);
                    }
                });
    }

    private void initializeRecyclerView(List<UserRecentActivity> recentActivityList) {
        // Initialize RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recentActivityRecyclerView); // Use findViewById directly
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        // Set adapter for RecyclerView
        UserRecentActivityAdapter adapter = new UserRecentActivityAdapter(context, recentActivityList);
        recyclerView.setAdapter(adapter);
    }
}