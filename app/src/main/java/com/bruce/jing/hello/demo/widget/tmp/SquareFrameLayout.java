package com.bruce.jing.hello.demo.widget.tmp;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.bruce.jing.hello.demo.R;


/**
 * @author bruce jing
 * @date 2019/12/9
 **/
public class SquareFrameLayout extends FrameLayout {

    private static final String TAG = "SquareFrameLayout";

    private static final int DECIDE_BY_WIDTH = 0;
    private static final int DECIDE_BY_HEIGHT = 1;

    private int mDecideSide = DECIDE_BY_WIDTH;

    public SquareFrameLayout(Context context) {
        this(context, null);
    }

    public SquareFrameLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SquareFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SquareFrameLayout);
        mDecideSide = typedArray.getInt(R.styleable.SquareFrameLayout_decide_side, DECIDE_BY_WIDTH);
        typedArray.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.d(TAG, "onMeasure measuredHeight1 = " + getMeasuredHeight()+" ,width = "+getMeasuredWidth());
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mDecideSide == DECIDE_BY_WIDTH) {
            int measuredWidth = getMeasuredWidth();
            Log.d(TAG, "onMeasure measuredWidth = " + measuredWidth);
            setMeasuredDimension(measuredWidth, measuredWidth);
        } else {
            int measuredHeight = getMeasuredHeight();
            Log.d(TAG, "onMeasure measuredHeight2 = " + measuredHeight+" ,width = "+getMeasuredWidth());
            setMeasuredDimension(measuredHeight, measuredHeight);
        }
    }

    @Override
    protected void measureChildWithMargins(View child, int parentWidthMeasureSpec, int widthUsed, int parentHeightMeasureSpec, int heightUsed) {

        super.measureChildWithMargins(child, parentWidthMeasureSpec, widthUsed, parentHeightMeasureSpec, heightUsed);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        //如果父view layout子view宽高不符合，重新设置宽高
        if (right - left > getMeasuredWidth()) {
            setRight(left + getMeasuredWidth());
        }
        if (bottom - top > getMeasuredHeight()) {
            bottom = top + getMeasuredHeight();
            setBottom(bottom);
        }
        super.onLayout(changed, left, top, right, bottom);
    }
}
