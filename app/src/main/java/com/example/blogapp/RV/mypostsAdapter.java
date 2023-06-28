package com.example.blogapp.RV;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.blogapp.MainActivity;
import com.example.blogapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class mypostsAdapter extends RecyclerView.Adapter<mypostsAdapter.mypostsVH> {


    public List<post> mypostList;
    public Context context;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private String currentUserId;

    private TextView likecount;
    private ImageView deleteIcon;

    public mypostsAdapter(List<post> a){
        mypostList = a;
    }

    @NonNull
    @NotNull
    @Override
    public mypostsVH onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.myposts_rv_item, parent, false);
        context = parent.getContext();
        return new mypostsVH(v);

    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull mypostsAdapter.mypostsVH holder, int position) {

        if (mypostList!=null){
            currentUserId = mypostList.get(position).getUser_id();
            db = FirebaseFirestore.getInstance();

            String id = mypostList.get(position).postid;

            String judul = mypostList.get(position).getJudul();
            holder.setJudul(judul);

            String deskripsi = mypostList.get(position).getDeskripsi();
            holder.setDeskripsi(deskripsi);

            //hitung like
            db.collection("Posts")
                    .document(id)
                    .collection("Likes")
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(QuerySnapshot value, FirebaseFirestoreException error) {

                            int count = value.size();
                            holder.setLikecount(count);

                        }
                    });

            //tombol delete
            deleteIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AlertDialog.Builder alert = new AlertDialog.Builder(context);
                    alert.setTitle("Peringatan!!");
                    alert.setMessage("Apakah anda yakin ingin mengkapus unggahan ini?");
                    alert.setPositiveButton("Ya", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            db.collection("Posts")
                                    .document(id)
                                    .delete();
                            dialog.dismiss();
                            Intent i = new Intent(context, MainActivity.class);
                            context.startActivity(i);

                        }
                    });
                    alert.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.dismiss();
                        }
                    });

                    alert.show();

                }
            });

        }

    }

    @Override
    public int getItemCount() {
        return mypostList == null ? 0 : mypostList.size();
    }

    public class mypostsVH extends RecyclerView.ViewHolder{


        private View v;
        private TextView judul, deskripsi;

        public mypostsVH(@NonNull @NotNull View itemView) {

            super(itemView);
            v = itemView;
            deleteIcon = v.findViewById(R.id.deletebtn);

        }

        public void setJudul(String jdl) {
            judul = v.findViewById(R.id.mypostsitem_title);
            judul.setText(jdl);
        }

        public void setDeskripsi(String desc) {
            deskripsi = v.findViewById(R.id.mypostsitem_desc);
            deskripsi.setText(desc);
        }

        public void setLikecount(int count){

            likecount = v.findViewById(R.id.myposts_tv_likecount);
            likecount.setText(count + " Likes");

        }

    }

}
