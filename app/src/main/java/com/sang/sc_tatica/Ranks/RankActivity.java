package com.sang.sc_tatica.Ranks;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.sang.sc_tatica.R;

public class RankActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private LinearLayout rank1;
    private LinearLayout rank2;
    private LinearLayout rank3;
    public static final String WHICH_RANK = "which_rank";
    private int rank_number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank);

//         Show toolbar
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Ranks");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        rank1 = findViewById(R.id.rank1);
        rank2 = findViewById(R.id.rank2);
        rank3 = findViewById(R.id.rank3);

        rank1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RankDetailDialog rankDetailDialog = new RankDetailDialog();
                Bundle bundle = new Bundle();
                rank_number = 0;
                bundle.putInt(WHICH_RANK, rank_number);
                rankDetailDialog.setArguments(bundle);
                rankDetailDialog.show((RankActivity.this).getSupportFragmentManager(), "rank");
            }
        });

        rank2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                RankDetailDialog rankDetailDialog = new RankDetailDialog();
                Bundle bundle = new Bundle();
                rank_number = 1;
                bundle.putInt(WHICH_RANK, rank_number);
                rankDetailDialog.setArguments(bundle);
                rankDetailDialog.show((RankActivity.this).getSupportFragmentManager(), "rank");
            }
        });

        rank3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RankDetailDialog rankDetailDialog = new RankDetailDialog();
                Bundle bundle = new Bundle();
                rank_number = 2;
                bundle.putInt(WHICH_RANK, rank_number);
                rankDetailDialog.setArguments(bundle);
                rankDetailDialog.show((RankActivity.this).getSupportFragmentManager(), "rank");
            }
        });
    }
}