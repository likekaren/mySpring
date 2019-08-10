package com.karen.annotation;

import java.lang.annotation.*;

/**
 * @author LIKE
 * @date 2019/8/9 8:51
 */
@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LKRequestMapping {
    String value() default "";
}
