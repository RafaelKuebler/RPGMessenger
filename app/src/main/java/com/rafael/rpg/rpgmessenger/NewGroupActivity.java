package com.rafael.rpg.rpgmessenger;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rafael.rpg.messengerclasses.Group;
import com.rafael.rpg.properties.DBProperties;

/**
 * Activities that creates a new group on behalf of the user and adds it to the database.
 */
public class NewGroupActivity extends AppCompatActivity {
    private static final String TAG = "EmailPassword";

    private EditText groupNameField;
    private Button createGroupButton;
    private Button checkGroupButton;
    private DatabaseReference usersDBReference;
    private DatabaseReference groupsDBReference;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_group);

        groupNameField = (EditText) findViewById(R.id.groupName);
        createGroupButton = (Button) findViewById(R.id.createGroupButton);
        checkGroupButton = (Button) findViewById(R.id.checkGroupButton);
        firebaseAuth = FirebaseAuth.getInstance();

        // deactivate the button until the validity of the data is checked
        checkGroupButton.setEnabled(false);

        usersDBReference = FirebaseDatabase.getInstance().getReference(DBProperties.USERS_PATH);
        groupsDBReference = FirebaseDatabase.getInstance().getReference(DBProperties.GROUPS_PATH);

        //initCheckGroupButtonListener();
        initCreateGroupButtonListener();
    }

    /**
     * Initializes the listener on the "Check Group"-Button to check the existence of a group with that name in the database.
     */
    private void initCheckGroupButtonListener(){
        checkGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String groupName = groupNameField.getText().toString();

                doesGroupExistInDB(groupName);
            }
        });
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
     * Checks if the group already exists in the database.
     * @param groupName The name of the group
     */
    private void doesGroupExistInDB(String groupName){
        groupsDBReference.child(groupName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()) {
                    // the group does not exist in the database
                    createGroupButton.setEnabled(true);
                } else {
                    // the group exists
                    createGroupButton.setEnabled(false);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
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
