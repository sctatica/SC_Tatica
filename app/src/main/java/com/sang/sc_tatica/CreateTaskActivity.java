package com.sang.sc_tatica;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class CreateTaskActivity extends AppCompatActivity {

    //Toolbar:
    private MaterialToolbar toolbar;

    //Views:
    private EditText edtName;
    private Spinner spinnerPriorities;
    private TextView txtStartTime, txtStartDate,
            txtDueTime, txtDueDate;
    private EditText edtShortDesc;
    private Button btnCreateTask;

    //Firebase:
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    //Task:
    private String userName, userEmail, userID;
    private String name, start_time, start_date, due_time, due_date, shortDesc, priority, taskID;

    //Spinner:
    private ArrayList<Priority> priorityList;
    private PriorityAdapter priorityAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);

        //init toolbar:
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Create Task");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        //init views;
        initViews();

        //init current user firebase:
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        //TODO: Get Name, Email, ID of CURRENT USER:
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");
        Query query = databaseReference.orderByChild("userID").equalTo(user.getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String user_Name = String.valueOf(ds.child("name").getValue());
                    String user_Email = String.valueOf(ds.child("email").getValue());
                    String user_ID = String.valueOf(ds.child("userID").getValue());

                    //Get some info of user:
                    userName = user_Name;
                    userEmail = user_Email;
                    userID = user_ID;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        //TODO: Set up Priority:
        priorityList = new ArrayList<>();
        priorityList.add(new Priority("0", "Urgent/ Important", R.drawable.ic_circle_green));
        priorityList.add(new Priority("1", "Urgent/ Not Important", R.drawable.ic_circle_red));
        priorityList.add(new Priority("2", "Not Urgent/ Important", R.drawable.ic_circle_blue));
        priorityList.add(new Priority("3", "Not Urgent/ Not Important", R.drawable.ic_circle_yellow));
        priorityAdapter = new PriorityAdapter(this, priorityList);
        spinnerPriorities.setAdapter(priorityAdapter);
        spinnerPriorities.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Priority clickedItem = (Priority) parent.getItemAtPosition(position);
                String clickedPriorityDescription = clickedItem.getDescription();
                Toast.makeText(CreateTaskActivity.this, clickedPriorityDescription + " selected", Toast.LENGTH_SHORT).show();
                priority = clickedItem.getPriority();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //TODO: set up default time:
        Calendar default_calendar = Calendar.getInstance();
        Date defaultTime = default_calendar.getTime();
        txtStartTime.setText(new SimpleDateFormat("HH:mm").format(defaultTime));
        txtStartDate.setText(new SimpleDateFormat("dd/MM/yyyy").format(defaultTime));
        default_calendar.setTime(defaultTime);
        default_calendar.add(Calendar.HOUR, 1);
        defaultTime = default_calendar.getTime();
        txtDueTime.setText(new SimpleDateFormat("HH:mm").format(defaultTime));
        txtDueDate.setText(new SimpleDateFormat("dd/MM/yyyy").format(defaultTime));

        //TODO: set up user choose time:
        txtStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showStartTimeDialog();
            }
        });

        txtStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showStartDateDialog();
            }
        });

        txtDueTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDueTimeDialog();
            }
        });

        txtDueDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDueDateDialog();
            }
        });

        //TODO: Add new Task to Firebase:
        btnCreateTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: Get info of new task:
                taskID = String.valueOf(UUID.randomUUID());
                name = edtName.getText().toString();
                start_time = txtStartTime.getText().toString();
                start_date = txtStartDate.getText().toString();
                due_time = txtDueTime.getText().toString();
                due_date = txtDueDate.getText().toString();
                shortDesc = edtShortDesc.getText().toString();

                //TODO: Check due date and start date:
                String InputStartDate = String.valueOf(start_time + " " + start_date);
                String InputDueDate = String.valueOf(due_time + " " + due_date);
                Date date_start = new Date();
                Date date_end = new Date();
                try {
                    date_start = new SimpleDateFormat("HH:mm dd/MM/yyyy").parse(InputStartDate);
                    date_end = new SimpleDateFormat("HH:mm dd/MM/yyyy").parse(InputDueDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if (name.equals("")) {
                    Toast.makeText(CreateTaskActivity.this, "Please enter name of task", Toast.LENGTH_SHORT).show();
                } else if (!date_end.after(date_start)) {
                    Toast.makeText(CreateTaskActivity.this, "Due date should be after Start date", Toast.LENGTH_SHORT).show();
                } else {
                    //Create a new Task in firebase:
                    firebaseDatabase = FirebaseDatabase.getInstance();
                    databaseReference = firebaseDatabase.getReference("Tasks");
                    Task newTask = new Task(userName, userEmail, userID, name, start_time, start_date, due_time, due_date, shortDesc, priority, taskID);
                    databaseReference.child(taskID).setValue(newTask);

                    //Navigate to TaskManagementActivity:
                    Toast.makeText(CreateTaskActivity.this, "Create a new task successfully", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(CreateTaskActivity.this, TaskManagementActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        });
    }

    private void initViews() {
        edtName = findViewById(R.id.edtName);
        spinnerPriorities = findViewById(R.id.spinnerPriorities);
        txtStartTime = findViewById(R.id.txtStartTime);
        txtStartDate = findViewById(R.id.txtStartDate);
        txtDueTime = findViewById(R.id.txtDueTime);
        txtDueDate = findViewById(R.id.txtDueDate);
        edtShortDesc = findViewById(R.id.edtShortDesc);
        btnCreateTask = findViewById(R.id.btnCreateTask);
    }

    @SuppressLint("SimpleDateFormat")
    private void showStartTimeDialog() {
        final Calendar calendar = Calendar.getInstance();
        TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                txtStartTime.setText(new SimpleDateFormat("HH:mm").format(calendar.getTime()));
            }
        };
        new TimePickerDialog(CreateTaskActivity.this,
                timeSetListener,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                false).show();
    }

    private void showStartDateDialog() {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                txtStartDate.setText(new SimpleDateFormat("dd/MM/yyyy").format(calendar.getTime()));
            }
        };
        new DatePickerDialog(CreateTaskActivity.this,
                dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    private void showDueTimeDialog() {
        final Calendar calendar = Calendar.getInstance();
        TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                txtDueTime.setText(new SimpleDateFormat("HH:mm").format(calendar.getTime()));
            }
        };
        new TimePickerDialog(CreateTaskActivity.this,
                timeSetListener,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                false).show();
    }

    private void showDueDateDialog() {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                txtDueDate.setText(new SimpleDateFormat("dd/MM/yyyy").format(calendar.getTime()));
            }
        };
        new DatePickerDialog(CreateTaskActivity.this,
                dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

}