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

/**
 * Activity that handles the login in of users. All activities activities should redirect here if the user is not authenticated.
 */
public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "EmailPassword";

    private TextView registerView;
    private EditText usernameField;
    private EditText passwordField;
    private Button loginButton;
    private String user;
    private String pass;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        registerView = (TextView) findViewById(R.id.register);
        usernameField = (EditText) findViewById(R.id.username);
        passwordField = (EditText) findViewById(R.id.password);
        loginButton = (Button) findViewById(R.id.loginButton);

        initAuthListener();
        initRegisterListener();
        initLoginButtonListener();
    }

    /**
     * Initializes the authentication listener that is called as soon as the authentication state changes (and once on creation).
     * In case the user successfully logged in the activity is changed to the groups overview.
     */
    private void initAuthListener() {
        firebaseAuth = FirebaseAuth.getInstance();
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:Login:" + user.getUid());
                    finish();
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
    }

    /**
     * Initializes the listener on the "register"-view to change to the register activity when clicked.
     */
    private void initRegisterListener() {
        registerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
    }

    /**
     * Initializes the listener on the login button to sign in if the data is valid.
     */
    private void initLoginButtonListener() {
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isFormValid()){
                    signIn(user, pass);
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
     * Attempts to sign in using the Firebase authentication.
     * @param email The user's email
     * @param password The user's password
     */
    private void signIn(String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithEmail:failed", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onStart() {
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