package com.sang.sc_tatica;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FeedbackActivity extends AppCompatActivity {

    private EditText mFeedbackDatEdt;

    private ProgressDialog progressDialog;

    // Firebase instance
    private FirebaseFirestore db;

    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        // get Firebase instance:
        db = FirebaseFirestore.getInstance();

        progressDialog = new ProgressDialog(this);

        // init firebase:
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        Button mSendFeedbackBtn = findViewById(R.id.sendFeedback);

        currentUser = firebaseAuth.getCurrentUser();

        mFeedbackDatEdt = findViewById(R.id.feedbackData);


        // click button to upload the data
        mSendFeedbackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String feedbackData = mFeedbackDatEdt.getText().toString().trim();

                if (feedbackData.equals(""))
                    Toast.makeText(FeedbackActivity.this, "Do you have any feedback?", Toast.LENGTH_SHORT).show();
                else
                    uploadData(feedbackData);
            }
        });
    }

    private void uploadData(String feedbackData) {
        // set title of progress bar
        progressDialog.setTitle("Send your feedbacks...");

        // show progress bar when user click save button
        progressDialog.show();

        String id = UUID.randomUUID().toString();
        String user_mail = currentUser.getEmail().toString();

        Map<String, Object> user = new HashMap<>();
        user.put("User's mail", user_mail);
        user.put("User's feedbacks", feedbackData);

        // add this data
        db.collection("Feedbacks").document(id).set(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // this will be called when data is added successfully
                        progressDialog.dismiss();
                        Toast.makeText(FeedbackActivity.this, "Thanks for your contribution!", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // this will be called if there is any error when uploading
                progressDialog.dismiss();
                // get and show error message
                Toast.makeText(FeedbackActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}