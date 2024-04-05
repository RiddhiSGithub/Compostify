package com.example.compostify;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.LOCATION_SERVICE;



import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.Manifest;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;


public class SearchFragment extends Fragment  {

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
    View view;
    EditText searchTxt;
    private String address;
    TextView emptyView;

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

         view = inflater.inflate(R.layout.fragment_search, container, false);

         searchTxt = view.findViewById(R.id.edtAddress);

        getLatLng(view);
        Places.initialize(getContext(), "AIzaSyBTqCW_QQhBwo6hyVIsAGJ66jtZbtecyC0");
        searchFetaure(searchTxt);

        searchTxt.setFocusable(false);
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
        emptyView = view.findViewById(R.id.tvEmptyRecyclerView);
        firebaseAuth = FirebaseAuth.getInstance();
        userArrayList = new ArrayList<User>();
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(getActivity(),new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION
            },100);
        }

        loadDataFromFirestore(latitude, longitude);
//        getCurrentLocationLatLng();

        return view;
    }

    private void searchFetaure(EditText searchTxt) {
        searchTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Place.Field> fieldList = Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.NAME);
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fieldList).build(getContext());
                startActivityForResult(intent, 100);
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == RESULT_OK) {
            Place place = Autocomplete.getPlaceFromIntent(data);
           searchTxt.setText(place.getAddress());
            address = place.getAddress();
            LatLng temp = place.getLatLng();
            latitude = temp.latitude;
            longitude = temp.longitude;
            loadDataFromFirestore(latitude,longitude);
        }
    }
    private void getLatLng(View view) {
        GPSTracker gpsTracker = new GPSTracker(getContext());

        if (gpsTracker.getIsGPSTrackingEnabled())
        {
            latitude = gpsTracker.latitude;

            longitude = gpsTracker.longitude;




            String country = gpsTracker.getCountryName(getContext());



            String city = gpsTracker.getLocality(getContext());


            String postalCode = gpsTracker.getPostalCode(getContext());


            String addressLine = gpsTracker.getAddressLine(getContext());


            EditText searchTxt = view.findViewById(R.id.edtAddress);
            searchTxt.setText("" + addressLine );
        }
        else
        {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gpsTracker.showSettingsAlert();
        }
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

    private void loadDataFromFirestore( double latitude, double longitude) {
        userId = firebaseAuth.getCurrentUser().getUid();
        typeOfUser = "";
        DocumentReference documentReference = firebaseFirestore.collection("users").document(userId);
        double radiusInKm = 10; // Radius within which to fetch nearby data
        CollectionReference collectionReference = firebaseFirestore.collection("Publish");
        // Query to retrieve nearby data
        Query query = collectionReference.orderBy("latitude")
                .startAt(latitude - (radiusInKm / 111.12))
                .endAt(latitude + (radiusInKm / 111.12));
                 // Limit the number of results
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                typeOfUser = value.getString("typeOfUser");
            }
        });

        List<User> dataList = new ArrayList<>();
        query.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if(task.isSuccessful())
                        {


                            emptyView.setVisibility(View.GONE);


                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {

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

                            if(task.getResult().isEmpty()){
                                dataList.clear();
                                wasteListAdapter.setData(dataList);
                                wasteListAdapter.notifyDataSetChanged();
                                emptyView.setVisibility(View.VISIBLE);

                                if(progressDialog.isShowing())
                                {
                                    progressDialog.dismiss();
                                }

                            }
                        }

                    }
                })

                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("TAG", "Error getting documents: ", e);
                    }
                });

    }



}