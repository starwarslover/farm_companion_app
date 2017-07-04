package com.licence.serban.farmcompanion.activities;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.licence.serban.farmcompanion.R;
import com.licence.serban.farmcompanion.misc.Utilities;

public class ResetPasswordActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;

    private Button backButton;
    private Button resetPassButton;
    private EditText emailEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        emailEditText = (EditText) findViewById(R.id.resetPassEmailEditText);
        resetPassButton = (Button) findViewById(R.id.resetPassResetButton);
        backButton = (Button) findViewById(R.id.resetPassBackButton);

        firebaseAuth = FirebaseAuth.getInstance();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ResetPasswordActivity.this.finish();
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

        resetPassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailEditText.getText().toString().trim();
                if (!email.isEmpty()) {
                    emailEditText.setError(getResources().getString(R.string.no_email_error));
                    return;
                }
                if (!Utilities.isValidEmail(email)) {
                    emailEditText.setError(getResources().getString(R.string.invalid_email));
                    return;
                }

                firebaseAuth
                        .sendPasswordResetEmail(email)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(ResetPasswordActivity.this, getResources().getString(R.string.reset_send_success), Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(ResetPasswordActivity.this, getResources().getString(R.string.reset_send_failure), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }
}
