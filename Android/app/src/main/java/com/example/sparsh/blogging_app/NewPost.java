package com.example.sparsh.blogging_app;

import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ServerTimestamp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NewPost extends AppCompatActivity implements View.OnClickListener{

    private Toolbar toolbar;
    private Button save_post ;
    private ImageView blog_img;
    private EditText content_des ;
    private Uri newPost_uri=null;
    private FirebaseFirestore firestore;
    private StorageReference storage ;
    private String user_id;
    private FirebaseAuth mAuth;
    private int width;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);
        toolbar = findViewById(R.id.newPost_toolbar);
        setActionBar(toolbar);
        getActionBar().setTitle("New Post");
        getActionBar().setDisplayHomeAsUpEnabled(true);

        // display the current size()
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
         width = size.x;

        save_post = findViewById(R.id.save_post);
        content_des = findViewById(R.id.content_des);
        blog_img = findViewById(R.id.blog_image);
        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        user_id = mAuth.getCurrentUser().getUid();
    }

    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.blog_image)
        {
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setMinCropResultSize(512,512)
                    .setAspectRatio(1,1)
                    .setRequestedSize(width,200)
                    .start(NewPost.this);
        }
        else if (v.getId()==R.id.save_post)
        {
            final String content = content_des.getText().toString();
            if(!TextUtils.isEmpty(content)&&newPost_uri!=null)
            {
                String randomName = UUID.randomUUID().toString();
               StorageReference filePath = storage.child("Post_Images").child(randomName+".jpg");

               filePath.putFile(newPost_uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                   @Override
                   public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                       if (task.isComplete())
                       {
                           Map<String,Object> map = new HashMap<>();

                           String downLoadUri = task.getResult().getDownloadUrl().toString();

                           map.put("content",content);
                           map.put("img_uri",downLoadUri);
                           map.put("user_id",user_id);
                           map.put("likes",0);

                           DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
                           LocalDateTime now = LocalDateTime.now();
                           map.put("time",dtf.format(now));

                           firestore.collection("Posts_Details").add(map).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                               @Override
                               public void onComplete(@NonNull Task<DocumentReference> task) {
                                   if (task.isComplete())
                                   {
                                       Toast.makeText(NewPost.this, "Post Uploaded Successfully", Toast.LENGTH_SHORT).show();
                                       startActivity(new Intent(NewPost.this,MainActivity.class));
                                       finish();
                                   }
                                   else
                                   {
                                       Toast.makeText(NewPost.this, "Firestore Error "+task.getException().toString(), Toast.LENGTH_SHORT).show();
                                   }

                               }
                           });

                       }
                       else
                       {
                           Toast.makeText(NewPost.this,"Storage Error "+ task.getException().toString(), Toast.LENGTH_SHORT).show();
                       }
                   }
               });
            }
            else
            {
                Toast.makeText(this, "Invalid Image Or Content", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                newPost_uri = result.getUri();
                blog_img.setImageURI(newPost_uri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
