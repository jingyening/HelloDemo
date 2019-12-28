package com.bruce.jing.hello.demo.widget.view.indicator.arc;

import android.content.Context;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import androidx.annotation.NonNull;import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
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
 * https://www.jianshu.com/p/34e0fe5f9e31
 */
public class IndicatorView2 extends View {

    private static final String TAG = "IndicatorView";

    public static final int OVAL_WIDTH = 400;
    public static final int OVAL_HEIGHT = 100;
    /**
     * 原点半径
     */
    public static final int DOT_RADIUS = 3;

    private static final float BASE_DEGREE = 180f;

    /**
     * indicator点的个数
     */
    public static final int DOT_COUNTS = 34;

    public static final float DEGREE_INCREASE = 180f / DOT_COUNTS;


    private Paint mPaint;
    private RectF mOvalRectF;

    /**
     * 初始角度
     */
    private float mFirstDegree = DEGREE_INCREASE / 2;


    private Matrix mMatrix;
    private Camera mCamera;

    public IndicatorView2(Context context) {
        this(context, null);
    }

    public IndicatorView2(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IndicatorView2(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mCamera = new Camera();
        mMatrix = new Matrix();

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
//        Log.d(TAG, "onMeasure: width = " + width + " height = " + height);
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
    }

    private void drawOvalDot(Canvas canvas) {

        mMatrix.reset();
        mCamera.save();
        mCamera.rotateX(80);
        mCamera.getMatrix(mMatrix);
        mCamera.restore();

        canvas.translate(mOvalRectF.left + OVAL_WIDTH / 2, /*mOvalRectF.top + OVAL_WIDTH / 2*/ 0);
        canvas.concat(mMatrix);
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
        int r = OVAL_WIDTH / 2;
        double tan = Math.tan(Math.PI * degree / 180);
        double numerator = Math.pow(r, 2);
        double denominator = 1 + Math.pow(tan, 2);
//        Log.d(TAG, "getDotPointCoordinateByDegree: numerator = " + numerator + "  ;denominator = " + denominator);
        double x = Math.sqrt(numerator / denominator);
        double y = x * tan;
        return new float[]{(float) x, (float) y};
    }

    private float[] getNagDotPointCoordinateByDegree(float degree) {
        int r = OVAL_WIDTH / 2;
        double tan = Math.tan(Math.PI * degree / 180);
        double numerator = Math.pow(r, 2);
        double denominator = 1 + Math.pow(tan, 2);
//        Log.d(TAG, "getDotPointCoordinateByDegree: numerator = " + numerator + "  ;denominator = " + denominator);
        double x = -Math.sqrt(numerator / denominator);
        double y = x * tan;
        return new float[]{(float) x, (float) y};
    }

    public void onHostScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
        onHostScrollChange(v, scrollX - oldScrollX, scrollY - oldScrollY);
    }

    public void onHostScrollChange(View v, int dX, int dY) {

    }


    private float mDownX;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            mDownX = event.getX();
        }

        return super.onTouchEvent(event);
    }
}
