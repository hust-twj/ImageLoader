package com.hust_twj.imageloderlibrary.loader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.hust_twj.imageloderlibrary.utils.IOUtil;
import com.hust_twj.imageloderlibrary.task.LoadRequest;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 网络图片加载器
 * Created by Wenjing.Tang on 2019-07-16.
 */
public class UrlLoadStrategy extends BaseLoadStrategy {

    @Override
    public Bitmap onLoadImage(LoadRequest loadRequest) {
        final String imageUrl = loadRequest.uri;
        InputStream is = null;
        Bitmap bitmap = null;
        HttpURLConnection conn = null;
        try {
            URL url = new URL(imageUrl);
            conn = (HttpURLConnection) url.openConnection();
            is = new BufferedInputStream(conn.getInputStream());
            bitmap = BitmapFactory.decodeStream(is, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                IOUtil.closeQuietly(is);
            }
            if (conn != null) {
                conn.disconnect();
            }
        }
        return bitmap;
    }
}
