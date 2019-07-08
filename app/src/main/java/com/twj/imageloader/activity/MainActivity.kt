package com.twj.imageloader.activity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.hust_twj.imageloderlibrary.ImageLoader
import com.hust_twj.imageloderlibrary.cache.DoubleCache
import com.hust_twj.imageloderlibrary.config.ImageLoaderConfig
import com.twj.imageloader.R
import com.twj.imageloader.activity.local.LoadLocalImageActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initImageLoader()

        tv_load_local.setOnClickListener {
            startActivity(Intent(this@MainActivity, LoadLocalImageActivity::class.java))
        }

        tv_load_remote.setOnClickListener {
            startActivity(Intent(this@MainActivity, LoadRemoteImageActivity::class.java))
        }

        tv_my_list.setOnClickListener {
            startActivity(Intent(this@MainActivity, MyListActivity::class.java))
        }

        tv_glide_list.setOnClickListener {
            startActivity(Intent(this@MainActivity, GlideListActivity::class.java))
        }
    }

    /**
     * 初始化ImageLoader
     */
    private fun initImageLoader() {
        val config = ImageLoaderConfig()
                .placeHolder(R.drawable.img_loading)
                .error(R.drawable.img_error)
                .cache(DoubleCache(this))
                .threadCount(4)
        ImageLoader.with(this).init(config)
    }
}
