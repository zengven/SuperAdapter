package com.github.core.base;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * author: zengven
 * date: 2017/6/20
 * Desc: RecyclerView base holder
 */
public abstract class BaseViewHolder<T> extends RecyclerView.ViewHolder {

    public BaseViewHolder(View itemView) {
        super(itemView);
    }

    public Context getContext() {
        return itemView.getContext();
    }

    @Nullable
    public final View findViewById(@IdRes int id) {
        return itemView.findViewById(id);
    }

    public abstract void onBind(T bean);
}
