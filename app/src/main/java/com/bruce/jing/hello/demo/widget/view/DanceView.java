package com.bruce.jing.hello.demo.widget.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.bruce.jing.hello.demo.R;
import com.bruce.jing.hello.demo.util.MathUtil;

/**
 * 贝塞尔曲线
 * https://www.jianshu.com/p/c2568ca445a0
 * 三阶贝塞尔曲线（B为曲线轨迹，P0为起始点，P1为左控制点，P2为右控制点，P3为结束点。t的取值范围为0-1之间）
 * B(t) = P0*(1-t)^3+3*P1*t*(1-t)^2+3*P2*t^2*(1-t)+P3*t^3
 * <p>
 * 缓动函数速查表
 * http://www.xuanfengge.com/easeing/easeing/
 * <p>
 * easeInOutSine ==>cubic-bezier(0.445, 0.05, 0.55, 0.95)
 * easeInOutCubic
 */
public class DanceView extends View {
    public static final String TAG = "DanceView";

    private static final int DEFAULT_COLOR = 0xFF44EFCD;
    private static final float DEFAULT_ITEM_WIDTH = 3;
    private static final float DEFAULT_ITEM_SPACE = 6;

    private static final int ITEM_COUNT = 4;
    private static final int DANCE_ANIM_DURATION = 2000;
    private static final int SHOW_HIDE_ANIM_DURATION = 300;

    private Paint mPaint;

    private int mColor = 0xFF44EFCD;
//    private int mColor = Color.BLUE;
    /**
     * 每个item的宽度，单位dp
     */
    private float mItemWidth = DEFAULT_ITEM_WIDTH;
    /**
     * item的圆角半径，单位dp
     */
    private float mRadius = DEFAULT_ITEM_WIDTH / 2;
    /**
     * 每个item之间的间隔距离，单位dp
     */
    private float mItemSpace = DEFAULT_ITEM_SPACE;
    /**
     * 竖线的最大高度，单位dp
     */
    private int mItemMaxHeight = 32;
    /**
     * 动画内容的宽度
     */
    private int mContentWidth;
    private int mContentHeight;

    /**
     * 竖线高度变化规则
     * 一个数组是一条竖线的变化规则
     */
    private int[][] mHeightTransform = {{24, 13, 27, 8, 24}, {12, 28, 16, 32, 12}, {32, 16, 32, 14, 32}, {16, 30, 22, 30, 16}};
    /**
     * 用来存储当前竖线的高度
     */
    private int[] mCurrentHeights = new int[ITEM_COUNT];

    private ValueAnimator mDanceAnimator;
    private ValueAnimator mShowHideAnimator;
    private State mState = State.HIDE;


    public DanceView(Context context) {
        this(context, null);
    }

