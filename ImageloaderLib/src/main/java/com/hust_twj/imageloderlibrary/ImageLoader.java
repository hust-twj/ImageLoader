package com.hust_twj.imageloderlibrary;

import android.content.ContentUris;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ImageView;

import com.hust_twj.imageloderlibrary.cache.DiskCache;
import com.hust_twj.imageloderlibrary.cache.IOUtil;
import com.hust_twj.imageloderlibrary.cache.MemoryCache;
import com.hust_twj.imageloderlibrary.config.ImageLoaderConfig;
import com.hust_twj.imageloderlibrary.listener.ImageLoadListener;
import com.hust_twj.imageloderlibrary.request.LoaderRequest;
import com.hust_twj.imageloderlibrary.utils.BitmapDecoder;
import com.hust_twj.imageloderlibrary.utils.ImageResizer;
import com.hust_twj.imageloderlibrary.utils.LoaderProvider;
import com.hust_twj.imageloderlibrary.utils.Md5Utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
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

    private static volatile ImageLoader sInstance;

    /**
     * 图片加载配置对象
     */
    private ImageLoaderConfig mConfig;

    private MemoryCache mMemoryCache;
    private DiskCache mDiskCache;

    private static final String TAG = "ImageLoader";
    //加载本地图片
    private static final int MESSAGE_LOAD_LOCAL_IMAGE = 0;
    //加载网络图片--加载中
    private static final int MESSAGE_LOADING_REMOTE_IMAGE = 1;
    //加载网络图片--加载结束
    private static final int MESSAGE_LOADED_REMOTE_IMAGE = 2;
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = 1;
    private static final int MAX_POOL_SIZE = CPU_COUNT * 2 + 1;
    private static final long KEEP_ALIVE = 10L;

    private static final int URI_TAG = R.id.image_loader_uri;
    private static final int IO_BUFFER_SIZE = 8 * 1024;

    private static final int DEFAULT_WIDTH = 200;
    private static final int DEFAULT_HEIGHT = 200;

    private Context mContext;

    private ImageLoadListener mListener;

    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger();

        @Override
        public Thread newThread(@NonNull Runnable r) {
            return new Thread(r, "ImageLoader#" + mCount.getAndIncrement());
        }
    };

    private static final Executor THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(
            CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE, TimeUnit.SECONDS,
            new LinkedBlockingDeque<Runnable>(), sThreadFactory);

    private Handler mMainHandler = new Handler(Looper.getMainLooper()) {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_LOAD_LOCAL_IMAGE: {
                    LoaderRequest result = (LoaderRequest) msg.obj;
                    ImageView imageView = result.mImageView;
                    String uri = (String) imageView.getTag(URI_TAG);
                    if (result.mBitmap != null) {
                        if (uri.equals(result.uri)) {
                            imageView.setImageBitmap(result.mBitmap);
                            if (mListener != null) {
                                mListener.onResourceReady(result.mBitmap, uri);
                            }
                        } else {
                            Log.w(TAG, "uri不相等");
                        }
                    }
                }
                break;
                case MESSAGE_LOADING_REMOTE_IMAGE: {
                    if (mConfig == null || mConfig.displayConfig == null) {
                        return;
                    }
                    LoaderRequest result = (LoaderRequest) msg.obj;
                    ImageView imageView = result.mImageView;
                    String uri = (String) imageView.getTag(URI_TAG);
                    if (uri.equals(result.uri)) {
                        imageView.setImageResource(mConfig.displayConfig.loadingResId);
                    }
                }
                break;
                case MESSAGE_LOADED_REMOTE_IMAGE: {
                    LoaderRequest result = (LoaderRequest) msg.obj;
                    ImageView imageView = result.mImageView;
                    String uri = (String) imageView.getTag(URI_TAG);
                    if (result.mBitmap != null) {
                        if (uri.equals(result.uri)) {
                            imageView.setImageBitmap(result.mBitmap);
                            if (mListener != null) {
                                mListener.onResourceReady(result.mBitmap, uri);
                            }
                        } else {
                            Log.w(TAG, "uri不相等");
                        }
                    } else if (mConfig != null && mConfig.displayConfig != null) {
                        imageView.setImageResource(mConfig.displayConfig.failedResId);
                    }
                }
                break;
                default:
                    break;
            }

        }
    };

    private ImageLoader(Context context) {
        mContext = context.getApplicationContext();
    }

    public static ImageLoader with() {
        if (sInstance == null) {
            synchronized (ImageLoader.class) {
                if (sInstance == null) {
                    if (LoaderProvider.mContext == null) {
                        throw new IllegalStateException("context == null");
                    }
                    sInstance = new ImageLoader(LoaderProvider.mContext);
                }
            }
        }
        return sInstance;
    }

    public void init(ImageLoaderConfig config) {
        mConfig = config;
        //mMemoryCache = config.bitmapCache;
        if (mMemoryCache == null) {
            mMemoryCache = new MemoryCache();
        }
        if (mDiskCache == null) {
            mDiskCache = new DiskCache(mContext);
        }
    }

    /**
     * 加载本地图片
     *
     * @param resID     本地图片资源ID
     * @param imageView ImageView
     */
    public ImageLoader load(final int resID, final ImageView imageView) {
        try {
            Resources res = imageView.getContext().getResources();
            Bitmap bitmap = BitmapFactory.decodeResource(res, resID);
            imageView.setImageBitmap(bitmap);

            if (bitmap != null && mListener != null) {
                mListener.onResourceReady(bitmap, "");
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (mListener != null) {
                mListener.onFailure(e);
            }
        }
        return this;
    }

    public ImageLoader load(final String uri, final ImageView imageView) {
        imageView.post(new Runnable() {
            @Override
            public void run() {
                int width, height;
                width = (imageView.getWidth() == 0) ? DEFAULT_WIDTH : imageView.getWidth();
                height = (imageView.getHeight() == 0) ? DEFAULT_HEIGHT : imageView.getHeight();
                load(uri, imageView, width, height);
            }
        });

        return this;
    }

    /**
     * 异步加载网络图片
     *
     * @param uri       资源ID
     * @param imageView ImageView
     */
    public ImageLoader load(final String uri, final ImageView imageView,
                            final int reqWidth, final int reqHeight) {
        imageView.setTag(URI_TAG, uri);
        Bitmap bitmap;
        //加载本地图片
        if (isLocalImage(uri)) {
            bitmap = loadLocalImage(uri, imageView);
            LoaderRequest local = new LoaderRequest()
                    .setBitmap(bitmap)
                    .setImageView(imageView)
                    .setUri(uri);
            mMainHandler.obtainMessage(MESSAGE_LOAD_LOCAL_IMAGE, local).sendToTarget();
            return this;
        }
        bitmap = loadBitmapFromMemoryCache(uri);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
            if (mListener != null) {
                mListener.onResourceReady(bitmap, uri);
            }
            return this;
        }
        Runnable downloadTask = new Runnable() {

            @Override
            public void run() {
                Message message;
                LoaderRequest result;
                Bitmap bitmap;


                //加载网络图片
                bitmap = loadBitmap(imageView, uri, reqWidth, reqHeight);
                Log.e("twj125", Thread.currentThread().getName() + "   download success:  " + uri + " ");
                result = new LoaderRequest()
                        .setBitmap(bitmap)
                        .setImageView(imageView)
                        .setUri(uri);
                message = mMainHandler.obtainMessage(MESSAGE_LOADED_REMOTE_IMAGE, result);
                mMainHandler.sendMessage(message);
            }
        };
        THREAD_POOL_EXECUTOR.execute(downloadTask);
        return this;
    }

    /**
     * 加载本地图片
     *
     * @param uri uri
     * @return Bitmap
     */
    private Bitmap loadLocalImage(String uri, ImageView imageView) {
        final String imagePath = getImagePath(mContext, uri);
        final File imgFile = new File(imagePath);
        if (!imgFile.exists()) {
            return null;
        }

        // 加载图片
        BitmapDecoder decoder = new BitmapDecoder() {

            @Override
            public Bitmap decodeBitmapWithOption(BitmapFactory.Options options) {
                return BitmapFactory.decodeFile(imagePath, options);
            }
        };
        Bitmap bitmap = decoder.decodeBitmap(imageView.getWidth(), imageView.getHeight());
        if (bitmap != null && mDiskCache != null) {
            String key = Md5Utils.toMD5(uri);
            mDiskCache.put(key, bitmap);
        }
        return bitmap;
    }

    private String getImagePath(Context context, String uriString) {
        String imagePath = "";
        Uri uri = Uri.parse(uriString);
        if (DocumentsContract.isDocumentUri(context, uri)) {
            //如果是document类型的Uri，那么通过document的id来处理
            String documentId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = documentId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=?";
                String[] selectionArgs = {id};
                imagePath = getDataColumn(context, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection, selectionArgs);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(documentId));
                imagePath = getImagePathFromUri(context, contentUri);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            //如果是content类型的uri那么使用正常的uri
            imagePath = getImagePathFromUri(context, uri);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            //如果uri是file 那么直接获取文件的路径
            imagePath = uri.getPath();
        }
        return imagePath;
    }

    /**
     * 根据Uri来获取到图片对应的真实路径
     */
    private String getImagePathFromUri(Context context, Uri uri) {
        String path = null;
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        if (cursor == null) {
            return null;
        }
        if (cursor.moveToFirst()) {
            try {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        cursor.close();
        return path;
    }

    /**
     * 获取数据库表中的 _data 列，即返回Uri对应的文件路径
     */
    private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        String path = "";
        String[] projection = new String[]{MediaStore.Images.Media.DATA};
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow(projection[0]);
                path = cursor.getString(columnIndex);
            }
        } catch (Exception e) {
            if (cursor != null) {
                cursor.close();
            }
        }
        return path;
    }

    private boolean isLocalImage(String uri) {
        return uri.startsWith("content") || uri.startsWith("file");
    }

    /**
     * 同步加载  （依次从内存缓存、磁盘缓存、网络中加载）在子线程执行，主线程执行抛异常
     *
     * @param uri       http url
     * @param reqWidth  ImageView宽度
     * @param reqHeight ImageView高度
     * @return bitmap, maybe null.
     */
    public Bitmap loadBitmap(ImageView imageView, String uri, int reqWidth, int reqHeight) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            throw new RuntimeException("需要在子线程中执行");
        }
        Bitmap bitmap = loadBitmapFromMemoryCache(uri);
        if (bitmap != null) {
            return bitmap;
        }
        bitmap = loadBitmapForDiskCache(uri, reqWidth, reqHeight);
        if (bitmap != null) {
            return bitmap;
        }
        //加载中
        LoaderRequest result = new LoaderRequest()
                .setImageView(imageView)
                .setUri(uri);
        mMainHandler.obtainMessage(MESSAGE_LOADING_REMOTE_IMAGE, result).sendToTarget();
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
    private Bitmap loadBitmapForDiskCache(String url, int reqWidth, int reqHeight) {
        if (mDiskCache == null || mDiskCache.getDiskLruCache() == null) {
            return null;
        }
        Bitmap bitmap = null;
        String key = Md5Utils.toMD5(url);
        if (mDiskCache != null) {
            bitmap = mDiskCache.get(key);
        }
        if (bitmap != null && mMemoryCache != null) {
            mMemoryCache.put(key, bitmap);
        }
        if (bitmap != null) {
            return ImageResizer.decodeBitmap(bitmap, reqWidth, reqHeight);
        }
        return null;
    }

    /**
     * 从网络加载图片
     */
    private Bitmap downloadBitmapFromUrl(String urlString) {
        long currentTime = System.currentTimeMillis();
        Log.e("twj125", currentTime + "");
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
                if (mMemoryCache != null) {
                    mMemoryCache.put(key, bitmap);
                }
                if (mDiskCache != null) {
                    mDiskCache.put(key, bitmap);
                }
            }
            Log.e("twj125", System.currentTimeMillis() + "  加载网络图片耗时：" +
                    (System.currentTimeMillis() - currentTime) + "ms");
        } catch (final IOException e) {
            Log.e(TAG, "Error in downloadBitmap: " + e);
            if (mListener != null) {
                mListener.onFailure(e);
            }
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            IOUtil.close(in);
        }
        return bitmap;
    }

    public ImageLoader onLoadListener(ImageLoadListener loadListener) {
        mListener = loadListener;
        return this;
    }

    /**
     * 将下载的图片写入磁盘中，实现磁盘缓存
     *
     * @param urlString 图片链接
     * @return Bitmap
     */
    private Bitmap downloadBitmapFromUrl(String urlString, ImageView imageView) {
        Log.e("twj1256", "download -------" + urlString + "  " + imageView.getWidth() + "  " + imageView.getHeight());
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
                BitmapFactory.Options options = new BitmapFactory.Options();
                Log.e("twj1256", "options begin   " + bitmap.getWidth() + "  " + bitmap.getHeight());
                options.outWidth = bitmap.getWidth();
                options.outHeight = bitmap.getHeight();
                BitmapDecoder.configBitmapOptions(options, imageView.getWidth(), imageView.getHeight());
                Log.e("twj1256", "options end  " + bitmap.getWidth() + "  " + bitmap.getHeight());

                String key = Md5Utils.toMD5(urlString);
                if (mMemoryCache != null) {
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

    public void clearDiskCache() {
        if (mDiskCache != null) {
            mDiskCache.clearCache();
        }

    }

    public void clearMemoryCache() {
        if (mMemoryCache != null) {
            mMemoryCache.clearCache();
        }
    }

}
