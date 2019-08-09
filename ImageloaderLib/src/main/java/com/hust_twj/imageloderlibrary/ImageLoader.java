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
import com.hust_twj.imageloderlibrary.constant.Constants;
import com.hust_twj.imageloderlibrary.constant.Schema;
import com.hust_twj.imageloderlibrary.listener.ImageLoadListener;
import com.hust_twj.imageloderlibrary.task.LoadRequest;
import com.hust_twj.imageloderlibrary.task.ImageLoadTask;
import com.hust_twj.imageloderlibrary.utils.LoaderProvider;

/**
 * @author hust_twj
 * @data 2019/6/10
 */
public class ImageLoader {

    private static final String TAG = ImageLoader.class.getSimpleName();

    private Context mContext;

    private static volatile ImageLoader sInstance;

    /**
     * 图片加载配置对象
     */
    private LoaderConfig mConfig;

    /**
     * 缓存
     */
    private BitmapCache mCache;

    private DisplayConfig mDisplayConfig;

    private ImageLoadTask mImageLoadTask;

    private LoadRequest mLoadRequest;

    private ImageLoader(Context context) {
        mContext = context.getApplicationContext();
    }

    public static ImageLoader get() {
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
        if (config == null) {
            throw new RuntimeException("config is null");
        }
        mConfig = config;
        mCache = config.bitmapCache;
        mDisplayConfig = config.mDisplayConfig;

        mImageLoadTask = new ImageLoadTask();

        if (mCache == null) {
            mCache = new DoubleCache(mContext);
        }
    }

    /**
     * 加载本地资源图片
     */
    public ImageLoader load(@DrawableRes int resID) {
        //资源图片加载，需要构造前缀
        String uri = Schema.PREFIX_RESOURCE.concat(Schema.SPIT).concat(String.valueOf(resID));
        load(uri);
        return this;
    }

    /**
     * 加载网络或者SD卡中的图片
     */
    public ImageLoader load(String uri) {
        mLoadRequest = new LoadRequest();
        mLoadRequest.setUri(uri);
        return this;
    }

    /**
     * 加载中的占位符
     */
    public ImageLoader placeHolder(@DrawableRes int placeHoldResID) {
        mLoadRequest.setPlaceHolder(placeHoldResID);
        return this;
    }

    /**
     * 加载失败的占位符
     */
    public ImageLoader error(@DrawableRes int errorResID) {
        mLoadRequest.setError(errorResID);
        return this;
    }

    /**
     * 是否显示原图
     *
     * @param displayRaw true：不缩放；false：缩放
     */
    public ImageLoader displayRaw(boolean displayRaw) {
        mLoadRequest.setDisplayRaw(displayRaw);
        return this;
    }

    /**
     * 回调监听
     */
    public ImageLoader listener(ImageLoadListener listener) {
        mLoadRequest.setImageLoadListener(listener);
        return this;
    }

    /**
     * 开始图片加载
     */
    public ImageLoader into(ImageView imageView) {
        setDefaultConfig();
        mLoadRequest.setImageView(imageView);

        // 添加对队列中
        mImageLoadTask.addRequest(mLoadRequest);
        //启动线程池加载图片
        mImageLoadTask.start();
        return this;
    }

    /**
     * 设置默认配置
     */
    private void setDefaultConfig() {
        if (mDisplayConfig == null) {
            return;
        }
        if (mLoadRequest.errorResID == Constants.DEFAULT_RES_ID && mDisplayConfig.errorResId > 0) {
            mLoadRequest.setError(mDisplayConfig.errorResId);
        }
        if (mLoadRequest.placeHolderResID == Constants.DEFAULT_RES_ID && mDisplayConfig.placeHolderResId > 0) {
            mLoadRequest.setPlaceHolder(mDisplayConfig.placeHolderResId);
        }
        mLoadRequest.setDisplayRaw(mDisplayConfig.displayRaw);

        if (mLoadRequest.defaultWidth == 0 && mDisplayConfig.defaultWidth > 0) {
            mLoadRequest.setDefaultWidth(mDisplayConfig.defaultWidth);
        }

        if (mLoadRequest.defaultHeight == 0 && mDisplayConfig.defaultHeight > 0) {
            mLoadRequest.setDefaultHeight(mDisplayConfig.defaultHeight);
        }
    }

    public LoaderConfig getConfig() {
        return mConfig;
    }

    /**
     * 取消图片加载请求
     */
    public void stop() {
        mImageLoadTask.stop();
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
            ((DoubleCache) mCache).clearMemoryCache();
        } else if (mCache instanceof MemoryCache) {
            mCache.clearCache();
        }
    }

    /**
     * 清除磁盘缓存
     */
    public void clearDiskCache() {
        if (mCache instanceof DoubleCache) {
            ((DoubleCache) mCache).clearDiskCache();
        } else if (mCache instanceof DiskCache) {
            mCache.clearCache();
        }
    }

}
