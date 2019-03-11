package com.bruce.jing.hello.demo.widget.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bruce.jing.hello.demo.R;
import com.bruce.jing.hello.demo.util.CommonUtil;

import java.util.List;

import static android.view.Gravity.CENTER;

/**
 * -------------------------------------
 * 作者：王文婷@<vitta.wang@uxin.com>
 * -------------------------------------
 * 时间：2019/2/20 下午12:10
 * -------------------------------------
 * 描述：在线头像列表
 * -------------------------------------
 * 备注：
 * -------------------------------------
 */
public class OnlineAvatarView extends ViewGroup {

    /**
     * 压住的宽度
     */
    private int mPressedWidth;
    /**
     * 数据集
     */
    private List<Object> mList;

    /**
     * 每条数据的长度
     */
    private int mItemLength;

    public OnlineAvatarView(Context context) {
        this(context, null);
    }

    public OnlineAvatarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OnlineAvatarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mItemLength =  CommonUtil.dip2px(context, 32);
        mPressedWidth = CommonUtil.dip2px(context, 6);
    }

    /**
     * 设置数据（在线列表用）
     *
     * @param list 头像列表
     */
    public void setData(List<Object> list) {
        if (list == null || list.isEmpty()) {
            setVisibility(GONE);
            return;
        }
        setVisibility(VISIBLE);
        mList = list;
        post(new Runnable() {
            @Override
            public void run() {
                addViewByData(getMeasuredWidth());
            }
        });
    }



    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
//        addViewByData(w);

    }

    private void addViewByData(int w) {
        //计算最多可以显示下几条数据
        int maxDisplayCount = (w - mItemLength) / (mItemLength - mPressedWidth) + 1;
        int size = mList.size();
        removeAllViews();
            addView(createLastView());
//        if (size > maxDisplayCount) {
//            //超出最大显示个数 在最后添加一个view
//            int endShowIndex = maxDisplayCount - 1;
//            for (int i = 0; i < endShowIndex; i++) {
//                addView(createNormalView(mList.get(i)));
//            }
//            //11条数据，最多显示8条/那么showArray就是7位，hideArray就是4位/最后一条是空置位
//            //showArray 0,1,2,3,4,5,6
//            //hideArray 7,8,9,10
//        } else {
//            //没超出最大显示个数 正常显示
//            for (int i = 0; i < size; i++) {
//                addView(createNormalView(mList.get(i)));
//            }
//        }
//        requestLayout();
    }

    private View createNormalView(Object data) {
        ImageView imageView = new ImageView(getContext());
        imageView.setBackgroundResource(R.drawable.avatar);
        imageView.setLayoutParams(new LayoutParams(mItemLength, mItemLength));
        return imageView;
    }

    private View createLastView() {
        TextView textView = new TextView(getContext());
        textView.setLayoutParams(new LayoutParams(mItemLength, mItemLength));
        textView.setBackgroundResource(R.drawable.circle_white);
        textView.setText("9");
        return textView;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        /**
         * 不计算子view高度，measureheight为0，textview内容不能垂直居中。
         */
        measureChildren(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int measuredWidth = getMeasuredWidth();
        int measuredHeight = getMeasuredHeight();
        if (measuredWidth <= 0 || measuredHeight <= 0) {
            return;
        }
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            int left = i * (mItemLength - mPressedWidth);
            int right = left + mItemLength;
            int top = 0;
            int bottom = mItemLength;
            getChildAt(i).layout(left, top, right, bottom);
            final View view = getChildAt(i);
            if(view instanceof TextView) {
                view.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ((TextView) view).setGravity(CENTER);
                        int extendedTop = ((TextView) view).getExtendedPaddingTop();
                        System.out.println("JJJextendedTop = " + extendedTop);
                        int totalTop = ((TextView) view).getTotalPaddingTop();
                        System.out.println("JJJtotalTop = " + totalTop);
                        int paddingLeft = ((TextView) view).getTotalPaddingLeft();
                        System.out.println("JJJpaddingLeft = " + paddingLeft);
                        int paddingBottom = ((TextView) view).getTotalPaddingBottom();
                        System.out.println("JJJpaddingBottom = " + paddingBottom);

                        int gravity = ((TextView) view).getGravity();
                        boolean isVerticalCenter = (gravity & Gravity.VERTICAL_GRAVITY_MASK) == Gravity.CENTER_VERTICAL;
                        System.out.println("JJJisVerticalCenter = " + isVerticalCenter);
                        boolean isHorizontalCenter = (gravity & Gravity.HORIZONTAL_GRAVITY_MASK) == Gravity.CENTER_HORIZONTAL;
                        System.out.println("JJJisHorizontalCenter = " + isHorizontalCenter);

                    }
                }, 500);
            }
        }
    }

}
