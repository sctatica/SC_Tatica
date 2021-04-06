package com.sang.sc_tatica;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static com.sang.sc_tatica.EventDetailDialog.EVENT_KEY;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {

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
        holder.txtStartDate.setText(eventsList.get(position).getStartDate());
        holder.txtDueDate.setText(eventsList.get(position).getDueDate());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventDetailDialog dialog = new EventDetailDialog();
                Bundle bundle = new Bundle();
                bundle.putParcelable(EVENT_KEY, eventsList.get(position));
                dialog.setArguments(bundle);
                dialog.show(((AppCompatActivity) context).getSupportFragmentManager(), "Event Detail Dialog");
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
