package com.bruce.jing.hello.demo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.os.MessageQueue;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import com.bruce.jing.hello.demo.adapter.BaseRecyclerAdapter;
import com.bruce.jing.hello.demo.adapter.GridSongCardAdapter;
import com.bruce.jing.hello.demo.adapter.LinearSongCardAdapter;
import com.bruce.jing.hello.demo.adapter.TitleAdapter;
import com.bruce.jing.hello.demo.util.CommonUtil;
import com.bruce.jing.hello.demo.widget.tmp.SpaceItemDecoration;

import java.util.Arrays;


public class MultiScreenAdapterActivity extends AppCompatActivity {

    private static final String TAG = "MultiScreenAdapterActiv";

    public static final int SCREEN_HEIGHT_DIVIDER = 864;
    public static final int WINDOW_MIN_WIDTH_FOR_FOUR_COLUMN = 1136;
    public static final int WINDOW_MIN_WIDTH_FOR_FIVE_COLUMN = 1392;
    public static final int WINDOW_MIN_WIDTH_FOR_SIX_COLUMN = 1648;
    public static final int THREE_COLUMN = 3;
    public static final int FOUR_COLUMN = 4;
    public static final int FIVE_COLUMN = 5;
    public static final int SIX_COLUMN = 6;


    private RecyclerView mTypeTabRecyclerView;
    private TitleAdapter mTabAdapter;

    private ConstraintLayout mRootLayout;
    private FrameLayout mFragmentContainer;
    private RecyclerView mHotRecommendRecyclerView;
    private BaseRecyclerAdapter mCardAdapter;


    //        private static String[] mTabTitles = {"热门推荐","排行榜单","本地音乐","我的音乐","其他TAB1","其他TAB2","其他TAB3","其他TAB4"};
    private static String[] mTabTitles = {"热门推荐", "排行榜单", "本地音乐", "我的音乐"};

    private static String[] mCardNames = {"今日推荐", "个性电台", "Say SomeThings", "生如夏花", "滴答滴滴答滴滴答滴答滴答滴", "滴答滴滴答滴滴答滴答滴答滴",
            "来左手画条龙右手画彩虹胸口比划一个郭富城", "啦啦啦啦", "啦啦啦啦", "我是卖报的小画家", "我是一个粉刷匠", "粉刷本领强", "我有一个新房子",
            "大河向东流啊", "天上的行星参北斗啊", "我吃蒸羊羔蒸熊掌整鹿尾儿烧雏鸡烧花鸭烧子鹅"};


    private MessageQueue.IdleHandler mIdleHandler = new MessageQueue.IdleHandler() {
        @Override
        public boolean queueIdle() {
            initGridLayout();
            Looper.myQueue().removeIdleHandler(mIdleHandler);
            return false;
        }
    };

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
        mRootLayout = (ConstraintLayout) findViewById(R.id.cl_main_activity_layout);
        initHeadView();
        initHotRecommendView();
        initTestView();
        initListener();

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
        int screenWidth = CommonUtil.px2dip(this, CommonUtil.getScreenWidth(this));
        Log.d(TAG, "initHotRecommendView screenHeight = " + screenHeight + " , screenWidth = " + screenWidth);
        int screenHeightDp = CommonUtil.getAvailableScreenHeightDp(this);
        int screenWidthDp = CommonUtil.getAvailableScreenWidthDp(this);
        int smallestWidthDp = CommonUtil.getSmallestWidthDp(this);
        Log.d(TAG, "initHotRecommendView screenHeightDp = " + screenHeightDp + " , screenWidthDp = " + screenWidthDp + " . smallestWidthDp = " + smallestWidthDp);

        //使用动态加载的方式
        if (screenHeight >= SCREEN_HEIGHT_DIVIDER) {
            LayoutInflater.from(this).inflate(R.layout.main_content_layout_for_height, mRootLayout, true);
        } else {
            LayoutInflater.from(this).inflate(R.layout.main_content_layout, mRootLayout, true);
        }
        mFragmentContainer = (FrameLayout) findViewById(R.id.fl_card_view_container);
        LayoutInflater.from(this).inflate(R.layout.fragment_hot_recommend, mFragmentContainer, true);
        mHotRecommendRecyclerView = mFragmentContainer.findViewById(R.id.rlv_hot_recommend);


        //初始化


