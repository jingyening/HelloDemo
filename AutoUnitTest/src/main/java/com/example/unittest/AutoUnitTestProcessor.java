package com.example.unittest;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

/**
 * @author bruce jing
 * @date 2020/4/28
 */



/**
 * @AutoService(Processor.class)
 * 这个注解不要忘了，否则无法生成Java文件
 */
@AutoService(Processor.class)
public class AutoUnitTestProcessor extends AbstractProcessor {

    /**
     * 文件相关的辅助类
     */
    private Filer mFiler;
    /**
     * 元素相关的辅助类
     */
    private Elements mElementUtils;
    /**
     * 日志相关的辅助类
     */
    private Messager mMessager;


    /**
     * 每一个注解处理器类都必须有一个空的构造函数。
     * 然而，这里有一个特殊的init()方法，它会被注解处理工具调用，
     * 并输入ProcessingEnviroment参数。
     *
     * @param processingEnvironment
     */
    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        mFiler = processingEnvironment.getFiler();
        mElementUtils = processingEnvironment.getElementUtils();
        mMessager = processingEnvironment.getMessager();
    }

    /**
     * @param set 是 getSupportedAnnotationTypes返回的set
     * @param roundEnvironment roundEnvironment 运行注解所在的环境
     * @return
     */
    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        // 类名和包名
        TypeSpec finderClass = TypeSpec.classBuilder("MyGeneratedClass")
                .addModifiers(Modifier.PUBLIC)
//                .addSuperinterface(ParameterizedTypeName.get(TypeUtil.INJECTOR, TypeName.get(mClassElement.asType())))
//                .addMethod(methodBuilder.build())
                .build();

        // 创建Java文件
        JavaFile javaFile = JavaFile.builder("com.example.unittest", finderClass).build();

        try {
            javaFile.writeTo(mFiler);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //运行注解所在的环境class 此处就是testActivity
        Set<? extends Element> rootElements = roundEnvironment.getRootElements();
        for (Element rootElement : rootElements) {
            //运行注解所在的环境.java所在的包
            String packageStr = rootElement.getEnclosingElement().toString();
            String classStr = rootElement.getSimpleName().toString();
            ClassName className = ClassName.get(packageStr, classStr + "$Test");
            MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder()
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(ClassName.get(packageStr, classStr), "activity");
            boolean isBind = false;
            //运行注解所在的环境.java的成员  包括成员字段Field,方法,内部类等
            List<? extends Element> enclosedElements = rootElement.getEnclosedElements();
            for (Element enclosedElement : enclosedElements) {
                //如果是字段
                if (enclosedElement.getKind().isField()) {
                    JavaClassTest bindView = enclosedElement.getAnnotation(JavaClassTest.class);
                    //如果是被BindView注解的字段
                    if (bindView != null) {
                        isBind = true;
                        //增加构造方法的内容
                        // $N是名称替换  $L 字面量替换 也就是直接替换,具体的看Javapoet的使用方法
                        constructorBuilder.addStatement("activity.$N = activity.findViewById($L)", enclosedElement.getSimpleName(), bindView.value());
                    }
                }
            }

            TypeSpec typeSpec = TypeSpec.classBuilder(className)
                    .addModifiers(Modifier.PUBLIC)
                    .addMethod(constructorBuilder.build())
                    .build();

            if (isBind) {
                try {
                    JavaFile.builder(packageStr, typeSpec)
                            .build().writeTo(mFiler);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return true;
    }

    /**
     * 这个方法必须重写，否则无法生成Java文件
     * 这里必须指定，这个注解处理器是注册给哪个注解的。
     * 注意，它的返回值是一个字符串的集合，包含本处理器想要处理的注解类型的合法全称。
     * 换句话说，在这里定义你的注解处理器注册到哪些注解上。
     * @return
     */
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new LinkedHashSet<>();
        types.add(Override.class.getCanonicalName());
//        types.add(OnClick.class.getCanonicalName());
        return types;
    }

    /**
     * 用来指定你使用的Java版本。
     * @return
     */
    @Override
    public SourceVersion getSupportedSourceVersion() {
        return super.getSupportedSourceVersion();
    }
}
