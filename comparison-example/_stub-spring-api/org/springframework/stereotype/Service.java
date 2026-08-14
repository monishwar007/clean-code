package org.springframework.stereotype;
import java.lang.annotation.*;
@Target(ElementType.TYPE) @Retention(RetentionPolicy.RUNTIME)
public @interface Service { String value() default ""; }
