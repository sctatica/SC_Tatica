package com.sang.sc_tatica;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-Activity. */
                checkUserStatus();
                finish();
            }
        }, 2000);
    }

    private void checkUserStatus() {
        // get current user
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            //user signed in, goto Home activity
            startActivity(new Intent(SplashScreenActivity.this, HomeActivity.class));
        } else {
            // user not signed in, goto Main activity
            startActivity(new Intent(SplashScreenActivity.this, LoginAccountActivity.class));
        }
    }
}
