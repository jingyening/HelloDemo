package com.example.unittest;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author bruce jing
 * @date 2020/4/28
 * 注解生命周期只停留在java源码截断
 */

@Retention(RetentionPolicy.CLASS)
@Target({ElementType.METHOD})
public @interface JavaMethodTest {
    int value();
}
