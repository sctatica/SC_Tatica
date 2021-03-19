package com.sang.userlogin_registration;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder>{

    private Context context;
    private ArrayList<Event> eventsList;

    public EventAdapter(Context context, ArrayList<Event> eventsList) {
        this.context = context;
        this.eventsList = eventsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_event, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            //if image is received then set
            Picasso.get().load(eventsList.get(position).getImage()).into(holder.imgEvent);
        } catch (Exception e) {
            e.printStackTrace();
        }
        holder.txtName.setText(eventsList.get(position).getName());
        holder.txtAvailable.setText("Slot: " + String.valueOf(eventsList.get(position).getUsersList().size()) + "/" + eventsList.get(position).getLimitRegister());
        holder.txtStartDate.setText(eventsList.get(position).getStartTime() + " - " + eventsList.get(position).getStartDate());
        holder.txtDueDate.setText( eventsList.get(position).getDueTime() + " - " + eventsList.get(position).getDueDate());
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context)
                        .setTitle("Do you want to join with us?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //dismiss dialog
                                dialog.dismiss();
                            }
                        });
                //show dialog
                builder.create().show();
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return eventsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtName, txtAvailable, txtStartDate, txtDueDate;
        private ImageView imgEvent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgEvent = itemView.findViewById(R.id.imgEvent);
            txtName = itemView.findViewById(R.id.txtName);
            txtStartDate = itemView.findViewById(R.id.txtStartDate);
            txtDueDate = itemView.findViewById(R.id.txtDueDate);
            txtAvailable = itemView.findViewById(R.id.txtAvailable);
        }
    }
}
