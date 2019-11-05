package com.example.tuananhe.myapplication.record;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.hardware.display.DisplayManager;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import com.example.tuananhe.myapplication.record.encode.MediaAudioEncoder;
import com.example.tuananhe.myapplication.record.encode.MediaEncoder;
import com.example.tuananhe.myapplication.record.encode.MediaMuxerWrapper;
import com.example.tuananhe.myapplication.record.encode.MediaScreenEncoder;
import com.example.tuananhe.myapplication.utils.MediaUtil;
import com.example.tuananhe.myapplication.utils.Settings;

import java.io.IOException;

public class RecordHelper implements MediaEncoder.MediaEncoderListener {

    private Context activity;
    private Point screenPoint;

    private MediaProjectionManager projectionManager;
    private MediaProjection mediaProjection;
    private MediaMuxerWrapper muxer;


    public RecordHelper(Context activity) {
        this.activity = activity;
        screenPoint = new Point();
        projectionManager = (MediaProjectionManager) activity.getSystemService(Context.MEDIA_PROJECTION_SERVICE);
    }

    public void startRecord(Intent intent, int resultCode) {
        if (mediaProjection == null) {
            mediaProjection = projectionManager.getMediaProjection(resultCode, intent);
        }
        startRecording();
    }

    private void startRecording() {
        DisplayManager dm = (DisplayManager) activity.getSystemService(Context.DISPLAY_SERVICE);
        Display defaultDisplay;
        if (dm != null) {
            defaultDisplay = dm.getDisplay(Display.DEFAULT_DISPLAY);
        } else {
            throw new IllegalStateException("Cannot display manager?!?");
        }
        if (defaultDisplay == null) {
            throw new RuntimeException("No display found.");
        }

        Display display = ((WindowManager) activity.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getRealSize(screenPoint);
        display.getRealMetrics(metrics);

        Settings settings = Settings.Companion.getSetting();
        int width = settings.getResolution().getWidth();
        int height = settings.getResolution().getHeight();
        int bitRate = settings.getBitrate();
        int fps = settings.getFps();
        boolean isRecordAudio = settings.isRecordAudio();
        String root = settings.getRootDirectory();

        try {
            if (muxer != null) {
                muxer = null;
            }
            muxer = new MediaMuxerWrapper(root, ".mp4");

            new MediaScreenEncoder(muxer, this, mediaProjection, width, height,
                    metrics.densityDpi, bitRate, fps, screenPoint);
            boolean micAvailable = MediaUtil.Companion.validateMicAvailability();

            if (micAvailable && isRecordAudio) {
                new MediaAudioEncoder(muxer, this);
            }
            muxer.prepare();
            muxer.startRecording();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void pauseRecord() {
        if (muxer != null) {
            muxer.pauseRecording();
        }
    }

    public void resumeRecord() {
        if (muxer != null) {
            muxer.resumeRecording();
        }
    }

    public void stopRecord() {
        if (muxer != null) {
            muxer.stopRecording();
        }
    }


    @Override
    public void onPrepared(MediaEncoder encoder) {

    }

    @Override
    public void onStopped(MediaEncoder encoder) {

    }
}
