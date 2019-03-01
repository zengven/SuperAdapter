package com.github;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.DeclaredType;

/**
 * author: zengven
 * date: 2019/3/1 10:07
 * desc: TODO
 */

public class ElementUtil {

    /**
     * get annotation mirrors
     *
     * @param element
     * @param annotationClass
     * @return
     */
    public static Map<? extends ExecutableElement, ? extends AnnotationValue> getAnnotationMirrors(Element element, Class annotationClass) {
        List<? extends AnnotationMirror> annotationMirrors = element.getAnnotationMirrors();
        for (AnnotationMirror am : annotationMirrors) {
            DeclaredType annotationType = am.getAnnotationType();
            if (annotationClass.getTypeName().equals(annotationType.toString())) {
                return am.getElementValues();
            }
        }
        return null;
    }

    /**
     * get annotation field value by field name
     *
     * @param elementValue
     * @param fieldName
     */
    public static AnnotationValue findAnnotationFieldValueByName(Map<? extends ExecutableElement, ? extends AnnotationValue> elementValue, String fieldName) {
        Set<? extends Map.Entry<? extends ExecutableElement, ? extends AnnotationValue>> entries = elementValue.entrySet();
        Iterator<? extends Map.Entry<? extends ExecutableElement, ? extends AnnotationValue>> iterator = entries.iterator();
        while (iterator.hasNext()) {
            Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> next = iterator.next();
            if (next.getKey().getSimpleName().contentEquals(fieldName)) {
                return next.getValue();
            }
        }
        return null;
    }
}
