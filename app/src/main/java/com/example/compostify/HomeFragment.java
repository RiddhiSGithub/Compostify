package com.example.compostify;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.compostify.adapters.UserRecentActivityAdapter;
import com.example.compostify.db.UserRecentActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private FirebaseFirestore db;
    private Context context;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = requireContext();
        fetchRecentActivityData();
    }

    private void fetchRecentActivityData() {
        // Get the current user ID (assuming you are using Firebase Authentication)
//        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Fetch user's recent activity data from Firestore
        db.collection("Publish")
//                .whereEqualTo("userId", currentUserId) // Filter by current user ID
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<UserRecentActivity> recentActivityList = new ArrayList<>();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        SimpleDateFormat stf = new SimpleDateFormat("HH:mm", Locale.getDefault());

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String typeOfUser = document.getString("typeOfUser");
                            String typeOfWaste = document.getString("typeOfWaste");
                            String weight = "Weight: "+ document.getString("totalWeight");
                            String imageUrls = document.getString("imageUrl");//business logo

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

                            UserRecentActivity recentActivity = new UserRecentActivity(typeOfUser, typeOfWaste, date, time, weight, imageUrls);
                            recentActivityList.add(recentActivity);
                        }
                        initializeRecyclerView(recentActivityList);
                    }
                });
    }

    private void initializeRecyclerView(List<UserRecentActivity> recentActivityList) {
        // Initialize RecyclerView
        // Initialize RecyclerView
        RecyclerView recyclerView = requireView().findViewById(R.id.recentActivityRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context)); // Pass context here

        // Set adapter for RecyclerView
        UserRecentActivityAdapter adapter = new UserRecentActivityAdapter(context,recentActivityList);
        recyclerView.setAdapter(adapter);
    }
}
