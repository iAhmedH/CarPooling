package com.example.carpooling;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText username;
    private EditText password;
    private Button loginButton;
    private TextView signupText;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen);

        mAuth = FirebaseAuth.getInstance();

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginButton);
        signupText = findViewById(R.id.signupText);
        progressBar = findViewById(R.id.ProgBar);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get user input
                String email = username.getText().toString();
                String userPassword = password.getText().toString();
                progressBar.setVisibility(View.VISIBLE);

                // Authenticate user with Firebase
                mAuth.signInWithEmailAndPassword(email, userPassword)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    // Login success
                                    if (isDriverEmail(email)) {
                                        // Navigate to the driver screen
                                        Intent intent = new Intent(Login.this, UpComingDriverRides.class);
                                        startActivity(intent);
                                        finish();
                                    } else if (isEngAsuEmail(email)) {
                                        // Navigate to the eng.asu.edu.eg screen
                                        Intent intent = new Intent(Login.this, Routes.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        // Navigate to a default screen (e.g., Routes)
                                        Intent intent = new Intent(Login.this, Routes.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                    finish();
                                } else {
                                    // If sign-in fails, display a message to the user.
                                    Toast.makeText(Login.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        signupText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start SignUp activity
                Intent intent = new Intent(Login.this, SignUp.class);
                startActivity(intent);
            }
        });
    }

    // Helper methods to check email domains
    private boolean isDriverEmail(String email) {
        return email.endsWith("@driver.com");
    }

    private boolean isEngAsuEmail(String email) {
        return email.endsWith("@eng.asu.edu.eg");
    }
}
