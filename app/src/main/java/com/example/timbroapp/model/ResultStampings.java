package com.example.timbroapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ResultStampings {

    @SerializedName("stampings")
    @Expose
    private List<Stamping> stampings;

    public List<Stamping> getStampings() {
        return stampings;
    }

    public void setStampings(List<Stamping> stampings) {
        this.stampings = stampings;
    }

}