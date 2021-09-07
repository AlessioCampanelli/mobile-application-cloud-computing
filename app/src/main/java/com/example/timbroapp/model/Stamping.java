package com.example.timbroapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Stamping {

    @SerializedName("id_doc")
    @Expose
    private String idDoc;
    @SerializedName("start_stamped_time")
    @Expose
    private String startStampedTime;
    @SerializedName("end_stamped_time")
    @Expose
    private String endStampedTime;
    @SerializedName("subtitle")
    @Expose
    private String subtitle;
    @SerializedName("start_time")
    @Expose
    private String startTime;
    @SerializedName("end_time")
    @Expose
    private String endTime;
    @SerializedName("id_user")
    @Expose
    private String idUser;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("address")
    @Expose
    private String address;

    @SerializedName("latitude")
    @Expose
    private Double latitude;

    @SerializedName("longitude")
    @Expose
    private Double longitude;

    @SerializedName("url_pdf")
    @Expose
    private String PDFUrl;

    @SerializedName("name_pdf")
    @Expose
    private String fileName;

    private StatusFile statusFile = StatusFile.UNKNOWN;

    private String filePath = null;

    public String getIdDoc() {
        return idDoc;
    }

    public void setIdDoc(String idDoc) {
        this.idDoc = idDoc;
    }

    public String getStartStampedTime() {
        return startStampedTime;
    }

    public void setStartStampedTime(String startStampedTime) {
        this.startStampedTime = startStampedTime;
    }

    public String getEndStampedTime() {
        return endStampedTime;
    }

    public void setEndStampedTime(String endStampedTime) {
        this.endStampedTime = endStampedTime;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public StatusFile getStatusFile() {
        return statusFile;
    }

    public void setStatusFile(StatusFile statusFile) {
        this.statusFile = statusFile;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getPDFUrl() {
        return PDFUrl;
    }

    public void setPDFUrl(String PDFUrl) {
        this.PDFUrl = PDFUrl;
    }

}

