package com.twj.imageloader.activity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.twj.imageloader.R
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
        }

        tv_list.setOnClickListener {
            startActivity(Intent(this@MainActivity, ListActivity::class.java))
        }
    }
}
