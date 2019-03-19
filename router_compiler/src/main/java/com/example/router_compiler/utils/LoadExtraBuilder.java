package com.example.router_compiler.utils;

import com.example.router_annotation.Extra;
import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;

import java.lang.reflect.Type;
import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;


/**
 * @author Lance
 * @date 2018/2/26
 */

public class LoadExtraBuilder {

    private static final String INJECT_TARGET = "$T t = ($T)target";
    private MethodSpec.Builder builder;
    private Elements elementUtils;
    private Types typeUtils;

    //
    private TypeMirror parcelableType;
    private TypeMirror iServiceType;

    public LoadExtraBuilder(ParameterSpec parameterSpec) {
        builder = MethodSpec.methodBuilder(Consts
                .METHOD_LOAD_EXTRA)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(parameterSpec);
    }

    public void setElementUtils(Elements elementUtils) {
        this.elementUtils = elementUtils;
        parcelableType = elementUtils.getTypeElement(Consts
                .PARCELABLE).asType();
        iServiceType = elementUtils.getTypeElement(Consts
                .ISERVICE).asType();
    }

    public void setTypeUtils(Types typeUtils) {
        this.typeUtils = typeUtils;
    }


    public void buildStatement(Element element) {
        TypeMirror typeMirror = element.asType();
        int type = typeMirror.getKind().ordinal();
        String fieldName = element.getSimpleName().toString();
        String extraName = element.getAnnotation(Extra.class).name();
        extraName = Utils.isEmpty(extraName) ? fieldName : extraName;
        String defaultValue = "t." + fieldName;
        String statement = defaultValue + " = t.getIntent().";
        if (type == TypeKind.BOOLEAN.ordinal()) {
            statement += "getBooleanExtra($S, " + defaultValue + ")";
        } else if (type == TypeKind.BYTE.ordinal()) {
            statement += "getByteExtra($S, " + defaultValue + ")";
        } else if (type == TypeKind.SHORT.ordinal()) {
            statement += "getShortExtra($S, " + defaultValue + ")";
        } else if (type == TypeKind.INT.ordinal()) {
            statement += "getIntExtra($S, " + defaultValue + ")";
        } else if (type == TypeKind.LONG.ordinal()) {
            statement += "getLongExtra($S, " + defaultValue + ")";
        } else if (type == TypeKind.CHAR.ordinal()) {
            statement += "getCharExtra($S, " + defaultValue + ")";
        } else if (type == TypeKind.FLOAT.ordinal()) {
            statement += "getFloatExtra($S, " + defaultValue + ")";
        } else if (type == TypeKind.DOUBLE.ordinal()) {
            statement += "getDoubleExtra($S, " + defaultValue + ")";
        } else {
            if (type == TypeKind.ARRAY.ordinal()) {
                addArrayStatement(statement, fieldName, extraName, typeMirror, element);
            } else {
                addObjectStatement(statement, fieldName, extraName, typeMirror, element);
            }
            return;
        }
        builder.addStatement(statement, extraName);
    }

