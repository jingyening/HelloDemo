package com.bruce.jing.hello.demo.widget;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import androidx.annotation.NonNull;import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Choreographer;
import android.view.Choreographer.FrameCallback;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.bruce.jing.hello.demo.R;

import java.lang.ref.WeakReference;

public class BlurLayout extends FrameLayout {

    private static final String TAG = "BlurLayout";
    
    public static final float DEFAULT_DOWNSCALE_FACTOR = 0.12F;
    public static final int DEFAULT_BLUR_RADIUS = 12;
    public static final int DEFAULT_FPS = 60;
    private float mDownscaleFactor;
    private int mBlurRadius;
    private int mFPS;
    private WeakReference<View> mActivityView;
    private FrameCallback invalidationLoop = new FrameCallback() {
        public void doFrame(long frameTimeNanos) {
            invalidate();
            postFrameCallback();
        }
    };

    public BlurLayout(Context context) {
        super(context, (AttributeSet)null);
    }

    public BlurLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        BlurKit.init(context);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.BlurLayout, 0, 0);

        try {
            this.mDownscaleFactor = a.getFloat( R.styleable.BlurLayout_downscaleFactor, 0.12F);
            this.mBlurRadius = a.getInteger( R.styleable.BlurLayout_blurRadius, 12);
            this.mFPS = a.getInteger( R.styleable.BlurLayout_fps, 60);
        } finally {
            a.recycle();
        }
        Log.d(TAG,"BlurLayout  mFPS = "+mFPS);
        if (this.mFPS > 0) {
            Choreographer.getInstance().postFrameCallback(this.invalidationLoop);
        }

    }

    private void postFrameCallback() {
        if (this.mFPS <= 0) {
            Log.d(TAG, "postFrameCallback mFPS <= 0 ");
        }
        if (getVisibility() != VISIBLE) {
            Log.d(TAG, "postFrameCallback view not visible ");
            return;
        }
        if (getWindowVisibility() != VISIBLE) {
            Log.d(TAG, "postFrameCallback window not visible ");
            return;
        }
        if (!isAttachedToWindow()) {
            Log.d(TAG, "postFrameCallback window not attached ");
            return;
        }

        Choreographer.getInstance().postFrameCallbackDelayed(invalidationLoop, (long) (1000 / mFPS));
    }

    public void invalidate() {
        super.invalidate();
        Bitmap bitmap = this.blur();
        if (bitmap != null) {
            this.setBackground(new BitmapDrawable(bitmap));
        }

    }

    private Bitmap blur() {
        if (this.getContext() == null) {
            return null;
        } else {
            if (this.mActivityView == null || this.mActivityView.get() == null) {
                this.mActivityView = new WeakReference(this.getActivityView());
                if (this.mActivityView.get() == null) {
                    return null;
                }
            }

            Point pointRelativeToActivityView = this.getPositionInScreen();
            this.setAlpha(0.0F);
            int screenWidth = ((View)this.mActivityView.get()).getWidth();
            int screenHeight = ((View)this.mActivityView.get()).getHeight();
            int width = (int)((float)this.getWidth() * this.mDownscaleFactor);
            int height = (int)((float)this.getHeight() * this.mDownscaleFactor);
            int x = (int)((float)pointRelativeToActivityView.x * this.mDownscaleFactor);
            int y = (int)((float)pointRelativeToActivityView.y * this.mDownscaleFactor);
            int xPadding = this.getWidth() / 8;
            int yPadding = this.getHeight() / 8;
            int leftOffset = -xPadding;
            leftOffset = x + leftOffset >= 0 ? leftOffset : 0;
            int rightOffset = x + this.getWidth() + xPadding <= screenWidth ? xPadding : screenWidth - this.getWidth() - x;
            int topOffset = -yPadding;
            topOffset = y + topOffset >= 0 ? topOffset : 0;
            int bottomOffset = y + height + yPadding <= screenHeight ? yPadding : 0;

            Bitmap bitmap;
            try {
                bitmap = this.getDownscaledBitmapForView((View)this.mActivityView.get(), new Rect(pointRelativeToActivityView.x + leftOffset, pointRelativeToActivityView.y + topOffset, pointRelativeToActivityView.x + this.getWidth() + Math.abs(leftOffset) + rightOffset, pointRelativeToActivityView.y + this.getHeight() + Math.abs(topOffset) + bottomOffset), this.mDownscaleFactor);
            } catch (NullPointerException var16) {
                return null;
            }

            bitmap = BlurKit.getInstance().blur(bitmap, this.mBlurRadius);
            bitmap = Bitmap.createBitmap(bitmap, (int)((float)Math.abs(leftOffset) * this.mDownscaleFactor), (int)((float)Math.abs(topOffset) * this.mDownscaleFactor), width, height);
            this.setAlpha(1.0F);
            return bitmap;
        }
    }

    private View getActivityView() {
        Activity activity;
        try {
            activity = (Activity)this.getContext();
        } catch (ClassCastException var3) {
            return null;
        }

        return activity.getWindow().getDecorView().findViewById(android.R.id.content);
    }

    private Point getPositionInScreen() {
        return this.getPositionInScreen(this);
    }

    private Point getPositionInScreen(View view) {
        if (this.getParent() == null) {
            return new Point();
        } else {
            ViewGroup parent;
            try {
                parent = (ViewGroup)view.getParent();
            } catch (Exception var4) {
                return new Point();
            }

            if (parent == null) {
                return new Point();
            } else {
                Point point = this.getPositionInScreen(parent);
                point.offset((int)view.getX(), (int)view.getY());
                return point;
            }
        }
    }

    private Bitmap getDownscaledBitmapForView(View view, Rect crop, float downscaleFactor) throws NullPointerException {
        View screenView = view.getRootView();
        int width = (int)((float)crop.width() * downscaleFactor);
        int height = (int)((float)crop.height() * downscaleFactor);
        if (screenView.getWidth() > 0 && screenView.getHeight() > 0 && width > 0 && height > 0) {
            float dx = (float)(-crop.left) * downscaleFactor;
            float dy = (float)(-crop.top) * downscaleFactor;
            Bitmap bitmap = Bitmap.createBitmap(width, height, Config.ARGB_4444);
            Canvas canvas = new Canvas(bitmap);
            Matrix matrix = new Matrix();
            matrix.preScale(downscaleFactor, downscaleFactor);
            matrix.postTranslate(dx, dy);
            canvas.setMatrix(matrix);
            screenView.draw(canvas);
            return bitmap;
        } else {
            throw new NullPointerException();
        }
    }

    public void setDownscaleFactor(float downscaleFactor) {
        this.mDownscaleFactor = downscaleFactor;
        this.invalidate();
    }

    public void setBlurRadius(int blurRadius) {
        this.mBlurRadius = blurRadius;
        this.invalidate();
    }

    public void setFPS(int fps) {
        this.mFPS = fps;
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        Log.d(TAG,"onAttachedToWindow");
        postFrameCallback();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Log.d(TAG,"onDetachedFromWindow");
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        Log.d(TAG,"onWindowFocusChanged hasWindowFocus = "+hasWindowFocus);
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        Log.d(TAG,"onVisibilityChanged visibility = "+visibility+"  , changedView = "+changedView);
        postFrameCallback();
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        Log.d(TAG,"onWindowVisibilityChanged visibility = "+visibility);
        postFrameCallback();
    }

}