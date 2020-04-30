package com.example.unittest;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author bruce jing
 * @date 2020/4/28
 * Retention 注解生命周期只停留在java源码字节码截断
 * Target 注解应用范围，应用于类
 */

@Retention(RetentionPolicy.CLASS)
@Target({ElementType.TYPE})
public @interface JavaClassTest {
    int value();
}
