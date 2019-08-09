package com.hust_twj.imageloderlibrary.task;

import android.support.annotation.NonNull;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Description ：线程池管理类
 * Created by Wenjing.Tang on 2019-08-09.
 */
public class ThreadPoolManager {

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


    private static ThreadPoolManager sInstance;

    public static ThreadPoolManager getInstance() {
        if (sInstance == null) {
            synchronized (ThreadPoolManager.class) {
                if (sInstance == null) {
                    sInstance =  new ThreadPoolManager();
                }
            }
        }
        return sInstance;
    }

    /**
     * 向线程池中添加任务
     */
    public void execute(Runnable task) {
        if (task != null) {
            THREAD_POOL_EXECUTOR.execute(task);
        }
    }

    /**
     * 关闭线程池，不再接受新的任务
     */
    public void shutdown() {
        THREAD_POOL_EXECUTOR.shutdown();
    }

}
