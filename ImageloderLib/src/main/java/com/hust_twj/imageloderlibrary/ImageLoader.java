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
import android.util.Log;
import android.widget.ImageView;

import com.hust_twj.imageloderlibrary.cache.DiskCache;
import com.hust_twj.imageloderlibrary.cache.DiskLruCache;
import com.hust_twj.imageloderlibrary.cache.IOUtil;
import com.hust_twj.imageloderlibrary.cache.MemoryCache;
import com.hust_twj.imageloderlibrary.config.ImageLoaderConfig;
import com.hust_twj.imageloderlibrary.listener.ImageLoadListener;
import com.hust_twj.imageloderlibrary.request.LoaderRequest;
import com.hust_twj.imageloderlibrary.utils.BitmapDecoder;
import com.hust_twj.imageloderlibrary.utils.ImageResizer;
import com.hust_twj.imageloderlibrary.utils.Md5Utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
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
    private static final int MAX_POOL_SIZE = CPU_COUNT * 2 + 1;
    private static final long KEEP_ALIVE = 10L;

    private static final int URI_TAG = R.id.image_loader_uri;
    private static final int IO_BUFFER_SIZE = 8 * 1024;

    private static final int  DEFAULT_WIDTH = 200;
    private static final int  DEFAULT_HEIGHT = 200;

    private Context mContext;

    private ImageLoadListener  mListener;

    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger();

        @Override
        public Thread newThread(Runnable r) {
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
                case MESSAGE_LOAD_IMAGE:
                    LoaderRequest result = (LoaderRequest) msg.obj;
                    ImageView imageView = result.mImageView;
                    String uri = (String) imageView.getTag(URI_TAG);
                    if (uri.equals(result.uri)) {
                        imageView.setImageBitmap(result.mBitmap);
                        if (mListener != null) {
                            mListener.onResourceReady(result.mBitmap, uri);
                        }
                    } else {
                        Log.w(TAG, "uri不相等");
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

    public static ImageLoader with(Context context) {
        if (sInstance == null) {
            synchronized (ImageLoader.class) {
                if (sInstance == null) {
                    sInstance = new ImageLoader(context);
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
     * @param resID 本地图片资源ID
     * @param imageView ImageView
     */
    public ImageLoader load(final int resID, final ImageView imageView) {
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
        return this;
    }

    public ImageLoader load(final String uri, final ImageView imageView){
        return load(uri, imageView, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }
    /**
     * 异步加载网络图片
     * @param uri 资源ID
     * @param imageView ImageView
     */
    public ImageLoader load(final String uri, final ImageView imageView,
                            final int reqWidth, final int reqHeight) {
        imageView.setTag(URI_TAG, uri);
        Bitmap bitmap;
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
                Bitmap bitmap;
                //加载本地图片
                if (isLocalImage(uri)) {
                    bitmap = loadLocalImage(uri, imageView);
                }else {
                    //加载网络图片
                    bitmap = loadBitmap(uri, reqWidth, reqHeight);
                }
                if (bitmap != null) {
                    Log.e("twj125",Thread.currentThread().getName() + "   download success:  " + uri + " ");
                    LoaderRequest result = new LoaderRequest()
                            .setBitmap(bitmap)
                            .setImageView(imageView)
                            .setUri(uri);
                    Message message =  mMainHandler.obtainMessage(MESSAGE_LOAD_IMAGE, result);
                    mMainHandler.sendMessage(message);
                }
            }
        };
        THREAD_POOL_EXECUTOR.execute(downloadTask);
        return this;
    }

    /**
     * 加载本地图片
     * @param uri uri
     * @return Bitmap
     */
    private Bitmap loadLocalImage(String uri, ImageView imageView){
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
        if (bitmap != null  && mDiskCache != null)  {
            String key =  Md5Utils.toMD5(uri);
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

    private boolean isLocalImage(String uri){
        return uri.startsWith("content") || uri.startsWith("file");
    }

    /**
     *
     * 同步加载  （依次从内存缓存、磁盘缓存、网络中加载）
     * @param uri http url
     * @param reqWidth ImageView宽度
     * @param reqHeight ImageView高度
     * @return bitmap, maybe null.
     *
     * 先从内存缓存尝试加载图片，找不到就去磁盘缓存拿，磁盘缓存拿不到就去网络拿
     * 在子线程执行，主线程执行抛异常
     */
    public Bitmap loadBitmap(String uri, int reqWidth, int reqHeight) {
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
       /* try {

            bitmap = loadBitmapFromHttp(uri, reqWidth, reqHeight);
            Log.d(TAG, "loadBitmapFromHttp,url:" + uri);
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        bitmap = downloadBitmapFromUrl(uri);
        return bitmap;
    }

    /*private Bitmap loadBitmapFromHttp(String url, int reqWidth, int reqHeight) throws IOException {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            throw new RuntimeException("需要在子线程中执行");
        }
        if (mDiskCache == null || mDiskCache.getDiskLruCache() == null) {
            return null;
        }

        String key = Md5Utils.toMD5(url);
        DiskLruCache.Editor editor = mDiskCache.getDiskLruCache().edit(key);
        if (editor != null) {
            OutputStream outputStream = editor.newOutputStream(0);
            if (downloadUrlToStream(url, outputStream)) {
                editor.commit();
            } else {
                editor.abort();
            }
            mDiskCache.getDiskLruCache().flush();
        }
        return loadBitmapForDiskCache(url, reqWidth, reqHeight);
    }*/

    public boolean downloadUrlToStream(String urlString, OutputStream outputStream) {
        HttpURLConnection urlConnection = null;
        BufferedOutputStream out = null;
        BufferedInputStream in = null;
        try {
            final URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            in = new BufferedInputStream(urlConnection.getInputStream(), IO_BUFFER_SIZE);
            out = new BufferedOutputStream(outputStream, IO_BUFFER_SIZE);

            int b;
            while ((b = in.read()) != -1) {
                out.write(b);
            }
            return true;
        } catch (IOException e) {
            Log.e(TAG, "downloadBitmap failed." + e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            IOUtil.close(out);
            IOUtil.close(in);
        }
        return false;
    }

    private Bitmap downloadBitmapFromUrl(String urlString) {
        Bitmap bitmap = null;
        HttpURLConnection urlConnection = null;
        BufferedInputStream in = null;
        try {
            final URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            in = new BufferedInputStream(urlConnection.getInputStream(), IO_BUFFER_SIZE);
            bitmap = BitmapFactory.decodeStream(in);
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
    public ImageLoader onLoadListener(ImageLoadListener loadListener){
        mListener = loadListener;
        return this;
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
        try {
            DiskLruCache.Snapshot snapshot = mDiskCache.getDiskLruCache().get(key);
            if (snapshot != null) {
                FileInputStream fileInputStream  = (FileInputStream) snapshot.getInputStream(0);
                FileDescriptor fileDescriptor = fileInputStream.getFD();
                bitmap = ImageResizer.decodeBitmapFromFileDescriptor(fileDescriptor,
                        reqWidth, reqHeight);
                if (bitmap != null && mMemoryCache != null) {
                    mMemoryCache.put(key, bitmap);
                }
            }
        } catch (IOException e) {
            Log.e(TAG,"get diskCache exception: " + e);
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     *  将下载的图片写入磁盘中，实现磁盘缓存
     * @param urlString 图片链接
     * @return Bitmap
     */
    private Bitmap downloadBitmapFromUrl(String urlString, ImageView imageView) {
        Log.e("twj1256","download -------" + urlString + "  " +imageView.getWidth() +"  " +imageView.getHeight());
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
                Log.e("twj1256", "options begin   "+bitmap.getWidth() + "  " +bitmap.getHeight());
                options.outWidth = bitmap.getWidth();
                options.outHeight = bitmap.getHeight();
                BitmapDecoder.configBitmapOptions(options, imageView.getWidth(), imageView.getHeight());
                Log.e("twj1256", "options end  "+bitmap.getWidth() + "  " +bitmap.getHeight());

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

}
