package jakarta.persistence;
import java.lang.annotation.*;
@Target(ElementType.FIELD) @Retention(RetentionPolicy.RUNTIME)
public @interface Column { boolean nullable() default true; String name() default ""; }
