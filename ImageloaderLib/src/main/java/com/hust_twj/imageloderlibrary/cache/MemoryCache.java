package com.hust_twj.imageloderlibrary.cache;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v4.util.LruCache;
import android.util.Log;


/**
 * 内存缓存（key为图片的uri，值为图片本身）
 *
 * @author hust_twj
 * @date 2019/6/11
 */
public class MemoryCache implements BitmapCache {

    private static final String TAG = "MemoryCache";
    private LruCache<String, Bitmap> mMemoryCache;

    public MemoryCache() {
        //获取应用在系统中分配的总内存
        int totalMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        //取总内存的 1/8 的作为缓存
        int cacheSize = totalMemory / 8; //32768 --> 大约为30M
        Log.e(TAG, cacheSize + "");
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {

            @Override
            protected int sizeOf(@NonNull String key, @NonNull Bitmap bitmap) {
                //重写sizeOf，计算每张图片的占用字节数
                Log.e(TAG, "key: " + key + "  " + bitmap.getByteCount() / 1024);
                return bitmap.getByteCount() / 1024;
            }
        };
    }

    @Override
    public Bitmap get(String key) {
        return mMemoryCache.get(key);
    }

    @Override
    public void put(String key, Bitmap value) {
        mMemoryCache.put(key, value);
    }

    @Override
    public void remove(String key) {
        mMemoryCache.remove(key);
    }

    @Override
    public void clearCache() {
        if (mMemoryCache != null) {
            int cacheSize = getCacheSize();
            mMemoryCache.evictAll();
            Log.e(TAG, "清除内存缓存成功，清除的缓存大小为：" + cacheSize);

        }
    }

    /**
     * 获取当前内存缓存的大小
     */
    private int getCacheSize() {
        int size = 0;
        if (mMemoryCache != null)
            size += mMemoryCache.size();
        return size;
    }

}
