package com.bruce.jing.hello.demo.widget.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.bruce.jing.hello.demo.util.CommonUtil;
import com.bruce.jing.hello.demo.util.log.JLogUtil;

import java.util.Iterator;
import java.util.ListIterator;
import java.util.Stack;

public class AnchorSeekBar extends View {

    private static final String LOG_TAG = "AnchorSeekBar";

    private boolean mAttached;

    /**
     * 保存overlay层的栈
     */
    private Stack<OverlayRect> mOverlayStack = new Stack<OverlayRect>();


    /**
     * 缓存新的一波操作之前的状态
     */

    private volatile boolean isInTransaction = false;
    private Stack<OverlayRect> mOverlayCache = new Stack<>();


    private OverlayRect mCurrentOverlay = new OverlayRect();
    private Paint mOverlayPaint = new Paint();
    private boolean mIsOverlayRender;
    private int mOverlayHeight = 24;

    /**
     * padding区域和进度条区域之间的留白
     */
//    private int mWhiteSpaceWidth ;
//    private int mHalfWhiteSpaceWidth ;



    /**
     * anchor bar 相关属性
     */
    private Drawable mAnchor;
    private int mAnchorOffset;
    private boolean mIsAnchorVisible;
    private int mAnchorPosition = 20;
    private boolean mIsDraggingAnchor;


    /**
     * seek bar 相关属性
     */
    private static final float THUMB_ANIMATE_SCALE_TATE = 1.2f;
    //seekbar 点击区域扩大（dp）
    private static final int THUMB_TOUCH_AREA_EXPANSION =18;
    private int mThumbTouchArea;
    private Drawable mThumb;
    private ColorStateList mThumbTintList = null;
    private PorterDuff.Mode mThumbTintMode = null;
    private boolean mHasThumbTint = false;
    private boolean mHasThumbTintMode = false;
    private int mThumbOffset;
    private boolean mIsDraggingThumb;
    private ValueAnimator mThumbScaleUpAnimator = null;
    private ValueAnimator mThumbScaleDownAnimator = null;


    /**
     * progress 相关属相
     */
    private Drawable mProgressDrawable;
    private int mProgressHeight = 24;
//    private int mProgressWidth;

    private int mMax =100;
    private int mProgress;

    /**
     * On touch, this offset plus the scaled value from the position of the
     * touch will form the progress value. Usually 0.
     */
    float mTouchProgressOffset;

    /**
     * Whether this is user seekable.
     */
    boolean mIsUserSeekable = true;


    private static final int NO_ALPHA = 0xFF;
    private float mDisabledAlpha;

    private int mScaledTouchSlop;
    private float mTouchDownX;
    private int mTouchDownProgress;



    private OnAnchorBarChangeListener mOnAnchorBarChangeListener;
    private OnSeekBarChangeListener mOnSeekBarChangeListener;

    public AnchorSeekBar(Context context) {
        this(context,null);
    }

    public AnchorSeekBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);//bruce todo
    }

    public AnchorSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setLayerType(LAYER_TYPE_SOFTWARE,null);
        mThumbTouchArea = CommonUtil.dip2px(context,THUMB_TOUCH_AREA_EXPANSION);
    }

//    public AnchorSeekBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
//        super(context, attrs, defStyleAttr, defStyleRes);
//        final TypedArray a = context.obtainStyledAttributes(
//                attrs, R.styleable.SeekBar, defStyleAttr, defStyleRes);
//
//        final Drawable thumb = a.getDrawable(R.styleable.SeekBar_thumb);
//        setThumb(thumb);
//
//        if (a.hasValue(R.styleable.SeekBar_thumbTintMode)) {
//            mThumbTintMode = Drawable.parseTintMode(a.getInt(
//                    R.styleable.SeekBar_thumbTintMode, -1), mThumbTintMode);
//            mHasThumbTintMode = true;
//        }
//
//        if (a.hasValue(R.styleable.SeekBar_thumbTint)) {
//            mThumbTintList = a.getColorStateList(R.styleable.SeekBar_thumbTint);
//            mHasThumbTint = true;
//        }
//
//        mSplitTrack = a.getBoolean(R.styleable.SeekBar_splitTrack, false);
//
//        // Guess thumb offset if thumb != null, but allow layout to override.
//        final int thumbOffset = a.getDimensionPixelOffset(R.styleable.SeekBar_thumbOffset, getThumbOffset());
//        setThumbOffset(thumbOffset);
//
//        final boolean useDisabledAlpha = a.getBoolean(R.styleable.SeekBar_useDisabledAlpha, true);
//        a.recycle();
//
//        if (useDisabledAlpha) {
//            final TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.Theme, 0, 0);
//            mDisabledAlpha = ta.getFloat(R.styleable.Theme_disabledAlpha, 0.5f);
//            ta.recycle();
//        } else {
//            mDisabledAlpha = 1.0f;
//        }
//
//        applyThumbTint();
//
//        mScaledTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

