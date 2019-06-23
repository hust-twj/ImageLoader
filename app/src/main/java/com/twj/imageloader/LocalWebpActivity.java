package com.twj.imageloader;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

/**
 * description ：加载PNG图片
 * Created by Wenjing.Tang on 2019-05-22.
 */
public class LocalWebpActivity extends AppCompatActivity {

    private ImageView mIv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_webp);

        mIv = findViewById(R.id.iv_image);

        mIv.setImageResource(R.drawable.qianxun_webp);
    }
}
