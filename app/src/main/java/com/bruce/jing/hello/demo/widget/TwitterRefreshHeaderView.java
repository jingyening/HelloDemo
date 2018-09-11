package com.bruce.jing.hello.demo.widget;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bruce.jing.hello.demo.R;
import com.bruce.jing.hello.demo.widget.opensource.swipeload.SwipeRefreshHeaderLayout;


/**
 * Created by Aspsine on 2015/9/9.
 */
public class TwitterRefreshHeaderView extends SwipeRefreshHeaderLayout {

    private ImageView ivArrow;

    private ImageView ivRefresh;

    private TextView tvRefresh;

    private int mHeaderHeight;
    private AnimationDrawable animationDrawable;
    private boolean rotated = false;

    public TwitterRefreshHeaderView(Context context) {
        this(context, null);
    }

    public TwitterRefreshHeaderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        mHeaderHeight = getResources().getDimensionPixelOffset(R.dimen.refresh_header_height_twitter);
    }

    public TwitterRefreshHeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mHeaderHeight = getResources().getDimensionPixelOffset(R.dimen.refresh_header_height_twitter);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        tvRefresh = (TextView) findViewById(R.id.tvRefresh);
        ivArrow = (ImageView) findViewById(R.id.ivArrow);
        ivRefresh = (ImageView) findViewById(R.id.ivRefresh);
        animationDrawable = (AnimationDrawable) ivRefresh.getBackground();
    }

    @Override
    public void onRefresh() {
        ivArrow.setVisibility(GONE);
        ivRefresh.setVisibility(VISIBLE);
        animationDrawable.start();
        tvRefresh.setText(R.string.refreshing);
    }

    @Override
    public void onPrepare() {
        Log.d("TwitterRefreshHeader", "onPrepare()");
    }

    @Override
    public void onMove(int y, boolean isComplete, boolean automatic) {
        if (!isComplete) {
            ivArrow.setVisibility(VISIBLE);
            animationDrawable.stop();
            ivRefresh.setVisibility(GONE);
            if (y > mHeaderHeight) {
                tvRefresh.setText(R.string.release_refresh);
                if (!rotated) {
                    rotated = true;
                }
            } else if (y < mHeaderHeight) {
                if (rotated) {
                    rotated = false;
                }

                tvRefresh.setText(R.string.pull_to_refresh);
            }
        }
    }

    @Override
    public void onRelease() {
        Log.d("TwitterRefreshHeader", "onRelease()");
    }

    @Override
    public void onComplete() {
        rotated = false;
        animationDrawable.stop();
        ivArrow.setVisibility(VISIBLE);
        ivRefresh.setVisibility(GONE);
        tvRefresh.setText(R.string.refresh_complete);
    }

    @Override
    public void onReset() {
        rotated = false;
        animationDrawable.stop();
        ivArrow.setVisibility(GONE);
        ivRefresh.setVisibility(GONE);
    }

    @Override
    public int refreshHeaderHeight() {
        return mHeaderHeight;
    }
}
