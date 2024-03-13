package com.example.compostify;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class PublishFragment extends Fragment {

    private AutoCompleteTextView edtTypeOfUser;
    private AutoCompleteTextView edtTypeOfWaste;
    private TextInputEditText edtQuantity;
    private TextInputEditText edtWeight;
    private TextInputEditText edtPrice;
    private TextInputEditText edtOtherDetails;
    private TextInputEditText edtBuilding;
    private TextInputEditText edtCity;
    private TextInputEditText edtProvince;
    private TextInputEditText edtPostalCode;

    private TextInputLayout txtLayQuantity;
    private TextInputLayout txtLayWeight;
    private TextInputLayout txtLayPrice;
    private TextInputLayout txtLayBuilding;
    private TextInputLayout txtLayCity;
    private TextInputLayout txtLayProvince;
    private TextInputLayout txtLayPostalCode;

    public PublishFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_publish, container, false);

        edtTypeOfUser = view.findViewById(R.id.edtTypeOfUser);
        edtTypeOfWaste = view.findViewById(R.id.edtTypeOfWaste);
        edtQuantity = view.findViewById(R.id.edtQuantity);
        edtWeight = view.findViewById(R.id.edtWeight);
        edtPrice = view.findViewById(R.id.edtPrice);
        edtOtherDetails = view.findViewById(R.id.edtOtherDetails);
