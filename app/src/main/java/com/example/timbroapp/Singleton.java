package com.example.timbroapp;

import com.example.timbroapp.model.Stamping;

import java.util.List;

public class Singleton  {

    private static Singleton INSTANCE = null;

    // other instance variables can be here
    private String id_user;
    private List<Stamping> stampings;
    private String jwt_token;
    private String firebase_token;

    private Singleton() {};

    public static Singleton getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Singleton();
        }
        return(INSTANCE);
    }

    // other instance methods can follow
    public List<Stamping> getStampings() {
        return this.stampings;
    }
    public void setStampings(List<Stamping> stampings) {
        this.stampings = stampings;
    }
    public String getFirebase_token() { return this.firebase_token; }
    public void setFirebase_token(String token) {this.firebase_token = token; }
    public String getJwt_token() { return this.jwt_token; }
    public void setJwt_token(String token) {this.jwt_token = token; }
    public String getId_user() { return this.id_user; }
    public void setId_user(String id_user) {this.id_user = id_user;}
}
