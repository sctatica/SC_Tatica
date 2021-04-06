package com.sang.sc_tatica;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.sang.sc_tatica.FullTips.FullTipsActivity;
import com.sang.sc_tatica.Ranks.RankActivity;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import static com.sang.sc_tatica.FirstTimeActivity.INPUT_NAME;

public class HomeActivity extends AppCompatActivity {

    private DrawerLayout drawer;
    private MaterialToolbar toolbar;
    private NavigationView nav_drawerView;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //init auth firebase:
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        //get userName:
        Intent intent = getIntent();
        if (intent != null) {
            String user_Name = intent.getStringExtra(INPUT_NAME);
            if (user_Name != null) {
                firebaseDatabase = FirebaseDatabase.getInstance();
                databaseReference = firebaseDatabase.getReference("Users");
                HashMap<String, Object> update = new HashMap<>();
                update.put("name", user_Name);
                databaseReference.child(user.getUid()).updateChildren(update);
            }
        }

        //Attach the SectionsPagerAdapter to the ViewPager
        SectionsPagerAdapter pagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        ViewPager pager = (ViewPager) findViewById(R.id.viewPager);
        pager.setAdapter(pagerAdapter);

        //Attach the ViewPager to the TabLayout
        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(pager);

        //TODO: Set up Top action - bar:
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle("Home");
        drawer = findViewById(R.id.drawer);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        //TODO: Set up Top action - bar:
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.drawer_open, R.string.drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        nav_drawerView = findViewById(R.id.nav_drawerView);
        ImageView imageView = nav_drawerView.findViewById(R.id.imageView);
        TextView txtName = nav_drawerView.findViewById(R.id.txtName);
        TextView txtEmail = nav_drawerView.findViewById(R.id.txtEmail);

        // Firebase:
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");
        // Get some info of user:
        Query query = databaseReference.orderByChild("userID").equalTo(user.getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //check until required data get:
                for (DataSnapshot ds : snapshot.getChildren()) {
                    //Get data from the user:
                    String image = String.valueOf(ds.child("image").getValue());
                    String name = String.valueOf(ds.child("name").getValue());
                    String email = String.valueOf(ds.child("email").getValue());

                    //set data:
                    txtName.setText(name);
                    txtEmail.setText(email);

                    try {
                        //if image is received then set
                        Picasso.get().load(image).into(imageView);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        nav_drawerView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_friends:
                        startActivity(new Intent(HomeActivity.this, SearchUsersActivity.class));
                        break;
                    case R.id.nav_tips:
                        //TODO: fixed 03/19/2021
                        startActivity(new Intent(HomeActivity.this, FullTipsActivity.class));
                        break;
                    case R.id.nav_rank:
                        startActivity(new Intent(HomeActivity.this, RankActivity.class));
                        break;
                    case R.id.nav_help:
                        startActivity(new Intent(HomeActivity.this, HelpActivity.class));
                        break;
                    case R.id.nav_feedback:
                        startActivity(new Intent(HomeActivity.this, FeedbackActivity.class));
                        break;
                    case R.id.nav_about:
                        Toast.makeText(HomeActivity.this, "We are developing \n this function", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
                return false;
            }
        });
        return true;
    }

    private class SectionsPagerAdapter extends FragmentPagerAdapter {
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new TodayTaskFragment();
                case 1:
                    return new PomodoroFragment();
            }
            return null;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getResources().getText(R.string.today_tab);
                case 1:
                    return getResources().getText(R.string.pomodoro_tab);
            }
            return null;
        }
    }

    private void checkUserStatus() {
        // get current user
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            // user signed in and stay here
            Toast.makeText(this, "You logged in as " + user.getEmail(), Toast.LENGTH_SHORT).show();
            finish();
        } else {
            // user not signed in, goto main activity
            startActivity(new Intent(HomeActivity.this, LoginAccountActivity.class));
            finish();
        }
    }

    // handle menu item clicks
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // get item id
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.action_logout:
                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                firebaseAuth.signOut();
                checkUserStatus();
                break;
            case R.id.action_profile:
                startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
                break;
            default:
                break;
        }
        return true;
    }
}