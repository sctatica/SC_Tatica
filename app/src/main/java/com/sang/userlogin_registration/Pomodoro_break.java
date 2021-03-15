package com.sang.userlogin_registration;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Locale;

public class Pomodoro_break extends AppCompatActivity {

    private int seconds = 5;//s;
    Animation rotate;
    FloatingActionButton play_break;
    //Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pomodoro_break);

        rotate = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotation);
        play_break = findViewById(R.id.breakBtn);

//        intent = getIntent();
//        s = intent.getIntExtra("key",seconds);

        play_break.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                play_break.setVisibility(View.GONE);
                runTimer();
            }
        });
    }
    private void runTimer() {
        final TextView textView = findViewById(R.id.time_break);
        //seconds = s;

        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                int minutes = (seconds % 3600) / 60;
                int secs = seconds % 60;

                String time = String.format(Locale.getDefault(), "%02d:%02d", minutes, secs);
                textView.setText(time);

                if (seconds == 0){
                    finish();
                }

                if (seconds > 0) {
                    seconds--;
                }
                handler.postDelayed(this,1000);
            }
        });

    }

}