package com.sang.userlogin_registration;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class OtherUsersAdapter extends RecyclerView.Adapter<OtherUsersAdapter.ViewHolder>{

    private Context context;
    private ArrayList<User> usersList;

    public OtherUsersAdapter(Context context, ArrayList<User> usersList) {
        this.context = context;
        this.usersList = usersList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            //if image is received then set
            Picasso.get().load(usersList.get(position).getImage()).into(holder.imgAvatar);
        } catch (Exception e) {
            e.printStackTrace();
        }
        holder.txtName.setText(usersList.get(position).getName());
        holder.txtEmail.setText(usersList.get(position).getEmail());
        holder.txtTimeWork.setText("Time work: " + usersList.get(position).getTimeWork() + " seconds");
        //TODO: Set icon for rank:

    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgAvatar;
        private TextView txtName, txtEmail, txtTimeWork;
        private ImageView imgRank;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgAvatar = itemView.findViewById(R.id.imgAvatar);
            txtName = itemView.findViewById(R.id.txtName);
            txtEmail = itemView.findViewById(R.id.txtEmail);
            txtTimeWork = itemView.findViewById(R.id.txtTimeWork);
            imgRank = itemView.findViewById(R.id.imgRank);
        }
    }
}
