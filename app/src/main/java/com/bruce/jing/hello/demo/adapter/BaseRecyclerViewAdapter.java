package com.bruce.jing.hello.demo.adapter;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

/**
 * RecyclerView基类，可设置外部点击事件监听
 * Created by Sylvester on 17/3/22.
 */
public abstract class BaseRecyclerViewAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements AdapterCallback<T> {

    protected List<T> mDatas = new ArrayList<>();

    protected boolean isScrolling = false;

    protected OnRecyclerItemClickListener mOnItemClickListener;

    public BaseRecyclerViewAdapter() {
        mDatas = new ArrayList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public int getItemCount() {
        return mDatas == null ? 0 : mDatas.size();
    }

    public T getItem(int position) {
        if (position < 0 || position >= getItemCount())
            return null;
        return mDatas == null ? null : mDatas.get(position);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (mOnItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = holder.getLayoutPosition();
                    mOnItemClickListener.onItemClick(holder.itemView, pos);
                }
            });

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int pos = holder.getLayoutPosition();
                    mOnItemClickListener.onItemLongClick(holder.itemView, pos);
                    //拦截单击事件
                    return true;
                }
            });
        }
    }

    // 加载数据时调用
    public void addAll(List<T> mList) {
        if (mList != null) {
            mDatas.clear();
            mDatas.addAll(mList);
            notifyDataSetChanged();
        }
    }


    /**
     * 添加数据用这个，不会清空之前的数据
     * @param list
     */
    public void addAllData(List<T> list) {
        if (list == null) {
            list = new ArrayList<>();
        }
        mDatas.addAll(list);
        int start = mDatas.size();
        notifyItemRangeInserted(start, list.size());
    }

    public void addItem(T t) {
        if (t == null) {
            return;
        }
        mDatas.add(t);
        notifyDataSetChanged();
    }

    public void addItem(T t,int position) {
        if (t == null) {
            return;
        }
        mDatas.add(position,t);
        notifyDataSetChanged();
    }

    public List<T> getmDatas(){
        return mDatas;
    }

    public void removeItemByPosition(int position) {
        if (position > mDatas.size() - 1 || position < 0) {
            return;
        }
        mDatas.remove(position);
        notifyDataSetChanged();
    }

    // 清空列表
    public void clearAll() {
        mDatas.clear();
        notifyDataSetChanged();
    }

    // 移除单个item，带原生动效
    public void removeItemWithAnim(int position) {
        if (mDatas != null && mDatas.size() > position && position >= 0) {
            mDatas.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void setOnItemClickListener(OnRecyclerItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public void setScrolling(boolean scrolling) {
        isScrolling = scrolling;
    }

    // 防文本复用
    protected void setCommonText(TextView textView, String str) {
        setCommonText(textView, str, "");
    }

    protected void setCommonText(TextView textView, String str, String defaultStr) {
        if (!TextUtils.isEmpty(str)) {
            textView.setText(str);
        } else {
            textView.setText(defaultStr);
        }
    }

    @Override
    public List<T> getDataList() {
        return mDatas;
    }

    @Override
    public int getOffsetCount() {
        return 0;
    }
}
