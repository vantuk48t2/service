package com.example.music;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

public class MyService extends Service{
    private static final String TAG = "MyService";

    // do m ko muon no start luc onCreate nen t tao bien nay de block no
    private static boolean mServiceRunning = false;

    private final MyBinder mMyBinder = new MyBinder();

    // MediaPlayer player;
    private static final String LOG_TAG ="ForegroundService";
    private int isPlay= android.R.drawable.ic_media_play;

    private MediaPlayer mMediaPlayer;



    private BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            receiverAction(intent);
        }
    };
    public void receiverAction(Intent intent)
    {
        // -_-
        if(intent==null||intent.getAction()==null) return;
        // TODO: ...
        Log.d(TAG, "receiverAction: action is "+intent.getAction());

        if(intent.getAction().equals(Constants.ACTION.PREV_ACTION)){

        }
        else if(intent.getAction().equals(Constants.ACTION.PLAY_ACTION)){
            //Phat nhac + thong bao khi playmusic
            initMediaPlayer();

            if(isPlay == android.R.drawable.ic_media_pause) {
                isPlay = android.R.drawable.ic_media_play;
                mMediaPlayer.pause();
            }
            else {
                isPlay = android.R.drawable.ic_media_pause;
                mMediaPlayer.start();
            }
        }
        else if(intent.getAction().equals(Constants.ACTION.NEXT_ACTION)){
            Toast.makeText(getApplicationContext(),"asd",Toast.LENGTH_SHORT).show();
        }

        if (intent.getAction().equals(Constants.ACTION.STOPFOREGROUND_ACTION)) {
            Log.i(LOG_TAG, "Received Stop Foreground Intent");
            stopForeground(true);
            stopSelf();//tat service
        }
        else {
            //notification
            //Main intent
            Intent notifi = new Intent(this, MainActivity.class);
            //intent.setAction(Constants.ACTION.MAIN_ACTION); //Set action chung duoc thuc hien
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); //dat co kiem soat intent

            //1 PendingIntent la 1 token dua den 1 ung dung khac(vi du notification manager, alarm), cho phep
            //ung dung khac nay su dung permission trong ung dung cua ban de thuc hien 1 chuc nang nao do
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0,
                    notifi, 0);
            //Previous
            Intent previousIntent = new Intent(this, MyService.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            previousIntent.setAction(Constants.ACTION.PREV_ACTION);
            PendingIntent ppreviousIntent = PendingIntent.getService(this, 0,
                    previousIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            //play
            Intent playIntent = new Intent(this, MyService.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            playIntent.setAction(Constants.ACTION.PLAY_ACTION);
            PendingIntent pplayIntent = PendingIntent.getService(this, 0,
                    playIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            //next
            Intent nextIntent = new Intent(this, MyService.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            nextIntent.setAction(Constants.ACTION.NEXT_ACTION);
            PendingIntent pnextIntent = PendingIntent.getService(this, 0,
                    nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.chin);
            //notification
            Notification notification = new Notification.Builder(this)
                    .setContentTitle("Music Player")
                    .setTicker("Music Player")
                    .setContentText("My music")
                    .setSmallIcon(R.drawable.chin)
                    .setLargeIcon(Bitmap.createScaledBitmap(icon, 128, 128, false))
                    .setPriority(Notification.PRIORITY_MAX)
                    //.setWhen(0)
                    .setContentIntent(pendingIntent)
                    .setOnlyAlertOnce(true)
                    // .addAction(android.R.drawable.ic_media_previous, "Prev",retrievePlaybackAction(Constants.ACTION.PREV_ACTION))
                    // .addAction(isPlay, "Play",retrievePlaybackAction(Constants.ACTION.PLAY_ACTION))
                    // .addAction(android.R.drawable.ic_media_next, "Next", retrievePlaybackAction(Constants.ACTION.NEXT_ACTION))
                    .addAction(android.R.drawable.ic_media_previous, "Prev",ppreviousIntent)
                    .addAction(isPlay, "Play",pplayIntent)
                    .addAction(android.R.drawable.ic_media_next, "Next", pnextIntent)

                    .build();
            startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, notification);
        }

    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        super.onCreate();
        IntentFilter intentFilter= new IntentFilter();
        intentFilter.addAction(Constants.ACTION.PREV_ACTION);
        intentFilter.addAction(Constants.ACTION.PLAY_ACTION);
        intentFilter.addAction(Constants.ACTION.NEXT_ACTION);
        registerReceiver(mIntentReceiver,intentFilter);
        mServiceRunning = true;

    }

    private void initMediaPlayer() {
        if(mMediaPlayer!=null) return;
        mMediaPlayer = MediaPlayer.create(this, R.raw.minute); // ak cai file nay no phat am dc ha
        mMediaPlayer.setLooping(true);
        mMediaPlayer.start();
    }

    public static boolean isServiceRunning(){
        return mServiceRunning;
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mMyBinder;
    }
    @Override
    //duoc goi khi o main activity goi startservice
    public int onStartCommand(Intent intent, int flags, int startId) {

        receiverAction(intent);
        return START_STICKY;
    }

//    private final PendingIntent retrievePlaybackAction(final String action) {
//        final ComponentName serviceName = new ComponentName(this, MyService.class);
//        Intent intent = new Intent(action);
//        intent.setComponent(serviceName);
//
//        return PendingIntent.getService(this, 0, intent, 0);
//    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
        mServiceRunning = false;
        unregisterReceiver(mIntentReceiver);

        if(mMediaPlayer!=null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        //stopSelf();
        //player.stop();
    }

    // Tao 1 cai Binder, cai nay can thi service moi bind dc
    public class MyBinder extends Binder {
        public MyService getService() {
            return MyService.this;
        }
    }
}
