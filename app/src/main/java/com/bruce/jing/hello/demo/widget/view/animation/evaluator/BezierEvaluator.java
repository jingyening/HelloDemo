package com.bruce.jing.hello.demo.widget.view.animation.evaluator;

import android.animation.TypeEvaluator;
import android.graphics.PointF;
import android.util.Log;

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
public class BezierEvaluator implements TypeEvaluator<Integer> {

    private static final String TAG = "BezierEvaluator";

    private final PointF mControlPoint1 = new PointF();
    private final PointF mControlPoint2 = new PointF();

    public BezierEvaluator(float x1, float y1, float x2, float y2) {
        mControlPoint1.x = x1;
        mControlPoint1.y = y1;
        mControlPoint2.x = x2;
        mControlPoint2.y = y2;
    }

    /**
     * @param fraction
     * @param startValue
     * @param endValue
     * @return
     */
    @Override
    public Integer evaluate(float fraction, Integer startValue, Integer endValue) {
        Log.d(TAG, "fraction = " + fraction + " ,startValue = " + startValue + " ,endValue = " + endValue);
        return (int) (startValue + (endValue - startValue) * cubicCurves(fraction, 0, mControlPoint1.y, mControlPoint2.y, 1));
    }

    public static double cubicCurves(double t, double value0, double value1, double value2, double value3) {
        double value;
        double u = 1 - t;
        double tt = t * t;
        double uu = u * u;
        double uuu = uu * u;
        double ttt = tt * t;
        value = uuu * value0;
        value += 3 * uu * t * value1;
        value += 3 * u * tt * value2;
        value += ttt * value3;
        return value;
    }
}
