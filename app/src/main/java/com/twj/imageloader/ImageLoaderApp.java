package com.twj.imageloader;

import android.app.Application;

import com.hust_twj.imageloderlibrary.ImageLoader;
import com.hust_twj.imageloderlibrary.cache.DoubleCache;
import com.hust_twj.imageloderlibrary.config.ImageLoaderConfig;

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
        ImageLoaderConfig config = new ImageLoaderConfig()
                .placeHolder(R.drawable.img_loading)
                .error(R.drawable.img_error)
                .displayRaw(true)
                .cache(new DoubleCache(this))
                .threadCount(4);
        ImageLoader.with().init(config);

    }

}
