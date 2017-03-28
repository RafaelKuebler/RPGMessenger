package com.rafael.rpg.rpgmessenger;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rafael.rpg.messengerclasses.Group;
import com.rafael.rpg.messengerclasses.User;

public class GroupsActivity extends AppCompatActivity {
    private static final String TAG = "EmailPassword";

    private ListView usersList;
    protected TextView noUsersText;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authListener;
    private DatabaseReference firebaseDB;
    private User user = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups);

        usersList = (ListView)findViewById(R.id.usersList);
        noUsersText = (TextView)findViewById(R.id.noUsersText);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDB = FirebaseDatabase.getInstance().getReference();

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:GroupsActivity:" + firebaseUser.getUid());
                    fetchGroups();
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    startActivity(new Intent(GroupsActivity.this, LoginActivity.class));
                }
            }
        };
    }

    private void fetchGroups(){
        Log.d(TAG, "Fetching groups for: " + firebaseAuth.getCurrentUser().getUid());
        firebaseDB = FirebaseDatabase.getInstance().getReference("users/" + firebaseAuth.getCurrentUser().getUid() + "/groups/");

        firebaseDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "Found groups: " + dataSnapshot.exists());
                for(DataSnapshot child : dataSnapshot.getChildren()){
                    // get the group ID
                    String groupID = child.getValue().toString();
                    Log.d(TAG, "GroupNames: " + groupID);

                    // set up listener to get the group data
                    DatabaseReference firebaseDB2 = FirebaseDatabase.getInstance().getReference("/groups/" + groupID + "/");
                    firebaseDB2.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // map the group to a Group.class instance and print the title
                            Group group = dataSnapshot.getValue(Group.class);
                            Log.d(TAG, group.getTitle());

                            // create a new element in the listview
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            System.out.println("The read failed: " + databaseError.getCode());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_groups, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "selected options menu");
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.new_group:
                startActivity(new Intent(GroupsActivity.this, NewGroupActivity.class));
                return true;
            case R.id.settings:
                startActivity(new Intent(GroupsActivity.this, SettingsActivity.class));
                return true;
            case R.id.app_info:
                startActivity(new Intent(GroupsActivity.this, InfoActivity.class));
                return true;
            case R.id.log_out:
                FirebaseAuth.getInstance().signOut();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onStart(){
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