//    }


    /**
     * 设置anchor bar
     * @param anchor
     */
    public void setAnchor(Drawable anchor){
        final boolean needUpdate;
        if (mAnchor != null && anchor != mAnchor) {
            mAnchor.setCallback(null);
            needUpdate = true;
        } else {
            needUpdate = false;
        }

        if (anchor != null) {
            anchor.setCallback(this);

            // Assuming the thumb drawable is symmetric, set the thumb offset
            // such that the thumb will hang halfway off either edge of the
            // progress bar.
            mAnchorOffset = anchor.getIntrinsicWidth() / 2;

            // If we're updating get the new states
            if (needUpdate &&
                    (anchor.getIntrinsicWidth() != mAnchor.getIntrinsicWidth()
                            || anchor.getIntrinsicHeight() != mAnchor.getIntrinsicHeight())) {
                requestLayout();
            }
        }

        mAnchor = anchor;

//        applyThumbTint();
        invalidate();

        if (needUpdate) {
            updateThumbAnchorAndTrackPos(getWidth(), getHeight());
            if (anchor != null && anchor.isStateful()) {
                // Note that if the states are different this won't work.
                // For now, let's consider that an app bug.
                int[] state = getDrawableState();
                anchor.setState(state);
            }
        }
    }


    /**
     * 设置 anchorbar 是否显示
     * @param isVisible
     */
    public void setAnchorVisible(boolean isVisible){
        mIsAnchorVisible = isVisible;
    }

    public boolean getAnchorVisible(){
        return mIsAnchorVisible;
    }

    /**
     * 设置anchorbar 显示位置
     * @param anchorPosition
     */
    public void setAnchorPosition(int anchorPosition){
        mAnchorPosition = anchorPosition;
        float scale = getScale(anchorPosition);

        final int paddedHeight = getHeight() - getPaddingTop() - getPaddingBottom();
        final Drawable anchor = mAnchor;

        // The max height does not incorporate padding, whereas the height
        // parameter does.
        final int trackHeight = /*Math.min(mMaxHeight, paddedHeight)*/paddedHeight;
        final int anchorHeight = anchor == null ? 0 : anchor.getIntrinsicHeight();

        // Apply offset to whichever item is taller.
        final int anchorOffset = (paddedHeight - anchorHeight) / 2;

        setAnchorPos(getWidth(), anchor,scale,anchorOffset);
        invalidate();
    }

    public int getAnchorPosition(){
        return mAnchorPosition;
    }

    /**
     * Sets the thumb that will be drawn at the end of the progress meter within the SeekBar.
     * <p>
     * If the thumb is a valid drawable (i.e. not null), half its width will be
     * used as the new thumb offset (@see #setThumbOffset(int)).
     *
     * @param thumb Drawable representing the thumb
     */
    public void setThumb(Drawable thumb) {
        final boolean needUpdate;
        // This way, calling setThumb again with the same bitmap will result in
        // it recalcuating mThumbOffset (if for example it the bounds of the
        // drawable changed)
        if (mThumb != null && thumb != mThumb) {
            mThumb.setCallback(null);
            needUpdate = true;
        } else {
            needUpdate = false;
        }

        if (thumb != null) {
            thumb.setCallback(this);

            // Assuming the thumb drawable is symmetric, set the thumb offset
            // such that the thumb will hang halfway off either edge of the
            // progress bar.
            mThumbOffset = thumb.getIntrinsicWidth() / 2;

            // If we're updating get the new states
            if (needUpdate &&
                    (thumb.getIntrinsicWidth() != mThumb.getIntrinsicWidth()
                            || thumb.getIntrinsicHeight() != mThumb.getIntrinsicHeight())) {
                requestLayout();
            }
        }

        mThumb = thumb;

        invalidate();

        if (needUpdate) {
            updateThumbAnchorAndTrackPos(getWidth(), getHeight());
            if (thumb != null && thumb.isStateful()) {
                // Note that if the states are different this won't work.
                // For now, let's consider that an app bug.
                int[] state = getDrawableState();
                thumb.setState(state);
            }
        }
    }

    /**
     * Return the drawable used to represent the scroll thumb - the component that
     * the user can drag back and forth indicating the current value by its position.
     *
     * @return The current thumb drawable
     */
    public Drawable getThumb() {
        return mThumb;
    }



    @Override
    protected boolean verifyDrawable(Drawable who) {
        return who == mThumb || super.verifyDrawable(who);
    }


    @Override
    public void jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState();

        if (mThumb != null) {
            mThumb.jumpToCurrentState();
        }
        if(mAnchor != null){
            mAnchor.jumpToCurrentState();
        }
    }


    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();


        final Drawable thumb = mThumb;
        if (thumb != null && thumb.isStateful()
                && thumb.setState(getDrawableState())) {
            invalidateDrawable(thumb);
        }

        final Drawable anchor = mAnchor;
        if (anchor != null && anchor.isStateful()
                && anchor.setState(getDrawableState())) {
            invalidateDrawable(anchor);
        }
    }


    @Override
    public void drawableHotspotChanged(float x, float y) {
        super.drawableHotspotChanged(x, y);
//        if (mThumb != null) {
//            mThumb.setHotspot(x, y);
//        }
//        if(mAnchor != null){
//            mAnchor.setHotspot(x,y);
//        }
    }



    void onProgressRefresh(float scale, boolean fromUser, int progress) {

        final Drawable thumb = mThumb;
        if (thumb != null) {
            setThumbPos(getWidth(), thumb, scale, Integer.MIN_VALUE);

            // Since we draw translated, the drawable's bounds that it signals
            // for invalidation won't be the actual bounds we want invalidated,
            // so just invalidate this whole view.

        }

        if(mIsOverlayRender){
            Rect rect = mCurrentOverlay.rect;
            int availableWidth = getWidth() - getPaddingLeft() - getPaddingRight() ;
            int right = getPaddingLeft()  + (int)(availableWidth * scale + 0.5f);

            rect.right = right;
        }
        invalidate();

        if(mOnSeekBarChangeListener != null){
            mOnSeekBarChangeListener.onProgressChanged(this,progress,fromUser);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        updateThumbAnchorAndTrackPos(w, h);
    }

    /**
     * 更新 seekbar、anchorbar和progressdrawable的位置
     * @param w
     * @param h
     */
    private void updateThumbAnchorAndTrackPos(int w, int h) {
        final int paddedHeight = h - getPaddingTop() - getPaddingBottom();
        final Drawable track = mProgressDrawable;
        final Drawable thumb = mThumb;
        final Drawable anchor = mAnchor;

        // The max height does not incorporate padding, whereas the height
        // parameter does.
        final int progressHeight = mProgressHeight;
        final int trackHeight = Math.min(progressHeight, paddedHeight);
        final int thumbHeight = thumb == null ? 0 : thumb.getIntrinsicHeight();
        final int anchorHeight = anchor == null ? 0 : anchor.getIntrinsicHeight();

//        DLog.logCommon("brucetest", "thumbHeight = "+thumbHeight);
        // Apply offset to whichever item is taller.
        final int trackOffset = (paddedHeight - trackHeight) / 2;
        final int thumbOffset = (paddedHeight - thumbHeight) / 2;
        final int anchorOffset = (paddedHeight - anchorHeight) / 2;


        if (track != null) {
//            final int trackWidth = w - getPaddingRight() - getPaddingLeft();
            track.setBounds(getPaddingLeft() , getPaddingTop() + trackOffset, getWidth() - getPaddingRight(), getPaddingTop() + trackOffset + trackHeight);
        }

        if (thumb != null) {
            setThumbPos(w, thumb, getScale(), thumbOffset);
        }

        if(anchor != null){
            int position = mAnchorPosition;
            setAnchorPos(w, anchor, getScale(position), anchorOffset);
        }
    }

    private float getScale() {
        final int max = getMax();
        return max > 0 ? getProgress() / (float) max : 0;
    }


    private float getScale(int progress){
        final int max = getMax();
        return max > 0 ? progress / (float) max : 0;
    }

    private int calculateProgressWithPosition(int position){
        int availableWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        if(availableWidth <= 0){
            return 0;
        }
        int pos = position - getPaddingLeft();
        int progress = pos * mMax / availableWidth;
        return progress;
    }

    public void setMax(int max){
        if(max < 0 || max > Integer.MAX_VALUE){
            throw new IllegalArgumentException(" illegal max value");
        }
        mMax = max;
    }

    public int getMax(){
        return mMax;
    }

    public synchronized void setProgress(int progress){
        setProgressInternal(progress, false, false);
    }

    public int getProgress(){
        return mProgress;
    }

    synchronized boolean setProgressInternal(int progress, boolean fromUser, boolean animate) {

        if(progress < 0){
            progress = 0;
        }else if(progress > mMax){
            progress = mMax;
        }

        if (progress == mProgress) {
            // No change from current.
            return false;
        }

        mProgress = progress;
        refreshProgress(mProgress, fromUser);

        return true;
    }

    private synchronized void refreshProgress(int progress, boolean fromUser){
        doRefreshProgress(progress,fromUser);
    }

    private synchronized void doRefreshProgress(int progress, boolean fromUser) {
        final float scale = mMax > 0 ? progress / (float) mMax : 0;
        onProgressRefresh(scale, fromUser, progress);

    }

    /**
     * 设置进度条背景
     * @param drawable
     */
    public void setProgressDrawable(Drawable drawable){
        mProgressDrawable = drawable;

        int paddedHeight = getHeight() - getPaddingTop() - getPaddingBottom();
        final int progressHeight = mProgressHeight;
        final int trackHeight = Math.min(progressHeight, paddedHeight);
        final int offset = (paddedHeight - trackHeight) / 2;

        int left = getPaddingLeft() ;
        int top = getPaddingTop() + offset ;
        int right = getWidth() - getPaddingRight();
        int bottom = getHeight() - getPaddingBottom() - offset;


        mProgressDrawable.setBounds(left,top,right,bottom);
    }

//    public void setProgressHeight(int height){
//
//    }

    public void setOverlayHeight(int height){
        mOverlayHeight = height;
    }


    private void  setAnchorPos(int w, Drawable anchor, float scale, int offset) {

        int available = w - getPaddingLeft() - getPaddingRight() ;
        final int anchorWidth = anchor.getIntrinsicWidth();
        final int anchorHeight = anchor.getIntrinsicHeight();


        final int anchorPos = getPaddingLeft() - mAnchorOffset + (int) (scale * available + 0.5f) ;

        final int top, bottom;
        if (offset == Integer.MIN_VALUE) {
            final Rect oldBounds = anchor.getBounds();
            top = oldBounds.top;
            bottom = oldBounds.bottom;
        } else {
            top = getPaddingTop() + offset;
            bottom = top + anchorHeight;
        }

        final int left = /*(isLayoutRtl() && mMirrorForRtl) ? available - thumbPos :*/ anchorPos;
        final int right = left + anchorWidth;

        // Canvas will be translated, so 0,0 is where we start drawing
        anchor.setBounds(left, top, right, bottom);

    }

    /**
     * Updates the thumb drawable bounds.
     *
     * @param w Width of the view, including padding
     * @param thumb Drawable used for the thumb
     * @param scale Current progress between 0 and 1
     * @param offset Vertical offset for centering. If set to
     *            {@link Integer#MIN_VALUE}, the current offset will be used.
     */
    private void setThumbPos(int w, Drawable thumb, float scale, int offset) {
        int available = w - getPaddingLeft() - getPaddingRight() ;

        Rect rect = thumb.getBounds();

        int rectWidth = Math.abs(rect.right - rect.left);
        int rectHeight = Math.abs(rect.bottom - rect.top);

        final int thumbWidth = Math.max(thumb.getIntrinsicWidth(),rectWidth);
        final int thumbHeight = Math.max(thumb.getIntrinsicHeight(),rectHeight);

        // The extra space for the thumb to move on the track
        mThumbOffset = thumbWidth / 2;

        final int thumbPos = getPaddingLeft() - mThumbOffset + (int) (scale * available + 0.5f) ;

        final int top, bottom;
        if (offset == Integer.MIN_VALUE) {
            final Rect oldBounds = thumb.getBounds();
            top = oldBounds.top;
            bottom = oldBounds.bottom;
        } else {
            top = getPaddingTop() + offset;
            bottom = top + thumbHeight;
        }

        final int left = /*(isLayoutRtl() && mMirrorForRtl) ? available - thumbPos :*/ thumbPos;
        final int right = left + thumbWidth;

//        final Drawable background = getBackground();
//        if (background != null) {
//            final int offsetX = getPaddingLeft() - mThumbOffset;
//            final int offsetY = getPaddingTop();
//            background.setHotspotBounds(left + offsetX, top + offsetY,
//                    right + offsetX, bottom + offsetY);
//        }

        // Canvas will be translated, so 0,0 is where we start drawing
        thumb.setBounds(left, top, right, bottom);
    }


//    /**
//     * @hide
//     */
//    @Override
//    public void onResolveDrawables(int layoutDirection) {
//        super.onResolveDrawables(layoutDirection);
//        if (mThumb != null) {
//            mThumb.setLayoutDirection(layoutDirection);
//        }
//    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        doRefreshProgress(mProgress,false);
        mAttached = true;
    }

    @Override
    protected void onDetachedFromWindow() {

        releaseAnimator();
        // This should come after stopAnimation(), otherwise an invalidate message remains in the
        // queue, which can prevent the entire view hierarchy from being GC'ed during a rotation
        super.onDetachedFromWindow();

        mAttached = false;

    }


    private void releaseAnimator(){
        if(mThumbScaleDownAnimator != null){
            if(mThumbScaleDownAnimator.isRunning()){
                mThumbScaleDownAnimator.cancel();
            }
            mThumbScaleDownAnimator = null;
        }

        if(mThumbScaleUpAnimator != null){
            if(mThumbScaleUpAnimator.isRunning()){
                mThumbScaleUpAnimator.cancel();
            }
            mThumbScaleUpAnimator = null;
        }
    }



    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int dw = 0;
        int dh = 0;

        final Drawable d = mProgressDrawable;
        if (d != null) {
            dw = Math.max(getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec), d.getIntrinsicWidth());
            dh = Math.max(getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec), d.getIntrinsicHeight());
        }

        int thumbHeight = mThumb == null ? 0 : mThumb.getIntrinsicHeight();
        int anchorHeight = mAnchor == null? 0 : mAnchor.getIntrinsicHeight();

        dh = Math.max(thumbHeight, dh);
        dh = Math.max(anchorHeight,dh);

        dw += getPaddingLeft() + getPaddingRight();
        dh += getPaddingTop() + getPaddingBottom();

