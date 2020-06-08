package com.bruce.jing.hello.demo.widget.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Choreographer;
import android.widget.TextView;

import com.bruce.jing.hello.demo.R;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import androidx.appcompat.widget.AppCompatTextView;

/**
 *  停止/开始跑马灯的地方，makeNewLayout，focusChange，windowFocusChange和setSelected 四个方法
 */
public class MarqueeTextView extends AppCompatTextView {

    private static final String TAG = "MarqueeTextView";

    private static final int MOVE_MIN_DISTANCE = 80;

    private static final int MIN_VELOCITY = 20;

    private OnMarqueeListener mOnMarqueeListener;
    private boolean mIsReset;
    private long mPreInvalidateTime;
    private long mInvalidateInterval = -1;

    public MarqueeTextView(Context context) {
        this(context, null);
    }


    public MarqueeTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }


    public MarqueeTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MarqueeTextView);
        mInvalidateInterval = typedArray.getInteger(R.styleable.MarqueeTextView_invalidate_interval,-1);
        Log.d(TAG,"init mInvalidateInterval = "+mInvalidateInterval);
        typedArray.recycle();

    }

    @Override
    public boolean isFocused() {
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!mIsReset && mInvalidateInterval > 0) {
            resetCallback();
            mIsReset = true;
        }
    }


    public void setMarqueeListener(OnMarqueeListener onMarqueeListener) {
        Log.d(TAG, "setMarqueeListener");
        this.mOnMarqueeListener = onMarqueeListener;
    }

    private void resetCallback() {
        try {
            Field marquee = TextView.class.getDeclaredField("mMarquee");
            if (marquee != null) {
                Log.d(TAG, "marquee != null");
                marquee.setAccessible(true);
                Object obj = marquee.get(this);
                if (obj != null) {
                    mIsReset = true;
                    Log.d(TAG, "obj != null");
                    Class cls = obj.getClass();
                    Field field = cls.getDeclaredField("mTickCallback");
                    if (field != null) {
                        field.setAccessible(true);
                        field.set(obj, mTickCallback);
                    }
                }
            }
        } catch (Exception e) {
            Log.d(TAG, "exception: " + e.getLocalizedMessage());
        }
    }

    private Choreographer.FrameCallback mTickCallback = new Choreographer.FrameCallback() {
        @Override
        public void doFrame(long frameTimeNanos) {
            try {
                Field marquee = TextView.class.getDeclaredField("mMarquee");
                if (marquee != null) {
                    marquee.setAccessible(true);
                    Object obj = marquee.get(MarqueeTextView.this);
                    if (obj != null) {
                        Class cls = obj.getClass();
                        Field maxScrollFeild = cls.getDeclaredField("mMaxScroll");
                        Field scrollFeild = cls.getDeclaredField("mScroll");
                        Field repeateLimitFeild = cls.getDeclaredField("mRepeatLimit");
                        if (maxScrollFeild != null && scrollFeild != null && repeateLimitFeild != null) {
                            maxScrollFeild.setAccessible(true);
                            scrollFeild.setAccessible(true);
                            repeateLimitFeild.setAccessible(true);
                            float maxScroll = (float) maxScrollFeild.get(obj);
                            float scroll = (float) scrollFeild.get(obj);
                            int repeateLimit = (int) repeateLimitFeild.get(obj);
//                            Log.d(TAG, "max: " + maxScroll + "   scroll: " + scroll +" limit: "+ repeateLimit);
                            final float density = getContext().getResources().getDisplayMetrics().density;
                            // private static final int MARQUEE_DP_PER_SECOND = 30; TextView中定义的每秒移动的dp值
                            //deltaPx 为每10秒移动的距离，多少秒移动一次暂没有取到
                            float deltaPx = 30 * density / 100f;
                            if (repeateLimit <= 1 && scroll >= maxScroll - deltaPx) {
                                if (mOnMarqueeListener != null) {
                                    mOnMarqueeListener.onMarqueeStoped();

                                }
                            }
                            if (mInvalidateInterval > 0) {
                                tick();
                            } else {
                                Method method = cls.getDeclaredMethod("tick");
                                method.setAccessible(true);
                                method.invoke(obj);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };


    /**
     * 通过反射实现
     * @link{android.widget.TextView.Marquee#tick()}方法，
     * 同时增加刷新频率控制逻辑
     */
    private void tick(){
        try{
//            long start = SystemClock.elapsedRealtime();
            Field marqueeField = TextView.class.getDeclaredField("mMarquee");
            if (marqueeField != null) {
                marqueeField.setAccessible(true);
                Object marqueeObject = marqueeField.get(MarqueeTextView.this);
                if (marqueeObject != null) {
                    Class marqueeCls = marqueeObject.getClass();
                    Field mStatusField = marqueeCls.getDeclaredField("mStatus");
                    mStatusField.setAccessible(true);
                    byte mStatus = (byte)mStatusField.get(marqueeObject);
                    Field marquee_running_field = marqueeCls.getDeclaredField("MARQUEE_RUNNING");
                    marquee_running_field.setAccessible(true);
                    byte marquee_running = (byte)marquee_running_field.get(null);
                    if(mStatus != marquee_running){
                        return;
                    }

                    Field mChoreographerField = marqueeCls.getDeclaredField("mChoreographer");
                    mChoreographerField.setAccessible(true);
                    Choreographer choreographer = (Choreographer)mChoreographerField.get(marqueeObject);
                    choreographer.removeFrameCallback(mTickCallback);
                    Field mLastAnimationMsField = marqueeCls.getDeclaredField("mLastAnimationMs");
                    mLastAnimationMsField.setAccessible(true);
                    if(isFocused() || isSelected()){

                        Class choreographerCls = choreographer.getClass();
                        Method getFrameTimeMethod = choreographerCls.getDeclaredMethod("getFrameTime", null);
                        getFrameTimeMethod.setAccessible(true);
                        long currentMs = (long)getFrameTimeMethod.invoke(choreographer,null);
                        long mLastAnimationMs = (long)mLastAnimationMsField.get(marqueeObject);
                        long deltaMs = currentMs - mLastAnimationMs;
                        mLastAnimationMsField.set(marqueeObject, currentMs);

                        Field mPixelsPerMsField = marqueeCls.getDeclaredField("mPixelsPerMs");
                        mPixelsPerMsField.setAccessible(true);
                        float mPixelsPerMs = (float)mPixelsPerMsField.get(marqueeObject);
                        float deltaPx = deltaMs * mPixelsPerMs;

                        Field mMaxScrollField = marqueeCls.getDeclaredField("mMaxScroll");
                        Field mScrollField = marqueeCls.getDeclaredField("mScroll");
                        mMaxScrollField.setAccessible(true);
                        mScrollField.setAccessible(true);
                        float mScroll = (float) mScrollField.get(marqueeObject);
                        mScroll += deltaPx;
                        mScrollField.set(marqueeObject,mScroll);
                        float mMaxScroll = (float) mMaxScrollField.get(marqueeObject);
                        if (mScroll > mMaxScroll) {
                            mScrollField.set(marqueeObject,mMaxScroll);
                            Field mRestartCallbackField = marqueeCls.getDeclaredField("mRestartCallback");
                            mRestartCallbackField.setAccessible(true);
                            Choreographer.FrameCallback mRestartCallback = (Choreographer.FrameCallback)mRestartCallbackField.get(marqueeObject);
                            Field marquee_delay_field = marqueeCls.getDeclaredField("MARQUEE_DELAY");
                            marquee_delay_field.setAccessible(true);
                            int marquee_delay = (int)marquee_delay_field.get(marqueeObject);
                            choreographer.postFrameCallbackDelayed(mRestartCallback,marquee_delay);
                        }else{
                            choreographer.postFrameCallback(mTickCallback);
                        }
                        long currentTime = SystemClock.elapsedRealtime();
                        if(currentTime - mPreInvalidateTime >= mInvalidateInterval){
                            invalidate();
                            mPreInvalidateTime = currentTime;
                        }
                    }
                }
            }
//            Log.d(TAG,"tick waste time = "+(SystemClock.elapsedRealtime() - start));
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    public final void startMarquee() {
        try {
            Method startMarquee = TextView.class.getDeclaredMethod("startMarquee");
            if (startMarquee != null) {
                startMarquee.setAccessible(true);
                startMarquee.invoke(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //onFocusChange为false会停止跑马灯
    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        if(!focused) {
            boolean attachedToWindow = isAttachedToWindow();
            boolean isVisible = getVisibility() == VISIBLE;
            Log.d(TAG, "onFocusChanged attachedToWindow = " + attachedToWindow + "  isVisible = " + isVisible);
            if (attachedToWindow && isVisible) {
                startMarquee();
            }
        }
    }


    //    @Override
//    protected void onWindowVisibilityChanged(int visibility) {
//        super.onWindowVisibilityChanged(visibility);
//        Log.d(TAG, "onWindowVisibilityChanged  visibility = "+visibility+", this = " + hashCode());
//    }
//
//    @Override
//    public void onWindowFocusChanged(boolean hasWindowFocus) {
//        super.onWindowFocusChanged(hasWindowFocus);
//        Log.d(TAG, "onWindowFocusChanged  hasWindowFocus = "+hasWindowFocus+",  this = " + hashCode()+" lineCount = "+getLineCount()+" ,layout = "+getLayout());
//    }


    public interface OnMarqueeListener {
        void onMarqueeStoped();
    }

}