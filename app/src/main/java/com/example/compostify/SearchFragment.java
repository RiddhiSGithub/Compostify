package com.example.compostify;

import static android.content.Context.LOCATION_SERVICE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class SearchFragment extends Fragment implements LocationListener {

    RecyclerView recyclerView;
    ArrayList<User> userArrayList;
    WasteListAdapter wasteListAdapter;
    FirebaseFirestore firebaseFirestore;

    String userId;
    ProgressDialog progressDialog;
    LocationManager locationManager;
    private double longitude;
    private double latitude;
    private FirebaseAuth firebaseAuth;
    private String typeOfUser;


    public SearchFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_search, container, false);

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Fetching Data...");
        progressDialog.show();
        recyclerView = view.findViewById(R.id.rvWasteList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        wasteListAdapter = new WasteListAdapter(getContext(), userArrayList);
        recyclerView.setAdapter(wasteListAdapter);
        firebaseFirestore = FirebaseFirestore.getInstance();
        TextView emptyView = view.findViewById(R.id.tvEmptyRecyclerView);
        firebaseAuth = FirebaseAuth.getInstance();
        userArrayList = new ArrayList<User>();
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(getActivity(),new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION
            },100);
        }

        loadDataFromFirestore(emptyView);
        getCurrentLocationLatLng();

        return view;
    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocationLatLng() {

        try {
            locationManager = (LocationManager) getContext().getSystemService(LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,5000,5,this);
            Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void loadDataFromFirestore(TextView emptyView) {
        userId = firebaseAuth.getCurrentUser().getUid();
        typeOfUser = "";
        DocumentReference documentReference = firebaseFirestore.collection("users").document(userId);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                typeOfUser = value.getString("typeOfUser");
            }
        });

        List<User> dataList = new ArrayList<>();
        firebaseFirestore.collection("Publish")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {


                        if(queryDocumentSnapshots.isEmpty())
                        {
                           emptyView.setVisibility(View.VISIBLE);
                           progressDialog.dismiss();
                           return;
                        }

                            emptyView.setVisibility(View.GONE);


                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                            Log.e("type of user", documentSnapshot.getString("typeOfUser"));
                            if (documentSnapshot.getString("postStatus").toLowerCase().equals("active")
                            && !documentSnapshot.getString("typeOfUser").toLowerCase().equals(typeOfUser.toLowerCase()))
                            {

                                    User user = documentSnapshot.toObject(User.class);
                                    user.setUserId(documentSnapshot.getId());
                                    DocumentReference documentReference = firebaseFirestore.collection("users").document(documentSnapshot.getString("userId"));
                                    documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                        @Override
                                        public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                                            user.setBusinessName(value.getString("businessName"));
                                            user.setDownloadUrl(value.getString("downloadUrl"));
                                            try{
                                                String[] address = value.getString("address").split(",");
                                                String cityName = address[1] + ", " + address[2];
                                                user.setCityName(cityName);
                                            }catch (NullPointerException e){
                                                    e.printStackTrace();
                                            }

                                            dataList.add(user);
                                            wasteListAdapter.setData(dataList);
                                            wasteListAdapter.notifyDataSetChanged();
                                        }
                                    });
                                    Log.e("user", ""+user.getDownloadUrl());
                                    Log.e("user", ""+user.getBusinessName());


                            }



                        }


                        progressDialog.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("TAG", "Error getting documents: ", e);
                    }
                });

    }

    @Override
    public void onLocationChanged(@NonNull Location location) {

        try {
            Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
            String address = addresses.get(0).getAddressLine(0);
            latitude = location.getLatitude();
            longitude = location.getLongitude();
//            Toast.makeText(getContext(), address, Toast.LENGTH_LONG).show();


        }catch (Exception e){
            Log.e("current location error", "print stack tree");
            e.printStackTrace();
        }

    }
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}