package com.bruce.jing.hello.demo.widget.tmp;

import android.content.ClipData;
import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

/**
 * @author bruce jing
 * @date 2019/12/9
 */
public class SpaceItemDecoration extends RecyclerView.ItemDecoration {

    private static final String TAG = "SpaceItemDecoration";

    private ItemDecorationPolicy mItemDecorationPolicy = new DefaultPolicy();

    private int leftSpace;
    private int rightSpace;
    private int topSpace;
    private int bottomSpace;

    public SpaceItemDecoration(int space) {
        this.leftSpace = space;
        this.rightSpace = space;
        this.topSpace = space;
        this.bottomSpace = space;
    }


    public SpaceItemDecoration(int space, ItemDecorationPolicy policy) {
        this.leftSpace = space;
        this.rightSpace = space;
        this.topSpace = space;
        this.bottomSpace = space;
        if (policy != null) {
            mItemDecorationPolicy = policy;
        }
    }

    public SpaceItemDecoration(int leftSpace, int topSpace, int rightSpace, int bottomSpace, ItemDecorationPolicy policy) {
        this.leftSpace = leftSpace;
        this.rightSpace = rightSpace;
        this.topSpace = topSpace;
        this.bottomSpace = bottomSpace;
        if (policy != null) {
            mItemDecorationPolicy = policy;
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        mItemDecorationPolicy.getItemOffsets(this, outRect, view, parent, state);
    }

    public int getLeftSpace() {
        return leftSpace;
    }

    public int getRightSpace() {
        return rightSpace;
    }

    public int getTopSpace() {
        return topSpace;
    }

    public int getBottomSpace() {
        return bottomSpace;
    }

    public interface ItemDecorationPolicy {
        void getItemOffsets(SpaceItemDecoration decoration, Rect outRect, View view, RecyclerView parent, RecyclerView.State state);
    }

    public static class DefaultPolicy implements ItemDecorationPolicy {

        @Override
        public void getItemOffsets(SpaceItemDecoration decoration, Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.left = decoration.getLeftSpace();
            outRect.right = decoration.getRightSpace();
            outRect.top = decoration.getTopSpace();
            outRect.bottom = decoration.getBottomSpace();
        }
    }

    public static class LinearHorizontalLayoutPolicy implements ItemDecorationPolicy {

        @Override
        public void getItemOffsets(SpaceItemDecoration decoration, Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            if (position == 0) {
                outRect.left = 0;
                outRect.right = decoration.getRightSpace();
                outRect.top = decoration.getTopSpace();
                outRect.bottom = decoration.getBottomSpace();
            } else if (position == parent.getLayoutManager().getItemCount() - 1) {
                outRect.left = decoration.getLeftSpace();
                outRect.right = 0;
                outRect.top = decoration.getTopSpace();
                outRect.bottom = decoration.getBottomSpace();
            } else {
                outRect.left = decoration.getLeftSpace();
                outRect.right = decoration.getRightSpace();
                outRect.top = decoration.getTopSpace();
                outRect.bottom = decoration.getBottomSpace();
            }
        }
    }

    public static class GridVerticalLayoutPolicy implements ItemDecorationPolicy {

        @Override
        public void getItemOffsets(SpaceItemDecoration decoration, Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            Log.d(TAG,"getItemOffsets position = "+position);
            RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
            if (layoutManager instanceof GridLayoutManager) {
                int spanCount = ((GridLayoutManager) layoutManager).getSpanCount();
                if (spanCount > 0) {
                    int rowCount = layoutManager.getItemCount() / spanCount + 1;
                    int indexAtHorizontal = position % spanCount;
                    int indexAtVertical = position / spanCount;

                    if (indexAtVertical == 0) {//第一行
                        outRect.top = 0;
                        if (indexAtHorizontal == 0) {
                            outRect.left = 0;
                            outRect.right = decoration.getRightSpace();
                            outRect.bottom = decoration.getBottomSpace();
                        } /*else if (indexAtHorizontal == spanCount - 1) {
                            outRect.left = decoration.getLeftSpace();
                            outRect.right = 0;
                            outRect.bottom = decoration.getBottomSpace();
                        }*/ else {
                            outRect.left = decoration.getLeftSpace();
                            outRect.right = decoration.getRightSpace();
                            outRect.bottom = decoration.getBottomSpace();
                        }
                    } else if (indexAtVertical == rowCount - 1) {//最后一行
                        outRect.bottom = 0;
                        if (indexAtHorizontal == 0) {
                            outRect.left = 0;
                            outRect.right = decoration.getRightSpace();
                            outRect.top = decoration.getTopSpace();

                        } /*else if (indexAtHorizontal == spanCount - 1) {
                            outRect.left = decoration.getLeftSpace();
                            outRect.right = 0;
                            outRect.top = decoration.getTopSpace();
                        }*/ else {
                            outRect.left = decoration.getLeftSpace();
                            outRect.right = decoration.getRightSpace();
                            outRect.top = decoration.getTopSpace();
                        }
                    } else {
                        if (indexAtHorizontal == 0) {
                            outRect.left = 0;
                            outRect.right = decoration.getRightSpace();
                            outRect.top = decoration.getTopSpace();
                            outRect.bottom = decoration.getBottomSpace();
                        } /*else if (indexAtHorizontal == spanCount - 1) {
                            outRect.left = decoration.getLeftSpace();
                            outRect.right = 0;
                            outRect.top = decoration.getTopSpace();
                            outRect.bottom = decoration.getBottomSpace();
                        } */else {
                            outRect.left = decoration.getLeftSpace();
                            outRect.right = decoration.getRightSpace();
                            outRect.top = decoration.getTopSpace();
                            outRect.bottom = decoration.getBottomSpace();
                        }
                    }

                    return;
                }


            }
            outRect.left = decoration.getLeftSpace();
            outRect.right = decoration.getRightSpace();
            outRect.top = decoration.getTopSpace();
            outRect.bottom = decoration.getBottomSpace();
        }
    }

}
