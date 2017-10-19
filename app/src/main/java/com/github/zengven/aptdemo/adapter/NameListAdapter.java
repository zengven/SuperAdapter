package com.github.zengven.aptdemo.adapter;

import android.view.View;

import com.github.core.base.BaseListAdapter;
import com.github.core.base.BaseViewHolder;
import com.github.zengven.aptdemo.R;
import com.github.zengven.aptdemo.bean.NameBean;
import com.github.zengven.aptdemo.holder.NameHolder;

/**
 * author: zengven
 * date: 2017/8/25 16:22
 * desc: TODO
 */

public class NameListAdapter extends BaseListAdapter<NameBean> {
    private static final String TAG = "NameListAdapter";

    @Override
    protected BaseViewHolder createViewHolder(int viewType, View itemView) {
        return new NameHolder(itemView);
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
