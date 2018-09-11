package com.bruce.jing.hello.demo.java;

import android.util.Log;

import com.bruce.jing.hello.demo.java.bean.TestBean;

public class Test {

    public static void test(){
        TestBean tb = new TestBean();
        tb.setId(7);
        tb.setAddr("望京");
        tb.setAge(30);
        tb.setName("你大爷");
        tb.setEnable(true);
        TestBean tb2 = null;
        try {
            tb2 = tb.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        Log.d("brucetest","tb = "+tb+" tb2 = "+tb2);
    }
}
