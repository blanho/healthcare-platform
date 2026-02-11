package com.healthcare.audit.domain;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Audited {

    ResourceCategory category() default ResourceCategory.PATIENT;

    AuditAction action() default AuditAction.READ;

    boolean containsPhi() default true;

    String description() default "";

    boolean trackChanges() default true;

    String[] excludeFields() default {};

    boolean maskValues() default true;
}
