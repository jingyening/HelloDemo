package com.bruce.jing.hello.demo.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bruce.jing.hello.demo.R;

/**
 * -------------------------------------
 * 作者：bruce jing
 * -------------------------------------
 * 时间：2018/10/25 下午9:34
 * -------------------------------------
 * 描述：
 * -------------------------------------
 * 备注：
 * -------------------------------------
 */
public class EmojiFragment extends Fragment {


    private View mRootView;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_emoji_view,container,false);
        initView();
        return mRootView;
    }

    private void initView() {
        mRootView.findViewById(R.id.et_input_text);
        mRootView.findViewById(R.id.iv_emoji);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}