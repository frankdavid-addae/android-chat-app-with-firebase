package com.example.androidchatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private EditText etUsername, etEmailAddress, etPassword;
    private TextView tvSignInInfo;
    private Button btnSubmit;

    private boolean isSigningUp = false;

    FirebaseAuth firebaseAuthInstance = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etEmailAddress = findViewById(R.id.etEmailAddress);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        tvSignInInfo = findViewById(R.id.tvSignInInfo);
        btnSubmit = findViewById(R.id.btnSubmit);

        if (firebaseAuthInstance.getCurrentUser() != null) {
            startActivity(new Intent(MainActivity.this, FriendsActivity.class));
            finish();
        }

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etEmailAddress.getText().toString().isEmpty() || etPassword.getText().toString().isEmpty()) {
                    if (isSigningUp && etUsername.getText().toString().isEmpty()) {
                        Toast.makeText(MainActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                if (isSigningUp) {
                    signUp();
                } else {
                    signIn();
                }
            }
        });

        tvSignInInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isSigningUp) {
                    isSigningUp = false;
                    etUsername.setVisibility(View.GONE);
                    btnSubmit.setText("Sign In");
                    tvSignInInfo.setText("Don't have an account? Sign up here.");
                } else {
                    isSigningUp = true;
                    etUsername.setVisibility(View.VISIBLE);
                    btnSubmit.setText("Sign Up");
                    tvSignInInfo.setText("Already have an account? Sign in here.");
                }
            }
        });
    }

    private void signUp() {
        firebaseAuthInstance.createUserWithEmailAndPassword(
                etEmailAddress.getText().toString(),
                etPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    startActivity(new Intent(MainActivity.this, FriendsActivity.class));
                    Toast.makeText(MainActivity.this, "Signed up successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(MainActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void signIn() {
        firebaseAuthInstance.signInWithEmailAndPassword(
                etEmailAddress.getText().toString(),
                etPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    startActivity(new Intent(MainActivity.this, FriendsActivity.class));
                    Toast.makeText(MainActivity.this, "Signed in successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(MainActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}