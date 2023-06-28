package com.example.blogapp.mainFragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.blogapp.*;
import com.example.blogapp.RV.post;
import com.example.blogapp.RV.postAdapter;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link homeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class homeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private List<post> postList;
    private postAdapter postRVAdapter;
    private RecyclerView posts_RV;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    public homeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment homeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static homeFragment newInstance(String param1, String param2) {
        homeFragment fragment = new homeFragment();
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
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        if (currentUser!= null){

            db = FirebaseFirestore.getInstance();

            com.google.firebase.firestore.Query queryAwal = db.collection("Posts")
                    .orderBy("timestamp", Query.Direction.DESCENDING);
            queryAwal.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot value, FirebaseFirestoreException error) {

                    if (value!=null){

                        for (DocumentChange doc: value.getDocumentChanges()){

                            if (doc.getType() == DocumentChange.Type.ADDED){

                                String idpost = doc.getDocument().getId();

                                post inpostList = new post();
                                inpostList = doc.getDocument().toObject(post.class).withID(idpost);
                                postList.add(inpostList);

                                postRVAdapter.notifyDataSetChanged();

                            }

                        }

                    }



                }
            });

            postList = new ArrayList<>();
            postRVAdapter = new postAdapter(postList);

            // Add the following lines to create RecyclerView
            posts_RV = view.findViewById(R.id.home_RV);
            posts_RV.setHasFixedSize(false);
            posts_RV.setLayoutManager(new LinearLayoutManager(view.getContext()));
            posts_RV.setAdapter(postRVAdapter);

        }

        // Inflate the layout for this fragment
        return view;

    }

}