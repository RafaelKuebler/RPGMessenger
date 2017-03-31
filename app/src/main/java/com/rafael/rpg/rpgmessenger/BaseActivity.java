package com.rafael.rpg.rpgmessenger;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class BaseActivity extends AppCompatActivity {
    private static final String USERS_PATH = "users";
    private static final String GROUPS_PATH = "groups";
    private static final String MESSAGES_PATH = "messages";
    private static final String TAG = "RPGMessenger";

    protected FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authListener;
    protected FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDB;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDB = FirebaseDatabase.getInstance();

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
        log("onAuthStateChanged:signed_in: " + firebaseUser.getUid());
    }

    protected void onUserSignOut() {
        log("onAuthStateChanged:signed_out");
    }

    protected final DatabaseReference getDBRefToUsers(){
        return firebaseDB.getReference(USERS_PATH);
    }

    protected final DatabaseReference getDBRefToGroups(){
        return firebaseDB.getReference(GROUPS_PATH);
    }

    protected final DatabaseReference getDBRefToCurrentUser(){
        return getDBRefToUser(firebaseUser.getUid());
    }

    protected final DatabaseReference getDBRefToUser(String userID){
        return getDBRefToUsers().child(userID);
    }

    protected final DatabaseReference getDBRefToCurrentUserGroups(){
        return getDBRefToUserGroups(firebaseUser.getUid());
    }

    protected final DatabaseReference getDBRefToUserGroups(String userID){
        return getDBRefToUser(userID).child(GROUPS_PATH);
    }

    protected final DatabaseReference getDBRefToGroup(String groupID){
        return getDBRefToGroups().child(groupID);
    }

    protected final DatabaseReference getDBRefToGroupMessages(String groupID){
        return getDBRefToGroup(groupID).child(MESSAGES_PATH);
    }

    @Override
    public void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authListener);
    }

    protected final void log(String text){
        Log.d(TAG, text);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            firebaseAuth.removeAuthStateListener(authListener);
        }
    }
}