//        edtPostDate = view.findViewById(R.id.edtPostDate);
        edtBuilding = view.findViewById(R.id.edtBuilding);
        edtCity = view.findViewById(R.id.edtCity);
        edtProvince = view.findViewById(R.id.edtProvince);
        edtPostalCode = view.findViewById(R.id.edtPostalCode);

        txtLayQuantity = view.findViewById(R.id.txtLayQuantity);
        txtLayWeight = view.findViewById(R.id.txtLayWeight);
        txtLayPrice = view.findViewById(R.id.txtLayPrice);
        txtLayBuilding = view.findViewById(R.id.txtLayBuilding);
        txtLayCity = view.findViewById(R.id.txtLayCity);
        txtLayProvince = view.findViewById(R.id.txtLayProvince);
        txtLayPostalCode = view.findViewById(R.id.txtLayPostalCode);

        //Price
        txtLayPrice.setVisibility(View.GONE);

        // Set up AutoCompleteTextView with predefined options
        ArrayAdapter<String> wasteAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                new String[]{"Natural Waste", "Mix Waste", "Both"}
        );
        edtTypeOfWaste.setAdapter(wasteAdapter);

        ArrayAdapter<String> userAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                new String[]{"Buyer(Fertilizer Companies, etc.)", "Seller(Restaurants, cafe, Super Markets, etc.)"}
        );
        edtTypeOfUser.setAdapter(userAdapter);


        // Add TextWatcher to edtTypeOfUser
        edtTypeOfUser.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int before, int count) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Check the selected type of user
                String userType = editable.toString();
                if ("Seller(Restaurants, cafe, Super Markets, etc.)".equals(userType)) {
                    // Show the "Price" field
                    txtLayPrice.setVisibility(View.VISIBLE);
                } else {
                    // Hide the "Price" field
                    txtLayPrice.setVisibility(View.GONE);
                    // Clear the "Price" field value
                    edtPrice.getText().clear();
                }
            }
        });

        Button btnPublish = view.findViewById(R.id.btnPost);
        btnPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInputs()) {
                    // Get logged-in user details
                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();


                    if (currentUser != null) {
                        // Get user ID and email
                        String userId = currentUser.getUid();
                        String userEmail = currentUser.getEmail();

                        // Retrieve other details from EditText fields
                        String typeOfUser = edtTypeOfUser.getText().toString();
                        String typeOfWaste = edtTypeOfWaste.getText().toString();
                        String quantity = edtQuantity.getText().toString();
                        String weight = edtWeight.getText().toString();
                        String otherDetails = edtOtherDetails.getText().toString();
                        String building = edtBuilding.getText().toString();
                        String city = edtCity.getText().toString();
                        String province = edtProvince.getText().toString();
                        String postalCode = edtPostalCode.getText().toString();

                        // Create a map to store post details
                        Map<String, Object> postDetails = new HashMap<>();
                        postDetails.put("userId", userId);
                        postDetails.put("userEmail", userEmail);
                        postDetails.put("typeOfUser", typeOfUser);
                        postDetails.put("typeOfWaste", typeOfWaste);
                        postDetails.put("quantity", quantity);
                        postDetails.put("weight", weight);
                        postDetails.put("otherDetails", otherDetails);
                        postDetails.put("building", building);
                        postDetails.put("city", city);
                        postDetails.put("province", province);
                        postDetails.put("postalCode", postalCode);

                        // Add current date and time
                        postDetails.put("postDateTime", FieldValue.serverTimestamp());

                        // Now, you can add this postDetails map to your Firebase Database or Firestore
                        // For example, assuming you have a "posts" collection in Firestore
                        FirebaseFirestore.getInstance().collection("Publish")
                                .add(postDetails)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        Toast.makeText(requireContext(), "Post published successfully", Toast.LENGTH_SHORT).show();
                                        clearFormFields();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(requireContext(), "Failed to publish post", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }
            }
        });

        return view;
    }


    private boolean validateInputs() {
        boolean isValid = true;

        // Validation for Type of User
        if (edtTypeOfUser.getText().toString().isEmpty()) {
            edtTypeOfUser.setError("Type of User is required");
            isValid = false;
        }

        // Validation for Type of Waste
        if (edtTypeOfWaste.getText().toString().isEmpty()) {
            edtTypeOfWaste.setError("Type of Waste is required");
            isValid = false;
        }

        // Validation for Quantity
        String quantity = edtQuantity.getText().toString();
        if (quantity.isEmpty()) {
            txtLayQuantity.setError("Quantity is required");
            isValid = false;
        } else {
            txtLayQuantity.setError(null);
        }

        // Validation for Weight
        String weight = edtWeight.getText().toString();
        if (weight.isEmpty()) {
            txtLayWeight.setError("Weight is required");
            isValid = false;
        } else {
            txtLayWeight.setError(null);
        }

        // Validation for Price (only if the user is a seller)
        String userType = edtTypeOfUser.getText().toString();
        if ("Seller(Restaurants, cafe, Super Markets, etc.)".equals(userType)) {
            String price = edtPrice.getText().toString();
            if (price.isEmpty()) {
                txtLayPrice.setError("Price is required for sellers");
                isValid = false;
            } else {
                txtLayPrice.setError(null);
            }
        }

        // Validation for Building
        if (edtBuilding.getText().toString().isEmpty()) {
            txtLayBuilding.setError("Building is required");
            isValid = false;
        } else {
            txtLayBuilding.setError(null);
        }

        // Validation for City
        if (edtCity.getText().toString().isEmpty()) {
            txtLayCity.setError("City is required");
            isValid = false;
        } else {
            txtLayCity.setError(null);
        }

        // Validation for Province
        if (edtProvince.getText().toString().isEmpty()) {
            txtLayProvince.setError("Province is required");
            isValid = false;
        } else {
            txtLayProvince.setError(null);
        }

        // Validation for Postal Code
        if (edtPostalCode.getText().toString().isEmpty()) {
            txtLayPostalCode.setError("Postal Code is required");
            isValid = false;
        } else {
            txtLayPostalCode.setError(null);
        }

        return isValid;
    }

    private void clearFormFields() {
        edtTypeOfUser.setText("");
        edtTypeOfWaste.setText("");
        edtQuantity.setText("");
        edtWeight.setText("");
        edtPrice.setText("");
        edtOtherDetails.setText("");
        edtBuilding.setText("");
        edtCity.setText("");
        edtProvince.setText("");
        edtPostalCode.setText("");
    }
}