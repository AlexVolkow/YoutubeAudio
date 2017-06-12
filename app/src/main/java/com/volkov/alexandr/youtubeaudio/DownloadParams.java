package com.volkov.alexandr.youtubeaudio;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import com.volkov.alexandr.youtubeaudio.downloader.AudioDownloader;
import com.volkov.alexandr.youtubeaudio.downloader.AudioLink;
import com.volkov.alexandr.youtubeaudio.downloader.FailedDownloadException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.DecimalFormat;


public class DownloadParams extends AppCompatActivity {
    private static final DecimalFormat decimalFormat = new DecimalFormat("#0.0");
    private RadioGroup qualityRG;
    private AudioLink max;
    private AudioLink min;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_params);
        qualityRG = (RadioGroup) findViewById(R.id.radioGroup_quality);
        Intent data = getIntent();
        max = data.getParcelableExtra("max");
        min = data.getParcelableExtra("min");
        addRadioButton(getQuality(min));
        addRadioButton(getQuality(max));
        qualityRG.check(1);
    }

    private static String getQuality(AudioLink audioLink) {
        return audioLink.getType() + " " + decimalFormat.format(audioLink.getSize()) + " Mb";
    }

    private void addRadioButton(String msg) {
        RadioButton newButton = new RadioButton(this);
        newButton.setText(msg);
        qualityRG.addView(newButton);
    }

    public void close(View v) {
        finish();
    }

    public void startDownload(View v) throws MalformedURLException {
        int id = qualityRG.getCheckedRadioButtonId();
        AudioLink url;
        if (id == 1) {
            url = min;
        } else {
            url = max;
        }
        EditText fileName = (EditText) findViewById(R.id.editText_FileName);
        DownloadManager dm = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        AudioDownloader downloader = new AudioDownloader(url, dm);
        try {
            downloader.download(fileName.getText().toString());
            finish();
        } catch (FailedDownloadException e) {
            Toast.makeText(this,"It's a trap!", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
