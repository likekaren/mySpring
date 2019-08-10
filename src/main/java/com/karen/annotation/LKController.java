package com.karen.annotation;

import java.lang.annotation.*;

/**
 * @author LIKE
 * @date 2019/8/9 8:47
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LKController {
    String value() default "";
}
