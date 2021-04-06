package com.sang.sc_tatica;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import android.widget.ImageView;
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
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.sang.sc_tatica.TaskAdapter.EDIT_TASK;

public class EditTaskActivity extends AppCompatActivity {

    //Toolbar:
    private MaterialToolbar toolbar;

    //Views:
    private ImageView imageView;
    private TextView txtUserName;
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
    private Task incomingTask;
    private String userName, userEmail, userID;
    private String name, start_time, start_date, due_time, due_date, shortDesc, priority, taskID;

    //Spinner:
    private ArrayList<Priority> priorityList;
    private PriorityAdapter priorityAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);

        //init toolbar:
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Edit Task");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        //init views;
        initViews();

        //TODO: Get Edit task information:
        Intent intent = getIntent();
        if (intent != null) {
            incomingTask = intent.getParcelableExtra(EDIT_TASK);
            userName = String.valueOf(incomingTask.getUserName());
            userEmail = String.valueOf(incomingTask.getUserEmail());
            userID = String.valueOf(incomingTask.getUserID());
            txtUserName.setText(userName);
            edtName.setText(incomingTask.getName());
            txtStartTime.setText(incomingTask.getStartTime());
            txtStartDate.setText(incomingTask.getStartDate());
            txtDueTime.setText(incomingTask.getDueTime());
            txtDueDate.setText(incomingTask.getDueDate());
            edtShortDesc.setText(incomingTask.getShortDesc());
        }

        //TODO: Get avatar image of user:
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");
        Query query = databaseReference.orderByChild("userID").equalTo(userID);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String image = String.valueOf(ds.child("image").getValue());
                    //Set avatar image
                    try {
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

        //init current user firebase:
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        //TODO: Set up Priority:
        priorityList = new ArrayList<>();
        priorityList.add(new Priority("0", "Urgent/ Important", R.drawable.ic_circle_green));
        priorityList.add(new Priority("1", "Urgent/ Not Important", R.drawable.ic_circle_red));
        priorityList.add(new Priority("2", "Not Urgent/ Important", R.drawable.ic_circle_blue));
        priorityList.add(new Priority("3", "Not Urgent/ Not Important", R.drawable.ic_circle_yellow));
        priorityAdapter = new PriorityAdapter(this, priorityList);
        spinnerPriorities.setAdapter(priorityAdapter);
        int incoming_priority = Integer.parseInt(incomingTask.getPriority());
        spinnerPriorities.setSelection(incoming_priority);
        spinnerPriorities.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Priority clickedItem = (Priority) parent.getItemAtPosition(position);
                String clickedPriorityDescription = clickedItem.getDescription();
                Toast.makeText(EditTaskActivity.this, clickedPriorityDescription + " selected", Toast.LENGTH_SHORT).show();
                priority = clickedItem.getPriority();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

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

        //TODO: Edit Task from Firebase:
        btnCreateTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: Get info of edit task:
                taskID = incomingTask.getTaskId();
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
                    Toast.makeText(EditTaskActivity.this, "Please input name of task", Toast.LENGTH_SHORT).show();
                } else if (!date_end.after(date_start)) {
                    Toast.makeText(EditTaskActivity.this, "Due date should be after Start date", Toast.LENGTH_SHORT).show();
                } else {
                    //Create a new Task in firebase:
                    firebaseDatabase = FirebaseDatabase.getInstance();
                    databaseReference = firebaseDatabase.getReference("Tasks");
                    Task editTask = new Task(userName, userEmail, userID, name, start_time, start_date, due_time, due_date, shortDesc, priority, taskID);
                    databaseReference.child(taskID).setValue(editTask);
                    Toast.makeText(EditTaskActivity.this, "Edit task successfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(EditTaskActivity.this, TaskManagementActivity.class));
                    finish();
                }
            }
        });
    }

    private void initViews() {
        imageView = findViewById(R.id.imageView);
        txtUserName = findViewById(R.id.txtUserName);
        edtName = findViewById(R.id.edtName);
        spinnerPriorities = findViewById(R.id.spinnerPriorities);
        txtStartTime = findViewById(R.id.txtStartTime);
        txtStartDate = findViewById(R.id.txtStartDate);
        txtDueTime = findViewById(R.id.txtDueTime);
        txtDueDate = findViewById(R.id.txtDueDate);
        edtShortDesc = findViewById(R.id.edtShortDesc);
        btnCreateTask = findViewById(R.id.btnCreateTask);
    }

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
        new TimePickerDialog(EditTaskActivity.this,
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
        new DatePickerDialog(EditTaskActivity.this,
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
        new TimePickerDialog(EditTaskActivity.this,
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
        new DatePickerDialog(EditTaskActivity.this,
                dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        ).show();
    }
}