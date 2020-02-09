package com.bruce.jing.hello.demo.widget.emoji.adapter;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bruce.jing.hello.demo.R;
import com.bruce.jing.hello.demo.adapter.BaseRecyclerViewAdapter;
import com.bruce.jing.hello.demo.util.CommonUtil;
import com.bruce.jing.hello.demo.widget.emoji.entity.BaseEmojiIconEntity;
import com.bruce.jing.hello.demo.widget.emoji.entity.EmoticonsEntity;

public class EmoticonsGridAdapter extends BaseRecyclerViewAdapter<BaseEmojiIconEntity> {




    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_emoticons, parent, false);
        return new EmoticonsHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        EmoticonsHolder emoticonsHolder = (EmoticonsHolder)holder;
        EmoticonsEntity entity = (EmoticonsEntity) mDatas.get(position);
        String text = entity.getText();
        emoticonsHolder.tvEmoticons.setText(text);

    }

    static class EmoticonsHolder extends RecyclerView.ViewHolder{

        private TextView tvEmoticons;

        public EmoticonsHolder(View itemView) {
            super(itemView);
            tvEmoticons = (TextView) itemView.findViewById(R.id.tv_emoticons);
            GradientDrawable bgDrawable = new GradientDrawable();
            bgDrawable.setCornerRadius(CommonUtil.dip2px(itemView.getContext(),6f));
            int color = Color.parseColor("#C7C7C7");
            bgDrawable.setStroke(CommonUtil.dip2px(itemView.getContext(),0.5f), color);
            bgDrawable.setColor(Color.WHITE);
            tvEmoticons.setBackgroundDrawable(bgDrawable);
        }



    }
}
