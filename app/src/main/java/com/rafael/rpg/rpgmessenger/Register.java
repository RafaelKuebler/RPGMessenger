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

public class Register extends AppCompatActivity {
    private static final String TAG = "EmailPassword";

    private EditText usernameField, passwordField;
    private Button registerButton;
    private TextView loginView;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // get references to fields and views
        usernameField = (EditText)findViewById(R.id.username);
        passwordField = (EditText)findViewById(R.id.password);
        registerButton = (Button)findViewById(R.id.registerButton);
        loginView = (TextView)findViewById(R.id.login);

        firebaseAuth = FirebaseAuth.getInstance();

        // if the user wants to login change back to login activity
        loginView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Register.this, Login.class));
            }
        });

        // if the user wants to register check input and register him
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = usernameField.getText().toString();
                String pass = passwordField.getText().toString();

                if(user.equals("")){
                    usernameField.setError("can't be blank");
                }
                else if(pass.equals("")){
                    passwordField.setError("can't be blank");
                }
                else if(!user.matches("[A-Za-z0-9]+")){
                    usernameField.setError("only alphabet or number allowed");
                }
                else if(user.length()<5){
                    usernameField.setError("at least 5 characters long");
                }
                else if(pass.length()<5){
                    passwordField.setError("at least 5 characters long");
                }
                else {
                    createAccount(user, pass);
                }
            }
        });

        // the moment the user has been authenticated: change to group view activity
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
    }

    // create a new account with the specified email and password, input has to be already sanitized!
    private void createAccount(String email, String password) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(Register.this, "Authentication failed!",
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