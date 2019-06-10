package com.twj.imageloader;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author hust_twj
 * @date 2019/6/10
 */
public class ListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        RecyclerView mRvPhotoList = findViewById(R.id.rv_photo_list);
        mRvPhotoList.setLayoutManager(new LinearLayoutManager(this));

        PhotoAdapter adapter = new PhotoAdapter(this);
        adapter.setDataList(generateData());
        mRvPhotoList.setAdapter(adapter);
    }

    private List<String> generateData() {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            list.add("");
        }
        return list;
    }
}
