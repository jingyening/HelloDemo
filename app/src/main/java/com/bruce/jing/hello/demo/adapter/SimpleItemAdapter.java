package com.bruce.jing.hello.demo.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bruce.jing.hello.demo.R;

import java.util.ArrayList;
import java.util.List;

/**
 * -------------------------------------
 * 作者：bruce jing
 * -------------------------------------
 * 时间：2018/8/18 下午6:38
 * -------------------------------------
 * 描述：
 * -------------------------------------
 * 备注：
 * -------------------------------------
 */
public class SimpleItemAdapter extends BaseRecyclerViewAdapter2<String> {


    private List<String> mDatas = new ArrayList<>();


    public SimpleItemAdapter(List<String> data) {
        if (data != null) {
            mDatas = data;
        }

    }


    @NonNull
    @Override
    public SimpleViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View itemView = View.inflate(viewGroup.getContext(),R.layout.adapter_item_simple_view, null);
        SimpleViewHolder holder = new SimpleViewHolder(itemView);
        holder.textView =  itemView.findViewById(R.id.textView);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        super.onBindViewHolder(viewHolder,position);
        SimpleViewHolder simpleViewHolder = (SimpleViewHolder)viewHolder;
        simpleViewHolder.textView.setText(mDatas.get(position));
    }



    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    @Override
    public void onItemClickImpl(int position, RecyclerView.ViewHolder holder) {

    }


    public class SimpleViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public SimpleViewHolder(View itemView) {
            super(itemView);
        }
    }


}
