package com.twj.imageloader.activity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.hust_twj.imageloderlibrary.ImageLoader
import com.twj.imageloader.R
import com.twj.imageloader.activity.local.LoadLocalImageActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tv_load_local.setOnClickListener {
            startActivity(Intent(this@MainActivity, LoadLocalImageActivity::class.java))
        }

        tv_load_remote.setOnClickListener {
            startActivity(Intent(this@MainActivity, LoadRemoteImageActivity::class.java))
            overridePendingTransition(0,0)
        }

        tv_my_list.setOnClickListener {
            startActivity(Intent(this@MainActivity, MyListActivity::class.java))
        }

        tv_glide_list.setOnClickListener {
            startActivity(Intent(this@MainActivity, GlideListActivity::class.java))
        }

        tv_picasso_list.setOnClickListener {
            startActivity(Intent(this@MainActivity, PicassoListActivity::class.java))
        }

        clean_all_cache.setOnClickListener {
            ImageLoader.get().clearCache()
        }

        clean_memory_cache.setOnClickListener {
            ImageLoader.get().clearMemoryCache()
        }

        clean_disk_cache.setOnClickListener {
            ImageLoader.get().clearDiskCache()
        }

    }

}
