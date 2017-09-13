package com.github.core.base;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * author: zengven
 * date: 2017/7/5 17:27
 * desc: base listview adapter
 */
public abstract class BaseListAdapter<T> extends BaseAdapter implements Filterable {

    private static final String TAG = "BaseListAdapter";

    /**
     * Lock used to modify the content of {@link #mObjects}. Any write operation
     * performed on the array should be synchronized on this lock. This lock is also
     * used by the filter (see {@link #getFilter()} to make a synchronized copy of
     * the original array of data.
     */
    private final Object mLock = new Object();

    /**
     * mObjects only contain the filtered or display svalues.
     */
    private List<T> mObjects;

    /**
     * A copy of the original mObjects array, initialized from and then used instead as soon as
     * the mFilter ArrayFilter is used. mObjects will then only contain the filtered values.
     */
    private ArrayList<T> mOriginalValues;


    /**
     * Indicates whether or not {@link #notifyDataSetChanged()} must be called whenever
     * {@link #mObjects} is modified.
     */
    private boolean mNotifyOnChange = true;
    private LayoutInflater mInflater;
    private ArrayFilter mFilter;

    public BaseListAdapter() {
        this.mObjects = new ArrayList<T>();
    }

    /**
     * Adds the specified object at the end of the array.
     *
     * @param object The object to add at the end of the array.
     */
    public void add(@Nullable T object) {
        synchronized (mLock) {
            if (mOriginalValues != null) {
                mOriginalValues.add(object);
            } else {
                mObjects.add(object);
            }
        }
        if (mNotifyOnChange) notifyDataSetChanged();
    }

    /**
     * add the specified object at the specified index in the array.
     *
     * @param object The object to insert into the array.
     * @param index  The index at which the object must be inserted.
     */
    public void add(@Nullable T object, int index) {
        synchronized (mLock) {
            if (mOriginalValues != null) {
                mOriginalValues.add(index, object);
            } else {
                mObjects.add(index, object);
            }
        }
        if (mNotifyOnChange) notifyDataSetChanged();
    }

    /**
     * Adds the specified Collection at the end of the array.
     *
     * @param collection The Collection to add at the end of the array.
     */
    public void addAll(@NonNull List<? extends T> collection) {
        synchronized (mLock) {
            if (mOriginalValues != null) {
                mOriginalValues.addAll(collection);
            } else {
                mObjects.addAll(collection);
            }
        }
        if (mNotifyOnChange) notifyDataSetChanged();
    }

    /**
     * Adds the specified Collection at the specified index in the array.
     *
     * @param collection The Collection to add at the end of the array.
     */
    public void addAll(@NonNull List<? extends T> collection, int index) {
        synchronized (mLock) {
            if (mOriginalValues != null) {
                mOriginalValues.addAll(index, collection);
            } else {
                mObjects.addAll(index, collection);
            }
        }
        if (mNotifyOnChange) notifyDataSetChanged();
    }

    /**
     * Adds the specified items at the end of the array.
     *
     * @param items The items to add at the end of the array.
     */
    public void addAll(T... items) {
        synchronized (mLock) {
            if (mOriginalValues != null) {
                Collections.addAll(mOriginalValues, items);
            } else {
                Collections.addAll(mObjects, items);
            }
        }
        if (mNotifyOnChange) notifyDataSetChanged();
    }

    /**
     * Removes the specified object from the array.
     *
     * @param object The object to remove.
     */
    public void remove(@Nullable T object) {
        synchronized (mLock) {
            if (mOriginalValues != null) {
                mOriginalValues.remove(object);
            } else {
                mObjects.remove(object);
            }
        }
        if (mNotifyOnChange) notifyDataSetChanged();
    }

    /**
     * Remove all elements from the list.
     */
    public void clear() {
        synchronized (mLock) {
            if (mOriginalValues != null) {
                mOriginalValues.clear();
            } else {
                mObjects.clear();
            }
        }
        if (mNotifyOnChange) notifyDataSetChanged();
    }

    /**
     * Sorts the content of this adapter using the specified comparator.
     *
     * @param comparator The comparator used to sort the objects contained
     *                   in this adapter.
     */
    public void sort(@NonNull Comparator<? super T> comparator) {
        synchronized (mLock) {
            if (mOriginalValues != null) {
                Collections.sort(mOriginalValues, comparator);
            } else {
                Collections.sort(mObjects, comparator);
            }
        }
        if (mNotifyOnChange) notifyDataSetChanged();
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        mNotifyOnChange = true;
    }

