package com.example.blogapp.RV;

import com.google.firebase.Timestamp;

public class post extends postid {

    private String judul, deskripsi, user_id;
    private Timestamp timestamp;

    public post(String judul, String deskripsi, String user_id, Timestamp timestamp) {
        this.judul = judul;
        this.deskripsi = deskripsi;
        this.user_id = user_id;
        this.timestamp = timestamp;
    }

    public post() { }

    public String getJudul() {
        return judul;
    }

    public void setJudul(String judul) {
        this.judul = judul;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