//        DLog.logCommon(LOG_TAG, "onMeasure dh = " + dh + "  dw = "+dw);
        final int measuredWidth = resolveSizeAndState(dw, widthMeasureSpec, 0);
        final int measuredHeight = resolveSizeAndState(dh, heightMeasureSpec, 0);
        setMeasuredDimension(measuredWidth, measuredHeight);
    }

    private Bitmap mOverlayBitmap = null;
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        /**
         * 先绘制的是dst，后绘制的是src
         */
        //画progress背景
        if(mProgressDrawable != null){
            Drawable d = mProgressDrawable;
            d.draw(canvas);
        }

        if(mOverlayBitmap == null || mOverlayBitmap.isRecycled()){
            mOverlayBitmap = Bitmap.createBitmap(getWidth(),getHeight(), Bitmap.Config.ARGB_8888);
        }

        //清空bitmap内容
        mOverlayPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        Canvas cvs = new Canvas(mOverlayBitmap);
        cvs.drawPaint(mOverlayPaint);

        //画overlay
        //先画堆栈里面的overlay区域
        ListIterator<OverlayRect> iterator = mOverlayStack.listIterator();
        while (iterator.hasNext()){
            OverlayRect overlayRect = iterator.next();
            drawOverlayRect(cvs, overlayRect);
        }
        //再画当前缓存的OverlayRect区域
        drawOverlayRect(cvs,mCurrentOverlay);

        //将overlay 图层的bitmap 画到画布上
        Rect srcRect = new Rect(0,0,mOverlayBitmap.getWidth(),mOverlayBitmap.getHeight());
        Rect dstRect = new Rect(0,0,getWidth(),getHeight());
        mOverlayPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
        canvas.drawBitmap(mOverlayBitmap,srcRect,dstRect,mOverlayPaint);

        //画anchor
        drawAhchor(canvas);
        //画thumb
        drawThumb(canvas);

    }




    //画进度条
