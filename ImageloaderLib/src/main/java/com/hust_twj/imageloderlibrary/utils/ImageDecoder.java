package com.hust_twj.imageloderlibrary.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;

/**
 * Description ：图片解码、压缩工具类
 * Created by Wenjing.Tang on 2019-07-09.
 */
public class ImageDecoder {

    private static final String TAG = ImageDecoder.class.getSimpleName();

    public static Bitmap decodeBitmap(Bitmap bitmap, int reqWidth, int reqHeight) {
        Matrix matrix = new Matrix();

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.outWidth = bitmap.getWidth();
        options.outHeight = bitmap.getHeight();

        int inSampleSize= calculateInSampleSize(options, reqWidth, reqHeight);

        Log.e(TAG, "inSampleSize：" + inSampleSize);
        matrix.setScale(1.0f / inSampleSize, 1.0f / inSampleSize);
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return bitmap;
    }

    /**
     * 计算采样大小
     */
    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int width = options.outWidth;
        final int height = options.outHeight;

        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

}
