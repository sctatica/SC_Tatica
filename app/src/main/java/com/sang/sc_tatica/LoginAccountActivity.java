package com.sang.sc_tatica;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class LoginAccountActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 100;
    private static final String TAG = "LoginAccountActivity";
    private GoogleSignInClient mGoogleSignInClient;

    private FirebaseAuth mAuth;

    // progress dialog
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_account);

        // Google before auth
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        //mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        //Email Login
        TextInputEditText emailEdT = findViewById(R.id.username);
        // Password login
        TextInputEditText passEdT = findViewById(R.id.password);
        TextView createAccountTv = findViewById(R.id.create_account);
        TextView forgotPassTv = findViewById(R.id.forgotPassword);
        Button loginBtn = findViewById(R.id.login);
        SignInButton googleSignInBtn = findViewById(R.id.google);

        // input email
        emailEdT.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    emailEdT.setError("Username must not empty");
                } else {
                    emailEdT.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // input password
        passEdT.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    passEdT.setError("Password must not empty");
                } else {
                    passEdT.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // Login successful
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // input data
                String email = emailEdT.getText().toString();
                String pass = passEdT.getText().toString().trim();
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    // invalid email pattern set error
                    emailEdT.setError("Invalid Error");
                    emailEdT.setFocusable(true);
                } else {
                    // login email, pass
                    loginUser(email, pass);
                }
            }
        });

        // Move to create an account
        createAccountTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gotoRegister = new Intent(LoginAccountActivity.this, RegisterAccountActivity.class);
                startActivity(gotoRegister);
                finish();
            }
        });

        // init progress dialog
        progressDialog = new ProgressDialog(this);

        // recover password click
        forgotPassTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRecoverPasswordDialog();
            }
        });

        // handle Google login
        googleSignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // begin Google login process
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);

            }
        });
    }

    // additional functions
    private void showRecoverPasswordDialog() {
        // Alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Recover password");

        // set layout linear layout
        LinearLayout linearLayout = new LinearLayout(this);
        // views to set in dialog
        EditText emailEdT = new EditText(this);
        emailEdT.setHint("Email");
        emailEdT.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

        // set min letters
        emailEdT.setMinEms(16);

        linearLayout.addView(emailEdT);
        linearLayout.setPadding(12, 10, 10, 10);
        builder.setView(linearLayout);

        // button recover
        builder.setPositiveButton("Recover", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String email = emailEdT.getText().toString().trim();
                beginRecovery(email);
            }
        });
        // button cancel
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // dismiss dialog
                dialog.dismiss();
            }
        });

        //show dialog
        builder.create().show();
    }

    private void beginRecovery(String email) {
        progressDialog.setMessage("Sending email...");
        progressDialog.show();
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            Toast.makeText(LoginAccountActivity.this, "A recover instruction sent to your email", Toast.LENGTH_SHORT).show();
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(LoginAccountActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // get and show proper error message

            }
        });
    }

    private void loginUser(String email, String pass) {
        // show progress dialog
        progressDialog.setMessage("Logging in...");
        progressDialog.show();
        mAuth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();

                            // Sign in successful
                            FirebaseUser user = mAuth.getCurrentUser();

                            // user logged in
                            startActivity(new Intent(LoginAccountActivity.this, HomeActivity.class));
                            finish();
                        } else {
                            progressDialog.dismiss();
                            // sign in fail, display a message to the user
                            Toast.makeText(LoginAccountActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                // error get and show message
                Toast.makeText(LoginAccountActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "FirebaseAuth with Google:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Failed to Google sign in", e);
                Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "Sign in with Credential: success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            //if user is signing in first time then get and show user info from google account:
                            if (task.getResult().getAdditionalUserInfo().isNewUser()) {
                                createUser(user);
                                // user email
                                Toast.makeText(LoginAccountActivity.this, "Logged in successful", Toast.LENGTH_LONG).show();
                                // go to home screen after login successful
                                startActivity(new Intent(LoginAccountActivity.this, FirstTimeActivity.class));
                                finish();
                            } else {
                                // user email
                                Toast.makeText(LoginAccountActivity.this, "Logged in successful", Toast.LENGTH_LONG).show();
                                // go to home screen after login successful
                                startActivity(new Intent(LoginAccountActivity.this, HomeActivity.class));
                                finish();
                            }

                        } else {
                            Log.d(TAG, "Sign in with Credential: failure");
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginAccountActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LoginAccountActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
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