package com.example.blogapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile extends AppCompatActivity {

    private Toolbar profTb;
    private ImageView profile_img;
    private EditText profile_nama;
    private String user_id;
    private Button btnProfile;
    private StorageReference ref;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ProgressBar profileProgress;
    private final int PICK_IMAGE_REQUEST = 22;
    private Uri filePath;
    private boolean isChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        ref = FirebaseStorage.getInstance().getReference();
        db = FirebaseFirestore.getInstance();
        user_id = mAuth.getCurrentUser().getUid();

        profileProgress = (ProgressBar) findViewById(R.id.profile_progressBar);
        profile_nama = (EditText) findViewById(R.id.profile_nama);
        btnProfile = (Button) findViewById(R.id.btn_simpan);
        profTb = (Toolbar) findViewById(R.id.profile_tb);
        profile_img = (ImageView) findViewById(R.id.profile_image);
        setSupportActionBar(profTb);
        getSupportActionBar().setTitle("Ubah Profil");

        profileProgress.setVisibility(View.VISIBLE);
        db.collection("Users").document(user_id).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){

                    if (task.getResult().exists()){

                        String nama = task.getResult().getString("nama");
                        String foto = task.getResult().getString("foto");

                        filePath = Uri.parse(foto);
                        profile_nama.setText(nama);

                        RequestOptions PlaceholderReq = new RequestOptions();
                        PlaceholderReq.placeholder(R.drawable.resource_default);

                        Glide.with(Profile.this).setDefaultRequestOptions(PlaceholderReq).load(foto).into(profile_img);

                    }

                }else {

                    String err = task.getException().getMessage();
                    Toast.makeText(Profile.this, "Error : " + err, Toast.LENGTH_LONG).show();

                }
                profileProgress.setVisibility(View.INVISIBLE);
            }
        });

        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String nama = profile_nama.getText().toString().trim();

                if (isChanged) {

                    if (!TextUtils.isEmpty(nama) && filePath != null) {
                        profileProgress.setVisibility(View.VISIBLE);
                        user_id = mAuth.getCurrentUser().getUid();
                        StorageReference img_path = ref.child("profile_img").child(user_id + ".jpg");

                        img_path.putFile(filePath)
                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        final Task<Uri> firebaseUri = taskSnapshot.getStorage().getDownloadUrl();
                                        firebaseUri.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                SimpanData(uri, nama);
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception exception) {
                                                // Handle any errors
                                                String err = firebaseUri.getException().getMessage();
                                                Toast.makeText(Profile.this, "Error : " + err, Toast.LENGTH_LONG).show();
                                                profileProgress.setVisibility(View.INVISIBLE);
                                            }
                                        });

                                    }
                                });

                    }
                } else {
                    SimpanData(filePath, nama);
                }
            }
        });

        profile_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if (ContextCompat.checkSelfPermission(Profile.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions(Profile.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                    } else {
                        SelectImage();
                    }
                }
            }
        });
    }
    private void SelectImage()
    {

        // Defining Implicit Intent to mobile gallery
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "pilih foto..."), PICK_IMAGE_REQUEST);
    }// Override onActivityResult method
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // checking request code and result code
        // if request code is PICK_IMAGE_REQUEST and
        // resultCode is RESULT_OK
        // then set image in the image view
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            // Get the Uri of data
            filePath = data.getData();
            isChanged = true;
            try {

                // Setting image on image view using Bitmap
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                profile_img.setImageBitmap(bitmap);
            }

            catch (IOException e) {
                // Log the exception
                e.printStackTrace();
            }
        }
    }
    private void sendto(Class X){
        Intent i = new Intent(Profile.this, X);
        startActivity(i);
        finish();
    }
    private void SimpanData(Uri uri, String nama){

        if (uri==null){
            Toast.makeText(Profile.this, "upload gambar!", Toast.LENGTH_LONG).show();
        } else {
            final String download_result = uri.toString();
            // complete the rest of your code

            Map<String, String> data_user = new HashMap<>();
            data_user.put("nama", nama);
            data_user.put("foto", download_result);

            db.collection("Users").document(user_id).set(data_user)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(Profile.this, "Data berhasil tersimpan", Toast.LENGTH_LONG).show();
                                sendto(MainActivity.class);
                            } else {

                                String err = task.getException().getMessage();
                                Toast.makeText(Profile.this, "Error : " + err, Toast.LENGTH_LONG).show();

                            }
                        }
                    });
        }
        profileProgress.setVisibility(View.INVISIBLE);
    }
}