package com.bruce.jing.hello.demo.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;

import androidx.viewpager.widget.ViewPager;

/**
 * @author bruce jing
 * @date 2020/5/15
 */
public class FlipPageLayout extends ViewGroup {

  private static final boolean DEBUG = true;

  private static final String TAG = "FlipPageLayout";

  private static final int MAX_SETTLE_DURATION = 600; // ms
  private static final int MIN_DISTANCE_FOR_FLING = 25; // dips

  private static final int DEFAULT_GUTTER_SIZE = 16; // dips

  private static final int MIN_FLING_VELOCITY = 400; // dips

  private static final int VERTICAL = 1;
  private static final int HORIZONTAL = 2;
  private int mOritation = VERTICAL;

  private int mChildWidthMeasureSpec;
  private int mChildHeightMeasureSpec;

  private boolean mIsBeingDragged;
  private int mTouchSlop;
  /**
   * Position of the last motion event.
   */
  private float mLastMotionX;
  private float mLastMotionY;
  private float mInitialMotionX;
  private float mInitialMotionY;
  /**
   * ID of the active pointer. This is used to retain consistency during
   * drags/flings if multiple pointers are used.
   */
  private int mActivePointerId = INVALID_POINTER;
  /**
   * Sentinel value for no current active pointer.
   * Used by {@link #mActivePointerId}.
   */
  private static final int INVALID_POINTER = -1;

  public FlipPageLayout(Context context) {
    super(context);
  }

  public FlipPageLayout(Context context,
                        @androidx.annotation.Nullable AttributeSet attrs) {
    super(context, attrs);
  }

  public FlipPageLayout(Context context, @androidx.annotation.Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    // For simple implementation, our internal size is always 0.
    // We depend on the container to specify the layout size of
    // our view.  We can't really know what it is since we will be
    // adding and removing different arbitrary views and do not
    // want the layout to change as this happens.
    setMeasuredDimension(getDefaultSize(0, widthMeasureSpec),
        getDefaultSize(0, heightMeasureSpec));

    final int measuredWidth = getMeasuredWidth();
    final int maxGutterSize = measuredWidth / 10;
    //mGutterSize = Math.min(maxGutterSize, mDefaultGutterSize);

    // Children are just made to fill our space.
    int childWidthSize = measuredWidth - getPaddingLeft() - getPaddingRight();
    int childHeightSize = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();

    /*
     * Make sure all children have been properly measured. Decor views first.
     * Right now we cheat and make this less complicated by assuming decor
     * views won't intersect. We will pin to edges based on gravity.
     */
    int size = getChildCount();
    for (int i = 0; i < size; ++i) {
      final View child = getChildAt(i);
      if (child.getVisibility() != GONE) {
        final LayoutParams lp =  child.getLayoutParams();
        if (lp != null) {
          //final int hgrav = lp.gravity & Gravity.HORIZONTAL_GRAVITY_MASK;
          //final int vgrav = lp.gravity & Gravity.VERTICAL_GRAVITY_MASK;
          int widthMode = MeasureSpec.AT_MOST;
          int heightMode = MeasureSpec.AT_MOST;
          //boolean consumeVertical = vgrav == Gravity.TOP || vgrav == Gravity.BOTTOM;
          //boolean consumeHorizontal = hgrav == Gravity.LEFT || hgrav == Gravity.RIGHT;
          //
          //if (consumeVertical) {
          //  widthMode = MeasureSpec.EXACTLY;
          //} else if (consumeHorizontal) {
          //  heightMode = MeasureSpec.EXACTLY;
          //}

          int widthSize = childWidthSize;
          int heightSize = childHeightSize;
          if (lp.width != ViewPager.LayoutParams.WRAP_CONTENT) {
            widthMode = MeasureSpec.EXACTLY;
            if (lp.width != ViewPager.LayoutParams.MATCH_PARENT) {
              widthSize = lp.width;
            }
          }
          if (lp.height != ViewPager.LayoutParams.WRAP_CONTENT) {
            heightMode = MeasureSpec.EXACTLY;
            if (lp.height != ViewPager.LayoutParams.MATCH_PARENT) {
              heightSize = lp.height;
            }
          }
          final int widthSpec = MeasureSpec.makeMeasureSpec(widthSize, widthMode);
          final int heightSpec = MeasureSpec.makeMeasureSpec(heightSize, heightMode);
          child.measure(widthSpec, heightSpec);

          //if (consumeVertical) {
          //  childHeightSize -= child.getMeasuredHeight();
          //} else if (consumeHorizontal) {
          //  childWidthSize -= child.getMeasuredWidth();
          //}
        }
      }
    }

    mChildWidthMeasureSpec = MeasureSpec.makeMeasureSpec(childWidthSize, MeasureSpec.EXACTLY);
    mChildHeightMeasureSpec = MeasureSpec.makeMeasureSpec(childHeightSize, MeasureSpec.EXACTLY);

    // Page views next.
    //size = getChildCount();
    //for (int i = 0; i < size; ++i) {
    //  final View child = getChildAt(i);
    //  if (child.getVisibility() != GONE) {
    //    if (DEBUG) {
    //      Log.v(TAG, "Measuring #" + i + " " + child + ": " + mChildWidthMeasureSpec);
    //    }
    //
    //    final ViewPager.LayoutParams lp = (ViewPager.LayoutParams) child.getLayoutParams();
    //    if (lp == null || !lp.isDecor) {
    //      final int widthSpec = MeasureSpec.makeMeasureSpec(
    //          (int) (childWidthSize * lp.widthFactor), MeasureSpec.EXACTLY);
    //      child.measure(widthSpec, mChildHeightMeasureSpec);
    //    }
    //  }
    //}
  }


