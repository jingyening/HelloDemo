package com.bruce.jing.hello.demo.widget.view.animation;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.List;

/**
 * @author bruce jing
 * @date 2019/5/7
 * <p>
 * bruce todo 如果做循环动画，有必要关注view销毁或者被遮盖的情况处理
 */
public class FrameAnimationView extends View implements FrameAnimationHelper.AnimationStateListener{

    private static final String TAG = "FrameAnimationView";
    
    private FrameAnimationHelper mFrameAnimationHelper;
    private FrameAnimationHelper.AnimationStateListener mAnimateStateListener;
    private Thread mUpdateThread;

    public FrameAnimationView(Context context) {
        this(context, null);
    }

    public FrameAnimationView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FrameAnimationView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mFrameAnimationHelper = new FrameAnimationHelper(this);
        mFrameAnimationHelper.setAnimationStateListener(this);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mFrameAnimationHelper != null) {
            mFrameAnimationHelper.drawBitmap(canvas);
        }
    }

    public void startAnimation(List<String> pathList) {
        mFrameAnimationHelper.initPathList(pathList);
        mFrameAnimationHelper.start();
        mUpdateThread = new Thread() {
            @Override
            public void run() {
                super.run();
                while (true) {
                    try {
                        long now = System.currentTimeMillis();
                        FrameAnimationView.this.postInvalidate();
                        //控制两帧之间的间隔
                        long frameInterval = mFrameAnimationHelper.getFrameInterval();
                        sleep(frameInterval - (System.currentTimeMillis() - now) > 0 ? frameInterval - (System.currentTimeMillis() - now) : 0);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        };
        mUpdateThread.start();
    }

    public void startAnimation(String assetPath) {
        startAnimation(mFrameAnimationHelper.getPathList(assetPath));
    }


    public void stopAnimation() {
        if (mFrameAnimationHelper != null) {
            mFrameAnimationHelper.stop();
            mUpdateThread.interrupt();
            mUpdateThread = null;
        }
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopAnimation();
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if(visibility != VISIBLE){
            stopAnimation();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
    }


    /**
     * animation duration in millisecond
     *
     * @param duration
     * @return
     */
    public void setDuration(int duration) {
        mFrameAnimationHelper.setDuration(duration);
    }

    /**
     * set number of frames resides in memory
     *
     * @param count number of frames resides in memory.
     * @return
     */
    public void setCacheCount(@IntRange(from = 1) int count) {
        mFrameAnimationHelper.setCacheCount(count);
    }

    /**
     * set Matrix
     *
     * @param matrix matrix hold the shape
     * @return
     */
    public void setMatrix(@NonNull Matrix matrix) {
        mFrameAnimationHelper.setMatrix(matrix);
    }

    /**
     * 设置AnimationStateListener
     *
     * @param listener
     * @return
     */
    public void setAnimationListener(@NonNull FrameAnimationHelper.AnimationStateListener listener) {
        mAnimateStateListener = listener;
    }

    /**
     * 设置是否支持inBitmap，支持inBitmap会非常显著的改善内存抖动的问题
     * 因为存在bitmap复用的问题，当设置支持inBitmap时，请务必保证帧动画
     * 所有的图片分辨率和颜色位数完全一致。默认为true。
     *
     * @param support
     * @see <a href="google">https://developer.android.com/reference/android/graphics/BitmapFactory.Options.html#inBitmap</a>
     */
    public void setSupportInBitmap(boolean support) {
        mFrameAnimationHelper.setSupportInBitmap(support);
    }

    /**
     * set repeat mode
     *
     * @param mode
     * @return
     */
    public void setRepeatMode(FrameAnimationHelper.RepeatMode mode) {
        mFrameAnimationHelper.setRepeatMode(mode);
    }

    /**
     * set scale type,same as ImageView
     *
     * @param type
     * @return
     */
    public void setScaleType(@FrameAnimationHelper.ScaleType int type) {
        mFrameAnimationHelper.setScaleType(type);
    }

    @Override
    public void onFrameAnimStart() {
        if(mAnimateStateListener != null){
            mAnimateStateListener.onFrameAnimFinish();
        }
    }

    @Override
    public void onFrameAnimFinish() {
        if(mUpdateThread != null && mUpdateThread.isAlive()){
            mUpdateThread.interrupt();
            Log.d(TAG, "onFrameAnimFinish interrupt");
        }

        if(mAnimateStateListener != null){
            mAnimateStateListener.onFrameAnimFinish();
        }
    }

    @Override
    public void onFrameAnimUnexpectedStop(int position) {

    }
}
