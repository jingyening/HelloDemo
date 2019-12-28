package com.bruce.jing.hello.demo.widget.tmp.recycler;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;

/**
 * @author bruce jing
 * @date 2019/12/9
 */
public class MyLinearLayoutManager extends LinearLayoutManager {

    private static final String TAG = "MyLinearLayoutManager";

    public MyLinearLayoutManager(Context context) {
        super(context);
    }

    public MyLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public MyLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    @Override
    public void measureChildWithMargins(View child, int widthUsed, int heightUsed) {
        Log.d(TAG,"measureChildWithMargins child width1 = "+child.getWidth()+" , measureWidth = "+child.getMeasuredWidth());
        super.measureChildWithMargins(child, widthUsed, heightUsed);
        Log.d(TAG,"measureChildWithMargins child width2 = "+child.getWidth()+" , measureWidth = "+child.getMeasuredWidth());
    }

    @Override
    public void layoutDecoratedWithMargins(View child, int left, int top, int right, int bottom) {
        Log.d(TAG,"layoutDecoratedWithMargins left = "+left +" , right = "+right);
        Log.d(TAG,"layoutDecoratedWithMargins child left1 = "+child.getLeft() +" , right = "+child.getRight());
        super.layoutDecoratedWithMargins(child, left, top, right, bottom);
        Log.d(TAG,"layoutDecoratedWithMargins child left2 = "+child.getLeft()+" , right = "+child.getRight());
    }


}
