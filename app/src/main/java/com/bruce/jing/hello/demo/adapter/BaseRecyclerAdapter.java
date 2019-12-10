package com.bruce.jing.hello.demo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author bruce jing
 * @date 2019/12/7
 */
public abstract class BaseRecyclerAdapter<T> extends RecyclerView.Adapter {

    protected Context mContext;
    protected List<T> mDataList = new ArrayList<>();

    public BaseRecyclerAdapter(Context context){
        mContext = context;
    }

    public void setData(List<T> data){
        mDataList.clear();
        mDataList.addAll(data);
        notifyDataSetChanged();
    }

    public void addData(List<T> data){
        if(data == null || data.isEmpty()){
            return;
        }
        mDataList.addAll(data);
        notifyDataSetChanged();
    }

    public void clearData(){
        mDataList.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }
}

