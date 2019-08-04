package com.hust_twj.imageloderlibrary.config;

/**
 * 图片下载过程中或下载失败的资源
 *
 * @author hust_twj
 * @date 2019/7/12
 */
public class DisplayConfig {

    public static final int DEFAULT_WIDTH = 200;
    public static final int DEFAULT_HEIGHT = 200;

    /**
     * 加载中的资源占位
     */
    public int placeHolderResId = -1;

    /**
     * 加载失败的资源占位
     */
    public int errorResId = -1;

    /**
     * 是否展示原图（false：根据控件大小展示图片，默认值； true：原图展示）
     */
    public boolean displayRaw = false;

}
