package com.bruce.jing.hello.demo.widget.view.image;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import androidx.annotation.NonNull;import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import android.util.AttributeSet;
import android.util.Log;

import com.bruce.jing.hello.demo.R;

/**
 * @author bruce jing
 * @date 2019/4/24
 */
public class ImageViewForGQ extends AppCompatImageView {

    private static final String TAG = "ImageViewForGQ";

    public static final PorterDuff.Mode RESET_MODE = PorterDuff.Mode.DST;
    /**
     * 如果RESET MODE为DST，color可以是任意颜色
     */
    public static final int RESET_COLOR = Color.parseColor("#FF000000");

    /**
     * 60%的不透明
     */
    public static final int DEFAULT_PRESS_COLOR = Color.parseColor("#99FFFFFF");
    public static final PorterDuff.Mode DEFAULT_MODE = PorterDuff.Mode.MULTIPLY;
    /**
     * 20%的不透明
     */
    public static final int DEFAULT_DISABLE_COLOR = Color.parseColor("#33FFFFFF");

    private int mPressColor;
    private PorterDuff.Mode mPressMode;
    private int mDisableColor;
    private PorterDuff.Mode mDisableMode;

    public ImageViewForGQ(Context context) {
        this(context, null);
    }

    public ImageViewForGQ(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ImageViewForGQ(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        final TypedArray a = context.obtainStyledAttributes(
                attrs, R.styleable.PressTintImageView);
        mPressColor = a.getColor(R.styleable.PressTintImageView_pressColor, DEFAULT_PRESS_COLOR);
        mDisableColor = a.getColor(R.styleable.PressTintImageView_disableColor, DEFAULT_DISABLE_COLOR);
        int pressColorType = a.getInteger(R.styleable.PressTintImageView_pressColorMode, 1);
        mPressMode = getDuffModeByTye(pressColorType);
        int disableColorType = a.getInteger(R.styleable.PressTintImageView_disableColorMode, 1);
        mDisableMode = getDuffModeByTye(disableColorType);
        a.recycle();
    }

    private PorterDuff.Mode getDuffModeByTye(int type) {
        switch (type) {
            case 1:
                return PorterDuff.Mode.MULTIPLY;
            case 2:
                return  PorterDuff.Mode.SRC_ATOP;
            default:
                break;
        }
        return DEFAULT_MODE;
    }

    @Override
    public void setPressed(boolean pressed) {
        super.setPressed(pressed);
        if (pressed) {
            refreshDrawablePressed();
        } else {
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
                setBackgroundTintMode(mPressMode);
                setBackgroundTintList(ColorStateList.valueOf(mPressColor));
            }
        }
        Drawable image = getDrawable();
        if (image != null) {
            setColorFilter(mPressColor, mPressMode);
        }
    }

    private void refreshDrawableDisabled() {
        Log.d(TAG, "refreshDrawableDisabled: ");
        Drawable background = getBackground();
        if (background != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                //对图片交集求与
                setBackgroundTintMode(mDisableMode);
                setBackgroundTintList(ColorStateList.valueOf(mDisableColor));
            }
        }
        Drawable image = getDrawable();
        if (image != null) {
            setColorFilter(mDisableColor, mDisableMode);
        }
    }


    private void resetDrawableState() {
        Drawable background = getBackground();
        if (background != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                setBackgroundTintMode(RESET_MODE);
                setBackgroundTintList(ColorStateList.valueOf(RESET_COLOR));
            }
        }
        Drawable image = getDrawable();
        if (image != null) {
            setColorFilter(RESET_COLOR,RESET_MODE);
        }
    }
}