  @Override protected void onLayout(boolean changed, int l, int t, int r, int b) {
    final int count = getChildCount();
    int width = r - l;
    int height = b - t;
    int paddingLeft = getPaddingLeft();
    int paddingTop = getPaddingTop();
    int paddingRight = getPaddingRight();
    int paddingBottom = getPaddingBottom();
    final int scrollX = getScrollX();

    int decorCount = 0;

    // First pass - decor views. We need to do this in two passes so that
    // we have the proper offsets for non-decor views later.
    for (int i = 0; i < count; i++) {
      final View child = getChildAt(i);
      if (child.getVisibility() != GONE) {
        //final LayoutParams lp = child.getLayoutParams();
        int childLeft = paddingLeft;
        int childTop = paddingLeft;

        if (mOritation == HORIZONTAL) {
          paddingLeft += child.getMeasuredWidth();
          paddingRight += child.getMeasuredWidth();
        } else {
          paddingTop += child.getMeasuredHeight();
          paddingBottom += child.getMeasuredHeight();
        }
        child.layout(childLeft, childTop,
            childLeft + child.getMeasuredWidth(),
            childTop + child.getMeasuredHeight());

        //childLeft += scrollX;
        //child.layout(childLeft, childTop,
        //    childLeft + child.getMeasuredWidth(),
        //    childTop + child.getMeasuredHeight());
      }
    }
  }

/*  @Override
  public boolean onInterceptTouchEvent(MotionEvent ev) {
    *//*
     * This method JUST determines whether we want to intercept the motion.
     * If we return true, onMotionEvent will be called and we do the actual
     * scrolling there.
     *//*

    final int action = ev.getAction() & MotionEvent.ACTION_MASK;

    // Always take care of the touch gesture being complete.
    if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
      // Release the drag.
      if (DEBUG) Log.v(TAG, "Intercept done!");
      resetTouch();
      return false;
    }

    // Nothing more to do here if we have decided whether or not we
    // are dragging.
    if (action != MotionEvent.ACTION_DOWN) {
      if (mIsBeingDragged) {
        if (DEBUG) Log.v(TAG, "Intercept returning true!");
        return true;
      }
    }

    switch (action) {
      case MotionEvent.ACTION_MOVE: {
        *//*
         * mIsBeingDragged == false, otherwise the shortcut would have caught it. Check
         * whether the user has moved far enough from his original down touch.
         *//*

        *//*
         * Locally do absolute value. mLastMotionY is set to the y value
         * of the down event.
         *//*
        final int activePointerId = mActivePointerId;
        if (activePointerId == INVALID_POINTER) {
          // If we don't have a valid id, the touch down wasn't on content.
          break;
        }

        final int pointerIndex = ev.findPointerIndex(activePointerId);
        final float x = ev.getX(pointerIndex);
        final float dx = x - mLastMotionX;
        final float xDiff = Math.abs(dx);
        final float y = ev.getY(pointerIndex);
        final float yDiff = Math.abs(y - mInitialMotionY);
        if (DEBUG) Log.v(TAG, "Moved x to " + x + "," + y + " diff=" + xDiff + "," + yDiff);

        if (dx != 0 && !isGutterDrag(mLastMotionX, dx)
            && canScroll(this, false, (int) dx, (int) x, (int) y)) {
          // Nested view has scrollable area under this point. Let it be handled there.
          mLastMotionX = x;
          mLastMotionY = y;
          mIsUnableToDrag = true;
          return false;
        }
        if (xDiff > mTouchSlop && xDiff * 0.5f > yDiff) {
          if (DEBUG) Log.v(TAG, "Starting drag!");
          mIsBeingDragged = true;
          requestParentDisallowInterceptTouchEvent(true);
          setScrollState(SCROLL_STATE_DRAGGING);
          mLastMotionX = dx > 0
              ? mInitialMotionX + mTouchSlop : mInitialMotionX - mTouchSlop;
          mLastMotionY = y;
          setScrollingCacheEnabled(true);
        } else if (yDiff > mTouchSlop) {
          // The finger has moved enough in the vertical
          // direction to be counted as a drag...  abort
          // any attempt to drag horizontally, to work correctly
          // with children that have scrolling containers.
          if (DEBUG) Log.v(TAG, "Starting unable to drag!");
          mIsUnableToDrag = true;
        }
        if (mIsBeingDragged) {
          // Scroll to follow the motion event
          if (performDrag(x)) {
            ViewCompat.postInvalidateOnAnimation(this);
          }
        }
        break;
      }

      case MotionEvent.ACTION_DOWN: {
        *//*
         * Remember location of down touch.
         * ACTION_DOWN always refers to pointer index 0.
         *//*
        mLastMotionX = mInitialMotionX = ev.getX();
        mLastMotionY = mInitialMotionY = ev.getY();
        mActivePointerId = ev.getPointerId(0);

        mIsScrollStarted = true;
        mScroller.computeScrollOffset();
        if (mScrollState == SCROLL_STATE_SETTLING
            && Math.abs(mScroller.getFinalX() - mScroller.getCurrX()) > mCloseEnough) {
          // Let the user 'catch' the pager as it animates.
          mScroller.abortAnimation();
          mPopulatePending = false;
          populate();
          mIsBeingDragged = true;
          requestParentDisallowInterceptTouchEvent(true);
          setScrollState(SCROLL_STATE_DRAGGING);
        } else {
          completeScroll(false);
          mIsBeingDragged = false;
        }

        if (DEBUG) {
          Log.v(TAG, "Down at " + mLastMotionX + "," + mLastMotionY
              + " mIsBeingDragged=" + mIsBeingDragged
              + "mIsUnableToDrag=" + mIsUnableToDrag);
        }
        break;
      }

      case MotionEvent.ACTION_POINTER_UP:
        onSecondaryPointerUp(ev);
        break;
    }

    if (mVelocityTracker == null) {
      mVelocityTracker = VelocityTracker.obtain();
    }
    mVelocityTracker.addMovement(ev);

    *//*
     * The only time we want to intercept motion events is if we are in the
     * drag mode.
     *//*
    return mIsBeingDragged;
  }*/