//    @Override
//    void drawTrack(Canvas canvas) {
//        final Drawable thumbDrawable = mThumb;
//        if (thumbDrawable != null && mSplitTrack) {
//            final Insets insets = thumbDrawable.getOpticalInsets();
//            final Rect tempRect = mTempRect;
//            thumbDrawable.copyBounds(tempRect);
//            tempRect.offset(getPaddingLeft() - mThumbOffset, getPaddingTop());
//            tempRect.left += insets.left;
//            tempRect.right -= insets.right;
//
//            final int saveCount = canvas.save();
//            canvas.clipRect(tempRect, Region.Op.DIFFERENCE);
//            super.drawTrack(canvas);
//            canvas.restoreToCount(saveCount);
//        } else {
//            super.drawTrack(canvas);
//        }
//    }

    /**
     * 画悬浮层
     * @param canvas
     * @param overlayRect
     */
    void drawOverlayRect(Canvas canvas , OverlayRect overlayRect){
        mOverlayPaint.setColor(overlayRect.getColor());
        mOverlayPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
        canvas.drawRect(overlayRect.rect,mOverlayPaint);
    }

    /**
     * Draw the thumb.
     */
    void drawThumb(Canvas canvas) {
        if (mThumb != null) {

//            canvas.save();
            // Translate the padding. For the x, we need to allow the thumb to
            // draw in its extra space
//            canvas.translate(getPaddingLeft()  - mThumbOffset, getPaddingTop());
            mThumb.draw(canvas);
//            canvas.restore();
        }
    }

    /**
     * 画anchorbar
     * @param canvas
     */
    void drawAhchor(Canvas canvas){
        if(mAnchor != null && mIsAnchorVisible){
            mAnchor.draw(canvas);
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!mIsUserSeekable || !isEnabled()) {
            return false;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mTouchDownX = event.getX();
                mTouchDownProgress = mProgress;
                Drawable thumb = mThumb;
                boolean isInThumbRect = false;
                if(thumb != null){
                    //扩大点击区域
                    Rect outerRect = new Rect(thumb.getBounds());
                    outerRect.left -= mThumbTouchArea;
                    outerRect.right += mThumbTouchArea;
                    isInThumbRect = isInRect(outerRect,event.getX(),event.getY());
                }

                if(isInThumbRect){
                    setPressed(true);
                    invalidate(thumb.getBounds()); // This may be within the padding region
                    onStartThumbTouch();
                    trackTouchEvent(event);
                    attemptClaimDrag();
                    notifyStartThumbTouch();
                    startThumbScaleUpAnim();
                }else{
                    if(mIsAnchorVisible) {
                        Drawable anchor = mAnchor;
                        if (anchor != null) {
                            boolean isInAnchorRect = isInRect(anchor.getBounds(), event.getX(), event.getY());
                            if (isInAnchorRect) {
                                invalidate(anchor.getBounds()); // This may be within the padding region
                                onStartAnchorTouch();
                                anchorTouchEvent(event);
                                attemptClaimDrag();
                                notifyStartAnchorTouch();
                            }
                        }
                    }
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (mIsDraggingThumb) {
                    trackTouchEvent(event);
                }else if(mIsDraggingAnchor){
                    anchorTouchEvent(event);
                }else{

                }

                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (mIsDraggingThumb) {
                    trackTouchEvent(event);
                    onStopThumbTouch();
                    setPressed(false);
                    notifyStopThumbTouch();
                    startThumbScaleDownAnim();
                } else if(mIsDraggingAnchor){
                    anchorTouchEvent(event);
                    onStopAnchorTouch();
                    setPressed(false);
                    notifyStopAnchorTouch();
                } else{
                    // Touch up when we never crossed the touch slop threshold should
                    // be interpreted as a tap-seek to that location.
                    onStopThumbTouch();
                    onStopAnchorTouch();
                }
                // ProgressBar doesn't know to repaint the thumb drawable
                // in its inactive state when the touch stops (because the
                // value has not apparently changed)
                invalidate();
                break;
        }
        return true;
    }


    /**
     * 开始seekbar放大动画
     */
    private void startThumbScaleUpAnim() {
        final Drawable d = mThumb;

        if(d != null){
//            int w = d.getIntrinsicWidth();
//            int h = d.getIntrinsicHeight();
//            int targetH = (int)(h * THUMB_ANIMATE_SCALE_TATE);
//            int targetW = (int)(w / (float)h * targetH);
//
//            Rect rect = d.getBounds();
//
//            int currentW = Math.abs(rect.right - rect.left);
//            int currentH = Math.abs(rect.bottom - rect.top);
//
//            int wOffset = (targetW - currentW)/2;
//            int hOffset = (targetH - currentH)/2;
//
//            int left = rect.left - wOffset;
//            int right = rect.right + wOffset;
//            int top = rect.top - hOffset;
//            int bottom = rect.bottom + hOffset;
//
//            d.setBounds(left,top,right,bottom);

            //动画结束后，seekbar的宽 高

            int w = d.getIntrinsicWidth();
            int h = d.getIntrinsicHeight();
            final int targetH = (int)(h * THUMB_ANIMATE_SCALE_TATE);
            final int targetW = (int)(w / (float)h * targetH);

            //当前seekbar的宽 高
            Rect rect = d.getBounds();
            final int currentW = Math.abs(rect.right - rect.left);
            final int currentH = Math.abs(rect.bottom - rect.top);

            if(mThumbScaleUpAnimator == null){
                mThumbScaleUpAnimator = ValueAnimator.ofFloat(0f,1f);
                mThumbScaleUpAnimator.setDuration(100);
                mThumbScaleUpAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        float fraction = animation.getAnimatedFraction();
                        Rect r =  d.getBounds();
                        int cW = Math.abs(r.right - r.left);
                        int cH = Math.abs(r.bottom - r.top);

                        int tW = currentW + (int)((targetW - currentW) * fraction);
                        int tH = currentH + (int)((targetH - currentH) * fraction);

                        //rect放大 left right需要位移的大小
                        int widthOffset = (tW - cW ) / 2;
                        //rect放大  bottom top需要位移的大小
                        int heightOffset = (tH - cH) / 2;


                        int left = r.left - widthOffset;
                        int right = r.right + widthOffset;
                        int top = r.top - heightOffset;
                        int bottom = r.bottom + heightOffset;
                        d.setBounds(left,top,right,bottom);
                    }
                });
            }
            if(mThumbScaleUpAnimator.isRunning()){
                mThumbScaleUpAnimator.cancel();
            }
            mThumbScaleUpAnimator.start();

        }

    }

    private void startThumbScaleDownAnim(){
        final Drawable d = mThumb;

        if(d != null){
//            int targetH = d.getIntrinsicHeight();
//            int targetW = d.getIntrinsicWidth();
//            DLog.logCommon(LOG_TAG, "startThumbScaleDownAnim targetH = "+targetH);
//            Rect rect = d.getBounds();
//
//            int currentW = Math.abs(rect.right - rect.left);
//            int currentH = Math.abs(rect.bottom - rect.top);
//
//            int wOffset = (currentW - targetW)/2;
//            int hOffset = (currentH - targetH)/2;
//
//            int left = rect.left + wOffset;
//            int right = rect.right - wOffset;
//            int top = rect.top + hOffset;
//            int bottom = rect.bottom - hOffset;
//
//            d.setBounds(left,top,right,bottom);

            //???
//            invalidate();
            //动画结束后，seekbar的宽 高
            final int targetH = d.getIntrinsicHeight();
            final int targetW = d.getIntrinsicWidth();
            //当前seekbar的宽 高
            Rect rect = d.getBounds();
            final int currentW = Math.abs(rect.right - rect.left);
            final int currentH = Math.abs(rect.bottom - rect.top);

            if(mThumbScaleDownAnimator == null){
                mThumbScaleDownAnimator = ValueAnimator.ofFloat(0f,1f);
                mThumbScaleDownAnimator.setDuration(100);
                mThumbScaleDownAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        float fraction = animation.getAnimatedFraction();
                        Rect r =  d.getBounds();
                        int cW = Math.abs(r.right - r.left);
                        int cH = Math.abs(r.bottom - r.top);

                        int tW = currentW - (int)((currentW - targetW) * fraction);
                        int tH = currentH - (int)((currentH - targetH) * fraction);

                        //rect缩小 left right需要位移的大小
                        int widthOffset = (cW - tW) / 2;
                        //rect缩小 bottom top需要位移的大小
                        int heightOffset = (cH - tH) / 2;

                        int left = r.left + widthOffset;
                        int right = r.right - widthOffset;
                        int top = r.top + heightOffset;
                        int bottom = r.bottom - heightOffset;
                        d.setBounds(left,top,right,bottom);

                    }
                });
            }
            if(mThumbScaleDownAnimator.isRunning()){
                mThumbScaleDownAnimator.cancel();
            }
            mThumbScaleDownAnimator.start();

        }
    }

    private boolean isInRect(Rect rect, float x, float y){
        boolean isInXRange = x > rect.left && x < rect.right;
        boolean isInYRange = y > rect.top && y < rect.bottom;
        return isInXRange && isInYRange;
    }

    private void attemptClaimDrag() {
        if (getParent() != null) {
            getParent().requestDisallowInterceptTouchEvent(true);
        }
    }

