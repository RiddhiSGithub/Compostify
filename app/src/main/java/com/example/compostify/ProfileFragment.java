package com.example.compostify;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.compostify.databinding.FragmentProfileBinding;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ProfileFragment extends Fragment implements View.OnClickListener {

    String name;
    String email;
    String businessEmail;
    String businessName;
    Uri logoURI;

    String password;
    String confirmPassword;

    String contactNumber;

    String street;
    String unitNo;
    String city;
    String province;
    String postalCode;
    private final int PICK_IMAGE_REQUEST = 1;
    String downloadURL;
    private FirebaseAuth firebaseAuth;
    private String userID;


    FragmentProfileBinding binding;
    private String userId;
    private FirebaseFirestore firebaseFirestore;
    private String address;
    private LatLng latLng;
    private String newDownloadURL = null;

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
        binding.edtStreet.setFocusable(false);
        Places.initialize(getContext(), "AIzaSyBTqCW_QQhBwo6hyVIsAGJ66jtZbtecyC0");
        setListeners();
        fillData();


        return binding.getRoot();
    }

    private void setListeners() {
        binding.btnSaveChanges.setOnClickListener(this);
        binding.btnLogOut.setOnClickListener(this);
        binding.btnSelectImage.setOnClickListener(this);
        binding.edtStreet.setOnClickListener(this);
        binding.edtBusinessPhoneNumber.addTextChangedListener(new CanadianPhoneNumberTextWatcher(binding.edtBusinessPhoneNumber));
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
                binding.edtStreet.setText(value.getString("address"));
                binding.edtUnitNumber.setText(value.getString("uni  tNo"));

                try {
                    Glide.with(getContext()).load(value.getString("downloadUrl")).into(binding.imgLogo);
                }catch (NullPointerException e)
                {

                }

                downloadURL = value.getString("downloadUrl");

            }
        });

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == binding.btnSaveChanges.getId()) {
            getUserInput();

            if (hasFieldError()) {
                new AlertDialog.Builder(getContext()) // Make sure to replace 'context' with a valid Context, e.g., YourActivityName.this
                        .setTitle("Confirmation") // Title of the dialog
                        .setMessage("Are you sure you want to save these changes?") // Message to show
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // This will be executed if the user clicks 'Yes'

                                String userId = firebaseAuth.getCurrentUser().getUid();
                                if(newDownloadURL == null)
                                {
                                    DocumentReference documentReference = firebaseFirestore.collection("users").document(userId);
                                    documentReference.update(getUpdatedData());
                                }
                                else {
                                    downloadURL = newDownloadURL;
                                    uploadImage(logoURI);
                                }


                                Toast.makeText(getContext(), "Changed Saved", Toast.LENGTH_LONG).show();
                            }
                        })
                        .setNegativeButton("No", null) // Null listener represents nothing happens on clicking 'No'
                        .show();
            }



        } else if (v.getId() == binding.btnLogOut.getId()) {
            firebaseAuth.signOut();
            startActivity(new Intent(getActivity(), LoginActivity.class));
            getActivity().finish();
        }else if(v.getId() == binding.btnSelectImage.getId()){
            openImageChooser();
        }else if (binding.edtStreet.getId() == v.getId()) {
            List<Place.Field> fieldList = Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.NAME);
            Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fieldList).build(getContext());
            startActivityForResult(intent, 100);
            startActivityForResult(intent, 100);
        }
    }
    private void uploadImage(Uri imageUri) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        int len = logoURI.getLastPathSegment().split("/").length;
        String imageName = logoURI.getLastPathSegment().split("/")[len - 1]+"-"+businessName+"-"+System.currentTimeMillis();

        StorageReference imagesRef = storageRef.child("LogoImages/" + imageName);

        UploadTask uploadTask = imagesRef.putFile(imageUri);

        uploadTask.addOnSuccessListener(taskSnapshot -> {
            imagesRef.getDownloadUrl().addOnSuccessListener(uri -> {
                newDownloadURL = uri.toString();
                DocumentReference documentReference = firebaseFirestore.collection("users").document(userId);
                documentReference.update(getUpdatedData());


            });
        }).addOnFailureListener(e ->
                Toast.makeText(getContext(), "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show());
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
        }


        return true;

    }

    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Logo"), PICK_IMAGE_REQUEST);
    }
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == RESULT_OK) {
            Place place = Autocomplete.getPlaceFromIntent(data);
            binding.edtStreet.setText(place.getAddress());
            address = place.getAddress();
            latLng = place.getLatLng();


        } else if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            logoURI = data.getData();
            Glide.with(this).load(logoURI).into(binding.imgLogo);
        }
    }
    private Map<String, Object> getUpdatedData() {
        Map<String, Object> newData = new HashMap<>();

        newData.put("userName", name);
        newData.put("userEmail", email);
        newData.put("businessName", businessName);
        newData.put("businessEmail", businessEmail);
        newData.put("businessContactNumber", contactNumber);
        newData.put("address", address);
        newData.put("latLng", latLng);
        newData.put("unitNo", unitNo);
        newData.put("city", city);
        newData.put("postalCode", postalCode);
        newData.put("downloadUrl",downloadURL);



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

    }
}