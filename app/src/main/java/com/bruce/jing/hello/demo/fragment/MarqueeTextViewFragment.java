package com.bruce.jing.hello.demo.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bruce.jing.hello.demo.R;
import com.bruce.jing.hello.demo.widget.view.DanceView;
import com.bruce.jing.hello.demo.widget.view.MarqueeTextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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
public class MarqueeTextViewFragment extends Fragment {


    private View mRootView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_marquee_text_view, container, false);
        initView();
        return mRootView;
    }

    private void initView() {
        final MarqueeTextView danceView = mRootView.findViewById(R.id.marqueeTextView);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
