package com.yexin.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * author: zengven
 * date: 2017/8/22 16:00
 * desc: TODO
 */
@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.CLASS)
public @interface Adapter {

    Class<?> dataClass() default String.class;

    Class<?>[] viewHolderClass() default {String.class};

    int[] layoutIds();
}
