package com.example.blogapp.mainFragments;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.blogapp.Profile;
import com.example.blogapp.R;
import com.example.blogapp.RV.mypostsAdapter;
import com.example.blogapp.RV.post;
import com.example.blogapp.RV.postAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link mypostsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class mypostsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private List<post> mypostsList;
    private mypostsAdapter mypostsRVAdapter;
    private RecyclerView myposts_RV;

    private CircleImageView pict;
    private TextView username;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;


    public mypostsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment mypostsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static mypostsFragment newInstance(String param1, String param2) {
        mypostsFragment fragment = new mypostsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_myposts, container, false);

        pict = (CircleImageView) view.findViewById(R.id.circledprofile_myposts);
        username = (TextView) view.findViewById(R.id.username_myposts);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        String id = currentUser.getUid();

        if (currentUser!= null){

            db = FirebaseFirestore.getInstance();
            db.collection("Users").document(id).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()){
                                String nama = task.getResult().getString("nama");
                                String foto = task.getResult().getString("foto");

                                username.setText(nama);

                                RequestOptions PlaceholderReq = new RequestOptions();
                                PlaceholderReq.placeholder(R.drawable.resource_default);

                                Glide.with(view.getContext()).setDefaultRequestOptions(PlaceholderReq).load(foto).into(pict);

                            }
                        }
                    });

            com.google.firebase.firestore.Query query = db.collection("Posts")
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .whereEqualTo("user_id", id);
            query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot value, FirebaseFirestoreException error) {

                    if (value!=null){

                        for (DocumentChange doc: value.getDocumentChanges()){

                            if (doc.getType() == DocumentChange.Type.ADDED){

                                String idpost = doc.getDocument().getId();

                                post inpostList = new post();
                                inpostList = doc.getDocument().toObject(post.class).withID(idpost);
                                mypostsList.add(inpostList);

                                mypostsRVAdapter.notifyDataSetChanged();

                            }

                        }

                    }



                }
            });

            mypostsList = new ArrayList<>();
            mypostsRVAdapter = new mypostsAdapter(mypostsList);

            // Add the following lines to create RecyclerView
            myposts_RV = view.findViewById(R.id.rv_myposts);
            myposts_RV.setHasFixedSize(false);
            myposts_RV.setLayoutManager(new LinearLayoutManager(view.getContext()));
            myposts_RV.setAdapter(mypostsRVAdapter);

        }

        return view;

    }
}