package com.bruce.jing.hello.demo.java.concurrent.lock;

import com.bruce.jing.hello.demo.util.log.JLogUtil;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;

/**
 * -------------------------------------
 * 作者：bruce jing
 * -------------------------------------
 * 时间：2018/8/28 下午8:19
 * -------------------------------------
 * 描述：
 * -------------------------------------
 * 备注：
 * -------------------------------------
 */
public class TestLock {

    private static Object mLockObj = new Object();

    private static CountDownLatch mCountDownLatch = new CountDownLatch(2);
    public static void testAwaitAndCountDown(){
        new Thread(){
            @Override
            public void run() {
                JLogUtil.d(TestLock.class," testAwait0000  ");
                JLogUtil.d(TestLock.class," testAwait  count = "+mCountDownLatch.getCount());
                mCountDownLatch.countDown();
                JLogUtil.d(TestLock.class," testAwait  count = "+mCountDownLatch.getCount());

                try {
                    mCountDownLatch.await();
                    JLogUtil.d(TestLock.class," testAwait  count = "+mCountDownLatch.getCount());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                JLogUtil.d(TestLock.class," testAwait1111");

                try {
                    mCountDownLatch.await();
                    JLogUtil.d(TestLock.class," testAwait  count = "+mCountDownLatch.getCount());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                JLogUtil.d(TestLock.class," testAwait2222");
            }
        }.start();

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {

                /**
                 * ok
                 */
                mCountDownLatch.countDown();

                /**
                 * not ok
                 */
//                synchronized (mCountDownLatch) {
//                    mCountDownLatch.notifyAll();
//                }

            }
        },3000);


    }

}
