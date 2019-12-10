package com.bruce.jing.hello.demo.widget.tmp;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * @author bruce jing
 * @date 2019/12/9
 */
public class SpaceItemDecoration extends RecyclerView.ItemDecoration {

    private int leftSpace;
    private int rightSpace;
    private int topSpace;
    private int bottomSpace;

    public SpaceItemDecoration(int space){
        this.leftSpace = space;
        this.rightSpace = space;
        this.topSpace = space;
        this.bottomSpace = space;
    }

    public SpaceItemDecoration(int leftSpace, int topSpace, int rightSpace, int bottomSpace){
        this.leftSpace = leftSpace;
        this.rightSpace = rightSpace;
        this.topSpace = topSpace;
        this.bottomSpace =bottomSpace;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view); // item position
        if (position == 0){
            outRect.left = 0;
            outRect.right = rightSpace;
            outRect.top = topSpace;
            outRect.bottom = bottomSpace;
        }else if(position == parent.getLayoutManager().getItemCount() -1){
            outRect.left = leftSpace;
            outRect.right = 0;
            outRect.top = topSpace;
            outRect.bottom = bottomSpace;
        }else{
            outRect.left = leftSpace;
            outRect.right = rightSpace;
            outRect.top = topSpace;
            outRect.bottom = bottomSpace;
        }

    }


}
