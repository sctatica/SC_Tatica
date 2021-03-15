package com.sang.userlogin_registration;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Menu;
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

    //TODO: Get image:
    //Path where images of user avatar and cover will be stored
    private String storagePath = "Users_Avatar_Cover_Images/";

    //Uri of picked image:
    private Uri image_uri;

    //Permission constants:
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    private static final int IMAGE_PICK_GALLERY_CODE = 300;
    private static final int IMAGE_PICK_CAMERA_CODE = 400;

    //Arrays of Permissions to be Requested:
    private String cameraPermissions[];
    private String storagePermissions[];

    //For checking profile or cover photo:
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

        // init Arrays of permissions:
        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

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
                    txtShowTimeWork.setText(timeWork);
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
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditProfileDialog();
            }
        });
    }

    private void showEditProfileDialog() {
        //Options to show in dialog:
        String options[] = {"Edit Profile Picture", "Edit Cover Photo", "Edit Name", "Edit Phone", "Edit Birthday"};
        //Alert Dialog:
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this)
                .setTitle("Choose Action")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //handle dialog items clicked:
                        switch (which) {
                            case 0:
                                progressDialog.setMessage("Updating Profile Picture");
                                image_key = "image";
                                showImagePicDialog();
                                break;
                            case 1:
                                progressDialog.setMessage("Updating Cover Photo");
                                image_key = "coverImage";
                                showImagePicDialog();
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
                        }
                        else {
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

    private void showImagePicDialog() {
        //show dialog containing options Camera and Gallery to pick the image
        String options[] = {"Camera", "Gallery"};

        //Alert Dialog:
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this)
                .setTitle("Pick Image From")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //handle dialog items clicked:
                        switch (which) {
                            case 0:
                                //Camera clicked
                                if (!checkCameraPermission()) {
                                    requestCameraPermission();
                                } else {
                                    pickFromCamera();
                                }
                                break;
                            case 1:
                                //Gallery clicked
                                if (!checkStoragePermission()) {
                                    requestStoragePermission();
                                } else {
                                    pickFromGallery();
                                }
                                break;
                            default:
                                break;
                        }
                    }
                });
        builder.create().show();
    }

    private boolean checkStoragePermission(){
        //check if storage permission is enable or not
        boolean result = ContextCompat.checkSelfPermission(ProfileActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestStoragePermission(){
        //request runtime storage permission:
        requestPermissions(storagePermissions, STORAGE_REQUEST_CODE);
    }

    private boolean checkCameraPermission(){
        //check if storage permission is enable or not
        boolean result = ContextCompat.checkSelfPermission(ProfileActivity.this, Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(ProfileActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    private void requestCameraPermission(){
        //request runtime storage permission:
        requestPermissions(cameraPermissions, CAMERA_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //This method called when user press Allow or Deny from permission request dialog
        //here we will handle permission cases (allow && denied)
        switch (requestCode) {
            case CAMERA_REQUEST_CODE: {
                //picking from camera, first check if camera and storage permissions allowed or not:
                if (grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && writeStorageAccepted) {
                        //permission enabled
                        pickFromCamera();
                    } else {
                        //permission denied:
                        Toast.makeText(ProfileActivity.this, "Please enable camera & storage permission", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            }
            case STORAGE_REQUEST_CODE: {
                //picking from gallery, first check if storage permissions allowed or not:
                if (grantResults.length > 0) {
                    boolean writeStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (writeStorageAccepted) {
                        //permission enabled
                        pickFromGallery();
                    } else {
                        //permission denied:
                        Toast.makeText(ProfileActivity.this, "Please enable storage permission", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            }
            default:
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void pickFromCamera() {
        //Intent of picking image from device:
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Temp Pic");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Temp Description");

        //Put image uri:
        image_uri = ProfileActivity.this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        //Intent to start camera:
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(cameraIntent, IMAGE_PICK_CAMERA_CODE);
    }

    private void pickFromGallery() {
        //Pick from gallery:
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, IMAGE_PICK_GALLERY_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //This method will be called after picking image from Camera or Gallery:
        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_PICK_GALLERY_CODE) {
                //image is picked from gallery, get uri of image
                image_uri = data.getData();
                uploadImageToStorage(image_uri);
            }
            if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                //image is picked from camera, get uri of image
                uploadImageToStorage(image_uri);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
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
                        while (!uriTask.isSuccessful());
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
                }). addOnFailureListener(new OnFailureListener() {
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