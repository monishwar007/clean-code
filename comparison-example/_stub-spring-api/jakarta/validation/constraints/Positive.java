package jakarta.validation.constraints;
import java.lang.annotation.*;
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD}) @Retention(RetentionPolicy.RUNTIME)
public @interface Positive { String message() default ""; }
