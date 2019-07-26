package com.hust_twj.imageloderlibrary;

import android.content.Context;
import android.widget.ImageView;

import com.hust_twj.imageloderlibrary.cache.BitmapCache;
import com.hust_twj.imageloderlibrary.cache.MemoryCache;
import com.hust_twj.imageloderlibrary.config.DisplayConfig;
import com.hust_twj.imageloderlibrary.config.LoaderConfig;
import com.hust_twj.imageloderlibrary.listener.ImageLoadListener;
import com.hust_twj.imageloderlibrary.request.LoadRequest;
import com.hust_twj.imageloderlibrary.request.RequestQueue;
import com.hust_twj.imageloderlibrary.utils.LoaderProvider;

/**
 * @author hust_twj
 * @data 2019/6/10
 */
public class ImageLoader {

    private static final String TAG = ImageLoader.class.getSimpleName();

    private static volatile ImageLoader sInstance;

    /**
     * 图片加载配置对象
     */
    private LoaderConfig mConfig;

    /**
     * 缓存
     */
    private volatile BitmapCache mCache = new MemoryCache();

    private RequestQueue mRequestQueue;

    private LoadRequest mLoadRequest/* = new LoadRequest()*/;

    private ImageLoader(Context context) {
        //mContext = context.getApplicationContext();
    }

    public static ImageLoader with() {
        if (sInstance == null) {
            synchronized (ImageLoader.class) {
                if (sInstance == null) {
                    if (LoaderProvider.mContext == null) {
                        throw new IllegalStateException("context is null");
                    }
                    sInstance = new ImageLoader(LoaderProvider.mContext);
                }
            }
        }
        return sInstance;
    }

    public void init(LoaderConfig config) {
        mConfig = config;
        mCache = config.bitmapCache;
        mRequestQueue = new RequestQueue();

        if (mConfig == null) {
            throw new RuntimeException("config is null");
        }
        //if (mConfig.)
        if (mCache == null) {
            mCache = new MemoryCache();
        }
    }

    public void load(int resID, ImageView imageView) {

        //load(uri, imageView);
    }

    public void load(String uri, ImageView imageView) {
        load(uri, imageView, null, null);
    }

    public void load(String uri, ImageView imageView, DisplayConfig config) {
        load(uri, imageView, config, null);
    }

    public void load(String uri, ImageView imageView, ImageLoadListener listener) {
        load(uri, imageView, null, listener);
    }


    public void load(String uri, ImageView imageView, DisplayConfig config, ImageLoadListener listener) {
         LoadRequest mLoadRequest = new LoadRequest(imageView, uri, config, listener);
//
     /*   mLoadRequest.setImageView(imageView)
                .setUri(uri)
                .setDisplayConfig(config)
                .setImageLoadListener(listener);
        //mLoaderRequest.mDisplayConfig = config != null ? config : mConfig.displayConfig;
        mLoadRequest.mDisplayConfig = mLoadRequest.mDisplayConfig != null ? mLoadRequest.mDisplayConfig
                : mConfig.displayConfig;*/
        // 添加对队列中
        mRequestQueue.addRequest(mLoadRequest);
        //启动线程池加载图片
        mRequestQueue.start();
    }
    /*public synchronized ImageLoader load(int resID) {
        //资源图片加载，需要构造前缀
        String uri = Schema.PREFIX_RESOURCE.concat(Schema.SPIT).concat(String.valueOf(resID));
        mLoadRequest.setUri(uri);
        return this;
    }

    public synchronized ImageLoader load(String uri) {
        mLoadRequest.setUri(uri);
        return this;
    }

    public synchronized ImageLoader error(int errorResID) {
        *//*DisplayConfig displayConfig = mLoadRequest.mDisplayConfig != null ? mLoadRequest.mDisplayConfig
                : new DisplayConfig();
        displayConfig.errorResId = errorResID;
        mLoadRequest.setDisplayConfig(displayConfig);*//*
        return this;
    }

    public synchronized ImageLoader placeHolder(int placeHoldResID) {
       *//* DisplayConfig displayConfig = mLoadRequest.mDisplayConfig != null ? mLoadRequest.mDisplayConfig
                : new DisplayConfig();
        displayConfig.placeHolderResId = placeHoldResID;
        mLoadRequest.setDisplayConfig(displayConfig);*//*
        return this;
    }

    public synchronized ImageLoader listener(ImageLoadListener listener) {
        mLoadRequest.setImageLoadListener(listener);
        return this;
    }

    public synchronized ImageLoader into(ImageView imageView) {
        mLoadRequest.setImageView(imageView);

        // 添加对队列中
        mRequestQueue.addRequest(mLoadRequest);
        //启动线程池加载图片
        mRequestQueue.start();
        return this;
    }*/

    public LoaderConfig getConfig() {
        return mConfig;
    }

    public void stop() {
        mRequestQueue.stop();
    }

    public void clearCache() {
        if (mCache != null) {
            mCache.clearCache();
        }
    }

}
