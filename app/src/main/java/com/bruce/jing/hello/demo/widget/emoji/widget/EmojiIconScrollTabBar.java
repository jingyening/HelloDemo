package com.bruce.jing.hello.demo.widget.emoji.widget;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bruce.jing.hello.demo.R;
import com.bruce.jing.hello.demo.util.CommonUtil;
import com.bruce.jing.hello.demo.util.log.JLogUtil;
import com.bruce.jing.hello.demo.widget.emoji.entity.EmojiIconGroupEntity;

import java.util.ArrayList;
import java.util.List;

import androidx.core.view.ViewCompat;

public class EmojiIconScrollTabBar extends RelativeLayout {

    private static final String TAG = "EmojiIconScrollTabBar";

    private Context context;
    private HorizontalScrollView scrollView;
    private LinearLayout tabContainer;

    private List<View> tabList = new ArrayList<View>();
    private EaseScrollTabBarItemClickListener itemClickListener;

    private static final int TAB_WIDTH = 56;

    public EmojiIconScrollTabBar(Context context) {
        this(context, null);
    }

    public EmojiIconScrollTabBar(Context context, AttributeSet attrs, int defStyle) {
        this(context, attrs);
    }

    public EmojiIconScrollTabBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs){
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.layout_emoji_tab_bar, this);

        scrollView = (HorizontalScrollView) findViewById(R.id.scroll_view);
        tabContainer = (LinearLayout) findViewById(R.id.tab_container);
    }

    /**
     * 添加tab
     * @param groupEntity
     */
    public void addTab(EmojiIconGroupEntity groupEntity){
        if(groupEntity == null){
            JLogUtil.d(TAG, "EmojiIconScrollTabBar addTab");
            return;
        }

        View tabView = View.inflate(context, R.layout.emoji_panel_scroll_tab_item, null);
        View labelContainer = tabView.findViewById(R.id.fl_tab_container);
        if (groupEntity.getIcon() > 0) {
            ImageView imageView = (ImageView) tabView.findViewById(R.id.iv_icon);
            imageView.setImageResource(groupEntity.getIcon());

        } else if (!TextUtils.isEmpty(groupEntity.getName())) {
            TextView textView = (TextView) tabView.findViewById(R.id.tv_name);
            textView.setText(groupEntity.getName());
        }

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(CommonUtil.dip2px(context, TAB_WIDTH), LayoutParams.MATCH_PARENT);
        tabView.setLayoutParams(params);

        tabContainer.addView(tabView);
        tabList.add(labelContainer);
        final int position = tabList.size() -1;
        labelContainer.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if(itemClickListener != null){
                    itemClickListener.onItemClick(position);
                }
            }
        });
    }

    /**
     * 移除tab
     * @param position
     */
    public void removeTab(int position){
        tabContainer.removeViewAt(position);
        tabList.remove(position);
    }

    public void selectedTo(int position){
        scrollTo(position);
        for (int i = 0; i < tabList.size(); i++) {
            if (position == i) {
                tabList.get(i).setBackgroundColor(Color.parseColor("#F2F2F3"));
            } else {
                tabList.get(i).setBackgroundColor(Color.parseColor("#FFFFFF"));
            }
        }
    }

    private void scrollTo(final int position){
        int childCount = tabContainer.getChildCount();
        if(position < childCount){
            scrollView.post(new Runnable() {
                @Override
                public void run() {
                    int mScrollX = tabContainer.getScrollX();
                    int childX = (int) ViewCompat.getX(tabContainer.getChildAt(position));

                    if(childX < mScrollX){
                        scrollView.scrollTo(childX,0);
                        return;
                    }

                    int childWidth = tabContainer.getChildAt(position).getWidth();
                    int hsvWidth = scrollView.getWidth();
                    int childRight = childX + childWidth;
                    int scrollRight = mScrollX + hsvWidth;

                    if(childRight > scrollRight){
                        scrollView.scrollTo(childRight - scrollRight,0);
                        return;
                    }
                }
            });
        }
    }


    public void setTabBarItemClickListener(EaseScrollTabBarItemClickListener itemClickListener){
        this.itemClickListener = itemClickListener;
    }


    public interface EaseScrollTabBarItemClickListener{
        void onItemClick(int position);
    }


}
