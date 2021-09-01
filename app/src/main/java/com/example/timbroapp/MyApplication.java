package com.example.timbroapp;

import android.app.Application;

import java.util.List;


public class MyApplication extends Application {
    private List<Stamping> stampings;

    public List<Stamping> getStampings() {
        return stampings;
    }

    public void setStampings(List<Stamping> stmpgs) {
        stampings = stmpgs;
    }
}
