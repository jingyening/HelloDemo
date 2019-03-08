package com.bruce.jing.hello.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.bruce.jing.hello.demo.adapter.BaseRecyclerView;
import com.bruce.jing.hello.demo.adapter.SimpleItemAdapter;
import com.bruce.jing.hello.demo.util.system.DeviceUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import tmp.TmpTestActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, BaseRecyclerView.OnItemClickListener{

    private static final String LOG_TAG = "MainActivity";


    private RecyclerView mRecyclerView;
    private SimpleItemAdapter mAdapter;

    public static final String[] sData = {
          "java",
          "test",
          "widget",
          "view",
          "animation"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DeviceUtil.setStatusBarTransparent(this);
        setContentView(R.layout.activity_main);

        mRecyclerView = findViewById(R.id.rlv_mainPage);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(llm);

        RecyclerView.ItemDecoration decoration = new RecyclerView.ItemDecoration() {
        };
        mRecyclerView.addItemDecoration(decoration);
        initData();
    }

    private void initData() {
        List<String> data = Arrays.asList(sData);
        mAdapter = new SimpleItemAdapter(data);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setItemClickListener(this);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onItemClick(int position, View itemView) {
        switch (position) {
            case 0:

                break;
            case 1:
                TmpTestActivity.launch(this);
                break;
            case 2:

                break;
            case 3:
                ViewDemoActivity.launch(this);
                break;
            case 4:

                break;
            default:

                break;
        }
    }
}
