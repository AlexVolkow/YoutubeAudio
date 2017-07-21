package com.volkov.alexandr.youtubeaudio.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import com.volkov.alexandr.youtubeaudio.R;

public class AddLinkActivity extends AppCompatActivity {
    private EditText editURL;

    public void add(View v) {
        String url = editURL.getText().toString();
        Intent data = getIntent();
        data.putExtra("url", url);
        setResult(RESULT_OK, data);
        finish();
    }

    public void close(View v) {
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube_link);
        editURL = (EditText) findViewById(R.id.editText_url);
    }
}
