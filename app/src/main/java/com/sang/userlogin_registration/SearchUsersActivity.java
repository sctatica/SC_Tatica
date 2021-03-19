package com.sang.userlogin_registration;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SearchUsersActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private RecyclerView recyclerView;
    private MaterialToolbar toolbar;
    private OtherUsersAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_users);

        // init firebase:
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        //TODO: Set up recyclerView:
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //TODO: Set up Top action - bar:
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle("Other Users");
        setSupportActionBar(toolbar);

        //TODO: default show all users:
        getAllUsers(new UsersCallBack() {
            @Override
            public void onCallBack(ArrayList<User> usersList) {
                adapter = new OtherUsersAdapter(SearchUsersActivity.this, usersList);
                recyclerView.setAdapter(adapter);
            }
        });
    }

    public interface UsersCallBack {
        void onCallBack(ArrayList<User> user);
    }

    private void getAllUsers(UsersCallBack usersCallBack){
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference reference = firebaseDatabase.getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<User> usersList = new ArrayList<>();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    User user = ds.getValue(User.class);
                    usersList.add(user);
                }
                usersCallBack.onCallBack(usersList);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void searchUsers(String query, UsersCallBack usersCallBack) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference reference = firebaseDatabase.getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<User> usersList = new ArrayList<>();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    User user = ds.getValue(User.class);
                    if (!user.getUserID().equals(currentUser.getUid())) {
                        if (user.getName().toLowerCase().contains(query.toLowerCase())
                            || (user.getEmail().toLowerCase().contains(query.toLowerCase()))) {
                                usersList.add(user);
                        }
                    }
                }
                usersCallBack.onCallBack(usersList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search_menu, menu);

        //init Search View:
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);

        //init Search Listener:
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!TextUtils.isEmpty(query.trim())) {
                    searchUsers(query, new UsersCallBack() {
                        @Override
                        public void onCallBack(ArrayList<User> usersList) {
                            adapter = new OtherUsersAdapter(SearchUsersActivity.this, usersList);
                            recyclerView.setAdapter(adapter);
                        }
                    });
                } else {
                    getAllUsers(new UsersCallBack() {
                        @Override
                        public void onCallBack(ArrayList<User> usersList) {
                            adapter = new OtherUsersAdapter(SearchUsersActivity.this, usersList);
                            recyclerView.setAdapter(adapter);
                        }
                    });
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!TextUtils.isEmpty(newText.trim())) {
                    searchUsers(newText, new UsersCallBack() {
                        @Override
                        public void onCallBack(ArrayList<User> usersList) {
                            adapter = new OtherUsersAdapter(SearchUsersActivity.this, usersList);
                            recyclerView.setAdapter(adapter);
                        }
                    });
                } else {
                    getAllUsers(new UsersCallBack() {
                        @Override
                        public void onCallBack(ArrayList<User> usersList) {
                            adapter = new OtherUsersAdapter(SearchUsersActivity.this, usersList);
                            recyclerView.setAdapter(adapter);
                        }
                    });
                }
                return false;
            }
        });
        return true;
    }

    // handle menu item clicks
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // get item id
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.action_logout) {
            mAuth.signOut();
            checkUserStatus();
        }
        return true;
    }

    private void checkUserStatus(){
        // get current user
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null){
            // user not signed in, goto main activity
            startActivity(new Intent(SearchUsersActivity.this, LoginAccountActivity.class));
            finish();
        }
    }
}