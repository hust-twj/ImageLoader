package com.hust_twj.imageloderlibrary.loader;


import com.hust_twj.imageloderlibrary.request.LoaderRequest;

/**
 * Created by Wenjing.Tang on 2019-07-16.
 */
public interface ILoadStrategy {

    void loadImage(LoaderRequest request);

}
