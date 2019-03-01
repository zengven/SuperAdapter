package com.yexin.core.base;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;

/**
 * author: zengven
 * date: 2017/6/20
 * Desc: base holder
 */
public abstract class BaseViewHolder<T> extends RecyclerView.ViewHolder {
    private static final String TAG = "BaseViewHolder";
    private final SparseArray<View> mViews;

    public BaseViewHolder(View itemView) {
        super(itemView);
        mViews = new SparseArray<View>();
    }

    /**
     * Returns the context the view is running in, through which it can
     * access the current theme, resources, etc.
     *
     * @return The view's Context.
     */
    public Context getContext() {
        return itemView.getContext();
    }

    /**
     * Look for a child view with the given id.  If this view has the given
     * id, return this view.
     *
     * @param id The id to search for.
     * @return The view that has the given id in the hierarchy or null
     */
    @Nullable
    protected final View findViewById(@IdRes int id) {
        return itemView.findViewById(id);
    }

    /**
     * get child view with the given id from itemview or cache
     *
     * @param id
     * @return
     */
    protected <E extends View> E getView(@IdRes int id) {
        View view = mViews.get(id);
        if (view == null) {
            view = findViewById(id);
            mViews.put(id, view);
        }
        return (E) view;
    }

    public abstract void onBind(T bean);
}
