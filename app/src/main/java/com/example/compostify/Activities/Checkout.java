package com.example.compostify.Activities;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.compostify.HomeFragment;
import com.example.compostify.R;
import com.example.compostify.adapters.PhotoAdapter;
import com.example.compostify.databinding.ActivityCheckoutBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class Checkout extends AppCompatActivity {

    ActivityCheckoutBinding binding;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private String purchaserId;
    private String sellerId;
    private String publishId;

    private String sellerImageUrl;

    private String typeOfWaste;
    private String totalWeight;
    private String naturalWeight;
    private String mixWeight;

    double naturalWastePricePerKg = 5.0; // $5 per 10Kg
    double mixWastePricePerKg = 3.0; // $3 per 10Kg

    Double totalAmount;

    String sellerName;
    String sellerType;
    String sellerAddress;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityCheckoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize FirebaseFirestore instance and firebaseAuth
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        // Retrieve data from intent extras
        Intent intent = getIntent();

        publishId = intent.getStringExtra("postId");
        sellerId = intent.getStringExtra("sellerId");
        purchaserId = firebaseAuth.getCurrentUser().getUid();

        //visibility of content
        binding.txtNaturalWeight.setVisibility(View.GONE);
        binding.txtMixWeight.setVisibility(View.GONE);

        //methods and functions
        loadData();
        setupInputFilters();

        binding.btnPlaceOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                placeOrder();
            }
        });
    }

    private void loadData() {
        RequestOptions requestOptions = new RequestOptions().transform(new CircleCrop());
        // Fetch purchase data
        DocumentReference purchaseRef = firebaseFirestore.collection("users").document(purchaserId);
        purchaseRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot purchaseSnapshot) {
                if (purchaseSnapshot.exists()) {
                    // Retrieve purchase data
                    String cardHolderName = purchaseSnapshot.getString("userName");
                    binding.edtCardHolderName.setText(cardHolderName.toUpperCase());
                }
            }
        });

        // Fetch seller data
        DocumentReference sellerRef = firebaseFirestore.collection("users").document(sellerId);
        sellerRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot sellerSnapshot) {
                if (sellerSnapshot.exists()) {
                    // Retrieve seller data
                    sellerName = sellerSnapshot.getString("userName");
                    sellerType = sellerSnapshot.getString("typeOfUser");
                    sellerAddress = sellerSnapshot.getString("address");
                    sellerImageUrl = sellerSnapshot.getString("downloadUrl");

                    // Update UI with seller information
                    binding.txtSellerBusinessName.setText(sellerName);
                    binding.txtSellerTypeOfUser.setText(sellerType);
                    binding.txtSellerBusinessAddress.setText(sellerAddress);
                    // Load seller image using Glide
                    Glide.with(Checkout.this)
                            .load(sellerImageUrl)
                            .placeholder(R.drawable.placeholder) // Placeholder image while loading
                            .error(R.drawable.error) // Error image if loading fails
                            .apply(requestOptions)
                            .into(binding.imgSellerBusinessLogo);
                }
            }
        });

        // Fetch publish data
        DocumentReference publishRef = firebaseFirestore.collection("Publish").document(publishId);
        publishRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot publishSnapshot) {
                if (publishSnapshot.exists()) {
                    // Retrieve publish data
                    typeOfWaste = publishSnapshot.getString("typeOfWaste");
                    totalWeight = publishSnapshot.getString("totalWeight");
                    naturalWeight = publishSnapshot.getString("naturalWasteWeight");
                    mixWeight = publishSnapshot.getString("mixWasteWeight");
                    String otherDetails =publishSnapshot.getString("otherDetails");
                    String userType = publishSnapshot.getString("typeOfUser");
                    List<String> imageUrls = (List<String>) publishSnapshot.get("imageUrls");

                    // Update UI with publish information
                    binding.txtTypeOfWaste.setText(typeOfWaste);
                    binding.txtWeight.setText("Total Weight: "+totalWeight);
                    binding.txtNaturalWeight.setText("Natural Weight: "+naturalWeight);
                    binding.txtMixWeight.setText("Mix Weight: "+mixWeight);
                    binding.txtOtherDetail.setText("Handling Instruction: "+otherDetails);

                    binding.rvWastePhotos.setLayoutManager(new LinearLayoutManager(Checkout.this, LinearLayoutManager.HORIZONTAL, false));

                    PhotoAdapter adapter = new PhotoAdapter(Checkout.this, imageUrls);
                    binding.rvWastePhotos.setAdapter(adapter);

                    if(typeOfWaste.equalsIgnoreCase("both")){
                        binding.txtNaturalWeight.setVisibility(View.VISIBLE);
                        binding.txtMixWeight.setVisibility(View.VISIBLE);
                        calculateTotalAmount(Double.parseDouble(naturalWeight),Double.parseDouble(mixWeight));
                    } else if (typeOfWaste.equalsIgnoreCase("Natural Waste (5$ per 10Kg)")) {
                        totalAmount = (Double.parseDouble(totalWeight) / 10.0) * naturalWastePricePerKg;
                        binding.txtTotalAmount.setText("Total Amount: "+ totalAmount);
                    } else if (typeOfWaste.equalsIgnoreCase("Mix Waste (3$ per 10Kg)")) {
                        totalAmount = (Double.parseDouble(totalWeight) / 10.0) * mixWastePricePerKg;
                        binding.txtTotalAmount.setText("Total Amount: "+ totalAmount);
                    }
                    if(userType .equalsIgnoreCase("buyer")){
                        binding.rvWastePhotos.setVisibility(View.GONE);
                        binding.txtTotalAmount.setVisibility(View.GONE);
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Handle failure
                Log.e(TAG, "Error fetching publish data: " + e.getMessage());
            }
        });

    }

    private boolean validateFields() {
        String cardHolderName = binding.edtCardHolderName.getText().toString().trim();
        String cardNumber = binding.edtCardNumber.getText().toString().trim();
        String cardCVV = binding.edtCardCVV.getText().toString().trim();
        if (cardHolderName.isEmpty()) {
            binding.edtCardHolderName.setError("Please enter card holder name");
            return false;
        }
        if (cardNumber.isEmpty()) {
            binding.edtCardNumber.setError("Please enter card number");
            return false;
        }

        if (cardCVV.isEmpty()) {
            binding.edtCardCVV.setError("Please enter CVV");
            return false;
        }

        return true;
    }

    private void placeOrder() {
        if (validateFields()) {
            // Get the values from the input fields
            String cardHolderName = binding.edtCardHolderName.getText().toString().trim();
            String cardNumber = binding.edtCardNumber.getText().toString().trim();
            String cardCVV = binding.edtCardCVV.getText().toString().trim();

            // Add fields to the document
            Map<String, Object> paymentData = new HashMap<>();
            paymentData.put("cardHolderName", cardHolderName);
            paymentData.put("cardNumber", cardNumber);
            paymentData.put("cardCVV", cardCVV);

            firebaseFirestore.collection("Payment")
                    .add(paymentData)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference paymentDocumentReference) {
                            // Payment details successfully stored
                            String paymentId = paymentDocumentReference.getId();
                            Toast.makeText(Checkout.this, "Payment details stored successfully", Toast.LENGTH_SHORT).show();

                            // Create a new document in the "Order" collection
                            Map<String, Object> orderData = new HashMap<>();
                            orderData.put("paymentId", paymentId);
                            orderData.put("publishId", publishId);
                            orderData.put("publishName", cardHolderName);
                            orderData.put("purchaserId", purchaserId);
                            orderData.put("purchaserCardNumber",cardNumber);
                            orderData.put("sellerId", sellerId);
                            orderData.put("sellerName", sellerName);
                            orderData.put("typeOfWaste", typeOfWaste);
                            orderData.put("totalWeight", totalWeight);
                            orderData.put("naturalWeight", naturalWeight);
                            orderData.put("mixWeight", mixWeight);
                            orderData.put("totalAmount", String.valueOf(totalAmount));
                            orderData.put("checkoutDateTime", FieldValue.serverTimestamp());

                            firebaseFirestore.collection("Order")
                                    .add(orderData)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference orderDocumentReference) {
                                            // Order details successfully stored
                                            Toast.makeText(Checkout.this, "Order placed successfully", Toast.LENGTH_SHORT).show();

                                            // Clear form fields after placing order
                                            clearFormFields();

                                            // Update publish status to "Deactive"
                                            firebaseFirestore.collection("Publish").document(publishId).update(
                                                    "postStatus", "Deactive"
                                            );
                                            Intent intent = new Intent(Checkout.this, HomeFragment.class);
                                            startActivity(intent);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Handle any errors that occurred while trying to store order details
                                            Toast.makeText(Checkout.this, "Error placing order: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Handle any errors that occurred while trying to store payment details
                            Toast.makeText(Checkout.this, "Error storing payment details: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void setupInputFilters() {
        // Input filter for card number and CVV
        binding.edtCardNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String digitsOnly = s.toString().replaceAll("\\D", "");
                // Limit card number length to 12 digits
                if (digitsOnly.length() > 16) {
                    digitsOnly = digitsOnly.substring(0, 16);
                }
                StringBuilder formatted = new StringBuilder();
                for (int i = 0; i < digitsOnly.length(); i++) {
                    formatted.append(digitsOnly.charAt(i));
                    if ((i + 1) % 4 == 0 && i < digitsOnly.length() - 1) {
                        formatted.append(" "); // Insert space after every 4 digits
                    }
                }
                binding.edtCardNumber.removeTextChangedListener(this); // Remove listener to prevent infinite loop
                binding.edtCardNumber.setText(formatted.toString());
                binding.edtCardNumber.setSelection(formatted.length()); // Move cursor to the end
                binding.edtCardNumber.addTextChangedListener(this); // Add listener back
            }
        });

        // Input filter for CVV
        binding.edtCardCVV.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                // Limit CVV length to 3 digits
                if (s.length() > 3) {
                    s.delete(3, s.length());
                }
            }
        });
    }

        private void clearFormFields() {
            // Clear card holder name field
            binding.edtCardHolderName.getText().clear();

            // Clear card number field
            binding.edtCardNumber.getText().clear();

            // Clear card CVV field
            binding.edtCardCVV.getText().clear();
        }

    private void calculateTotalAmount(double naturalWeight, double mixWeight) {
        // Calculate total amount
        double naturalWasteTotalAmount = (naturalWeight / 10.0) * naturalWastePricePerKg;
        double mixWasteTotalAmount = (mixWeight / 10.0) * mixWastePricePerKg;
        totalAmount = naturalWasteTotalAmount + mixWasteTotalAmount;
        binding.txtTotalAmount.setText(String.format(Locale.getDefault(), "Total Amount: $%.2f", totalAmount));
    }
    }