  protected boolean canScroll(View v, boolean checkV, int dx, int x, int y) {
    if (v instanceof ViewGroup) {
      final ViewGroup group = (ViewGroup) v;
      final int scrollX = v.getScrollX();
      final int scrollY = v.getScrollY();
      final int count = group.getChildCount();
      // Count backwards - let topmost views consume scroll distance first.
      for (int i = count - 1; i >= 0; i--) {
        // TODO: Add versioned support here for transformed views.
        // This will not work for transformed views in Honeycomb+
        final View child = group.getChildAt(i);
        if (x + scrollX >= child.getLeft() && x + scrollX < child.getRight()
            && y + scrollY >= child.getTop() && y + scrollY < child.getBottom()
            && canScroll(child, true, dx, x + scrollX - child.getLeft(),
            y + scrollY - child.getTop())) {
          return true;
        }
      }
    }

    return checkV && v.canScrollHorizontally(-dx);
  }

  @Override public boolean onTouchEvent(MotionEvent event) {
    return super.onTouchEvent(event);
  }

  private void resetTouch() {
    mActivePointerId = INVALID_POINTER;
  }



  private int getClientWidth() {
    return getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
  }

  private int getClientHeight() {
    return getMeasuredHeight() - getPaddingTop() - getPaddingBottom();
  }

}
