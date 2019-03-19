package com.example.router_compiler.processor;

import com.example.router_annotation.Extra;
import com.example.router_compiler.utils.Consts;
import com.example.router_compiler.utils.LoadExtraBuilder;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.*;
import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.io.File;
import java.io.IOException;
import java.util.*;

@AutoService(Processor.class)
@SupportedOptions(Consts.ARGUMENTS_NAME)
@SupportedSourceVersion(SourceVersion.RELEASE_7)
@SupportedAnnotationTypes({Consts.ANN_TYPE_Extra})
public class ExtraProcessor extends AbstractProcessor {

    private Map<TypeElement, List<Element>> parentAndChild = new HashMap<>();

    private Elements elementsUtils;

    private Types typeUtils;

    private File filerUtils;
    private Types envTypeUtils;
    private Filer fileUtils;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        elementsUtils = processingEnv.getElementUtils();
        envTypeUtils = processingEnv.getTypeUtils();
        fileUtils = processingEnv.getFiler();

    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        if (set == null) return false;
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(Extra.class);

        category(elements);
        generateAutoWired();
        return false;
    }

    private void generateAutoWired() {
        TypeMirror typeActivity = elementsUtils.getTypeElement(Consts.ACTIVITY).asType();
        TypeElement iExtra = elementsUtils.getTypeElement(Consts.IEXTRA);

        ParameterSpec objectParamSpec = ParameterSpec.builder(TypeName.OBJECT, "target").build();
        Set<Map.Entry<TypeElement, List<Element>>> entry = parentAndChild.entrySet();

        for (Map.Entry<TypeElement, List<Element>> listEntry : entry) {
            TypeElement rawClass = listEntry.getKey();
            if (!typeUtils.isSubtype(rawClass.asType(), typeActivity)) {
                throw new RuntimeException("[Just] Support Activity Field]: " + rawClass);
            }
            LoadExtraBuilder loadExtraBuilder = new LoadExtraBuilder(objectParamSpec);
            loadExtraBuilder.setElementUtils(elementsUtils);
            loadExtraBuilder.setTypeUtils(typeUtils);
            ClassName className = ClassName.get(rawClass);
            loadExtraBuilder.injectTarget(className);
            List<Element> value = listEntry.getValue();
            for (Element element : value) {
                loadExtraBuilder.buildStatement(element);
            }
            String extraClassName = rawClass.getSimpleName() + Consts.NAME_OF_EXTRA;
            try {
                JavaFile.builder(className.packageName(),
                        TypeSpec.classBuilder(extraClassName)
                                .addSuperinterface((TypeName) null)
                                .addModifiers(Modifier.PUBLIC).addMethod(loadExtraBuilder.build())
                                .build())
                        .build()
                        .writeTo(filerUtils);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void category(Set<? extends Element> elements) {
        for (Element element : elements) {
            TypeElement typeElement = (TypeElement) element.getEnclosingElement();
            if (parentAndChild.containsKey(typeElement)) {
                parentAndChild.get(typeElement).add(element);
            } else {
                List<Element> childElement = new ArrayList<>();
                childElement.add(element);
                parentAndChild.put(typeElement, childElement);
            }
        }
    }


}
