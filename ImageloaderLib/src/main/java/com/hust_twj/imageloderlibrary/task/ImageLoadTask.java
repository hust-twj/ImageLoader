package com.hust_twj.imageloderlibrary.task;

import android.util.Log;

import com.hust_twj.imageloderlibrary.loader.ILoadStrategy;
import com.hust_twj.imageloderlibrary.loader.LoadManager;
import com.hust_twj.imageloderlibrary.utils.CheckUtil;
import com.hust_twj.imageloderlibrary.utils.SchemaUtil;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
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
    private BlockingQueue<Request> mRequestQueue = new PriorityBlockingQueue<>();
    /**
     * 请求的序列生成器
     */
    private AtomicInteger mSerialNumber = new AtomicInteger(0);

    /**
     * 启动线程池，开始图片加载
     */
    public void start() {
        Runnable downloadTask = new Runnable() {

            @Override
            public void run() {
                try {
                    Request request = mRequestQueue.take();

                    if (request.isCancel) {
                        return;
                    }
                    Log.e(TAG, "isActivityFinished: " + CheckUtil.isActivityFinished(request) + "  " + request.uri);
                    if (CheckUtil.isActivityFinished(request)) {
                        return;
                    }
                    String schema = SchemaUtil.parseSchema(request.uri);
                    ILoadStrategy imageLoader = LoadManager.getInstance().getLoader(schema);
                    if (imageLoader == null) {
                        Log.e(TAG, "run -- schema: " + request.uri);
                        return;
                    }
                    imageLoader.loadImage(request);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        ThreadPoolManager.getInstance().execute(downloadTask);
    }

    /**
     * 停止，结束图片加载
     */
    public void stop() {
        // TODO: 2019/8/4
    }

    /**
     * 不能重复添加请求
     */
    public void addRequest(Request request) {
        if (!mRequestQueue.contains(request)) {
            request.serialNum = generateSerialNumber();
            mRequestQueue.add(request);
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
