package com.bruce.jing.hello.demo.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bruce.jing.hello.demo.R;
import com.bruce.jing.hello.demo.widget.view.DanceView;

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
public class DanceViewFragment extends Fragment {


    private View mRootView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_dance_view, container, false);
        initView();
        return mRootView;
    }

    private void initView() {
        final DanceView danceView = mRootView.findViewById(R.id.danceView);
        danceView.startDance();

        mRootView.findViewById(R.id.btn_show).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                danceView.showDancer();
            }
        });

        mRootView.findViewById(R.id.btn_hide).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                danceView.hideDancer();
            }
        });

        mRootView.findViewById(R.id.btn_dance).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                danceView.startDance();
            }
        });

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
