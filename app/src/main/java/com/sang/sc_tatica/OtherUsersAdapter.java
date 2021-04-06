package com.sang.sc_tatica;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class OtherUsersAdapter extends RecyclerView.Adapter<OtherUsersAdapter.ViewHolder> {

    private Context context;
    private ArrayList<User> usersList;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private FirebaseDatabase firebaseDatabase;

    public OtherUsersAdapter(Context context, ArrayList<User> usersList) {
        this.context = context;
        this.usersList = usersList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_user, parent, false);

        //init firebase:
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();

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
        holder.txtTimeWork.setText("Work: " + usersList.get(position).getTimeWork() + " seconds");

        //TODO: Set icon for rank:
        try {
            if (usersList.get(position).getRank().equals("Newbie")) {
                Picasso.get().load(R.drawable.rank_1_image).into(holder.imgRank);
            } else if (usersList.get(position).getRank().equals("Time Controller")) {
                Picasso.get().load(R.drawable.rank_2_image).into(holder.imgRank);
            } else if (usersList.get(position).getRank().equals("Time Master")) {
                Picasso.get().load(R.drawable.rank_3_image).into(holder.imgRank);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.btnAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference databaseReference = firebaseDatabase.getReference("Users");
                if (currentUser != null) {
                    databaseReference.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            User user = snapshot.getValue(User.class);
                            if (user != null) {
                                ArrayList<String> listfriends = user.getFriends();
                                if (listfriends == null)
                                    listfriends = new ArrayList<>();
                                listfriends.add(usersList.get(position).getUserID());
                                HashMap<String, Object> updateFriends = new HashMap<>();
                                updateFriends.put("friends", listfriends);
                                databaseReference.child(currentUser.getUid()).updateChildren(updateFriends);
                                Toast.makeText(context, "Add Friend: Successful", Toast.LENGTH_SHORT).show();

                                //Reset SearchUsersActivity:
                                Intent intent = new Intent(context, SearchUsersActivity.class);
                                context.startActivity(intent);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });

        DatabaseReference databaseReference = firebaseDatabase.getReference("Users");
        if (currentUser != null) {
            databaseReference.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                        ArrayList<String> friendLists = user.getFriends();
                        if (friendLists != null) {
                            for (String userID : friendLists) {
                                if (usersList.get(position).getUserID().equals(userID)) {
                                    holder.btnAddFriend.setVisibility(View.GONE);
                                    break;
                                }
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgAvatar;
        private TextView txtName, txtTimeWork;
        private ImageView imgRank;
        private Button btnAddFriend;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgAvatar = itemView.findViewById(R.id.imgAvatar);
            txtName = itemView.findViewById(R.id.txtName);
            txtTimeWork = itemView.findViewById(R.id.txtTimeWork);
            imgRank = itemView.findViewById(R.id.imgRank);
            btnAddFriend = itemView.findViewById(R.id.btnAddFriend);
        }
    }
}
