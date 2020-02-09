package com.bruce.jing.hello.demo.widget.emoji.adapter;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bruce.jing.hello.demo.R;
import com.bruce.jing.hello.demo.adapter.BaseRecyclerViewAdapter;
import com.bruce.jing.hello.demo.widget.emoji.entity.BaseEmojiIconEntity;
import com.bruce.jing.hello.demo.widget.emoji.entity.EmojiIconEntity;

public class EmojiIconAdapter extends BaseRecyclerViewAdapter<BaseEmojiIconEntity> {


    private static final int ITEM_TYPE_EMOJI = R.layout.item_emoji;
    private static final int ITEM_TYPE_DELETE = R.layout.item_emoji_delete_btn;
    private static final int ITEM_TYPE_PLACEHOLDER = R.layout.item_emoji_place_holder;


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(viewType, parent, false);
        if (viewType == ITEM_TYPE_PLACEHOLDER) {
            return new PlaceHolder(itemView);
        } else if (viewType == ITEM_TYPE_DELETE) {
            return new DeleteHolder(itemView);
        } else {
            return new EmojiIconHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        if(holder instanceof EmojiIconHolder) {

            EmojiIconHolder emojiIconHolder = (EmojiIconHolder) holder;
            EmojiIconEntity entity = (EmojiIconEntity) mDatas.get(position);
            emojiIconHolder.ivEmojiIcon.setBackgroundResource(entity.getIcon());
        }else if(holder instanceof DeleteHolder){
            DeleteHolder deleteHolder = (DeleteHolder) holder;
            deleteHolder.ivDeleteIcon.setBackgroundResource(R.drawable.icon_emoji_delete);
        }



    }


    @Override
    public int getItemViewType(int position) {
        BaseEmojiIconEntity entity = mDatas.get(position);
        switch (entity.getType()) {
            case PLACEHOLDER:
                return ITEM_TYPE_PLACEHOLDER;
            case DELETE_BUTTON:
                return ITEM_TYPE_DELETE;
            default:
                return ITEM_TYPE_EMOJI;
        }
    }

    static class EmojiIconHolder extends RecyclerView.ViewHolder{

        private ImageView ivEmojiIcon;
        public EmojiIconHolder(View itemView) {
            super(itemView);
            ivEmojiIcon = (ImageView) itemView.findViewById(R.id.iv_emoji);
        }


    }

    static class PlaceHolder extends RecyclerView.ViewHolder{

        public PlaceHolder(View itemView) {
            super(itemView);
        }


    }

    static class DeleteHolder extends RecyclerView.ViewHolder{
        private ImageView ivDeleteIcon;
        public DeleteHolder(View itemView) {
            super(itemView);
            ivDeleteIcon = (ImageView) itemView.findViewById(R.id.iv_delete);
        }


    }


}