    /**
     * Control whether methods that change the list ({@link #add}, {@link #addAll(List)},
     * {@link #addAll(Object[])}, {@link #add(Object, int)}, {@link #remove}, {@link #clear},
     * {@link #sort(Comparator)}) automatically call {@link #notifyDataSetChanged}.  If set to
     * false, caller must manually call notifyDataSetChanged() to have the changes
     * reflected in the attached view.
     * <p>
     * The default is true, and calling notifyDataSetChanged()
     * resets the flag to true.
     *
     * @param notifyOnChange if true, modifications to the list will
     *                       automatically call {@link
     *                       #notifyDataSetChanged}
     */
    public void setNotifyOnChange(boolean notifyOnChange) {
        mNotifyOnChange = notifyOnChange;
    }


    @Override
    public int getCount() {
        return mObjects.size();
    }

    @Override
    public
    @Nullable
    T getItem(int position) {
        return mObjects.get(position);
    }

    /**
     * Returns the position of the specified item in the array.
     *
     * @param item The item to retrieve the position of.
     * @return The position of the specified item.
     */
    public int getPosition(@Nullable T item) {
        return mObjects.indexOf(item);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return getViewType(getItem(position), position);
    }

    @Override
    public int getViewTypeCount() {
        int[] layoutIds = getLayoutIds();
        return getLayoutIds().length;
    }

    @Override
    public
    @NonNull
    View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        BaseListViewHolder baseViewHolder = null;
        if (convertView == null) {
            baseViewHolder = onCreateViewHolder(parent, getLayoutIds()[getItemViewType(position)], getItemViewType(position));
            convertView = baseViewHolder.itemView;
            convertView.setTag(baseViewHolder);
        } else {
            baseViewHolder = (BaseListViewHolder) convertView.getTag();
        }
        baseViewHolder.onBind(getItem(position));
        return convertView;
    }

    private BaseListViewHolder onCreateViewHolder(ViewGroup parent, int layoutId, int viewType) {
        if (mInflater == null)
            mInflater = LayoutInflater.from(parent.getContext());
        View itemView = mInflater.inflate(layoutId, parent, false);
        return createViewHolder(viewType, itemView);
    }

    protected abstract BaseListViewHolder createViewHolder(int viewType, View itemView);

    protected abstract int[] getLayoutIds();

    /**
     * @param t
     * @return 必须为LayoutIds对于的角标
     */
    public abstract int getViewType(T t, int position);

    public OnViewTypeListener mOnViewTypeListener;

    public interface OnViewTypeListener<T> {
        int onViewType(T t);
    }

    public void setOnViewTypeListener(OnViewTypeListener listener) {
        mOnViewTypeListener = listener;
    }

    @Override
    public
    @NonNull
    Filter getFilter() {
        if (mFilter == null) {
            mFilter = new ArrayFilter();
        }
        return mFilter;
    }

    /**
     * <p>An array filter constrains the content of the array adapter with
     * a prefix. Each item that does not start with the supplied prefix
     * is removed from the list.</p>
     */
    private class ArrayFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            final FilterResults results = new FilterResults();

            if (mOriginalValues == null) {
                synchronized (mLock) {
                    mOriginalValues = new ArrayList<>(mObjects);
                }
            }

            if (prefix == null || prefix.length() == 0) {
                final ArrayList<T> list;
                synchronized (mLock) {
                    list = new ArrayList<>(mOriginalValues);
                }
                results.values = list;
                results.count = list.size();
            } else {
                final ArrayList<T> values;
                synchronized (mLock) {
                    values = new ArrayList<>(mOriginalValues);
                }

                final int count = values.size();
                final ArrayList<T> newValues = new ArrayList<>();

                final String prefixString = prefix.toString().toLowerCase();

                for (int i = 0; i < count; i++) {
                    final T value = values.get(i);

                    // First match against the whole, non-splitted value
                    T filter = BaseListAdapter.this.valueFilter(prefixString, value);
                    if (null != filter) {
                        newValues.add(filter);
                    }
                }

                results.values = newValues;
                results.count = newValues.size();
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            //noinspection unchecked
            mObjects = (List<T>) results.values;
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }

        @Override
        public CharSequence convertResultToString(Object resultValue) {
            return BaseListAdapter.this.convertResultToString(resultValue);
        }
    }

    /**
     * 输入过滤器
     *
     * @param prefixString
     * @param value
     * @return
     */
    protected T valueFilter(String prefixString, T value) {
        final String valueText = value.toString().toLowerCase();
        if (valueText.startsWith(prefixString)) {
            return value;
        } else {
            final String[] words = valueText.split(" ");
            for (String word : words) {
                if (word.startsWith(prefixString)) {
                    return value;
                }
            }
        }
        return null;
    }

    /**
     * <p>Converts a value from the filtered set into a CharSequence. Subclasses
     * should override this method to convert their results. The default
     * implementation returns an empty String for null values or the default
     * String representation of the value.</p>
     *
     * @param resultValue the value to convert to a CharSequence
     * @return a CharSequence representing the value
     */
    public CharSequence convertResultToString(Object resultValue) {
        return resultValue == null ? "" : resultValue.toString();
    }
}
