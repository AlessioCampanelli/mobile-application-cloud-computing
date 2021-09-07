package com.example.timbroapp.utility;

import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class PDFUtility {

    public void getPDF(URL urlPDF, File path, PDFDownloaderCallback listener) {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.i("URL:", String.valueOf(urlPDF));

                    URLConnection connection = urlPDF.openConnection();
                    connection.setReadTimeout(30000);
                    connection.setConnectTimeout(30000);
                    int filesize = connection.getContentLength();
                    InputStream inputStream = connection.getInputStream();
                    OutputStream outputStream = new FileOutputStream(path);
                    byte[] buffer = new byte[2 * 1024];
                    int length = 0;
                    int downloadedSize = 0;
                    while ((length = inputStream.read(buffer)) > 0) {

                        outputStream.write(buffer, 0, length);
                        downloadedSize += length;
                        //Log.i("downloaded perc:", "" + (downloadedSize*100)/filesize + "%");

                        listener.downloadInProgress((downloadedSize*100)/filesize);

                    }

                    listener.onFinish(path.getAbsolutePath());

                } catch (IOException e) {
                    e.printStackTrace();
                    listener.onError(e.getMessage());
                }
            }
        });

        thread.start();
    }

}

