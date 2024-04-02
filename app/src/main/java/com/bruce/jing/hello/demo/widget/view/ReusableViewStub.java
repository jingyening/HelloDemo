/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bruce.jing.hello.demo.widget.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;

/**
 * A ViewStub is an invisible, zero-sized View that can be used to lazily inflate
 * layout resources at runtime.
 *
 * When a ViewStub is made visible, or when {@link #inflate()}  is invoked, the layout resource 
 * is inflated. The ViewStub then replaces itself in its parent with the inflated View or Views.
 * Therefore, the ViewStub exists in the view hierarchy until {@link #setVisibility(int)} or
 * {@link #inflate()} is invoked.
 *
 * The inflated View is added to the ViewStub's parent with the ViewStub's layout
 * parameters. Similarly, you can define/override the inflate View's id by using the
 * ViewStub's inflatedId property. For instance:
 *
 * <pre>
 *     &lt;ViewStub android:id="@+id/stub"
 *               android:inflatedId="@+id/subTree"
 *               android:layout="@layout/mySubTree"
 *               android:layout_width="120dip"
 *               android:layout_height="40dip" /&gt;
 * </pre>
 *
 * The ViewStub thus defined can be found using the id "stub." After inflation of
 * the layout resource "mySubTree," the ViewStub is removed from its parent. The
 * View created by inflating the layout resource "mySubTree" can be found using the
 * id "subTree," specified by the inflatedId property. The inflated View is finally
 * assigned a width of 120dip and a height of 40dip.
 *
 * The preferred way to perform the inflation of the layout resource is the following:
 *
 * <pre>
 *     ViewStub stub = findViewById(R.id.stub);
 *     View inflated = stub.inflate();
 * </pre>
 *
 * When {@link #inflate()} is invoked, the ViewStub is replaced by the inflated View
 * and the inflated View is returned. This lets applications get a reference to the
 * inflated View without executing an extra findViewById().
 *
 * @attr ref android.R.styleable#ViewStub_inflatedId
 * @attr ref android.R.styleable#ViewStub_layout
 */

/**
 * 一个可以重复inflate的ViewStub
 */
public final class ReusableViewStub extends View {

    private static final String TAG = "ReusableViewStub";

    private int mInflatedId = NO_ID;
    private int mLayoutResource;

    private View mInflatedView;

    private LayoutInflater mInflater;
    private OnInflateListener mInflateListener;

    public ReusableViewStub(Context context) {
        this(context, 0);
    }

    /**
     * Creates a new ViewStub with the specified layout resource.
     *
     * @param context The application's environment.
     * @param layoutResource The reference to a layout resource that will be inflated.
     */
    public ReusableViewStub(Context context, @LayoutRes int layoutResource) {
        this(context, null);
        mLayoutResource = layoutResource;
    }

    public ReusableViewStub(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        Log.d(TAG,"ReusableViewStub2222 id = "+getId());
    }

    public ReusableViewStub(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Log.d(TAG,"ReusableViewStub333 id = "+getId());
        setVisibility(GONE);
        setWillNotDraw(true);
    }


    /**
     * Returns the id taken by the inflated view. If the inflated id is
     * {@link View#NO_ID}, the inflated view keeps its original id.
     *
     * @return A positive integer used to identify the inflated view or
     *         {@link #NO_ID} if the inflated view should keep its id.
     *
     * @see #setInflatedId(int)
     * @attr ref android.R.styleable#ViewStub_inflatedId
     */
    @IdRes
    public int getInflatedId() {
        return mInflatedId;
    }

    /**
     * Defines the id taken by the inflated view. If the inflated id is
     * {@link View#NO_ID}, the inflated view keeps its original id.
     *
     * @param inflatedId A positive integer used to identify the inflated view or
     *                   {@link #NO_ID} if the inflated view should keep its id.
     *
     * @see #getInflatedId()
     * @attr ref android.R.styleable#ViewStub_inflatedId
     */
    public void setInflatedId(@IdRes int inflatedId) {
        mInflatedId = inflatedId;
    }

