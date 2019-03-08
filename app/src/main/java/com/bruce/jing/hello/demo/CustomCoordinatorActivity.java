package com.bruce.jing.hello.demo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Window;
import android.widget.FrameLayout;

import com.bruce.jing.hello.demo.adapter.SimpleItemAdapter;
import com.bruce.jing.hello.demo.util.system.DeviceUtil;
import com.bruce.jing.hello.demo.widget.MySimpleCoordinatorLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * -------------------------------------
 * 作者：bruce jing
 * -------------------------------------
 * 时间：2018/8/18 下午2:09
 * -------------------------------------
 * 描述：
 * -------------------------------------
 * 备注：
 * -------------------------------------
 */
public class CustomCoordinatorActivity extends AppCompatActivity {


    private List<String> listData = new ArrayList<>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DeviceUtil.setStatusBarTransparent(this);
        getWindow().addFlags(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activit_custom_coordinator);

        initData();


        final MySimpleCoordinatorLayout myScLayout = (MySimpleCoordinatorLayout) findViewById(R.id.myScLayout);



//        FrameLayout headLayout = (FrameLayout) LayoutInflater.from(this).inflate(R.layout.coordinator_head_view,null);
//        myScLayout.setHeadLayout(headLayout,new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1500));
        FrameLayout mainLayout = (FrameLayout) LayoutInflater.from(this).inflate(R.layout.coordinate_main_view, null);
        myScLayout.setMainLayout(mainLayout/*,new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1000)*/);


        RecyclerView coordinateRecyclerView = mainLayout.findViewById(R.id.coordinateRecyclerView);



        SimpleItemAdapter adapter = new SimpleItemAdapter(listData);
        coordinateRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        coordinateRecyclerView.setAdapter(adapter);



    }

    private void initData() {

        for (int i = 0; i < 50; i ++){
            String s = "第 "+i+" 个item";
            listData.add(i,s);
        }

    }


}
