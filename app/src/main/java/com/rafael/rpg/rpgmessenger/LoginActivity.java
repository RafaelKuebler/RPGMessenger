package com.rafael.rpg.rpgmessenger;

import android.content.Intent;
import android.support.annotation.NonNull;
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

/**
 * Activity that handles the login in of users. All activities activities should redirect here if the user is not authenticated.
 */
public class LoginActivity extends BaseActivity {
    private TextView registerView;
    private EditText usernameField;
    private EditText passwordField;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        registerView = (TextView) findViewById(R.id.register);
        usernameField = (EditText) findViewById(R.id.username);
        passwordField = (EditText) findViewById(R.id.password);
        loginButton = (Button) findViewById(R.id.loginButton);

        initRegisterListener();
        initLoginButtonListener();
    }

    @Override
    protected void onUserSignIn() {
        super.onUserSignIn();

        Intent intent = new Intent(LoginActivity.this, GroupsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    /**
     * Initializes the listener on the "register"-view to change to the register activity when clicked.
     */
    private void initRegisterListener() {
        registerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
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
                String user = usernameField.getText().toString();
                String pass = passwordField.getText().toString();

                if (isLoginDataValid(user, pass)) {
                    signIn(user, pass);
                }
            }
        });
    }

    /**
     * Checks if input in the form is valid.
     *
     * @return true if the input satisfies the constraints, false otherwise
     */
    private boolean isLoginDataValid(String user, String pass) {
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
     *
     * @param email    The user's email
     * @param password The user's password
     */
    private void signIn(String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        log("signInWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w("RPGMessenger", "signInWithEmail:failed", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}