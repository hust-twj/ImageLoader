package com.hust_twj.imageloderlibrary;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

import com.hust_twj.imageloderlibrary.cache.DiskCache;
import com.hust_twj.imageloderlibrary.cache.IOUtil;
import com.hust_twj.imageloderlibrary.cache.MemoryCache;
import com.hust_twj.imageloderlibrary.config.DisplayConfig;
import com.hust_twj.imageloderlibrary.config.ImageLoaderConfig;
import com.hust_twj.imageloderlibrary.listener.ImageLoadListener;
import com.hust_twj.imageloderlibrary.utils.ImageResizer;
import com.hust_twj.imageloderlibrary.utils.Md5Utils;

import java.io.BufferedInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

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

    private MemoryCache mMemoryCache;
    private DiskCache mDiskCache;

    private static final String TAG = "ImageLoader";

    public static final int MESSAGE_POST_RESULT = 1;

    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();

    private static final int CORE_POOL_SIZE = CPU_COUNT + 1;
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
    private static final long KEEP_ALIVE = 10L;

    private static final int TAG_KEY_URI = R.id.image_loader_uri;
    private static final long DISK_CACHE_SIZE = 1024 * 1024 * 50;
    private static final int IO_BUFFER_SIZE = 8 * 1024;
    private static final int DISK_CACHE_INDEX = 0;
    private boolean mIsDiskLruCacheCreated = false;

    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger();

        @Override
        public Thread newThread(Runnable r) {
            // TODO Auto-generated method stub
            return new Thread(r, "ImageLoader#" + mCount.getAndIncrement());
        }
    };

    public static final Executor THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(
            CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE, TimeUnit.SECONDS,
            new LinkedBlockingDeque<Runnable>(), sThreadFactory);

    private Handler mMainHandler = new Handler(Looper.getMainLooper()) {

        @Override
        public void handleMessage(Message msg) {
            LoaderResult result = (LoaderResult) msg.obj;
            ImageView imageView = result.imageView;
            imageView.setImageBitmap(result.bitmap);
            String uri = (String) imageView.getTag(TAG_KEY_URI);
            if (uri.equals(result.uri)) {
                imageView.setImageBitmap(result.bitmap);
            } else {
                Log.w(TAG, "set image bitmap,but url has changed , ignored!");
            }
        }

    };

    private Context mContext;
    private ImageResizer mImageResizer = new ImageResizer();

    private ImageLoader(Context context) {
        mContext = context.getApplicationContext();
        init();
    }

    public static ImageLoader build(Context context) {
        return new ImageLoader(context);
    }

    /*public static ImageLoader getInstance() {
        if (sInstance == null) {
            synchronized (ImageLoader.class) {
                if (sInstance == null) {
                    sInstance = new ImageLoader();
                }
            }
        }
        return sInstance;
    }*/

    public void init(/*ImageLoaderConfig config*/) {
        //mConfig = config;
        ///mMemoryCache = config.bitmapCache;

        if (mMemoryCache == null) {
            mMemoryCache = new MemoryCache();
        }
        if (mDiskCache == null) {
            mDiskCache = new DiskCache(mContext);
        }
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

    // 异步加载
    public void bindBitmap(final String uri, final ImageView imageView) {
        imageView.setTag(TAG_KEY_URI, uri);
        Bitmap bitmap = loadBitmpaFromMemCache(uri);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
            return;
        }
        Runnable loadBitmapTask = new Runnable() {

            @Override
            public void run() {
                Bitmap bitmap = loadBitmap(uri);
                if (bitmap != null) {
                    LoaderResult result = new LoaderResult(imageView, uri, bitmap);
                    mMainHandler.obtainMessage(MESSAGE_POST_RESULT, result)
                            .sendToTarget();
                }
            }
        };
        THREAD_POOL_EXECUTOR.execute(loadBitmapTask);
    }

    // 同步加载
    public Bitmap loadBitmap(String uri) {
        Bitmap bitmap = loadBitmpaFromMemCache(uri);
        if (bitmap != null) {
            return bitmap;
        }

        bitmap = loadBitmapForDiskCache(uri);
        if (bitmap != null) {
            return bitmap;
        }
        bitmap = loadBitmapFromHttp(uri);

        if (bitmap == null && !mIsDiskLruCacheCreated) {
            bitmap = downloadBitmapFromUrl(uri);
        }
        return bitmap;
    }

    private Bitmap loadBitmpaFromMemCache(String url) {
        final String key = Md5Utils.toMD5(url);
        return mMemoryCache.get(key);
    }

    // 将下载的图片写入磁盘中，实现磁盘缓存
    private Bitmap loadBitmapFromHttp(String url) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            throw new RuntimeException("can not visit network from UI Thread.");
        }
        if (mDiskCache == null) {
            return null;
        }

        String key = Md5Utils.toMD5(url);
        mDiskCache.put(key, null);
        return loadBitmapForDiskCache(url);
    }

    /**
     * 从磁盘加载图片
     */
    private Bitmap loadBitmapForDiskCache(String url) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            Log.w(TAG, "warning: load bitmap from UI Thread ");
        }
        if (mDiskCache == null) {
            return null;
        }
        Bitmap bitmap;
        String key = Md5Utils.toMD5(url);
        bitmap = mDiskCache.get(key);
        if (bitmap != null && mMemoryCache != null && mMemoryCache.get(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
        return bitmap;
    }

    private Bitmap downloadBitmapFromUrl(String urlString) {
        Bitmap bitmap = null;
        HttpURLConnection urlConnection = null;
        BufferedInputStream in = null;
        try {
            final URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            in = new BufferedInputStream(urlConnection.getInputStream(),
                    IO_BUFFER_SIZE);
            bitmap = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            // TODO: handle exception
            Log.e(TAG, "Error in downloadBitmap:" + e);
        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
            IOUtil.close(in);
        }
        return bitmap;
    }


    private static class LoaderResult {

        public ImageView imageView;
        public String uri;
        public Bitmap bitmap;

        private LoaderResult(ImageView imageView, String uri, Bitmap bitmap) {
            super();
            this.imageView = imageView;
            this.uri = uri;
            this.bitmap = bitmap;
        }

    }

}
