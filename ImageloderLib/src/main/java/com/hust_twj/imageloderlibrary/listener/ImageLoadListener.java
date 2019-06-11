package com.hust_twj.imageloderlibrary.listener;

import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * 图片加载回调接口
 * @author hust_twj
 * @date 2019/6/12
 */
public interface ImageLoadListener {

    void onComplete(ImageView imageView, Bitmap bitmap, String uri);

    void onFailure(Exception e);

}
