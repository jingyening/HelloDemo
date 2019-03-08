package com.bruce.jing.hello.demo.widget.view;

import com.bruce.jing.hello.demo.util.log.JLogUtil;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Region;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.view.View;

public class FullscreenGuideView extends View{

    private static final long ANIMATOR_DURATION = 1200L;
    
    /**
     * 内圆呼吸最大半径 / 外圆最大半径 = RADIUS_PERCENT；
     */
    private static final float MAX_RADIUS_PERCENT = 0.72f;
    
    /**
     * 内圆呼吸最小半径 / 外圆最大半径 = MIN_RADIUS_PERCENT；
     */
    private static final float MIN_RADIUS_PERCENT = 0.66f;
    
    /**
     * animator start delay
     */
    private static final long CENTERCIRCLE_ANIMATOR_STARTDELAY = 276L;
    
    private static final float OUTCIRCLE_VISIBLE_FRACTION = 0.73f;
    
    private static final int CENTER_CIRCLE_COLOR = 0xB2ffffff;
    
    private static final int OUT_CIRCLE_COLOR = 0x7Fffffff;
    
    private float mOutCircleRadius;
    
    /**
     * 外圆呼吸的最大半径
     */
    private float mOutCircleMaxRadius;
 
    private float mCenterCircleMaxRadius;
     
    private float mCenterCircleMinRadius;

    private float maxStrokeWidth;
    
    private float mCenterCircleRadius;

    private Path mPath = new Path();


    private Paint mCenterCirclePaint = new Paint();
    private Paint mOutCirclePaint = new Paint();
    private float mCircleX, mCircleY;
    
    private ValueAnimator mOutCircleAnimator, mCenterCircleAnimator;
    
    public FullscreenGuideView(Context context) {
        super(context);
        initPaint();
    }
    
    
    private void initPaint() {

        mCenterCirclePaint.setColor(CENTER_CIRCLE_COLOR);
        mCenterCirclePaint.setStyle(Style.STROKE);
        mCenterCirclePaint.setAntiAlias(true);
        mCenterCirclePaint.setStyle(Style.FILL_AND_STROKE);

        mOutCirclePaint.setColor(OUT_CIRCLE_COLOR);
        mOutCirclePaint.setStyle(Style.STROKE);
        mOutCirclePaint.setStrokeWidth(maxStrokeWidth);
        mOutCirclePaint.setAntiAlias(true);
        mOutCirclePaint.setStyle(Style.STROKE);

    }

    
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mOutCircleMaxRadius = Math.min(getWidth() * 0.5f, getHeight() * 0.5f);

        mCenterCircleMaxRadius = mOutCircleMaxRadius * MAX_RADIUS_PERCENT;

        maxStrokeWidth = (mOutCircleMaxRadius - mCenterCircleMaxRadius);

        mCenterCircleMinRadius = mOutCircleMaxRadius * MIN_RADIUS_PERCENT;

        mCircleX = getWidth() * 0.5f;
        mCircleY = getHeight() * 0.5f;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {

        JLogUtil.e(FullscreenGuideView.class, "FullscreenGuideView dispatchDraw");

        canvas.drawCircle(mCircleX, mCircleY, mCenterCircleRadius, mCenterCirclePaint);
        canvas.save();
        mPath.reset();
        mPath.addCircle(mCircleX, mCircleY, mCenterCircleRadius, Path.Direction.CCW);
        canvas.clipRect(0, 0, getWidth(), getHeight());
        canvas.clipPath(mPath, Region.Op.DIFFERENCE);
        canvas.drawCircle(mCircleX, mCircleY, mOutCircleRadius, mOutCirclePaint);
        canvas.restore();

        super.dispatchDraw(canvas);
    }

    public void cancelAnimator() {

        if (mOutCircleAnimator != null && mOutCircleAnimator.isRunning()) {
            mOutCircleAnimator.cancel();
            mOutCircleAnimator = null;
        }
        
        if (mCenterCircleAnimator != null && mCenterCircleAnimator.isRunning()) {
            mCenterCircleAnimator.cancel();
            mCenterCircleAnimator = null;
        }
    }

    
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        startAnimator();
    }
    
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        cancelAnimator();
    }
    
    private void startAnimator() {

        mOutCircleAnimator = ValueAnimator.ofFloat(0f, 1f)
                .setDuration(ANIMATOR_DURATION);

        mOutCircleAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mOutCircleAnimator.setRepeatMode(ValueAnimator.RESTART);
        mOutCircleAnimator.addUpdateListener(new AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator arg0) {
                float fraction = arg0.getAnimatedFraction();
                float startR = mCenterCircleMinRadius - maxStrokeWidth * 0.5f;
                if (fraction <= OUTCIRCLE_VISIBLE_FRACTION) {
                    float f = fraction / OUTCIRCLE_VISIBLE_FRACTION;
                    mOutCircleRadius = (mOutCircleMaxRadius - startR)
                            * f + startR;
                    mOutCirclePaint.setStrokeWidth(maxStrokeWidth
                            - maxStrokeWidth * f);
                } else {
                    mOutCircleRadius = startR;
                }

                invalidate();
            }
        });
        
        mOutCircleAnimator.start();

        mCenterCircleAnimator = ValueAnimator.ofFloat(0f, 1f).setDuration(
                ANIMATOR_DURATION);

        mCenterCircleAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mCenterCircleAnimator.setRepeatMode(ValueAnimator.RESTART);
        mCenterCircleAnimator.addUpdateListener(new AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator arg0) {
                float fraction = arg0.getAnimatedFraction();
                if (fraction <= 0.5f) {
                    float f = fraction / 0.5f;

                    mCenterCircleRadius = (mCenterCircleMaxRadius - mCenterCircleMinRadius)
                            * f + mCenterCircleMinRadius;

                } else {
                    float f = (fraction - 0.5f) / 0.5f;
                    mCenterCircleRadius = (mCenterCircleMaxRadius - mCenterCircleMinRadius)
                            * (1.0f - f) + mCenterCircleMinRadius;

                }

                invalidate();
            }
        });
        mCenterCircleAnimator.setStartDelay(CENTERCIRCLE_ANIMATOR_STARTDELAY);
        mCenterCircleAnimator.start();
        
    }
    
    public void pauseAnimator() {
        if (mOutCircleAnimator != null) {
            mOutCircleAnimator.cancel();
        }

        if (mCenterCircleAnimator != null) {
            mCenterCircleAnimator.cancel();
        }
    }

    public void resumeAnimator() {
        if (mOutCircleAnimator != null) {
            mOutCircleAnimator.start();
        }

        if (mCenterCircleAnimator != null) {
            mCenterCircleAnimator.start();
        }

    }

}
