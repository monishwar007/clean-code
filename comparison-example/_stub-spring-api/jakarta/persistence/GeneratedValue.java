package jakarta.persistence;
import java.lang.annotation.*;
@Target(ElementType.FIELD) @Retention(RetentionPolicy.RUNTIME)
public @interface GeneratedValue { GenerationType strategy() default GenerationType.AUTO; }
