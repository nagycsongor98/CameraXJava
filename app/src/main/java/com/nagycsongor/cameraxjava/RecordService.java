package com.nagycsongor.cameraxjava;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.util.Rational;
import android.view.Surface;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.CameraX;
import androidx.camera.core.VideoCapture;
import androidx.camera.core.VideoCaptureConfig;
import androidx.core.app.NotificationCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleService;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static androidx.core.app.NotificationCompat.PRIORITY_LOW;

public class RecordService extends LifecycleService {


    private VideoCapture videoCapture;
    public RecordService() {

    }

    StopReceiver receiver;

    @SuppressLint("RestrictedApi")
    @Override
    public void onCreate() {
        super.onCreate();
        receiver = new StopReceiver();
        registerReceiver(receiver,new IntentFilter("stop"));
        startForeground(1, getNotification());
//        VideoCaptureConfig videoCaptureConfig = new VideoCaptureConfig.Builder()
//                .setLensFacing(CameraX.LensFacing.BACK)
//                .setTargetAspectRatio(Rational.POSITIVE_INFINITY)
//                .setTargetRotation(Surface.ROTATION_0)
//                .build();
//
//        videoCapture = new VideoCapture(videoCaptureConfig);
//
//        CameraX.bindToLifecycle((LifecycleOwner) this,videoCapture);

        this.videoCapture = Camera.camera.getVideoCapture();


        Camera.camera.bindLifecycle(this);
        //CameraX.bindToLifecycle(this, videoCapture);


        String prepend = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
        File mVideoFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES), "CameraX");
        if (!mVideoFolder.exists()){
            mVideoFolder.mkdirs();
        }
        File file = null;
        try {
            file = File.createTempFile(prepend, ".mp4", mVideoFolder);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Handler handler = new Handler();
        videoCapture.startRecording(file, new VideoCapture.OnVideoSavedListener() {
            @Override
            public void onVideoSaved(@NonNull File file) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        CameraX.unbindAll();
                    }
                });


                stopSelf();
            }

            @Override
            public void onError(@NonNull VideoCapture.VideoCaptureError videoCaptureError, @NonNull String message, @Nullable Throwable cause) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        CameraX.unbindAll();
                    }
                });

                stopSelf();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }


    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }

     class StopReceiver extends BroadcastReceiver{

         @SuppressLint("RestrictedApi")
        @Override
        public void onReceive(Context context, Intent intent) {
            videoCapture.stopRecording();
        }
    }

    private Notification getNotification() {
        String channel = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ? createChannel() : "";
        Intent stopIntent = new Intent(this, RecordService.class);
        stopIntent.setAction("stop");
        return new NotificationCompat.Builder(this, channel)
               .setContentTitle("recording...")
                .setPriority(PRIORITY_LOW)
                .addAction(android.R.drawable.ic_media_pause, "", PendingIntent.getService(this, 0, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT))
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
    }

    @NonNull
    @TargetApi(26)
    private synchronized String createChannel() {
        String res = "some channel id";
        NotificationChannel channel = new NotificationChannel(res, "some name", NotificationManager.IMPORTANCE_LOW);
        channel.enableLights(true);
        channel.setLightColor(Color.BLUE);
        NotificationManager manager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.createNotificationChannel(channel);
        } else {
            stopSelf();
        }
        return res;
    }


}
