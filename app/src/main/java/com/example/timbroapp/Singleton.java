package com.example.timbroapp;

import java.util.List;

public class Singleton  {

    private static Singleton INSTANCE = null;

    // other instance variables can be here
    private List<Stamping> stampings;

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
}
