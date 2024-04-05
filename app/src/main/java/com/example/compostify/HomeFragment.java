package com.example.compostify;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.compostify.Activities.MyOrders;
import com.example.compostify.Activities.UserHistoryViewMore;
import com.example.compostify.adapters.UserRecentActivityAdapter;
import com.example.compostify.databinding.FragmentHomeBinding;
import com.example.compostify.db.UserRecentActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
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
    private FirebaseFirestore db;
    private Context context;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public void onResume(){
        super.onResume();
        fetchRecentActivityData();
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
                try{
                    binding.txtBusinessName.setText(value.getString("businessName"));
                    binding.txtAddress.setText(value.getString("address"));
                    binding.txtUserName.setText(value.getString("userName"));
                }catch(NullPointerException e)
                {

                }
                try {
                        Glide.with(getContext()).load(value.getString("downloadUrl")).into(binding.imgProfilePic);
                }catch (NullPointerException e)
                {
                }
            }
        });

        // Fetch recent activity data
        fetchRecentActivityData();
//        return inflater.inflate(R.layout.fragment_home, container, false);
        return binding.getRoot();
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = requireContext();
        fetchRecentActivityData();

        // Set click listener for "View More" TextView
        binding.txtViewAllActivity.setOnClickListener(v -> {
            // Navigate to the user history view more activity
            Intent intent = new Intent(requireContext(), UserHistoryViewMore.class);
            startActivity(intent);
        });

        //set click listener for "My Orders" button
        binding.btnMyOrder.setOnClickListener(v -> {
            // Navigate to the my orders activity
            Intent intent = new Intent(requireContext(), MyOrders.class);
            startActivity(intent);
        });

    }

    private void fetchRecentActivityData() {
        if (firebaseAuth.getCurrentUser() != null) {

            // Get the current user ID (assuming you are using Firebase Authentication)
            String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

            // Fetch user's recent activity data from Firestore
            db.collection("Publish")
                    .whereEqualTo("userId", currentUserId)
                    .limit(3) // Limit to only 3 documents
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
                                String NaturalWasteWeight = document.getString("naturalWasteWeight");
                                String MixWasteWeight = document.getString("mixWasteWeight");
                                String weight = document.getString("totalWeight");
                                String OtherDetails = document.getString("otherDetails");
//                            String imageUrls = document.getString("imageUrls");

                                // Retrieve the image URLs array
                                List<String> imageUrls = (List<String>) document.get("imageUrls");


                                // Convert timestamp to date string
                                String date;
                                if (document.contains("postDateTime")) {
                                    long timestamp = document.getDate("postDateTime").getTime();
                                    date = "Date: " + sdf.format(timestamp);
                                } else {
                                    date = "";
                                }

                                String time;
                                if (document.contains("postDateTime")) {
                                    long timestamp = document.getDate("postDateTime").getTime();
                                    time = "Time: " + stf.format(timestamp);
                                } else {
                                    time = "";
                                }

                                String postStatus = document.getString("postStatus");
                                UserRecentActivity recentActivity = new UserRecentActivity(currentUserId, publishId, typeOfUser, typeOfWaste, date, time, NaturalWasteWeight, MixWasteWeight, weight, OtherDetails, imageUrls, postStatus);
                                recentActivityList.add(recentActivity);
                            }
                            initializeRecyclerView(recentActivityList);
                        }

                    });
        }

    }

    private void initializeRecyclerView(List<UserRecentActivity> recentActivityList) {
        // Initialize RecyclerView
        RecyclerView recyclerView = binding.recentActivityRecyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(context)); // Pass context here

        // Set adapter for RecyclerView
        UserRecentActivityAdapter adapter = new UserRecentActivityAdapter(context,recentActivityList);
        recyclerView.setAdapter(adapter);
    }
}
