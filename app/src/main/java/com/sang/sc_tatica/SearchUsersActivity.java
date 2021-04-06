package com.sang.sc_tatica;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import java.util.Collections;
import java.util.Comparator;

public class SearchUsersActivity extends AppCompatActivity {

    public interface UsersCallBack {
        void onCallBack(ArrayList<User> user);
    }

    public interface UsersIDCallBack {
        void onCallBack(ArrayList<String> user);
    }

    //firebase:
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    //views:
    private RecyclerView recyclerView;
    private MaterialToolbar toolbar;
    private OtherUsersAdapter adapter;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_users);

        progressDialog = new ProgressDialog(this);

        // init firebase:
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();

        //TODO: Set up recyclerView:
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //TODO: Set up Top action - bar:
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle("Friends");
        setSupportActionBar(toolbar);

        //init recyclerViews();
        getFriendsView();
    }

    private void searchUsers(String query, UsersCallBack usersCallBack) {
        DatabaseReference databaseReference = firebaseDatabase.getReference("Users");
        databaseReference.addValueEventListener(new ValueEventListener() {
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
                    getFriendsView();
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
                    getFriendsView();
                }
                return false;
            }
        });
        return true;
    }

    private void getFriendsList(UsersIDCallBack users) {
        databaseReference = firebaseDatabase.getReference("Users");
        databaseReference.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                ArrayList<String> friendsList = null;
                if (user != null) {
                    friendsList = user.getFriends();
                    if (friendsList == null)
                        friendsList = new ArrayList<>();
                }
                users.onCallBack(friendsList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getFriendsView() {
        toolbar.setTitle("Friends");
        progressDialog.setTitle("Loading friends...\nCheck your connection");
        progressDialog.show();
        getFriendsList(new UsersIDCallBack() {
            @Override
            public void onCallBack(ArrayList<String> friendsID) {
                databaseReference = firebaseDatabase.getReference("Users");
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ArrayList<User> userList = new ArrayList<>();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            User user = ds.getValue(User.class);
                            boolean isFriends = false;
                            if (user != null) {
                                for (String userID : friendsID) {
                                    if (user.getUserID().equals(userID)) {
                                        isFriends = true;
                                        break;
                                    }
                                }
                                if (isFriends) userList.add(user);
                            }
                        }
                        if (userList.size() != 0) {
                            Comparator<User> usersComparator = new Comparator<User>() {
                                @Override
                                public int compare(User o1, User o2) {
                                    return Integer.parseInt(o2.getTimeWork()) - Integer.parseInt(o1.getTimeWork());
                                }
                            };
                            Collections.sort(userList, usersComparator);
                            adapter = new OtherUsersAdapter(SearchUsersActivity.this, userList);
                            recyclerView.setAdapter(adapter);
                            progressDialog.dismiss();
                        } else {
                            progressDialog.dismiss();
                            getOtherUsersView();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }

    private void getOtherUsersView() {
        toolbar.setTitle("Other Users");
        progressDialog.setTitle("Loading other users...\nCheck your connection");
        progressDialog.show();
        getFriendsList(new UsersIDCallBack() {
            @Override
            public void onCallBack(ArrayList<String> friendsID) {
                databaseReference = firebaseDatabase.getReference("Users");
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ArrayList<User> userList = new ArrayList<>();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            User user = ds.getValue(User.class);
                            boolean isFriends = false;
                            if (user != null) {
                                for (String userID : friendsID) {
                                    if (user.getUserID().equals(userID)) {
                                        isFriends = true;
                                        break;
                                    }
                                }
                                if (!isFriends) userList.add(user);
                            }
                        }
                        if (userList.size() != 0) {
                            Comparator<User> usersComparator = new Comparator<User>() {
                                @Override
                                public int compare(User o1, User o2) {
                                    return Integer.parseInt(o2.getTimeWork()) - Integer.parseInt(o1.getTimeWork());
                                }
                            };
                            Collections.sort(userList, usersComparator);
                            adapter = new OtherUsersAdapter(SearchUsersActivity.this, userList);
                            recyclerView.setAdapter(adapter);
                        }
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }

    // handle menu item clicks
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // get item id
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.action_friends: {
                getFriendsView();
                break;
            }
            case R.id.action_otherUsers: {
                getOtherUsersView();
                break;
            }
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SearchUsersActivity.this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        super.onBackPressed();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}