package com.hust_twj.imageloderlibrary.loader;


import com.hust_twj.imageloderlibrary.utils.SchemaUtil;

/**
 * 加载器管理类
 * 根据不同的uri使用不同的加载策略
 * Created by Wenjing.Tang on 2019-07-16.
 */
public class LoadManager {

    private static LoadManager INSTANCE;

    public static LoadManager getInstance() {
        if (INSTANCE == null) {
            synchronized (LoadManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new LoadManager();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * 根据不同的uri获取不同的加载器
     *
     * @param uri uri
     * @return ILoader
     */
    public ILoadStrategy getLoader(String uri) {
        if (SchemaUtil.isFromNetwork(uri)) {
            return new NetworkLoadStrategy();
        } else if (SchemaUtil.isFromFile(uri)) {
            return new LocalLoadStrategy();
        } else if (SchemaUtil.isFromDrawable(uri)) {
            return new DrawableLoadStrategy();
        }
        return null;
    }

}
