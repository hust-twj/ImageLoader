package com.hust_twj.imageloderlibrary.listener;

/**
 * 图片加载回调接口
 * @author hust_twj
 * @date 2019/6/12
 */
public interface ImageLoadListener<T> {

    void onResourceReady(T t, String uri);

    void onFailure(Exception e);

}
