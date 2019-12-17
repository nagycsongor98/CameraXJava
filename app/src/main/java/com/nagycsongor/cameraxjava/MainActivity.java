package com.nagycsongor.cameraxjava;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraX;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureConfig;
import androidx.camera.core.Preview;
import androidx.camera.core.PreviewConfig;
import androidx.camera.core.VideoCapture;
import androidx.camera.core.VideoCaptureConfig;
import androidx.lifecycle.LifecycleOwner;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.util.Rational;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private ImageCapture imageCapture;
    public static VideoCapture videoCapture;
    private boolean recording = false;
    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Camera camera = Camera.camera;

        Preview preview = camera.getPreview();

//        camera.bindLifecycle(this);

        TextureView textureView = findViewById(R.id.textureView);

        preview.setOnPreviewOutputUpdateListener(
                previewOutput -> {
                    textureView.setSurfaceTexture(previewOutput.getSurfaceTexture());
                });


//        PreviewConfig previewConfig = new PreviewConfig.Builder()
//                .setTargetRotation(getWindowManager().getDefaultDisplay().getRotation())
//                .build();
//        Preview preview = new Preview(previewConfig);
//
//        TextureView textureView = findViewById(R.id.textureView);
//
//        preview.setOnPreviewOutputUpdateListener(
//                previewOutput -> {
//                    textureView.setSurfaceTexture(previewOutput.getSurfaceTexture());
//                });
//
//        VideoCaptureConfig videoCaptureConfig = new VideoCaptureConfig.Builder()
//                .setLensFacing(CameraX.LensFacing.BACK)
//                .setTargetAspectRatio(Rational.POSITIVE_INFINITY)
//                .setTargetRotation(getWindowManager().getDefaultDisplay().getRotation())
//                .build();
//
//        videoCapture = new VideoCapture(videoCaptureConfig);
//        CameraX.bindToLifecycle((LifecycleOwner) this, preview,videoCapture);
//
        Button b = findViewById(R.id.saveButton);
        b.setOnClickListener(v -> {
//           if (recording){
//               videoCapture.stopRecording();
//               recording = false;
//           }else {
//               saveVideo();
//               recording = true;
//           }
//
            if (recording) {
                Intent intent = new Intent(MainActivity.this, RecordService.class);
                stopService(intent);
                recording = false;
            } else {
                Intent intent = new Intent(MainActivity.this, RecordService.class);
                startService(intent);
                recording = true;
            }
        });

    }

    @SuppressLint("RestrictedApi")
    public void saveVideo() {
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

            }

            @Override
            public void onError(@NonNull VideoCapture.VideoCaptureError videoCaptureError, @NonNull String message, @Nullable Throwable cause) {

            }
        });

    }



}
