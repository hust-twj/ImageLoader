package com.hust_twj.imageloderlibrary.task;

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
public class LoadRequest implements Comparable<LoadRequest> {

    public ImageView mImageView;

    public String uri ;

    //配置信息
    public DisplayConfig mDisplayConfig = new DisplayConfig();
    //加载回调
    public ImageLoadListener mImageLoadListener;

    public Bitmap mBitmap;

    //是否取消该请求
    public boolean isCancel = false;

    //请求的序列号
    public int serialNum = 0;

    //仅在内存中缓存
    public boolean onlyCacheMemory = false;

    public LoadRequest(ImageView imageView, String uri) {
        this.mImageView = imageView;
        this.uri = uri;
        imageView.setTag(uri);
    }

    public LoadRequest(ImageView imageView, String uri, DisplayConfig config, ImageLoadListener listener) {
        this.mImageView = imageView;
        this.uri = uri;
        mDisplayConfig = config;
        this.mImageLoadListener = listener;
        imageView.setTag(uri);
    }

    public LoadRequest setImageView(ImageView imageView) {
        this.mImageView = imageView;
        //设置tag
        mImageView.setTag(uri);
        return this;
    }

    public LoadRequest setUri(String uri) {
        this.uri = uri;
        return this;
    }

    public LoadRequest setDisplayConfig(DisplayConfig displayConfig) {
        this.mDisplayConfig = displayConfig;
        return this;
    }

    public LoadRequest setImageLoadListener(ImageLoadListener imageLoadListener) {
        this.mImageLoadListener = imageLoadListener;
        return this;
    }

    public LoadRequest setBitmap(Bitmap bitmap) {
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
     * 有效性：ImageView的tag是否与uri相等
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
        LoadRequest other = (LoadRequest) obj;
        if (uri == null) {
            if (other.uri != null)
                return false;
        } else if (!uri.equals(other.uri))
            return false;
        if (mImageView == null) {
            if (other.mImageView != null)
                return false;
        }
        return serialNum == other.serialNum;
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
    public int compareTo(LoadRequest request) {
        return 0;
    }
}
