# ImageLoader
自己实现简单的图片加载框架ImageLoader，包含不同图片的加载策略、三级缓存

### 使用
- 全局配置
```
    LoaderConfig config = new LoaderConfig()
                    .cache(new DoubleCache(this))
                   .threadCount(4);
    ImageLoader.get().init(config);
```

- 加载图片

```
     ImageLoader.get()
                .load(url)
                .error(R.drawable.img_error)
                .placeHolder(R.drawable.img_place_holder)
                .listener(new ImageLoadListener() {
                    @Override
                    public void onResourceReady(Bitmap bitmap, String uri) {
                        Log.e("twj", "onResourceReady: " + uri);
                    }

                    @Override
                    public void onFailure() {
                        Log.e("twj", "onError: ");

                    }
                })
                .into(mIvRemote);
```

### 类图

![Image text](https://upload-images.jianshu.io/upload_images/6319734-8fa76cdff5cc155d.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/726/format/webp)

http://pw3zf0ic2.bkt.clouddn.com/uml.png
