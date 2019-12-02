package com.bruce.jing.hello.demo.widget.view.image;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.bruce.jing.hello.demo.R;


/**
 *
 * view被点击时，对view背景做colorfilter处理，达到特定的按压效果
 *
 * @author bruce jing
 * @date 2019/4/24
 */
public class PressTintImageView extends android.support.v7.widget.AppCompatImageView {

    private static final String TAG = "PressTintImageView";

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

    private Paint mShadowPaint;

    public PressTintImageView(Context context) {
        this(context, null);
    }

    public PressTintImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PressTintImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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

        mShadowPaint = new Paint();
        mShadowPaint.setStyle(Paint.Style.FILL);
        mShadowPaint.setAntiAlias(true);
        int shadowColor = Color.parseColor("#33000000");
        mShadowPaint.setColor(shadowColor);
        mShadowPaint.setShadowLayer(7.5f, -5, -5, shadowColor);
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

    @Override
    protected void onDraw(Canvas canvas) {
//        if (getLayerType() != LAYER_TYPE_SOFTWARE) {
//            setLayerType(LAYER_TYPE_SOFTWARE, null);
//        }
//        Rect bounds = getDrawable().getBounds();
//        int left = bounds.left;
//        int top = bounds.top;
//        int right = bounds.right;
//        int bottom = bounds.bottom;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            canvas.drawRoundRect(left, top, right, bottom, 0, 0, mShadowPaint);
//        }
        super.onDraw(canvas);

//        drawShadow(canvas);

    }

//    private boolean mIsShadowed = true;
//    private boolean mInvalidateShadow = true;
//    private
//    private void drawShadow(Canvas canvas){
//        if (mIsShadowed) {
//            // If need to redraw shadow
//            if (mInvalidateShadow) {
//                // If bounds is zero
//                if (mBounds.width() != 0 && mBounds.height() != 0) {
//                    // Reset bitmap to bounds
//                    mBitmap = Bitmap.createBitmap(
//                            mBounds.width(), mBounds.height(), Bitmap.Config.ARGB_8888
//                    );
//                    // Canvas reset
//                    mCanvas.setBitmap(mBitmap);
//
//                    // We just redraw
//                    mInvalidateShadow = false;
//                    // Main feature of this lib. We create the local copy of all content, so now
//                    // we can draw bitmap as a bottom layer of natural canvas.
//                    // We draw shadow like blur effect on bitmap, cause of setShadowLayer() method of
//                    // paint does`t draw shadow, it draw another copy of bitmap
//                    super.dispatchDraw(mCanvas);
//
//                    // Get the alpha bounds of bitmap
//                    final Bitmap extractedAlpha = mBitmap.extractAlpha();
//                    // Clear past content content to draw shadow
//                    mCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
//
//                    // Draw extracted alpha bounds of our local canvas
//                    mPaint.setColor(adjustShadowAlpha(false));
//                    mCanvas.drawBitmap(extractedAlpha, mShadowDx, mShadowDy, mPaint);
//
//                    // Recycle and clear extracted alpha
//                    extractedAlpha.recycle();
//                } else {
//                    // Create placeholder bitmap when size is zero and wait until new size coming up
//                    mBitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.RGB_565);
//                }
//            }
//
//            // Reset alpha to draw child with full alpha
//            mPaint.setColor(adjustShadowAlpha(true));
//            // Draw shadow bitmap
//            if (mCanvas != null && mBitmap != null && !mBitmap.isRecycled())
//                canvas.drawBitmap(mBitmap, 0.0F, 0.0F, mPaint);
//        }
//    }
}
