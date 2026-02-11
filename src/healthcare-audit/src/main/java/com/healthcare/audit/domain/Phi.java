package com.healthcare.audit.domain;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Phi {

    boolean masked() default true;

    PhiCategory category() default PhiCategory.GENERAL;

    String maskPattern() default "***MASKED***";
}
