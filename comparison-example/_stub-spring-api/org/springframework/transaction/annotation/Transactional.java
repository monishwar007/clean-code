package org.springframework.transaction.annotation;
import java.lang.annotation.*;
@Target({ElementType.TYPE, ElementType.METHOD}) @Retention(RetentionPolicy.RUNTIME)
public @interface Transactional { boolean readOnly() default false; }
