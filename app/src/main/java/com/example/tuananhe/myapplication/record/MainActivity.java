package com.example.tuananhe.myapplication.record;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.example.tuananhe.myapplication.R;
import com.example.tuananhe.myapplication.screen.video.VideoPresenter;

import java.io.File;
import java.io.FileFilter;

public class MainActivity extends AppCompatActivity {

    private final String[] okFileExtensions = new String[]{"jpg", "png", "gif"};

    private static final int PERMISSION_CODE = 218;
    private static final int RECORD_REQUEST_CODE = 101;

    private FileFilter fileFilter = new FileFilter() {
        @Override
        public boolean accept(File file) {
            return file.getName().toLowerCase().endsWith("mp4");
        }
    };

    private String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA};

    private RecordHelper recordHelper;
    private VideoPresenter videoRetriever;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.button_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRecord();
            }
        });
        findViewById(R.id.button_end).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endRecord();
            }
        });
        findViewById(R.id.button_video).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadVideo();
            }
        });

        recordHelper = new RecordHelper(this);
//        videoRetriever = new VideoPresenter();

        if (!hasPermission(permissions)) {
            requestPermission(permissions);
        }
        try {


        }catch (NullPointerException|SecurityException e) {

        }
        String url = "asas fasf fasfasf";

        int a = url.lastIndexOf("?");
    }

    private void loadVideo() {
        File outputFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/video");
        File[] videos = outputFile.listFiles(fileFilter);
        if (videos.length == 0) return;
//        videoRetriever.getVideos(videos);
    }

    private void startRecord() {
        recordHelper.requestRecord(RECORD_REQUEST_CODE);
    }

    private void endRecord() {
        recordHelper.releaseEncoders();
    }

    private boolean hasPermission(String[] permissions) {
        if (permissions == null) {
            return false;
        }
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(MainActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void requestPermission(String... permissions) {
        ActivityCompat.requestPermissions(this, permissions, PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length >= 2) {
                if (grantResults[0] + grantResults[1] + grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Permission NOT granted", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == RECORD_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            recordHelper.startRecord(data, resultCode);
        }
    }


}
