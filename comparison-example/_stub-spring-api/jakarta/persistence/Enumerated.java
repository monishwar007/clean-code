package jakarta.persistence;
import java.lang.annotation.*;
@Target(ElementType.FIELD) @Retention(RetentionPolicy.RUNTIME)
public @interface Enumerated { EnumType value() default EnumType.ORDINAL; }
