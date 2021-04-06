package com.sang.sc_tatica;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class EventDetailDialog extends DialogFragment {

    public static final String EVENT_KEY = "Event details";

    private CircleImageView imgEvent;
    private TextView txtName, txtShowOrganizers, txtShowLocation, txtShowDesc,
            txtShowStartDate, txtShowDueDate, txtShowAvailable;
    private Button btnJoin, btnDonate;
    private ImageView extImage;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_event_details, null);
        initViews(view);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()).setView(view);

        Bundle bundle = getArguments();
        if (bundle != null) {
            Event event = bundle.getParcelable(EVENT_KEY);
            if (event != null) {
                try {
                    //if image is received then set
                    Picasso.get().load(event.getImage()).into(imgEvent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                txtName.setText(event.getName());
                txtShowOrganizers.setText(event.getOrganizers());
                txtShowLocation.setText(event.getLocation());
                txtShowDesc.setText(event.getDescription());
                txtShowStartDate.setText(event.getStartTime() + " - " + event.getStartDate());
                txtShowDueDate.setText(event.getDueTime() + " - " + event.getDueDate());
                txtShowAvailable.setText(String.valueOf(event.getUsersList().size()) + "/" + event.getLimitRegister());

                if (event.getIsDonate().equals("true")) {
                    btnDonate.setVisibility(View.VISIBLE);
                    btnDonate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            DonateDialog dialog = new DonateDialog();
                            Bundle bundle = new Bundle();
                            bundle.putParcelable(EVENT_KEY, event);
                            dialog.setArguments(bundle);
                            dialog.show(getActivity().getSupportFragmentManager(), "Event Donate Dialog");
                        }
                    });
                } else {
                    btnDonate.setVisibility(View.GONE);
                }
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
                            if (user != null) {
                                RegisterUser newRegisterUser = new RegisterUser(user.getEmail(), user.getName(), user.getRank());
                                //Set update event:
                                ArrayList<RegisterUser> usersList = event.getUsersList();
                                boolean flag = true;
                                for (RegisterUser u : usersList) {
                                    if (u.getUserEmail().equals(user.getEmail())) {
                                        flag = false;
                                        Toast.makeText(getActivity(), "You have registered to this event", Toast.LENGTH_LONG).show();
                                        break;
                                    }
                                }
                                if (flag) {
                                    usersList.add(newRegisterUser);
                                    event.setUsersList(usersList);
                                    if (!event.getLimitRegister().equals("unlimited")) {
                                        if (usersList.size() <= Integer.parseInt(event.getLimitRegister())) {
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
                                    } else {
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
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            });
            extImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
        }
        return builder.create();
    }

    private void initViews(View view) {
        imgEvent = view.findViewById(R.id.imgEvent);
        txtName = view.findViewById(R.id.txtName);
        txtShowOrganizers = view.findViewById(R.id.txtShowOrganizers);
        txtShowLocation = view.findViewById(R.id.txtShowLocation);
        txtShowAvailable = view.findViewById(R.id.txtShowAvailable);
        txtShowStartDate = view.findViewById(R.id.txtShowStartDate);
        txtShowDueDate = view.findViewById(R.id.txtShowDueDate);
        txtShowDesc = view.findViewById(R.id.txtShowDesc);
        btnDonate = view.findViewById(R.id.btnDonate);
        btnJoin = view.findViewById(R.id.btnJoin);
        extImage = view.findViewById(R.id.extImage);
    }
}
