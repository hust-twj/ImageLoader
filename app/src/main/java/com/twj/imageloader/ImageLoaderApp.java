package com.twj.imageloader;

import android.app.Application;

import com.hust_twj.imageloderlibrary.ImageLoader;
import com.hust_twj.imageloderlibrary.cache.DoubleCache;
import com.hust_twj.imageloderlibrary.config.LoaderConfig;

/**
 * Description ：
 * Created by Wenjing.Tang on 2019-07-26.
 */
public class ImageLoaderApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        initImageLoader();
    }

    /**
     * 初始化ImageLoader
     */
    private void initImageLoader() {
        LoaderConfig config = new LoaderConfig()
                .cache(new DoubleCache(this))
                .threadCount(4);
        ImageLoader.with().init(config);

    }

}
