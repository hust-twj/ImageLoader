package com.hust_twj.imageloderlibrary.config;

import com.hust_twj.imageloderlibrary.constant.Constants;

/**
 * 显示配置类
 *
 * @author hust_twj
 * @date 2019/7/12
 */
public class DisplayConfig {

    /**
     * 获取不到图片控件宽高时，默认宽高值
     */
    public int defaultWidth = Constants.DEFAULT_IMAGE_WIDTH;
    public int defaultHeight = Constants.DEFAULT_IMAGE_HEIGHT;

    /**
     * 加载中的资源占位
     */
    public int placeHolderResId = Constants.DEFAULT_RES_ID;

    /**
     * 加载失败的资源占位
     */
    public int errorResId = Constants.DEFAULT_RES_ID;

    /**
     * 是否展示原图（false：根据控件大小展示图片，默认值； true：原图展示）
     */
    public boolean displayRaw = false;

}
