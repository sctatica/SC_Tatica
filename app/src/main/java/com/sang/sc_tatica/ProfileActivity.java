package com.sang.sc_tatica;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;

    //Views:
    private ImageView image_cover, image_avatar;
    private TextView txtShowName, txtShowEmail, txtShowPhone,
            txtShowBirthday, txtShowTimeWork, txtShowRank;
    private FloatingActionButton btnEditProfile;

    //Firebase:
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    //Firebase storage:
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    //Progress dialog:
    private ProgressDialog progressDialog;

    //TODO: init variable to get image:
    //Path where images of user avatar and cover will be stored
    private String storagePath = "Users_Avatar_Cover_Images/";

    //Permission constants:
    private static final int PHOTO_PERMISSION_CODE = 101;

    //Arrays of permissions to be requested:
    private String PhotoPermissions[];

    //For checking avatar photo or cover photo:
    private String image_key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // init toolbar:
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Profile");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        // init views:
        initViews();

        // init progress dialog:
        progressDialog = new ProgressDialog(ProfileActivity.this);

        // init arrays of permissions:
        PhotoPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};

        // init firebase:
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        // update Profile Information Activity:
        Query query = databaseReference.orderByChild("userID").equalTo(user.getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //check until required data get:
                for (DataSnapshot ds : snapshot.getChildren()) {
                    //Get data from the user:
                    String image = String.valueOf(ds.child("image").getValue());
                    String cover = String.valueOf(ds.child("coverImage").getValue());
                    String name = String.valueOf(ds.child("name").getValue());
                    String email = String.valueOf(ds.child("email").getValue());
                    String phone = String.valueOf(ds.child("phone").getValue());
                    String birthday = String.valueOf(ds.child("birthday").getValue());
                    String timeWork = String.valueOf(ds.child("timeWork").getValue());
                    String rank = String.valueOf(ds.child("rank").getValue());

                    //set data:
                    txtShowName.setText(name);
                    txtShowEmail.setText(email);
                    txtShowPhone.setText(phone);
                    txtShowBirthday.setText(birthday);
                    txtShowTimeWork.setText(String.valueOf(timeWork + " seconds"));
                    txtShowRank.setText(rank);

                    try {
                        //if image is received then set
                        Picasso.get().load(image).into(image_avatar);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        //if image is received then set
                        Picasso.get().load(cover).into(image_cover);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditProfileDialog();
            }
        });
    }

    private void showPersonalInfoUpdate(String key) {
        //Set up layout of Dialog:
        LinearLayout linearLayout = new LinearLayout(ProfileActivity.this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(10, 10, 10, 10);

        //Add edit text to the layout:
        EditText editText = new EditText(ProfileActivity.this);
        editText.setHint("Enter " + key);
        linearLayout.addView(editText);

        //Set up Dialog:
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
        builder.setTitle("Update " + key)
                .setView(linearLayout)
                .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String value = String.valueOf(editText.getText());

                        if (!TextUtils.isEmpty(value)) {
                            //Update to firebase user:
                            progressDialog.show();
                            HashMap<String, Object> result = new HashMap<>();
                            result.put(key, value);
                            databaseReference.child(user.getUid()).updateChildren(result)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            progressDialog.dismiss();
                                            Toast.makeText(ProfileActivity.this, "Updated...", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            Toast.makeText(ProfileActivity.this, "Please input " + key, Toast.LENGTH_SHORT).show();
                        }
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        //Create and show dialog
        builder.create().show();
    }

    private void showEditProfileDialog() {
        //Options to show in dialog:
        String options[] = {"Edit Avatar Photo", "Edit Cover Photo", "Edit Name", "Edit Phone", "Edit Birthday"};
        //Alert Dialog:
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this)
                .setTitle("Choose Action")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //handle dialog items clicked:
                        switch (which) {
                            case 0:
                                progressDialog.setMessage("Updating Avatar Photo");
                                image_key = "image";
                                getImage();
                                break;
                            case 1:
                                progressDialog.setMessage("Updating Cover Photo");
                                image_key = "coverImage";
                                getImage();
                                break;
                            case 2:
                                progressDialog.setMessage("Updating Name");
                                showPersonalInfoUpdate("name");
                                break;
                            case 3:
                                progressDialog.setMessage("Updating Phone");
                                showPersonalInfoUpdate("phone");
                                break;
                            case 4:
                                progressDialog.setMessage("Updating Birthday");
                                showPersonalInfoUpdate("birthday");
                                break;
                            default:
                                break;
                        }
                    }
                });
        builder.create().show();
    }

    private void startActivity() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this);
    }

    private boolean isCameraPermission() {
        return ActivityCompat.checkSelfPermission(ProfileActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private boolean isStoragePermission() {
        return ActivityCompat.checkSelfPermission(ProfileActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void getImage() {
        boolean isCamera = isCameraPermission();
        boolean isStorage = isStoragePermission();
        if (isCamera && isStorage) {
            startActivity();
        } else if (!isCamera || !isStorage) {
            ActivityCompat.requestPermissions(this, PhotoPermissions, PHOTO_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PHOTO_PERMISSION_CODE) {
            if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED) && (grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
                startActivity();
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                uploadImageToStorage(resultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private void uploadImageToStorage(final Uri uri) {
        //show the progress:
        progressDialog.show();
        // Store image to firebase storage
        String filePathAndName = storagePath + "" + image_key + "_" + user.getUid();
        StorageReference storageReference2nd = storageReference.child(filePathAndName);
        storageReference2nd.putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        //img is uploaded to storage, now get it's url and store in user's database
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful()) ;
                        Uri downloadUri = uriTask.getResult();
                        //check if image is uploaded or not and uri is received or not:
                        if (uriTask.isSuccessful()) {
                            HashMap<String, Object> results = new HashMap<>();
                            results.put(image_key, downloadUri.toString());
                            databaseReference.child(user.getUid()).updateChildren(results)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            progressDialog.dismiss();
                                            Toast.makeText(ProfileActivity.this, "Image Updated...", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    Toast.makeText(ProfileActivity.this, "Error updating image...", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(ProfileActivity.this, "Some error occurred", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //there were some error(s), get and show error message, dismiss progress dialog
                progressDialog.dismiss();
                Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initViews() {
        image_cover = findViewById(R.id.image_cover);
        image_avatar = findViewById(R.id.image_avatar);
        txtShowName = findViewById(R.id.txtShowName);
        txtShowEmail = findViewById(R.id.txtShowEmail);
        txtShowPhone = findViewById(R.id.txtShowPhone);
        txtShowBirthday = findViewById(R.id.txtShowBirthday);
        txtShowTimeWork = findViewById(R.id.txtShowTimeWork);
        txtShowRank = findViewById(R.id.txtShowRank);
        btnEditProfile = findViewById(R.id.btnEditProfile);
    }
}