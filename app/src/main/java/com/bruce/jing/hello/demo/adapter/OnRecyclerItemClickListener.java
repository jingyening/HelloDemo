package com.bruce.jing.hello.demo.adapter;

import android.view.View;

/**
 * Created by Sylvester on 17/3/22.
 */
public interface OnRecyclerItemClickListener {
    void onItemClick(View view, int position);

    void onItemLongClick(View view, int position);
}
