package com.example.timbroapp.utility;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class PDFUtility {

    public void getPDF(URL urlPDF, File file, PDFDownloaderCallback listener) {

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
                    OutputStream outputStream = new FileOutputStream(file);
                    byte[] buffer = new byte[2 * 1024];
                    int length = 0;
                    int downloadedSize = 0;
                    while ((length = inputStream.read(buffer)) > 0) {

                        outputStream.write(buffer, 0, length);
                        downloadedSize += length;
                        //Log.i("downloaded perc:", "" + (downloadedSize*100)/filesize + "%");

                        listener.downloadInProgress((downloadedSize*100)/filesize);

                    }

                    listener.onFinish(file.getAbsolutePath());

                } catch (IOException e) {
                    e.printStackTrace();
                    listener.onError(e.getMessage());
                }
            }
        });

        thread.start();
    }

    public void openPDF(Context context, File file) {

        Intent intent = new Intent(Intent.ACTION_VIEW);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

            Uri uri = FileProvider.getUriForFile(context,
                    context.getApplicationContext().getPackageName() + ".provider", file);
            intent.setDataAndType(uri, "application/pdf");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        } else {
            intent.setDataAndType(Uri.parse(file.getPath()), "application/pdf");
        }

        try {
            context.startActivity(intent);
        } catch (Throwable t) {
            t.printStackTrace();
            //attemptInstallViewer();
        }
    }

}

