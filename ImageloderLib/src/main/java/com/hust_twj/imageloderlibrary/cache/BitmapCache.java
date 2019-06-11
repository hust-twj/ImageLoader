package com.hust_twj.imageloderlibrary.cache;

import android.graphics.Bitmap;

/**
 * 图片缓存接口，具体实现类为：无缓存、内存缓存、磁盘缓存
 * @author hust_twj
 * @date 2019/6/11
 */
public interface BitmapCache {

    Bitmap get(String key);

    void put(String key, Bitmap value);

    void remove(String key);
}
