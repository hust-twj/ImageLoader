package com.twj.imageloader

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_load_local_image.*

/**
 * description ：加载本地图片
 * Created by Wenjing.Tang on 2019-05-22.
 */
class LoadLocalImageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_load_local_image)

        tv_load_png.setOnClickListener {
            startActivity(Intent(this@LoadLocalImageActivity, LocalPngActivity::class.java))
        }

        tv_load_jpg.setOnClickListener {
            startActivity(Intent(this@LoadLocalImageActivity, LocalJpgActivity::class.java))
        }

        tv_load_jpeg.setOnClickListener {
            startActivity(Intent(this@LoadLocalImageActivity, LocalJpegActivity::class.java))
        }

        tv_load_webp.setOnClickListener {
            startActivity(Intent(this@LoadLocalImageActivity, LocalWebpActivity::class.java))
        }
    }
}
