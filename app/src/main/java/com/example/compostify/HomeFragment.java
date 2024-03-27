package com.example.compostify;

import android.content.Context;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.compostify.databinding.FragmentHomeBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
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
import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideContext;
import com.example.compostify.databinding.FragmentHomeBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

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
    FragmentHomeBinding binding;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    String userId;
    String imageURL;

    private FirebaseFirestore db;
    private Context context;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        userId = firebaseAuth.getCurrentUser().getUid();

        DocumentReference documentReference = firebaseFirestore.collection("users").document(userId);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                binding.txtBusinessName.setText(value.getString("businessName"));
                binding.txtAddress.setText(value.getString("address"));
                binding.txtUserName.setText(value.getString("userName"));
                try {

                        Glide.with(getContext()).load(value.getString("downloadUrl")).into(binding.imgProfilePic);



                }catch (NullPointerException e)
                {

                }





            }
        });

//        return inflater.inflate(R.layout.fragment_home, container, false);
        return binding.getRoot();
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

                            UserRecentActivity recentActivity = new UserRecentActivity(typeOfUser, typeOfWaste, date, time, weight, firstImageUrl);
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

                            UserRecentActivity recentActivity = new UserRecentActivity(typeOfUser, typeOfWaste, date, time, weight, firstImageUrl);
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
