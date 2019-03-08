package com.bruce.jing.hello.demo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.bruce.jing.hello.demo.adapter.BaseRecyclerView;
import com.bruce.jing.hello.demo.adapter.SimpleItemAdapter;
import com.bruce.jing.hello.demo.fragment.EmojiFragment;
import com.bruce.jing.hello.demo.fragment.FragmentContainerActivity;
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
public class ViewDemoActivity extends AppCompatActivity implements BaseRecyclerView.OnItemClickListener {


    private static final String[] sData = {
            "波纹",
            "进度条",
            "表情"
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
        mRecyclerView = findViewById(R.id.rlv_mainPage);
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
        }
    }


}
