package com.hust_twj.imageloderlibrary.utils;

import android.annotation.SuppressLint;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Description ：利用Provider获取context
 * Created by Wenjing.Tang on 2019-07-11.
 */
public class LoaderProvider extends ContentProvider {

    @SuppressLint("StaticFieldLeak")
    public static Context mContext;

    @Override
    public boolean onCreate() {
        mContext = getContext();
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@Nullable Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        return null;
    }

    @Nullable
    @Override
    public String getType(@Nullable Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@Nullable Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete(@Nullable Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@Nullable Uri uri, @Nullable ContentValues values, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        return 0;
    }

}
