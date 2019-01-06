package com.example.sparsh.blogging_app;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth mAuth;
    private FloatingActionButton add_post;
    private android.support.v7.widget.Toolbar toolbar ;
    private FirebaseFirestore firestore;
    private String user_id;
    private HomeFrag homeFrag;
    private NotifiFrag notifiFrag;
    private AccountFrag accountFrag;
    BottomNavigationView navigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Blogging App");
        add_post = findViewById(R.id.add_post);
        homeFrag = new HomeFrag();
        accountFrag = new AccountFrag();
        notifiFrag = new NotifiFrag();

        if (mAuth!=null)
        changeFragment(homeFrag);

        navigationView = findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId())
                {
                    case R.id.nav_account:
                        changeFragment(accountFrag);
                        return true;
                    case R.id.nav_home:
                        changeFragment(homeFrag);
                        return true;
                    case R.id.nav_notification:
                        changeFragment(notifiFrag);
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser()==null)
                {
                   loginPage();
                }
                else
                {
                    mAuth = firebaseAuth;
                    firestore = FirebaseFirestore.getInstance();
                    user_id = mAuth.getCurrentUser().getUid();
                   // System.out.println(user_id+" "+firestore);
                    firestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.isSuccessful())
                        {
                          if (task.getResult().exists())
                          {
                              Toast.makeText(MainActivity.this, "hello", Toast.LENGTH_SHORT).show();
                          }
                          else
                          {
                              startActivity(new Intent(MainActivity.this,Profile.class));
                          }
                        }
                        else
                        {
                            Toast.makeText(MainActivity.this, "Firestore Error", Toast.LENGTH_SHORT).show();
                        }
                        }
                    });
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.account_logout:
                logout();
                loginPage();
                break;


            case R.id.account_setting:
                startActivity(new Intent(MainActivity.this,Profile.class));
                break;
            case R.id.search:
                break;

            default:
                return false;

        }
        return true;
    }

    private void logout()
    {
        mAuth.signOut();
    }

    private void loginPage() {
        startActivity(new Intent(MainActivity.this,Login.class));
        finish();
    }

    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.add_post)
        {
            startActivity(new Intent(MainActivity.this,NewPost.class));
        }
    }

    public void changeFragment(Fragment fragment)
    {
        FragmentTransaction transaction  = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.Main_frame,fragment);
        transaction.commit();
    }
}
