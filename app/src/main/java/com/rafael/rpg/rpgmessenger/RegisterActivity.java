package com.rafael.rpg.rpgmessenger;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.rafael.rpg.dbwrappers.User;

/**
 * Activity that handles the registration of new users.
 */
public class RegisterActivity extends BaseActivity {
    private TextView loginView;
    private EditText usernameField;
    private EditText passwordField;
    private Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        usernameField = (EditText)findViewById(R.id.username);
        passwordField = (EditText)findViewById(R.id.password);
        registerButton = (Button)findViewById(R.id.registerButton);
        loginView = (TextView)findViewById(R.id.login);

        initLoginListener();
        initRegisterButtonListener();
    }

    @Override
    protected void onUserSignIn() {
        super.onUserSignIn();

        User newUser = new User("Username");
        getDBRefToCurrentUser().setValue(newUser);

        Intent intent = new Intent(RegisterActivity.this, GroupsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
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
                String user = usernameField.getText().toString();
                String pass = passwordField.getText().toString();

                if(isRegisterDataValid(user, pass)){
                    createAccount(user, pass);
                }
            }
        });
    }

    /**
     * Checks if input in the form is valid.
     * @return true if the input satisfies the constraints, false otherwise
     */
    private boolean isRegisterDataValid(String user, String pass){
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
}