package com.sang.sc_tatica.FullTips;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.sang.sc_tatica.R;

import java.util.ArrayList;

public class FullTipsActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;

    // recycler view to display data
    RecyclerView full_tips_recyclerView;

    ProgressDialog pd;

    // firebase
    private DatabaseReference TipsRef;

    private ArrayList<GetTips> TipsList;

    private FullTipsRecyclerViewAdapter fullTipsRecyclerViewAdapter;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_tips);

        // TODO: init toolbar:
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Useful tips for concentration");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        // init Dialog
        pd = new ProgressDialog(this);

        full_tips_recyclerView = (RecyclerView) findViewById(R.id.full_tips_recylerview);

        // set layout for full_tips_recyclerView
        LinearLayoutManager linearLayout = new LinearLayoutManager(this);
        full_tips_recyclerView.setLayoutManager(linearLayout);
        full_tips_recyclerView.setHasFixedSize(true);

        // get reference to Firebase Database
        TipsRef = FirebaseDatabase.getInstance().getReference();

        // implement a new ArrayList
        TipsList = new ArrayList<>();

        // clear all Data
        clearAll();

        // get data function
        getDataFromFirebase();

    }

    private void getDataFromFirebase() {

        // set title of progress dialog
        pd.setTitle("Loading tips...\nCheck your connection");
        pd.show();

        Query query = TipsRef.child("Tips");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                clearAll();

                // call when data is retrieved
                pd.dismiss();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    GetTips getTips = new GetTips();

                    getTips.setType(dataSnapshot.child("Type").getValue().toString());
                    getTips.setTitle(dataSnapshot.child("Title").getValue().toString());
                    getTips.setDescription(dataSnapshot.child("Description").getValue().toString());
                    getTips.setImageUrl(dataSnapshot.child("Image").getValue().toString());

                    TipsList.add(getTips);
                }

                fullTipsRecyclerViewAdapter = new FullTipsRecyclerViewAdapter(getApplicationContext(), TipsList);
                full_tips_recyclerView.setAdapter(fullTipsRecyclerViewAdapter);
                fullTipsRecyclerViewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void clearAll() {
        if (TipsList != null) {
            TipsList.clear();
            if (fullTipsRecyclerViewAdapter != null) {
                fullTipsRecyclerViewAdapter.notifyDataSetChanged();
            }
        }

        TipsList = new ArrayList<>();
    }
}