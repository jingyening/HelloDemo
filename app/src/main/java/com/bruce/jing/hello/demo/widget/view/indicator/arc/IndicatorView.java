package com.bruce.jing.hello.demo.widget.view.indicator.arc;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import androidx.annotation.NonNull;import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

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
public class IndicatorView extends View {

    private static final String TAG = "IndicatorView";

    public static final int OVAL_WIDTH = 400;
    public static final int OVAL_HEIGHT = 100;
    /**
     * 原点半径
     */
    public static final int DOT_RADIUS = 2;

    private static final float BASE_DEGREE = 180f;

    /**
     * indicator点的个数
     */
    public static final int DOT_COUNTS = 24;

    public static final float DEGREE_INCREASE = 180f / DOT_COUNTS;


    private Paint mPaint;
    private RectF mOvalRectF;

    /**
     * 初始角度
     */
    private float mFirstDegree = DEGREE_INCREASE / 2;

    public IndicatorView(Context context) {
        this(context, null);
    }

    public IndicatorView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IndicatorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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
        mPaint.setColor(Color.BLACK);
        canvas.drawRect(0, 0, 20, 20, mPaint);
        mPaint.setColor(Color.RED);
        float dotDegree = BASE_DEGREE - mFirstDegree;
        for (int i = 0; i < DOT_COUNTS; i++) {
            Log.d(TAG, "drawOvalDot: dotDegree = " + dotDegree);
            float[] dotPoint;
            if (dotDegree > 90 && dotDegree < 270) {
                dotPoint = getNagDotPointCoordinateByDegree(dotDegree);
            } else {
                dotPoint = getPosDotPointCoordinateByDegree(dotDegree);
            }
            Log.d(TAG, "drawOvalDot: dotPoint x = " + dotPoint[0] + " y = " + dotPoint[1]);
            canvas.drawCircle(dotPoint[0], dotPoint[1], DOT_RADIUS, mPaint);
            dotDegree -= DEGREE_INCREASE;
        }
    }

    /**
     * x^2/a^2 + y^2/b^2 = 1
     * y/x = tanθ
     *
     * @param degree 角度
     */
    private float[] getPosDotPointCoordinateByDegree(float degree) {
        int a = OVAL_WIDTH / 2;
        int b = OVAL_HEIGHT / 2;

        double tan = Math.tan(Math.PI * degree / 180);
        double numerator = Math.pow(a, 2) * Math.pow(b, 2);
        double denominator = Math.pow(a, 2) * Math.pow(tan, 2) + Math.pow(b, 2);
        Log.d(TAG, "getDotPointCoordinateByDegree: numerator = " + numerator + "  ;denominator = " + denominator);
        double x = Math.sqrt(numerator / denominator);
        double y = x * tan;
        return new float[]{(float) x, (float) y};
    }

    private float[] getNagDotPointCoordinateByDegree(float degree) {
        int a = OVAL_WIDTH / 2;
        int b = OVAL_HEIGHT / 2;

        double tan = Math.tan(Math.PI * degree / 180);
        double numerator = Math.pow(a, 2) * Math.pow(b, 2);
        double denominator = Math.pow(a, 2) * Math.pow(tan, 2) + Math.pow(b, 2);
        Log.d(TAG, "getDotPointCoordinateByDegree: numerator = " + numerator + "  ;denominator = " + denominator);
        double x = -Math.sqrt(numerator / denominator);
        double y = x * tan;
        return new float[]{(float) x, (float) y};
    }

    /**
     * x^2/a^2 + y^2/b^2 = 1
     * y/x = tanθ
     *
     * @param degree 角度
     */
//    private float[] getDotPointCoordinateByDegree(float degree) {
//        int a = OVAL_WIDTH / 2;
//        int b = OVAL_HEIGHT / 2;
//
//        double tan = Math.tan(Math.PI * degree / 180);
//        double numerator = Math.pow(a, 2) * Math.pow(b, 2) * Math.pow(tan, 2);
//        double denominator = Math.pow(a, 2) * Math.pow(tan, 2) - Math.pow(b, 2);
//        Log.d(TAG, "getDotPointCoordinateByDegree: numerator = " + numerator + "  ;denominator = " + denominator);
//        double y = -Math.sqrt(numerator / denominator);
//        double x = y / tan;
//        return new float[]{(float) x, (float) y};
//    }

}
