package com.hust_twj.imageloderlibrary.loader;

import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

import com.hust_twj.imageloderlibrary.ImageLoader;
import com.hust_twj.imageloderlibrary.cache.BitmapCache;
import com.hust_twj.imageloderlibrary.config.DisplayConfig;
import com.hust_twj.imageloderlibrary.task.LoadRequest;
import com.hust_twj.imageloderlibrary.utils.Md5Utils;

/**
 * 图片加载器
 * Created by Wenjing.Tang on 2019-07-16.
 */
public abstract class BaseLoadStrategy implements ILoadStrategy {

    private static final String TAG = BaseLoadStrategy.class.getSimpleName();

    /**
     * 图片缓存
     */
    private static BitmapCache mCache = ImageLoader.get().getConfig().bitmapCache;

    @Override
    public final void loadImage(LoadRequest request) {
        Bitmap resultBitmap;
        resultBitmap = mCache.get(request.uri);
        Log.e(TAG, "是否有缓存 : " + (resultBitmap != null) + ", uri: " + request.uri + "  " +
                Md5Utils.toMD5(request.uri) + "   "  +Thread.currentThread().getName());
        if (resultBitmap == null) {
            showLoading(request);
            resultBitmap = onLoadImage(request);
            Log.e(TAG, "下载完成：" + request.uri + "  " + Md5Utils.toMD5(request.uri) + "   "
            + Thread.currentThread().getName());
            cacheBitmap(request, resultBitmap);
        } else {
            request.onlyCacheMemory = true;
        }
        updateImageView(request, resultBitmap);
    }

    protected abstract Bitmap onLoadImage(LoadRequest request);

    /**
     * 缓存新的图片
     */
    private void cacheBitmap(LoadRequest request, Bitmap bitmap) {
        if (LoadRequest.isResource(request.uri)) {
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
     * 显示加载中的视图,注意这里也要判断ImageView的tag与image uri的相等性,否则逆序加载时出现问题
     *
     * @param request request
     */
    private void showLoading(final LoadRequest request) {
        final ImageView imageView = request.mImageView;
        if (request.isImageViewTagValid() && hasLoadingPlaceholder(request.mDisplayConfig)) {
            imageView.post(new Runnable() {

                @Override
                public void run() {
                    imageView.setImageResource(request.mDisplayConfig.placeHolderResId);
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
    private void updateImageView(final LoadRequest request, final Bitmap bitmap) {
        final ImageView imageView = request.mImageView;
        if (imageView == null) {
            return;
        }
        //加载成功并回调接口
        if (bitmap != null && request.isImageViewTagValid()) {
            imageView.post(new Runnable() {
                @Override
                public void run() {
                    imageView.setImageBitmap(bitmap);
                }
            });
            if (request.mImageLoadListener != null) {
                request.mImageLoadListener.onResourceReady(bitmap, request.uri);
            }
        } else if (bitmap == null) {
            if (hasErrorPlaceholder(request.mDisplayConfig)) {
                imageView.post(new Runnable() {
                    @Override
                    public void run() {
                        imageView.setImageResource(request.mDisplayConfig.errorResId);
                    }
                });
            }
            Log.e(TAG, "加载失败  " + request.uri + "  " + Md5Utils.toMD5(request.uri) + "   ");

            // 加载失败并回调
            if (request.mImageLoadListener != null) {
                request.mImageLoadListener.onFailure();
            }

        }
    }

    private boolean hasLoadingPlaceholder(DisplayConfig displayConfig) {
        return displayConfig != null && displayConfig.placeHolderResId > 0;
    }

    private boolean hasErrorPlaceholder(DisplayConfig displayConfig) {
        return displayConfig != null && displayConfig.errorResId > 0;
    }

}
