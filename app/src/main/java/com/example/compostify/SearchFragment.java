package com.example.compostify;

import static android.content.Context.LOCATION_SERVICE;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.Manifest;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

    ProgressDialog progressDialog;
    LocationManager locationManager;
    private double longitude;
    private double latitude;


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
        userArrayList = new ArrayList<User>();
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(getActivity(),new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION
            },100);
        }

        loadDataFromFirestore();
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

    private void loadDataFromFirestore() {
        List<User> dataList = new ArrayList<>();
        firebaseFirestore.collection("Publish")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            User user = documentSnapshot.toObject(User.class);
                            DocumentReference documentReference = firebaseFirestore.collection("users").document(user.getUserId());
                            documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                @Override
                                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                                    user.setBusinessName(value.getString("businessName"));
                                    user.setDownloadUrl(value.getString("downloadUrl"));
                                    String[] address = value.getString("address").split(",");
                                    String cityName = address[1] + ", "+address[2];
                                    user.setCityName(cityName);
                                }
                            });
                            Log.e("user", ""+user.getDownloadUrl());
                            Log.e("user", ""+user.getBusinessName());
                            dataList.add(user);
                        }

                        wasteListAdapter.setData(dataList);
                        wasteListAdapter.notifyDataSetChanged();
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
            Toast.makeText(getContext(), address, Toast.LENGTH_LONG).show();


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