    public DanceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DanceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.DanceView);
        mColor = typedArray.getColor(R.styleable.DanceView_dancer_color, DEFAULT_COLOR);
        mItemWidth = typedArray.getDimension(R.styleable.DanceView_item_width, DEFAULT_ITEM_WIDTH);
        mItemSpace = typedArray.getDimension(R.styleable.DanceView_item_space, DEFAULT_ITEM_SPACE);
        boolean hasRoundCorner = typedArray.getBoolean(R.styleable.DanceView_has_round_corner, true);
        if (hasRoundCorner) {
            mRadius = mItemWidth / 2;
        } else {
            mRadius = 0;
        }
        typedArray.recycle();
        init();
    }


    private void init() {
        initPaint();
        initValueAnimator();
        initParams();
    }


    private void initPaint() {
        mPaint = new Paint();
        mPaint.setStyle(Style.FILL);
        mPaint.setFilterBitmap(false);
//        mPaint.setPathEffect(new DashPathEffect(new float[]{3, 2}, 0));
//        mPaint.setStrokeWidth(getDrawingWidth() / 10);
        mPaint.setAntiAlias(true);
        mPaint.setColor(mColor);
        mPaint.setStrokeCap(Cap.ROUND);

    }

    private void initValueAnimator() {
        mDanceAnimator = ValueAnimator.ofFloat(0f, 1.0f);
        mDanceAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mDanceAnimator.setDuration(DANCE_ANIM_DURATION);
        mDanceAnimator.setInterpolator(new LinearInterpolator());
        mDanceAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Log.d(TAG, "onAnimationUpdate");
                postInvalidate();
            }
        });
        mDanceAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
            }

            @Override
            public void onAnimationPause(Animator animation) {
                super.onAnimationPause(animation);
            }
        });

        mShowHideAnimator = ValueAnimator.ofFloat(0f, 1f);
        mShowHideAnimator.setDuration(SHOW_HIDE_ANIM_DURATION);
        mShowHideAnimator.setInterpolator(new LinearInterpolator());
        mShowHideAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Log.d(TAG, "onAnimationUpdate");
                postInvalidate();
            }
        });
        mShowHideAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
            }

            @Override
            public void onAnimationPause(Animator animation) {
                super.onAnimationPause(animation);
            }
        });


    }

    private void initParams() {

        mContentWidth = (int) (mItemWidth * ITEM_COUNT + mItemSpace * (ITEM_COUNT - 1));
        mContentHeight = mItemMaxHeight;

    }

    public void setPaintColor(int color) {
        this.mColor = color;
        mPaint.setColor(color);
        invalidate();
    }

    public int getColor() {
        return mColor;
    }

    public void setColor(int color) {
        this.mColor = color;
    }


    public void startDance() {
        mState = State.DANCE;
        stopAllAnimation();
        mDanceAnimator.start();
    }

    public void stopDance() {
        showDancer();
    }

    public void hideDancer() {
        fillCurrentHeights();
        stopAllAnimation();
        mState = State.HIDE;
        mShowHideAnimator.start();
    }

    public void showDancer() {
        fillCurrentHeights();
        stopAllAnimation();
        mState = State.IDLE;
        mShowHideAnimator.start();
    }

    private void fillCurrentHeights() {
        if (mState == State.DANCE) {
            for (int i = 0; i < ITEM_COUNT; i++) {
                int itemTransformHeight = getItemTransformHeight(i);
                mCurrentHeights[i] = itemTransformHeight;
            }
        } else if (mState == State.IDLE) {
            for (int i = 0; i < ITEM_COUNT; i++) {
                mCurrentHeights[i] = mHeightTransform[i][0];
            }
        } else {
            for (int i = 0; i < ITEM_COUNT; i++) {
                mCurrentHeights[i] = 0;
            }
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        long start = System.currentTimeMillis();
        int height = getDrawingHeight();
        int width = getDrawingWidth();
        float offsetX = (width - mContentWidth) / 2;
        float offsetY = (height - mContentHeight) / 2;
        canvas.save();
        canvas.translate(getPaddingLeft(), getPaddingTop());
        for (int i = 0; i < ITEM_COUNT; i++) {
            int itemTransformHeight = getItemTransformHeight(i);
            float left = offsetX;
            float right = offsetX + mItemWidth;
            float top = offsetY + (mItemMaxHeight - itemTransformHeight);
            float bottom = offsetY + mItemMaxHeight;
            RectF rect = new RectF(left, top, right, bottom);
            canvas.drawRoundRect(rect, mRadius, mRadius, mPaint);
            offsetX += mItemWidth + mItemSpace;
        }
        canvas.restore();
//        Log.d(TAG, "onDraw waste = " + (System.currentTimeMillis() - start));
    }

    /**
     * 获取每个竖线变化后的高度
     *
     * @param i
     * @return
     */
    private int getItemTransformHeight(int i) {
        if (mState == State.DANCE) {
            float animValue = (float) mDanceAnimator.getAnimatedValue();
            int[] transformArray = mHeightTransform[i];
            float keyFrameInterval = 1f / (transformArray.length - 1);
            int keyFramePosition = (int) (animValue / keyFrameInterval);
            if (keyFramePosition == transformArray.length - 1) {
                return transformArray[keyFramePosition];
            }
            int startHeight = transformArray[keyFramePosition];
            int endHeight = transformArray[keyFramePosition + 1];
            float fraction = mDanceAnimator.getAnimatedFraction() % keyFrameInterval / keyFrameInterval;
            double transformFraction = MathUtil.cubicCurves(fraction, 0, 0.05f, 0.95f, 1);
            int transformHeight = (int) (startHeight + (endHeight - startHeight) * transformFraction);

//            if (i == 3) {
//                Log.d(TAG, "getItemTransformHeight animValue = " + animValue + " , fraction = " + fraction + " , transformFraction = " + transformFraction + ", " + transformHeight);
//            }
            return transformHeight;
        } else {
            if (mState == State.IDLE) {
                int startHeight = mCurrentHeights[i];
                int endHeight = mHeightTransform[i][0];
                float fraction = mShowHideAnimator.getAnimatedFraction();
                double transformFraction = MathUtil.cubicCurves(fraction, 0, 0.05f, 0.95f, 1);
                int transformHeight = (int) (startHeight + (endHeight - startHeight) * transformFraction);
                return transformHeight;
            } else {
                int startHeight = mCurrentHeights[i];
                int endHeight = 0;
                float fraction = mShowHideAnimator.getAnimatedFraction();
                double transformFraction = MathUtil.cubicCurves(fraction, 0, 0.05f, 0.95f, 1);
                int transformHeight = (int) (startHeight + (endHeight - startHeight) * transformFraction);
                return transformHeight;
            }
        }


    }

    private int getDrawingWidth() {
        return getWidth() - getPaddingLeft() - getPaddingRight();
    }

    private int getDrawingHeight() {
        return getHeight() - getPaddingTop() - getPaddingBottom();
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopAllAnimation();
        mState = State.HIDE;
    }

    private void stopAllAnimation() {
        if (mDanceAnimator.isRunning()) {
            mDanceAnimator.cancel();
        }
        if (mShowHideAnimator.isRunning()) {
            mShowHideAnimator.cancel();
        }
    }

    public enum State {
        DANCE,
        IDLE,
        HIDE
    }
}
