package com.example.timbroapp;

import java.util.List;

public class Singleton  {

    private static Singleton INSTANCE = null;

    // other instance variables can be here
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
    public String getJwt_token_token() { return this.jwt_token; }
    public void setJwt_token_token(String token) {this.jwt_token = token; }
}
