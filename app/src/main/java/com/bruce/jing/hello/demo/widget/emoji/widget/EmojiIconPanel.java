package com.bruce.jing.hello.demo.widget.emoji.widget;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.bruce.jing.hello.demo.R;
import com.bruce.jing.hello.demo.widget.emoji.entity.BaseEmojiIconEntity;
import com.bruce.jing.hello.demo.widget.emoji.entity.EmojiIconGroupEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * 表情面板
 */
public class EmojiIconPanel extends EmojiIconPanelBase {



    private int mEmojiconColumns = EmojiIconPagerView.DEFAULT_EMOJI_ICON_COLUMNS;
    private int mBigEmojiIconColumns = EmojiIconPagerView.DEFAULT_BIG_EMOJI_ICON_COLUMNS;
    private EmojiIconScrollTabBar mTabBar;
    private EmojiIconIndicatorView mIndicatorView;
    public EmojiIconPagerView mPagerView;

    private List<EmojiIconGroupEntity> mEmojiIconGroupList = new ArrayList<EmojiIconGroupEntity>();


    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public EmojiIconPanel(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    public EmojiIconPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public EmojiIconPanel(Context context) {
        super(context);
        init(context, null);
    }


    private void init(Context context, AttributeSet attrs){
        LayoutInflater.from(context).inflate(R.layout.layout_chat_widget_emoji_panel, this);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.EmojiIconPanel);
        mEmojiconColumns = ta.getInt(R.styleable.EmojiIconPanel_emojiIconColumns, EmojiIconPagerView.DEFAULT_EMOJI_ICON_COLUMNS);
        mBigEmojiIconColumns = ta.getInt(R.styleable.EmojiIconPanel_bigEmojiIconRows, EmojiIconPagerView.DEFAULT_BIG_EMOJI_ICON_COLUMNS);
        ta.recycle();
        mPagerView = (EmojiIconPagerView) findViewById(R.id.pager_view);
        mIndicatorView = (EmojiIconIndicatorView) findViewById(R.id.indicator_view);
        mTabBar = (EmojiIconScrollTabBar) findViewById(R.id.tab_bar);

    }


    public void init(List<EmojiIconGroupEntity> groupEntities){
        if(groupEntities == null || groupEntities.size() == 0){
            return;
        }
        //添加底部tabbar
        for(EmojiIconGroupEntity groupEntity : groupEntities){
            mEmojiIconGroupList.add(groupEntity);
            mTabBar.addTab(groupEntity);
        }
        //初始化表情面板
        mPagerView.setPagerViewListener(new EmojiIconPagerViewListener());
        mPagerView.init(mEmojiIconGroupList, mEmojiconColumns, mBigEmojiIconColumns);

        //底部tab点击事件处理（滚动到当前表情面板）
        mTabBar.setTabBarItemClickListener(new EmojiIconScrollTabBar.EaseScrollTabBarItemClickListener() {

            @Override
            public void onItemClick(int position) {
                mPagerView.setGroupPostion(position);
            }
        });

    }


    /**
     * 添加表情组
     * @param groupEntity
     */
    public void addEmojiIconGroup(EmojiIconGroupEntity groupEntity){
        mEmojiIconGroupList.add(groupEntity);
        mPagerView.addEmojiIconGroup(groupEntity, true);
        mTabBar.addTab(groupEntity);
    }

    /**
     * 添加一系列表情组
     * @param groupEntitieList
     */
    public void addEmojiIconGroup(List<EmojiIconGroupEntity> groupEntitieList){
        for(int i= 0; i < groupEntitieList.size(); i++){
            EmojiIconGroupEntity groupEntity = groupEntitieList.get(i);
            mEmojiIconGroupList.add(groupEntity);
            mPagerView.addEmojiIconGroup(groupEntity, i == groupEntitieList.size() - 1 ? true : false);
            mTabBar.addTab(groupEntity);
        }

    }

    /**
     * 移除表情组
     * @param position
     */
    public void removeEmojiIconGroup(int position){
        mEmojiIconGroupList.remove(position);
        mPagerView.removeEmojiIconGroup(position);
        mTabBar.removeTab(position);
    }

    public void setTabBarVisibility(boolean isVisible){
        if(!isVisible){
            mTabBar.setVisibility(View.GONE);
        }else{
            mTabBar.setVisibility(View.VISIBLE);
        }
    }


    private class EmojiIconPagerViewListener implements EmojiIconPagerView.EmojiIconPagerViewListener {

        @Override
        public void onPagerViewInited(int groupMaxPageSize, int firstGroupPageSize) {
            mIndicatorView.init(groupMaxPageSize);
            mIndicatorView.updateIndicator(firstGroupPageSize);
            mTabBar.selectedTo(0);
        }

        @Override
        public void onGroupPositionChanged(int groupPosition, int pagerSizeOfGroup) {
            mIndicatorView.updateIndicator(pagerSizeOfGroup);
            mTabBar.selectedTo(groupPosition);
        }

        @Override
        public void onGroupInnerPagePositionChanged(int oldPosition, int newPosition) {
            mIndicatorView.selectTo(oldPosition, newPosition);
        }

        @Override
        public void onGroupPagePositionChangedTo(int position) {
            mIndicatorView.selectTo(position);
        }

        @Override
        public void onGroupMaxPageSizeChanged(int maxCount) {
            mIndicatorView.updateIndicator(maxCount);
        }

        @Override
        public void onDeleteImageClicked() {
            if(listener != null){
                listener.onDeleteImageClicked();
            }
        }

        @Override
        public void onExpressionClicked(BaseEmojiIconEntity emojicon) {
            if(listener != null){
                listener.onExpressionClicked(emojicon);
            }
        }

    }

}