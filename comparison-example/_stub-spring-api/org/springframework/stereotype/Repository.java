package org.springframework.stereotype;
import java.lang.annotation.*;
@Target(ElementType.TYPE) @Retention(RetentionPolicy.RUNTIME)
public @interface Repository { String value() default ""; }