    /** @hide **/
    public Runnable setInflatedIdAsync(@IdRes int inflatedId) {
        mInflatedId = inflatedId;
        return null;
    }

    /**
     * Returns the layout resource that will be used by {@link #setVisibility(int)} or
     * {@link #inflate()} to replace this StubbedView
     * in its parent by another view.
     *
     * @return The layout resource identifier used to inflate the new View.
     *
     * @see #setLayoutResource(int)
     * @see #setVisibility(int)
     * @see #inflate()
     * @attr ref android.R.styleable#ViewStub_layout
     */
    @LayoutRes
    public int getLayoutResource() {
        return mLayoutResource;
    }

    /**
     * Specifies the layout resource to inflate when this StubbedView becomes visible or invisible
     * or when {@link #inflate()} is invoked. The View created by inflating the layout resource is
     * used to replace this StubbedView in its parent.
     * 
     * @param layoutResource A valid layout resource identifier (different from 0.)
     * 
     * @see #getLayoutResource()
     * @see #setVisibility(int)
     * @see #inflate()
     * @attr ref android.R.styleable#ViewStub_layout
     */
    public void setLayoutResource(@LayoutRes int layoutResource) {
        mLayoutResource = layoutResource;
    }

    /** @hide **/
    public Runnable setLayoutResourceAsync(@LayoutRes int layoutResource) {
        mLayoutResource = layoutResource;
        return null;
    }

    /**
     * Set {@link LayoutInflater} to use in {@link #inflate()}, or {@code null}
     * to use the default.
     */
    public void setLayoutInflater(LayoutInflater inflater) {
        mInflater = inflater;
    }

