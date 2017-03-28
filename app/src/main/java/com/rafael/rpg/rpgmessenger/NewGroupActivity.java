package com.rafael.rpg.rpgmessenger;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rafael.rpg.messengerclasses.Group;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public class NewGroupActivity extends AppCompatActivity {
    private static final String TAG = "EmailPassword";

    private EditText groupID;
    private EditText groupName;
    private Button createGroupButton;
    private Button checkGroupButton;
    private DatabaseReference firebaseDB;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_group);

        groupID = (EditText) findViewById(R.id.groupID);
        groupName = (EditText) findViewById(R.id.groupName);
        createGroupButton = (Button) findViewById(R.id.createGroupButton);
        checkGroupButton = (Button) findViewById(R.id.checkGroupButton);
        createGroupButton.setEnabled(false);

        firebaseAuth = FirebaseAuth.getInstance();

        checkGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String groupIDText = groupID.getText().toString();
                String groupNameText = groupName.getText().toString();

                if (groupIDText.equals("")) {
                    groupID.setError("can't be blank");
                } else if (groupNameText.equals("")) {
                    groupName.setError("can't be blank");
                } else {
                    doesGroupExistInDB(groupIDText);
                }
            }
        });

        createGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String groupIDText = groupID.getText().toString();
                String groupNameText = groupName.getText().toString();

                if (groupIDText.equals("")) {
                    groupID.setError("can't be blank");
                } else if (groupNameText.equals("")) {
                    groupName.setError("can't be blank");
                } else {
                    storeGroupInDB(groupNameText, groupIDText);
                }
            }
        });
    }

    private void doesGroupExistInDB(String groupID){
        firebaseDB = FirebaseDatabase.getInstance().getReference("groups/" + groupID);

        firebaseDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()) {
                    createGroupButton.setEnabled(true);
                } else {
                    createGroupButton.setEnabled(false);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

        firebaseDB = FirebaseDatabase.getInstance().getReference();
    }

    private void storeGroupInDB(String groupNameText, String groupIDText){
        String key  = firebaseDB.child("groups").push().getKey();
        Log.d(TAG, "Pushed: " + key);
        Group group = new Group(groupNameText);
        Log.d(TAG, "Added user to group");
        group.addMember(firebaseAuth.getCurrentUser().getUid());
        Log.d(TAG, "Created new group: " + groupNameText);
        //Map<String, Object> groupData = group.toMap();
        //Log.d(TAG, "Mapped group");

        Map<String, Object> childUpdates = new HashMap<>();

        // add group to group list
        //childUpdates.put("/groups/" + key, groupData);
        firebaseDB.child("groups").child(key).setValue(group);
        Log.d(TAG, "Added group to group list");
        // add group to user
        //childUpdates.put("/users/" + firebaseAuth.getCurrentUser().getUid() + "/groups/", key);
        firebaseDB.child("users").child(firebaseAuth.getCurrentUser().getUid()).child("groups").push().setValue(key);
        //firebaseDB.child("users").child(firebaseAuth.getCurrentUser().getUid()).child("groups").setValue(key);
        Log.d(TAG, "Added group to user");

        //firebaseDB.updateChildren(childUpdates);

        startActivity(new Intent(NewGroupActivity.this, GroupsActivity.class));
    }
}
