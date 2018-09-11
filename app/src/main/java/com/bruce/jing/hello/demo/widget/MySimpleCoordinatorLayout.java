package com.bruce.jing.hello.demo.widget;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.NestedScrollingParentHelper;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.Scroller;

import com.bruce.jing.hello.demo.R;
import com.bruce.jing.hello.demo.util.CommonUtil;
import com.bruce.jing.hello.demo.util.log.JLogUtil;
import com.bruce.jing.hello.demo.widget.opensource.swipeload.OnRefreshListener;
import com.bruce.jing.hello.demo.widget.opensource.swipeload.SwipeRefreshTrigger;
import com.bruce.jing.hello.demo.widget.opensource.swipeload.SwipeTrigger;


/**
 * ------------------------------------
 * 作者：bruce jing
 * ------------------------------------
 * 时间：2018/8/18 上午11:44
 * ------------------------------------
 * 描述：
 * ------------------------------------
 * 备注：
 * ------------------------------------
 *
 **/

public class MySimpleCoordinatorLayout extends FrameLayout implements NestedScrollingParent {

    private static final String LOG_TAG = "MySimpleCoordinatorLayout";

    private static final String PULL_REFRESH_VIEW_TAG = "PULL_REFRESH_VIEW_TAG";


    private static final int INVALID_POINTER = -1;



    private FrameLayout mPullRefreshContainer;
    private FrameLayout mHeadContainer;

    private FrameLayout mHoverContainer;

    private FrameLayout mMainContainer;

    private View mRefreshView;


    /**
     * true 是vertical
     * false 是horizontal
     */
    private boolean mIsLayoutVertical = true;

    /**
     * 处理触摸事件拦截处理需要的变量
     */
    private VelocityTracker mVelocityTracker;//用于计算滑动放手时的速度
    private int mActivePointerId = INVALID_POINTER;
    private int mLastMotionY = 0;
    private int mDownMotionY = 0;
    private int mDownMotionX = 0;
    private boolean mIsScroll = false;
    private int mPagingTouchSlop;

    //是否纵向滑动

    private static final int SCROLL_DIRECTION_NONE = 0;
    private static final int SCROLL_DIRECTION_HORIZENTAL = 1;
    private static final int SCROLL_DIRECTION_VERTICAL = 2;

    private int mScrollDirection = SCROLL_DIRECTION_NONE;

    private Scroller mScroller;
    private int mHeadHeight;
    private int mMaxScrollY;
    private int mMinScrollY;

    private Runnable mRunableAfterScrollFinish;



    private boolean mIsInTouchHandleRect = false;

    //联动滑动类
    private NestedScrollingParentHelper mParentHelper;


    public MySimpleCoordinatorLayout(@NonNull Context context) {
        this(context,null);
    }

    public MySimpleCoordinatorLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public MySimpleCoordinatorLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();

    }

    private void init() {
        mParentHelper = new NestedScrollingParentHelper(this);

        // TODO Auto-generated method stub
        mPagingTouchSlop = ViewConfiguration.get(getContext()).getScaledPagingTouchSlop();

        mScroller = new Scroller(getContext(), new DecelerateInterpolator());

    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        initView(getContext());

    }


    private void initView(Context context){
//        View rootView = LayoutInflater.from(context).inflate(R.layout.simple_coordinator_layout,this);
        mPullRefreshContainer = (FrameLayout) findViewById(R.id.pullRefreshContainer);
        mPullRefreshContainer.setTag(PULL_REFRESH_VIEW_TAG);

        mHeadContainer = (FrameLayout) findViewById(R.id.headContainer);

        mHoverContainer = (FrameLayout) findViewById(R.id.hoverContainer);

        mMainContainer = (FrameLayout) findViewById(R.id.mainContainer);

        mRefreshView = findViewById(R.id.pull_refresh_header);


    }


    //    public void setPullRefreshLayout(View pullRefreshLayout){
