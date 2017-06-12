package com.volkov.alexandr.youtubeaudio;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import com.volkov.alexandr.youtubeaudio.downloader.*;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    private EditText urlEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        urlEdit = (EditText) findViewById(R.id.editText_URL);
    }

    public void download(View v) throws IOException, ExecutionException, InterruptedException {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED)  {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET},0);
        }
        URL url = new URL(urlEdit.getText().toString());

        PageDownloader pageDownloader = new PageDownloader();

        String html;
        try {
            html = pageDownloader.downloadPage(url);
        }catch (InterruptedException| ExecutionException e) {
            showAlert("Failed to download page on this url");
            return;
        }

        List<AudioLink> audioLink = PageParser.getAudioLinks(html);

        if (audioLink.isEmpty()) {
            showAlert("Failed to download audio from this video");
            return;
        }

        AudioLink max = Collections.max(audioLink);
        AudioLink min = Collections.min(audioLink);

        Intent downloadParams = new Intent();
        downloadParams.setClass(this, DownloadParams.class);
        downloadParams.putExtra("max", max);
        downloadParams.putExtra("min", min);
        startActivity(downloadParams);
    }

    public void showAlert(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Failed downloading")
                .setMessage(msg)
                .setCancelable(false)
                .setNegativeButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
