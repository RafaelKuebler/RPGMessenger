package com.rafael.rpg.rpgmessenger;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rafael.rpg.dbwrappers.User;

/**
 * Activity that handles the registration of new users.
 */
public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "EmailPassword";

    private TextView loginView;
    private EditText usernameField;
    private EditText passwordField;
    private Button registerButton;
    private String user;
    private String pass;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authListener;
    private DatabaseReference firebaseDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        usernameField = (EditText)findViewById(R.id.username);
        passwordField = (EditText)findViewById(R.id.password);
        registerButton = (Button)findViewById(R.id.registerButton);
        loginView = (TextView)findViewById(R.id.login);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDB = FirebaseDatabase.getInstance().getReference();

        initAuthListener();
        initLoginListener();
        initRegisterButtonListener();
    }

    /**
     * Initializes the listener on the "login"-view to change to the login activity when clicked.
     */
    private void initLoginListener(){
        loginView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
    }

    /**
     * Initializes the listener on the register button to check the create an account if the data is valid.
     */
    private void initRegisterButtonListener(){
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isFormValid()){
                    createAccount(user, pass);
                }
            }
        });
    }

    /**
     * Checks if input in the form is valid.
     * @return true if the input satisfies the constraints, false otherwise
     */
    private boolean isFormValid(){
        user = usernameField.getText().toString();
        pass = passwordField.getText().toString();

        if (user.equals("")) {
            usernameField.setError("can't be blank");
        } else if (pass.equals("")) {
            passwordField.setError("can't be blank");
        } else {
            return true;
        }
        return false;
    }

    /**
     * Initializes the authentication listener that is called as soon as the authentication state changes (and once on creation).
     * In case the user is not logged in returns to the login screen.
     */
    public void initAuthListener(){
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:Register:" + user.getUid());
                    User newUser = new User("Username");
                    firebaseDB.child("users").child(user.getUid()).setValue(newUser);

                    Intent intent = new Intent(RegisterActivity.this, GroupsActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
    }

    /**
     * Creates a new account.
     * @param email The user's email
     * @param password The user's password
     */
    private void createAccount(String email, String password) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, "Registration failed!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
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