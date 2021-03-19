package com.sang.userlogin_registration;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class CommunityActivity extends AppCompatActivity {

    //Views:
    private MaterialToolbar toolbar;
    private BottomNavigationView nav_bottomView;
    private Spinner spinner_local, spinner_global;
    private RecyclerView recyclerView_local, recyclerView_global;
    private ProgressDialog progressDialog;

    //Firebase:
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community);

        //TODO: Set up Firebase:
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        firestore = FirebaseFirestore.getInstance();

        //TODO: Set up recyclerView:
        recyclerView_local = findViewById(R.id.recyclerView_local);
        recyclerView_local.setHasFixedSize(true);
        recyclerView_local.setLayoutManager(new LinearLayoutManager(this));

        recyclerView_global = findViewById(R.id.recyclerView_global);
        recyclerView_global.setHasFixedSize(true);
        recyclerView_global.setLayoutManager(new LinearLayoutManager(this));

        //TODO: Show events:
        progressDialog = new ProgressDialog(this);
        showEvents();

        //TODO: Set up Top action - bar:
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle("Community");
        setSupportActionBar(toolbar);

        //TODO: Set up bottom navigation:
        nav_bottomView = findViewById(R.id.nav_bottomView);
        nav_bottomView.setSelectedItemId(R.id.nav_community);
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

    private void showEvents() {
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
                        ArrayList<Event> events_global = new ArrayList<>();
                        ArrayList<Event> events_local = new ArrayList<>();
                        for (DocumentSnapshot document : task.getResult()){
                            if (document.getString("isLocal").equals("true")) {
                                Event event = document.toObject(Event.class);
                                if (event.getUsersList() == null) {
                                    ArrayList<User> usersList = new ArrayList<>();
                                    event.setUsersList(usersList);
                                }
                                events_local.add(event);
                            }
                            else if (document.getString("isLocal").equals("false")) {
                                Event event = document.toObject(Event.class);
                                if (event.getUsersList() == null) {
                                    ArrayList<User> usersList = new ArrayList<>();
                                    event.setUsersList(usersList);
                                }
                                events_global.add(event);
                            }
                        }
//                        EventAdapter eventAdapter_local = new EventAdapter(CommunityActivity.this, events_local);
//                        recyclerView_local.setAdapter(eventAdapter_local);
//                        EventAdapter eventAdapter_global = new EventAdapter(CommunityActivity.this, events_global);
//                        recyclerView_global.setAdapter(eventAdapter_global);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }
}