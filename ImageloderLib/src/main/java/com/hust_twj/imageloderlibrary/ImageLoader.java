package com.hust_twj.imageloderlibrary;

import android.widget.ImageView;

import com.hust_twj.imageloderlibrary.cache.BitmapCache;
import com.hust_twj.imageloderlibrary.cache.MemoryCache;
import com.hust_twj.imageloderlibrary.config.DisplayConfig;
import com.hust_twj.imageloderlibrary.config.ImageLoaderConfig;
import com.hust_twj.imageloderlibrary.listener.ImageLoadListener;

/**
 * @author hust_twj
 * @data 2019/6/10
 */
public class ImageLoader {

    private static ImageLoader sInstance;

    /**
     * 图片加载配置对象
     */
    private ImageLoaderConfig mConfig;

    private BitmapCache mCache = new MemoryCache();

    public static final int HAHA = 1;

    private ImageLoader() {

    }
    public static ImageLoader getInstance() {
        if (sInstance == null) {
            synchronized (ImageLoader.class) {
                if (sInstance == null) {
                    sInstance = new ImageLoader();
                }
            }
        }
        return sInstance;
    }

    public void init(ImageLoaderConfig config) {
        mConfig = config;
        mCache = config.bitmapCache;
    }

    public void display(ImageView imageView, String uri) {
        display(imageView, uri, null, null);
    }

    public void display(ImageView imageView, String uri, ImageLoadListener listener) {
        display(imageView, uri, null, listener);
    }

    public void display(ImageView imageView, String uri, DisplayConfig displayConfig) {
        display(imageView, uri, displayConfig, null);
    }

    public void display(ImageView imageView, String uri, DisplayConfig displayConfig, ImageLoadListener listener) {

    }


}
