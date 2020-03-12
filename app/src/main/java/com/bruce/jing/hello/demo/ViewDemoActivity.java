package com.bruce.jing.hello.demo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import com.bruce.jing.hello.demo.adapter.BaseRecyclerViewAdapter2;
import com.bruce.jing.hello.demo.adapter.SimpleItemAdapter;
import com.bruce.jing.hello.demo.fragment.DanceViewFragment;
import com.bruce.jing.hello.demo.fragment.EmojiFragment;
import com.bruce.jing.hello.demo.fragment.FragmentContainerActivity;
import com.bruce.jing.hello.demo.fragment.IndicatorFragment;
import com.bruce.jing.hello.demo.fragment.RippleFragment;

import java.util.Arrays;
import java.util.List;

/**
 * -------------------------------------
 * 作者：bruce jing
 * -------------------------------------
 * 时间：2018/10/25 下午9:11
 * -------------------------------------
 * 描述：
 * -------------------------------------
 * 备注：
 * -------------------------------------
 */
public class ViewDemoActivity extends AppCompatActivity implements BaseRecyclerViewAdapter2.OnItemClickListener {


    private static final String[] sData = {
            "波纹",
            "进度条",
            "表情",
            "指示条",
            "按压",
            "音乐跳舞",
    };


    private RecyclerView mRecyclerView;
    private SimpleItemAdapter mAdapter;

    public static final void launch(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, ViewDemoActivity.class);
        context.startActivity(intent);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_demo);
        mRecyclerView = (RecyclerView) findViewById(R.id.rlv_mainPage);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(llm);
        initData();
    }

    private void initData() {
        List<String> data = Arrays.asList(sData);
        mAdapter = new SimpleItemAdapter(data);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setItemClickListener(this);
    }

    @Override
    public void onItemClick(int position, View itemView) {
        switch (position) {
            case 0:
                FragmentContainerActivity.startFragment(this, RippleFragment.class, null);
                break;
            case 1:

                break;
            case 2:
                FragmentContainerActivity.startFragment(this, EmojiFragment.class, null);
                break;
            case 3:
                FragmentContainerActivity.startFragment(this, IndicatorFragment.class,null);
                break;
            case 5:
                FragmentContainerActivity.startFragment(this, DanceViewFragment.class,null);
                break;
        }
    }


}
