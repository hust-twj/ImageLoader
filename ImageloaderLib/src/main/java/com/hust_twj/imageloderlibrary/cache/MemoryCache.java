package com.hust_twj.imageloderlibrary.cache;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v4.util.LruCache;
import android.util.Log;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 内存缓存（key为图片的uri，值为图片本身）
 *
 * @author hust_twj
 * @date 2019/6/11
 */
public class MemoryCache implements BitmapCache {

    private LruCache<String, Bitmap> mMemoryCache;

    public MemoryCache() {
        //获取应用在系统中分配的总内存
        int totalMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        //取总内存的 1/8 的作为缓存
        int cacheSize = totalMemory / 8; //32768 --> 大约为30M
        Log.e("twjcache", cacheSize + "");
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {

            @Override
            protected int sizeOf(@NonNull String key, @NonNull Bitmap bitmap) {
                //重写sizeOf，计算每张图片的占用字节数
                Log.e("twjcache", "key: " + key + "  " + bitmap.getByteCount() / 1024);
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

    public void clearCache() {
        if (mMemoryCache == null) {
            return;
        }
        Map<String, Bitmap> map = mMemoryCache.snapshot();
        while (map.entrySet().iterator().hasNext()) {
            Map.Entry<String, Bitmap> entry = map.entrySet().iterator().next();
            map.remove(entry.getKey());
            mMemoryCache.remove(entry.getKey());
            Log.e("clearCache", entry.getKey() + "  " + entry.getValue());
        }

    }
}