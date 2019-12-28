package com.bruce.jing.hello.demo.widget.view.indicator.arc;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import androidx.annotation.NonNull;import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.bruce.jing.hello.demo.R;


/**
 * @author bruce jing
 * @date 2019/5/5
 * <p>
 * 参考资料：
 * https://www.cnblogs.com/zhoug2020/p/7842808.html
 */
public class IndicatorView3 extends View {

    private static final String TAG = "IndicatorView";

    public static final int DEFAULT_OVAL_WIDTH = 400;

    /**
     * 观察圆的视角，决定了椭圆的长轴和短轴
     */
    public static final float VIEW_DEGREE = 80;

    /**
     * 原点半径
     */
    public static final float DOT_RADIUS = 2;

    private static final float BASE_DEGREE = 0f;

    /**
     * 半椭圆类型下indicator点的个数
     */
    public static final int HALF_OVAL_DOT_COUNTS = 34;
    public static final float HALF_OVAL_DEGREE_INCREASE = 180f / HALF_OVAL_DOT_COUNTS;

    /**
     * 椭圆类型下indicator点的个数
     */
    public static final int OVAL_DOT_COUNTS = 74;
    public static final float OVAL_DEGREE_INCREASE = 360f / OVAL_DOT_COUNTS;

    private Paint mPaint;
    private RectF mOvalRectF;


    public static final int SHAPE_HALF_OVAL = 1;
    public static final int SHAPE_OVAL = 2;

    /**
     * 初始角度
     */
    private float mFirstDegree;
    private int mShapeType = SHAPE_HALF_OVAL;

    public IndicatorView3(Context context) {
        this(context, null);
    }

    public IndicatorView3(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IndicatorView3(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.IndicatorDotView);
        mShapeType = typedArray.getInt(R.styleable.IndicatorDotView_shape_type, SHAPE_HALF_OVAL);
        typedArray.recycle();
        init();
    }

    private void init() {

        mPaint = new Paint();
        mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.WHITE);

        if(mShapeType == SHAPE_HALF_OVAL){
            mFirstDegree = HALF_OVAL_DEGREE_INCREASE / 2;
        }else{
            mFirstDegree = OVAL_DEGREE_INCREASE / 2;
        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();

        //可以先不考虑宽高的逻辑，直接在xml中写死
        Log.d(TAG, "onMeasure: width = " + width + " height = " + height);

    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(mShapeType == SHAPE_HALF_OVAL) {
            drawHalfOvalDot(canvas);
        }else{
            drawOvalDot(canvas);
        }
    }

    private void drawHalfOvalDot(Canvas canvas) {
        canvas.translate(getMeasuredWidth() / 2, 0);
        float dotDegree = BASE_DEGREE + mFirstDegree;

        for (int i = 0; i < HALF_OVAL_DOT_COUNTS; i++) {
            if(dotDegree < 0 || dotDegree > 180){
                dotDegree += HALF_OVAL_DEGREE_INCREASE;
                continue;
            }
            float[] dotPoint = getDotPointCoordinateByDegree(dotDegree);
//            Log.d(TAG, "drawOvalDot: dotPoint x = " + dotPoint[0] + " y = " + dotPoint[1]);
            float[] matrixPoint = matrixPositionByXRotate(dotPoint[0], dotPoint[1], VIEW_DEGREE);
            double radian = Math.PI * dotDegree / 180;
            float dotRadius = (float) (DOT_RADIUS - (DOT_RADIUS / 2  * Math.abs(Math.cos(radian))));
//            Log.d(TAG, "drawOvalDot: matrixPoint x = " + matrixPoint[0] + " y = " + matrixPoint[1]);
            //根据ui要求做透明度渐变
            int alpha = 255;
            if((dotDegree >145 && dotDegree < 180)){
                float d = dotDegree % 45;
                alpha = (int)(255 *(1- d / 45 * 0.98f));
            }else if((dotDegree > 0 && dotDegree <45)){
                float d = dotDegree % 45;
                alpha = (int)(255 *(d / 45 * 0.98f));
            }
            mPaint.setAlpha(alpha);
            canvas.drawCircle(matrixPoint[0], matrixPoint[1], dotRadius, mPaint);
            dotDegree += HALF_OVAL_DEGREE_INCREASE;
        }
    }

