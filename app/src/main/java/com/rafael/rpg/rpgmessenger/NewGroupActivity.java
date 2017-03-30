package com.rafael.rpg.rpgmessenger;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rafael.rpg.dbwrappers.Group;
import com.rafael.rpg.dbproperties.DBProperties;

/**
 * Activities that creates a new group on behalf of the user and adds it to the database.
 */
public class NewGroupActivity extends AppCompatActivity {
    private static final String TAG = "EmailPassword";

    private EditText groupNameField;
    private Button createGroupButton;
    private DatabaseReference usersDBReference;
    private DatabaseReference groupsDBReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_group);

        groupNameField = (EditText) findViewById(R.id.groupName);
        createGroupButton = (Button) findViewById(R.id.createGroupButton);

        usersDBReference = FirebaseDatabase.getInstance().getReference(DBProperties.USERS_PATH);
        groupsDBReference = FirebaseDatabase.getInstance().getReference(DBProperties.GROUPS_PATH);

        initAuthListener();
        initCreateGroupButtonListener();
    }

    /**
     * Initializes the authentication listener that is called as soon as the authentication state changes (and once on creation).
     * In case the user is not authenticated returns to the login activity.
     */
    private void initAuthListener() {
        firebaseAuth = FirebaseAuth.getInstance();
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                firebaseAuth = FirebaseAuth.getInstance();
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:Login:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    startActivity(new Intent(NewGroupActivity.this, LoginActivity.class));
                }
            }
        };
    }

    /**
     * Initializes the listener on the "Create Group"-Button to create and store the new group.
     */
    private void initCreateGroupButtonListener(){
        createGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String groupName = groupNameField.getText().toString();

                if (groupName.equals("")) {
                    groupNameField.setError("can't be blank");
                } else {
                    storeGroupInDB(groupName);
                }
            }
        });
    }

    /**
     * Stores a new group in the database and creates the associations.
     * @param groupName The name of the group
     */
    private void storeGroupInDB(String groupName){
        String groupID  = groupsDBReference.push().getKey();
        Group group = new Group(groupName);
        group.addMember(firebaseAuth.getCurrentUser().getUid());

        // add group to group list
        groupsDBReference.child(groupID).setValue(group);
        // add group to user
        usersDBReference.child(firebaseAuth.getCurrentUser().getUid()).child("groups").push().setValue(groupID);

        startActivity(new Intent(NewGroupActivity.this, GroupsActivity.class));
    }
}
