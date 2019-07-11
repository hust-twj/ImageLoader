package com.hust_twj.imageloderlibrary.request;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.hust_twj.imageloderlibrary.config.DisplayConfig;
import com.hust_twj.imageloderlibrary.listener.ImageLoadListener;

/**
 * @author hust_twj
 * @date 2019/7/2
 */
public class LoaderRequest {

    public ImageView mImageView;
    public String uri;

    public DisplayConfig mDisplayConfig;
    public ImageLoadListener mImageLoadListener;

    public Bitmap mBitmap;

    public LoaderRequest setImageView(ImageView imageView) {
        this.mImageView = imageView;
        return this;
    }

    public LoaderRequest setUri(String uri) {
        this.uri = uri;
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

}
