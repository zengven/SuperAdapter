package com.github.core.base;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * author: zengven
 * date: 2017/6/16 15:14
 * desc: 轮播图 适配器基类
 */
public abstract class BasePagerAdapter<T> extends PagerAdapter {
    /**
     * Lock used to modify the content of {@link #mObjects}. Any write operation
     * performed on the array should be synchronized on this lock.
     */
    private final Object mLock = new Object();

    /**
     * mObjects only contain the filtered or display svalues.
     */
    private List<T> mObjects;

    public BasePagerAdapter() {
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
    public void addAll(@NonNull List<? extends T> collection) {
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
    public void addAll(@NonNull List<? extends T> collection, int index) {
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

    public List<T> getList() {
        return mObjects;
    }

    protected T getItem(int position) {
        return mObjects.get(position);
    }

    @Override
    public int getCount() {
        return null != mObjects ? mObjects.size() : 0;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = getView(container.getContext(), position);
        container.addView(view);
        return view;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    protected abstract View getView(Context context, int position);
}
