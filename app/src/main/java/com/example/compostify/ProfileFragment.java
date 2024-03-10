package com.example.compostify;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.compostify.databinding.FragmentProfileBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;


public class ProfileFragment extends Fragment implements View.OnClickListener {

    String name;
    String email;
    String businessEmail;
    String businessName;

    String password;
    String confirmPassword;

    String contactNumber;

    String street;
    String unitNo;
    String city;
    String province;
    String postalCode;
    private FirebaseAuth firebaseAuth;
    private String userID;
    private DocumentReference documentReference;
    private FirebaseFirestore firebaseFireStore;

    FragmentProfileBinding binding;
    private String userId;
    private FirebaseFirestore firebaseFirestore;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_profile, container, false);
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        binding.btnSaveChanges.setOnClickListener(this);
        binding.btnLogOut.setOnClickListener(this);
        fillData();


        return binding.getRoot();
    }

    private void fillData() {
        userId = firebaseAuth.getCurrentUser().getUid();
        DocumentReference documentReference = firebaseFirestore.collection("users").document(userId);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                binding.edtUserName.setText(value.getString("userName"));
                binding.edtUserEmail.setText(value.getString("userEmail"));
                binding.edtBusinessName.setText(value.getString("businessName"));
                binding.edtBusinessEmail.setText(value.getString("businessEmail"));
                binding.edtBusinessPhoneNumber.setText(value.getString("businessContactNumber"));
                binding.edtStreet.setText(value.getString("street"));
                binding.edtUnitNumber.setText(value.getString("unitNo"));
                binding.edtCity.setText(value.getString("city"));
                binding.edtPcode.setText(value.getString("postalCode"));
            }
        });

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == binding.btnSaveChanges.getId()) {

            if (hasFieldError()) {
                new AlertDialog.Builder(getContext()) // Make sure to replace 'context' with a valid Context, e.g., YourActivityName.this
                        .setTitle("Confirmation") // Title of the dialog
                        .setMessage("Are you sure you want to save these changes?") // Message to show
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // This will be executed if the user clicks 'Yes'
                                getUserInput();
                                String userId = firebaseAuth.getCurrentUser().getUid();
                                DocumentReference documentReference = firebaseFirestore.collection("users").document(userId);
                                documentReference.update(getUpdatedData());
                                Toast.makeText(getContext(), "Changed Saved", Toast.LENGTH_LONG).show();
                            }
                        })
                        .setNegativeButton("No", null) // Null listener represents nothing happens on clicking 'No'
                        .show();
            }

            // Show confirmation dialog

        } else if (v.getId() == binding.btnLogOut.getId()) {
            firebaseAuth.signOut();
            startActivity(new Intent(getActivity(), LoginActivity.class));
            getActivity().finish();
        }
    }

    private boolean hasFieldError() {
        if (TextUtils.isEmpty(name)) {
            binding.edtUserName.setError("Name Cannot be empty");
            return false;
        } else if (TextUtils.isEmpty(email)) {
            binding.edtUserEmail.setError("Email Cannot be empty");
            return false;
        } else if (TextUtils.isEmpty(businessName)) {
            binding.edtBusinessName.setError("Business Name Cannot be empty");
            return false;
        } else if (TextUtils.isEmpty(businessEmail)) {
            binding.edtBusinessEmail.setError("Business Email Cannot be empty");
            return false;
        } else if (TextUtils.isEmpty(contactNumber)) {
            binding.edtBusinessPhoneNumber.setError("Contact Cannot be empty");
            return false;
        } else if (TextUtils.isEmpty(street)) {
            binding.edtStreet.setError("Street Name Cannot be empty");
            return false;
        } else if (TextUtils.isEmpty(city)) {
            binding.edtCity.setError("City Cannot be empty");
            return false;
        } else if (TextUtils.isEmpty(province)) {
            binding.edtProvince.setError("Province Cannot be empty");
            return false;
        } else if (TextUtils.isEmpty(city)) {
            binding.edtPcode.setError("Postal Code Cannot be empty");
            return false;
        }


        return true;

    }

    private Map<String, Object> getUpdatedData() {
        Map<String, Object> newData = new HashMap<>();

        newData.put("userName", name);
        newData.put("userEmail", email);
        newData.put("businessName", businessName);
        newData.put("businessEmail", businessEmail);
        newData.put("businessContactNumber", contactNumber);
        newData.put("street", street);
        newData.put("unitNo", unitNo);
        newData.put("city", city);
        newData.put("postalCode", postalCode);


        return newData;
    }

    private void getUserInput() {
        name = String.valueOf(binding.edtUserName.getText());
        email = String.valueOf(binding.edtUserEmail.getText());
        businessEmail = String.valueOf(binding.edtBusinessEmail.getText());
        businessName = String.valueOf(binding.edtBusinessName.getText());
        contactNumber = String.valueOf(binding.edtBusinessPhoneNumber.getText());
        street = String.valueOf(binding.edtStreet.getText());
        unitNo = String.valueOf(binding.edtUnitNumber.getText());
        city = String.valueOf(binding.edtCity.getText());
        postalCode = String.valueOf(binding.edtPcode.getText());
        province = String.valueOf(binding.edtProvince.getText());

    }
}