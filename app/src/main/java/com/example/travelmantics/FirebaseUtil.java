package com.example.travelmantics;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class FirebaseUtil {
    public static ArrayList<TravelDeal> mDeals;
    public static FirebaseDatabase mFirebaseDatabase;
    public static DatabaseReference mDatabaseReference;
    private static FirebaseUtil mFirebaseUtil;

    public FirebaseUtil(){};

    public static void openFbReference(String ref){
        if(mFirebaseUtil == null){
            mFirebaseUtil = new FirebaseUtil();
            mFirebaseDatabase = FirebaseDatabase.getInstance();

            mDeals = new ArrayList<>();
        }

        mDatabaseReference = mFirebaseDatabase.getReference().child(ref);
    }



}
