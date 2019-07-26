package com.hust_twj.imageloderlibrary.request;

import android.support.annotation.NonNull;
import android.util.Log;

import com.hust_twj.imageloderlibrary.constant.Schema;
import com.hust_twj.imageloderlibrary.loader.ILoadStrategy;
import com.hust_twj.imageloderlibrary.loader.LoadManager;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 请求队列（优先级队列）,使得请求可以按照优先级进行处理
 * Created by Wenjing.Tang on 2019-07-16.
 */
public final class RequestQueue {

    private static final String TAG = RequestQueue.class.getSimpleName();

    /**
     * 请求队列
     */
    private BlockingQueue<LoaderRequest> mRequestQueue = new PriorityBlockingQueue<>();
    /**
     * 请求的序列化生成器
     */
    private AtomicInteger mSerialNumGenerator = new AtomicInteger(0);

    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = CPU_COUNT + 1;
    private static final int MAX_POOL_SIZE = CPU_COUNT * 2 + 1;
    private static final long KEEP_ALIVE = 10L;

    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        @Override
        public Thread newThread(@NonNull Runnable r) {
            return new Thread(r, "ImageLoaderThread#" + mCount.getAndIncrement());
        }
    };

    private static final ThreadPoolExecutor THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(
            CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE, TimeUnit.SECONDS,
            new LinkedBlockingDeque<Runnable>(), sThreadFactory);

    /**
     * 启动线程池
     */
    public void start() {
        Runnable downloadTask = new Runnable() {

            @Override
            public void run() {
                LoaderRequest request;
                try {
                    request = mRequestQueue.take();

                    if (request.isCancel) {
                        return;
                    }
                    ILoadStrategy imageLoader;
                    String schema = parseSchema(request.uri);
                    imageLoader = LoadManager.getInstance().getLoader(schema);
                    if (imageLoader == null) {
                        Log.e(TAG, "---- schema is : " + request.uri);
                        return;
                    }
                    imageLoader.loadImage(request);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        THREAD_POOL_EXECUTOR.execute(downloadTask);
    }

    /**
     * 解析schema
     *
     * @param uri uri
     * @return schema
     */
    private String parseSchema(String uri) {
        if (uri.contains(Schema.SPIT)) {
            return uri.split(Schema.SPIT)[0];
        }
        return uri;
    }

    /**
     * 停止RequestDispatcher
     */
    public void stop() {
        THREAD_POOL_EXECUTOR.shutdownNow();
    }

    /**
     * 不能重复添加请求
     */
    public void addRequest(LoaderRequest loaderRequest) {
        if (!mRequestQueue.contains(loaderRequest)) {
            loaderRequest.serialNum = generateSerialNumber();
            mRequestQueue.add(loaderRequest);
        } else {
            Log.d("", "### 请求队列中已经含有");
        }
    }

    public void clear() {
        mRequestQueue.clear();
    }

    public BlockingQueue<LoaderRequest> getAllRequests() {
        return mRequestQueue;
    }

    /**
     * 为每个请求生成一个系列号
     *
     * @return 序列号
     */
    private int generateSerialNumber() {
        return mSerialNumGenerator.incrementAndGet();
    }
}
