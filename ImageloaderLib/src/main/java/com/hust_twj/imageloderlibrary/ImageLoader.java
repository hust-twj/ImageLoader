package com.hust_twj.imageloderlibrary;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.widget.ImageView;

import com.hust_twj.imageloderlibrary.cache.BitmapCache;
import com.hust_twj.imageloderlibrary.cache.DiskCache;
import com.hust_twj.imageloderlibrary.cache.DoubleCache;
import com.hust_twj.imageloderlibrary.cache.MemoryCache;
import com.hust_twj.imageloderlibrary.config.DisplayConfig;
import com.hust_twj.imageloderlibrary.config.LoaderConfig;
import com.hust_twj.imageloderlibrary.constant.Schema;
import com.hust_twj.imageloderlibrary.listener.ImageLoadListener;
import com.hust_twj.imageloderlibrary.task.LoadRequest;
import com.hust_twj.imageloderlibrary.task.LoadTask;
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

    private LoadTask mLoadTask;

    private LoadRequest mLoadRequest;

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
        mLoadTask = new LoadTask();

        if (mConfig == null) {
            throw new RuntimeException("config is null");
        }
        if (mCache == null) {
            mCache = new MemoryCache();
        }
    }

    public ImageLoader load(@DrawableRes int resID) {
        //资源图片加载，需要构造前缀
        String uri = Schema.PREFIX_RESOURCE.concat(Schema.SPIT).concat(String.valueOf(resID));
        load(uri);
        return this;
    }

    public ImageLoader load(String uri) {
        mLoadRequest = new LoadRequest();
        mLoadRequest.setUri(uri);
        return this;
    }

    public ImageLoader error(@DrawableRes int errorResID) {
        DisplayConfig displayConfig = mLoadRequest.mDisplayConfig != null ? mLoadRequest.mDisplayConfig
                : new DisplayConfig();
        displayConfig.errorResId = errorResID;
        mLoadRequest.setDisplayConfig(displayConfig);
        return this;
    }

    public ImageLoader placeHolder(@DrawableRes int placeHoldResID) {
        DisplayConfig displayConfig = mLoadRequest.mDisplayConfig != null ? mLoadRequest.mDisplayConfig
                : new DisplayConfig();
        displayConfig.placeHolderResId = placeHoldResID;
        mLoadRequest.setDisplayConfig(displayConfig);
        return this;
    }

    public ImageLoader listener(ImageLoadListener listener) {
        mLoadRequest.setImageLoadListener(listener);
        return this;
    }

    public ImageLoader into(ImageView imageView) {
        mLoadRequest.setImageView(imageView);

        // 添加对队列中
        mLoadTask.addRequest(mLoadRequest);
        //启动线程池加载图片
        mLoadTask.start();
        return this;
    }

    public LoaderConfig getConfig() {
        return mConfig;
    }

    public void stop() {
        mLoadTask.stop();
    }

    /**
     * 清除全部缓存
     */
    public void clearCache() {
        if (mCache != null) {
            mCache.clearCache();
        }
    }

    /**
     * 清除内存缓存
     */
    public void clearMemoryCache() {
        if (mCache instanceof DoubleCache) {
            ((DoubleCache)mCache).clearMemoryCache();
        } else if (mCache instanceof MemoryCache) {
            mCache.clearCache();
        }
    }

    /**
     * 清除磁盘缓存
     */
    public void clearDiskCache() {
        if (mCache instanceof DoubleCache) {
            ((DoubleCache)mCache).clearDiskCache();
        } else if (mCache instanceof DiskCache) {
            mCache.clearCache();
        }
    }

}
