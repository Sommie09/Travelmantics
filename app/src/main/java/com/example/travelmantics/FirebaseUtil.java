package com.example.travelmantics;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FirebaseUtil {
    public static ArrayList<TravelDeal> mDeals;
    public static FirebaseDatabase mFirebaseDatabase;
    public static DatabaseReference mDatabaseReference;
    private static FirebaseUtil mFirebaseUtil;
    public static FirebaseAuth sFirebaseAuth;
    public static FirebaseStorage mFirebaseStorage;
    public static StorageReference mStorageReference;
    public static FirebaseAuth.AuthStateListener sAuthStateListener;
    public static boolean isAdmin;

    private static final int RC_SIGN_IN = 123;
    private static ListActivity caller;

    public FirebaseUtil(){};

    public static void openFbReference(String ref, final Activity callerActivity){
        if(mFirebaseUtil == null){
            mFirebaseUtil = new FirebaseUtil();
            mFirebaseDatabase = FirebaseDatabase.getInstance();
            sFirebaseAuth = FirebaseAuth.getInstance();
            caller = (ListActivity) callerActivity;
            sAuthStateListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    if(firebaseAuth.getCurrentUser() == null){
                        FirebaseUtil.signIn();
                        }else{
                        String userID = firebaseAuth.getUid();
                        checkAdmin(userID);
                    }
                        Toast.makeText(callerActivity.getBaseContext(), "Welcome Back", Toast.LENGTH_SHORT).show();
                }
            };
        }
        mDeals = new ArrayList<>();

        mDatabaseReference = mFirebaseDatabase.getReference().child(ref);
    }

    public static void checkAdmin(String uid){
        FirebaseUtil.isAdmin = false;
        DatabaseReference reference = mFirebaseDatabase.getReference().child("administrator").child(uid);
        ChildEventListener listener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                FirebaseUtil.isAdmin = true;
                caller.showMenu();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
    }

    public static void signIn(){
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());

        // Create and launch sign-in intent
        caller.startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
    }

    public static void attachListener(){
        sFirebaseAuth.addAuthStateListener(sAuthStateListener);
    }

    public static void detachListener(){
        sFirebaseAuth.removeAuthStateListener(sAuthStateListener);
    }

    public static void connectStorage(){
        mFirebaseStorage = FirebaseStorage.getInstance();
        mStorageReference = mFirebaseStorage.getReference().child("deal_pictures");
    }



}
