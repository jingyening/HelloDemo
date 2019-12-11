package com.bruce.jing.hello.demo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bruce.jing.hello.demo.R;

/**
 * @author bruce jing
 * @date 2019/12/9
 */
public class LinearSongCardAdapter extends BaseRecyclerAdapter<String> {

    private static final String TAG = "SongCardAdapter";

    public LinearSongCardAdapter(Context context) {
        super(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_linear_song_card, parent, false);
        SongCardViewHolder viewHolder = new SongCardViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        final SongCardViewHolder songCardViewHolder = (SongCardViewHolder) holder;
        String songName = mDataList.get(position);
        songCardViewHolder.nameTV.setText(songName);
        int squireLayoutWidth = songCardViewHolder.squireLayout.getMeasuredWidth();
        int itemViewWidth = songCardViewHolder.itemView.getMeasuredWidth();
//        Log.d(TAG, "onBindViewHolder squireLayoutWidth = " + squireLayoutWidth + " ,itemViewWidth = " + itemViewWidth);
        if(itemViewWidth > squireLayoutWidth){
            ViewGroup.LayoutParams layoutParams = songCardViewHolder.itemView.getLayoutParams();
            layoutParams.width = squireLayoutWidth;
            layoutParams.height = songCardViewHolder.itemView.getHeight();
            songCardViewHolder.itemView.setLayoutParams(layoutParams);
        }
        songCardViewHolder.squireLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "onBindViewHolder width = " + songCardViewHolder.squireLayout.getWidth() + " ,measureWith = " + songCardViewHolder.squireLayout.getMeasuredWidth());
                Log.d(TAG, "onBindViewHolder item width = " + songCardViewHolder.itemView.getWidth());
            }
        }, 100);
    }

    static class SongCardViewHolder extends RecyclerView.ViewHolder {
        private TextView nameTV;
        private View squireLayout;

        public SongCardViewHolder(View itemView) {
            super(itemView);
            nameTV = itemView.findViewById(R.id.tv_song_name);
            squireLayout = itemView.findViewById(R.id.fl_song_card);
        }
    }
}
