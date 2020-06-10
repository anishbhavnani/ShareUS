package com.share.in.main;

import android.app.Activity;
import android.os.Bundle;
import android.widget.VideoView;

import com.share.in.R;

public class FullVideoActivity extends Activity {

    String str_video;
    VideoView vv_video;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_video);
        init();
    }

    private void init() {

        vv_video = (VideoView) findViewById(R.id.image);

        str_video = getIntent().getStringExtra("image");
        vv_video.setVideoPath(str_video);
        vv_video.start();

    }


}