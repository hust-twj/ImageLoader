package com.twj.imageloader.activity.local;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hust_twj.imageloderlibrary.ImageLoader;
import com.twj.imageloader.R;


/**
 * description ：加载PNG图片
 * Created by Wenjing.Tang on 2019-05-22.
 */
public class LocalAlbumActivity extends AppCompatActivity {

    private TextView mTvAlbum;
    private ImageView mIv;

    private static final int CODE_OPEN_ALBUM = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_album);

        mTvAlbum = findViewById(R.id.tv_album);
        mIv = findViewById(R.id.iv_image);

        mTvAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 23) {
                    if (ContextCompat.checkSelfPermission(LocalAlbumActivity.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(LocalAlbumActivity.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    } else {
                        //跳转到相册
                        openAlbum();
                    }
                } else {
                    //跳转到相册
                    openAlbum();
                }

            }
        });

    }

    /**
     * 打开相册
     */
    public void openAlbum() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, CODE_OPEN_ALBUM);
    }

    /**
     * 打开设置界面
     */
    public void openSetting() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.fromParts("package", getApplication().getPackageName(), null));
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CODE_OPEN_ALBUM:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openAlbum();
                } else {
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(LocalAlbumActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        openSetting();
                    }
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CODE_OPEN_ALBUM && resultCode == RESULT_OK) {
            handleImage(data);
        }
    }

    public void handleImage(Intent data) {
        Uri uri = data.getData();
        if (uri == null) {
            return;
        }
        ImageLoader.get().load(uri.toString()).into(mIv);
    }

}
