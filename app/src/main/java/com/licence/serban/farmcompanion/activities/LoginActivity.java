package com.licence.serban.farmcompanion.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.licence.serban.farmcompanion.R;
import com.licence.serban.farmcompanion.classes.Utilities;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private EditText emailEditText;
    private EditText passEditText;
    private Button loginButton;
    private Button toResetPassButton;
    private Button toSignUpButton;
    private ProgressBar progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginButton = (Button) findViewById(R.id.loginLoginButton);
        toResetPassButton = (Button) findViewById(R.id.loginForgotPassButton);
        toSignUpButton = (Button) findViewById(R.id.loginSignUpButton);
        emailEditText = (EditText) findViewById(R.id.loginEmailEditText);
        passEditText = (EditText) findViewById(R.id.loginPasswordEditText);
        progressDialog = (ProgressBar) findViewById(R.id.loginProgressBar);


        toResetPassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, ResetPasswordActivity.class);
                startActivity(intent);
            }
        });

        toSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = emailEditText.getText().toString().trim();
                String password = passEditText.getText().toString().trim();

                if (email.isEmpty()) {
                    emailEditText.setError(getResources().getString(R.string.email_error));
                    return;
                }
                if (password.isEmpty()) {
                    passEditText.setError(getResources().getString(R.string.pass_error));
                    return;
                }

                firebaseAuth = FirebaseAuth.getInstance();

                final String finalEmail = email;
                final String finalPass = password;

                progressDialog.setVisibility(View.VISIBLE);

                firebaseAuth
                        .signInWithEmailAndPassword(email, password)
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                sharedPreferences = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                                editor = sharedPreferences.edit();
                                editor.putString(Utilities.Constants.EMAIL, finalEmail);
                                editor.putString(Utilities.Constants.PASSWORD, finalPass);
                                editor.apply();

                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                String userID = email.replace(".", "_").replace("@", "_");
                                intent.putExtra(Utilities.Constants.USER_ID, userID);
                                startActivity(intent);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(LoginActivity.this, getResources().getString(R.string.login_failed), Toast.LENGTH_SHORT).show();
                                progressDialog.setVisibility(View.GONE);
                            }
                        });


            }
        });


    }
}
