package com.hust_twj.imageloderlibrary.cache;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import com.hust_twj.imageloderlibrary.utils.Md5Utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Description ：磁盘缓存
 * Created by Wenjing.Tang on 2019-06-16.
 * <p>
 * 参考：https://www.jianshu.com/p/f9cfbea586c2
 */
public class DiskCache implements BitmapCache {

    private static final String TAG = "DiskCache";

    private DiskLruCache mDiskLruCache;

    private static final String IMAGE_DISK_CACHE = "bitmap";
    /**
     * 缓存最大值
     */
    private static final int MAX_DISK_CACHE_SIZE = 50 * 1024 * 1024;

    private static DiskCache mInstance;

    public DiskCache(Context context) {
        initDiskCache(context);
    }

    public static DiskCache getInstance(Context context) {
        if (mInstance == null) {
            synchronized (DiskCache.class) {
                if (mInstance == null) {
                    mInstance = new DiskCache(context);
                }
            }
        }
        return mInstance;
    }

    private void initDiskCache(Context context) {
        try {
            File cacheDir = getDiskCacheDir(context, IMAGE_DISK_CACHE);
            if (!cacheDir.exists()) {
                cacheDir.mkdirs();
            }
            mDiskLruCache = DiskLruCache.open(cacheDir, getAppVersion(context),
                    1, MAX_DISK_CACHE_SIZE);
        } catch (IOException e) {
            Log.e(TAG,"initDiskCache Exception: " + e);
            e.printStackTrace();
        }
    }

    private int getAppVersion(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return info.versionCode;
        } catch (Exception e) {
            Log.e(TAG,"getAppVersion Exception: " + e);
            e.printStackTrace();
        }
        return 1;
    }

    /**
     * 获取内存缓存路径
     * external：如：/storage/emulated/0/Android/data/package_name/cache
     * internal 如：/data/data/package_name/cache
     *
     * @param context    context
     * @param uniqueName 缓存后缀
     */
    public static File getDiskCacheDir(Context context, String uniqueName) {
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                && context.getExternalCacheDir() != null) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return new File(cachePath + File.separator + uniqueName);
    }

    @Override
    public Bitmap get(final String key) {
        String md5Key;
        try {
            md5Key = Md5Utils.toMD5(key);
            DiskLruCache.Snapshot snapshot = mDiskLruCache.get(md5Key);
            if (snapshot != null) {
                InputStream in = snapshot.getInputStream(0);
                return BitmapFactory.decodeStream(in);
            }
        } catch (IOException e) {
            Log.e(TAG,"DiskCache get() Exception: " + e);
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
        DiskLruCache.Editor editor;
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
            Log.e(TAG,"DiskCache put() Exception: " + e);
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
            Log.e(TAG, "writeBitmapToDisk failed ." + e);
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
            Log.e(TAG,"DiskCache remove Exception: " + e);
            e.printStackTrace();
        }
    }

}
