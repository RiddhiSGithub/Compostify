package com.example.compostify;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class PublishFragment extends Fragment {


    private AutoCompleteTextView edtTypeOfUser;
    private AutoCompleteTextView edtTypeOfWaste;
    private TextInputEditText edtQuantity;
    private TextInputEditText edtWeight;
    private TextInputEditText edtOtherDetails;
    private TextInputEditText edtPostDate;
    private TextInputEditText edtBuilding;
    private TextInputEditText edtCity;
    private TextInputEditText edtProvince;
    private TextInputEditText edtPostalCode;

    private TextInputLayout txtLayQuantity;
    private TextInputLayout txtLayWeight;
    private TextInputLayout txtLayBulding;
    private TextInputLayout txtLayCity;
    private TextInputLayout txtLayProvince;
    private TextInputLayout txtLayPostalCode;
    private Calendar selectedDate;

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
        edtOtherDetails = view.findViewById(R.id.edtOtherDetails);
        edtPostDate = view.findViewById(R.id.edtPostDate);
        edtBuilding = view.findViewById(R.id.edtBuilding);
        edtCity = view.findViewById(R.id.edtCity);
        edtProvince = view.findViewById(R.id.edtProvince);
        edtPostalCode = view.findViewById(R.id.edtPostalCode);

        txtLayQuantity = view.findViewById(R.id.txtLayQuantity);
        txtLayWeight = view.findViewById(R.id.txtLayWeight);
        txtLayBulding = view.findViewById(R.id.txtLayBuilding);
        txtLayCity = view.findViewById(R.id.txtLayCity);
        txtLayProvince = view.findViewById(R.id.txtLayProvince);
        txtLayPostalCode = view.findViewById(R.id.txtLayPostalCode);

        // Set up AutoCompleteTextView with predefined options
        ArrayAdapter<String> wasteAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                new String[]{"Natural Waste", "Mix Waste","Both"}
        );
        edtTypeOfWaste.setAdapter(wasteAdapter);

        ArrayAdapter<String> userAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                new String[]{"Buyer(Fertilizer Companies, etc.)", "Seller(Restaurants, cafe, Super Markets, etc.)"}
        );
        edtTypeOfUser.setAdapter(userAdapter);

        setUpPostDateField();

        Button btnPublish = view.findViewById(R.id.btnPost);
        btnPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInputs()) {
                    // Perform your publish action here
                    // You can use the values from the EditText fields for further processing
                    Toast.makeText(requireContext(), "Publishing...", Toast.LENGTH_SHORT).show();
                    clearFormFields();
                }
            }
        });

        return view;
    }

    private void setUpPostDateField() {
        selectedDate = Calendar.getInstance();

        // Set the initial date in the EditText
        updatePostDateEditText();

        // Disable the field to prevent user input
        edtPostDate.setEnabled(false);

        // Remove the click listener for the post date field
        edtPostDate.setOnClickListener(null);
    }


    private void updatePostDateEditText() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        edtPostDate.setText(sdf.format(selectedDate.getTime()));
    }

    private boolean validateInputs() {
        boolean isValid = true;

        // Validation for Type of Waste
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

        // Validation for Building
        if (edtBuilding.getText().toString().isEmpty()) {
            txtLayBulding.setError("Building is required");
            isValid = false;
        } else {
            txtLayBulding.setError(null);
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
        edtOtherDetails.setText("");
        edtBuilding.setText("");
        edtCity.setText("");
        edtProvince.setText("");
        edtPostalCode.setText("");
        selectedDate = Calendar.getInstance();
        updatePostDateEditText();
    }
}