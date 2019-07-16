package com.hust_twj.imageloderlibrary.config;

/**
 * 图片下载过程中或下载失败的资源
 *
 * @author hust_twj
 * @date 2019/6/12
 */
public class DisplayConfig {

    /**
     * 加载中的资源占位
     */
    public int loadingResId = -1;

    /**
     * 加载失败的资源占位
     */
    public int failedResId = -1;

    /**
     * 是否展示原图（false：默认根据控件大小优化图片的尺寸； true：原图展示）
     */
    public boolean displayRaw = false;

}
