package com.matrix_maeny.musicplayer.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.matrix_maeny.musicplayer.R;
import com.matrix_maeny.musicplayer.activities.PlayerActivity;
import com.matrix_maeny.musicplayer.javaclasses.Songs;
import com.matrix_maeny.musicplayer.models.SongModel;

import java.util.ArrayList;

public class SongListAdapter extends RecyclerView.Adapter<SongListAdapter.viewHolder> {

    Context context;
   private final ArrayList<SongModel> list;


    public SongListAdapter(Context context, ArrayList<SongModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View root = LayoutInflater.from(context).inflate(R.layout.song_list_layout, parent, false);

        return new viewHolder(root);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {

        SongModel model = list.get(position);

        holder.textViewSongName.setText(model.getSongName());
        Bitmap bitmap = model.getSongImage();

        if (bitmap != null) {
            holder.imageView.setImageBitmap(model.getSongImage());

        } else {
            holder.imageView.setImageResource(R.drawable.ic_baseline_music_note_24);

        }


      holder.cardView.setOnClickListener(V->{
          Intent intent = new Intent(context.getApplicationContext(), PlayerActivity.class);
         intent.putExtra("position",holder.getAdapterPosition());
          Songs.songState = "new";
          context.startActivity(intent);
      });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class viewHolder extends RecyclerView.ViewHolder {

        TextView textViewSongName;
        CardView cardView;
        ImageView imageView;

        public viewHolder(@NonNull View itemView) {
            super(itemView);

            textViewSongName = itemView.findViewById(R.id.textViewSongName);
            cardView = itemView.findViewById(R.id.cardViewSong);
            imageView = itemView.findViewById(R.id.imageViewSong);
        }
    }
}
