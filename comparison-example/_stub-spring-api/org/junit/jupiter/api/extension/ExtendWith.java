package org.junit.jupiter.api.extension;
import java.lang.annotation.*;
@Target(ElementType.TYPE) @Retention(RetentionPolicy.RUNTIME)
public @interface ExtendWith { Class<?>[] value(); }
