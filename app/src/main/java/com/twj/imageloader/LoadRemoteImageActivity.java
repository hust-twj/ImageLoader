package com.twj.imageloader;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.hust_twj.imageloderlibrary.ImageLoader;

/**
 * description ：加载网络图片
 * Created by Wenjing.Tang on 2019-05-22.
 */
public class LoadRemoteImageActivity extends AppCompatActivity {

    private ImageView mIvRemote;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_remote_image);

        mIvRemote = findViewById(R.id.iv_image);

        String url = "http://p3-q.mafengwo.net/s12/M00/5F/01/wKgED1va9ZeAf0k5AAijT_WanQ006.jpeg";
        ImageLoader.build(this).bindBitmap(url, mIvRemote);
    }
}
