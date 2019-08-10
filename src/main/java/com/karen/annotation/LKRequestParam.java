package com.karen.annotation;

import java.lang.annotation.*;

/**
 * @author LIKE
 * @date 2019/8/9 8:57
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LKRequestParam {
    String value() default "";
}
