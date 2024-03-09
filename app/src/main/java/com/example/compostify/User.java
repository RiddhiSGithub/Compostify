package com.example.compostify;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class User {
    String name;
    String email;
    String businessEmail;
    String businessName;

    String password;


    String contactNumber;

    String street;
    String unitNo;
    String city;
    String province;
    String postalCode;

    FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFireStore;
    private String userID;

    public User(String name, String email, String businessEmail, String businessName, String password, String contactNumber, String street, String unitNo, String city, String province, String postalCode) {
        this.name = name;
        this.email = email;
        this.businessEmail = businessEmail;
        this.businessName = businessName;
        this.password = password;
        this.contactNumber = contactNumber;
        this.street = street;
        this.unitNo = unitNo;
        this.city = city;
        this.province = province;
        this.postalCode = postalCode;
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFireStore = FirebaseFirestore.getInstance();
    }


}
