package com.example.sparsh.blogging_app;

import android.content.Intent;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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

public class Login extends AppCompatActivity implements View.OnClickListener {

    private EditText uname,upass ;
    private Button login,signup;
    private ProgressBar loginBar;

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        uname = findViewById(R.id.lname);
        upass = findViewById(R.id.lpass);
        login  = findViewById(R.id.login);
        signup = findViewById(R.id.new1);
        loginBar = findViewById(R.id.loginProg);
    }

    @Override
    public void onClick(View v) {

        if (v.getId()==R.id.login)
        {
        String email =  uname.getText().toString();
        String pass = upass.getText().toString();

        if (!TextUtils.isEmpty(email)&& !TextUtils.isEmpty(pass))
        {
            loginBar.setVisibility(View.VISIBLE);

            mAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isComplete())
                    {
                        Toast.makeText(Login.this, "Signn", Toast.LENGTH_SHORT).show();
                      startActivity(new Intent(Login.this,MainActivity.class));
                      finish();
                    }
                    else
                    {
                        String excep = task.getException().toString();
                        Toast.makeText(Login.this, excep, Toast.LENGTH_LONG).show();
                    }

                }
            });

            loginBar.setVisibility(View.INVISIBLE);
        }
        else
        {
            Toast.makeText(this, "Email or Password Invalid", Toast.LENGTH_SHORT).show();
        }
        }
        else if (v.getId()==R.id.new1)
        {
        startActivity(new Intent(Login.this,SignUp.class));
        }

    }

}
