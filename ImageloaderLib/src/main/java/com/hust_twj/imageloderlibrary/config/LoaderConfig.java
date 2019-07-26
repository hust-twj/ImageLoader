package com.hust_twj.imageloderlibrary.config;

import com.hust_twj.imageloderlibrary.cache.BitmapCache;
import com.hust_twj.imageloderlibrary.cache.MemoryCache;

/**
 * ImageLoader的配置类
 *
 * @author hust_twj
 * @date 2019/6/11
 */
public class LoaderConfig {

    /**
     * 图片缓存配置对象
     */
    public BitmapCache bitmapCache = new MemoryCache();

    /**
     * 加载图片时的loading和加载失败的图片配置对象
     */
    public DisplayConfig displayConfig = new DisplayConfig();

    /**
     * 线程数
     */
    public int threadCount = Runtime.getRuntime().availableProcessors() + 1;

    public LoaderConfig cache(BitmapCache cache) {
        bitmapCache = cache;
        return this;
    }

    public LoaderConfig threadCount(int count) {
        threadCount = Math.max(1, count);
        return this;
    }

}
