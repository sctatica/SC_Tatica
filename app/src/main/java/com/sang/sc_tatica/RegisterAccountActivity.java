package com.sang.sc_tatica;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;


public class RegisterAccountActivity extends AppCompatActivity {

    //ProgressDialog progressDialog;
    ProgressDialog progressDialog;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_account);

        //Elements
        TextInputEditText r_username = findViewById(R.id.r_username);
        TextInputEditText r_password = findViewById(R.id.r_password);
        TextInputEditText pass_confirm = findViewById(R.id.confirm_password);
        Button register = findViewById(R.id.register);

        //Progressing
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Registering your account...");

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        //Click Create an account -> Register account
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //input mail, pass
                String email = Objects.requireNonNull(r_username.getText()).toString().trim();
                String pass = Objects.requireNonNull(r_password.getText()).toString().trim();
                String pass_c = Objects.requireNonNull(pass_confirm.getText()).toString().trim();

                //Validate them
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    //Set error
                    r_username.setError("Invalid Email");
                    r_username.setFocusable(true);

                } else if (r_password.length() < 6) {
                    //set error
                    r_password.setError("Password length at least 6 characters");
                    r_password.setFocusable(true);

                } else if (!pass.equals(pass_c)) {
                    pass_confirm.setError("Not match");
                } else {
                    registerUser(email, pass);
                }
            }
        });

    }

    // register function
    private void registerUser(String email, String pass) {
        // email and password are valid, show the progress dialog and start register
        progressDialog.show();

        mAuth.createUserWithEmailAndPassword(email, pass).
                addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // sign in success, dismiss dialog and start register activity
                            progressDialog.dismiss();
                            FirebaseUser user = mAuth.getCurrentUser();

                            // create User:
                            createUser(user);

                            Toast.makeText(RegisterAccountActivity.this, "Registered", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(RegisterAccountActivity.this, FirstTimeActivity.class);
                            startActivity(intent);
                        } else {
                            // If sign in fail, display a message to the user
                            progressDialog.dismiss();
                            Toast.makeText(RegisterAccountActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                        }
                    }

                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(RegisterAccountActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Create a new user email and userID:
    private void createUser(FirebaseUser user) {
        //TODO: Get user email and userID from auth:
        String email = user.getEmail();
        String userID = user.getUid();

        // TODO: Store to firebase basic info of new user:
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("Users");
        reference.child(userID).setValue(new User(email, userID));
    }
}