package com.example.compostify.Activities;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.compostify.adapters.MyOrderAdapter;
import com.example.compostify.databinding.ActivityMyOrdersBinding;
import com.example.compostify.db.Order;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MyOrders extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    private Context context;
    ActivityMyOrdersBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyOrdersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        context = this;

        List<Order> orders = fetchOrders();
        initializeRecyclerView(orders);
    }

    private List<Order> fetchOrders(){
        String userId = firebaseAuth.getCurrentUser().getUid();
        List<Order> myOrders = new ArrayList<>();
        db.collection("Orders")
                .whereEqualTo("purchaserId",userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            myOrders.add(
                                    new Order(
                                            document.getId(),
                                            document.getString("publishId"),
                                            document.getString("sellerId"),
                                            document.getString("purchaserId")
                                    )
                            );

                        }
                    }
                });
        return myOrders;
    }

    private void initializeRecyclerView(List<Order> recentOrder) {
        // Initialize RecyclerView
        RecyclerView recyclerView = binding.recentActivityRecyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(context)); // Pass context here

        // Set adapter for RecyclerView
        MyOrderAdapter adapter = new MyOrderAdapter(context,recentOrder);
        recyclerView.setAdapter(adapter);
    }
}