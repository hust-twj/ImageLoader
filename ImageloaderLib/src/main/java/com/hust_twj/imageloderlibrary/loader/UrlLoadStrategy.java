package com.hust_twj.imageloderlibrary.loader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.hust_twj.imageloderlibrary.utils.IOUtil;
import com.hust_twj.imageloderlibrary.task.LoadRequest;
import com.hust_twj.imageloderlibrary.utils.ImageDecoder;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 网络图片加载器
 * Created by Wenjing.Tang on 2019-07-16.
 */
public class UrlLoadStrategy extends BaseLoadStrategy {

    private static final String TAG = UrlLoadStrategy.class.getSimpleName();

    @Override
    public Bitmap onLoadImage(LoadRequest request) {
        final String imageUrl = request.uri;
        InputStream inputStream = null;
        Bitmap bitmap = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(imageUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            inputStream = new BufferedInputStream(urlConnection.getInputStream());
            bitmap = BitmapFactory.decodeStream(inputStream, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                IOUtil.closeQuietly(inputStream);
            }
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        if (bitmap == null) {
            return null;
        }
        Log.e(TAG, "显示原图：" + request.mDisplayConfig.displayRaw + "  " +
                "原始图片大小：" + bitmap.getWidth() + "*" + bitmap.getHeight() + "  " +
                "处理后图片大小：" +  ImageDecoder.decodeBitmap(bitmap, request.getImageViewWidth(), request.getImageViewHeight()).getWidth() +
                "*" + ImageDecoder.decodeBitmap(bitmap, request.getImageViewWidth(), request.getImageViewHeight()).getHeight());
        if (request.mDisplayConfig.displayRaw) {
            return bitmap;
        }
        return ImageDecoder.decodeBitmap(bitmap, request.getImageViewWidth(), request.getImageViewHeight());
    }

}
