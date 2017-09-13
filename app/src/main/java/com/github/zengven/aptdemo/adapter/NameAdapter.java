package com.github.zengven.aptdemo.adapter;

import android.view.View;

import com.github.core.base.BaseRecyclerAdapter;
import com.github.core.base.BaseViewHolder;
import com.github.zengven.aptdemo.R;
import com.github.zengven.aptdemo.bean.NameBean;
import com.github.zengven.aptdemo.holder.NameHolder;

/**
 * author: zengven
 * date: 2017/8/22 16:19
 * desc: TODO
 */
public class NameAdapter extends BaseRecyclerAdapter<NameBean> {

    public NameAdapter() {
    }

    @Override
    protected BaseViewHolder createViewHolder(int viewType, View itemView) {
        return new NameHolder(itemView);
    }

    @Override
    public int getViewType(NameBean s) {
        return R.layout.simple_list_item_1;
    }
}
