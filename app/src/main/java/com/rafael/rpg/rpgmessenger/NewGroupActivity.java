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
    private DatabaseReference usersDBReference;
    private DatabaseReference groupsDBReference;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_group);

        groupNameField = (EditText) findViewById(R.id.groupName);
        createGroupButton = (Button) findViewById(R.id.createGroupButton);
        firebaseAuth = FirebaseAuth.getInstance();

        usersDBReference = FirebaseDatabase.getInstance().getReference(DBProperties.USERS_PATH);
        groupsDBReference = FirebaseDatabase.getInstance().getReference(DBProperties.GROUPS_PATH);

        initCreateGroupButtonListener();
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
