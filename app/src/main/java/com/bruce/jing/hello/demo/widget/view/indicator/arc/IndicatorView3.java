package com.bruce.jing.hello.demo.widget.view.indicator.arc;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * @author bruce jing
 * @date 2019/5/5
 * <p>
 * 参考资料：
 * https://www.cnblogs.com/zhoug2020/p/7842808.html
 */
public class IndicatorView3 extends View {

    private static final String TAG = "IndicatorView";

    public static final int OVAL_WIDTH = 400;

    public static final float VIEW_DEGREE = 80;

    /**
     * 原点半径
     */
    public static final int DOT_RADIUS = 1;

    private static final float BASE_DEGREE = 180f;

    /**
     * indicator点的个数
     */
    public static final int DOT_COUNTS = 34;

    public static final float DEGREE_INCREASE = 180f / DOT_COUNTS;


    private Paint mPaint;

    /**
     * 初始角度
     */
    private float mFirstDegree = DEGREE_INCREASE / 2;



    public IndicatorView3(Context context) {
        this(context, null);
    }

    public IndicatorView3(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IndicatorView3(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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
//        Log.d(TAG, "onMeasure: width = " + width + " height = " + height);

    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawOvalDot(canvas);
    }

    private void drawOvalDot(Canvas canvas) {
        canvas.translate(getMeasuredWidth() / 2, /*mOvalRectF.top + OVAL_WIDTH / 2*/0 );
//        canvas.concat(mMatrix);
        float dotDegree = BASE_DEGREE - mFirstDegree;

        for (int i = 0; i < DOT_COUNTS; i++) {
            float[] dotPoint;
            if (dotDegree > 90 && dotDegree < 270) {
                dotPoint = getNagDotPointCoordinateByDegree(dotDegree);
            } else {
                dotPoint = getPosDotPointCoordinateByDegree(dotDegree);
            }
            Log.d(TAG, "drawOvalDot: dotPoint x = " + dotPoint[0] + " y = " + dotPoint[1]);
            float[] matrixPoint = matrixPositionByXRotate(dotPoint[0], dotPoint[1], VIEW_DEGREE);
            Log.d(TAG, "drawOvalDot: matrixPoint x = " + matrixPoint[0] + " y = " + matrixPoint[1]);
            canvas.drawCircle(matrixPoint[0], matrixPoint[1], DOT_RADIUS, mPaint);


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
        double radian = Math.PI * degree / 180;
        double tan = Math.tan(radian);
        double numerator = Math.pow(r, 2);
        double denominator = 1 + Math.pow(tan, 2);
//        Log.d(TAG, "getDotPointCoordinateByDegree: numerator = " + numerator + "  ;denominator = " + denominator);
        double x = Math.sqrt(numerator / denominator);
        double y = x * tan;
        return new float[]{(float) x, (float) y};
    }

    private float[] getNagDotPointCoordinateByDegree(float degree) {
        int r = OVAL_WIDTH / 2;
        double radian = Math.PI * degree / 180;
        double tan = Math.tan(radian);
        double numerator = Math.pow(r, 2);
        double denominator = 1 + Math.pow(tan, 2);
//        Log.d(TAG, "getDotPointCoordinateByDegree: numerator = " + numerator + "  ;denominator = " + denominator);
        double x = -Math.sqrt(numerator / denominator);
        double y = x * tan;
        return new float[]{(float) x, (float) y};
    }

    /**
     * 把坐标点围绕x轴进行旋转，求出旋转后的坐标
     *
     * @param degree
     * @return
     */
    private float[] matrixPositionByXRotate(double x, double y, float degree) {
        double radian = Math.PI * degree / 180;
        double matrixX = x;
        double matrixY = y * Math.cos(radian);
        return new float[]{(float) matrixX, (float) matrixY};
    }


    public void onHostScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
        onHostScrollChange(v, scrollX - oldScrollX, scrollY - oldScrollY);
    }

    public void onHostScrollChange(View v, float dx, float dy) {
        float degree = dx;
        mFirstDegree = (mFirstDegree + dx) % DEGREE_INCREASE;
        invalidate();
    }


    private float mDownX;
    private float mPreX;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        float dx = 0;
        switch (action){
            case MotionEvent.ACTION_DOWN:
                mDownX = event.getX();
                mPreX = mDownX;
                break;
            case MotionEvent.ACTION_MOVE:
                dx = event.getX() - mPreX;
                mPreX = event.getX();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                dx = event.getX() - mPreX;
                mDownX = 0;
                mPreX = 0;
                break;
        }
        onHostScrollChange(this, dx,0);
        return true;
    }
}
