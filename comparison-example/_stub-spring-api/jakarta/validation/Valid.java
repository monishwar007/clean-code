package jakarta.validation;
import java.lang.annotation.*;
@Target({ElementType.PARAMETER, ElementType.FIELD}) @Retention(RetentionPolicy.RUNTIME)
public @interface Valid {}
