package com.licence.serban.farmcompanion.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.licence.serban.farmcompanion.R;
import com.licence.serban.farmcompanion.classes.Utilities;

import java.util.prefs.Preferences;

public class StartActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private FirebaseAuth firebaseAuth;

    private String username;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

//        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        username = sharedPreferences.getString(Utilities.Constants.EMAIL, "");
        password = sharedPreferences.getString(Utilities.Constants.PASSWORD, "");

//        if (!username.isEmpty() && !password.isEmpty()) {
//
////            firebaseAuth
////                    .signInWithEmailAndPassword(username, password)
////                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
////                        @Override
////                        public void onComplete(@NonNull Task<AuthResult> task) {
////                            Intent i = new Intent(StartActivity.this, MainActivity.class);
////                            startActivity(i);
////                        }
////                    })
////                    .addOnFailureListener(new OnFailureListener() {
////                        @Override
////                        public void onFailure(@NonNull Exception e) {
////                            Intent intent = new Intent(StartActivity.this, LoginActivity.class);
////                            startActivity(intent);
////                        }
////                    });
//
//        }
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            Intent i = new Intent(StartActivity.this, MainActivity.class);
            startActivity(i);
        } else {
//            Toast.makeText(this, "Here", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent i = new Intent(StartActivity.this, LoginActivity.class);
                    startActivity(i);
                }
            }, Utilities.Constants.SPLASH_TIMEOUT);
        }

    }
}
