package com.sang.userlogin_registration;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ListUserNotes extends AppCompatActivity {

    List<ModelUserNotes> modelUserNotes_list = new ArrayList<>();
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager layoutManager;

    // firestore instance
    FirebaseFirestore db;

    CustomAdapter_UserNotes customAdapter_userNotes;
    
    ProgressDialog pd;

    FloatingActionButton mAddNoteBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_user_notes);

        // initialize Firebase
        db = FirebaseFirestore.getInstance();
        
        // initialize views
        mRecyclerView = findViewById(R.id.recycler_view_user_notes);
        mAddNoteBtn = findViewById(R.id.addNoteBtn);
        
        // set recycler view properties
        mRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        // init Dialog
        pd = new ProgressDialog(this);
        
        // show data in recycler view
        showUserNote();

        mAddNoteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ListUserNotes.this, NoteInPomodro.class));
                finish();
            }
        });
    }

    private void showUserNote() {

        // set title of progress dialog
        pd.setTitle("Loading notes...");
        pd.show();

        db.collection("User's note")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        // call when data is retrieved
                        pd.dismiss();
                        // show Data
                        for (DocumentSnapshot user:task.getResult()){
                            ModelUserNotes modelUserNotes = new ModelUserNotes(user.getString("id"),
                            user.getString("title"),
                            user.getString("description"));

                            modelUserNotes_list.add(modelUserNotes);

                        }
                        // adapter
                        customAdapter_userNotes = new CustomAdapter_UserNotes(ListUserNotes.this, modelUserNotes_list);

                        mRecyclerView.setAdapter(customAdapter_userNotes);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // called when there is any error while retrieved the data
                    }
                });

    }
}
