package com.hust_twj.imageloderlibrary.cache;

import android.graphics.Bitmap;

/**
 * 图片缓存接口
 * 实现类：内存缓存（{@link MemoryCache}）、磁盘缓存（{@link DiskCache}）、双缓存（{@link DoubleCache}）
 *
 * @author hust_twj
 * @date 2019/6/11
 */
public interface BitmapCache {

    Bitmap get(String key);

    void put(String key, Bitmap value);

    void remove(String key);

    void clearCache();
}
