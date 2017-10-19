package com.github.core.base;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author yuhushuan@le.com
 * @ClassName: BaseRecyclerAdapter
 * @Description: 多类型适配器
 * @date 2017/3/17 20:34
 */
public abstract class BaseRecyclerAdapter<T> extends RecyclerView.Adapter<BaseViewHolder> {
    /**
     * Lock used to modify the content of {@link #mObjects}. Any write operation
     * performed on the array should be synchronized on this lock.
     */
    private final Object mLock = new Object();

    /**
     * mObjects only contain the filtered or display svalues.
     */
    private List<T> mObjects;

    public BaseRecyclerAdapter() {
        this.mObjects = new ArrayList<T>();
    }

    /**
     * Adds the specified object at the end of the array.
     *
     * @param object The object to add at the end of the array.
     */
    public void add(@Nullable T object) {
        synchronized (mLock) {
            mObjects.add(object);
        }
        notifyDataSetChanged();
    }

    /**
     * add the specified object at the specified index in the array.
     *
     * @param object The object to insert into the array.
     * @param index  The index at which the object must be inserted.
     */
    public void add(@Nullable T object, int index) {
        synchronized (mLock) {
            mObjects.add(index, object);
        }
        notifyDataSetChanged();
    }

    /**
     * Adds the specified Collection at the end of the array.
     *
     * @param collection The Collection to add at the end of the array.
     */
    public void addAll(@NonNull Collection<? extends T> collection) {
        synchronized (mLock) {
            mObjects.addAll(collection);
        }
        notifyDataSetChanged();
    }

    /**
     * Adds the specified Collection at the specified index in the array.
     *
     * @param collection The Collection to add at the end of the array.
     */
    public void addAll(@NonNull Collection<? extends T> collection, int index) {
        synchronized (mLock) {
            mObjects.addAll(index, collection);
        }
        notifyDataSetChanged();
    }

    /**
     * Adds the specified items at the end of the array.
     *
     * @param items The items to add at the end of the array.
     */
    public void addAll(T... items) {
        synchronized (mLock) {
            Collections.addAll(mObjects, items);
        }
        notifyDataSetChanged();
    }

    /**
     * Removes the specified object from the array.
     *
     * @param object The object to remove.
     */
    public void remove(@Nullable T object) {
        synchronized (mLock) {
            mObjects.remove(object);
        }
        notifyDataSetChanged();
    }

    /**
     * Remove all elements from the list.
     */
    public void clear() {
        synchronized (mLock) {
            mObjects.clear();
        }
        notifyDataSetChanged();
    }

    /**
     * Sorts the content of this adapter using the specified comparator.
     *
     * @param comparator The comparator used to sort the objects contained
     *                   in this adapter.
     */
    public void sort(@NonNull Comparator<? super T> comparator) {
        synchronized (mLock) {
            Collections.sort(mObjects, comparator);
        }
        notifyDataSetChanged();
    }

    /**
     * 删除
     *
     * @param position
     */
    public void removeItem(int position) {
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, getItemCount());
        mObjects.remove(position);
    }

    /**
     * 删除
     *
     * @param position
     */
    public void removeItemData(int position) {
        mObjects.remove(position);
        notifyDataSetChanged();
    }

    public boolean isEmpty() {
        return mObjects == null || mObjects.isEmpty();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    protected T getItem(int position) {
        return mObjects.get(position);
    }

    @Override
    public int getItemCount() {
        return mObjects == null ? 0 : mObjects.size();
    }

    @Override
    public int getItemViewType(int position) {
        return getViewType(getItem(position));
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(viewType, parent, false);
        return createViewHolder(viewType, itemView);
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        T t = getItem(position);
        holder.onBind(t);
    }

    protected abstract BaseViewHolder createViewHolder(int viewType, View itemView);

    /**
     * @param t
     * @return 必须是布局文件的layout id
     */
    public abstract int getViewType(T t);


    public OnItemClickListener mListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public OnViewTypeListener mOnViewTypeListener;

    public interface OnViewTypeListener<T> {
        int onViewType(T t);
    }

    public void setOnViewTypeListener(OnViewTypeListener listener) {
        mOnViewTypeListener = listener;
    }

}
