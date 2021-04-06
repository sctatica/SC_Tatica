package com.sang.sc_tatica;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;

import com.google.android.material.appbar.MaterialToolbar;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

public class HelpActivity extends AppCompatActivity {
    private MaterialToolbar toolbar;
    private YouTubePlayerView youTubePlayerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);


        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Tutorial");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        youTubePlayerView = findViewById(R.id.youtube_tutorial);
        getLifecycle().addObserver(youTubePlayerView);

    }
}