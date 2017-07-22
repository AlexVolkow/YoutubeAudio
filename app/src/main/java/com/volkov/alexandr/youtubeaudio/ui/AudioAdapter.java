package com.volkov.alexandr.youtubeaudio.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.squareup.picasso.Picasso;
import com.volkov.alexandr.youtubeaudio.R;
import com.volkov.alexandr.youtubeaudio.db.DBService;
import com.volkov.alexandr.youtubeaudio.db.DBServiceImpl;
import com.volkov.alexandr.youtubeaudio.model.AudioLink;
import com.volkov.alexandr.youtubeaudio.network.NetworkService;

import java.io.File;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

import static com.volkov.alexandr.youtubeaudio.network.downloader.AudioDownloader.FULL_PATH;
import static com.volkov.alexandr.youtubeaudio.utils.AndroidHelper.fromByteToMb;
import static com.volkov.alexandr.youtubeaudio.utils.LogHelper.makeLogTag;


/**
 * Created by AlexandrVolkov on 14.06.2017.
 */
public class AudioAdapter extends RecyclerView.Adapter<AudioAdapter.Holder> {
    private static final DateFormat DATE_FORMAT = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.ENGLISH);
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#0.0");
    private static final int TITLE_LENGTH = 70;
    private static final String LOG_TOG = makeLogTag(AudioAdapter.class);

    private List<AudioLink> dataSet;
    private DBService dbService;
    private Context context;
    private NetworkService networkService;

    private Drawable icPlay;

    public AudioAdapter(Context context) {
        this.dbService = new DBServiceImpl(context);
        this.dataSet = dbService.getAllLink();
        this.context = context;
        this.networkService = new NetworkService(context);
        this.icPlay = context.getDrawable(R.drawable.ic_play_arrow_black_24dp);
    }

    private AlertDialog createDialog(int pos) {
        AudioLink link = dataSet.get(pos);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setItems(new String[]{"remove"},
                (dialog, which) -> {
                    dbService.deleteLink(link);
                    dbService.deleteAudio(link.getAudio());
                    deleteItem(pos);
                    Log.i(LOG_TOG, "Deleted audio " + link.getTitle());
                });
        builder.setCancelable(true);
        return builder.create();
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent,
                                     int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_audio_item, parent, false);

        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(final Holder holder, final int position) {
        final AudioLink audioLink = dataSet.get(position);

        holder.title.setText(DATE_FORMAT.format(audioLink.getDate()));

        String title = audioLink.getTitle();
        title = title.substring(0, Math.min(title.length(), TITLE_LENGTH));
        holder.text.setText(title);

        Picasso.with(context).load(audioLink.getCoverUrl()).
                into(holder.cover);

        String duration = String.valueOf(audioLink.getDuration() / 60) + " min";
        holder.duration.setText(duration);

        double value = fromByteToMb(audioLink.getAudio().getSize());
        String size = DECIMAL_FORMAT.format(value) + " mb";
        holder.size.setText(size);

        if (networkService.isDownloaded(audioLink)) {
            setOpenAction(holder, audioLink);
        } else {
            setDownloadAction(holder, audioLink);
        }
    }

    private void setOpenAction(Holder holder, AudioLink audioLink) {
        holder.ivAction.setBackground(icPlay);
        holder.download.setText(context.getString(R.string.open_file));
        holder.download.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.parse(getFullPath(audioLink)), "audio/*");
            context.startActivity(intent);
        });
    }

    private void setDownloadAction(Holder holder, AudioLink audioLink) {
        holder.download.setOnClickListener((View v) -> {
            holder.download.setEnabled(false);
            networkService.download(audioLink,
                    () -> {
                        holder.download.setEnabled(true);
                        setOpenAction(holder, audioLink);
                    });
        });
    }

    private String getFullPath(AudioLink audioLink) {
        return FULL_PATH + "/" + audioLink.getFileName();
    }

    public void addItem(AudioLink dataObj, int index) {
        dataSet.add(index, dataObj);
        notifyItemInserted(index);
    }

    public void addItem(AudioLink dataObj) {
        addItem(dataObj, 0);
    }

    public void deleteItem(int index) {
        dataSet.remove(index);
        notifyItemRemoved(index);
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }


    public class Holder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        @BindView(R.id.tv_title)
        TextView title;
        @BindView(R.id.tv_text)
        TextView text;
        @BindView(R.id.tv_length)
        TextView duration;
        @BindView(R.id.btn_download)
        Button download;
        @BindView(R.id.item_cover)
        ImageView cover;
        @BindView(R.id.tv_size)
        TextView size;
        @BindView(R.id.iv_action)
        ImageView ivAction;

        public Holder(View itemView) {
            super(itemView);
            itemView.setOnLongClickListener(this);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public boolean onLongClick(View v) {
            int pos = getAdapterPosition();
            AlertDialog dialog = createDialog(pos);
            dialog.show();
            return true;
        }
    }
}
