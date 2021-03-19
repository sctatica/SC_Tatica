package com.sang.userlogin_registration;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

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

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class EventDetailDialog extends DialogFragment {
    public static final String EVENT_KEY = "Event details";
    private CircleImageView imgEvent;
    private TextView txtName, txtAvailable, txtStartDate, txtDueDate, txtDesc;
    private Button btnLeave, btnCancel, btnJoin;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_event_details, null);
        initViews(view);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setView(view)
                .setTitle("Event Details");

        Bundle bundle = getArguments();
        if (bundle != null) {
            Event event = bundle.getParcelable(EVENT_KEY);
            if (event != null) {
                txtName.setText("Event's name: " + event.getName());
                txtStartDate.setText("Start from: " + event.getStartTime() + " " + event.getStartDate());
                txtDueDate.setText("Due to: " +event.getDueTime() + " " + event.getDueDate());
                txtAvailable.setText("Available Slot: " + String.valueOf(event.getUsersList().size()) + "/" + event.getLimitRegister());
                txtDesc.setText("Description: \n" + event.getDescription());
            }

            btnJoin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    FirebaseUser currentUser = mAuth.getCurrentUser();
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference reference = database.getReference("Users");
                    reference.child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            //Get current user:
                            User user = snapshot.getValue(User.class);
                            user.setImage("");
                            user.setCoverImage("");
                            user.setBirthday("");
                            user.setPhone("");
                            user.setFriends(null);
                            //Set update event:
                            ArrayList<User> usersList = event.getUsersList();
                            boolean flag = true;
                            for (User u : usersList) {
                                if (u.getUserID().equals(user.getUserID())) {
                                    flag = false;
                                    break;
                                }
                            }
                            if (flag) {
                                usersList.add(user);
                                if (usersList.size() == Integer.parseInt(event.getLimitRegister())) {
                                    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                                    firestore.collection("Events").document(event.getEventID()).set(event).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(getActivity(), "Register: Successful", Toast.LENGTH_LONG).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getActivity(), "Register: Fail", Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            });
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
            btnLeave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
        }

        return  builder.create();
    }

    private void initViews(View view) {
        imgEvent = view.findViewById(R.id.imgEvent);
        txtName = view.findViewById(R.id.txtName);
        txtAvailable = view.findViewById(R.id.txtAvailable);
        txtStartDate = view.findViewById(R.id.txtStartDate);
        txtDueDate = view.findViewById(R.id.txtDueDate);
        txtDesc = view.findViewById(R.id.txtDesc);
        btnLeave = view.findViewById(R.id.btnLeave);
        btnCancel = view.findViewById(R.id.btnCancel);
        btnJoin = view.findViewById(R.id.btnJoin);
    }


}
