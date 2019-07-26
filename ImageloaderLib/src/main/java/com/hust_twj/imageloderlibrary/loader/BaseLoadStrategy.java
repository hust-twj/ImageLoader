package com.hust_twj.imageloderlibrary.loader;

import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

import com.hust_twj.imageloderlibrary.ImageLoader;
import com.hust_twj.imageloderlibrary.cache.BitmapCache;
import com.hust_twj.imageloderlibrary.config.DisplayConfig;
import com.hust_twj.imageloderlibrary.request.LoaderRequest;

/**
 * Created by Wenjing.Tang on 2019-07-16.
 */
public abstract class BaseLoadStrategy implements ILoadStrategy {

    private static final String TAG = BaseLoadStrategy.class.getSimpleName();

    /**
     * 图片缓存
     */
    private static BitmapCache mCache = ImageLoader.with().getConfig().bitmapCache;

    @Override
    public final void loadImage(LoaderRequest request) {
        Bitmap resultBitmap;
        resultBitmap = mCache.get(request.uri);
        Log.e(TAG, "是否有缓存 : " + resultBitmap + ", uri = " + request.uri);
        if (resultBitmap == null) {
            showLoading(request);
            resultBitmap = onLoadImage(request);
            cacheBitmap(request, resultBitmap);
        } else {
            request.onlyCacheMemory = true;
        }
        updateImageView(request, resultBitmap);
    }

    /**
     * @param request request
     * @return Bitmap
     */
    protected abstract Bitmap onLoadImage(LoaderRequest request);

    /**
     * @param request request
     * @param bitmap  bitmap
     */
    private void cacheBitmap(LoaderRequest request, Bitmap bitmap) {
        if (LoaderRequest.isResource(request.uri)) {
            return;
        }
        // 缓存新的图片
        if (bitmap != null && mCache != null) {
            synchronized (BaseLoadStrategy.class) {
                mCache.put(request.uri, bitmap);
            }
        }
    }

    /**
     * 显示加载中的视图,注意这里也要判断ImageView的tag与image uri的相等性,否则逆序加载时出现问题
     *
     * @param request request
     */
    private void showLoading(final LoaderRequest request) {
        final ImageView imageView = request.mImageView;
        if (request.isImageViewTagValid() && hasLoadingPlaceholder(request.mDisplayConfig)) {
            imageView.post(new Runnable() {

                @Override
                public void run() {
                    imageView.setImageResource(request.mDisplayConfig.loadingResId);
                }
            });
        }
    }

    /**
     * 更新ImageView，加载并显示图片
     *
     * @param request request
     * @param bitmap  bitmap
     */
    private void updateImageView(final LoaderRequest request, final Bitmap bitmap) {
        final ImageView imageView = request.mImageView;
        if (imageView == null) {
            return;
        }
        //加载成功并回调接口
        if (bitmap != null && imageView.getTag().equals(request.uri)) {
            imageView.post(new Runnable() {
                @Override
                public void run() {
                    imageView.setImageBitmap(bitmap);
                }
            });
            if (request.mImageLoadListener != null) {
                request.mImageLoadListener.onResourceReady(bitmap, request.uri);
            }
        } else if (bitmap == null && hasErrorPlaceholder(request.mDisplayConfig)) {
            // 加载失败并回调
            imageView.post(new Runnable() {
                @Override
                public void run() {
                    imageView.setImageResource(request.mDisplayConfig.failedResId);
                }
            });
            if (request.mImageLoadListener != null) {
                request.mImageLoadListener.onFailure();
            }
        }
    }

    private boolean hasLoadingPlaceholder(DisplayConfig displayConfig) {
        return displayConfig != null && displayConfig.loadingResId > 0;
    }

    private boolean hasErrorPlaceholder(DisplayConfig displayConfig) {
        return displayConfig != null && displayConfig.failedResId > 0;
    }

}
