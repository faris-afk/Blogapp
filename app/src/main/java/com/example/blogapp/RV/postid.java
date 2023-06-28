package com.example.blogapp.RV;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

public class postid {

    @Exclude
    public String postid;

    public <T extends postid> T withID(@NonNull final String id){

        this.postid = id;
        return (T)this;

    }
}