//    private void setHotspot(float x, float y) {
//        final Drawable bg = getBackground();
//        if (bg != null) {
//            bg.setHotspot(x, y);
//        }
//    }

    /**
     * 根据MotionEvent 处理进度
     * @param event
     */
    private void trackTouchEvent(MotionEvent event) {
        final int width = getWidth();
        final int available = width - getPaddingLeft() - getPaddingRight();
        final int x = (int) event.getX();
        float scale;
        float progress = 0;
            if (x < getPaddingLeft() ) {
                scale = 0.0f;
            } else if (x > width - getPaddingRight()) {
                scale = 1.0f;
            } else {
                float scaleIncrement = (float)(x - mTouchDownX) / (float)available;
                scale = getScale(mTouchDownProgress) + scaleIncrement;
                progress = mTouchProgressOffset;
            }
        final int max = getMax();
        progress += scale * max;

//        setHotspot(x, (int) event.getY());
        setProgressInternal((int) progress, true,false);
    }

    private void anchorTouchEvent(MotionEvent event){
        final int width = getWidth();
        final int available = width - getPaddingLeft() - getPaddingRight() ;
        final int x = (int) event.getX();
        float scale;
        float progress = 0;
        if (x < getPaddingLeft()) {
            scale = 0.0f;
        } else if (x > width - getPaddingRight()) {
            scale = 1.0f;
        } else {
            scale = (float)(x - getPaddingLeft()) / (float)available;
        }
        final int max = getMax();
        int anchorPosition = (int)(scale * max);

        setAnchorPosition(anchorPosition);

    }


    /**
     * This is called when the user has started touching this widget.
     */
    void onStartThumbTouch() {
        mIsDraggingThumb = true;

    }

    /**
     * This is called when the user either releases his touch or the touch is
     * canceled.
     */
    void onStopThumbTouch() {
        mIsDraggingThumb = false;

    }

    void notifyStartThumbTouch(){
        if(mOnSeekBarChangeListener != null){
            mOnSeekBarChangeListener.onStartTrackingTouch(this);
        }
    }

    void notifyStopThumbTouch(){
        if(mOnSeekBarChangeListener != null){
            mOnSeekBarChangeListener.onStopTrackingTouch(this);
        }
    }

    /**
     * This is called when the user has started touching this widget.
     */
    void onStartAnchorTouch() {
        mIsDraggingAnchor = true;

    }

    /**
     * This is called when the user either releases his touch or the touch is
     * canceled.
     */
    void onStopAnchorTouch() {
        mIsDraggingAnchor = false;

    }

    void notifyStartAnchorTouch(){
        if(mOnAnchorBarChangeListener != null){
            mOnAnchorBarChangeListener.onStartAnchorTouch(this,mAnchorPosition);
        }
    }

    void notifyStopAnchorTouch(){
        if(mOnAnchorBarChangeListener != null){
            mOnAnchorBarChangeListener.onStopAnchorTouch(this,mAnchorPosition);
        }
    }


    public void beginTransaction(){
        if(isInTransaction){
            return;
        }
        isInTransaction = true;
        mOverlayCache.clear();
        Iterator<OverlayRect> iterator = mOverlayStack.iterator();
        while (iterator.hasNext()){
            OverlayRect overlayRect = iterator.next();
            mOverlayCache.push(overlayRect);
        }

    }


    public void endTransaction(boolean isSaveTransaction){
        if(!isInTransaction){
            return;
        }

        isInTransaction = false;

        if(isTwiceOverlayRectStackSame()){
            mOverlayCache.clear();//图层没有变化
            return;
        }

        if(isSaveTransaction){
            mOverlayCache.clear();//保存操作，删除缓存
        }else{
            //不保存操作，还原数据
            Iterator<OverlayRect> iterator = mOverlayCache.iterator();
            mOverlayStack.clear();;
            while (iterator.hasNext()){
                OverlayRect overlayRect = iterator.next();
                mOverlayStack.push(overlayRect);
            }
            mOverlayCache.clear();
            invalidate();
            JLogUtil.d(LOG_TAG,"endTransaction size =  "+mOverlayStack.size());
        }

    }

    private boolean isTwiceOverlayRectStackSame(){
        if(mOverlayStack.empty() && mOverlayCache.empty()){
            return true;
        }
        int cacheSize = mOverlayCache.size();
        int stackSize = mOverlayStack.size();
        if(cacheSize != stackSize){
            return false;
        }

        for (int i = 0; i < cacheSize; i++){
            OverlayRect overlayRectCache = mOverlayCache.get(i);
            OverlayRect overlayRectStack = mOverlayStack.get(i);
            if(!overlayRectCache.equals(overlayRectStack)){
                return false;
            }

        }
        return true;

    }

    /**
     * 启动图层功能
     * @param overlayColor
     */
    public void startOverlayRender(int overlayColor){
        handleOverlayRender(overlayColor);
    }

    private void handleOverlayRender(int overlayColor){

        mIsOverlayRender = true;

        Drawable d = mProgressDrawable;
        int availableWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        int availableHeight = getHeight() - getPaddingTop() - getPaddingBottom();
        final int left;
        final int top;
        final int right;
        final int bottom;

        if(d != null){
            int pH = mProgressHeight;
            int offset = (availableHeight - pH) / 2;
            top = getPaddingTop() + offset;
            bottom = getHeight() - getPaddingBottom() - offset;
        }else{
            int offset = (availableHeight - mOverlayHeight) / 2;
            top = getPaddingTop() + offset;
            bottom = getHeight() - getPaddingBottom() - offset;
        }

        left = getPaddingLeft() + (int)(availableWidth * getScale() + 0.5f);
        //初始化时，rect没有宽度
        right = left;

        Rect rect = mCurrentOverlay.getRect();
        mCurrentOverlay.setColor(overlayColor);


        rect.left = left;
        rect.right = right;
        rect.top = top;
        rect.bottom = bottom;

    }

    /**
     * 停止图层功能
     */
    public void stopOverlayRender() {

        mIsOverlayRender = false;
        OverlayRect overlayRect = (OverlayRect) mCurrentOverlay.clone();
        mOverlayStack.push(overlayRect);
        mCurrentOverlay.clear();

    }

    public void setAnchorColorFilter(int color){
        if(mAnchor != null){
            /**
             * 有透明度时才可以
             */
//            mAnchor.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);

            /**
             * 没有透明度时也可以
             */
            mAnchor.setColorFilter(color, PorterDuff.Mode.MULTIPLY);
            invalidate();
        }
    }

    public void setThumbColorFilter(int color){
        if(mThumb != null){
            /**
             * 有透明度时才可以
             */
//            mAnchor.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);

            /**
             * 没有透明度时也可以
             */
            mThumb.setColorFilter(color, PorterDuff.Mode.MULTIPLY);
            invalidate();
        }
    }

    public synchronized void deleteLastOverlay(){
        if(!mOverlayStack.empty()){
            OverlayRect overlayRect = mOverlayStack.pop();
            int nextPos = overlayRect.getRect().left;
            int progress = calculateProgressWithPosition(nextPos);
            setProgressInternal(progress,true,false);

            invalidate();
        }
    }

    public synchronized void clearAllOverlay(){
        if(!mOverlayStack.empty()){

            OverlayRect overlayRect = mOverlayStack.get(0);
            mOverlayStack.clear();
            int nextPos = overlayRect.getRect().left;
            int progress = calculateProgressWithPosition(nextPos);
            setProgressInternal(progress,true,false);

            invalidate();
        }
    }


    public interface OnSeekBarChangeListener {


        void onProgressChanged(AnchorSeekBar seekBar, int progress, boolean fromUser);


        void onStartTrackingTouch(AnchorSeekBar seekBar);

        void onStopTrackingTouch(AnchorSeekBar seekBar);
    }

    public void setOnSeekBarChangeListener(OnSeekBarChangeListener l) {
        mOnSeekBarChangeListener = l;
    }

    public interface OnAnchorBarChangeListener {


        void onStartAnchorTouch(AnchorSeekBar seekBar, int position);

        void onStopAnchorTouch(AnchorSeekBar seekBar, int position);
    }



    public void setOnAnchorBarChangeListener(OnAnchorBarChangeListener l) {
        mOnAnchorBarChangeListener = l;
    }

    @Override
    public CharSequence getAccessibilityClassName() {
        return AnchorSeekBar.class.getName();
    }




    class OverlayRect implements Cloneable {

        Rect rect = new Rect();
        int color;

        public Rect getRect(){
            return rect;
        }

        public int getColor(){
            return color;
        }
        public void setColor(int c){
            color = c;
        }

        public void clear(){
            rect.bottom = 0;
            rect.top = 0;
            rect.right = 0;
            rect.left = 0;

        }

        @Override
        public OverlayRect clone(){
            OverlayRect overlayRect = null;
            try {
                overlayRect  = (OverlayRect)super.clone();
            }catch (Exception e){
                e.printStackTrace();
            }
            overlayRect.color = this.color;
            overlayRect.rect = new Rect(this.getRect());
            return overlayRect;
        }

        @Override
        public String toString() {
            return " color = " + color + "  rect = " + rect.toString();
        }

        @Override
        public boolean equals(Object obj) {

            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }

            OverlayRect or = ((OverlayRect)obj);
            return this.color == or.color && this.rect.equals(or.rect);

        }
    }

}
