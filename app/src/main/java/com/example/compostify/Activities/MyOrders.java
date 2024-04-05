package com.example.compostify.Activities;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.compostify.adapters.MyOrderItemListAdapter;
import com.example.compostify.databinding.ActivityMyOrdersBinding;
import com.example.compostify.db.Order;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MyOrders extends AppCompatActivity {

    private ActivityMyOrdersBinding binding;
    private MyOrderItemListAdapter adapter;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;


    private String sellerId;
    private String sellerBusinessName;
    private String sellerName;
    private String sellerTypeOfUser;
    private String sellerAddress;
    private Timestamp checkoutDateTime;
    private String typeOfWaste;
    private String weight;
    private String naturalWeight;
    private String mixWeight;
    private String totalAmount;
    private String purchaserBusinessName;
    private String purchaserName;
    private String purchaserTypeOfUser;
    private String purchaserCardNumber;
    private String sellerBusinessLogoUrl;
    private List<Order> orderList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyOrdersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.recentActivityRecyclerView.setLayoutManager(layoutManager);

        // Initialize adapter
        orderList = new ArrayList<>();
        adapter = new MyOrderItemListAdapter(orderList);
        binding.recentActivityRecyclerView.setAdapter(adapter);

        // Load user's order history
        loadOrderHistory();
    }

    private void loadOrderHistory() {
        String purchaserUserUid = firebaseAuth.getCurrentUser().getUid();
        db.collection("Order")
                .whereEqualTo("purchaserId", purchaserUserUid)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        // Retrieve order data from the document
                        sellerId = document.getString("sellerId");
                        checkoutDateTime = document.getTimestamp("checkoutDateTime");
                        String date;
                        if (checkoutDateTime != null) {
                            // Format the Timestamp as per your requirement
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                            date = dateFormat.format(checkoutDateTime.toDate());
                        } else {
                            date = "Unknown"; // Handle the case where checkoutDateTime is null
                        }
                        typeOfWaste = document.getString("typeOfWaste");
                        weight = document.getString("totalWeight");
                        naturalWeight = document.getString("naturalWeight");
                        mixWeight = document.getString("mixWeight");
                        totalAmount = document.getString("totalAmount");
                        purchaserCardNumber = document.getString("purchaserCardNumber");

                        // Retrieve additional data from the user document
                        db.collection("users")
                                .document(sellerId)
                                .get()
                                .addOnSuccessListener(sellerDocument -> {
                                    sellerName = sellerDocument.getString("userName");
                                    sellerBusinessName = sellerDocument.getString("businessName");
                                    sellerTypeOfUser = sellerDocument.getString("typeOfUser");
                                    sellerAddress = sellerDocument.getString("address");
                                    sellerBusinessLogoUrl = sellerDocument.getString("downloadUrl");
                                    Log.e("My Order","seller business name: "+ sellerBusinessName+" , "+sellerName+" , "+sellerTypeOfUser+" , "+ sellerAddress+" , "+ date+" , "+ typeOfWaste+" , "+ weight+" , "+
                                            naturalWeight+" , "+ mixWeight+" , "+totalAmount+" , "+ purchaserBusinessName+" , "+ purchaserName+" , "+
                                            purchaserTypeOfUser+" , "+ purchaserCardNumber+" , "+sellerBusinessLogoUrl);
                                    //logcat result
                                    //why its providing null values
                                    // Tim Hortons , null , Seller , 300 Regina St N, Waterloo, ON N2J 4H2, Canada , 2024-03-31 11:32:55 , Both , 350 kg , 100 , 250 , 125.0 , KFC , null , Buyer , 5136 3985 3896 6170 , https://firebasestorage.googleapis.com/v0/b/compostify-a5e7a.appspot.com/o/LogoImages%2F1000056236-Tim%20Hortons-1711588946174?alt=media&token=a2a1a310-ea46-4c81-be55-4a156a1f2448
                                    // Create Order object and add it to the list
                                    Order order = new Order(sellerBusinessName, sellerName,sellerTypeOfUser, sellerAddress, date, typeOfWaste, weight, naturalWeight, mixWeight, totalAmount, purchaserBusinessName, purchaserName,purchaserTypeOfUser, purchaserCardNumber, sellerBusinessLogoUrl);
                                    orderList.add(order);
                                    adapter.notifyDataSetChanged();
                                })
                                .addOnFailureListener(e -> {
                                    // Handle errors
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle errors
                });

        // Fetch purchaserBusinessName outside the onSuccess listener
        db.collection("users")
                .document(purchaserUserUid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    purchaserName = documentSnapshot.getString("userName");
                    purchaserBusinessName = documentSnapshot.getString("businessName");
                    purchaserTypeOfUser = documentSnapshot.getString("typeOfUser");

                })
                .addOnFailureListener(e -> {
                    // Handle errors
                });
    }
}