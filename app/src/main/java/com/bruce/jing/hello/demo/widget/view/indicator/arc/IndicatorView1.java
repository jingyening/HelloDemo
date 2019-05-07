package com.bruce.jing.hello.demo.widget.view.indicator.arc;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * @author bruce jing
 * @date 2019/5/5
 * <p>
 * 参考资料：
 * https://blog.csdn.net/qq_34131399/article/details/79948266
 * <p>
 * https://blog.csdn.net/iteye_4389/article/details/82323701
 * <p>
 * https://blog.csdn.net/abcdef314159/article/details/52797274
 * <p>
 * https://blog.csdn.net/wang29169/article/details/84201044
 */
public class IndicatorView1 extends View {

    private static final String TAG = "IndicatorView";

    public static final int OVAL_WIDTH = 400;
    public static final int OVAL_HEIGHT = 100;
    /**
     * 原点半径
     */
    public static final int DOT_RADIUS = 2;


    /**
     * indicator点的个数
     */
    public static final int DOT_COUNTS = 24;

    public static final float DOT_X_SPACE = (float) OVAL_WIDTH / DOT_COUNTS;
    public static final int BASE_X_POSITION = -OVAL_WIDTH / 2;


    private Paint mPaint;
    private RectF mOvalRectF;

    /**
     * 初始角度
     */
    private float mFirstXPosition = -OVAL_WIDTH / 2;

    public IndicatorView1(Context context) {
        this(context, null);
    }

    public IndicatorView1(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IndicatorView1(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.RED);

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        Log.d(TAG, "onMeasure: width = " + width + " height = " + height);
        mOvalRectF = new RectF();
        mOvalRectF.left = (width - OVAL_WIDTH) / 2;
        mOvalRectF.right = (width + OVAL_WIDTH) / 2;
        mOvalRectF.top = (height - OVAL_HEIGHT) / 2;
        mOvalRectF.bottom = (height + OVAL_HEIGHT) / 2;

    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        drawWithClear(canvas);

        drawOvalDot(canvas);
        canvas.drawArc(mOvalRectF, 0F, .06F, true, mPaint);

    }

    private void drawOvalDot(Canvas canvas) {
        canvas.translate(mOvalRectF.left + OVAL_WIDTH / 2, mOvalRectF.top + OVAL_HEIGHT / 2);
        for (int i = 0; i < DOT_COUNTS + 1; i++) {
            float x = mFirstXPosition + DOT_X_SPACE * i;
            if( x > OVAL_WIDTH / 2){
                continue;
            }
            float y = getPosYPosition(x);
            Log.d(TAG, "drawOvalDot: dotPosition x= " + x + " ;y = " + y);
            canvas.drawCircle(x, y, DOT_RADIUS, mPaint);
        }
    }


    /**
     * x^2/a^2 + y^2/b^2 = 1
     *
     * @param x
     * @return
     */
    private float getPosYPosition(float x) {
        int a = OVAL_WIDTH / 2;
        int b = OVAL_HEIGHT / 2;
        double y2 = Math.pow(b, 2) - Math.pow(b, 2) * Math.pow(x, 2) / Math.pow(a, 2);
        float y = (float) Math.sqrt(y2);
        return y;
    }

    private float getNagYPosition(float x) {
        int a = OVAL_WIDTH / 2;
        int b = OVAL_HEIGHT / 2;
        double y2 = Math.pow(b, 2) - Math.pow(b, 2) * Math.pow(x, 2) / Math.pow(a, 2);
        float y = (float) -Math.sqrt(y2);
        return y;
    }
}
