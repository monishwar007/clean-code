package org.springframework.beans.factory.annotation;
import java.lang.annotation.*;
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.CONSTRUCTOR}) @Retention(RetentionPolicy.RUNTIME)
public @interface Autowired { boolean required() default true; }
