package org.springframework.data.jpa.repository;
import java.lang.annotation.*;
@Target({ElementType.METHOD, ElementType.TYPE}) @Retention(RetentionPolicy.RUNTIME)
public @interface EntityGraph { String[] attributePaths() default {}; }
