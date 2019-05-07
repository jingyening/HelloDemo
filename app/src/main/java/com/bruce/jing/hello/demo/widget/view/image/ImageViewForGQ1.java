package com.bruce.jing.hello.demo.widget.view.image;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;

/**
 * @author bruce jing
 * @date 2019/4/24
 */
public class ImageViewForGQ1 extends android.support.v7.widget.AppCompatImageView {

    private static final String TAG = "ImageViewForGQ";

    public ImageViewForGQ1(Context context) {
        super(context);
    }

    public ImageViewForGQ1(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ImageViewForGQ1(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setPressed(boolean pressed) {
        super.setPressed(pressed);
        if(pressed){
            refreshDrawablePressed();
        }else{
            resetDrawableState();
        }
    }


    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (enabled) {
            resetDrawableState();
        } else {
            refreshDrawableDisabled();
        }
    }

    private void refreshDrawablePressed() {
        Log.d(TAG, "refreshDrawablePressed: ");
        Drawable background = getBackground();
        if (background != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                //上层覆盖模式
                setBackgroundTintMode(PorterDuff.Mode.SRC_ATOP);
                setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#14CCDDFF")));
            }
        }
        Drawable image = getDrawable();
        if (image != null) {
            setColorFilter(Color.parseColor("#14CCDDFF"));
        }
    }

    private void refreshDrawableDisabled() {
        Log.d(TAG, "refreshDrawableDisabled: ");
        Drawable background = getBackground();
        if (background != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                //对图片交集求与
                setBackgroundTintMode(PorterDuff.Mode.MULTIPLY);
                setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#33FFFFFF")));
            }
        }
        Drawable image = getDrawable();
        if (image != null) {
            setColorFilter(Color.parseColor("#14CCDDFF"), PorterDuff.Mode.DARKEN);
        }
    }



    private void resetDrawableState(){
        Drawable background = getBackground();
        if (background != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                setBackgroundTintList(null);
            }
        }
        Drawable image = getDrawable();
        if (image != null) {
            setColorFilter(Color.parseColor("#00FFFFFF"));
        }
    }
}
