package com.example.blogapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.blogapp.mainFragments.homeFragment;
import com.example.blogapp.mainFragments.mypostsFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

public class MainActivity extends AppCompatActivity {

    private Toolbar mainTb;
    private FloatingActionButton btn_addpost;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String CurrentUserID;

    private BottomNavigationView BotNav;
    private homeFragment homeFrag;
    private mypostsFragment mypostFrag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainTb = (Toolbar) findViewById(R.id.main_tb);
        setSupportActionBar(mainTb);
        getSupportActionBar().setTitle("Tugas Akhir PAM");

        homeFrag = new homeFragment();
        mypostFrag = new mypostsFragment();
        BotNav = (BottomNavigationView) findViewById(R.id.BottomNav_main);

        BotNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
                switch (item.getItemId()){

                    case R.id.bottomnav_home:
                        replaceFrag(homeFrag);
                        return true;

                    case R.id.bottomnav_write:
                        sendto(PostForm.class, 0);
                        return true;

                    case R.id.bottomnav_account:
                        replaceFrag(mypostFrag);
                        return true;

                    default:
                        return false;
                }
            }
        });

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser== null){
            sendto(Login.class, 1);
        } else {

            CurrentUserID = currentUser.getUid();

            db.collection("Users").document(CurrentUserID).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()){

                                if (!task.getResult().exists()){

                                    sendto(Profile.class, 1);

                                } else {
                                    replaceFrag(homeFrag);
                                }
                            }
                        }
                    });

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){

            case R.id.logout_item:
                logout();
                return true;

            case R.id.profile_item:
                sendto(Profile.class, 0);
                return true;

            default:
                return false;

        }
    }

    private void logout(){
        mAuth.signOut();
        sendto(Login.class, 1);
    }

    private void sendto(Class X, int y){
        Intent i = new Intent(MainActivity.this, X);
        startActivity(i);
        if (y == 1){
            finish();
        }
    }

    private void replaceFrag(Fragment Frag){

        FragmentTransaction FT = getSupportFragmentManager().beginTransaction();
        FT.replace(R.id.frame_main, Frag);
        FT.commit();

    }
}