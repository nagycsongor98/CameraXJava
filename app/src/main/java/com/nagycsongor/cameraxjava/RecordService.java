package com.nagycsongor.cameraxjava;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.util.Rational;
import android.view.Surface;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.CameraX;
import androidx.camera.core.VideoCapture;
import androidx.camera.core.VideoCaptureConfig;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleService;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RecordService extends LifecycleService {
    private VideoCapture videoCapture;
    public RecordService() {

    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onCreate() {
        super.onCreate();

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

        videoCapture.startRecording(file, new VideoCapture.OnVideoSavedListener() {
            @Override
            public void onVideoSaved(@NonNull File file) {
                CameraX.unbindAll();
                stopSelf();
            }

            @Override
            public void onError(@NonNull VideoCapture.VideoCaptureError videoCaptureError, @NonNull String message, @Nullable Throwable cause) {

            }
        });
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean stopService(Intent name) {
        this.videoCapture.stopRecording();
        return false;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }

}
