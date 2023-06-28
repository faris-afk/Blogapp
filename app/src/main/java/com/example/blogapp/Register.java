package com.example.blogapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
import com.google.firebase.auth.FirebaseUser;

public class Register extends AppCompatActivity {

    private EditText in_email, in_password;
    private Button btn_login, btn_register;
    private ProgressBar registerProgress;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        in_email = (EditText) findViewById(R.id.reg_email);
        in_password = (EditText) findViewById(R.id.reg_pass);
        btn_login = (Button) findViewById(R.id.reg_btn_login);
        btn_register = (Button) findViewById(R.id.reg_btn_reg);
        registerProgress = (ProgressBar) findViewById(R.id.regis_progressBar);

        mAuth = FirebaseAuth.getInstance();

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = in_email.getText().toString().trim();
                String password = in_password.getText().toString().trim();

                if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
                    registerProgress.setVisibility(View.VISIBLE);

                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull @org.jetbrains.annotations.NotNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                sendto(Profile.class);

                            } else {
                                String err = task.getException().getMessage();
                                Toast.makeText(Register.this, "Error : " + err, Toast.LENGTH_LONG).show();
                            }

                            registerProgress.setVisibility(View.INVISIBLE);
                        }
                    });
                }
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendto(Login.class);
            }
        });
    }

    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null){
            sendto(MainActivity.class);
        }
    }

    private void sendto(Class X){
        Intent i = new Intent(Register.this, X);
        startActivity(i);
        finish();
    }
}