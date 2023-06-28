package com.example.blogapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class PostForm extends AppCompatActivity {

    private EditText post_title, post_desc;
    private Button btn_publish;
    private ProgressBar PublishProgress;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String currentuserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_form);

        post_title = (EditText) findViewById(R.id.in_judul);
        post_desc = (EditText) findViewById(R.id.in_isi);
        btn_publish = (Button) findViewById(R.id.btn_publish);
        PublishProgress = (ProgressBar) findViewById(R.id.form_progressBar);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentuserID = mAuth.getCurrentUser().getUid();

        btn_publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String title = post_title.getText().toString().trim();
                String Desc = post_desc.getText().toString().trim();

                if (!(TextUtils.isEmpty(title) && TextUtils.isEmpty(Desc))) {

                    PublishProgress.setVisibility(View.VISIBLE);

                    Date date = new Date(System.currentTimeMillis());
                    com.google.firebase.Timestamp tm = new Timestamp(date);

                    Map<String, Object> data_posts = new HashMap<>();
                    data_posts.put("judul", title);
                    data_posts.put("deskripsi", Desc);
                    data_posts.put("user_id", currentuserID);
                    data_posts.put("timestamp", tm);

                    db.collection("Posts").add(data_posts)
                            .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                @Override
                                public void onComplete(@NonNull @NotNull Task<DocumentReference> task) {
                                    if (task.isSuccessful()) {

                                        Toast.makeText(PostForm.this, "Post berhasil ditambahkan", Toast.LENGTH_LONG).show();
                                        sendto(MainActivity.class);
                                        PublishProgress.setVisibility(View.INVISIBLE);

                                    } else {

                                        String err = task.getException().getMessage();
                                        Toast.makeText(PostForm.this, "Error : " + err, Toast.LENGTH_LONG).show();
                                        PublishProgress.setVisibility(View.INVISIBLE);

                                    }
                                }
                            });
                }
            }
        });
    }
    private void sendto(Class X){
        Intent i = new Intent(PostForm.this, X);
        startActivity(i);
        finish();
    }
}