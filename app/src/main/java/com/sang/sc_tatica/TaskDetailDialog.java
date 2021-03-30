package com.sang.sc_tatica;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import static com.sang.sc_tatica.TaskAdapter.EDIT_TASK;

public class TaskDetailDialog extends DialogFragment {
    public static final String TASK_KEY = "Task details";

    private TextView txtName, txtShowStartDate, txtShowDueDate, txtShowDesc;
    private Button btnLeave, btnEdit;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_task_detail, null);

        initViews(view);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()).setView(view);

        Bundle bundle = getArguments();
        if (bundle != null) {
            Task task = bundle.getParcelable(TASK_KEY);
            if (task != null) {
                txtName.setText(task.getName());
                txtShowDesc.setText(task.getShortDesc());
                txtShowStartDate.setText(task.getStartTime() + " - " + task.getStartDate());
                txtShowDueDate.setText(task.getDueTime() + " - " + task.getDueDate());
            }
            btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), EditTaskActivity.class);
                    intent.putExtra(EDIT_TASK, task);
                    startActivity(intent);
                }
            });
            btnLeave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
        }
        return builder.create();
    }

    private void initViews(View view) {
        txtName = view.findViewById(R.id.txtName);
        txtShowStartDate = view.findViewById(R.id.txtShowStartDate);
        txtShowDueDate = view.findViewById(R.id.txtShowDueDate);
        txtShowDesc = view.findViewById(R.id.txtShowDesc);
        btnLeave = view.findViewById(R.id.btnLeave);
        btnEdit = view.findViewById(R.id.btnEdit);
    }

}
