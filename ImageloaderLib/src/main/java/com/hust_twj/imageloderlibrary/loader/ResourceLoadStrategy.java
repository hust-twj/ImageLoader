package com.hust_twj.imageloderlibrary.loader;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.hust_twj.imageloderlibrary.constant.Schema;
import com.hust_twj.imageloderlibrary.request.LoadRequest;


/**
 * 资源图片加载器
 * Created by Wenjing.Tang on 2019-07-16.
 */
public class ResourceLoadStrategy extends BaseLoadStrategy {

    @Override
    public Bitmap onLoadImage(LoadRequest request) {
        Bitmap bitmap = null;
        try {
            int resID = Integer.parseInt(request.uri.split(Schema.SPIT)[1]);
            if (request.mImageView == null) {
                return null;
            }
            Resources resources = request.mImageView.getResources();
            bitmap = BitmapFactory.decodeResource(resources, resID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}
