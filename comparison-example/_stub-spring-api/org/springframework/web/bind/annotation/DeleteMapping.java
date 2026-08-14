package org.springframework.web.bind.annotation;
import java.lang.annotation.*;
@Target(ElementType.METHOD) @Retention(RetentionPolicy.RUNTIME)
public @interface DeleteMapping { String value() default ""; }
