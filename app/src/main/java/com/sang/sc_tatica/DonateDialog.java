package com.sang.sc_tatica;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.lang.ref.Reference;
import java.util.ArrayList;
import java.util.HashMap;

import static com.sang.sc_tatica.EventDetailDialog.EVENT_KEY;

public class DonateDialog extends DialogFragment {

    private TextView txtLink, txtConvertMoney;
    private EditText edtConvertTime;
    private Button btnDonate;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private FirebaseDatabase database;
    private FirebaseFirestore firestore;

    private double TimeConverted;
    private double MoneyConverted;

    public interface EventObject {
        void onCallBack(Event event);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_donate, null);
        initViews(view);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()).setView(view);

        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        firestore = FirebaseFirestore.getInstance();

        //TODO: Get user max time:
        database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("Users");
        reference.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (user != null) {
                    edtConvertTime.setHint(user.getTimeWork());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //TODO: Set change money due to time:
        edtConvertTime.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!edtConvertTime.getText().toString().equals("")) {
                    TimeConverted = Double.parseDouble(edtConvertTime.getText().toString());
                    MoneyConverted = Math.round((TimeConverted / 100) * 100.0) / 100.0;
                    txtConvertMoney.setText(String.valueOf(MoneyConverted));
                } else {
                    txtConvertMoney.setText("0");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        Bundle bundle = getArguments();
        if (bundle != null) {
            Event event = bundle.getParcelable(EVENT_KEY);
            if (event != null) {
                btnDonate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (edtConvertTime.getText().toString().equals("")) {
                            Toast.makeText(getActivity(), "Please fulfil your work time to donate", Toast.LENGTH_SHORT).show();
                        } else {
                            //TODO: TimeDonate and MoneyDonate:
                            getEvent(event.getEventID(), new EventObject() {
                                @Override
                                public void onCallBack(Event current_event) {
                                    double CurrentTimeWork = Double.parseDouble(edtConvertTime.getHint().toString());
                                    TimeConverted = Double.parseDouble(edtConvertTime.getText().toString());
                                    if (TimeConverted <= CurrentTimeWork) {
                                        //TODO: Update timeDonate and moneyDonate to Event:
                                        HashMap<String, Object> update_event = new HashMap<>();
                                        update_event.put("timeDonate", String.valueOf(TimeConverted + Double.parseDouble(current_event.getTimeDonate())));
                                        update_event.put("moneyDonate", String.valueOf(MoneyConverted + Double.parseDouble(current_event.getMoneyDonate())));
                                        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                                        firestore.collection("Events").document(current_event.getEventID())
                                                .update(update_event).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Toast.makeText(getActivity(), "Thanks for your donation!", Toast.LENGTH_LONG).show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(getActivity(), "Oops, somethings wrong happened! Please try again!", Toast.LENGTH_LONG).show();
                                            }
                                        });

                                        //TODO: Update work time before donation of user:
                                        database = FirebaseDatabase.getInstance();
                                        DatabaseReference reference = database.getReference("Users");
                                        String remainTime = String.valueOf(Math.round((CurrentTimeWork - TimeConverted) * 100.0 / 100.0));
                                        HashMap<String, Object> updateTime = new HashMap<>();
                                        updateTime.put("timeWork", remainTime);
                                        reference.child(currentUser.getUid()).updateChildren(updateTime);

                                        //TODO: Update max time that user can continue donate:
                                        edtConvertTime.setText("");
                                        edtConvertTime.setHint(remainTime);
                                    } else {
                                        Toast.makeText(getActivity(), "The time donation should be less than the total work time", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }
                });
            }
        }
        return builder.create();
    }

    private void initViews(View view) {
        txtLink = view.findViewById(R.id.txtLink);
        txtConvertMoney = view.findViewById(R.id.txtConvertMoney);
        edtConvertTime = view.findViewById(R.id.edtConvertTime);
        btnDonate = view.findViewById(R.id.btnDonate);
    }

    private void getEvent(String eventID, EventObject eventObject) {
        firestore.collection("Events")
                .document(eventID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        if (documentSnapshot != null) {
                            Event event = documentSnapshot.toObject(Event.class);
                            eventObject.onCallBack(event);
                        }
                    }
                });
    }
}
