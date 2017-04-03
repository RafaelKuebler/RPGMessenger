package com.rafael.rpg.rpgmessenger;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.rafael.rpg.dbwrappers.Message;
import com.rafael.rpg.diceroller.DiceRoller;

public class ChatActivity extends BaseActivity {
    private String groupID;
    private LinearLayout layout;
    private ImageView sendButton;
    private EditText messageField;
    private ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        layout = (LinearLayout) findViewById(R.id.layout1);
        sendButton = (ImageView) findViewById(R.id.sendButton);
        messageField = (EditText) findViewById(R.id.messageArea);
        scrollView = (ScrollView) findViewById(R.id.scrollView);

        groupID = getIntent().getStringExtra("groupID");
        setTitle(getIntent().getStringExtra("groupName"));

        initSendButtonListener();
    }

    private void initSendButtonListener() {
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = messageField.getText().toString();

                if (!messageText.equals("")) {
                    messageText = MessageHandler.handle(messageText);

                    // push message to DB
                    Message message = new Message(messageText, firebaseUser.getUid(), 1);
                    getDBRefToGroupMessages(groupID).push().setValue(message);

                    messageField.setText("");
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        layout.removeAllViews();
    }

    @Override
    public void onUserSignIn() {
        super.onUserSignIn();
        fetchMessages();
    }

    @Override
    protected void onUserSignOut() {
        super.onUserSignOut();
        finish();
    }

    private void fetchMessages() {
        getDBRefToGroupMessages(groupID).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Message message = dataSnapshot.getValue(Message.class);
                addMessageBox(message);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    public void addMessageBox(Message messageWrapper) {
        String message = messageWrapper.getText();
        int type = messageWrapper.getStyle();

        TextView textView = new TextView(ChatActivity.this);
        textView.setText(message);
        textView.setTextSize(18);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, 0, 10);
        textView.setLayoutParams(lp);

        if (type == 1) {
            textView.setBackgroundResource(R.drawable.rounded_corner1);
        } else {
            textView.setBackgroundResource(R.drawable.rounded_corner2);
        }

        layout.addView(textView);
        scrollView.fullScroll(View.FOCUS_DOWN);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.roll_dice:
                return true;
            case R.id.add_member:
                return true;
            case R.id.group_info:
                return true;
            case R.id.delete_messages:
                deleteAllMessages();
                return true;
            case R.id.delete_group:
                // delete messages
                deleteAllMessages();
                // delete group inside each member

                // delete from groups
                getDBRefToGroup(groupID).setValue(null);

                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void deleteAllMessages() {
        getDBRefToGroupMessages(groupID).setValue(null);
        layout.removeAllViews();
    }
}