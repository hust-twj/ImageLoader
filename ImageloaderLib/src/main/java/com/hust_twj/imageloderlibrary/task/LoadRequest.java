package com.hust_twj.imageloderlibrary.task;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.hust_twj.imageloderlibrary.constant.Constants;
import com.hust_twj.imageloderlibrary.listener.ImageLoadListener;
import com.hust_twj.imageloderlibrary.utils.ImageViewUtil;

/**
 * 图片加载的请求信息
 *
 * @author hust_twj
 * @date 2019/7/2
 */
public class LoadRequest implements Comparable<LoadRequest> {

    public ImageView mImageView;

    public String uri;

    public int placeHolderResID;

    public int errorResID;

    public boolean displayRaw;

    public int defaultWidth = Constants.DEFAULT_IMAGE_WIDTH;

    public int defaultHeight = Constants.DEFAULT_IMAGE_HEIGHT;

    //加载回调
    public ImageLoadListener mImageLoadListener;

    public Bitmap mBitmap;

    //是否取消该请求
    public boolean isCancel = false;

    //请求的序列号
    public int serialNum = 0;

    //仅在内存中缓存
    public boolean onlyCacheMemory = false;

    public LoadRequest setImageView(ImageView imageView) {
        this.mImageView = imageView;
        //设置tag
        if (uri != null) {
            mImageView.setTag(uri);
        }
        return this;
    }

    public LoadRequest setUri(String uri) {
        this.uri = uri;
        if (mImageView  != null) {
            mImageView.setTag(uri);
        }
        return this;
    }

    public LoadRequest setDefaultWidth(int defaultWidth) {
        this.defaultWidth = defaultWidth;
        return this;
    }

    public LoadRequest setDefaultHeight(int defaultHeight) {
        this.defaultHeight = defaultHeight;
        return this;
    }

    public LoadRequest setPlaceHolder(int placeHolderResID) {
        this.placeHolderResID = placeHolderResID;
        return this;
    }

    public LoadRequest setError(int errorResID) {
        this.errorResID = errorResID;
        return this;
    }

    public LoadRequest setDisplayRaw(boolean displayRaw) {
        this.displayRaw = displayRaw;
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
        if (ImageViewUtil.getImageViewWidth(mImageView) != 0) {
            return ImageViewUtil.getImageViewWidth(mImageView);
        }
        return defaultWidth;
    }

    public int getImageViewHeight() {
        if (ImageViewUtil.getImageViewHeight(mImageView) != 0) {
            return ImageViewUtil.getImageViewHeight(mImageView);
        }
        return defaultHeight;
    }

    /**
     * 有效性：ImageView的tag是否与uri相等
     */
    public boolean isImageViewTagValid() {
        return mImageView != null && mImageView.getTag().equals(uri);
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
    public int compareTo(@NonNull LoadRequest request) {
        return 0;
    }
}
