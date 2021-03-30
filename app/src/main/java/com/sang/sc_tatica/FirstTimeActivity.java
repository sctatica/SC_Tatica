package com.sang.sc_tatica;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class FirstTimeActivity extends AppCompatActivity {
    public static final String INPUT_NAME = "USER NAME";

    private String userName;
    private EditText edtUserName;
    private Button btnOK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_time);

        initViews();

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userName = edtUserName.getText().toString();
                if (!userName.equals("")) {
                    Intent intent = new Intent(FirstTimeActivity.this, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(INPUT_NAME, userName);
                    startActivity(intent);
                }
            }
        });
    }

    private void initViews() {
        edtUserName = findViewById(R.id.edtUserName);
        btnOK = findViewById(R.id.btnOK);
    }
}