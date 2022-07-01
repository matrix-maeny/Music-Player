package com.matrix_maeny.musicplayer.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.matrix_maeny.musicplayer.MainActivity;
import com.matrix_maeny.musicplayer.R;
import com.matrix_maeny.musicplayer.javaclasses.MusicService;
import com.matrix_maeny.musicplayer.javaclasses.Songs;
import com.matrix_maeny.musicplayer.models.SongModel;

import org.jetbrains.annotations.Contract;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

public class PlayerActivity extends AppCompatActivity {

    String mainSongName = null;
    Bitmap mainSongImage = null;
    ImageView playPauseSong, previousSong, nextSong, songImage;
    TextView startTime, endTime, songName;
    SeekBar seekBar;

    int playPauseFlag = 1;
    int position;
    Uri uri = null;

    Thread updateSeekbarThread;


    MediaPlayer mediaPlayer = null;
    final Handler handler = new Handler();
    private ArrayList<SongModel> songModels = null;
    ArrayList<File> allSongs;
    int startTimeCount = 0;
    int currentPos = 0;// mediaPlayer.getCurrentPosition();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        Objects.requireNonNull(getSupportActionBar()).hide();
        initialize();
        try {
            mediaPlayer = Songs.mediaPlayer;

            if (mediaPlayer != null) {
                if (Songs.songState.equals("new")) {
                    if (position != Songs.currentPosition)
                        pauseSong();
                    else {
                        loadCurrentPlayingSong();
                    }
                } else {
                    position = Songs.currentPosition;
                    loadCurrentPlayingSong();
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    private void loadCurrentPlayingSong() {
        loadVariablesOfSong(position);
        if (mediaPlayer.isPlaying()) {
            playPauseSong.setImageResource(R.drawable.ic_baseline_pause_circle_outline_24);
            playPauseFlag = 2;
        } else {
            playPauseSong.setImageResource(R.drawable.ic_baseline_play_circle_outline_24);
        }

        startSeekbar();
    }


    private void initialize() {

        if (Songs.songState.equals("new")) {
            position = getIntent().getIntExtra("position", -1);
        }

        playPauseSong = findViewById(R.id.imageViewPPlay);
        previousSong = findViewById(R.id.imageViewPrevious);
        nextSong = findViewById(R.id.imageViewNext);
        songImage = findViewById(R.id.imageViewPSongImage);

        startTime = findViewById(R.id.textViewPSongStartTime);
        endTime = findViewById(R.id.textViewPSongEndTime);
        seekBar = findViewById(R.id.seekBarPSongTime);
        songName = findViewById(R.id.textViewPSongName);
        songName.setSelected(true);

        setListeners();

        allSongs = Songs.allSongs;
        songModels = Songs.songModels;


        if (position != -1 && Songs.songState.equals("new"))
            initializeSong(position);


    }

    private void setListeners() {
        playPauseSong.setOnClickListener(playPauseListener);
        seekBar.setOnSeekBarChangeListener(seekBarChangeListener);

        nextSong.setOnClickListener(nextSongListener);
        previousSong.setOnClickListener(previousSongListener);
    }

    SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

            mediaPlayer.seekTo(seekBar.getProgress());
            startTimeCount = seekBar.getProgress();
        }
    };
    View.OnClickListener nextSongListener = v -> {
        seekBar.setProgress(0);
        startNextSong(true);
    };
    View.OnClickListener previousSongListener = v -> {
        seekBar.setProgress(0);
        startNextSong(false);
    };


    @SuppressLint("SetTextI18n")


    View.OnClickListener playPauseListener = v -> {
        changePlayPause();
    };

    private void changePlayPause() {
        if (playPauseFlag == 1) {

            playPauseSong.setImageResource(R.drawable.ic_baseline_pause_circle_outline_24);
            playPauseFlag = 2;
            resumeSong();
            startService();
        } else {

            playPauseSong.setImageResource(R.drawable.ic_baseline_play_circle_outline_24);
            playPauseFlag = 1;
            pauseSong();
            stopService();
        }
    }


    private void initializeSong(int position) {
        loadVariablesOfSong(position);

        uri = getSongUri(allSongs.get(position).toString());

        if (Songs.currentPosition == position) {
            return;
        }

        handler.postDelayed(() -> playSelectedSong(uri), 300);
        changePlayPause();


    }

    private void loadVariablesOfSong(int position) {
        mainSongName = Songs.songModels.get(position).getSongName();
        mainSongImage = Songs.songModels.get(position).getSongImage();
        songName.setText(mainSongName);
        if (mainSongImage != null) {
            songImage.setImageBitmap(mainSongImage);
        } else {
            songImage.setImageResource(R.drawable.music);
        }


    }


    private Uri getSongUri(String recognitionPath) {

        Uri tempUri = null;

        try {
            tempUri = Uri.parse(recognitionPath);
        } catch (Exception ignored) {
        }

        return tempUri;
    }


    MediaPlayer.OnCompletionListener songCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            changePlayPause();

            handler.post(() -> {
                changeSong();
                seekBar.setProgress(0);
            });
        }


    };


    private void playSelectedSong(Uri uri) {

        stopSong();
        mediaPlayer = MediaPlayer.create(PlayerActivity.this, uri);
        Songs.mediaPlayer = mediaPlayer;
        Songs.currentUri = uri;
        mediaPlayer.setOnCompletionListener(songCompletionListener);
        mediaPlayer.start();

        startSeekbar();
        startService();


    }

    private void resumeSong() {
        if (mediaPlayer != null) {
            try {
                mediaPlayer.start();
            } catch (Exception ignored) {
            }
        }
    }

    private void stopSong() {
        if (mediaPlayer != null) {
            try {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
            } catch (Exception ignored) {
            }

        }

    }

    private void pauseSong() {
        if (mediaPlayer != null) {
            try {
                mediaPlayer.pause();
                updateSeekbarThread.interrupt();
            } catch (Exception ignored) {
            }
        }
    }

    private void changeSong() {
        int randomSong;

        if (!allSongs.isEmpty()) {

            randomSong = new Random().nextInt(allSongs.size());
            position = randomSong;
            playSelectedSong(getSongUri(allSongs.get(position).toString()));

            changePlayPause();
            loadVariablesOfSong(position);

            startSeekbar();
        }
    }

    private void startNextSong(boolean toNext) {
        if (!allSongs.isEmpty()) {
            if (toNext) {
                position++;
            } else {
                position--;
            }

            if (position >= allSongs.size()) {
                position = 0;
            }
            if (position < 0) position = allSongs.size() - 1;


            handler.post(() -> playSelectedSong(getSongUri(allSongs.get(position).toString())));

            playPauseSong.setImageResource(R.drawable.ic_baseline_pause_circle_outline_24);

            loadVariablesOfSong(position);
        }
    }

    private void startSeekbar() {

        try {
            updateSeekbarThread.interrupt();
        } catch (Exception ignored) {
        }

        updateSeekbarThread = new Thread() {
            @SuppressLint("SetTextI18n")
            public void run() {
                int totalDuration = mediaPlayer.getDuration();
                handler.post(() -> endTime.setText(createTime(totalDuration)));


                currentPos = 0;
                seekBar.setMax(mediaPlayer.getDuration());


                while (currentPos < totalDuration) {

                    try {
                        sleep(700);
                        currentPos = mediaPlayer.getCurrentPosition();
                        int finalCurrentPos = currentPos;
                        handler.post(() -> {
                            seekBar.setProgress(finalCurrentPos);
                            startTime.setText(createTime(finalCurrentPos));

                        });


                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }


            }
        };
        updateSeekbarThread.start();
    }

    @NonNull
    @Contract(pure = true)
    private String createTime(int duration) {
        String time = "";
        String hourText = "", minuteText = "", secondText = "";

        int seconds = duration / 1000;
        int minutes = seconds / 60;
        seconds = seconds % 60;

        int hour = minutes / 60;

        if (hour == 0) hourText = "00";
        if (hour < 10) hourText = ("0" + hour);
        else hourText += hour;

        if (minutes == 0) minuteText = "00";
        if (minutes < 10) minuteText = ("0" + minutes);
        else minuteText += minutes;

        if (seconds == 0) secondText = "00";
        if (seconds < 10) secondText = ("0" + seconds);
        else secondText += seconds;


        if (hour != 0) {
            time = hourText + ":" + minuteText + ":" + secondText;
        } else {
            time = minuteText + ":" + secondText;

        }


        return time;


    }

    @Override
    public void onBackPressed() {
        try {
            updateSeekbarThread.interrupt();
        } catch (Exception ignored) {
        }


        super.onBackPressed();

    }


    private void startService() {

//        String hello = "Baktha singh hello";

        Intent intent = new Intent(PlayerActivity.this, MusicService.class);
//        intent.putExtra("msg", hello);
//        intent.putExtra("position", position);
        Songs.currentPosition = position;
        startService(intent);
    }

    private void stopService() {
        Intent intent = new Intent(PlayerActivity.this, MusicService.class);
        stopService(intent);
    }
}