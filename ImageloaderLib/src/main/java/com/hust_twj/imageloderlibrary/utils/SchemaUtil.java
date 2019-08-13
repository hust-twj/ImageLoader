package com.hust_twj.imageloderlibrary.utils;

import com.hust_twj.imageloderlibrary.constant.Schema;

/**
 * Description ：Schema工具类
 * Created by Wenjing.Tang on 2019-08-13.
 */
public class SchemaUtil {

    /**
     * 解析schema
     *
     * @param uri uri
     * @return schema
     */
    public static String parseSchema(String uri) {
        if (uri.contains(Schema.SPIT)) {
            return uri.split(Schema.SPIT)[0];
        }
        return uri;
    }

    public static boolean isFromNetwork(String uri) {
        return uri.startsWith(Schema.PREFIX_HTTP) || uri.startsWith(Schema.PREFIX_HTTPS);
    }

    public static boolean isFromFile(String uri) {
        return uri.startsWith(Schema.PREFIX_FILE) || uri.startsWith(Schema.PREFIX_CONTENT);
    }

    public static boolean isFromDrawable(String uri) {
        return uri.startsWith(Schema.PREFIX_RESOURCE);
    }

}
