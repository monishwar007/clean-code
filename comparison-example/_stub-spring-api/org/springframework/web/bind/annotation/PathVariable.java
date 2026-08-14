package org.springframework.web.bind.annotation;
import java.lang.annotation.*;
@Target(ElementType.PARAMETER) @Retention(RetentionPolicy.RUNTIME)
public @interface PathVariable { boolean required() default true; }
