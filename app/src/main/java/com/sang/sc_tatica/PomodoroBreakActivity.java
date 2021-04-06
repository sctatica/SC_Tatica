package com.sang.sc_tatica;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Locale;

import static com.sang.sc_tatica.PomodoroFragment.OPTIONAL_BREAK_TIME;

public class PomodoroBreakActivity extends AppCompatActivity {

    private int secondsBreak = 300;       // get from PomodoroFragment
    Animation rotate;
    FloatingActionButton play_break;
    ImageView circle_break;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pomodoro_break);

        rotate = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotation);
        play_break = findViewById(R.id.breakBtn);
        circle_break = findViewById(R.id.circle_break);

        play_break.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Animation
                circle_break.startAnimation(rotate);
                Intent gotoBreak = getIntent();
                if (gotoBreak != null) {
                    secondsBreak = gotoBreak.getIntExtra(OPTIONAL_BREAK_TIME, 0);
                    if (secondsBreak != 0) {
                        play_break.setVisibility(View.GONE);
                        runTimer();
                    }
                }
            }
        });
    }

    private void runTimer() {
        final TextView textView = findViewById(R.id.time_break);

        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                int minutes = (secondsBreak % 3600) / 60;
                int secs = secondsBreak % 60;

                String time = String.format(Locale.getDefault(), "%02d:%02d", minutes, secs);
                textView.setText(time);

                if (secondsBreak == 0) {
                    finish();
                }

                if (secondsBreak > 0) {
                    secondsBreak--;
                }
                handler.postDelayed(this, 1000);
            }
        });

    }

}