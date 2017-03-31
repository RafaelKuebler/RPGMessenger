package com.rafael.rpg.rpgmessenger;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.rafael.rpg.dbwrappers.Group;

/**
 * Activities that creates a new group on behalf of the user and adds it to the database.
 */
public class NewGroupActivity extends BaseActivity {
    private EditText groupNameField;
    private Button createGroupButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_group);

        groupNameField = (EditText) findViewById(R.id.groupName);
        createGroupButton = (Button) findViewById(R.id.createGroupButton);

        initCreateGroupButtonListener();
    }

    @Override
    protected void onUserSignOut() {
        super.onUserSignOut();

        finish();
    }

    /**
     * Initializes the listener on the "Create Group"-Button to create and store the new group.
     */
    private void initCreateGroupButtonListener(){
        createGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String groupName = groupNameField.getText().toString();

                if (isGroupDataValid(groupName)) {
                    storeGroupInDB(groupName);
                    finish();
                }
            }
        });
    }

    public boolean isGroupDataValid(String groupName){
        if (groupName.equals("")) {
            groupNameField.setError("can't be blank");
            return false;
        } else {
            return true;
        }
    }

    /**
     * Stores a new group in the database and creates the associations.
     * @param groupName The name of the group
     */
    private void storeGroupInDB(String groupName){
        String groupID  = getDBRefToGroups().push().getKey();
        Group group = new Group(groupName);
        group.addMember(firebaseUser.getUid());

        // add group to group list
        getDBRefToGroup(groupID).setValue(group);
        // add group to user
        getDBRefToCurrentUserGroups().push().setValue(groupID);

        startActivity(new Intent(NewGroupActivity.this, GroupsActivity.class));
    }
}
