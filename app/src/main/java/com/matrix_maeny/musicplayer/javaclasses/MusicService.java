package com.matrix_maeny.musicplayer.javaclasses;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.matrix_maeny.musicplayer.R;
import com.matrix_maeny.musicplayer.activities.PlayerActivity;

import java.util.concurrent.Executor;

public class MusicService extends Service{


    @Override
    public int onStartCommand(@NonNull Intent intent, int flags, int startId) {


        int position = Songs.currentPosition;

        Intent notificationIntent = new Intent(this, PlayerActivity.class);
        notificationIntent.putExtra("state","tn"); // tn = through notification
        Songs.songState = "old";

        Bitmap bitmap = Songs.songModels.get(position).getSongImage();
        String message = Songs.songModels.get(position).getSongName();
        if(bitmap == null){
            bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.music_1);
        }


        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,notificationIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(this,CreateNotification.CHANNEL_ID)
                .setContentTitle(message)
                .setContentText("Music currently running...")
                .setLargeIcon(bitmap)
                .setSmallIcon(R.drawable.ic_baseline_music_note_24)
                .setContentIntent(pendingIntent).build();

        startForeground(1,notification);

        return START_STICKY;
    }



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }



}
