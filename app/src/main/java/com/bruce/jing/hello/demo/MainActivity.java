package com.bruce.jing.hello.demo;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bruce.jing.hello.demo.java.Test;
import com.bruce.jing.hello.demo.java.concurrent.lock.TestLock;
import com.bruce.jing.hello.demo.util.log.JLogUtil;

import tmp.TmpTest;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = "MainActivity";

    private Object mObject = new Object();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        final float density = metrics.density;
        int dpi = metrics.densityDpi;
        Log.d("brucetest","DisplayMetrics = "+ metrics.toString());
        float dimenValue = getResources().getDimension(R.dimen.test_dimen_value);
        Log.d("brucetest","dimenValue = "+dimenValue);
        final ImageView iv = findViewById(R.id.imageView);

        iv.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                TestLock.testAwaitAndCountDown();
                JLogUtil.d(MainActivity.class,Integer.toBinaryString(-38));
            }
        });

    }

    private boolean isScaleUp;

    private void scaleUpPhoto(ImageView view) {
//        Matrix matrix = new Matrix();
//        int dw = view.getDrawable().getIntrinsicWidth();
//        int dh = view.getDrawable().getIntrinsicHeight();
//
//        matrix.setTranslate(-dw/2,-dh/2);
//        matrix.postScale(1.2f, 1.2f, dw / 2, dh / 2);
//        view.setImageMatrix(matrix);
        view.setScaleX(1.2f);
        view.setScaleY(1.2f);
        isScaleUp = true;
    }

    private void scaleDownPhoto(ImageView view) {
//        Matrix matrix = new Matrix(view.getImageMatrix());
//        int dw = view.getDrawable().getIntrinsicWidth();
//        int dh = view.getDrawable().getIntrinsicHeight();
//        matrix.setTranslate(-dw/2,-dh/2);
//        matrix.postScale(1f, 1f, dw / 2, dh / 2);
//        view.setImageMatrix(matrix);
        view.setScaleX(1f);
        view.setScaleY(1f);
        isScaleUp = false;
    }

}
