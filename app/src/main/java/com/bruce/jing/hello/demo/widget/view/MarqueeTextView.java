package com.bruce.jing.hello.demo.widget.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Choreographer;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class MarqueeTextView extends android.support.v7.widget.AppCompatTextView {

    private static final String TAG = "MarqueeTextView";

    private static final int MOVE_MIN_DISTANCE = 80;

    private static final int MIN_VELOCITY = 20;

    private OnMarqueeListener mOnMarqueeListener;
    private boolean mIsReset;

    public MarqueeTextView(Context context) {
        this(context, null);
    }


    public MarqueeTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }


    public MarqueeTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean isFocused() {
        return true;
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        Log.d(TAG,"setSelected selected = "+selected +" stack = "+Log.getStackTraceString(new Throwable()));
        if (!isSelected()) {
            setSelected(true);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mOnMarqueeListener != null && !mIsReset) {
            resetCallback();
        }
    }


    public void setMarqueeListener(OnMarqueeListener onMarqueeListener) {
        Log.d(TAG, "setMarqueeListener");
        this.mOnMarqueeListener = onMarqueeListener;
    }

    private void resetCallback() {
        if (mOnMarqueeListener == null) {
            return;
        }
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
                            Method method = cls.getDeclaredMethod("tick");
                            method.setAccessible(true);
                            method.invoke(obj);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };


    public interface OnMarqueeListener {
        void onMarqueeStoped();
    }

}