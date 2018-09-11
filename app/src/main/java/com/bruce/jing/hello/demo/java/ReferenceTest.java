package com.bruce.jing.hello.demo.java;

import com.bruce.jing.hello.demo.util.log.JLogUtil;

public class ReferenceTest {

    private static Object mObject = new Object();

    /**
     * 局部变量指向成员变量的栈地址，成员变量置空，局部变量也为空
     */
    public  static void testMethodReference(){
        Object obj = mObject;
        mObject = null;
        JLogUtil.d("brucetest","obj = "+obj+"  mObject = "+mObject);
    }

}
