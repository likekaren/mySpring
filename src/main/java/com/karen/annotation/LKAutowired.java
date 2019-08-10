package com.karen.annotation;

import java.lang.annotation.*;

/**
 * @author LIKE
 * @date 2019/8/9 8:56
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LKAutowired {
    String value() default "";
}
