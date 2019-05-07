package com.bruce.jing.hello.demo;

import android.app.Application;

import com.wonderkiln.blurkit.BlurKit;

/**
 * @author bruce jing
 * @date 2019/4/11
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        BlurKit.init(this);
    }
}
