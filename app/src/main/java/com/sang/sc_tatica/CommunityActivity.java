package com.sang.sc_tatica;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class CommunityActivity extends AppCompatActivity {

    //Views:
    private MaterialToolbar toolbar;
    private BottomNavigationView nav_bottomView;
    private Spinner spinner;
    private RecyclerView recyclerView;
    private ProgressDialog progressDialog;

    //Firebase:
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore firestore;

    @Override
    protected void onStart() {
        super.onStart();
        nav_bottomView.setSelectedItemId(R.id.nav_community);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community);

        //TODO: Set up Firebase:
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        firestore = FirebaseFirestore.getInstance();

        //TODO: Set up recyclerView:
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //TODO: Set up spinner:
        //Local:
        spinner = findViewById(R.id.spinner);
        ArrayList<LocalOrGlobal> list = new ArrayList<>();
        list.add(new LocalOrGlobal("Vietnam", R.drawable.vietnam_flag));
        list.add(new LocalOrGlobal("International", R.drawable.global_icon));
        LocalOrGlobalAdapter adapter = new LocalOrGlobalAdapter(this, list);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //TODO: Show events:
                progressDialog = new ProgressDialog(CommunityActivity.this);
                showEvents(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //TODO: Set up Top action - bar:
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle("Community");
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
                        startActivity(new Intent(CommunityActivity.this, HomeActivity.class));
                        return true;
                    case R.id.nav_task:
                        startActivity(new Intent(CommunityActivity.this, TaskManagementActivity.class));
                        return true;
                    case R.id.nav_note:
                        startActivity(new Intent(CommunityActivity.this, ListUserNotesActivity.class));
                        return true;
                    case R.id.nav_community:
                        return true;
                    default:
                        break;
                }
                return false;
            }
        });
    }

    private void showEvents(int position) {
        // set up progress dialog:
        progressDialog.setTitle("Loading Events...");
        progressDialog.show();
        // get firestore:
        firestore.collection("Events")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        // call when data is retrieved
                        progressDialog.dismiss();
                        // show Data
                        ArrayList<Event> events = new ArrayList<>();
                        for (DocumentSnapshot document : task.getResult()) {
                            if (position == 0) {
                                if (document.getString("isLocal").equals("true")) {
                                    Event event = document.toObject(Event.class);
                                    if (event.getUsersList() == null) {
                                        ArrayList<RegisterUser> usersList = new ArrayList<>();
                                        event.setUsersList(usersList);
                                    }
                                    events.add(event);
                                }
                            } else if (position == 1) {
                                if (document.getString("isLocal").equals("false")) {
                                    Event event = document.toObject(Event.class);
                                    if (event.getUsersList() == null) {
                                        ArrayList<RegisterUser> usersList = new ArrayList<>();
                                        event.setUsersList(usersList);
                                    }
                                    events.add(event);
                                }
                            }
                        }
                        EventAdapter eventAdapter = new EventAdapter(CommunityActivity.this, events);
                        recyclerView.setAdapter(eventAdapter);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }
}