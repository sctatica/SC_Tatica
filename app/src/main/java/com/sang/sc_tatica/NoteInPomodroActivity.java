package com.sang.sc_tatica;

import android.app.ProgressDialog;
import android.content.Intent;
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

import java.util.UUID;

public class NoteInPomodroActivity extends AppCompatActivity {

    private EditText mTitleEt, mDescriptionEt;
    private Button mSaveBtn, mShowNoteBtn;

    private ProgressDialog pd;

    // Firebase instance
    private FirebaseFirestore db;

    private String pId, pTitle, pDescription;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_in_pomodro);

        // get Firebase instance:
        db = FirebaseFirestore.getInstance();

        // init firebase:
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        // initialize views
        mTitleEt = findViewById(R.id.title_in_note);
        mDescriptionEt = findViewById(R.id.noteData);
        mSaveBtn = findViewById(R.id.save);
        mShowNoteBtn = findViewById(R.id.showNote);

        // if users choose update the data (from AlertDialog of ListUserNotes
        // get data from internet (id, title, desc) and set to Editexts
        // update

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            // Update
            mSaveBtn.setText("Update");

            // get Data
            pId = bundle.getString("pId");
            pTitle = bundle.getString("pTitle");
            pDescription = bundle.getString("pDescription");

            // set data
            mTitleEt.setText(pTitle);
            mDescriptionEt.setText(pDescription);
        } else {
            // new data
            mSaveBtn.setText("Save");
        }

        // progress dialog
        pd = new ProgressDialog(this);

        // choose update then update existing data


        // click button to upload the data
        mSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle1 = getIntent().getExtras();
                if (bundle != null) {
                    // update
                    String id = pId;
                    String title = mTitleEt.getText().toString().trim();
                    String description = mDescriptionEt.getText().toString().trim();

                    if (title.equals(""))
                        Toast.makeText(NoteInPomodroActivity.this, "Please enter the title", Toast.LENGTH_SHORT).show();
                    else if (description.equals(""))
                        Toast.makeText(NoteInPomodroActivity.this, "Please enter the description", Toast.LENGTH_SHORT).show();
                    else
                        uploadData(id, title, description);
                } else {
                    // adding new data
                    // input data
                    String title = mTitleEt.getText().toString().trim();
                    String description = mDescriptionEt.getText().toString().trim();

                    if (title.equals(""))
                        Toast.makeText(NoteInPomodroActivity.this, "Please enter the title", Toast.LENGTH_SHORT).show();
                    else if (description.equals(""))
                        Toast.makeText(NoteInPomodroActivity.this, "Please enter the description", Toast.LENGTH_SHORT).show();
                    else
                        uploadData(title, description);
                }
            }

        });

        // click show note button
        mShowNoteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NoteInPomodroActivity.this, ListUserNotesActivity.class));
                finish();
            }
        });
    }

    private void uploadData(String id, String title, String description) {
        // set title of progress bar
        pd.setTitle("Updating...");

        // show progress bar when user click save button
        pd.show();

        ModelUserNotes newNote = new ModelUserNotes(currentUser.getUid(), currentUser.getEmail(), id, title, description);

        db.collection("User's note").document(id).set(newNote)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // called when updated successfully
                        pd.dismiss();
                        Toast.makeText(NoteInPomodroActivity.this, "Updated successfully", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // called when fail update
                pd.dismiss();
                // get and show any error
                Toast.makeText(NoteInPomodroActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }


    private void uploadData(String title, String description) {
        // set title of progress bar
        pd.setTitle("Saving data");

        // show progress bar when user click save button
        pd.show();

        // random id for each data
        String id = UUID.randomUUID().toString();
        ModelUserNotes newNote = new ModelUserNotes(currentUser.getUid(), currentUser.getEmail(), id, title, description);

        // add this data
        db.collection("User's note").document(id).set(newNote)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // this will be called when data is added successfully
                        pd.dismiss();
                        Toast.makeText(NoteInPomodroActivity.this, "Uploaded successfully", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // this will be called if there is any error when uploading
                pd.dismiss();
                // get and show error message
                Toast.makeText(NoteInPomodroActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }
}