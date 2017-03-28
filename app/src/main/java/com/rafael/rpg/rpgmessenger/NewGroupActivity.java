package com.rafael.rpg.rpgmessenger;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rafael.rpg.messengerclasses.Group;

public class NewGroupActivity extends AppCompatActivity {
    private DatabaseReference firebaseDB;
    private boolean groupAlreadyExists = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_group);
    }

    private void buttonPressed(){
        final Group newGroup = new Group("1", "TestGroup");

        doesGroupExistInDB(newGroup);
        storeGroupInDB(newGroup);
    }

    private void doesGroupExistInDB(final Group group){
        firebaseDB = FirebaseDatabase.getInstance().getReference("groups/" + group.getGroupID());

        firebaseDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()) {
                    groupAlreadyExists = false;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    private void storeGroupInDB(final Group group){
        if(groupAlreadyExists){
            Toast.makeText(NewGroupActivity.this, "GroupID already exists in DB.",
                    Toast.LENGTH_SHORT).show();
        } else {
            firebaseDB.child(group.getGroupID()).setValue(group);
            startActivity(new Intent(NewGroupActivity.this, GroupsActivity.class));
        }
    }
}
