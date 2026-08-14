package jakarta.persistence;
import java.lang.annotation.*;
@Target(ElementType.FIELD) @Retention(RetentionPolicy.RUNTIME)
public @interface ManyToOne { FetchType fetch() default FetchType.EAGER; }
