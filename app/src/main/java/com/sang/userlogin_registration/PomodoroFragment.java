package com.sang.userlogin_registration;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Time;
import java.util.HashMap;
import java.util.Locale;

public class PomodoroFragment extends Fragment {

    public interface TimeWorkCallBack {
        void onCallBack(User got_user);
    }

    public static final String OPTIONAL_BREAK_TIME = "break_time";

    private int seconds = 1500;
    private int workTime = 0;
    private boolean running = false;
    private boolean wasRunning = false;
    private boolean  case_55_5 = false;
    private boolean  case_50_10 = false;
    private boolean  case_45_15 = false;
    private boolean  case_40_20 = false;

    //Intent intent;
    FloatingActionButton playButton, pauseButton, letsBreak, reset;
    Animation rotate;
    ImageView circle, add, sub;
    TextView youWill;
    SwitchMaterial auto;
    Spinner howLong;
    Button take_note;
    ToggleButton wifi, screen, music;
    Intent gotoBreak;                   // goto break

    //init firebase auth:
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            seconds = savedInstanceState.getInt("seconds");
            running = savedInstanceState.getBoolean("running");
            wasRunning = savedInstanceState.getBoolean("wasRunning");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_pomodoro, container, false);

        // init firebase auth:
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        runTimer(layout);
        // rotate the circle
        //rotate = AnimationUtils.loadAnimation(getContext(),R.anim.rotation);

        // set Listener for play, pause button, texts,...
        playButton = layout.findViewById(R.id.play);
        pauseButton = layout.findViewById(R.id.pause);
        letsBreak = layout.findViewById(R.id.letsBreak);

        // switch auto
        auto = layout.findViewById(R.id.auto);

        // reset to regain pause, play button
        reset = layout.findViewById(R.id.reset);

        // set auto time
        youWill = layout.findViewById(R.id.youWil);
        sub = layout.findViewById(R.id.sub);
        add = layout.findViewById(R.id.add);

        howLong = layout.findViewById(R.id.howLong);
        // rotate animation
        circle = layout.findViewById(R.id.circle);

        // At beginning
        howLong.setEnabled(false);                          // disabled
        howLong.setVisibility(View.GONE);              // INVISIBLE
        youWill.setVisibility(View.GONE);
        letsBreak.setVisibility(View.GONE);
        reset.setVisibility(View.GONE);
        gotoBreak = new Intent(getActivity(), PomodoroBreakActivity.class);


        // EXTRA FEATURES---------------------------------------------------------------------------
        take_note = layout.findViewById(R.id.note);
        wifi = layout.findViewById(R.id.wifi);
        screen = layout.findViewById(R.id.screen_never_off);
        music = layout.findViewById(R.id.music);


        // wifi
        wifi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                CharSequence text_off = "Wifi turned off";
                CharSequence text_on = "Wifi turned on";
                int duration = Snackbar.LENGTH_SHORT;
                Snackbar snackbar;
                if (isChecked) {
                    snackbar = Snackbar.make(layout.findViewById(R.id.wifi), text_off, duration);
                } else {
                    snackbar = Snackbar.make(layout.findViewById(R.id.wifi), text_on, duration);
                }
                snackbar.show();
            }
        });
        // screen
        screen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                CharSequence text_off = "Screen turned off";
                CharSequence text_on = "Screen turned on";
                int duration = Snackbar.LENGTH_SHORT;
                Snackbar snackbar;
                if (isChecked) {
                    snackbar = Snackbar.make(layout.findViewById(R.id.screen_never_off), text_off, duration);
                } else {
                    snackbar = Snackbar.make(layout.findViewById(R.id.screen_never_off), text_on, duration);
                }
                snackbar.show();
            }
        });
        // music
        music.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                CharSequence text_off = "Music turned off";
                CharSequence text_on = "Music turned on";
                int duration = Snackbar.LENGTH_SHORT;
                Snackbar snackbar;
                if (isChecked) {
                    snackbar = Snackbar.make(layout.findViewById(R.id.music), text_on, duration);
                } else {
                    snackbar = Snackbar.make(layout.findViewById(R.id.music), text_off, duration);
                }
                snackbar.show();
            }
        });

        // take note
        take_note.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goto_note_activity = new Intent(getActivity(), NoteInPomodroActivity.class);
                startActivity(goto_note_activity);
            }
        });

        //------------------------------------------------------------------------------------------

        // Pause, play countdown timer, break, reset------------------------------------------------
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Rotate the circle
                rotate = AnimationUtils.loadAnimation(getContext(), R.anim.rotation);
                circle.setAnimation(rotate);
                // Disable sub and add
                sub.setVisibility(View.GONE);
                add.setVisibility(View.GONE);
                Toast.makeText(getActivity(), "play", Toast.LENGTH_SHORT).show();
                onClickStart();
            }
        });
        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                circle.clearAnimation();
                Toast.makeText(getActivity(), "pause", Toast.LENGTH_SHORT).show();
                onClickStop(new TimeWorkCallBack() {
                    @Override
                    public void onCallBack(User got_user) {
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference reference = database.getReference("Users");
                        HashMap<String, Object> update = new HashMap<>();
                        String timeWorkUpdate = String.valueOf(Integer.parseInt(got_user.getTimeWork()) + workTime);
                        update.put("timeWork", timeWorkUpdate);
                        reference.child(currentUser.getUid()).updateChildren(update);
                        workTime = 0;
                    }
                });
            }
        });

        letsBreak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int optionTimeInAuto = 0;
                if (case_55_5){
                    optionTimeInAuto = 300;
                    gotoBreak.putExtra(OPTIONAL_BREAK_TIME,optionTimeInAuto);
                }
                else if(case_50_10){
                    optionTimeInAuto = 600;
                    gotoBreak.putExtra(OPTIONAL_BREAK_TIME ,optionTimeInAuto);
                }
                else if(case_45_15){
                    optionTimeInAuto = 900;
                    gotoBreak.putExtra(OPTIONAL_BREAK_TIME ,optionTimeInAuto);
                }
                else if (case_40_20){
                    optionTimeInAuto = 1200;
                    gotoBreak.putExtra(OPTIONAL_BREAK_TIME ,optionTimeInAuto);
                }
                startActivity(gotoBreak);
            }
        });

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!auto.isChecked()) {
                    seconds = 1500;
                    add.setVisibility(View.VISIBLE);
                    sub.setVisibility(View.VISIBLE);

                } else {
                    seconds = 3300;

                }
                playButton.setVisibility(View.VISIBLE);
                pauseButton.setVisibility(View.VISIBLE);
                letsBreak.setVisibility(View.GONE);
                reset.setVisibility(View.GONE);

                // snackbar when press reset button

                Snackbar snackbar = Snackbar.make(layout.findViewById(R.id.reset),"Reset",BaseTransientBottomBar.LENGTH_LONG);
                snackbar.setAction("Undo", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        pauseButton.setVisibility(View.GONE);
                        playButton.setVisibility(View.GONE);

                        reset.setVisibility(View.VISIBLE);
                        letsBreak.setVisibility(View.VISIBLE);
                    }
                });
                snackbar.show();
            }
        });

        //------------------------------------------------------------------------------------------

        // sub and add time-------------------------------------------------------------------------
        sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (seconds > 600) {
                    seconds -= 300;
                }
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (seconds <= 3300) {
                    seconds += 300;
                }
            }
        });
        //------------------------------------------------------------------------------------------

        // Auto Switch
        auto.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Auto on
                if (isChecked) {

                    // set timer to 55 minutes and break to 5 minutes
                    seconds = 3300;
                    // Display VISIBLE and ENABLE texts
                    sub.setVisibility(View.GONE);
                    add.setVisibility(View.GONE);

                    youWill.setEnabled(true);
                    howLong.setEnabled(true);
                    youWill.setVisibility(View.VISIBLE);
                    howLong.setVisibility(View.VISIBLE);

                    Snackbar snackbar = Snackbar.make(layout.findViewById(R.id.auto), "Auto on", BaseTransientBottomBar.LENGTH_SHORT);
                    snackbar.show();

                    // Spinner choose
                    howLong.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                            int item_clicked = (int) parent.getSelectedItemPosition();
                            switch (item_clicked) {
                                case 0:
                                    seconds = 3300;
                                    // get data to break
                                    case_55_5 = true;
                                    case_50_10 = false;
                                    case_45_15 = false;
                                    case_40_20 = false;
                                    break;
                                case 1:
                                    seconds = 3000;
                                    case_50_10 = true;
                                    case_55_5 = false;
                                    case_45_15 = false;
                                    case_40_20 = false;
                                    break;
                                case 2:
                                    seconds = 2700;
                                    case_45_15 = true;
                                    case_55_5 = false;
                                    case_50_10 = false;
                                    case_40_20 = false;
                                    break;
                                case 3:
                                    seconds = 2400;
                                    case_40_20 = true;
                                    case_55_5 = false;
                                    case_50_10 = false;
                                    case_45_15 = false;
                                    break;
                            }
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                            seconds = 1500;
                        }
                    });


                } else {
                    // set time to 25 minutes = 1500s
                    seconds = 1500;
                    // Enable sub and add
                    sub.setEnabled(true);
                    add.setEnabled(true);
                    // Disable texts and make invisible
                    youWill.setEnabled(false);
                    howLong.setEnabled(false);
                    youWill.setVisibility(View.GONE);
                    howLong.setVisibility(View.GONE);

                    Snackbar snackbar = Snackbar.make(layout.findViewById(R.id.auto), "Auto off - default: 25 minutes", BaseTransientBottomBar.LENGTH_SHORT);
                    snackbar.show();
                }
            }
        });

        return layout;
    }

    // save onSaveInstanceState
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt("seconds", seconds);
        outState.putBoolean("running", running);
        outState.putBoolean("wasRunning", wasRunning);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (wasRunning) running = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        wasRunning = running;
        running = false;
    }

    private void onClickStart() {
        running = true;
        workTime = 0;
    }

    private void onClickStop(TimeWorkCallBack timeWorkCallBack) {
        running = false;
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("Users");
        reference.child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                timeWorkCallBack.onCallBack(user);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    // Countdown function for pomodoro timer
    public void runTimer(View view) {
        final TextView textView = (TextView) view.findViewById(R.id.time);

        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                int minutes = (seconds % 3600) / 60;
                int secs = seconds % 60;

                String time = String.format(Locale.getDefault(), "%02d:%02d", minutes, secs);
                String time_test = String.format(Locale.getDefault(), "%03d",workTime);
                textView.setText(time);

                if (seconds == 0) {
                    running = false;
                    onClickStop(new TimeWorkCallBack() {
                                    @Override
                                    public void onCallBack(User got_user) {
                                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                                        DatabaseReference reference = database.getReference("Users");
                                        HashMap<String, Object> update = new HashMap<>();
                                        String timeWorkUpdate = String.valueOf(Integer.parseInt(got_user.getTimeWork()) + workTime);
                                        update.put("timeWork", timeWorkUpdate);
                                        reference.child(currentUser.getUid()).updateChildren(update);
                                        workTime = 0;
                                    }
                                });
                    circle.clearAnimation();
                    playButton.setVisibility(View.GONE);
                    pauseButton.setVisibility(View.GONE);
                    letsBreak.setVisibility(View.VISIBLE);
                    reset.setVisibility(View.VISIBLE);
                }

                if (running && seconds > 0) {
                    seconds--;
                    workTime++;
                }
                handler.postDelayed(this, 1000);
            }
        });
    }
}

