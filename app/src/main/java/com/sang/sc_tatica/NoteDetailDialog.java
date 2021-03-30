package com.sang.sc_tatica;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class NoteDetailDialog extends DialogFragment {
    public static final String NOTE_KEY = "Note details";

    private TextView txtName, txtShowDesc;
//    private Button btnLeave, btnEdit;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_note_detail, null);

        initViews(view);

        Bundle bundle = getArguments();
        if (bundle != null) {
            ModelUserNotes note = bundle.getParcelable(NOTE_KEY);
            if (note != null) {
                txtName.setText(note.getTitle());
                txtShowDesc.setText(note.getDescription());
            }
//            btnEdit.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(getActivity(), EditTaskActivity.class);
//                    intent.putExtra(NOTE_KEY, task);
//                    startActivity(intent);
//                }
//            });
//            btnLeave.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    dismiss();
//                }
//            });
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()).setView(view);
        return builder.create();
    }

    private void initViews(View view) {
        txtName = view.findViewById(R.id.txtName);
        txtShowDesc = view.findViewById(R.id.txtShowDesc);
//        btnLeave = view.findViewById(R.id.btnLeave);
//        btnEdit = view.findViewById(R.id.btnEdit);
    }
}
