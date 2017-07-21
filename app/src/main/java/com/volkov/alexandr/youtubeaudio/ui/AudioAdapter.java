package com.volkov.alexandr.youtubeaudio.ui;

import android.app.DownloadManager;
import android.content.*;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.squareup.picasso.Picasso;
import com.volkov.alexandr.youtubeaudio.R;
import com.volkov.alexandr.youtubeaudio.db.DBService;
import com.volkov.alexandr.youtubeaudio.db.DBServiceImpl;
import com.volkov.alexandr.youtubeaudio.model.AudioLink;
import com.volkov.alexandr.youtubeaudio.network.NetworkService;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.volkov.alexandr.youtubeaudio.utils.AndroidHelper.fromByteToMb;


/**
 * Created by AlexandrVolkov on 14.06.2017.
 */
public class AudioAdapter extends RecyclerView.Adapter<AudioAdapter.Holder> {
    private static final DateFormat DATE_FORMAT = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.ENGLISH);
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#0.0");
    public static final int TITLE_LENGTH = 70;

    private List<AudioLink> dataSet;
    private DBService dbService;
    private Context context;
    private NetworkService networkService;


    public AudioAdapter(Context context) {
        this.dbService = new DBServiceImpl(context);
        this.dataSet = dbService.getAllLink();
        this.context = context;
        this.networkService = new NetworkService(context);
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
        title = title.substring(0, Math.min(title.length(),TITLE_LENGTH));
        holder.text.setText(title);

        Picasso.with(context).load(audioLink.getCoverUrl()).
                into(holder.cover);

        String duration = String.valueOf(audioLink.getDuration() / 60) + " min";
        holder.duration.setText(duration);

        double value = fromByteToMb(audioLink.getAudio().getSize());
        String size = DECIMAL_FORMAT.format(value)+ " mb";
        holder.size.setText(size);

        holder.download.setOnClickListener((View v) -> networkService.download(audioLink));
    }

    public void addItem(AudioLink dataObj, int index) {
        dataSet.add(index, dataObj);
        notifyItemInserted(index);
    }

    public void addItem(AudioLink dataObj) {
        addItem(dataObj,0);
    }

    public void deleteItem(int index) {
        dataSet.remove(index);
        notifyItemRemoved(index);
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public static class Holder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
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

        public Holder(View itemView) {
            super(itemView);
            itemView.setOnLongClickListener(this);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public boolean onLongClick(View v) {
            int pos = getAdapterPosition();
            Toast.makeText(v.getContext(), String.valueOf(pos),Toast.LENGTH_SHORT).show();
            return true;
        }
    }
}
