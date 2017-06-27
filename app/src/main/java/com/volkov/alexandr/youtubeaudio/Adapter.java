package com.volkov.alexandr.youtubeaudio;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import com.volkov.alexandr.youtubeaudio.music.Audio;
import com.volkov.alexandr.youtubeaudio.downloader.AudioDownloader;
import com.volkov.alexandr.youtubeaudio.downloader.FailedDownloadException;
import com.volkov.alexandr.youtubeaudio.music.MusicControls;

import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static com.volkov.alexandr.youtubeaudio.music.MusicService.*;

/**
 * Created by AlexandrVolkov on 14.06.2017.
 */
public class Adapter extends RecyclerView.Adapter<Adapter.Holder> {
    private static final String SET_ONLY_PLAY_IMG = "SET_ONLY_PLAY_IMG";

    private static String lastSongUrl;
    private static int mCurrentPlayingPosition = -1;

    private static Drawable pauseImg;
    private static Drawable playImg;

    private static final DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.ENGLISH);
    private static final DecimalFormat decimalFormat = new DecimalFormat("#0.0");

    private List<Audio> dataSet;
    private Context context;
    private MusicControls musicControls;

    public static class Holder extends RecyclerView.ViewHolder {
        TextView title;
        TextView text;
        TextView length;
        TextView size;
        ImageButton download;
        ImageButton play;
        ImageView cover;

        public Holder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.textView_title);
            text = (TextView) itemView.findViewById(R.id.textView_text);
            length = (TextView) itemView.findViewById(R.id.textView_length);
            size = (TextView) itemView.findViewById(R.id.textView_size);
            download = (ImageButton) itemView.findViewById(R.id.button_download);
            play = (ImageButton) itemView.findViewById(R.id.button_play);
            cover = (ImageView) itemView.findViewById(R.id.item_cover);
        }
    }

    public Adapter(Context context, List<Audio> myDataset,final MusicControls musicControls) {
        this.dataSet = myDataset;
        this.context = context;
        this.musicControls = musicControls;

        pauseImg = context.getResources().getDrawable(R.drawable.exo_controls_pause);
        playImg = context.getResources().getDrawable(R.drawable.exo_controls_play);

        BroadcastReceiver playerReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent != null) {
                    String status = intent.getStringExtra(CMD_STATUS);
                    if (status != null) {
                        String param = musicControls.isPlaying() ? PLAY : PAUSE;
                        notifyItemChanged(mCurrentPlayingPosition, Arrays.asList(SET_ONLY_PLAY_IMG, param));
                    }
                }
            }
        };

        IntentFilter intFilt = new IntentFilter(BROADCAST_ACTION);
        context.registerReceiver(playerReceiver, intFilt);
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent,
                                     int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_audio_item, parent, false);

        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(Holder holder, int position, List<Object> payloads) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position);
        } else {
            List params = (List) payloads.get(0);
            if (params.get(0) instanceof String) {
                String cmd = (String) params.get(0);
                String arg = (String) params.get(1);
                if (cmd.equals(SET_ONLY_PLAY_IMG)) {
                    if (mCurrentPlayingPosition == holder.getAdapterPosition() && PLAY.equals(arg)) {
                        holder.play.setBackground(pauseImg);
                    } else {
                        holder.play.setBackground(playImg);
                    }
                }
            }
        }
    }

    @Override
    public void onBindViewHolder(final Holder holder, final int position) {
        final Audio audio = dataSet.get(position);

        holder.title.setText(df.format(audio.getDate()));
        holder.text.setText(audio.getTitle());
        Picasso.with(context).load(audio.getCoverUrl()).
                into(holder.cover);
        String length = String.valueOf((int) (audio.getLength() / 60)) + " min";
        holder.length.setText(length);
        holder.size.setText(String.format("%s Mb", decimalFormat.format(audio.getSize())));

        holder.download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DownloadManager dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                AudioDownloader downloader = new AudioDownloader(dm, audio.getUrl());
                try {
                    downloader.download(audio.getTitle());
                } catch (IOException | FailedDownloadException e) {
                    e.printStackTrace();
                }
            }
        });

        holder.play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lastSongUrl == null || !lastSongUrl.equals(audio.getUrl())) {
                    if (mCurrentPlayingPosition != -1) {
                        notifyItemChanged(mCurrentPlayingPosition, Arrays.asList(SET_ONLY_PLAY_IMG, PAUSE));
                    }
                    mCurrentPlayingPosition = holder.getAdapterPosition();
                    lastSongUrl = audio.getUrl();

                    musicControls.playSong(audio);
                } else if (musicControls.isPlaying()) {
                    musicControls.pause();
                } else {
                    musicControls.play();
                }
            }
        });
    }

    public void addItem(Audio dataObj, int index) {
        dataSet.add(index, dataObj);
        notifyItemInserted(index);
    }

    public void addItem(Audio dataObj) {
        dataSet.add(dataObj);
        notifyItemInserted(dataSet.size() - 1);
    }

    public void deleteItem(int index) {
        dataSet.remove(index);
        notifyItemRemoved(index);
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}