    /**
     * Get current {@link LayoutInflater} used in {@link #inflate()}.
     */
    public LayoutInflater getLayoutInflater() {
        return mInflater;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(0, 0);
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void draw(Canvas canvas) {

    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
    }

    /**
     * When visibility is set to {@link #VISIBLE} or {@link #INVISIBLE},
     * {@link #inflate()} is invoked and this StubbedView is replaced in its parent
     * by the inflated layout resource. After that calls to this function are passed
     * through to the inflated view.
     *
     * @param visibility One of {@link #VISIBLE}, {@link #INVISIBLE}, or {@link #GONE}.
     *
     * @see #inflate() 
     */
    @Override
    public void setVisibility(int visibility) {
        if (mInflatedView != null) {
            mInflatedView.setVisibility(visibility);
        } else {
            super.setVisibility(visibility);
            if (visibility == VISIBLE || visibility == INVISIBLE) {
                inflate();
            }
        }
    }

    /** @hide **/
//    public Runnable setVisibilityAsync(int visibility) {
//        if (visibility == VISIBLE || visibility == INVISIBLE) {
//            ViewGroup parent = (ViewGroup) getParent();
//            return new ViewReplaceRunnable(inflateViewNoAdd(parent));
//        } else {
//            return null;
//        }
//    }

    private View inflateViewNoAdd(ViewGroup parent) {
        final LayoutInflater factory;
        if (mInflater != null) {
            factory = mInflater;
        } else {
            factory = LayoutInflater.from(getContext());
        }
        final View view = factory.inflate(mLayoutResource, parent, false);

        if (mInflatedId != NO_ID) {
            view.setId(mInflatedId);
        }
        return view;
    }

    /**
     * 替换ViewStub本身
     * @param view
     * @param parent
     */
    private void replaceSelfWithView(View view, ViewGroup parent) {
        final int index = parent.indexOfChild(this);
        final ViewGroup.LayoutParams layoutParams = getLayoutParams();
        if (layoutParams != null) {
            parent.addView(view, index, layoutParams);
        } else {
            parent.addView(view, index);
        }
        parent.removeViewInLayout(this);
    }

    /**
     * 替换之前的view
     * @param view
     * @param parent
     */
    private void replacePreWithView(View view, ViewGroup parent){
        final int index = parent.indexOfChild(mInflatedView);
        final ViewGroup.LayoutParams layoutParams = mInflatedView.getLayoutParams();
        if (layoutParams != null) {
            parent.addView(view, index, layoutParams);
        } else {
            parent.addView(view, index);
        }
        parent.removeViewInLayout(mInflatedView);
    }

    /**
     * Inflates the layout resource identified by {@link #getLayoutResource()}
     * and replaces this StubbedView in its parent by the inflated layout resource.
     *
     * @return The inflated layout resource.
     *
     */
    public View inflate() {
        ViewParent viewParent = getParent();
        if (viewParent != null && viewParent instanceof ViewGroup) {
            return inflate((View) viewParent,true);
        } else {
            if (mInflatedView != null) {
                viewParent = mInflatedView.getParent();
                if(viewParent != null && viewParent instanceof ViewGroup){
                    return inflate((View) viewParent,false);
                }else{
                    throw new IllegalStateException("inflateView must have a non-null ViewGroup viewParent");
                }
            } else {
                throw new IllegalStateException("ViewStub must have a non-null ViewGroup viewParent");
            }
        }
    }

    private View inflate(View viewParent,boolean replaceSelf) {
        if (viewParent != null && viewParent instanceof ViewGroup) {
            if (mLayoutResource != 0) {
                final ViewGroup parent = (ViewGroup) viewParent;
                final View view = inflateViewNoAdd(parent);
                if (replaceSelf) {
                    replaceSelfWithView(view, parent);
                } else {
                    replacePreWithView(view, parent);
                }

                mInflatedView = view;
                if (mInflateListener != null) {
                    mInflateListener.onInflate(this, view);
                }
                return view;
            } else {
                throw new IllegalArgumentException("ViewStub must have a valid layoutResource");
            }
        } else {
            throw new IllegalStateException("ViewStub or inflateView must have a non-null ViewGroup viewParent");
        }
    }

    /**
     * ViewStub 复位
     */
    public void reset() {
        ViewParent parent = getParent();
        if (parent != null && parent instanceof ViewGroup) {
            mInflatedId = NO_ID;
            mLayoutResource = 0;
            removeInflateView();
        } else {
            if (mInflatedView != null) {
                ViewParent parent1 = mInflatedView.getParent();
                if (parent1 != null && parent1 instanceof ViewGroup) {
                    replacePreWithView(this, (ViewGroup) parent1);
                }
                mInflatedView = null;
            }
        }
    }


    private void removeInflateView() {
        if (mInflatedView != null) {
            ViewParent parent = mInflatedView.getParent();
            if (parent != null && parent instanceof ViewGroup) {
                ((ViewGroup) parent).removeView(mInflatedView);
            }
            mInflatedView = null;
        }
    }



    /**
     * Specifies the inflate listener to be notified after this ViewStub successfully
     * inflated its layout resource.
     *
     * @param inflateListener The OnInflateListener to notify of successful inflation.
     *
     * @see ReusableViewStub.OnInflateListener
     */
    public void setOnInflateListener(OnInflateListener inflateListener) {
        mInflateListener = inflateListener;
    }

    /**
     * Listener used to receive a notification after a ViewStub has successfully
     * inflated its layout resource.
     *
     * @see ReusableViewStub#setOnInflateListener(ReusableViewStub.OnInflateListener)
     */
    public static interface OnInflateListener {
        /**
         * Invoked after a ViewStub successfully inflated its layout resource.
         * This method is invoked after the inflated view was added to the
         * hierarchy but before the layout pass.
         *
         * @param stub The ViewStub that initiated the inflation.
         * @param inflated The inflated View.
         */
        void onInflate(ReusableViewStub stub, View inflated);
    }

    /** @hide **/
//    public class ViewReplaceRunnable implements Runnable {
//        public final View view;
//
//        ViewReplaceRunnable(View view) {
//            this.view = view;
//        }
//
//        @Override
//        public void run() {
//            replaceSelfWithView(view, (ViewGroup) getParent());
//        }
//    }
}
