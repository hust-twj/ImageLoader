package com.hust_twj.imageloderlibrary.utils;

import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.lang.reflect.Field;

/**
 * Created by Wenjing.Tang on 2019-07-16.
 */
public class ImageViewUtil {

    /**
     * 获取ImageView宽度
     * 依次从getWidth() --> layout_width(params.width) --> maxWidth 中取
     */
    public static int getImageViewWidth(ImageView imageView) {
        if (imageView != null) {
            final ViewGroup.LayoutParams params = imageView.getLayoutParams();
            int width = 0;
            if (params != null && params.width != ViewGroup.LayoutParams.WRAP_CONTENT) {
                width = imageView.getWidth();
            }
            if (width <= 0 && params != null) {
                width = params.width;
            }
            if (width <= 0) {
                width = getImageViewFieldValue(imageView, "mMaxWidth");
            }
            return width;
        }
        return 0;
    }

    /**
     * 获取ImageView高度
     * getHeight() --> layout_height(params.height) --> maxHeight 中取
     */
    public static int getImageViewHeight(ImageView imageView) {
        if (imageView != null) {
            final ViewGroup.LayoutParams params = imageView.getLayoutParams();
            int height = 0;
            if (params != null
                    && params.height != ViewGroup.LayoutParams.WRAP_CONTENT) {
                height = imageView.getHeight();
            }
            if (height <= 0 && params != null) {
                height = params.height;
            }
            if (height <= 0) {
                height = getImageViewFieldValue(imageView, "mMaxHeight");
            }
            return height;
        }
        return 0;
    }

    /**
     * 根据ImageView属性获取值
     */
    private static int getImageViewFieldValue(Object object, String fieldName) {
        int value = 0;
        try {
            Field field = ImageView.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            int fieldValue = (Integer) field.get(object);
            if (fieldValue > 0 && fieldValue < Integer.MAX_VALUE) {
                value = fieldValue;
            }
        } catch (Exception e) {
            Log.e("", e.getMessage());
        }
        return value;
    }

}
