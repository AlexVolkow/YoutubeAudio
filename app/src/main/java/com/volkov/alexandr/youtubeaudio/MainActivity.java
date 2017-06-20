package com.volkov.alexandr.youtubeaudio;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import com.volkov.alexandr.youtubeaudio.downloader.*;
import org.json.JSONException;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private RecyclerView listAudio;
    private Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listAudio = (RecyclerView) findViewById(R.id.list_audio);
        listAudio.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        listAudio.setLayoutManager(layoutManager);
        adapter = new Adapter(new ArrayList<Audio>(), this);
        listAudio.setAdapter(adapter);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;

            case R.id.action_add:
                try {
                    Audio audio = PageParser.getAudio("https://www.youtube.com/watch?v=HO6ebtWczX8");
                    adapter.addItem(audio);

                    audio = PageParser.getAudio("https://www.youtube.com/watch?v=lBztnahrOFw");
                    adapter.addItem(audio);
                } catch (JSONException e) {
                    showAlert("Failed to parse page on this url");
                    e.printStackTrace();
                } catch (Exception e) {
                    showAlert("Failed to download page on this url");
                    e.printStackTrace();
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
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
