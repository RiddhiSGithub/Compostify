package com.example.compostify;

import android.os.Bundle;
import android.view.View;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.compostify.adapters.PhotoAdapter;
import com.example.compostify.databinding.ActivityWasteDetailsBinding;
import com.example.compostify.db.Order;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.stripe.android.PaymentConfiguration;

import java.util.List;


public class WasteDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    ActivityWasteDetailsBinding binding;
    private String sellerId;
    String publishId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWasteDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();


        PaymentConfiguration.init(getApplicationContext(), "pk_test_51OzmZfDIaLN1YyfY1mmha9m5vJoXuesGR8xQktRengYzSXQoukkuWqejP1UjMnsCIPK37Cjp82AWYNxesdNk3fHJ00Ssa6boOt");

        if(getIntent().getBooleanExtra("fromSearch",true))
        {
            publishId = getIntent().getStringExtra("publishId");
            fillData(publishId);

        }
        binding.btnPlaceOrder.setOnClickListener(this);
    }

    private void fillData(String publishId) {
         
        DocumentReference documentReference = firebaseFirestore.collection("Publish").document(publishId);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
               sellerId = value.getString("userId");

               getUsersInfo();

               if(value.getString("typeOfUser").equalsIgnoreCase("seller"))
               {
                   List<String> imageUrls = (List<String>) value.get("imageUrls");
                   if (!imageUrls.isEmpty()) {
                       setupRecyclerView(imageUrls);
                   }
                   String typeOfWaste = value.getString("typeOfWaste");
                   // Define your data for rows and columns, for example a 2D array
                   String[][] data = {
                           {"Natural Waste ", value.getString("naturalWasteWeight").equals("") ? "0 Kg" : value.getString("naturalWasteWeight") + " Kg", "$5 per 10 Kg"},
                           {"Mix Waste ", value.getString("mixWasteWeight").equals("") ? "Not Available" : value.getString("mixWasteWeight") + "kg", "$3 per 10 Kg"}

                   };

                   // Iterate over data and create TableRow for each row
                   for (int i = 0; i < data.length; i++) {
                       TableRow row = new TableRow(WasteDetailsActivity.this);

                       // Iterate over columns for each row and add TextViews as cells
                       for (int j = 0; j < data[i].length; j++) {
                           TextView cell = new TextView(WasteDetailsActivity.this);
                           cell.setText(data[i][j]);
                           row.addView(cell);
                       }

                       // Add the row to the TableLayout
                       binding.tblWasteTypes.addView(row);
                   }
                   binding.txtHandlingInstruction.setText(value.getString("otherDetails"));
                   binding.txtWeight.setText(value.getString("totalWeight"));

               }
               else
               {
                   binding.rvWastePhotos.setVisibility(View.GONE);
               }

            }
        });



    }
    void getUsersInfo(){
        DocumentReference documentReference1 = firebaseFirestore.collection("users").document(sellerId);
        documentReference1.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                binding.lblUserName.setText(value.getString("userName"));

                binding.lblBusinessName.setText(value.getString("businessName"));

                binding.lblContact.setText(value.getString("businessContactNumber"));
                binding.lblAddress.setText(value.getString("address"));

                try {
                    Glide.with(WasteDetailsActivity.this).load(value.getString("downloadUrl")).into(binding.imgLogo);
                } catch (NullPointerException e) {

                }
            }
        });
    }

    private void setupRecyclerView(List<String> imageUrls) {
        RecyclerView rvWastePhotos = binding.rvWastePhotos;
        PhotoAdapter adapter = new PhotoAdapter(WasteDetailsActivity.this, imageUrls);
        rvWastePhotos.setAdapter(adapter);
        rvWastePhotos.setLayoutManager(new LinearLayoutManager(WasteDetailsActivity.this, LinearLayoutManager.HORIZONTAL, false));
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == binding.btnPlaceOrder.getId()){
//            String purchaserId = firebaseAuth.getCurrentUser().getUid();
            Order order = new Order(
                    publishId,
                    sellerId,
                    firebaseAuth.getCurrentUser().getUid()
            );


            firebaseFirestore.collection("Orders").add(order.toHashMap()).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    order.setOrderId(documentReference.getId());
                    Toast.makeText(WasteDetailsActivity.this, "Your order has been placed successfully", Toast.LENGTH_SHORT).show();
                }
            });
            firebaseFirestore.collection("Publish").document(publishId).update(
                    "postStatus","Deactive"
            );
//            Intent intent = new Intent(v.getContext(), HomeFragment.class);
//            // Start the ActivityEditPost
//            v.getContext().startActivity(intent);
        }
    }
}