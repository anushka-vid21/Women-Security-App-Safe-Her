package com.devdroid.cardviewexmp;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

public class VideoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        VideoView videoView= findViewById(R.id.videoview);

        String onlinePath ="https://www.w3schools.com/html/mov_bbb.mp4\n ";

        Uri onlineVideoURI = Uri.parse(onlinePath);

        videoView.setVideoURI(onlineVideoURI);
        videoView.start();

        MediaController mediaController=new MediaController(this);
        videoView.setMediaController(mediaController);

        mediaController.setAnchorView(videoView);

    }
}