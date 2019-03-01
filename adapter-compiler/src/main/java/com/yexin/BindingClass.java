package com.yexin;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.Modifier;

/**
 * author: zengven
 * date: 2017/8/31 15:54
 * desc:
 */
public class BindingClass {

    private String adapterBinderClassName;
    private String targetClassName;
    private String packageName;
    private List<Statement> statements;

    public BindingClass(String packageName, String targetClassName, String adapterBinderClassName, Statement statement) {
        this.packageName = packageName;
        this.targetClassName = targetClassName;
        this.adapterBinderClassName = adapterBinderClassName;
        statements = new ArrayList<>();
        statements.add(statement);
    }

    public String getPackageName() {
        return packageName;
    }

    public String getAdapterBinderClassName() {
        return adapterBinderClassName;
    }

    public void setAdapterBinderClassName(String adapterBinderClassName) {
        this.adapterBinderClassName = adapterBinderClassName;
    }

    public String getTargetClassName() {
        return targetClassName;
    }

    public void setTargetClassName(String targetClassName) {
        this.targetClassName = targetClassName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }


    public void addField(Statement statement) {
        statements.add(statement);
    }

    public TypeSpec createAdapterBinderClass() {
        MethodSpec.Builder bindMethodSpecBuilder = MethodSpec.methodBuilder("bind")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .returns(void.class)
                .addParameter(TypeVariableName.get("T"), "target");
        for (Statement statement : statements) {
            bindMethodSpecBuilder.addStatement("target." + statement.field + ".setAdapter(new $T())", ClassName.get(statement.packageName, statement.className));
        }
        MethodSpec bindMethodSpec = bindMethodSpecBuilder.build();

        TypeSpec typeSpec = TypeSpec.classBuilder(adapterBinderClassName)
                .addModifiers(Modifier.PUBLIC)
                .addTypeVariable(TypeVariableName.get("T", ClassName.get(packageName, targetClassName)))
                .addSuperinterface(ParameterizedTypeName.get(ClassName.get("com.yexin.core.binder.SuperAdapter", "AdapterBinder"), TypeVariableName.get("T")))
                .addMethod(bindMethodSpec)
                .build();
        return typeSpec;
    }

    public static class Statement {
        public String field;
        public String packageName;
        public String className;

        public Statement(String field, String packageName, String className) {
            this.field = field;
            this.packageName = packageName;
            this.className = className;
        }
    }
}
