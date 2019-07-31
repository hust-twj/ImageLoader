package com.hust_twj.imageloderlibrary.loader;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.net.Uri;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import com.hust_twj.imageloderlibrary.constant.Schema;
import com.hust_twj.imageloderlibrary.task.LoadRequest;
import com.hust_twj.imageloderlibrary.utils.BitmapDecoder;

import java.io.File;

/**
 * 本地sd卡图片加载器
 * Created by Wenjing.Tang on 2019-07-16.
 */
public class LocalLoadStrategy extends BaseLoadStrategy {

    @Override
    public Bitmap onLoadImage(LoadRequest request) {
        final String imagePath = getPath(request.mImageView.getContext(), request.uri);
        final File imgFile = new File(imagePath);
        if (!imgFile.exists()) {
            return null;
        }

        // 从sd卡中加载的图片仅缓存到内存中,不做本地缓存
        request.onlyCacheMemory = true;

        // 加载图片
        BitmapDecoder decoder = new BitmapDecoder() {

            @Override
            public Bitmap decodeBitmapWithOption(Options options) {
                return BitmapFactory.decodeFile(imagePath, options);
            }
        };
        return decoder.decodeBitmap(request.getImageViewWidth(), request.getImageViewHeight());
    }

    private String getPath(Context context, String uriString) {
        String imagePath = "";
        Uri uri = Uri.parse(uriString);
        if (DocumentsContract.isDocumentUri(context, uri)) {
            //如果是document类型的Uri，那么通过document的id来处理
            String documentId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = documentId.split(":")[1];
                //等价于下面两句代码
                String selection = MediaStore.Images.Media._ID + "=?";
                String[] selectionArgs = {id};
                imagePath = getDataColumn(context, MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        selection, selectionArgs);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),
                        Long.valueOf(documentId));
                imagePath = getImagePath(context, contentUri, null);
            }
        } else if (Schema.PREFIX_CONTENT.equalsIgnoreCase(uri.getScheme())) {
            //如果是content类型的uri那么使用正常的uri
            imagePath = getImagePath(context, uri, null);
        } else if (Schema.PREFIX_FILE.equalsIgnoreCase(uri.getScheme())) {
            //如果uri是file 那么直接获取文件的路径
            imagePath = uri.getPath();
        }
        return imagePath;
    }

    /**
     * 根据Uri来获取到图片对应的真实路径
     */
    private String getImagePath(Context context, Uri uri, String selection) {
        String path = null;
        Cursor cursor = context.getContentResolver().query(uri, null,
                selection, null, null);
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
     * 获取数据库表中的 _data 列，即返回Uri对应的文件路径
     *
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
