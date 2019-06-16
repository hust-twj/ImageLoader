package com.hust_twj.imageloderlibrary.cache;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.hust_twj.imageloderlibrary.utils.BitmapDecoder;
import com.hust_twj.imageloderlibrary.utils.Md5Utils;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Description ：磁盘缓存
 * Created by Wenjing.Tang on 2019-06-16.
 */
public class DiskCache implements BitmapCache {

    private DiskLruCache mDiskLruCache;

    public DiskCache(Context context) {

    }

    @Override
    public Bitmap get(final String key) {
        return null;
        // 图片解析器
       /* BitmapDecoder decoder = new BitmapDecoder() {

            @Override
            public Bitmap decodeBitmapWithOption(BitmapFactory.Options options) {
                final InputStream inputStream = getInputStream(Md5Utils.toMD5(key));
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null,
                        options);
                IOUtil.closeQuietly(inputStream);
                return bitmap;
            }
        };

        return decoder.decodeBitmap(bean.getImageViewWidth(),
                bean.getImageViewHeight());*/
    }

    private InputStream getInputStream(String md5) {
        DiskLruCache.Snapshot snapshot;
        try {
            snapshot = mDiskLruCache.get(md5);
            if (snapshot != null) {
                return snapshot.getInputStream(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 内存缓存只缓存从网络下载的图片
     *
     * @param key   key
     * @param value value
     */
    @Override
    public void put(String key, Bitmap value) {
        DiskLruCache.Editor editor = null;
        try {
            // 如果没有找到对应的缓存，则准备从网络上请求数据，并写入缓存
            editor = mDiskLruCache.edit(Md5Utils.toMD5(key));
            if (editor != null) {
                OutputStream outputStream = editor.newOutputStream(0);
                if (writeBitmapToDisk(value, outputStream)) {
                    // 写入disk缓存
                    editor.commit();
                } else {
                    editor.abort();
                }
                IOUtil.closeQuietly(outputStream);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean writeBitmapToDisk(Bitmap bitmap, OutputStream outputStream) {
        BufferedOutputStream bos = new BufferedOutputStream(outputStream, 8 * 1024);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        boolean result = true;
        try {
            bos.flush();
        } catch (IOException e) {
            e.printStackTrace();
            result = false;
        } finally {
            IOUtil.closeQuietly(bos);
        }
        return result;
    }

    @Override
    public void remove(String key) {
        try {
            mDiskLruCache.remove(Md5Utils.toMD5(key));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
