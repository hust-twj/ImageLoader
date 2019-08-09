package com.hust_twj.imageloderlibrary.config;

import com.hust_twj.imageloderlibrary.cache.BitmapCache;

/**
 * ImageLoader的配置类
 *
 * @author hust_twj
 * @date 2019/7/11
 */
public class LoaderConfig {

    /**
     *显示配置
     */
    public DisplayConfig mDisplayConfig;

    /**
     * 图片缓存配置
     */
    public BitmapCache bitmapCache;

    /**
     * 线程数
     */
    public int threadCount = Runtime.getRuntime().availableProcessors() + 1;

    public LoaderConfig displayConfig(DisplayConfig displayConfig) {
        mDisplayConfig = displayConfig;
        return this;
    }

    public LoaderConfig cache(BitmapCache cache) {
        bitmapCache = cache;
        return this;
    }

    public LoaderConfig threadCount(int count) {
        threadCount = Math.max(1, count);
        return this;
    }

}
