package com.example.sparsh.blogging_app;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.w3c.dom.Text;

public class SignUp extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth mAuth;
    private Button signup , existingUser;
    private EditText sname , spass;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mAuth = FirebaseAuth.getInstance();
        signup = findViewById(R.id.s_signUp);
        existingUser = findViewById(R.id.exist_user);
        sname = findViewById(R.id.sname);
        spass = findViewById(R.id.spass);
        progressBar = findViewById(R.id.sloginProg);
    }

    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.s_signUp)
        {
            String email = sname.getText().toString();
            String password = spass.getText().toString();

            if (!TextUtils.isEmpty(email)&&!TextUtils.isEmpty(password))
            {
                progressBar.setVisibility(View.VISIBLE);
               mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                   @Override
                   public void onComplete(@NonNull Task<AuthResult> task) {
                       if (task.isComplete())
                       {
                           startActivity(new Intent(SignUp.this,Profile.class));
                           finish();
                       }
                       else {
                           String exception = task.getException().toString();
                           Toast.makeText(SignUp.this, exception, Toast.LENGTH_SHORT).show();
                       }
                   }
               });
            }
            else
            {
                Toast.makeText(SignUp.this, "Invalid Email Or Password", Toast.LENGTH_SHORT).show();
            }
            progressBar.setVisibility(View.INVISIBLE);
        }
        else if (v.getId()==R.id.exist_user)
        {
            startActivity(new Intent(SignUp.this,Login.class));
            finish();
        }
    }
}
