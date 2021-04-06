package com.sang.sc_tatica;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ListUserNotesActivity extends AppCompatActivity {

    List<ModelUserNotes> modelUserNotes_list = new ArrayList<>();
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager layoutManager;

    // Firebase:
    FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;

    UserNotesAdapter customAdapter_userNotes;

    ProgressDialog pd;

    FloatingActionButton mAddNoteBtn;

    //Views:
    private MaterialToolbar toolbar;
    private BottomNavigationView nav_bottomView;

    @Override
    protected void onStart() {
        super.onStart();
        nav_bottomView.setSelectedItemId(R.id.nav_note);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_user_notes);

        // initialize Firebase
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

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
                startActivity(new Intent(ListUserNotesActivity.this, NoteInPomodroActivity.class));
                finish();
            }
        });

        //TODO: Set up Top action - bar:
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle("Note Management");
        setSupportActionBar(toolbar);

        //TODO: Set up bottom navigation:
        nav_bottomView = findViewById(R.id.nav_bottomView);
        nav_bottomView.setBackground(null);
        nav_bottomView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                //Handle item click
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        startActivity(new Intent(ListUserNotesActivity.this, HomeActivity.class));
                        return true;
                    case R.id.nav_task:
                        startActivity(new Intent(ListUserNotesActivity.this, TaskManagementActivity.class));
                        return true;
                    case R.id.nav_note:
                        return true;
                    case R.id.nav_community:
                        startActivity(new Intent(ListUserNotesActivity.this, CommunityActivity.class));
                        return true;
                    default:
                        break;
                }
                return false;
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
                        modelUserNotes_list.clear();
                        // call when data is retrieved
                        pd.dismiss();
                        // show Data
                        for (DocumentSnapshot user : task.getResult()) {
                            if (user.getString("userID").equals(currentUser.getUid())) {
                                ModelUserNotes modelUserNotes = new ModelUserNotes(user.getString("id"),
                                        user.getString("title"),
                                        user.getString("description"));
                                modelUserNotes_list.add(modelUserNotes);
                            }
                        }
                        // adapter
                        customAdapter_userNotes = new UserNotesAdapter(ListUserNotesActivity.this, modelUserNotes_list);

                        mRecyclerView.setAdapter(customAdapter_userNotes);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // called when there is any error while retrieved the data
                        Toast.makeText(ListUserNotesActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void deleteNote(int index) {
        // set title of progress dialog
        pd.setTitle("Deleting notes...");
        pd.show();

        db.collection("User's note").document(modelUserNotes_list.get(index).getId())
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // called when delete data succesfully
                        modelUserNotes_list.clear();
                        Toast.makeText(ListUserNotesActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                        showUserNote();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // called when fail
                        pd.dismiss();
                        Toast.makeText(ListUserNotesActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}

