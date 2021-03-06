package com.rafael.rpg.rpgmessenger;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rafael.rpg.dbwrappers.Group;

import java.util.ArrayList;

public class GroupsActivity extends BaseActivity {
    private ListView groupsList;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> groupNameList = new ArrayList<>();
    private ArrayList<String> groupIDList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups);

        groupsList = (ListView) findViewById(R.id.groupsList);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, groupNameList);
        groupsList.setAdapter(adapter);

        initGroupsListListener();
    }

    @Override
    protected void onUserSignIn() {
        super.onUserSignIn();

        fetchGroups();
    }

    @Override
    protected void onUserSignOut() {
        super.onUserSignOut();

        Intent intent = new Intent(GroupsActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    /**
     * Initializes the listener on the list view to detect when an item was clicked and start the chat activity.
     */
    public void initGroupsListListener() {
        groupsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // get group id and pass to chat activity
                Intent chatActivityIntent = new Intent(GroupsActivity.this, ChatActivity.class);
                chatActivityIntent.putExtra("groupID", groupIDList.get(position));
                chatActivityIntent.putExtra("groupName", groupNameList.get(position));
                startActivity(chatActivityIntent);
            }
        });
    }

    private void fetchGroups() {
        getDBRefToCurrentUserGroups().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    // get the group ID of each child
                    final String groupID = child.getValue().toString();

                    // set up listener to get the group data
                    DatabaseReference firebaseDB2 = FirebaseDatabase.getInstance().getReference("/groups/" + groupID);
                    firebaseDB2.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) { // in case the group was changed (deleted etc.)
                                // map the group to a Group.class instance and print the title
                                Group group = dataSnapshot.getValue(Group.class);

                                // create a new element in the list view for the group
                                if (!groupIDList.contains(groupID)) {
                                    groupNameList.add(group.getTitle());
                                    groupIDList.add(groupID);
                                    adapter.notifyDataSetChanged();
                                }
                            } else { // in case the group was deleted
                                groupNameList.remove(groupIDList.indexOf(groupID));
                                groupIDList.remove(groupID);
                                adapter.notifyDataSetChanged();
                            }
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
        switch (item.getItemId()) {
            case R.id.new_group:
                startActivity(new Intent(GroupsActivity.this, NewGroupActivity.class));
                return true;
            case R.id.settings:
                startActivity(new Intent(GroupsActivity.this, SettingsActivity.class));
                return true;
            case R.id.app_about:
                startActivity(new Intent(GroupsActivity.this, AboutActivity.class));
                return true;
            case R.id.log_out:
                FirebaseAuth.getInstance().signOut();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}