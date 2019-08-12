package com.hust_twj.imageloderlibrary.utils;

import android.app.Activity;

import com.hust_twj.imageloderlibrary.task.Request;

/**
 * Description ：
 * Created by Wenjing.Tang on 2019-08-09.
 */
public class CheckUtil {

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

    /**
     * 检查Activity是否Finish
     */
    public static boolean isActivityFinished(Request request) {
        if (request == null) {
            return false;
        }
        if (request.mImageView == null) {
            return false;
        }
        if (!(request.mImageView.getContext() instanceof Activity)) {
            return false;
        }
        Activity activity = (Activity) request.mImageView.getContext();
        return activity.isFinishing() || activity.isDestroyed();
    }

}
