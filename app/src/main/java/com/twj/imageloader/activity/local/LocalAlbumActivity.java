package com.twj.imageloader.activity.local;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
                    if (ContextCompat.checkSelfPermission(LocalAlbumActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(LocalAlbumActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
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

    /**
     * 根据Uri来获取到图片对应的真实路径
     */
    public String getImagePath(Uri uri, String selection) {
        String path = null;
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor == null) {
            return null;
        }
        if (cursor.moveToFirst()) {
            try {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        cursor.close();

        return path;
    }

    /**
     * 展示图片
     *
     * @param path
     */

    public void displayImage(String path) {
        if (path != null) {
            ImageLoader.with(this).load(path, mIv);
           /* Bitmap bitmap = BitmapFactory.decodeFile(path);
            mIv.setImageBitmap(bitmap);*/
        } else {
            Toast.makeText(this, "图片地址为空", Toast.LENGTH_SHORT).show();
        }
    }

    @TargetApi(19)
    public void handleImage(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        if (uri == null) {
            return;
        }
        ImageLoader.with(this).load(uri.toString(), mIv);
       // LiteImageLoader.getInstance().displayImage(mIv, uri.toString());
        /*if (DocumentsContract.isDocumentUri(LocalAlbumActivity.this, uri)) {
            //如果是document类型的Uri，那么通过document的id来处理
            String documentId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = documentId.split(":")[1];
                //等价于下面两句代码
               *//* String [] array = documentId.split(":");
                String id = array[1];*//*

                String selection = MediaStore.Images.Media._ID + "=?";
                String[] selectionArgs = {id};
                imagePath = getDataColumn(this, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection, selectionArgs);

                //String selection = MediaStore.Images.Media._ID + "=" + id;
                //imagePath = getImagePath(uri, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(documentId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            //如果是content类型的uri那么使用正常的uri
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            //如果uri是file 那么直接获取文件的路径
            imagePath = uri.getPath();
        }
        displayImage(imagePath);*/
    }

    /**
     * 获取数据库表中的 _data 列，即返回Uri对应的文件路径
     * @return
     */
    private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        String path = null;

        String[] projection = new String[]{MediaStore.Images.Media.DATA};
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow(projection[0]);
                path = cursor.getString(columnIndex);
            }
        } catch (Exception e) {
            if (cursor != null) {
                cursor.close();
            }
        }
        return path;
    }

}
