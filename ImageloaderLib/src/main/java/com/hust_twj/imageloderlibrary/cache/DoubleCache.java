package com.hust_twj.imageloderlibrary.cache;

import android.content.Context;
import android.graphics.Bitmap;


/**
 * 内存和sd卡双缓存
 * Created by Wenjing.Tang on 2019-06-16.
 */
public class DoubleCache implements BitmapCache {

    private DiskCache mDiskCache;

    private MemoryCache mMemoryCache = new MemoryCache();

    public DoubleCache(Context context) {
        mDiskCache = DiskCache.getDiskCache(context);
    }

    @Override
    public Bitmap get(String key) {
        Bitmap value = mMemoryCache.get(key);
        if (value == null) {
            value = mDiskCache.get(key);
            saveBitmapIntoMemory(key, value);
        }
        return value;
    }

    private void saveBitmapIntoMemory(String key, Bitmap bitmap) {
        // 如果Value从disk中读取，那么存入内存缓存
        if (bitmap != null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    @Override
    public void put(String key, Bitmap value) {
        mDiskCache.put(key, value);
        mMemoryCache.put(key, value);
    }

    @Override
    public void remove(String key) {
        mDiskCache.remove(key);
        mMemoryCache.remove(key);
    }

    @Override
    public void clearCache() {
        mMemoryCache.clearCache();
        mDiskCache.clearCache();
    }

    public void clearMemoryCache() {
        mMemoryCache.clearCache();
    }

    public void clearDiskCache() {
        mDiskCache.clearCache();
    }

}
