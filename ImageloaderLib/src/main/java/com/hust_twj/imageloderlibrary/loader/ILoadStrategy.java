package com.hust_twj.imageloderlibrary.loader;


import com.hust_twj.imageloderlibrary.task.LoadRequest;

/**
 * 加载策略接口
 * Created by Wenjing.Tang on 2019-07-16.
 */
public interface ILoadStrategy {

    void loadImage(LoadRequest request);

}
