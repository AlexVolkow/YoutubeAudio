package com.volkov.alexandr.youtubeaudio;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.squareup.picasso.Picasso;
import com.volkov.alexandr.youtubeaudio.downloader.Audio;
import com.volkov.alexandr.youtubeaudio.downloader.AudioDownloader;
import com.volkov.alexandr.youtubeaudio.downloader.FailedDownloadException;

import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Created by AlexandrVolkov on 14.06.2017.
 */
public class Adapter extends RecyclerView.Adapter<Adapter.Holder> {
    private DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
    private SimpleExoPlayer player;
    private SimpleExoPlayerView simpleExoPlayerView;
    private DataSource.Factory dataSourceFactory;
    private ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();

    private static final DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.ENGLISH);
    private static final DecimalFormat decimalFormat = new DecimalFormat("#0.0");

    private List<Audio> dataSet;
    private static ClickListener clickListener;
    private Context context;

    public static class Holder extends RecyclerView.ViewHolder implements View.OnClickListener {
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
            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            clickListener.onItemClick(getAdapterPosition(), v);
        }
    }

    public void setOnItemClickListener(ClickListener myClickListener) {
        this.clickListener = myClickListener;
    }

    public Adapter(List<Audio> myDataset, Context context) {
        this.dataSet = myDataset;
        this.context = context;

        simpleExoPlayerView = new SimpleExoPlayerView(context);
        TrackSelection.Factory audioTrackSelectionFactory =
                new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector =
                new DefaultTrackSelector(audioTrackSelectionFactory);
        player = ExoPlayerFactory.newSimpleInstance(context, trackSelector);

        simpleExoPlayerView.setUseController(true);
        simpleExoPlayerView.requestFocus();

        simpleExoPlayerView.setPlayer(player);

        dataSourceFactory= new DefaultDataSourceFactory(context,
                Util.getUserAgent(context, "YoutubeAudio"), bandwidthMeter);

    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent,
                                     int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_audio_item, parent, false);

        Holder dataObjectHolder = new Holder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(Holder holder, final int position) {
        final Audio audio = dataSet.get(position);
        holder.title.setText(df.format(audio.getDate()));
        holder.text.setText(audio.getTitle());
        Picasso.with(context).load("https://img.youtube.com/vi/"+ audio.getId() + "/0.jpg").
                into(holder.cover);
        String length = String.valueOf((int) (audio.getLength() / 60)) + " min";
        holder.length.setText(length);
        holder.size.setText(decimalFormat.format(audio.getSize()) + " Mb");
        holder.download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DownloadManager dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                AudioDownloader downloader = new AudioDownloader(audio.getUrl(), dm);
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
                MediaSource videoSource = new ExtractorMediaSource(Uri.parse(audio.getUrl()),
                        dataSourceFactory, extractorsFactory, null, null);

                player.prepare(videoSource);
                player.setPlayWhenReady(true);
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

    public interface ClickListener {
        void onItemClick(int position, View v);
    }
}
