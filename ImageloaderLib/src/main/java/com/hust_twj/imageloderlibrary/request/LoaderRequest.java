package com.hust_twj.imageloderlibrary.request;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.hust_twj.imageloderlibrary.config.DisplayConfig;
import com.hust_twj.imageloderlibrary.listener.ImageLoadListener;
import com.hust_twj.imageloderlibrary.utils.ImageViewUtils;


/**
 * 图片加载的请求信息
 *
 * @author hust_twj
 * @date 2019/7/2
 */
public class LoaderRequest implements Comparable<LoaderRequest> {

    public ImageView mImageView;
    //uri
    public String uri = "";

    //配置信息
    public DisplayConfig mDisplayConfig;
    //回调
    public ImageLoadListener mImageLoadListener;

    public Bitmap mBitmap;

    //是否取消该请求
    public boolean isCancel = false;

    //请求序列号
    public int serialNum = 0;

    //仅在内存中缓存
    public boolean onlyCacheMemory = false;

    public LoaderRequest(ImageView imageView, String uri, DisplayConfig config, ImageLoadListener listener) {
        this.mImageView = imageView;
        this.uri = uri;
        mDisplayConfig = config;
        this.mImageLoadListener = listener;
        imageView.setTag(uri);
    }
    public LoaderRequest setImageView(ImageView imageView) {
        this.mImageView = imageView;
        return this;
    }

    public LoaderRequest setUri(String uri) {
        this.uri = uri;
        if (mImageView != null) {
            mImageView.setTag(uri);
        }
        return this;
    }

    public LoaderRequest setDisplayConfig(DisplayConfig displayConfig) {
        this.mDisplayConfig = displayConfig;
        return this;
    }

    public LoaderRequest setImageLoadListener(ImageLoadListener imageLoadListener) {
        this.mImageLoadListener = imageLoadListener;
        return this;
    }

    public LoaderRequest setBitmap(Bitmap bitmap) {
        this.mBitmap = bitmap;
        return this;
    }

    public int getImageViewWidth() {
        return ImageViewUtils.getImageViewWidth(mImageView);
    }

    public int getImageViewHeight() {
        return ImageViewUtils.getImageViewHeight(mImageView);
    }

    /**
     * 判断ImageView的tag是否与uri相等
     */
    public boolean isImageViewTagValid() {
        return mImageView != null && mImageView.getTag().equals(uri);
    }

    /**
     * 是否为本地资源
     */
    public static boolean isResource(String uri) {
        boolean isResource;
        try {
            Integer.parseInt(uri);
            isResource = true;
        } catch (Exception e) {
            isResource = false;
        }
        return isResource;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        LoaderRequest other = (LoaderRequest) obj;
        if (uri == null) {
            if (other.uri != null)
                return false;
        } else if (!uri.equals(other.uri))
            return false;
        if (mImageView == null) {
            if (other.mImageView != null)
                return false;
        }
        if (serialNum != other.serialNum)
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((uri == null) ? 0 : uri.hashCode());
        result = prime * result + serialNum;
        return result;
    }

    @Override
    public int compareTo(LoaderRequest o) {
        return 0;
    }
}
