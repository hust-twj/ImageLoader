package com.hust_twj.imageloderlibrary;

import android.content.Context;
import android.content.res.Resources;
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

    private static final int MESSAGE_LOAD_IMAGE = 1;

    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();

    private static final int CORE_POOL_SIZE = CPU_COUNT + 1;
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
    private static final long KEEP_ALIVE = 10L;

    private static final int TAG_KEY_URI = R.id.image_loader_uri;
    private static final int IO_BUFFER_SIZE = 8 * 1024;

    private Context mContext;
    private ImageResizer mImageResizer = new ImageResizer();

    private ImageLoadListener  mListener;

    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger();

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "ImageLoader#" + mCount.getAndIncrement());
        }
    };

    private static final Executor THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(
            CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE, TimeUnit.SECONDS,
            new LinkedBlockingDeque<Runnable>(), sThreadFactory);

    private Handler mMainHandler = new Handler(Looper.getMainLooper()) {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_LOAD_IMAGE:
                    LoaderResult result = (LoaderResult) msg.obj;
                    ImageView imageView = result.imageView;
                    String uri = (String) imageView.getTag(TAG_KEY_URI);
                    if (uri.equals(result.uri)) {
                        imageView.setImageBitmap(result.bitmap);
                        if (mListener != null) {
                            mListener.onResourceReady(result.bitmap, uri);
                        }
                    } else {
                        Log.w(TAG, "set image bitmap,but url has changed , ignored!");
                    }
                    break;
                default:
                break;
            }

        }
    };

    private ImageLoader(Context context) {
        mContext = context.getApplicationContext();
        init();
    }

    private void init(/*ImageLoaderConfig config*/) {
        //mConfig = config;
        ///mMemoryCache = config.bitmapCache;

        if (mMemoryCache == null) {
            mMemoryCache = new MemoryCache();
        }
        if (mDiskCache == null) {
            mDiskCache = new DiskCache(mContext);
        }
    }

    public static ImageLoader build(Context context) {
        return getInstance(context);
    }

    private static ImageLoader getInstance(Context context) {
        if (sInstance == null) {
            synchronized (ImageLoader.class) {
                if (sInstance == null) {
                    sInstance = new ImageLoader(context);
                }
            }
        }
        return sInstance;
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

    /**
     * 加载本地图片
     * @param resID 本地图片资源ID
     * @param imageView ImageView
     */
    public void load(final int resID, final ImageView imageView) {
        try{
            Resources res = imageView.getContext().getResources();
            Bitmap bitmap = BitmapFactory.decodeResource(res, resID);
            imageView.setImageBitmap(bitmap);

            if (bitmap != null && mListener != null) {
                mListener.onResourceReady(bitmap,"");
            }
        }catch (Exception e){
            e.printStackTrace();
            if ( mListener != null) {
                mListener.onFailure(e);
            }
        }
    }

    public ImageLoader onLoadListener(ImageLoadListener loadListener){
        mListener = loadListener;
        return this;
    }

    /**
     * 异步加载网络图片
     * @param uri 资源ID
     * @param imageView ImageView
     */
    public ImageLoader bindBitmap(final String uri, final ImageView imageView) {
        imageView.setTag(TAG_KEY_URI, uri);
        Bitmap bitmap = loadBitmapFromMemoryCache(uri);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
            if (mListener != null) {
                mListener.onResourceReady(bitmap, uri);
            }
            return this;
        }
        Runnable loadBitmapTask = new Runnable() {

            @Override
            public void run() {
                Bitmap bitmap = loadBitmap(uri);
                if (bitmap != null) {
                    LoaderResult result = new LoaderResult(imageView, uri, bitmap);
                    mMainHandler.obtainMessage(MESSAGE_LOAD_IMAGE, result)
                            .sendToTarget();
                }
            }
        };
        THREAD_POOL_EXECUTOR.execute(loadBitmapTask);
        return this;
    }

    /**
     * 加载图片：三级缓存
     * @param uri uri
     * @return Bitmap
     */
    public Bitmap loadBitmap(String uri) {
        Bitmap bitmap = loadBitmapFromMemoryCache(uri);
        if (bitmap != null) {
            return bitmap;
        }
        bitmap = loadBitmapForDiskCache(uri);
        if (bitmap != null) {
            return bitmap;
        }
        bitmap = downloadBitmapFromUrl(uri);
        return bitmap;
    }
    /**
     * 从内存缓存中加载图片
     */
    private Bitmap loadBitmapFromMemoryCache(String url) {
        final String key = Md5Utils.toMD5(url);
        return mMemoryCache.get(key);
    }

    /**
     * 从磁盘缓存中加载图片
     */
    private Bitmap loadBitmapForDiskCache(String url) {
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

    /**
     *  将下载的图片写入磁盘中，实现磁盘缓存
     * @param urlString 图片链接
     * @return Bitmap
     */
    private Bitmap downloadBitmapFromUrl(String urlString) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            throw new RuntimeException("应在子线程访问网络");
        }
        if (!(urlString.startsWith("http") || urlString.startsWith("https"))) {
            throw new RuntimeException("图片链接有误");
        }
        Bitmap bitmap = null;
        HttpURLConnection urlConnection = null;
        BufferedInputStream in = null;
        try {
            final URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            in = new BufferedInputStream(urlConnection.getInputStream(), IO_BUFFER_SIZE);
            bitmap = BitmapFactory.decodeStream(in);
            if (bitmap != null) {
                String key = Md5Utils.toMD5(urlString);
                if (mMemoryCache != null)  {
                    mMemoryCache.put(key, bitmap);
                }
                if (mDiskCache != null) {
                    mDiskCache.put(key, bitmap);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in downloadBitmap:" + e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            IOUtil.close(in);
        }
        return bitmap;
    }

    private static class LoaderResult {

        private ImageView imageView;
        private String uri;
        private Bitmap bitmap;

        private LoaderResult(ImageView imageView, String uri, Bitmap bitmap) {
            super();
            this.imageView = imageView;
            this.uri = uri;
            this.bitmap = bitmap;
        }

    }

}
