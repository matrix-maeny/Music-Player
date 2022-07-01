package com.matrix_maeny.musicplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.os.Environment;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.matrix_maeny.musicplayer.activities.AboutActivity;
import com.matrix_maeny.musicplayer.adapters.SongListAdapter;
import com.matrix_maeny.musicplayer.javaclasses.Songs;
import com.matrix_maeny.musicplayer.models.SongModel;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static final int STORAGE_PERMISSION_CODE = 1;

    private RecyclerView recyclerView;
    private ArrayList<SongModel> list;
    SongListAdapter songListAdapter = null;
    TextView emptyTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialize();

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestStoragePermission();
        } else {
            displaySongs();
        }


    }

    // used to initialize views, by their ids.
    private void initialize() {

        recyclerView = findViewById(R.id.recyclerView);
        emptyTextView = findViewById(R.id.textViewEmpty);
        list = new ArrayList<>();


        LinearLayoutManager manager = new LinearLayoutManager(MainActivity.this);
        recyclerView.setLayoutManager(manager);


        startFetching();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void startFetching() {
        displaySongs();
        songListAdapter.notifyDataSetChanged();

    }

    // this requests the use to ask storage permission
    private void requestStoragePermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Permission needed")
                    .setMessage("Storage permission needed to access songs")
                    .setPositiveButton("ok", (dialog, which) -> ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE))
                    .setNegativeButton("cancel", (dialog, which) -> dialog.dismiss())
                    .create().show();
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        }
    }

    @Override // this method automatically called when user grant permission or denied...
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length <= 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                tempToast("Permission DENIED... please enable manually...", 1);
            } else {
                displaySongs();
            }
        }
    }

    // common method for simple toast
    private void tempToast(String s, int time) {
        if (time == 0) {
            Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, s, Toast.LENGTH_LONG).show();
        }
    }


    // returns songs
    public ArrayList<File> findSongs(@NonNull File file) {

        ArrayList<File> list = new ArrayList<>();
        File[] songFiles = file.listFiles();

        if (songFiles != null) {
            for (File file1 : songFiles) {
                if (file1.isDirectory() && !file1.isHidden()) {
                    list.addAll(findSongs(file1));
                } else {
                    if (file1.getName().endsWith(".mp3") || file1.getName().endsWith(".wav")) {
                        list.add(file1);
                    }
                }
            }
        }

        return list;
    }


    public void displaySongs() {
        list.clear();
        final ArrayList<File> songList = findSongs(Environment.getExternalStorageDirectory());


        for (int i = 0; i < songList.size(); i++) {
            String name = songList.get(i).getName().replace(".mp3", "").replace(".wav", "");

            Bitmap bitmap = getBitmap(songList.get(i).getPath());


            list.add(new SongModel(name, bitmap));
        }

        if(list.isEmpty()){
            emptyTextView.setVisibility(View.VISIBLE);
        }else{
            emptyTextView.setVisibility(View.GONE);
        }

        Songs.allSongs = songList;
        Songs.songModels = list;
        songListAdapter = new SongListAdapter(MainActivity.this, list);
        recyclerView.setAdapter(songListAdapter);



    }

    private Bitmap getBitmap(String path) {

        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(path);

        byte[] byteArray = mediaMetadataRetriever.getEmbeddedPicture();
        Bitmap bitmap = null;

        if (byteArray != null) {
            bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        }
        return bitmap;
    }


    @Override
    protected void onStart() {
        super.onStart();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.about_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        startActivity(new Intent(MainActivity.this, AboutActivity.class));

        return super.onOptionsItemSelected(item);
    }
}