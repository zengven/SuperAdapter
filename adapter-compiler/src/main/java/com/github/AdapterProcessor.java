package com.github;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class AdapterProcessor extends AbstractProcessor {
    public static final String ADAPTERBINDER_SUFFIX = "$$AdapterBinder";
    public static final String ADAPTER_SUFFIX = "$$Adapter";
    private static final String ANDROID_BASE_PACKAGE = "android.view";
    private static final String ADAPTER_PACKAGE_SUFFIX = ".adapter";
    private static final String PACKAGE_CORE = "com.github.core.base"; //core 核心包名
    private static final String RECYCLER_TYPE_MIRROR_SUFFIX = "RecyclerView";
    private static final String LIST_TYPE_MIRROR_SUFFIX = "ListView";
    private static final String GRID_TYPE_MIRROR_SUFFIX = "GridView";

    private Messager mMessager;
    private Filer mFiler;
    private Types mTypeUtils;
    private Elements mElementsUtils;

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        LinkedHashSet<String> types = new LinkedHashSet<>();
        types.add(Adapter.class.getCanonicalName());
        return types;
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        mFiler = processingEnvironment.getFiler();
        mMessager = processingEnvironment.getMessager();
        mTypeUtils = processingEnvironment.getTypeUtils();
        mElementsUtils = processingEnvironment.getElementUtils();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        debug("set TypeElement : " + set.toString());
        Map<AdapterClass, TypeSpec> typeSpecs = findAndParseTargets(roundEnvironment);
        for (Map.Entry<AdapterClass, TypeSpec> typeSpecEntry : typeSpecs.entrySet()) {
            debug("adapter : " + typeSpecEntry.getKey());
            JavaFile javaFile = JavaFile.builder(typeSpecEntry.getKey().getPackageName(), typeSpecEntry.getValue()).build();
            try {
                javaFile.writeTo(mFiler);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Map<String, BindingClass> bindingClass = findAndParseBindingClass(roundEnvironment);
        for (Map.Entry<String, BindingClass> bindClass : bindingClass.entrySet()) {
            JavaFile javaFile = JavaFile.builder(bindClass.getValue().getPackageName(), bindClass.getValue().createAdapterBinderClass()).build();
            try {
                javaFile.writeTo(mFiler);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    /**
     * process binding class
     *
     * @param env
     * @return
     */
    private Map<String, BindingClass> findAndParseBindingClass(RoundEnvironment env) {
        Map<String, BindingClass> bindingClassMap = new LinkedHashMap<>();

        //process binding class
        for (Element element : env.getElementsAnnotatedWith(Adapter.class)) {
            getOrCreateBindingClass(bindingClassMap, element);
        }
        return bindingClassMap;
    }

    private void getOrCreateBindingClass(Map<String, BindingClass> bindingClassMap, Element element) {
        TypeElement typeElement = (TypeElement) element.getEnclosingElement();
        String packageName = getPackageName(typeElement);
        String className = getClassName(typeElement, packageName);
        debug(" packageName: " + packageName + " className: " + className);
        BindingClass bindingClass = bindingClassMap.get(className);
        if (bindingClass == null) {
            bindingClass = new BindingClass(packageName, className, className + ADAPTERBINDER_SUFFIX, new BindingClass.Statement(element.getSimpleName().toString(), packageName + ADAPTER_PACKAGE_SUFFIX, getAdapterClassName(element)));
        } else {
            bindingClass.addField(new BindingClass.Statement(element.getSimpleName().toString(), packageName + ADAPTER_PACKAGE_SUFFIX, getAdapterClassName(element)));
        }
        bindingClassMap.put(className, bindingClass);
    }

    private static String getClassName(TypeElement type, String packageName) {
        int packageLen = packageName.length() + 1;
        return type.getQualifiedName().toString().substring(packageLen).replace('.', '$');
    }


    /**
     * process different annotation
     *
     * @param env
     * @return
     */
    private Map<AdapterClass, TypeSpec> findAndParseTargets(RoundEnvironment env) {
        Map<AdapterClass, TypeSpec> typeSpecs = new LinkedHashMap<>();

        //process recycler adapter
        for (Element element : env.getElementsAnnotatedWith(Adapter.class)) {
            debug("SimpleName: " + element.getSimpleName() + "  TypeMirror: " + element.asType() + " ElementKind: " + element.getKind() + " binaryName: " + mElementsUtils.getBinaryName((TypeElement) element.getEnclosingElement()));

            TypeMirror typeMirror = element.asType();
            if (typeMirror.toString().endsWith(RECYCLER_TYPE_MIRROR_SUFFIX)) {
                parseRecyclerAdapter(typeSpecs, element);
            }

            if (typeMirror.toString().endsWith(LIST_TYPE_MIRROR_SUFFIX) || typeMirror.toString().endsWith(GRID_TYPE_MIRROR_SUFFIX)) {
                parseListAdapter(typeSpecs, element);
            }

        }
        return typeSpecs;
    }

    /**
     * parse list adapter
     *
     * @param typeSpecs
     * @param element
     */
    private void parseListAdapter(Map<AdapterClass, TypeSpec> typeSpecs, Element element) {
        Adapter adapter = element.getAnnotation(Adapter.class);
        ParameterizedTypeName typeName = ParameterizedTypeName.get(ClassName.get(List.class), ClassName.get(Class.class));
        FieldSpec.Builder builder = FieldSpec.builder(typeName, "mClassList", Modifier.PRIVATE);
        FieldSpec fieldSpec = builder.build();

        FieldSpec layoutIdsFieldSpec = FieldSpec.builder(ArrayTypeName.of(int.class), "mLayoutIds", Modifier.PRIVATE).build();

        MethodSpec.Builder constructorMethodSpecBuilder = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addStatement("mClassList=new $T<>()", ClassName.get(ArrayList.class))
                .addStatement("mLayoutIds=new $T[$L]", int.class, adapter.layoutIds().length);

        Map<? extends ExecutableElement, ? extends AnnotationValue> annotationMirror = ElementUtil.getAnnotationMirrors(element, Adapter.class);
        AnnotationValue viewHolderClassAnnotationValue = ElementUtil.findAnnotationFieldValueByName(annotationMirror, "viewHolderClass");
        List<? extends AnnotationValue> viewHolderClassTypeMirrors = (List<? extends AnnotationValue>) viewHolderClassAnnotationValue.getValue();
        for (int i = 0; i < adapter.layoutIds().length; i++) {
            constructorMethodSpecBuilder.addStatement("mClassList.add($T.class)", ClassName.get((TypeMirror) viewHolderClassTypeMirrors.get(i).getValue()));
            constructorMethodSpecBuilder.addStatement("mLayoutIds[$L]=$L", i, adapter.layoutIds()[i]);
        }
        MethodSpec constructorMethodSpec = constructorMethodSpecBuilder.build();

        MethodSpec viewHolderMethodSpec = MethodSpec.methodBuilder("createViewHolder")
                .addModifiers(Modifier.PROTECTED)
                .addAnnotation(Override.class)
                .returns(ClassName.get(PACKAGE_CORE, "BaseViewHolder"))
                .addParameter(int.class, "viewType")
                .addParameter(ClassName.get(ANDROID_BASE_PACKAGE, "View"), "itemView")
                .beginControlFlow("try")
                .addStatement("$T cons = mClassList.get(viewType).getConstructor($T.class)", ClassName.get(Constructor.class), ClassName.get(ANDROID_BASE_PACKAGE, "View"))
                .addStatement("return (BaseViewHolder)cons.newInstance(itemView)")
                .nextControlFlow("catch ($T e)", NoSuchMethodException.class)
                .addStatement("e.printStackTrace()")
                .nextControlFlow("catch ($T e)", InstantiationException.class)
                .addStatement("e.printStackTrace()")
                .nextControlFlow("catch ($T e)", IllegalAccessException.class)
                .addStatement("e.printStackTrace()")
                .nextControlFlow("catch ($T e)", InvocationTargetException.class)
                .addStatement("e.printStackTrace()")
                .endControlFlow()
                .addStatement("return null")
                .build();

        AnnotationValue dataClassAnnotationValue = ElementUtil.findAnnotationFieldValueByName(annotationMirror, "dataClass");

        MethodSpec viewTypeMethodSpec = MethodSpec.methodBuilder("getViewType")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .returns(int.class)
                .addParameter(ClassName.get((TypeMirror) dataClassAnnotationValue.getValue()), "bean")
                .addParameter(int.class, "position")
                .beginControlFlow("if (mOnViewTypeListener == null)")
                .addStatement("return $L", 0)
                .endControlFlow()
                .addStatement("return mOnViewTypeListener.onViewType(bean)")
                .build();

        MethodSpec layoutIdMethodSpec = MethodSpec.methodBuilder("getLayoutIds")
                .addModifiers(Modifier.PROTECTED)
                .addAnnotation(Override.class)
                .returns(int[].class)
                .addStatement("return mLayoutIds")
                .build();

        TypeSpec typeSpec = TypeSpec.classBuilder(getAdapterClassName(element))
                .addModifiers(Modifier.PUBLIC)
                .superclass(ParameterizedTypeName.get(ClassName.get(PACKAGE_CORE, "BaseListAdapter"), ClassName.get((TypeMirror) dataClassAnnotationValue.getValue())))
                .addMethod(constructorMethodSpec)
                .addMethod(viewHolderMethodSpec)
                .addMethod(viewTypeMethodSpec)
                .addMethod(layoutIdMethodSpec)
                .addField(fieldSpec)
                .addField(layoutIdsFieldSpec)
                .build();
        typeSpecs.put(new AdapterClass(getPackageName((TypeElement) element.getEnclosingElement()) + ADAPTER_PACKAGE_SUFFIX), typeSpec);
    }

    /**
     * parse recycler adapter
     *
     * @param typeSpecs
     * @param element
     */
    private void parseRecyclerAdapter(Map<AdapterClass, TypeSpec> typeSpecs, Element element) {
        debug(" parseRecyclerAdapter : before ");
        Adapter adapter = element.getAnnotation(Adapter.class);

        ParameterizedTypeName typeName = ParameterizedTypeName.get(ClassName.get(Map.class), ClassName.get(Integer.class), ClassName.get(Class.class));
        FieldSpec.Builder builder = FieldSpec.builder(typeName, "mClassMap", Modifier.PRIVATE);
        FieldSpec fieldSpec = builder.build();

        MethodSpec.Builder constructorMethodSpecBuilder = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addStatement("mClassMap=new $T<>()", ClassName.get(HashMap.class));

        Map<? extends ExecutableElement, ? extends AnnotationValue> annotationMirror = ElementUtil.getAnnotationMirrors(element, Adapter.class);
        AnnotationValue viewHolderClassAnnotationValue = ElementUtil.findAnnotationFieldValueByName(annotationMirror, "viewHolderClass");
        List<? extends AnnotationValue> viewHolderClassTypeMirrors = (List<? extends AnnotationValue>) viewHolderClassAnnotationValue.getValue();

        for (int i = 0; i < adapter.layoutIds().length; i++) {
            constructorMethodSpecBuilder.addStatement("mClassMap.put($L,$T.class)", adapter.layoutIds()[i], ClassName.get((TypeMirror) viewHolderClassTypeMirrors.get(i).getValue()));
        }
        MethodSpec constructorMethodSpec = constructorMethodSpecBuilder.build();

        MethodSpec viewHolderMethodSpec = MethodSpec.methodBuilder("createViewHolder")
                .addModifiers(Modifier.PROTECTED)
                .addAnnotation(Override.class)
                .returns(ClassName.get(PACKAGE_CORE, "BaseViewHolder"))
                .addParameter(int.class, "viewType")
                .addParameter(ClassName.get(ANDROID_BASE_PACKAGE, "View"), "itemView")
                .beginControlFlow("try")
                .addStatement("$T cons = mClassMap.get(viewType).getConstructor($T.class)", ClassName.get(Constructor.class), ClassName.get(ANDROID_BASE_PACKAGE, "View"))
                .addStatement("return (BaseViewHolder)cons.newInstance(itemView)")
                .nextControlFlow("catch ($T e)", NoSuchMethodException.class)
                .addStatement("e.printStackTrace()")
                .nextControlFlow("catch ($T e)", InstantiationException.class)
                .addStatement("e.printStackTrace()")
                .nextControlFlow("catch ($T e)", IllegalAccessException.class)
                .addStatement("e.printStackTrace()")
                .nextControlFlow("catch ($T e)", InvocationTargetException.class)
                .addStatement("e.printStackTrace()")
                .endControlFlow()
                .addStatement("return null")
                .build();

        AnnotationValue dataClassAnnotationValue = ElementUtil.findAnnotationFieldValueByName(annotationMirror, "dataClass");

        MethodSpec viewTypeMethodSpec = MethodSpec.methodBuilder("getViewType")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .returns(int.class)
                .addParameter(ClassName.get((TypeMirror) dataClassAnnotationValue.getValue()), "bean")
                .beginControlFlow("if (mOnViewTypeListener == null)")
                .addStatement("return $L", adapter.layoutIds()[0])
                .endControlFlow()
                .addStatement("return mOnViewTypeListener.onViewType(bean)")
                .build();

        TypeSpec typeSpec = TypeSpec.classBuilder(getAdapterClassName(element))
                .addModifiers(Modifier.PUBLIC)
                .superclass(ParameterizedTypeName.get(ClassName.get(PACKAGE_CORE, "BaseRecyclerAdapter"), ClassName.get((TypeMirror) dataClassAnnotationValue.getValue())))
                .addMethod(constructorMethodSpec)
                .addMethod(viewHolderMethodSpec)
                .addMethod(viewTypeMethodSpec)
                .addField(fieldSpec)
                .build();
        typeSpecs.put(new AdapterClass(getPackageName((TypeElement) element.getEnclosingElement()) + ADAPTER_PACKAGE_SUFFIX), typeSpec);
    }

    private String getAdapterClassName(Element element) {
        TypeElement typeElement = (TypeElement) element.getEnclosingElement();
        String targetClass = getClassName(typeElement, getPackageName(typeElement));
        String elementName = element.getSimpleName().toString();
        debug(" targetClass: " + targetClass + "  elementName: " + elementName);
        StringBuilder builder = new StringBuilder();
        builder.append(targetClass);
        builder.append(elementName);
        builder.append(ADAPTER_SUFFIX);
        return builder.toString();
    }

    /**
     * 根据TypeElement获取包名
     *
     * @param type
     * @return
     */
    private String getPackageName(TypeElement type) {
        return mElementsUtils.getPackageOf(type).getQualifiedName().toString();
    }

    private void debug(String content) {
        mMessager.printMessage(Diagnostic.Kind.NOTE, content);
    }

    private void error(String content) {
        mMessager.printMessage(Diagnostic.Kind.ERROR, content);
    }
}
