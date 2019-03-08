package com.bruce.jing.hello.demo.widget.emoji.widget;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bruce.jing.hello.demo.R;
import com.bruce.jing.hello.demo.util.CommonUtil;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("NewApi")
public class EmojiIconIndicatorView extends LinearLayout {

    private Context context;
    private Bitmap selectedBitmap;
    private Bitmap unselectedBitmap;

    private List<ImageView> dotViews;

    private int dotHeight = 12;
    private int dotWidth = 4;

    public EmojiIconIndicatorView(Context context, AttributeSet attrs, int defStyle) {
        this(context, null);
    }

    public EmojiIconIndicatorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public EmojiIconIndicatorView(Context context) {
        this(context,null);
    }

    private void init(Context context, AttributeSet attrs){
        this.context = context;
        dotHeight = CommonUtil.dip2px(context, dotHeight);
        dotWidth = CommonUtil.dip2px(context, dotWidth);
        selectedBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.emoji_page_select);
        unselectedBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.emoji_page_unselect);
        setGravity(Gravity.CENTER_HORIZONTAL);
    }

    public void init(int count){
        dotViews = new ArrayList<ImageView>();
        for(int i = 0 ; i < count ; i++){
            ImageView imageView = generateAndAddDotView();

            if (i == 0) {
                imageView.setImageBitmap(selectedBitmap);
            } else {
                imageView.setImageBitmap(unselectedBitmap);
            }
        }
    }

    @NonNull
    private ImageView generateAndAddDotView() {
        RelativeLayout rl = new RelativeLayout(context);
        LayoutParams params = new LayoutParams(dotWidth,dotHeight);
        params.leftMargin = CommonUtil.dip2px(context,8);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        ImageView imageView = new ImageView(context);
        rl.addView(imageView, layoutParams);
        this.addView(rl, params);
        dotViews.add(imageView);
        return imageView;
    }

    public void updateIndicator(int count) {
        if(dotViews == null){
            return;
        }
        for(int i = 0 ; i < dotViews.size() ; i++){
            if(i >= count){
                dotViews.get(i).setVisibility(GONE);
                ((View)dotViews.get(i).getParent()).setVisibility(GONE);
            }
            else {
                dotViews.get(i).setVisibility(VISIBLE);
                ((View)dotViews.get(i).getParent()).setVisibility(VISIBLE);
            }
        }
        if(count > dotViews.size()){
            int diff = count - dotViews.size();
            for(int i = 0 ; i < diff ; i++){
                generateAndAddDotView();
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if(selectedBitmap != null){
            selectedBitmap.recycle();
        }
        if(unselectedBitmap != null){
            unselectedBitmap.recycle();
        }
    }

    public void selectTo(int position){
        for(ImageView iv : dotViews){
            iv.setImageBitmap(unselectedBitmap);
        }
        dotViews.get(position).setImageBitmap(selectedBitmap);
    }


    public void selectTo(int startPosition, int targetPostion){
        ImageView startView = dotViews.get(startPosition);
        ImageView targetView = dotViews.get(targetPostion);
        startView.setImageBitmap(unselectedBitmap);
        targetView.setImageBitmap(selectedBitmap);
    }

}