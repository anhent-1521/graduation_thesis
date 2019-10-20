package com.example.tuananhe.myapplication.record;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.hardware.display.DisplayManager;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Surface;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

public class RecordHelperTest {

    private static final String VIDEO_MIME_TYPE = "video/avc";

    private Activity activity;
    private Point screenPoint;

    private MediaMuxer mediaMuxer;
    private MediaCodec videoEncoder;
    private Surface inputSurface;

    private MediaProjectionManager projectionManager;
    private MediaProjection mediaProjection;

    private boolean muxerStarted;
    private int trackIndex = -1;

    private android.media.MediaCodec.Callback encoderCallback;

    public RecordHelperTest(Activity activity) {
        this.activity = activity;
        screenPoint = new Point();
        projectionManager = (MediaProjectionManager) activity.getSystemService(Context.MEDIA_PROJECTION_SERVICE);
    }

    public void requestRecord(int requestCode) {
        activity.startActivityForResult(projectionManager.createScreenCaptureIntent(), requestCode);
    }

    private void initCallback() {
        encoderCallback = new MediaCodec.Callback() {
            @Override
            public void onInputBufferAvailable(@NonNull MediaCodec codec, int index) {
                Log.d("-----", "Input Buffer Avail");
            }

            @Override
            public void onOutputBufferAvailable(@NonNull MediaCodec codec, int index, @NonNull MediaCodec.BufferInfo info) {
                Log.d("-----", "onOutputBufferAvailable: " + index);
                ByteBuffer encodedData = videoEncoder.getOutputBuffer(index);
                if (encodedData == null) {
                    throw new RuntimeException("couldn't fetch buffer at index " + index);
                }

                if ((info.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
                    info.size = 0;
                }

                if (info.size != 0) {
                    if (muxerStarted) {
                        encodedData.position(info.offset);
                        encodedData.limit(info.offset + info.size);
                        mediaMuxer.writeSampleData(trackIndex, encodedData, info);
                    }
                }

                videoEncoder.releaseOutputBuffer(index, false);

            }

            @Override
            public void onError(@NonNull MediaCodec codec, @NonNull MediaCodec.CodecException e) {
                Log.e("-----", "MediaCodec " + codec.getName() + " onError:", e);
            }

            @Override
            public void onOutputFormatChanged(@NonNull MediaCodec codec, @NonNull MediaFormat format) {
                Log.d("-----", "Output Format changed");
                if (trackIndex >= 0) {
                    throw new RuntimeException("format changed twice");
                }
                trackIndex = mediaMuxer.addTrack(videoEncoder.getOutputFormat());
                if (!muxerStarted && trackIndex >= 0) {
                    mediaMuxer.start();
                    muxerStarted = true;
                }
            }
        };
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

        // Lấy thông tin kích thước và tỉ lệ màn hình
        Display display = activity.getWindowManager().getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getRealSize(screenPoint);
        display.getRealMetrics(metrics);

        // Khởi tạo các thành phần
        initCallback();
        prepareVideoEncoder(screenPoint.x , screenPoint.y);
        initMuxer();
        initVirtualDisplay(metrics);
    }

    private void initMuxer() {
        try {
            File outputFile = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS) + "/video", "Screen-record-" +
                Long.toHexString(System.currentTimeMillis()) + ".mp4");
            if (!outputFile.getParentFile().exists()) {
                outputFile.getParentFile().mkdirs();
            }
            mediaMuxer = new MediaMuxer(outputFile.getCanonicalPath(), MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
        } catch (IOException ioe) {
            throw new RuntimeException("MediaMuxer creation failed", ioe);
        }
    }

    private void initVirtualDisplay(DisplayMetrics metrics) {
        // Start the video input.
        mediaProjection.createVirtualDisplay("Recording Display", screenPoint.x,
            screenPoint.y, metrics.densityDpi, DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR/* flags */, inputSurface,
            null /* callback */, null /* handler */);
    }

    private void prepareVideoEncoder(int width, int height) {
        MediaFormat format = MediaFormat.createVideoFormat(VIDEO_MIME_TYPE, width, height);
        int frameRate = 30; // 30 fps

        // Set some required properties. The media codec may fail if these aren't defined.
        //For Surface input, you must set the color format to COLOR_FormatSurface (also known as AndroidOpaque.)
        format.setInteger(MediaFormat.KEY_COLOR_FORMAT,
            MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
        format.setInteger(MediaFormat.KEY_BIT_RATE, 6000000); // 6Mbps
        format.setInteger(MediaFormat.KEY_FRAME_RATE, frameRate);
        format.setInteger(MediaFormat.KEY_CAPTURE_RATE, frameRate);
        format.setInteger(MediaFormat.KEY_REPEAT_PREVIOUS_FRAME_AFTER, 1000000 / frameRate);
        format.setInteger(MediaFormat.KEY_CHANNEL_COUNT, 1);
        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1); // 1 seconds between I-frames

        // Create a MediaCodec encoder and configure it. Get a Surface we can use for recording into.
        try {
            videoEncoder = MediaCodec.createEncoderByType(VIDEO_MIME_TYPE);
            videoEncoder.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
            inputSurface = videoEncoder.createInputSurface();
            videoEncoder.setCallback(encoderCallback);
            videoEncoder.start();
        } catch (IOException e) {
            releaseEncoders();
        }
    }

    public void releaseEncoders() {
        if (mediaMuxer != null) {
            if (muxerStarted) {
                mediaMuxer.stop();
            }
            mediaMuxer.release();
            mediaMuxer = null;
            muxerStarted = false;
        }
        if (videoEncoder != null) {
            videoEncoder.stop();
            videoEncoder.release();
            videoEncoder = null;
        }
        if (inputSurface != null) {
            inputSurface.release();
            inputSurface = null;
        }
        if (mediaProjection != null) {
            mediaProjection.stop();
            mediaProjection = null;
        }
        trackIndex = -1;
    }

    private void stopRecording() {
        releaseEncoders();
    }

}
