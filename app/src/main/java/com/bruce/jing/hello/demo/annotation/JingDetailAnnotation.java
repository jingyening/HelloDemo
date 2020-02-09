package com.bruce.jing.hello.demo.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
public @interface JingDetailAnnotation {
    /**
     * 声明或者定义时，默认值不能为null
     * @return
     */
    int id() default -1;
    String description() default "";
}
