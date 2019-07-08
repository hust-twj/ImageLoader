package com.twj.imageloader.activity.local;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.hust_twj.imageloderlibrary.ImageLoader;
import com.twj.imageloader.R;

/**
 * description ：加载PNG图片
 * Created by Wenjing.Tang on 2019-05-22.
 */
public class LocalPngActivity extends AppCompatActivity {

    private ImageView mIv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_png);

        mIv = findViewById(R.id.iv_image);

        ImageLoader.with(this).load(R.drawable.qianxun_png, mIv);

    }
}
