package com.sang.userlogin_registration;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CustomAdapter_UserNotes extends RecyclerView.Adapter<ViewHolder_UserNotes> {

    ListUserNotes listActivity;
    List<ModelUserNotes> modelUserNotes;
    Context context;

    public CustomAdapter_UserNotes(ListUserNotes listActivity, List<ModelUserNotes> modelUserNotes) {
        this.listActivity = listActivity;
        this.modelUserNotes = modelUserNotes;

    }

    @NonNull
    @Override
    public ViewHolder_UserNotes onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       // inflate layout
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.model_user_notes,parent,false);

        ViewHolder_UserNotes viewHolder_userNotes = new ViewHolder_UserNotes(itemView);
        // handle item clicks

        viewHolder_userNotes.setOnClickListener(new ViewHolder_UserNotes.ClickListener() {
            @Override
            public void OnItemClick(View view, int position) {
                // this will be called when user click item

                // show data in toast when click
                String title = modelUserNotes.get(position).getTitle();
                String description = modelUserNotes.get(position).getDescription();
                Toast.makeText(listActivity,title+"" +description,Toast.LENGTH_LONG).show();
            }

            @Override
            public void OnItemLongClick(View view, int position) {
                // this will be called when user long click item
                AlertDialog.Builder builder = new AlertDialog.Builder(listActivity);
                // options to display in dialog
                String[] options = {"Edit","Delete"};
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0){
                            // update is clicked
                            // get data
                            String id = modelUserNotes.get(position).getId();
                            String title = modelUserNotes.get(position).getTitle();
                            String description = modelUserNotes.get(position).getDescription();

                            // intent to start activity
                            Intent intent = new Intent(listActivity, NoteInPomodro.class );

                            // put data in intent
                            intent.putExtra("pId",id);
                            intent.putExtra("pTitle",title);
                            intent.putExtra("pDescription", description);

                            // start activity
                            listActivity.startActivity(intent);
                        }
                        if (which == 1)
                        {
                            // delete
                        }
                    }
                }).create().show();

            }
        });

        return viewHolder_userNotes;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder_UserNotes holder, int position) {
        // bind views / set data
        holder.mTitleTv.setText(modelUserNotes.get(position).getTitle());
        holder.mDescriptionTv.setText(modelUserNotes.get(position).getDescription());
    }

    @Override
    public int getItemCount() {
        return modelUserNotes.size();
    }
}
