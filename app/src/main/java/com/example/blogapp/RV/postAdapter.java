package com.example.blogapp.RV;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
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

public class postAdapter extends RecyclerView.Adapter<postAdapter.postVH> {

    public List<post> postList;
    public Context context;

    private FirebaseFirestore db;
    private FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private String userid;

    {
        assert firebaseUser != null;
        userid = firebaseUser.getUid();
    }

    public postAdapter(List<post> a){
        postList = a;
    }

    @NonNull
    @NotNull
    @Override
    public postVH onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.posts_rv_item, parent, false);
        context = parent.getContext();
        return new postVH(v);

    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull postAdapter.postVH holder, int position) {

        holder.setIsRecyclable(false);

        if (postList!=null){

            String id = postList.get(position).postid;

            String uID = postList.get(position).getUser_id();

            db = FirebaseFirestore.getInstance();
            db.collection("Users").document(uID).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()){
                                String nama = task.getResult().getString("nama");
                                String foto = task.getResult().getString("foto");

                                holder.setUserName(nama);
                                holder.setProfilePict(foto);

                            }
                        }
                    });

            Timestamp tm = postList.get(position).getTimestamp();
            Date ms = tm.toDate();

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm:ss");
            holder.setTanggal(sdf.format(ms));
            holder.setWaktu(sdf2.format(ms));

            String judul = postList.get(position).getJudul();
            holder.setJudul(judul);

            String deskripsi = postList.get(position).getDeskripsi();
            holder.setDeskripsi(deskripsi);

            //hitung like
            db.collection("Posts")
                    .document(id)
                    .collection("Likes")
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(QuerySnapshot value, FirebaseFirestoreException error) {

                            if (value!=null) {

                                if (value.isEmpty()) {

                                    holder.setLikecount(0);

                                } else {

                                    int count = value.size();
                                    holder.setLikecount(count);

                                }
                            }

                        }
                    });

            //cek apakah user sudah like
            db.collection("Posts")
                    .document(id)
                    .collection("Likes")
                    .document(userid)
                    .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(DocumentSnapshot value, FirebaseFirestoreException error) {

                            if (value!=null) {

                                if (value.exists()) {

                                    holder.likebtn.setImageDrawable(context.getDrawable(R.drawable.ic_baseline_thumb_up_24));

                                } else {

                                    holder.likebtn.setImageDrawable(context.getDrawable(R.drawable.ic_baseline_thumb_up_grey));

                                }
                            }
                        }
                    });

            holder.likebtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    db.collection("Posts")
                            .document(id)
                            .collection("Likes")
                            .document(userid)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {

                                    if (!task.getResult().exists()){

                                        Map <String, Object> likemaps = new HashMap<>();
                                        likemaps.put("timestamp", FieldValue.serverTimestamp());

                                        db.collection("Posts")
                                                .document(id)
                                                .collection("Likes")
                                                .document(userid)
                                                .set(likemaps);

                                    } else {

                                        db.collection("Posts")
                                                .document(id)
                                                .collection("Likes")
                                                .document(userid)
                                                .delete();

                                    }

                                }
                            });

                }
            });

        }

    }

    @Override
    public int getItemCount() {
        return postList == null ? 0 : postList.size();
    }

    public class postVH extends RecyclerView.ViewHolder{

        private View v;
        private TextView userName, tanggal, waktu, judul, deskripsi;
        private CircleImageView profilePict;
        private ImageButton likebtn;
        private TextView likecount;

        public postVH(@NonNull @NotNull View itemView) {

            super(itemView);
            v = itemView;
            likebtn = v.findViewById(R.id.btn_like);
            likecount = v.findViewById(R.id.tv_likecount);

        }

        public void setUserName(String uname) {
            userName = (TextView) v.findViewById(R.id.item_username);
            userName.setText(uname);
        }

        public void setTanggal(String tgl) {
            tanggal = (TextView) v.findViewById(R.id.item_date);
            tanggal.setText(tgl);
        }

        public void setWaktu(String wkt) {
            waktu = (TextView) v.findViewById(R.id.item_time);
            waktu.setText(wkt);
        }

        public void setJudul(String jdl) {
            judul = (TextView) v.findViewById(R.id.item_title);
            judul.setText(jdl);
        }

        public void setDeskripsi(String desc) {
            deskripsi = (TextView) v.findViewById(R.id.item_desc);
            deskripsi.setText(desc);
        }

        public void setProfilePict(String pict) {
            profilePict = (CircleImageView) v.findViewById(R.id.item_profile_image);

            RequestOptions PlaceholderReq = new RequestOptions();
            PlaceholderReq.placeholder(R.drawable.resource_default);
            Glide.with(context).setDefaultRequestOptions(PlaceholderReq).load(Uri.parse(pict)).into(profilePict);

        }

        public void setLikecount(int count){

            likecount = v.findViewById(R.id.tv_likecount);
            likecount.setText(count + " Likes");

        }

    }

}
