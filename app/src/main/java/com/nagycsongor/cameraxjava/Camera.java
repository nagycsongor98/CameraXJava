package com.nagycsongor.cameraxjava;

import android.annotation.SuppressLint;
import android.util.Rational;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.TextureView;

import androidx.annotation.NonNull;
import androidx.camera.core.CameraX;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureConfig;
import androidx.camera.core.Preview;
import androidx.camera.core.PreviewConfig;
import androidx.camera.core.VideoCapture;
import androidx.camera.core.VideoCaptureConfig;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;

public class Camera {

    private VideoCapture videoCapture;
    private Preview preview;
    public static Camera camera = new Camera();

    @SuppressLint("RestrictedApi")
    private Camera() {
        PreviewConfig previewConfig = new PreviewConfig.Builder()
                .setTargetRotation(Surface.ROTATION_0)
                .build();
        preview = new Preview(previewConfig);


        VideoCaptureConfig videoCaptureConfig = new VideoCaptureConfig.Builder()
                .setLensFacing(CameraX.LensFacing.BACK)
                .setTargetAspectRatio(Rational.POSITIVE_INFINITY)
                .setTargetRotation(Surface.ROTATION_0)
                .build();

        videoCapture = new VideoCapture(videoCaptureConfig);

        //CameraX.bindToLifecycle(this, preview,videoCapture);
    }

    public VideoCapture getVideoCapture() {
        return videoCapture;
    }

    public Preview getPreview() {
        return preview;
    }

    public void bindLifecycle(LifecycleOwner lifecycleOwner) {
        CameraX.bindToLifecycle(lifecycleOwner, preview,videoCapture);
    }
}