        if (screenHeight >= SCREEN_HEIGHT_DIVIDER) {
            initGridLayout();
        } else {
            initLinearLayout();
        }


    }

    private void initLinearLayout() {
        mCardAdapter = new LinearSongCardAdapter(this);
        mCardAdapter.setData(Arrays.asList(mCardNames));

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.HORIZONTAL);

        mHotRecommendRecyclerView.setLayoutManager(llm);
        mHotRecommendRecyclerView.setAdapter(mCardAdapter);


        int rightSpace = CommonUtil.dip2px(this, 16f);
        SpaceItemDecoration spaceItemDecoration = new SpaceItemDecoration(0, 0, rightSpace, 0, new SpaceItemDecoration.LinearHorizontalLayoutPolicy());
        mHotRecommendRecyclerView.addItemDecoration(spaceItemDecoration);

        mHotRecommendRecyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "mHotRecommendRecyclerView height = " + mHotRecommendRecyclerView.getHeight() + " ,width = " + mHotRecommendRecyclerView.getWidth());
            }
        }, 500);
    }

    private void initGridLayout() {
        mCardAdapter = new GridSongCardAdapter(this);
        mCardAdapter.setData(Arrays.asList(mCardNames));

        GridLayoutManager glm = new GridLayoutManager(this, THREE_COLUMN);
        updateSpanCount(glm);

        mHotRecommendRecyclerView.setAdapter(mCardAdapter);
        mHotRecommendRecyclerView.setLayoutManager(glm);

        int rightSpace = CommonUtil.dip2px(this, 16f);
        int bottomSpace = CommonUtil.dip2px(this, 32f);
        SpaceItemDecoration itemDecoration = new SpaceItemDecoration(0, 0, rightSpace, bottomSpace, new SpaceItemDecoration.GridVerticalLayoutPolicy());
        mHotRecommendRecyclerView.addItemDecoration(itemDecoration);

    }

    private void updateSpanCount(GridLayoutManager glm) {
        if(glm == null){
            return;
        }
        int width = CommonUtil.px2dip(this, mRootLayout.getWidth());
        Log.d(TAG, "initGridLayout width = " + width);
        if (width < WINDOW_MIN_WIDTH_FOR_FOUR_COLUMN) {
//            if (glm.getSpanCount() != THREE_COLUMN) {
                glm.setSpanCount(THREE_COLUMN);
//            }
        } else if (width >= WINDOW_MIN_WIDTH_FOR_FOUR_COLUMN && width < WINDOW_MIN_WIDTH_FOR_FIVE_COLUMN) {
            if (glm.getSpanCount() != FOUR_COLUMN) {
                glm.setSpanCount(FOUR_COLUMN);
            }
        } else if (width >= WINDOW_MIN_WIDTH_FOR_FIVE_COLUMN && width < WINDOW_MIN_WIDTH_FOR_SIX_COLUMN) {
            if (glm.getSpanCount() != FIVE_COLUMN) {
                glm.setSpanCount(FIVE_COLUMN);

            }
        } else {
            if (glm.getSpanCount() != SIX_COLUMN) {
                glm.setSpanCount(SIX_COLUMN);
            }
        }
    }

    private int mPreWidth = 0;
    private int mPreHeight = 0;

    private void initListener() {

        mRootLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
//                int width = mRootLayout.getWidth();
//                int recyclerViewWidth = mHotRecommendRecyclerView.getWidth();
//                Log.d(TAG,"onGlobalLayout width = "+width+" , recyclerViewWidth = "+recyclerViewWidth);
//                int height = mRootLayout.getHeight();
//                if (mPreWidth != width) {
//                    mPreWidth = width;
//                    RecyclerView.LayoutManager layoutManager = mHotRecommendRecyclerView.getLayoutManager();
//                    if (layoutManager instanceof GridLayoutManager) {
//                        updateSpanCount((GridLayoutManager) layoutManager);
//                    }
//                }
//                if (mPreHeight != height) {
//                    mPreHeight = height;
//                }
            }
        });

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
                int start = mRootLayout.getWidth();
                int end = 1920;
                ValueAnimator animator = ValueAnimator.ofInt(start, end);
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        int width = (int) animation.getAnimatedValue();
                        Log.d(TAG, "onAnimationUpdate width = " + width);
                        ViewGroup.LayoutParams layoutParams = mRootLayout.getLayoutParams();
                        layoutParams.width = width;
                        mRootLayout.setLayoutParams(layoutParams);

                    }
                });

                animator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        RecyclerView.LayoutManager layoutManager = mHotRecommendRecyclerView.getLayoutManager();
                        if (layoutManager instanceof GridLayoutManager) {
                        updateSpanCount((GridLayoutManager) layoutManager);
                        }
                    }
                });

                animator.setDuration(300);
                animator.start();
            }
        });

        View toNarrower = findViewById(R.id.btn_to_narrower);
        toNarrower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int start = mRootLayout.getWidth();
                int end = 1000;
                ValueAnimator animator = ValueAnimator.ofInt(start, end);
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        int width = (int) animation.getAnimatedValue();
                        Log.d(TAG, "onAnimationUpdate height = " + width);
                        ViewGroup.LayoutParams layoutParams = mRootLayout.getLayoutParams();
                        layoutParams.width = width;
                        mRootLayout.setLayoutParams(layoutParams);

                    }
                });

                animator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        RecyclerView.LayoutManager layoutManager = mHotRecommendRecyclerView.getLayoutManager();
                        if (layoutManager instanceof GridLayoutManager) {
                            updateSpanCount((GridLayoutManager) layoutManager);
                        }
                    }
                });
                animator.setDuration(300);
                animator.start();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        int screenHeight = CommonUtil.px2dip(this, CommonUtil.getScreenHeight(this));
        if (screenHeight >= SCREEN_HEIGHT_DIVIDER) {
            Looper.myQueue().addIdleHandler(mIdleHandler);
        }
    }
}
