package com.sang.sc_tatica;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;

public class TaskManagementActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;

    //init bottomNavigation:
    private BottomNavigationView nav_bottomView;

    @Override
    protected void onStart() {
        super.onStart();
        nav_bottomView.setSelectedItemId(R.id.nav_task);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_management);

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
        toolbar.setTitle("Task Management");
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
                        startActivity(new Intent(TaskManagementActivity.this, HomeActivity.class));
                        return true;
                    case R.id.nav_task:
                        return true;
                    case R.id.nav_note:
                        startActivity(new Intent(TaskManagementActivity.this, ListUserNotesActivity.class));
                        return true;
                    case R.id.nav_community:
                        startActivity(new Intent(TaskManagementActivity.this, CommunityActivity.class));
                        return true;
                    default:
                        break;
                }
                return false;
            }
        });
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
                    return new TaskInDurationFragment();
                case 1:
                    return new TaskEndDurationFragment();
            }
            return null;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getResources().getText(R.string.in_duration);
                case 1:
                    return getResources().getText(R.string.end_duration);
            }
            return null;
        }
    }
}