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

    private List<String> imageList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        RecyclerView mRvPhotoList = findViewById(R.id.rv_photo_list);
        mRvPhotoList.setLayoutManager(new LinearLayoutManager(this));

        PhotoAdapter adapter = new PhotoAdapter(this);
        adapter.setDataList(generateImage());
        mRvPhotoList.setAdapter(adapter);
    }

    private List<String> generateData() {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            list.add("");
        }
        return list;
    }

    /**
     * http://www.mafengwo.cn/photo/10754/scenery_13617685/395418101.html
     */
    private List<String> generateImage() {
        imageList.add("http://p3-q.mafengwo.net/s12/M00/5F/01/wKgED1va9ZeAf0k5AAijT_WanQ006.jpeg");
        imageList.add("http://n2-q.mafengwo.net/s12/M00/5E/0A/wKgED1va9LaAKZOEAAn9xR-vnu480.jpeg");
        imageList.add("http://n3-q.mafengwo.net/s12/M00/5E/11/wKgED1va9MCAOLIGAAoS3bnVSz097.jpeg");
        imageList.add("http://p3-q.mafengwo.net/s12/M00/5E/12/wKgED1va9MKAXAROAAluoeL0uCs08.jpeg");
        imageList.add("http://b3-q.mafengwo.net/s12/M00/5F/05/wKgED1va9Z-AAK57AAHJEEHa_Hs93.jpeg");
        imageList.add("http://n3-q.mafengwo.net/s12/M00/5F/05/wKgED1va9aCAGHnwAAhsktZK18823.jpeg");
        imageList.add("http://b4-q.mafengwo.net/s12/M00/6A/DB/wKgED1vbACWAeMSTAAalv_JAClA65.jpeg");
        imageList.add("http://p1-q.mafengwo.net/s12/M00/6F/6A/wKgED1vbA8KADU1-AA4lm2o5tB429.jpeg");
        imageList.add("http://n1-q.mafengwo.net/s12/M00/6F/E6/wKgED1vbBAuASdxXAAk6eerEjB495.jpeg");
        imageList.add("http://p4-q.mafengwo.net/s12/M00/B1/EB/wKgED1vbpbuAfDCKAAU6657jy3I79.jpeg");
        imageList.add("http://n3-q.mafengwo.net/s12/M00/B1/F4/wKgED1vbpcuAJsYIAAVmCljiQ7k97.jpeg");
        imageList.add("http://n2-q.mafengwo.net/s12/M00/B1/F6/wKgED1vbpc-AQmQcAAa5ga7V9z036.jpeg");
        imageList.add("http://b1-q.mafengwo.net/s12/M00/12/49/wKgED1vb-puATZyGAAl3N-lU8As49.jpeg");
        imageList.add("http://p2-q.mafengwo.net/s12/M00/12/4A/wKgED1vb-pyAfmmSAAodet3oiMA14.jpeg");
        imageList.add("http://b3-q.mafengwo.net/s12/M00/12/4E/wKgED1vb-p-AZVHRAAjBa3VWikE84.jpeg");
        imageList.add("http://n4-q.mafengwo.net/s12/M00/12/52/wKgED1vb-qGAKxK8AA9K1e-SqEk04.jpeg");
        imageList.add("http://b3-q.mafengwo.net/s12/M00/12/54/wKgED1vb-qKASXH6AAaYxrp3-I813.jpeg");
        imageList.add("http://b1-q.mafengwo.net/s12/M00/12/57/wKgED1vb-qSAdj3dAAzNa0SADPU87.jpeg");
        imageList.add("http://p2-q.mafengwo.net/s12/M00/E2/AC/wKgED1vfoVeATL0AAAcpzeOujb075.jpeg");
        imageList.add("http://b3-q.mafengwo.net/s12/M00/E2/B1/wKgED1vfoVqAY2e4AAZFVSJEfDs84.jpeg");
        imageList.add("http://b4-q.mafengwo.net/s12/M00/E2/B4/wKgED1vfoVyANAFeAAg6MPuyMJ409.jpeg");
        imageList.add("http://n2-q.mafengwo.net/s12/M00/E2/B6/wKgED1vfoV6ABgm5AAn_YYpivLk38.jpeg");
        imageList.add("http://n1-q.mafengwo.net/s12/M00/EE/3B/wKgED1vfrG2AZb6BAAwpDQDN60c13.jpeg");
        imageList.add("http://p2-q.mafengwo.net/s12/M00/EE/3D/wKgED1vfrG-AdY8oAAqY4QzuXjA88.jpeg");
        imageList.add("http://p4-q.mafengwo.net/s12/M00/EE/40/wKgED1vfrHGALsUPAAk9itYadwE50.jpeg");
        imageList.add("http://b3-q.mafengwo.net/s12/M00/EE/44/wKgED1vfrHKAVu4WAAlwIXQlotE69.jpeg");
        imageList.add("http://b2-q.mafengwo.net/s12/M00/EE/47/wKgED1vfrHSAPzRPAAcrBaBl4I465.jpeg");
        imageList.add("http://b1-q.mafengwo.net/s12/M00/EE/52/wKgED1vfrHiAYPXQAAuW_WJh8o041.jpeg");
        imageList.add("http://b4-q.mafengwo.net/s12/M00/F7/61/wKgED1vfs2mAOXMNAAdTHLvSdZE21.jpeg");
        imageList.add("http://b3-q.mafengwo.net/s12/M00/4A/EE/wKgED1vf6wuAW6SkAAgryXX5yYA04.jpeg");
        imageList.add("http://n3-q.mafengwo.net/s12/M00/4B/02/wKgED1vf6xaAef-ZAAYpylVzAII89.jpeg");
        imageList.add("http://b4-q.mafengwo.net/s12/M00/4B/10/wKgED1vf6xuAJq-EAAbDTgYhTdo21.jpeg");
        imageList.add("http://n1-q.mafengwo.net/s12/M00/4B/19/wKgED1vf6x6ALKlqAAiWJ8OXrcU94.jpeg");
        imageList.add("http://p2-q.mafengwo.net/s12/M00/4B/21/wKgED1vf6yOAfh0fAAtLwBFbC5A84.jpeg");
        imageList.add("http://p2-q.mafengwo.net/s12/M00/4B/2C/wKgED1vf6ymADQQkAAzt1HGqMBc45.jpeg");
        imageList.add("http://p2-q.mafengwo.net/s12/M00/59/E5/wKgED1vf9MCAFfP5AAeKHiML9Ds92.jpeg");
        imageList.add("http://n1-q.mafengwo.net/s12/M00/59/EC/wKgED1vf9MOAIughAA-OZ1SV-lA13.jpeg");
        imageList.add("http://n2-q.mafengwo.net/s12/M00/5A/16/wKgED1vf9N2ACxybAAn9ujTK5g820.jpeg");
        imageList.add("http://n4-q.mafengwo.net/s12/M00/98/70/wKgED1vhPUOALCyoAAny_lJtCSs42.jpeg");
        imageList.add("http://n3-q.mafengwo.net/s12/M00/98/76/wKgED1vhPUaALcI6AAz_xkq_QPo10.jpeg");
        imageList.add("http://p4-q.mafengwo.net/s12/M00/A4/25/wKgED1vhRAWAcQaIAAsF2oyxorc69.jpeg");
        imageList.add("http://n1-q.mafengwo.net/s12/M00/AD/FB/wKgED1vhSWmADFgOAAin3XglObI01.jpeg");
        imageList.add("http://p4-q.mafengwo.net/s12/M00/64/E0/wKgED1viRvmADRijAAtAh4xaB3w65.jpeg");
        imageList.add("http://p1-q.mafengwo.net/s12/M00/64/E7/wKgED1viRwCAC4tXAApM8LrNwqc60.jpeg");
        imageList.add("http://b3-q.mafengwo.net/s12/M00/64/F1/wKgED1viRw2ADqlRAAWy-zy__DY94.jpeg");
        imageList.add("http://p4-q.mafengwo.net/s12/M00/65/02/wKgED1viRyaAX7toAAdjqfqeTFs74.jpeg");
        imageList.add("http://p1-q.mafengwo.net/s12/M00/A2/BC/wKgED1vjobSAcrZtAAqyBSdrRcA33.jpeg");
        imageList.add("http://b2-q.mafengwo.net/s12/M00/F9/73/wKgED1vj3oeAIh2QAAgzya28aDQ54.jpeg");
        return imageList;
    }

}
