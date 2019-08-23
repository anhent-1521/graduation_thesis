package com.example.tuananhe.myapplication.record;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.display.DisplayManager;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Environment;
import android.util.DisplayMetrics;

import java.io.IOException;

public class ScreenRecorder {

    private Activity activity;
    private DisplayMetrics displayMetrics;

    private MediaRecorder mediaRecorder;
    private MediaProjection mediaProjection;
    private MediaProjectionManager mediaProjectionManager;

    public ScreenRecorder(Activity activity, DisplayMetrics displayMetrics) {
        this.activity = activity;
        this.displayMetrics = displayMetrics;
        mediaRecorder = new MediaRecorder();
        mediaProjectionManager = (MediaProjectionManager) activity.getSystemService(Context.MEDIA_PROJECTION_SERVICE);
    }

    public void initMediaRecorder() {
        try {

            CamcorderProfile cpHigh = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
//
            cpHigh.videoFrameWidth = displayMetrics.widthPixels;
            cpHigh.videoFrameHeight = 2340;

            cpHigh.videoBitRate = 6000000;

            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);


            mediaRecorder.setProfile(cpHigh);
//            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);


            mediaRecorder.setOutputFile(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                + "/"
                + System.currentTimeMillis()
                + ".mp4");

//            mediaRecorder.setVideoSize(1152, 2048);
//            mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
//            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
//            mediaRecorder.setVideoEncodingBitRate(3840000);
//            mediaRecorder.setVideoFrameRate(30);

            mediaRecorder.prepare();

        } catch (IllegalStateException | IOException e) {

        }
    }

    public void initVirtualDisplay() {
        if (mediaProjection != null) {
            mediaProjection.createVirtualDisplay("virtual display",
                displayMetrics.widthPixels,
                2340,
                (int) displayMetrics.density,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                mediaRecorder.getSurface(),
                null, null);
        }
    }

    public void startRequestRecord(int requestCode) {
        activity.startActivityForResult(mediaProjectionManager.createScreenCaptureIntent(), requestCode);
    }

    public void startRecord(Intent intent, int resultCode) {
        if (mediaProjection == null) {
            mediaProjection = mediaProjectionManager.getMediaProjection(resultCode, intent);
        }
        initMediaRecorder();
        initVirtualDisplay();

        mediaRecorder.start();
    }

    public void endRecord() {
        mediaRecorder.stop();
        mediaRecorder.release();
        mediaProjection.stop();
        mediaProjection = null;
    }
}
