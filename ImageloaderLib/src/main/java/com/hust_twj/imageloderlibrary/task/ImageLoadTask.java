package com.hust_twj.imageloderlibrary.task;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Log;

import com.hust_twj.imageloderlibrary.constant.Schema;
import com.hust_twj.imageloderlibrary.loader.ILoadStrategy;
import com.hust_twj.imageloderlibrary.loader.LoadManager;
import com.hust_twj.imageloderlibrary.utils.CheckUtil;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 图片加载请求任务（包含优先级队列,使得请求可以按照优先级进行处理）
 * Created by Wenjing.Tang on 2019-07-16.
 */
public final class ImageLoadTask {

    private static final String TAG = ImageLoadTask.class.getSimpleName();

    /**
     * 请求队列
     */
    private BlockingQueue<LoadRequest> mRequestQueue = new PriorityBlockingQueue<>();
    /**
     * 请求的序列化生成器
     */
    private AtomicInteger mSerialNumber = new AtomicInteger(0);

    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = CPU_COUNT + 1;
    private static final int MAX_POOL_SIZE = CPU_COUNT * 2 + 1;
    private static final long KEEP_ALIVE = 10L;

    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        @Override
        public Thread newThread(@NonNull Runnable runnable) {
            return new Thread(runnable, "ImageLoaderThread#" + mCount.getAndIncrement());
        }
    };

    private static final ThreadPoolExecutor THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(
            CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE, TimeUnit.SECONDS,
            new LinkedBlockingDeque<Runnable>(), sThreadFactory);

    /**
     * 启动线程池，开始图片加载
     */
    public void start() {
        Runnable downloadTask = new Runnable() {

            @Override
            public void run() {
                LoadRequest request;
                try {
                    request = mRequestQueue.take();

                    if (request.isCancel) {
                        return;
                    }
                    Log.e(TAG, "isActivityFinished: " + CheckUtil.isActivityFinished(request));
                    if (CheckUtil.isActivityFinished(request)) {
                        return;
                    }
                    String schema = parseSchema(request.uri);
                    ILoadStrategy imageLoader = LoadManager.getInstance().getLoader(schema);
                    if (imageLoader == null) {
                        Log.e(TAG, "start -- schema: " + request.uri);
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
     * 停止，结束图片加载
     */
    public void stop() {
        // TODO: 2019/8/4
        clear();
    }

    /**
     * 不能重复添加请求
     */
    public void addRequest(LoadRequest loadRequest) {
        if (!mRequestQueue.contains(loadRequest)) {
            loadRequest.serialNum = generateSerialNumber();
            mRequestQueue.add(loadRequest);
        } else {
            Log.e(TAG, "请求已在队列中");
        }
    }

    public void clear() {
        mRequestQueue.clear();
    }

    /**
     * 为每个请求生成一个系列号
     *
     * @return 序列号
     */
    private int generateSerialNumber() {
        return mSerialNumber.incrementAndGet();
    }
}
