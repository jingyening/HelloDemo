package com.bruce.jing.hello.demo.adapter;

import java.util.List;

/**
 * adapter的状态和数据回调
 */
public interface AdapterCallback<T> {
    /**
     * 获取adapter数据集合
     *
     * @return
     */
    List<T> getDataList();

    /**
     * 获取adapter偏移的数量，一般是指加载数据前添加的item的个数，如RecyclerView额外添加的头数据
     * 用来修正获取数据的index值
     *
     * @return 偏移数量
     */
    int getOffsetCount();
}
