package org.springframework.stereotype;
import java.lang.annotation.*;
@Target(ElementType.TYPE) @Retention(RetentionPolicy.RUNTIME)
public @interface Controller { String value() default ""; }
