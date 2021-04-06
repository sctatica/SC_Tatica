package com.sang.sc_tatica;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import static com.sang.sc_tatica.NoteDetailDialog.NOTE_KEY;

public class UserNotesAdapter extends RecyclerView.Adapter<UsersNotesViewHolder> {

    ListUserNotesActivity listActivity;
    List<ModelUserNotes> modelUserNotes;

    public UserNotesAdapter(ListUserNotesActivity listActivity, List<ModelUserNotes> modelUserNotes) {
        this.listActivity = listActivity;
        this.modelUserNotes = modelUserNotes;
    }

    @NonNull
    @Override
    public UsersNotesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // inflate layout
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.model_user_notes, parent, false);

        UsersNotesViewHolder viewHolder_userNotes = new UsersNotesViewHolder(itemView);
        // handle item clicks

        viewHolder_userNotes.setOnClickListener(new UsersNotesViewHolder.ClickListener() {
            @Override
            public void OnItemClick(View view, int position) {
                // this will be called when user click item

                // show data in toast when click
                String title = modelUserNotes.get(position).getTitle();
                String description = modelUserNotes.get(position).getDescription();
                Toast.makeText(listActivity, title + "" + description, Toast.LENGTH_LONG).show();
            }

            @Override
            public void OnItemLongClick(View view, int position) {
                // this will be called when user long click item
                AlertDialog.Builder builder = new AlertDialog.Builder(listActivity);
                // options to display in dialog
                String[] options = {"Edit", "Delete"};
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            // update is clicked
                            // get data
                            String id = modelUserNotes.get(position).getId();
                            String title = modelUserNotes.get(position).getTitle();
                            String description = modelUserNotes.get(position).getDescription();

                            // intent to start activity
                            Intent intent = new Intent(listActivity, NoteInPomodroActivity.class);

                            // put data in intent
                            intent.putExtra("pId", id);
                            intent.putExtra("pTitle", title);
                            intent.putExtra("pDescription", description);

                            // start activity
                            listActivity.startActivity(intent);
                        }
                        if (which == 1) {
                            // delete
                            listActivity.deleteNote(position);
                        }
                    }
                }).create().show();
            }
        });

        return viewHolder_userNotes;
    }

    @Override
    public void onBindViewHolder(@NonNull UsersNotesViewHolder holder, int position) {
        // bind views / set data
        holder.mTitleTv.setText(modelUserNotes.get(position).getTitle());
        holder.mDescriptionTv.setText(modelUserNotes.get(position).getDescription());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NoteDetailDialog dialog = new NoteDetailDialog();
                Bundle bundle = new Bundle();
                bundle.putParcelable(NOTE_KEY, modelUserNotes.get(position));
                dialog.setArguments(bundle);
                dialog.show(((AppCompatActivity) v.getContext()).getSupportFragmentManager(), "Task Detail Dialog");
            }
        });
    }

    @Override
    public int getItemCount() {
        return modelUserNotes.size();
    }
}
