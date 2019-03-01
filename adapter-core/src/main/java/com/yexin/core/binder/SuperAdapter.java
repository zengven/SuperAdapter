package com.yexin.core.binder;

import android.util.Log;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * author: zengven
 * date: 2017/8/30 18:28
 * desc: TODO
 */
public class SuperAdapter {

    private static final String TAG = "SuperAdapter";
    private static final String SUFFIX = "$$AdapterBinder";
    private static final boolean debug = true;
    private static final Map<Class<?>, AdapterBinder<Object>> BINDERS = new LinkedHashMap<>();

    private SuperAdapter() {
    }

    public static void bind(Object target) {
        Class<?> targetClass = target.getClass();
        try {
            AdapterBinder<Object> adapterBinder = findAdapterBinderForClass(targetClass);
            if (adapterBinder != null) {
                adapterBinder.bind(target);
            }
        } catch (Exception e) {
            throw new RuntimeException("Unable to bind adapter for " + targetClass.getName(), e);
        }
    }

    private static AdapterBinder<Object> findAdapterBinderForClass(Class<?> cls) throws IllegalAccessException, InstantiationException {
        AdapterBinder<Object> adapterBinder = BINDERS.get(cls);
        if (adapterBinder != null) {
            if (debug) Log.i(TAG, "get adapter binder from cache map!");
            return adapterBinder;
        }
        String clsName = cls.getName();
        Log.i(TAG, "findAdapterBinderForClass: clsName: " + clsName + "  SUFFIX: " + SUFFIX);
        try {
            Class<?> adapterBinderClass = Class.forName(clsName + SUFFIX);
            adapterBinder = (AdapterBinder<Object>) adapterBinderClass.newInstance();
            if (debug) Log.i(TAG, " loaded adapter from binder class ");
        } catch (ClassNotFoundException e) {
            if (debug) Log.i(TAG, " not found try super class " + cls.getClass().getName());
        }
        BINDERS.put(cls, adapterBinder);
        return adapterBinder;
    }


    /**
     * @param <T>
     */
    public interface AdapterBinder<T> {
        void bind(T target);
    }
}
