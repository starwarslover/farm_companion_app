package com.licence.serban.farmcompanion.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.licence.serban.farmcompanion.R;
import com.licence.serban.farmcompanion.misc.models.User;
import com.licence.serban.farmcompanion.misc.Utilities;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    private EditText confirmPassEditText;
    private EditText passwordEditText;
    private EditText confirmEmailEditText;
    private EditText emailEditText;
    private EditText nameEditText;
    private Button goToLoginButton;
    private Button signUpButton;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        nameEditText = (EditText) findViewById(R.id.registerNameEditText);
        emailEditText = (EditText) findViewById(R.id.registerEmailEditText);
        confirmEmailEditText = (EditText) findViewById(R.id.registerConfirmEmailEditText);
        passwordEditText = (EditText) findViewById(R.id.registerPasswordEditText);
        confirmPassEditText = (EditText) findViewById(R.id.registerConfirmPasswordEditText);
        signUpButton = (Button) findViewById(R.id.registerSignUpButton);
        goToLoginButton = (Button) findViewById(R.id.registerToLoginButton);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();


        goToLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                RegisterActivity.this.finish();
            }
        });

        confirmEmailEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    String email = emailEditText.getText().toString().trim();
                    String confEmail = confirmEmailEditText.getText().toString().trim();
                    if (!email.isEmpty()) {
                        if (!email.equals(confEmail)) {
                            confirmEmailEditText.setError(getResources().getString(R.string.unmatching_email));
                        }
                    }
                }
            }
        });

        emailEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    String email = emailEditText.getText().toString().trim();
                    if (!email.isEmpty()) {
                        if (!Utilities.isValidEmail(email)) {
                            emailEditText.setError(getResources().getString(R.string.invalid_email));
                        }
                    }
                }
            }
        });

        confirmPassEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    String pass = passwordEditText.getText().toString().trim();
                    String confPass = confirmPassEditText.getText().toString().trim();
                    if (!pass.isEmpty()) {
                        if (!pass.equals(confPass)) {
                            confirmPassEditText.setError(getResources().getString(R.string.unmatching_passwords));
                        }
                    }
                }
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = nameEditText.getText().toString().trim();
                final String email = emailEditText.getText().toString().trim();
                String confEmail = confirmEmailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString();
                String confirmPass = confirmPassEditText.getText().toString();

                if (name.isEmpty()) {
                    nameEditText.setError(getResources().getString(R.string.no_name_error));
                    return;
                }
                if (email.isEmpty()) {
                    emailEditText.setError(getResources().getString(R.string.no_email_error));
                    return;
                }
                if (!email.equals(confEmail)) {
                    confirmEmailEditText.setError(getResources().getString(R.string.unmatching_email));
                    return;
                }
                if (password.isEmpty()) {
                    passwordEditText.setError(getResources().getString(R.string.no_pass_error));
                    return;
                }
                if (!password.equals(confirmPass)) {
                    confirmPassEditText.setError(getResources().getString(R.string.unmatching_passwords));
                    return;
                }
                final User user = new User();
                user.setName(name);
                user.setEmail(email);
                user.setAdmin(true);
                final String finalPassword = password;

                firebaseAuth
                        .createUserWithEmailAndPassword(email, password)
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {

                                Toast.makeText(RegisterActivity.this, getResources().getString(R.string.register_success), Toast.LENGTH_SHORT).show();
                                String userID = authResult.getUser().getUid();
                                user.setId(userID);
                                DatabaseReference reference = databaseReference.child(Utilities.Constants.DB_USERS);
                                reference.child(userID).setValue(user);

                                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                                i.putExtra(Utilities.Constants.INTENT_USER, user);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(i);
                                RegisterActivity.this.finish();

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                e.printStackTrace();
                                Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });


            }
        });
    }
}
