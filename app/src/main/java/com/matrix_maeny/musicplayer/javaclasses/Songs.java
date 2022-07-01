package com.matrix_maeny.musicplayer.javaclasses;

import android.media.MediaPlayer;
import android.net.Uri;

import com.matrix_maeny.musicplayer.models.SongModel;

import java.io.File;
import java.util.ArrayList;

public class Songs {

    public static ArrayList<File> allSongs = null;
    public static ArrayList<SongModel> songModels = null;

    public static MediaPlayer mediaPlayer = null;
    public static  int currentPosition = -1;
    public static String songState = null;
    public static Uri currentUri = null;
}
