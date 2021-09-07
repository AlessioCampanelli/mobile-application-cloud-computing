package com.example.timbroapp.utility;

public interface PDFDownloaderCallback {
    void onError(String errMessage);
    void downloadInProgress(int perc);
    void onFinish(String filePath);
}
