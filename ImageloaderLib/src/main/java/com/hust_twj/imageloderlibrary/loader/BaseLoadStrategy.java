package com.hust_twj.imageloderlibrary.loader;

import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

import com.hust_twj.imageloderlibrary.ImageLoader;
import com.hust_twj.imageloderlibrary.cache.BitmapCache;
import com.hust_twj.imageloderlibrary.constant.Constants;
import com.hust_twj.imageloderlibrary.task.Request;
import com.hust_twj.imageloderlibrary.utils.CheckUtil;

/**
 * 图片加载器
 * Created by Wenjing.Tang on 2019-07-16.
 */
public abstract class BaseLoadStrategy implements ILoadStrategy {

    private static final String TAG = BaseLoadStrategy.class.getSimpleName();

    /**
     * 图片缓存
     */
    private BitmapCache mCache = ImageLoader.get().getConfig().bitmapCache;

    @Override
    public final void loadImage(Request request) {
        Bitmap resultBitmap;
        resultBitmap = mCache.get(request.uri);
        Log.e(TAG, "是否有缓存 : " + (resultBitmap != null) + ", uri: " +
                request.uri + "   "  +Thread.currentThread().getName());
        if (resultBitmap == null) {
            showLoadingView(request);
            resultBitmap = onLoadImage(request);
            Log.e(TAG, "下载完成：" + request.uri + "  " + request.uri + "   "
            + Thread.currentThread().getName());
            updateCache(request, resultBitmap);
        } else {
            request.onlyCacheMemory = true;
        }
        updateImageView(request, resultBitmap);
    }

    protected abstract Bitmap onLoadImage(Request request);

    /**
     * 缓存新的图片
     */
    private void updateCache(Request request, Bitmap bitmap) {
        if (CheckUtil.isResource(request.uri)) {
            return;
        }
        if (bitmap == null || mCache == null) {
            return;
        }
        synchronized (BaseLoadStrategy.class) {
            mCache.put(request.uri, bitmap);
        }
    }

    /**
     * 显示加载中的占位图
     * 注意：要判断ImageView的tag与image uri的相等性
     *
     * @param request request
     */
    private void showLoadingView(final Request request) {
        final ImageView imageView = request.mImageView;
        if (request.isImageViewTagValid() && request.placeHolderResID != Constants.DEFAULT_RES_ID) {
            setImageResource(imageView, request.placeHolderResID);
        }
    }

    /**
     * 更新ImageView，加载并显示图片
     *
     * @param request request
     * @param bitmap  bitmap
     */
    private void updateImageView(final Request request, final Bitmap bitmap) {
        final ImageView imageView = request.mImageView;
        if (imageView == null) {
            return;
        }
        //加载成功并回调接口
        if (bitmap != null && request.isImageViewTagValid()) {
            Log.e(TAG, "updateImageView：" + bitmap.getWidth() + "*" + bitmap.getHeight() + "  " +request.uri);
            setImageBitmap(imageView, bitmap);
            if (request.mImageLoadListener != null) {
                request.mImageLoadListener.onResourceReady(bitmap, request.uri);
            }
        } else if (bitmap == null) {
            if (request.errorResID != Constants.DEFAULT_RES_ID) {
                setImageResource(imageView, request.errorResID);
            }
            Log.e(TAG, "加载失败  " + request.uri + "  " + request.uri + "   ");

            // 加载失败并回调
            if (request.mImageLoadListener != null) {
                request.mImageLoadListener.onFailure();
            }
        }
    }

    private void setImageBitmap(final ImageView imageView, final Bitmap bitmap) {
        imageView.post(new Runnable() {
            @Override
            public void run() {
                imageView.setImageBitmap(bitmap);
            }
        });
    }

    private void setImageResource(final ImageView imageView, final int resID) {
        imageView.post(new Runnable() {
            @Override
            public void run() {
                imageView.setImageResource(resID);
            }
        });
    }

}
