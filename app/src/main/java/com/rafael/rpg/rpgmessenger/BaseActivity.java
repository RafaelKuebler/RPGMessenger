package com.rafael.rpg.rpgmessenger;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class BaseActivity extends AppCompatActivity {
    protected static final String TAG = "EmailPassword";
    protected FirebaseAuth firebaseAuth;
    protected FirebaseAuth.AuthStateListener authListener;
    protected FirebaseUser firebaseUser;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null) {
                    onUserSignIn();
                } else {
                    onUserSignOut();
                }
            }
        };
    }

    protected void onUserSignIn() {
        Log.d(TAG, "onAuthStateChanged:signed_in: " + firebaseUser.getUid());
    }

    protected void onUserSignOut() {
        Log.d(TAG, "onAuthStateChanged:signed_out");
    }

    @Override
    public void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            firebaseAuth.removeAuthStateListener(authListener);
        }
    }
}
