package com.bruce.jing.hello.demo.widget.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * @author bruce jing
 * @date 2019/4/11
 */
public class BlurView extends AppCompatImageView {

    private static final String TAG = "BlurView";

    public BlurView(Context context) {
        super(context);
    }

    public BlurView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BlurView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    Bitmap blurBitmap;
    @Override
    public void invalidate() {
        super.invalidate();
//        Bitmap bitmap = getPreDrawBitmap();
//        Log.d(TAG, "invalidate:  bitmap = " + bitmap);
//        if (bitmap != null) {
//            Log.d(TAG, "invalidate: bitmap width = " + bitmap.getWidth() + " , height = " + bitmap.getHeight());
//            blurBitmap = BlurBitmapUtil.blurBitmap(getContext(), bitmap, 8);
//
//        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Bitmap bitmap = getPreDrawBitmap();
        if (bitmap != null) {
//            canvas.drawBitmap(blurBitmap, 0, 0, null);
//            Log.d(TAG, "onDraw: bitmap width = " + blurBitmap.getWidth() + " , height = " + blurBitmap.getHeight());
//            Bitmap blurBitmap = BlurBitmapUtil.blurBitmap(getContext(), bitmap, 8);
//            canvas.drawBitmap(blurBitmap, 0, 0, null);
        }
        super.onDraw(canvas);
    }


    private Bitmap getPreDrawBitmap() {
        View rootView = getRootView();
        if (rootView == null || rootView == this) {
            return null;
        }
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(bitmap);
        Matrix matrix = new Matrix();
        int[] positionInWindow = new int[2];
        getLocationInWindow(positionInWindow);
        Log.d(TAG, "getPreDrawBitmap: positionInWindow = " + positionInWindow[0] + "  positionInWindow = " + positionInWindow[1]);
        Log.d(TAG, "rootView: width = " + rootView.getWidth() + "  height = " + rootView.getHeight());
        canvas.translate(-positionInWindow[0],-positionInWindow[1]);
        rootView.draw(canvas);
        return bitmap;
    }

    private Bitmap getPreDrawBitmap2() {
        Log.d(TAG, "getPreDrawBitmap2");
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(bitmap);
        draw(canvas);
        return bitmap;
    }


}
