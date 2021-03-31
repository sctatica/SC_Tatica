package com.sang.sc_tatica;

import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class TaskInDurationFragment extends Fragment {

    private TaskAdapter adapterTasks;
    private ArrayList<Task> taskList = new ArrayList<>();
    private RecyclerView recyclerView;

    //init firebase:
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_task_in_duration, container, false);

        //TODO: init RecyclerView:
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //TODO: Get current Date:
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        String currentDate = new SimpleDateFormat("dd/MM/yyyy").format(date);

        //init firebase:
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Tasks");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                taskList = new ArrayList<>();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Task task = ds.getValue(Task.class);
                    if (task != null) {
                        if (task.getUserID().equals(user.getUid())) {
                            if (!task.isDoneStatus() && onDurationDate(task.getDueDate(), currentDate))
                                taskList.add(task);
                        }
                    }
                }
                if (taskList != null) {
                    Comparator<Task> newTasksComparator = new Comparator<Task>() {
                        @Override
                        public int compare(Task o1, Task o2) {
                            int comparision = Integer.parseInt(o1.getPriority()) - Integer.parseInt(o2.getPriority());
                            if (comparision == 0) {
                                Date date1 = null, date2 = null;
                                try {
                                    date1 = new SimpleDateFormat("dd/MM/yyyy").parse(o1.getDueDate());
                                    date2 = new SimpleDateFormat("dd/MM/yyyy").parse(o2.getDueDate());
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                return date1.compareTo(date2);
                            }
                            return comparision;
                        }
                    };
                    Collections.sort(taskList, newTasksComparator);
                }
                adapterTasks = new TaskAdapter(getActivity(), taskList);
                recyclerView.setAdapter(adapterTasks);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //TODO: Set up Delete Action:
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        return view;
    }

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }
        @Override
        public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int direction) {
            final int position = viewHolder.getAdapterPosition();
            if (direction == ItemTouchHelper.RIGHT) {
                Task deletedTask = taskList.get(position);
                Snackbar.make(viewHolder.itemView, "You have successfully deleted " + deletedTask.getName(), Snackbar.LENGTH_LONG)
                        .setAction("Undo", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                                DatabaseReference databaseReference = firebaseDatabase.getReference("Tasks");
                                databaseReference.child(deletedTask.getTaskId()).setValue(deletedTask);
                            }
                        }).show();
                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                DatabaseReference databaseReference = firebaseDatabase.getReference("Tasks");
                databaseReference.child(deletedTask.getTaskId()).removeValue();
            }
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeRightBackgroundColor(ContextCompat.getColor(getActivity(), R.color.red))
                    .addSwipeRightActionIcon(R.drawable.ic_delete)
                    .addSwipeRightLabel(getString(R.string.action_delete))
                    .setSwipeRightLabelColor(Color.WHITE)
                    .create()
                    .decorate();
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };

    private boolean onDurationDate(String dueDate, String currentDate) {
        Date end = new Date();
        Date entry = new Date();
        try {
            end = new SimpleDateFormat("dd/MM/yyyy").parse(dueDate);
            entry = new SimpleDateFormat("dd/MM/yyyy").parse(currentDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return (entry.equals(end) || entry.before(end));
    }
}