    private void drawOvalDot(Canvas canvas) {
        //画布移动到的位置是坐标系的中心点
        canvas.translate(getMeasuredWidth() / 2, getMeasuredHeight()/2);
        float dotDegree = BASE_DEGREE + mFirstDegree;

        for (int i = 0; i < OVAL_DOT_COUNTS; i++) {
            float[] dotPoint = getDotPointCoordinateByDegree(dotDegree);
//            Log.d(TAG, "drawOvalDot: dotPoint x = " + dotPoint[0] + " y = " + dotPoint[1]);
            float[] matrixPoint = matrixPositionByXRotate(dotPoint[0], dotPoint[1], VIEW_DEGREE);
//            Log.d(TAG, "drawOvalDot: matrixPoint i = "+i+"  ,x = " + matrixPoint[0] + " y = " + matrixPoint[1]);
            //根据ui要求做半径渐变
            double radian = Math.PI * dotDegree / 180;
            float dotRadius = (float) (DOT_RADIUS / 4 * 3 + (DOT_RADIUS / 4  * Math.sin(radian)));
            Log.d(TAG, "drawOvalDot: dotRadius = "+dotRadius+" ,dotDegree = "+dotDegree);

            //根据ui要求做透明度渐变
            int alpha = 255;
            if (dotDegree >= 0 && dotDegree < 15) {
                alpha = (int) (255 * (0.4 * (dotDegree % 15 / 15) + 0.6));
            } else if (dotDegree > 345 && dotDegree < 360) {
                alpha = (int) (255 * (0.4 * (dotDegree % 15 / 15) + 0.2));
            } else if (dotDegree > 165 && dotDegree <= 180) {
                alpha = (int) (255 * (1 - 0.4 * (dotDegree % 15 / 15)));
            } else if (dotDegree > 180 && dotDegree < 195) {
                alpha = (int) (255 * (0.6 - 0.4 * (dotDegree % 15 / 15)));
            } else if (dotDegree >= 15 && dotDegree <= 165) {
                alpha = 255;
            } else {
                alpha = 51;
            }
            mPaint.setAlpha(alpha);
            canvas.drawCircle(matrixPoint[0], matrixPoint[1], dotRadius, mPaint);
            dotDegree += OVAL_DEGREE_INCREASE;
        }
    }

    /**
     * 获取坐标点
     * x^2/a^2 + y^2/b^2 = 1
     * y/x = tanθ
     * @param degree
     * @return
     */
    private float[] getDotPointCoordinateByDegree(float degree) {
        degree = degree % 360;
        int r = (int)(getMeasuredWidth() / 2 - DOT_RADIUS * 2);
        double radian = Math.PI * degree / 180;
        double tan = Math.tan(radian);
        double numerator = Math.pow(r, 2);
        double denominator = 1 + Math.pow(tan, 2);
//        Log.d(TAG, "getDotPointCoordinateByDegree: numerator = " + numerator + "  ;denominator = " + denominator);\
        double x;

        if(degree >= 90 && degree < 270){
            x = -Math.sqrt(numerator / denominator);
        }else{
            x = Math.sqrt(numerator / denominator);
        }
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
        float degree = dx / 20;
        if (mShapeType == SHAPE_HALF_OVAL) {
            mFirstDegree = (mFirstDegree + degree) % HALF_OVAL_DEGREE_INCREASE;

        } else {
            mFirstDegree = (mFirstDegree + degree) % OVAL_DEGREE_INCREASE;
        }
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