//        mPullRefreshContainer.removeAllViews();
//        mPullRefreshContainer.addView(pullRefreshLayout);
//
//    }
//
//
//    public void setPullRefreshContainer(View pullRefreshLayout, LayoutParams params){
//        if(params == null){
//            setPullRefreshLayout(pullRefreshLayout);
//        }else{
//            mPullRefreshContainer.removeAllViews();
//            mPullRefreshContainer.addView(pullRefreshLayout, params);
//            LayoutParams refreshContainerParams = (FrameLayout.LayoutParams)mPullRefreshContainer.getLayoutParams();
//            refreshContainerParams.height = params.height;
//            mPullRefreshContainer.setLayoutParams(refreshContainerParams);
//
//        }
//    }

    public void setHeadLayout(View headLayout){
        mHeadContainer.removeAllViews();
        mHeadContainer.addView(headLayout);
    }
    public void setHeadLayout(View headLayout, LayoutParams params){
        if(params == null){
            setHeadLayout(headLayout);
        }else{
            mHeadContainer.removeAllViews();
            mHeadContainer.addView(headLayout, params);
            /**
             * 解决onMeasure时，headcontainer 属性为wrap content属性时，
             * headcontainer子view高度超过了{@this}的高度，measure时得到的高度智能和{@this}一样
             */
            LayoutParams headContainerParams = (FrameLayout.LayoutParams) mHeadContainer.getLayoutParams();
            headContainerParams.height = params.height;
            mHeadContainer.setLayoutParams(headContainerParams);

        }
    }

    public void setMainLayout(View mainLayout){
        mMainContainer.removeAllViews();
        mMainContainer.addView(mainLayout);
    }

    public void setMainLayout(View mainLayout, LayoutParams params){
        if(params == null){
            setMainLayout(mainLayout);
        }else{
            mMainContainer.removeAllViews();
            mMainContainer.addView(mainLayout, params);
            LayoutParams mainContainerParams = (FrameLayout.LayoutParams)mMainContainer.getLayoutParams();
            mainContainerParams.height = params.height;
            mMainContainer.setLayoutParams(mainContainerParams);

        }

    }

    private boolean isInTouchHandleRect(int x, int y) {
        int scrollX = getScrollX();
        int scrollY = getScrollY();
        int left = mHeadContainer.getLeft() - scrollX;
        int top = mHeadContainer.getTop() - scrollY;

        int bottom = mHoverContainer.getBottom() - scrollY;
        int right = mHoverContainer.getRight() - scrollX;
        Rect localHeadRect = new Rect(left, top, right, bottom);
//        JLogUtil.d(LOG_TAG, "isInTouchHandleRect  localHeadRect = " + localHeadRect);
        return CommonUtil.isInRect(localHeadRect, x, y);
    }

    private boolean shouldInterceptTouchEvent() {
//        JLogUtil.d(LOG_TAG,"shouldInterceptTouchEvent mIsScroll = "+mIsScroll+ "  mIsInTouchHandleRect = "+mIsInTouchHandleRect+" mScrollDirection = "+mScrollDirection);
        return mScrollDirection == SCROLL_DIRECTION_VERTICAL && mIsInTouchHandleRect;
//        if(mIsScroll) {
//            return mIsInTouchHandleRect;
//        }
//        return false;
    }



    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {//不修改framelayout的事件分发逻辑
        return super.dispatchTouchEvent(ev);

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        JLogUtil.d(LOG_TAG, "onInterceptTouchEvent ");
        int motionX = (int) ev.getX();
        int motionY = (int) ev.getY();
        int deltaY = 0;
        switch(ev.getAction()) {
            case MotionEvent.ACTION_DOWN:

                mIsInTouchHandleRect = isInTouchHandleRect(motionX,motionY);
                if(!mScroller.isFinished()) {
                    mScroller.forceFinished(true);
                }
                initOrResetVelocityTracker();
                acquireVelocityTrackerAndAddMovement(ev);
                mActivePointerId = ev.getPointerId(0);
                mDownMotionX = motionX;
                mDownMotionY = motionY;
                mLastMotionY = motionY;
                mIsScroll = false;
                break;
            case MotionEvent.ACTION_MOVE:
                acquireVelocityTrackerAndAddMovement(ev);
                deltaY = motionY - mLastMotionY;
                mLastMotionY = motionY;

                int distanceX = Math.abs(motionX - mDownMotionX);
                int distanceY = Math.abs(motionY - mDownMotionY);

                if(distanceX > mPagingTouchSlop){
                    if(distanceX > distanceY) {
                        if (mScrollDirection == SCROLL_DIRECTION_NONE) {
                            mScrollDirection = SCROLL_DIRECTION_HORIZENTAL;
                        }
                    }
                }else if(distanceY > mPagingTouchSlop){
                    if(distanceY > distanceX ){
                        if (mScrollDirection == SCROLL_DIRECTION_NONE) {
                            mScrollDirection = SCROLL_DIRECTION_VERTICAL;
                            mIsScroll = true;
                        }
                    }
                }

                if(mScrollDirection != SCROLL_DIRECTION_NONE){
                    ViewParent parent = getParent();
                    if(parent != null){//判断方向后，不允许父view拦截此事件
                        parent.requestDisallowInterceptTouchEvent(true);
                    }
                }

                if(shouldInterceptTouchEvent()) {//滑动操作后，拦截触摸事件给自己处理
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:

                /**
                 * 如果事件没有被{@this}消费，抬起时，如果处于下拉状态，执行下拉刷新操作
                 */

                if(getScrollY() < 0){
                    handleActionUpFling(0);
                }
                mScrollDirection = SCROLL_DIRECTION_NONE;
                break;
            default:
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        super.onTouchEvent(ev);
        // TODO Auto-generated method stub

        int motionY = (int) ev.getY();
//        JLogUtil.d(LOG_TAG,"onTouchEvent motionY = "+motionY);
        switch(ev.getAction()) {
            case MotionEvent.ACTION_DOWN:

                break;
            case MotionEvent.ACTION_MOVE:
                onTouchActionMove(ev, motionY);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                onTouchActionUp(ev, motionY);
            default:
                break;
        }

        return true;
    }

    private void onTouchActionMove(MotionEvent ev, int motionY) {
        int deltaY;
        acquireVelocityTrackerAndAddMovement(ev);
        deltaY = motionY - mLastMotionY;
        mLastMotionY = motionY;
        if(Math.abs(motionY - mDownMotionY) > mPagingTouchSlop) {
            mIsScroll = true;
        }
//        if(shouldInterceptTouchEvent(deltaY)) {
            scrollYByWithPullRereshCondition(-deltaY);
//        }
    }

    private void onTouchActionUp(MotionEvent ev, int motionY) {
        acquireVelocityTrackerAndAddMovement(ev);
//        if(shouldInterceptTouchEvent()) {
            mVelocityTracker.computeCurrentVelocity(1000);
            int velocityY = (int) mVelocityTracker.getYVelocity(mActivePointerId);

            handleActionUpFling(-velocityY);

            mIsScroll = false;
            mIsInTouchHandleRect = false;
            recycleVelocityTracker();
//        } else {
//            mIsScroll = false;
//            mIsInTouchHandleRect = false;
//            recycleVelocityTracker();
//        }
        mScrollDirection = SCROLL_DIRECTION_NONE;
    }

    private void handleActionUpFling(int velocityY) {
        if(!mScroller.isFinished()) {
            mScroller.forceFinished(true);
//            JLogUtil.d(LOG_TAG, "handleActionUpFling forceFinish");
        }
        int currentScrollY = getScrollY();
//        JLogUtil.d(LOG_TAG, "handleActionUpFling currentScrollY = "+currentScrollY + "  mMinScrollY = "+mMinScrollY);
        if (currentScrollY < mMinScrollY) {
            mScroller.startScroll(getScrollX(),currentScrollY,0,mMinScrollY - currentScrollY,150);
            postInvalidate();
            mRunableAfterScrollFinish = new Runnable() {
                @Override
                public void run() {
                    mRefreshCallback.onRefresh();
                }
            };

        } else if (currentScrollY >= mMinScrollY && currentScrollY < 0) {
            mScroller.startScroll(getScrollX(),currentScrollY,0,-currentScrollY,150);
            postInvalidate();
        } else {
            if(velocityY <= 0){//向下滑动
                fling(velocityY, 0);
            }else{//向上滑动
                fling(velocityY, mHeadHeight);
            }

        }


    }


    private void fling(int velocityY,int limitY) {
        if(velocityY > 0){//上滑

            mScroller.fling(0, getScrollY(), 0, velocityY, 0, 0, getScrollY(), limitY);
        }else{//下滑
            mScroller.fling(0, getScrollY(), 0, velocityY, 0, 0, limitY, getScrollY());
        }
        postInvalidate();//调用fling后，需要调用重新绘制才能生效
    }



    private void scrollYByWithPullRereshCondition(int dy) {
        int currentScrollY = getScrollY();
//        JLogUtil.d(LOG_TAG, "scrollYByFocre  minScrollY = " + mMinScrollY+" maxScrollY = "+mMaxScrollY+"  currentScrollY = "+currentScrollY+" dy = "+dy);
        int nextScrollY = 0;
        if (currentScrollY < 0) {
            if (dy < 0) {//下拉刷新
                float ratio = getDampingRatio();
                int newDy = (int) Math.ceil(dy * ratio);
                nextScrollY = currentScrollY + newDy;
                //
                mRefreshCallback.onMove(Math.abs(nextScrollY),false,false);
            } else {//下拉刷新显示出来的状态下上滑
                nextScrollY = Math.min(mMaxScrollY, currentScrollY + dy);
                mRefreshCallback.onMove(Math.abs(nextScrollY),false,false);
            }

        } else {
            if (dy < 0) {
                if (mMinScrollY < 0) {//有下拉刷新，但是下拉刷新未显示时，下拉操作
                    nextScrollY = currentScrollY + dy;
                } else {//无下拉刷新，下拉操作
                    nextScrollY = Math.max(0, currentScrollY + dy);
                }
            } else {//上滑
                nextScrollY = Math.min(mMaxScrollY, currentScrollY + dy);
            }
        }
        scrollTo(getScrollX(), nextScrollY);

    }

    /**
     * 获取阻尼系数；
     * @return
     */
    private float getDampingRatio(){

        if(mMinScrollY >= 0){//没有下拉刷新
            return 1;
        }

        if(getScrollY() >= 0){//当前没有进入下拉状态
            return 1;
        }

        int distancePerRange = (int) (20 * CommonUtil.getDensity(getContext()));
        float ratio = Math.max(0,1 - (float) Math.abs(getScrollY()) / distancePerRange /10);//每移动distancePerRange，系数减少0.1

        return ratio;

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);


        /**
         * 因为{@this FrameLayout}高度受父布局限制，导致测量时mHeadContiner高度也受限制
         * 修改MeasureSpec的mode，使mHeadContiner的高度为真实高度
         */
        int mode = MeasureSpec.getMode(heightMeasureSpec);
        int size = MeasureSpec.getSize(heightMeasureSpec);
//        JLogUtil.d(LOG_TAG,"onMeasure mode = "+mode +"  size = "+size);
        int headHeightMeasureSpec = MeasureSpec.makeMeasureSpec(size, MeasureSpec.UNSPECIFIED);
        measureChildWithMargins(mHeadContainer, widthMeasureSpec, 0, headHeightMeasureSpec, 0);
        mHeadHeight = mHeadContainer.getMeasuredHeight();
//        JLogUtil.d(LOG_TAG, "onMeasure mHeadHeight =  "+mHeadHeight);

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        layoutChildren(left,top,right,bottom,false);
//        JLogUtil.d(LOG_TAG,"onLayout bottom = "+getChildAt(getChildCount()-1).getBottom()+"  height = "+getMeasuredHeight());
        mMaxScrollY = Math.max(0,getChildAt(getChildCount()-1).getBottom() - getMeasuredHeight());
        mMinScrollY = Math.min(0,mPullRefreshContainer.getTop());
    }

    /**
     *
     * @param left
     * @param top
     * @param right
     * @param bottom
     * @param forceLeftGravity
     */
    private void layoutChildren(int left, int top, int right, int bottom, boolean forceLeftGravity) {
        final int count = getChildCount();

        final int parentLeft = getPaddingLeft();
        final int parentRight = right - left - getPaddingRight();//bruce todo check logic

        final int parentTop = getPaddingTop();
        final int parentBottom = bottom - top - getPaddingBottom();//bruce todo

        int startOffset = 0;
        if (mIsLayoutVertical) {
            startOffset = parentTop;
        } else {
            startOffset = parentLeft;
        }


        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {


                final LayoutParams lp = (LayoutParams) child.getLayoutParams();

                final int width = child.getMeasuredWidth();
                final int height = child.getMeasuredHeight();

                int childLeft = 0;
                int childTop = 0;

                //layout 下拉刷新布局
                Object tag = child.getTag();
                if(tag != null && tag instanceof String && PULL_REFRESH_VIEW_TAG.equals(tag)){
                    childLeft = parentLeft + lp.leftMargin;
                    childTop = parentTop - height - lp.bottomMargin;
                    child.layout(childLeft,childTop,childLeft + width , childTop + height);
                    continue;
                }



                if (mIsLayoutVertical) {
                    startOffset += lp.topMargin;
                    childTop = startOffset;
                } else {
                    startOffset += lp.leftMargin;
                    childLeft = startOffset;
                }

//                JLogUtil.d(LOG_TAG,"onLayout  childTop = " + childTop + "  ,  height = "+height +"  ,  width = "+width);

                child.layout(childLeft, childTop, childLeft + width, childTop + height);
                if (mIsLayoutVertical) {
                    startOffset += height + lp.bottomMargin;

                } else {
                    startOffset += height + lp.rightMargin;
                }
            }
        }
    }


    private void initOrResetVelocityTracker() {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        } else {
            mVelocityTracker.clear();
        }
    }


    private void recycleVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    private void acquireVelocityTrackerAndAddMovement(MotionEvent ev) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(ev);
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if(mScroller.computeScrollOffset()){
            scrollTo(getScrollX(),mScroller.getCurrY());
            postInvalidate();
//            JLogUtil.d(LOG_TAG,"computeScroll getCurrY = "+mScroller.getCurrY());
        }else{
            if(mRunableAfterScrollFinish != null){
                mRunableAfterScrollFinish.run();
                mRunableAfterScrollFinish = null;
            }
        }
    }

    /******NestedScrollingParent  接口实现**************/

    //在此可以判断参数target是哪一个子view以及滚动的方向，然后决定是否要配合其进行嵌套滚动
    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        JLogUtil.d(LOG_TAG,"onStartNestedScroll child = "+child.getClass().getSimpleName()+"  target = "+target.getClass().getSimpleName());
        return true;
    }

    @Override
    public void onStopNestedScroll(View target) {
        mParentHelper.onStopNestedScroll(target);
        JLogUtil.d(LOG_TAG,"onStopNestedScroll  target = "+target.getClass().getSimpleName());
    }


    @Override
    public void onNestedScrollAccepted(View child, View target, int nestedScrollAxes) {
        mParentHelper.onNestedScrollAccepted(child, target, nestedScrollAxes);
        JLogUtil.d(LOG_TAG,"onNestedScrollAccepted child = "+child.getClass().getSimpleName()+"  target = "+target.getClass().getSimpleName());
    }



    //先于child滚动
    //前3个为输入参数，最后一个是输出参数
    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        if(mIsInTouchHandleRect /*|| mScrollDirection == SCROLL_DIRECTION_HORIZENTAL*/){//触摸区域是headrect，不处理联动
            return;
        }
        if (dy > 0) {//手势向上滑动
            commonScrollUp(target,dx,dy,consumed);
        } else if (dy < 0) {//手势向下滑动
            commonScrollDown(target,dx,dy,consumed);
        }
        JLogUtil.d(LOG_TAG,"onNestedPreScroll  target = "+target.getClass().getSimpleName());
    }

    //后于child滚动
    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
    }

    //返回值：是否消费了fling
    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        if(mIsInTouchHandleRect/*|| mScrollDirection == SCROLL_DIRECTION_HORIZENTAL*/){//触摸区域是headrect，不处理联动
            return false;
        }
        JLogUtil.d(LOG_TAG,"onNestedPreFling  target = "+target.getClass().getSimpleName()+"  velocityY = "+velocityY);
        if (target instanceof RecyclerView){
            //判断recyclerview 是否到达顶部
            boolean canRecyclerViewScrollDown = isCanRecyclerViewScrollDown((RecyclerView)target) ;
            if(canRecyclerViewScrollDown){
                return false;
            }else{
                if(velocityY < 0){//向下滑动
                    handleActionUpFling((int) velocityY);
                    return true;
                }else{//向上滑动
                    if(getScrollY() < mHeadHeight){
                        handleActionUpFling((int) velocityY);
                        return true;
                    }
                }
            }
        }

        return false;
    }

    //返回值：是否消费了fling
    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        JLogUtil.d(LOG_TAG,"onNestedFling  target = "+target.getClass().getSimpleName());
        return false;
    }

    @Override
    public int getNestedScrollAxes() {
        return mParentHelper.getNestedScrollAxes();
    }

    /******NestedScrollingParent  接口实现**************/

    private void commonScrollDown(View target, int dx, int dy, int[] consumed) {
        if (target instanceof RecyclerView) {
            boolean canRecyclerViewScrollDown = isCanRecyclerViewScrollDown((RecyclerView) target);
//            JLogUtil.d(LOG_TAG," commonScrollDown canRecyclerViewScrollDown = "+canRecyclerViewScrollDown);
            if (canRecyclerViewScrollDown) {
                return;
            }
        }
        scrollYByWithPullRereshCondition(dy);//滚动
        consumed[1] = dy;//告诉child我消费了多少

    }

    private boolean isCanRecyclerViewScrollDown(RecyclerView target) {
//        boolean canRecyclerViewScrollDown = target.canScrollVertically(-1);
        RecyclerView.LayoutManager lm = target.getLayoutManager();
        if(lm == null || lm.getChildCount() == 0){
            return false;
        }
        //查看第一个view的位置
        View view = lm.findViewByPosition(0);
        if (view != null) {
            int top = view.getTop();
//                int bottom = view.getBottom();
//                JLogUtil.d(LOG_TAG, "commonScrollDown  top = "+top +" bottom = "+bottom);
            return top < 0;
        } else {
            return true;
        }
    }

    private void commonScrollUp(View target, int dx, int dy, int[] consumed){
        if (getScrollY() < mMaxScrollY) {
            int surplusDistance = mMaxScrollY - getScrollY();
            if(surplusDistance < dy){
                scrollYByWithPullRereshCondition(surplusDistance);
                consumed[1] = surplusDistance;
            }else{
                scrollYByWithPullRereshCondition(dy);//滚动
                consumed[1] = dy;//告诉child我消费了多少
            }

        }
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
//        JLogUtil.d(LOG_TAG,"onScrollChanged  currentScrollY = "+getScrollY());
        if(mScrollChangeListener != null){
            mScrollChangeListener.onScrollChange(l,t,oldl,oldt);
        }
    }


    /**
     *
     */
    public void autoPullAndRefresh() {
        mScroller.startScroll(getScrollX(), getScrollY(), 0, mMinScrollY - getScrollY(), 150);
        postInvalidate();
        postDelayed(new Runnable() {
            @Override
            public void run() {
                mRefreshCallback.onRefresh();
            }
        }, 200);
    }

    public void onRereshComplete(){
        post(new Runnable() {
            @Override
            public void run() {
                if(mRefreshCallback != null){
                    mRefreshCallback.onComplete();
                }
            }
        });
    }

    public void onRefreshFail(){
        post(new Runnable() {
            @Override
            public void run() {
                if(mRefreshCallback != null){
                    mRefreshCallback.onComplete();
                }
            }
        });
    }

    private OnRefreshListener mRefreshListener;
    /**
     * set an {@link OnRefreshListener} to listening refresh event
     *
     * @param listener
     */
    public void setOnRefreshListener(OnRefreshListener listener) {
        this.mRefreshListener = listener;
    }


    private ScrollChangeListener mScrollChangeListener;
    public void setScrollChangeListen(ScrollChangeListener listener){
        mScrollChangeListener = listener;
    }

    private RefreshCallback mRefreshCallback = new RefreshCallback() {

        @Override
        public void onRefresh() {
            if(mRefreshView instanceof TwitterRefreshHeaderView){
                ((TwitterRefreshHeaderView) mRefreshView).onRefresh();
                if(mRefreshListener != null){
                    mRefreshListener.onRefresh();
                }
            }
        }

        @Override
        public int refreshHeaderHeight() {
            if(mRefreshView instanceof  TwitterRefreshHeaderView){
                ((TwitterRefreshHeaderView) mRefreshView).refreshHeaderHeight();
            }
            return 0;
        }

        @Override
        public void onPrepare() {
            if(mRefreshView instanceof  TwitterRefreshHeaderView){
                ((TwitterRefreshHeaderView) mRefreshView).onPrepare();
            }
        }

        @Override
        public void onMove(int y, boolean isComplete, boolean automatic) {
            if(mRefreshView instanceof TwitterRefreshHeaderView){
                ((TwitterRefreshHeaderView) mRefreshView).onMove(y,isComplete,automatic);
            }
        }

        @Override
        public void onRelease() {
            if(mRefreshView instanceof TwitterRefreshHeaderView){
                ((TwitterRefreshHeaderView) mRefreshView).onRelease();
            }
        }

        @Override
        public void onComplete() {
            MySimpleCoordinatorLayout.this.post(new Runnable() {
                @Override
                public void run() {
                    if (mRefreshView instanceof TwitterRefreshHeaderView) {
                        ((TwitterRefreshHeaderView) mRefreshView).onComplete();
                    }
                    if (!mScroller.isFinished()) {
                        mScroller.forceFinished(true);
                    }
                    mScroller.startScroll(getScrollX(), getScrollY(), 0, -getScrollY(), 150);
                }
            });

        }

        @Override
        public void onReset() {
            if(mRefreshView instanceof TwitterRefreshHeaderView){
                ((TwitterRefreshHeaderView) mRefreshView).onReset();
            }
        }
    };


    /**
     * refresh event callback
     */
    abstract class RefreshCallback implements SwipeTrigger, SwipeRefreshTrigger {

    }

    public interface ScrollChangeListener {
        void onScrollChange(int left, int top, int oldl, int oldt);
    }



}
