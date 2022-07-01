package com.matrix_maeny.musicplayer.models;

import android.graphics.Bitmap;

public class SongModel {

    private String songName;
    private Bitmap songImage;

    public SongModel(String songName, Bitmap songImage) {
        this.songName = songName;
        this.songImage = songImage;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public Bitmap getSongImage() {
        return songImage;
    }

    public void setSongImage(Bitmap songImage) {
        this.songImage = songImage;
    }
}
