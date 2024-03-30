package com.example.compostify;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


import com.bumptech.glide.Glide;
import com.example.compostify.databinding.ActivityCheckoutBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.List;


public class CheckoutActivity extends AppCompatActivity {

    ActivityCheckoutBinding binding;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    private String userId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityCheckoutBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_checkout);


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        fillData(getIntent().getStringExtra("publishId"));

    }

    private void fillData(String publishId) {

        DocumentReference documentReference = firebaseFirestore.collection("Publish").document(publishId);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                userId = value.getString("userId");

                getUsersInfo();

                if(value.getString("typeOfUser").equalsIgnoreCase("seller"))
                {
                    List<String> imageUrls = (List<String>) value.get("imageUrls");
                    if (!imageUrls.isEmpty()) {

                    }
                    String typeOfWaste = value.getString("typeOfWaste");
                    // Define your data for rows and columns, for example a 2D array
                    String[][] data = {
                            {"Natural Waste ", value.getString("naturalWasteWeight").equals("") ? "0 Kg" : value.getString("naturalWasteWeight") + " Kg", "$5 per 10 Kg"},
                            {"Mix Waste ", value.getString("mixWasteWeight").equals("") ? "Not Available" : value.getString("mixWasteWeight") + "kg", "$3 per 10 Kg"}

                    };

                    // Iterate over data and create TableRow for each row

                    binding.txtOtherDetail.setText(value.getString("otherDetails"));
                    binding.txtWeight.setText(value.getString("totalWeight"));

                }

            }
        });



    }

    void getUsersInfo(){
        DocumentReference documentReference1 = firebaseFirestore.collection("users").document(userId);
        documentReference1.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                binding.txtTypeOfUser.setText(value.getString("userName"));

                binding.txtBusinessName.setText(value.getString("businessName"));

//                binding.lblContact.setText(value.getString("businessContactNumber"));
                binding.txtBusinessAddress.setText(value.getString("address"));

                try {
                    Glide.with(CheckoutActivity.this).load(value.getString("downloadUrl")).into(binding.imgBusinessLogo);
                } catch (NullPointerException e) {

                }
            }
        });
    }
}