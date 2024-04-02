package com.bruce.jing.hello.demo;

import android.app.Application;
import android.content.Context;

import com.bruce.jing.hello.demo.fragment.WTFragmentOp;
import com.bruce.jing.hello.demo.widget.BlurKit;

/**
 * @author bruce jing
 * @date 2019/4/11
 */
public class App extends Application {

    private static Context globalContext = null;
    private static final String TAG = "App";

    @Override
    public void onCreate() {
        globalContext = this.getApplicationContext();
//        Looper.getMainLooper().setMessageLogging(new Printer() {
//            @Override
//            public void println(String x) {
//                Log.d(TAG, "### debug performance:" + x);
//            }
//        });
        super.onCreate();
        BlurKit.init(this);
        registerActivityLifecycleCallbacks(WTFragmentOp.getInstance(this));
    }

    public static Context getGlobalContext(){
        return globalContext;
    }
}
