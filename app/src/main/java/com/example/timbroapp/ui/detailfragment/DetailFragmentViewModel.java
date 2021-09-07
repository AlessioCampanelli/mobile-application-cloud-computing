package com.example.timbroapp.ui.detailfragment;

import android.os.Environment;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.timbroapp.model.Stamping;
import com.example.timbroapp.model.StatusFile;
import com.example.timbroapp.utility.PDFDownloaderCallback;
import com.example.timbroapp.utility.PDFUtility;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

public class DetailFragmentViewModel extends ViewModel {

    public MutableLiveData<StatusFile> statusFile = new MutableLiveData<>();


    public void checkFileExist(Stamping stamping) {
        String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath()+"/" + stamping.getFileName();
        File file = new File(filePath);

        if (file.exists()) {
            stamping.setStatusFile(StatusFile.READY);
            stamping.setFilePath(filePath);
            statusFile.postValue(StatusFile.READY);
        } else {
            stamping.setStatusFile(StatusFile.TO_DOWNLOAD);
            statusFile.postValue(StatusFile.TO_DOWNLOAD);
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
