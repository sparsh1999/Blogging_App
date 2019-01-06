package com.example.sparsh.blogging_app;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile extends AppCompatActivity implements View.OnClickListener{

    private Toolbar toolbar;
    private CircleImageView img ;
    private Uri imgUri;
    private EditText name ;
    private Button save ;
    private StorageReference mStorageRef;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    private FirebaseFirestore firestore;
    private String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        toolbar = findViewById(R.id.toolbar5);
        setActionBar(toolbar);
        getActionBar().setTitle("Profile Setup");
        img = findViewById(R.id.profilePic);
        save = findViewById(R.id.profileSetting);
        name = findViewById(R.id.uname);
        mAuth = FirebaseAuth.getInstance();
        user_id = mAuth.getCurrentUser().getUid();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        progressBar = findViewById(R.id.progressBar2);
        firestore = FirebaseFirestore.getInstance();


        if (user_id!=null) {
            firestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isComplete()) {
                        if (task.getResult().exists()) {
                        String name1 = task.getResult().getString("name");
                        String image1 = task.getResult().getString("image");
                        name.setText(name1);
                        Glide.with(Profile.this).load(image1).into(img);

                        } else {
                            Toast.makeText(Profile.this, "Media Doesn't Exist", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(Profile.this, "Firestore Error" + task.getException().toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @Override
    public void onClick(View v) {

        if (v.getId()==R.id.profilePic)
        {
            if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(Profile.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(Profile.this, "Already permission Granted", Toast.LENGTH_SHORT).show();
                    ActivityCompat.requestPermissions(Profile.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 10);
                } else {
                    getImagePicker();
                }
            }
            else
            {
                getImagePicker();
            }
        }
        else if (v.getId()==R.id.profileSetting)
        {

            final String uname = name.getText().toString();
            progressBar.setVisibility(View.VISIBLE);
            Toast.makeText(this, uname+" "+imgUri, Toast.LENGTH_SHORT).show();
            if (!TextUtils.isEmpty(uname)&&imgUri!=null)
            {
                StorageReference image_Path = mStorageRef.child("Profile_Pics").child(user_id+".jpg");
                image_Path.putFile(imgUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isComplete())
                        {
                        String imgDownload = task.getResult().getDownloadUrl().toString();

                        Map<String,String> user_Map = new HashMap<>();
                        user_Map.put("name",uname);
                        user_Map.put("image",imgDownload);

                        firestore.collection("Users").document(user_id).set(user_Map).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isComplete())
                                {
                                    startActivity(new Intent(Profile.this,MainActivity.class));
                                    finish();
                                }
                                else
                                {
                                    String excep = task.getException().toString();
                                    Toast.makeText(Profile.this, "Firebase Error " +excep, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        progressBar.setVisibility(View.INVISIBLE);
                        }
                        else
                        {
                         String exception = task.getException().toString();
                            Toast.makeText(Profile.this,"Image Error "+ exception, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                progressBar.setVisibility(View.INVISIBLE);

            }
            else
            {
                Toast.makeText(Profile.this, "Please Fill All Details", Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imgUri = result.getUri();
                img.setImageURI(imgUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void getImagePicker() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1,1)
                .start(Profile.this);
    }

}
