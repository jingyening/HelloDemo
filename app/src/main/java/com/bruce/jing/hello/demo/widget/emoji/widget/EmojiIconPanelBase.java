package com.bruce.jing.hello.demo.widget.emoji.widget;


import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.bruce.jing.hello.demo.widget.emoji.entity.BaseEmojiIconEntity;

public class EmojiIconPanelBase extends LinearLayout {
    protected EmojiIconMenuListener listener;

    public EmojiIconPanelBase(Context context) {
        super(context);
    }

    @SuppressLint("NewApi")
    public EmojiIconPanelBase(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    public EmojiIconPanelBase(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    /**
     * 设置回调监听
     * @param listener
     */
    public void setEmojiIconMenuListener(EmojiIconMenuListener listener){
        this.listener = listener;
    }

    public interface EmojiIconMenuListener {
        /**
         * 表情被点击
         * @param emojiIconEntity
         */
        void onExpressionClicked(BaseEmojiIconEntity emojiIconEntity);
        /**
         * 删除按钮被点击
         */
        void onDeleteImageClicked();
    }
}