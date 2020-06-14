package com.share.in.main;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.VideoView;

import com.share.in.R;

public class FullAudioActivity extends Activity {

    String str_video;
    ImageView vv_video;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_audio);
        init();
    }

    private void init() {

        vv_video = (ImageView) findViewById(R.id.image);

        str_video = getIntent().getStringExtra("image");


    }


}