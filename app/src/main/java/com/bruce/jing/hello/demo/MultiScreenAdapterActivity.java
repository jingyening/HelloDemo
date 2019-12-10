package com.bruce.jing.hello.demo;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.bruce.jing.hello.demo.adapter.SongCardAdapter;
import com.bruce.jing.hello.demo.adapter.TitleAdapter;
import com.bruce.jing.hello.demo.util.CommonUtil;
import com.bruce.jing.hello.demo.widget.tmp.SpaceItemDecoration;

import java.util.Arrays;


public class MultiScreenAdapterActivity extends AppCompatActivity {

    private static final String TAG = "MultiScreenAdapterActiv";

    public static final int SCREEN_HEIGHT_DIVIDER = 864;

    private RecyclerView mTypeTabRecyclerView;
    private TitleAdapter mTabAdapter;

    private FrameLayout mFragmentContainer;
    private RecyclerView mHotRecommendRecyclerView;
    private SongCardAdapter mCardAdapter;


//        private static String[] mTabTitles = {"热门推荐","排行榜单","本地音乐","我的音乐","其他TAB1","其他TAB2","其他TAB3","其他TAB4"};
    private static String[] mTabTitles = {"热门推荐", "排行榜单", "本地音乐", "我的音乐"};

    private static String[] mCardNames = {"今日推荐", "个性电台", "Say SomeThings", "生如夏花", "滴答滴滴答滴滴答滴答滴答滴", "滴答滴滴答滴滴答滴答滴答滴", "来左手画条龙右手画彩虹胸口比划一个郭富城", "啦啦啦啦", "啦啦啦啦"};


    public static void launch(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, MultiScreenAdapterActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_multi_screen_adapter);
        initView();
    }

    private void initView() {
        initHeadView();
        initHotRecommendView();
        initTestView();
    }

    private void initHeadView() {
        mTypeTabRecyclerView = (RecyclerView) findViewById(R.id.rlv_type_tab);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.HORIZONTAL);
        mTypeTabRecyclerView.setLayoutManager(llm);
        mTabAdapter = new TitleAdapter(this);
        mTabAdapter.setData(Arrays.asList(mTabTitles));
        mTypeTabRecyclerView.setAdapter(mTabAdapter);
    }

    private void initHotRecommendView() {
        int screenHeight = CommonUtil.px2dip(this, CommonUtil.getScreenHeight(this));
        if (screenHeight > SCREEN_HEIGHT_DIVIDER) {

        }

        mFragmentContainer = (FrameLayout) findViewById(R.id.fl_card_view_container);
        LayoutInflater.from(this).inflate(R.layout.fragment_hot_recommend, mFragmentContainer, true);
        mHotRecommendRecyclerView = mFragmentContainer.findViewById(R.id.rlv_hot_recommend);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.HORIZONTAL);
        mHotRecommendRecyclerView.setLayoutManager(llm);
        mCardAdapter = new SongCardAdapter(this);
        mCardAdapter.setData(Arrays.asList(mCardNames));
        mHotRecommendRecyclerView.setAdapter(mCardAdapter);
        mHotRecommendRecyclerView.addItemDecoration(new SpaceItemDecoration(0, 0, 16, 0));

        mHotRecommendRecyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "mHotRecommendRecyclerView height = " + mHotRecommendRecyclerView.getHeight());
            }
        }, 500);
    }


    private void initTestView() {
        View toHigher = findViewById(R.id.btn_to_higher);
        toHigher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int start = mHotRecommendRecyclerView.getHeight();
                int end = 300;
                ValueAnimator animator = ValueAnimator.ofInt(start, end);
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        int height = (int) animation.getAnimatedValue();
                        Log.d(TAG, "onAnimationUpdate height = " + height);
                        ViewGroup.LayoutParams layoutParams = mHotRecommendRecyclerView.getLayoutParams();
                        layoutParams.height = height;
                        mHotRecommendRecyclerView.setLayoutParams(layoutParams);

                    }
                });

                animator.setDuration(300);
                animator.start();

            }
        });

        View toShorter = findViewById(R.id.btn_to_shorter);
        toShorter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int start = mHotRecommendRecyclerView.getHeight();
                int end = 150;
                ValueAnimator animator = ValueAnimator.ofInt(start, end);
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        int height = (int) animation.getAnimatedValue();
                        Log.d(TAG, "onAnimationUpdate height = " + height);
                        ViewGroup.LayoutParams layoutParams = mHotRecommendRecyclerView.getLayoutParams();
                        layoutParams.height = height;
                        mHotRecommendRecyclerView.setLayoutParams(layoutParams);

                    }
                });

                animator.setDuration(300);
                animator.start();
            }
        });
        View toLonger = findViewById(R.id.btn_to_longer);
        toLonger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        View toNarrower = findViewById(R.id.btn_to_narrower);
        toNarrower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }


}
