package com.bruce.jing.hello.demo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bruce.jing.hello.demo.R;


/**
 * @author bruce jing
 * @date 2019/12/7
 */
public class TitleAdapter extends BaseRecyclerAdapter<String> {

    public TitleAdapter(Context context) {
        super(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_title_tab, parent, false);
        TitleViewHolder viewHolder = new TitleViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        String title = mDataList.get(position);
        ((TitleViewHolder) holder).nameTV.setText(title);
    }

    static class TitleViewHolder extends RecyclerView.ViewHolder {
        TextView nameTV;

        public TitleViewHolder(View itemView) {
            super(itemView);
            nameTV = itemView.findViewById(R.id.tv_tab_name);
        }
    }

}

