package com.hust_twj.imageloderlibrary.cache;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import com.hust_twj.imageloderlibrary.utils.IOUtil;
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

    private static final String TAG = DiskCache.class.getSimpleName();

    private static final int MAX_CACHE_SIZE = 100 * 1024 * 1024;

    private static final String IMAGE_DISK_CACHE = "image";

    private DiskLruCache mDiskLruCache;

    private static DiskCache mDiskCache;

    private DiskCache(Context context) {
        initDiskCache(context);
    }

    public static DiskCache getDiskCache(Context context) {
        if (mDiskCache == null) {
            synchronized (DiskCache.class) {
                if (mDiskCache == null) {
                    mDiskCache = new DiskCache(context);
                }
            }
        }
        return mDiskCache;
    }

    /**
     * 初始化sdcard缓存
     */
    private void initDiskCache(Context context) {
        try {
            File cacheDir = getDiskCacheDir(context, IMAGE_DISK_CACHE);
            if (!cacheDir.exists()) {
                cacheDir.mkdirs();
            }
            mDiskLruCache = DiskLruCache
                    .open(cacheDir, getAppVersion(context), 1, MAX_CACHE_SIZE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public File getDiskCacheDir(Context context, String uniqueName) {
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            Log.d(TAG, "dir = " + context.getExternalCacheDir());
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return new File(cachePath + File.separator + uniqueName);
    }

    private int getAppVersion(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(),
                    0);
            return info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 1;
    }

    @Override
    public synchronized Bitmap get(final String key) {
        try {
            DiskLruCache.Snapshot snapshot = mDiskLruCache.get(Md5Utils.toMD5(key));
            if (snapshot != null) {
                InputStream in = snapshot.getInputStream(0);
                return BitmapFactory.decodeStream(in);
            }
        } catch (Exception e) {
            Log.e(TAG, "DiskCache exception: " + e);
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 内存缓存只缓存从网络下载的图片,本地图片不缓存
     */
    @Override
    public void put(final String key, Bitmap value) {
        DiskLruCache.Editor editor;
        try {
            editor = mDiskLruCache.edit(Md5Utils.toMD5(key));
            if (editor != null) {
                OutputStream outputStream = editor.newOutputStream(0);
                if (writeBitmapToDisk(value, outputStream)) {
                    // 写入内存缓存
                    editor.commit();
                } else {
                    editor.abort();
                }
                IOUtil.close(outputStream);
            }
        } catch (Exception e) {
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

    @Override
    public void clearCache() {
        if (mDiskLruCache == null) {
            return;
        }
        try {
            int cacheSize = getCacheSize();
            mDiskLruCache.delete();
            Log.e(TAG, "清除磁盘缓存成功，清除的缓存大小为：" + cacheSize);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取当前磁盘缓存的大小
     */
    private int getCacheSize() {
        int size = 0;
        if (mDiskLruCache != null) {
            size = (int) mDiskLruCache.size();
        }
        return size;
    }

}
