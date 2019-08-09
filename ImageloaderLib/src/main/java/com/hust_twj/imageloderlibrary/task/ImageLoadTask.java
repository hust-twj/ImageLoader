package com.hust_twj.imageloderlibrary.task;

import android.util.Log;

import com.hust_twj.imageloderlibrary.constant.Schema;
import com.hust_twj.imageloderlibrary.loader.ILoadStrategy;
import com.hust_twj.imageloderlibrary.loader.LoadManager;
import com.hust_twj.imageloderlibrary.utils.CheckUtil;

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
    private BlockingQueue<LoadRequest> mRequestQueue = new PriorityBlockingQueue<>();
    /**
     * 请求的序列化生成器
     */
    private AtomicInteger mSerialNumber = new AtomicInteger(0);

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
                    Log.e(TAG, "isActivityFinished: " + CheckUtil.isActivityFinished(request) + "  " + request.uri);
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
        ThreadPoolManager.getInstance().execute(downloadTask);
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
