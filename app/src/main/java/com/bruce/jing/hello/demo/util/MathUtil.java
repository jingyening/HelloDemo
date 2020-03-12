package com.bruce.jing.hello.demo.util;

public class MathUtil {

    /**
     * 三阶贝塞尔曲线公式
     * @param t
     * @param value0
     * @param value1
     * @param value2
     * @param value3
     * @return
     */
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
