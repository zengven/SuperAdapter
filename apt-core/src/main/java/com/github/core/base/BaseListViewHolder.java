package com.github.core.base;

import android.view.View;

/**
 * author: zengven
 * date: 2017/8/11 16:53
 * desc: TODO
 */

public abstract class BaseListViewHolder<T> {

    public final View itemView;

    public BaseListViewHolder(View itemView) {
        if (itemView == null) {
            throw new IllegalArgumentException("itemView may not be null");
        }
        this.itemView = itemView;
    }

    public abstract void onBind(T bean);
}