    /**
     * 添加对象 String/List/Parcelable
     *
     * @param statement
     * @param extraName
     * @param typeMirror
     * @param element
     */
    private void addObjectStatement(String statement, String fieldName, String extraName,
                                    TypeMirror typeMirror,
                                    Element element) {
        if (typeUtils.isSubtype(typeMirror, parcelableType)) {
            statement += "getParcelableExtra($S)";
        } else if (typeMirror.toString().equals(Consts.STRING)) {
            statement += "getStringExtra($S)";
        } else if (typeUtils.isSubtype(typeMirror, iServiceType)) {
//            TestService testService = (TestService) DNRouter.getInstance().build("/main/service1")
//                    .navigation();
//            testService.test();
            statement = "t." + fieldName + " = ($T) $T.getInstance().build($S).navigation()";
            builder.addStatement(statement, TypeName.get(element.asType()), Consts.ROUTER,
                    extraName);
            return;
        } else {
            TypeName typeName = ClassName.get(typeMirror);
            if (typeName instanceof ParameterizedTypeName) {
                ClassName rawType = ((ParameterizedTypeName) typeName).rawType;
                List<TypeName> typeArguments = ((ParameterizedTypeName) typeName)
                        .typeArguments;
                if (!rawType.toString().equals(Consts.ARRAYLIST) && !rawType.toString()
                        .equals(Consts.LIST)) {
                    throw new RuntimeException("Not Support Inject Type:" + typeMirror + " " +
                            element);
                }
                if (typeArguments.isEmpty() || typeArguments.size() != 1) {
                    throw new RuntimeException("List Must Specify Generic Type:" + typeArguments);
                }
                TypeName typeArgumentName = typeArguments.get(0);
                TypeElement typeElement = elementUtils.getTypeElement(typeArgumentName
                        .toString());
                if (typeUtils.isSubtype(typeElement.asType(), parcelableType)) {
                    statement += "getParcelableArrayListExtra($S)";
                } else if (typeElement.asType().toString().equals(Consts.STRING)) {
                    statement += "getStringArrayListExtra($S)";
                } else if (typeElement.asType().toString().equals(Consts.INTEGER)) {
                    statement += "getIntegerArrayListExtra($S)";
                } else {
                    throw new RuntimeException("Not Support Generic Type : " + typeMirror + " " +
                            element);
                }
            } else {
                throw new RuntimeException("Not Support Extra Type : " + typeMirror + " " +
                        element);
            }
        }
        builder.addStatement(statement, extraName);
    }

    /**
     * 添加数组
     *
     * @param statement
     * @param fieldName
     * @param typeMirror
     * @param element
     */
    private void addArrayStatement(String statement, String fieldName, String extraName, TypeMirror
            typeMirror, Element element) {
        //数组
        switch (typeMirror.toString()) {
            case Consts.BOOLEANARRAY:
                statement += "getBooleanArrayExtra($S)";
                break;
            case Consts.INTARRAY:
                statement += "getIntArrayExtra($S)";
                break;
            case Consts.SHORTARRAY:
                statement += "getShortArrayExtra($S)";
                break;
            case Consts.FLOATARRAY:
                statement += "getFloatArrayExtra($S)";
                break;
            case Consts.DOUBLEARRAY:
                statement += "getDoubleArrayExtra($S)";
                break;
            case Consts.BYTEARRAY:
                statement += "getByteArrayExtra($S)";
                break;
            case Consts.CHARARRAY:
                statement += "getCharArrayExtra($S)";
                break;
            case Consts.LONGARRAY:
                statement += "getLongArrayExtra($S)";
                break;
            case Consts.STRINGARRAY:
                statement += "getStringArrayExtra($S)";
                break;
            default:
                String defaultValue = "t." + fieldName;
                ArrayTypeName arrayTypeName = (ArrayTypeName) ClassName.get(typeMirror);
                TypeElement typeElement = elementUtils.getTypeElement(arrayTypeName
                        .componentType.toString());
                if (!typeUtils.isSubtype(typeElement.asType(), parcelableType)) {
                    throw new RuntimeException("Not Support Extra Type:" + typeMirror + " " +
                            element);
                }
                statement = "$T[] " + fieldName + " = t.getIntent()" +
                        ".getParcelableArrayExtra" +
                        "($S)";
                builder.addStatement(statement, parcelableType, extraName);
                builder.beginControlFlow("if( null != $L)", fieldName);
                statement = defaultValue + " = new $T[" + fieldName + ".length]";
                builder.addStatement(statement, arrayTypeName.componentType)
                        .beginControlFlow("for (int i = 0; i < " + fieldName + "" +
                                ".length; " +
                                "i++)")
                        .addStatement(defaultValue + "[i] = ($T)" + fieldName + "[i]",
                                arrayTypeName.componentType)
                        .endControlFlow();
                builder.endControlFlow();
                return;
        }
        builder.addStatement(statement, extraName);
    }

    /**
     * 加入 $T t = ($T)target
     *
     * @param className
     */
    public void injectTarget(ClassName className) {
        builder.addStatement(INJECT_TARGET, className, className);

    }

    public MethodSpec build() {
        return builder.build();
    }
}
