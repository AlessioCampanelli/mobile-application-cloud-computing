package com.example.timbroapp.ui.detailfragment;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import androidx.core.content.FileProvider;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.timbroapp.model.Stamping;
import com.example.timbroapp.model.StatusFile;
import com.example.timbroapp.utility.PDFDownloaderCallback;
import com.example.timbroapp.utility.PDFUtility;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;

public class DetailFragmentViewModel extends ViewModel {

    public MutableLiveData<StatusFile> statusFile = new MutableLiveData<>();


    public void checkFileExist(Stamping stamping) {
        String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath()+"/dummy.pdf";
        File file = new File(filePath);

        if (file.exists()) {
            stamping.setStatusFile(StatusFile.READY);
            stamping.setFilePath(filePath);
            statusFile.postValue(StatusFile.READY);
        } else {
            stamping.setStatusFile(StatusFile.DASCARICARE);
            statusFile.postValue(StatusFile.DASCARICARE);
        }
    }

    public void getPDF(File file, Stamping stamping) throws MalformedURLException {


        PDFUtility PDFUtility = new PDFUtility();
        PDFUtility.getPDF( new URL(stamping.getPDFUrl()), file, new PDFDownloaderCallback() {
            @Override
            public void onError(String errMessage) {
                Log.e("PDFDoownloaderError" , errMessage);
            }

            @Override
            public void downloadInProgress(int perc) {
                Log.d("PDFDoownloader", "Perc: "+perc);
            }

            @Override
            public void onFinish(String path) {
                stamping.setStatusFile(StatusFile.READY);
                stamping.setFilePath(path);
                statusFile.postValue(StatusFile.READY);
                Log.d("PDFDoownloader", "Ready");

            }
        });
    }

}
