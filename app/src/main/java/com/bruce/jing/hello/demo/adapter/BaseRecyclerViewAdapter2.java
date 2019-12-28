package com.bruce.jing.hello.demo.adapter;

import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

/**
 * -------------------------------------
 * 作者：bruce jing
 * -------------------------------------
 * 时间：2018/10/25 下午8:36
 * -------------------------------------
 * 描述：
 * -------------------------------------
 * 备注：
 * -------------------------------------
 */
public abstract class BaseRecyclerViewAdapter2<T> extends RecyclerView.Adapter {


    private List<T> mData = new ArrayList<>();

    private OnItemClickListener mItemClickListener;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, final int position) {
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickImpl(position, viewHolder);
                    if(mItemClickListener != null) {
                        mItemClickListener.onItemClick(position, viewHolder.itemView);
                    }
                }
            });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void setItemClickListener(OnItemClickListener itemClickListener) {
        this.mItemClickListener = itemClickListener;
    }

    public void setData(List<T> data) {
        if(data != null){
            this.mData = data;
            notifyDataSetChanged();
        }
    }

    public abstract void onItemClickImpl(int position, RecyclerView.ViewHolder holder);

    public interface OnItemClickListener{
        void onItemClick(int position, View itemView);
    }
}
