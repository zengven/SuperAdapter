package com.github.zengven.aptdemo.adapter;

import android.view.View;

import com.github.core.base.BaseListAdapter;
import com.github.core.base.BaseListViewHolder;
import com.github.zengven.aptdemo.R;
import com.github.zengven.aptdemo.bean.NameBean;
import com.github.zengven.aptdemo.holder.NameListHolder;

/**
 * author: zengven
 * date: 2017/8/25 16:22
 * desc: TODO
 */

public class NameListAdapter extends BaseListAdapter<NameBean> {
    private static final String TAG = "NameListAdapter";

    @Override
    protected BaseListViewHolder createViewHolder(int viewType, View itemView) {
        return new NameListHolder(itemView);
    }

    @Override
    protected int[] getLayoutIds() {
        return new int[]{R.layout.simple_list_item_1};
    }

    @Override
    public int getViewType(NameBean nameBean, int position) {
        return 0;
    }


}
