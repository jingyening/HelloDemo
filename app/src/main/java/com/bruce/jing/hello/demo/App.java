package com.bruce.jing.hello.demo;

import android.app.Application;
import android.os.Looper;
import android.util.Log;
import android.util.Printer;

import com.wonderkiln.blurkit.BlurKit;

/**
 * @author bruce jing
 * @date 2019/4/11
 */
public class App extends Application {

    private static final String TAG = "App";

    @Override
    public void onCreate() {
//        Looper.getMainLooper().setMessageLogging(new Printer() {
//            @Override
//            public void println(String x) {
//                Log.d(TAG, "### debug performance:" + x);
//            }
//        });
        super.onCreate();
        BlurKit.init(this);
    }
}
